package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickPriority;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;

public class ProtoTickList implements TickList {
   protected final Predicate ignore;
   private final ChunkPos chunkPos;
   private final ShortList[] toBeTicked;

   public ProtoTickList(Predicate predicate, ChunkPos chunkPos) {
      this(predicate, chunkPos, new ListTag());
   }

   public ProtoTickList(Predicate ignore, ChunkPos chunkPos, ListTag listTag) {
      this.toBeTicked = new ShortList[16];
      this.ignore = ignore;
      this.chunkPos = chunkPos;

      for(int var4 = 0; var4 < listTag.size(); ++var4) {
         ListTag var5 = listTag.getList(var4);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            ChunkAccess.getOrCreateOffsetList(this.toBeTicked, var4).add(var5.getShort(var6));
         }
      }

   }

   public ListTag save() {
      return ChunkSerializer.packOffsets(this.toBeTicked);
   }

   public void copyOut(TickList tickList, Function function) {
      for(int var3 = 0; var3 < this.toBeTicked.length; ++var3) {
         if(this.toBeTicked[var3] != null) {
            ShortListIterator var4 = this.toBeTicked[var3].iterator();

            while(var4.hasNext()) {
               Short var5 = (Short)var4.next();
               BlockPos var6 = ProtoChunk.unpackOffsetCoordinates(var5.shortValue(), var3, this.chunkPos);
               tickList.scheduleTick(var6, function.apply(var6), 0);
            }

            this.toBeTicked[var3].clear();
         }
      }

   }

   public boolean hasScheduledTick(BlockPos blockPos, Object object) {
      return false;
   }

   public void scheduleTick(BlockPos blockPos, Object object, int var3, TickPriority tickPriority) {
      ChunkAccess.getOrCreateOffsetList(this.toBeTicked, blockPos.getY() >> 4).add(ProtoChunk.packOffsetCoordinates(blockPos));
   }

   public boolean willTickThisTick(BlockPos blockPos, Object object) {
      return false;
   }

   public void addAll(Stream stream) {
      stream.forEach((tickNextTickData) -> {
         this.scheduleTick(tickNextTickData.pos, tickNextTickData.getType(), 0, tickNextTickData.priority);
      });
   }
}
