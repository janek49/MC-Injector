package net.minecraft.world.phys.shapes;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.world.phys.shapes.CubePointRange;
import net.minecraft.world.phys.shapes.IndexMerger;
import net.minecraft.world.phys.shapes.Shapes;

public final class DiscreteCubeMerger implements IndexMerger {
   private final CubePointRange result;
   private final int firstSize;
   private final int secondSize;
   private final int gcd;

   DiscreteCubeMerger(int firstSize, int secondSize) {
      this.result = new CubePointRange((int)Shapes.lcm(firstSize, secondSize));
      this.firstSize = firstSize;
      this.secondSize = secondSize;
      this.gcd = IntMath.gcd(firstSize, secondSize);
   }

   public boolean forMergedIndexes(IndexMerger.IndexConsumer indexMerger$IndexConsumer) {
      int var2 = this.firstSize / this.gcd;
      int var3 = this.secondSize / this.gcd;

      for(int var4 = 0; var4 <= this.result.size(); ++var4) {
         if(!indexMerger$IndexConsumer.merge(var4 / var3, var4 / var2, var4)) {
            return false;
         }
      }

      return true;
   }

   public DoubleList getList() {
      return this.result;
   }
}
