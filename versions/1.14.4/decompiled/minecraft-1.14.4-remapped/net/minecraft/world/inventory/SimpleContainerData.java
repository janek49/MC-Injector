package net.minecraft.world.inventory;

import net.minecraft.world.inventory.ContainerData;

public class SimpleContainerData implements ContainerData {
   private final int[] ints;

   public SimpleContainerData(int ints) {
      this.ints = new int[ints];
   }

   public int get(int i) {
      return this.ints[i];
   }

   public void set(int var1, int var2) {
      this.ints[var1] = var2;
   }

   public int getCount() {
      return this.ints.length;
   }
}
