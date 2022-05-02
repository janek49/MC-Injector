package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.world.phys.shapes.IndexMerger;

public class NonOverlappingMerger extends AbstractDoubleList implements IndexMerger {
   private final DoubleList lower;
   private final DoubleList upper;
   private final boolean swap;

   public NonOverlappingMerger(DoubleList lower, DoubleList upper, boolean swap) {
      this.lower = lower;
      this.upper = upper;
      this.swap = swap;
   }

   public int size() {
      return this.lower.size() + this.upper.size();
   }

   public boolean forMergedIndexes(IndexMerger.IndexConsumer indexMerger$IndexConsumer) {
      return this.swap?this.forNonSwappedIndexes((var1, var2, var3) -> {
         return indexMerger$IndexConsumer.merge(var2, var1, var3);
      }):this.forNonSwappedIndexes(indexMerger$IndexConsumer);
   }

   private boolean forNonSwappedIndexes(IndexMerger.IndexConsumer indexMerger$IndexConsumer) {
      int var2 = this.lower.size() - 1;

      for(int var3 = 0; var3 < var2; ++var3) {
         if(!indexMerger$IndexConsumer.merge(var3, -1, var3)) {
            return false;
         }
      }

      if(!indexMerger$IndexConsumer.merge(var2, -1, var2)) {
         return false;
      } else {
         for(int var3 = 0; var3 < this.upper.size(); ++var3) {
            if(!indexMerger$IndexConsumer.merge(var2, var3, var2 + 1 + var3)) {
               return false;
            }
         }

         return true;
      }
   }

   public double getDouble(int i) {
      return i < this.lower.size()?this.lower.getDouble(i):this.upper.getDouble(i - this.lower.size());
   }

   public DoubleList getList() {
      return this;
   }
}
