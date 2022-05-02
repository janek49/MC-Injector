package net.minecraft.world.item;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface DyeableLeatherItem {
   default boolean hasCustomColor(ItemStack itemStack) {
      CompoundTag var2 = itemStack.getTagElement("display");
      return var2 != null && var2.contains("color", 99);
   }

   default int getColor(ItemStack itemStack) {
      CompoundTag var2 = itemStack.getTagElement("display");
      return var2 != null && var2.contains("color", 99)?var2.getInt("color"):10511680;
   }

   default void clearColor(ItemStack itemStack) {
      CompoundTag var2 = itemStack.getTagElement("display");
      if(var2 != null && var2.contains("color")) {
         var2.remove("color");
      }

   }

   default void setColor(ItemStack itemStack, int var2) {
      itemStack.getOrCreateTagElement("display").putInt("color", var2);
   }

   static default ItemStack dyeArmor(ItemStack var0, List list) {
      ItemStack var2 = ItemStack.EMPTY;
      int[] vars3 = new int[3];
      int var4 = 0;
      int var5 = 0;
      DyeableLeatherItem var6 = null;
      Item var7 = var0.getItem();
      if(var7 instanceof DyeableLeatherItem) {
         var6 = (DyeableLeatherItem)var7;
         var2 = var0.copy();
         var2.setCount(1);
         if(var6.hasCustomColor(var0)) {
            int var8 = var6.getColor(var2);
            float var9 = (float)(var8 >> 16 & 255) / 255.0F;
            float var10 = (float)(var8 >> 8 & 255) / 255.0F;
            float var11 = (float)(var8 & 255) / 255.0F;
            var4 = (int)((float)var4 + Math.max(var9, Math.max(var10, var11)) * 255.0F);
            vars3[0] = (int)((float)vars3[0] + var9 * 255.0F);
            vars3[1] = (int)((float)vars3[1] + var10 * 255.0F);
            vars3[2] = (int)((float)vars3[2] + var11 * 255.0F);
            ++var5;
         }

         for(DyeItem var9 : list) {
            float[] vars10 = var9.getDyeColor().getTextureDiffuseColors();
            int var11 = (int)(vars10[0] * 255.0F);
            int var12 = (int)(vars10[1] * 255.0F);
            int var13 = (int)(vars10[2] * 255.0F);
            var4 += Math.max(var11, Math.max(var12, var13));
            vars3[0] += var11;
            vars3[1] += var12;
            vars3[2] += var13;
            ++var5;
         }
      }

      if(var6 == null) {
         return ItemStack.EMPTY;
      } else {
         int var8 = vars3[0] / var5;
         int var9 = vars3[1] / var5;
         int var10 = vars3[2] / var5;
         float var11 = (float)var4 / (float)var5;
         float var12 = (float)Math.max(var8, Math.max(var9, var10));
         var8 = (int)((float)var8 * var11 / var12);
         var9 = (int)((float)var9 * var11 / var12);
         var10 = (int)((float)var10 * var11 / var12);
         int var13 = (var8 << 8) + var9;
         var13 = (var13 << 8) + var10;
         var6.setColor(var2, var13);
         return var2;
      }
   }
}
