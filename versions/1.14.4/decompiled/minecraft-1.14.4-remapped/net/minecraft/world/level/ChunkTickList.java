package net.minecraft.world.level;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ServerTickList;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickNextTickData;
import net.minecraft.world.level.TickPriority;

public class ChunkTickList implements TickList {
   private final Set ticks;
   private final Function toId;

   public ChunkTickList(Function function, List list) {
      this(function, (Set)Sets.newHashSet(list));
   }

   private ChunkTickList(Function toId, Set ticks) {
      this.ticks = ticks;
      this.toId = toId;
   }

   public boolean hasScheduledTick(BlockPos blockPos, Object object) {
      return false;
   }

   public void scheduleTick(BlockPos blockPos, Object object, int var3, TickPriority tickPriority) {
      this.ticks.add(new TickNextTickData(blockPos, object, (long)var3, tickPriority));
   }

   public boolean willTickThisTick(BlockPos blockPos, Object object) {
      return false;
   }

   public void addAll(Stream stream) {
      Set var10001 = this.ticks;
      this.ticks.getClass();
      stream.forEach(var10001::add);
   }

   public Stream ticks() {
      return this.ticks.stream();
   }

   public ListTag save(long l) {
      return ServerTickList.saveTickList(this.toId, this.ticks, l);
   }

   public static ChunkTickList create(ListTag listTag, Function var1, Function var2) {
      Set<TickNextTickData<T>> var3 = Sets.newHashSet();

      for(int var4 = 0; var4 < listTag.size(); ++var4) {
         CompoundTag var5 = listTag.getCompound(var4);
         T var6 = var2.apply(new ResourceLocation(var5.getString("i")));
         if(var6 != null) {
            var3.add(new TickNextTickData(new BlockPos(var5.getInt("x"), var5.getInt("y"), var5.getInt("z")), var6, (long)var5.getInt("t"), TickPriority.byValue(var5.getInt("p"))));
         }
      }

      return new ChunkTickList(var1, var3);
   }
}
