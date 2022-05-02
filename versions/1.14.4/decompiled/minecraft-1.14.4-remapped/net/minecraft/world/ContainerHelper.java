package net.minecraft.world;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

public class ContainerHelper {
   public static ItemStack removeItem(List list, int var1, int var2) {
      return var1 >= 0 && var1 < list.size() && !((ItemStack)list.get(var1)).isEmpty() && var2 > 0?((ItemStack)list.get(var1)).split(var2):ItemStack.EMPTY;
   }

   public static ItemStack takeItem(List list, int var1) {
      return var1 >= 0 && var1 < list.size()?(ItemStack)list.set(var1, ItemStack.EMPTY):ItemStack.EMPTY;
   }

   public static CompoundTag saveAllItems(CompoundTag var0, NonNullList nonNullList) {
      return saveAllItems(var0, nonNullList, true);
   }

   public static CompoundTag saveAllItems(CompoundTag var0, NonNullList nonNullList, boolean var2) {
      ListTag var3 = new ListTag();

      for(int var4 = 0; var4 < nonNullList.size(); ++var4) {
         ItemStack var5 = (ItemStack)nonNullList.get(var4);
         if(!var5.isEmpty()) {
            CompoundTag var6 = new CompoundTag();
            var6.putByte("Slot", (byte)var4);
            var5.save(var6);
            var3.add(var6);
         }
      }

      if(!var3.isEmpty() || var2) {
         var0.put("Items", var3);
      }

      return var0;
   }

   public static void loadAllItems(CompoundTag compoundTag, NonNullList nonNullList) {
      ListTag var2 = compoundTag.getList("Items", 10);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         CompoundTag var4 = var2.getCompound(var3);
         int var5 = var4.getByte("Slot") & 255;
         if(var5 >= 0 && var5 < nonNullList.size()) {
            nonNullList.set(var5, ItemStack.of(var4));
         }
      }

   }
}
