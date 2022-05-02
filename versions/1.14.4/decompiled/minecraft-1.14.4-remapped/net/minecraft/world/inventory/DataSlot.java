package net.minecraft.world.inventory;

import net.minecraft.world.inventory.ContainerData;

public abstract class DataSlot {
   private int prevValue;

   public static DataSlot forContainer(final ContainerData containerData, final int var1) {
      return new DataSlot() {
         public int get() {
            return containerData.get(var1);
         }

         public void set(int i) {
            containerData.set(var1, i);
         }
      };
   }

   public static DataSlot shared(final int[] ints, final int var1) {
      return new DataSlot() {
         public int get() {
            return ints[var1];
         }

         public void set(int i) {
            ints[var1] = i;
         }
      };
   }

   public static DataSlot standalone() {
      return new DataSlot() {
         private int value;

         public int get() {
            return this.value;
         }

         public void set(int value) {
            this.value = value;
         }
      };
   }

   public abstract int get();

   public abstract void set(int var1);

   public boolean checkAndClearUpdateFlag() {
      int var1 = this.get();
      boolean var2 = var1 != this.prevValue;
      this.prevValue = var1;
      return var2;
   }
}
