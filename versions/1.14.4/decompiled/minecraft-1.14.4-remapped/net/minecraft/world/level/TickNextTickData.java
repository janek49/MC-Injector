package net.minecraft.world.level;

import java.util.Comparator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.TickPriority;

public class TickNextTickData {
   private static long counter;
   private final Object type;
   public final BlockPos pos;
   public final long delay;
   public final TickPriority priority;
   private final long c;

   public TickNextTickData(BlockPos blockPos, Object object) {
      this(blockPos, object, 0L, TickPriority.NORMAL);
   }

   public TickNextTickData(BlockPos blockPos, Object type, long delay, TickPriority priority) {
      this.c = (long)(counter++);
      this.pos = blockPos.immutable();
      this.type = type;
      this.delay = delay;
      this.priority = priority;
   }

   public boolean equals(Object object) {
      if(!(object instanceof TickNextTickData)) {
         return false;
      } else {
         TickNextTickData<?> var2 = (TickNextTickData)object;
         return this.pos.equals(var2.pos) && this.type == var2.type;
      }
   }

   public int hashCode() {
      return this.pos.hashCode();
   }

   public static Comparator createTimeComparator() {
      return (var0, var1) -> {
         int var2 = Long.compare(var0.delay, var1.delay);
         if(var2 != 0) {
            return var2;
         } else {
            var2 = var0.priority.compareTo(var1.priority);
            return var2 != 0?var2:Long.compare(var0.c, var1.c);
         }
      };
   }

   public String toString() {
      return this.type + ": " + this.pos + ", " + this.delay + ", " + this.priority + ", " + this.c;
   }

   public Object getType() {
      return this.type;
   }
}
