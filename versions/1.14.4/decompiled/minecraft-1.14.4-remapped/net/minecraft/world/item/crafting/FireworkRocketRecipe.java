package net.minecraft.world.item.crafting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class FireworkRocketRecipe extends CustomRecipe {
   private static final Ingredient PAPER_INGREDIENT = Ingredient.of(new ItemLike[]{Items.PAPER});
   private static final Ingredient GUNPOWDER_INGREDIENT = Ingredient.of(new ItemLike[]{Items.GUNPOWDER});
   private static final Ingredient STAR_INGREDIENT = Ingredient.of(new ItemLike[]{Items.FIREWORK_STAR});

   public FireworkRocketRecipe(ResourceLocation resourceLocation) {
      super(resourceLocation);
   }

   public boolean matches(CraftingContainer craftingContainer, Level level) {
      boolean var3 = false;
      int var4 = 0;

      for(int var5 = 0; var5 < craftingContainer.getContainerSize(); ++var5) {
         ItemStack var6 = craftingContainer.getItem(var5);
         if(!var6.isEmpty()) {
            if(PAPER_INGREDIENT.test(var6)) {
               if(var3) {
                  return false;
               }

               var3 = true;
            } else if(GUNPOWDER_INGREDIENT.test(var6)) {
               ++var4;
               if(var4 > 3) {
                  return false;
               }
            } else if(!STAR_INGREDIENT.test(var6)) {
               return false;
            }
         }
      }

      return var3 && var4 >= 1;
   }

   public ItemStack assemble(CraftingContainer craftingContainer) {
      ItemStack itemStack = new ItemStack(Items.FIREWORK_ROCKET, 3);
      CompoundTag var3 = itemStack.getOrCreateTagElement("Fireworks");
      ListTag var4 = new ListTag();
      int var5 = 0;

      for(int var6 = 0; var6 < craftingContainer.getContainerSize(); ++var6) {
         ItemStack var7 = craftingContainer.getItem(var6);
         if(!var7.isEmpty()) {
            if(GUNPOWDER_INGREDIENT.test(var7)) {
               ++var5;
            } else if(STAR_INGREDIENT.test(var7)) {
               CompoundTag var8 = var7.getTagElement("Explosion");
               if(var8 != null) {
                  var4.add(var8);
               }
            }
         }
      }

      var3.putByte("Flight", (byte)var5);
      if(!var4.isEmpty()) {
         var3.put("Explosions", var4);
      }

      return itemStack;
   }

   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 * var2 >= 2;
   }

   public ItemStack getResultItem() {
      return new ItemStack(Items.FIREWORK_ROCKET);
   }

   public RecipeSerializer getSerializer() {
      return RecipeSerializer.FIREWORK_ROCKET;
   }
}
