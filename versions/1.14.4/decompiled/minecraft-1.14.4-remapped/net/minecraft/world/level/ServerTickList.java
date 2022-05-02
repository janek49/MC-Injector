package net.minecraft.world.level;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickNextTickData;
import net.minecraft.world.level.TickPriority;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class ServerTickList implements TickList {
   protected final Predicate ignore;
   private final Function toId;
   private final Function fromId;
   private final Set tickNextTickSet = Sets.newHashSet();
   private final TreeSet tickNextTickList = Sets.newTreeSet(TickNextTickData.createTimeComparator());
   private final ServerLevel level;
   private final Queue currentlyTicking = Queues.newArrayDeque();
   private final List alreadyTicked = Lists.newArrayList();
   private final Consumer ticker;

   public ServerTickList(ServerLevel level, Predicate ignore, Function toId, Function fromId, Consumer ticker) {
      this.ignore = ignore;
      this.toId = toId;
      this.fromId = fromId;
      this.level = level;
      this.ticker = ticker;
   }

   public void tick() {
      int var1 = this.tickNextTickList.size();
      if(var1 != this.tickNextTickSet.size()) {
         throw new IllegalStateException("TickNextTick list out of synch");
      } else {
         if(var1 > 65536) {
            var1 = 65536;
         }

         ServerChunkCache var2 = this.level.getChunkSource();
         Iterator<TickNextTickData<T>> var3 = this.tickNextTickList.iterator();
         this.level.getProfiler().push("cleaning");

         while(var1 > 0 && var3.hasNext()) {
            TickNextTickData<T> var4 = (TickNextTickData)var3.next();
            if(var4.delay > this.level.getGameTime()) {
               break;
            }

            if(var2.isTickingChunk(var4.pos)) {
               var3.remove();
               this.tickNextTickSet.remove(var4);
               this.currentlyTicking.add(var4);
               --var1;
            }
         }

         this.level.getProfiler().popPush("ticking");

         TickNextTickData<T> var4;
         while((var4 = (TickNextTickData)this.currentlyTicking.poll()) != null) {
            if(var2.isTickingChunk(var4.pos)) {
               try {
                  this.alreadyTicked.add(var4);
                  this.ticker.accept(var4);
               } catch (Throwable var8) {
                  CrashReport var6 = CrashReport.forThrowable(var8, "Exception while ticking");
                  CrashReportCategory var7 = var6.addCategory("Block being ticked");
                  CrashReportCategory.populateBlockDetails(var7, var4.pos, (BlockState)null);
                  throw new ReportedException(var6);
               }
            } else {
               this.scheduleTick(var4.pos, var4.getType(), 0);
            }
         }

         this.level.getProfiler().pop();
         this.alreadyTicked.clear();
         this.currentlyTicking.clear();
      }
   }

   public boolean willTickThisTick(BlockPos blockPos, Object object) {
      return this.currentlyTicking.contains(new TickNextTickData(blockPos, object));
   }

   public void addAll(Stream stream) {
      stream.forEach(this::addTickData);
   }

   public List fetchTicksInChunk(ChunkPos chunkPos, boolean var2, boolean var3) {
      int var4 = (chunkPos.x << 4) - 2;
      int var5 = var4 + 16 + 2;
      int var6 = (chunkPos.z << 4) - 2;
      int var7 = var6 + 16 + 2;
      return this.fetchTicksInArea(new BoundingBox(var4, 0, var6, var5, 256, var7), var2, var3);
   }

   public List fetchTicksInArea(BoundingBox boundingBox, boolean var2, boolean var3) {
      List<TickNextTickData<T>> list = this.fetchTicksInArea((List)null, this.tickNextTickList, boundingBox, var2);
      if(var2 && list != null) {
         this.tickNextTickSet.removeAll(list);
      }

      list = this.fetchTicksInArea(list, this.currentlyTicking, boundingBox, var2);
      if(!var3) {
         list = this.fetchTicksInArea(list, this.alreadyTicked, boundingBox, var2);
      }

      return list == null?Collections.emptyList():list;
   }

   @Nullable
   private List fetchTicksInArea(@Nullable List var1, Collection collection, BoundingBox boundingBox, boolean var4) {
      Iterator<TickNextTickData<T>> var5 = collection.iterator();

      while(var5.hasNext()) {
         TickNextTickData<T> var6 = (TickNextTickData)var5.next();
         BlockPos var7 = var6.pos;
         if(var7.getX() >= boundingBox.x0 && var7.getX() < boundingBox.x1 && var7.getZ() >= boundingBox.z0 && var7.getZ() < boundingBox.z1) {
            if(var4) {
               var5.remove();
            }

            if(var1 == null) {
               var1 = Lists.newArrayList();
            }

            ((List)var1).add(var6);
         }
      }

      return (List)var1;
   }

   public void copy(BoundingBox boundingBox, BlockPos blockPos) {
      for(TickNextTickData<T> var5 : this.fetchTicksInArea(boundingBox, false, false)) {
         if(boundingBox.isInside(var5.pos)) {
            BlockPos var6 = var5.pos.offset(blockPos);
            T var7 = var5.getType();
            this.addTickData(new TickNextTickData(var6, var7, var5.delay, var5.priority));
         }
      }

   }

   public ListTag save(ChunkPos chunkPos) {
      List<TickNextTickData<T>> var2 = this.fetchTicksInChunk(chunkPos, false, true);
      return saveTickList(this.toId, var2, this.level.getGameTime());
   }

   public static ListTag saveTickList(Function function, Iterable iterable, long var2) {
      ListTag listTag = new ListTag();

      for(TickNextTickData<T> var6 : iterable) {
         CompoundTag var7 = new CompoundTag();
         var7.putString("i", ((ResourceLocation)function.apply(var6.getType())).toString());
         var7.putInt("x", var6.pos.getX());
         var7.putInt("y", var6.pos.getY());
         var7.putInt("z", var6.pos.getZ());
         var7.putInt("t", (int)(var6.delay - var2));
         var7.putInt("p", var6.priority.getValue());
         listTag.add(var7);
      }

      return listTag;
   }

   public boolean hasScheduledTick(BlockPos blockPos, Object object) {
      return this.tickNextTickSet.contains(new TickNextTickData(blockPos, object));
   }

   public void scheduleTick(BlockPos blockPos, Object object, int var3, TickPriority tickPriority) {
      if(!this.ignore.test(object)) {
         this.addTickData(new TickNextTickData(blockPos, object, (long)var3 + this.level.getGameTime(), tickPriority));
      }

   }

   private void addTickData(TickNextTickData tickNextTickData) {
      if(!this.tickNextTickSet.contains(tickNextTickData)) {
         this.tickNextTickSet.add(tickNextTickData);
         this.tickNextTickList.add(tickNextTickData);
      }

   }

   public int size() {
      return this.tickNextTickSet.size();
   }
}
