package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class BookCloningRecipe extends CustomRecipe {
   public BookCloningRecipe(ResourceLocation resourceLocation) {
      super(resourceLocation);
   }

   public boolean matches(CraftingContainer craftingContainer, Level level) {
      int var3 = 0;
      ItemStack var4 = ItemStack.EMPTY;

      for(int var5 = 0; var5 < craftingContainer.getContainerSize(); ++var5) {
         ItemStack var6 = craftingContainer.getItem(var5);
         if(!var6.isEmpty()) {
            if(var6.getItem() == Items.WRITTEN_BOOK) {
               if(!var4.isEmpty()) {
                  return false;
               }

               var4 = var6;
            } else {
               if(var6.getItem() != Items.WRITABLE_BOOK) {
                  return false;
               }

               ++var3;
            }
         }
      }

      return !var4.isEmpty() && var4.hasTag() && var3 > 0;
   }

   public ItemStack assemble(CraftingContainer craftingContainer) {
      int var2 = 0;
      ItemStack var3 = ItemStack.EMPTY;

      for(int var4 = 0; var4 < craftingContainer.getContainerSize(); ++var4) {
         ItemStack var5 = craftingContainer.getItem(var4);
         if(!var5.isEmpty()) {
            if(var5.getItem() == Items.WRITTEN_BOOK) {
               if(!var3.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               var3 = var5;
            } else {
               if(var5.getItem() != Items.WRITABLE_BOOK) {
                  return ItemStack.EMPTY;
               }

               ++var2;
            }
         }
      }

      if(!var3.isEmpty() && var3.hasTag() && var2 >= 1 && WrittenBookItem.getGeneration(var3) < 2) {
         ItemStack var4 = new ItemStack(Items.WRITTEN_BOOK, var2);
         CompoundTag var5 = var3.getTag().copy();
         var5.putInt("generation", WrittenBookItem.getGeneration(var3) + 1);
         var4.setTag(var5);
         return var4;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public NonNullList getRemainingItems(CraftingContainer craftingContainer) {
      NonNullList<ItemStack> nonNullList = NonNullList.withSize(craftingContainer.getContainerSize(), ItemStack.EMPTY);

      for(int var3 = 0; var3 < nonNullList.size(); ++var3) {
         ItemStack var4 = craftingContainer.getItem(var3);
         if(var4.getItem().hasCraftingRemainingItem()) {
            nonNullList.set(var3, new ItemStack(var4.getItem().getCraftingRemainingItem()));
         } else if(var4.getItem() instanceof WrittenBookItem) {
            ItemStack var5 = var4.copy();
            var5.setCount(1);
            nonNullList.set(var3, var5);
            break;
         }
      }

      return nonNullList;
   }

   public RecipeSerializer getSerializer() {
      return RecipeSerializer.BOOK_CLONING;
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 >= 3 && var2 >= 3;
   }
}
