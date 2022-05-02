package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;

public class SuspiciousStewRecipe extends CustomRecipe {
   public SuspiciousStewRecipe(ResourceLocation resourceLocation) {
      super(resourceLocation);
   }

   public boolean matches(CraftingContainer craftingContainer, Level level) {
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;

      for(int var7 = 0; var7 < craftingContainer.getContainerSize(); ++var7) {
         ItemStack var8 = craftingContainer.getItem(var7);
         if(!var8.isEmpty()) {
            if(var8.getItem() == Blocks.BROWN_MUSHROOM.asItem() && !var5) {
               var5 = true;
            } else if(var8.getItem() == Blocks.RED_MUSHROOM.asItem() && !var4) {
               var4 = true;
            } else if(var8.getItem().is(ItemTags.SMALL_FLOWERS) && !var3) {
               var3 = true;
            } else {
               if(var8.getItem() != Items.BOWL || var6) {
                  return false;
               }

               var6 = true;
            }
         }
      }

      return var3 && var5 && var4 && var6;
   }

   public ItemStack assemble(CraftingContainer craftingContainer) {
      ItemStack itemStack = ItemStack.EMPTY;

      for(int var3 = 0; var3 < craftingContainer.getContainerSize(); ++var3) {
         ItemStack var4 = craftingContainer.getItem(var3);
         if(!var4.isEmpty() && var4.getItem().is(ItemTags.SMALL_FLOWERS)) {
            itemStack = var4;
            break;
         }
      }

      ItemStack var3 = new ItemStack(Items.SUSPICIOUS_STEW, 1);
      if(itemStack.getItem() instanceof BlockItem && ((BlockItem)itemStack.getItem()).getBlock() instanceof FlowerBlock) {
         FlowerBlock var4 = (FlowerBlock)((BlockItem)itemStack.getItem()).getBlock();
         MobEffect var5 = var4.getSuspiciousStewEffect();
         SuspiciousStewItem.saveMobEffect(var3, var5, var4.getEffectDuration());
      }

      return var3;
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 >= 2 && var2 >= 2;
   }

   public RecipeSerializer getSerializer() {
      return RecipeSerializer.SUSPICIOUS_STEW;
   }
}
