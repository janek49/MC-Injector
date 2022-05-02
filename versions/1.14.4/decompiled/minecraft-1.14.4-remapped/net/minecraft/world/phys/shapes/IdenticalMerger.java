package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.world.phys.shapes.IndexMerger;

public class IdenticalMerger implements IndexMerger {
   private final DoubleList coords;

   public IdenticalMerger(DoubleList coords) {
      this.coords = coords;
   }

   public boolean forMergedIndexes(IndexMerger.IndexConsumer indexMerger$IndexConsumer) {
      for(int var2 = 0; var2 <= this.coords.size(); ++var2) {
         if(!indexMerger$IndexConsumer.merge(var2, var2, var2)) {
            return false;
         }
      }

      return true;
   }

   public DoubleList getList() {
      return this.coords;
   }
}
