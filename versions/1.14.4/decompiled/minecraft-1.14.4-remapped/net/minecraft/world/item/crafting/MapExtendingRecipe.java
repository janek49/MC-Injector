package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapExtendingRecipe extends ShapedRecipe {
   public MapExtendingRecipe(ResourceLocation resourceLocation) {
      super(resourceLocation, "", 3, 3, NonNullList.of(Ingredient.EMPTY, new Ingredient[]{Ingredient.of(new ItemLike[]{Items.PAPER}), Ingredient.of(new ItemLike[]{Items.PAPER}), Ingredient.of(new ItemLike[]{Items.PAPER}), Ingredient.of(new ItemLike[]{Items.PAPER}), Ingredient.of(new ItemLike[]{Items.FILLED_MAP}), Ingredient.of(new ItemLike[]{Items.PAPER}), Ingredient.of(new ItemLike[]{Items.PAPER}), Ingredient.of(new ItemLike[]{Items.PAPER}), Ingredient.of(new ItemLike[]{Items.PAPER})}), new ItemStack(Items.MAP));
   }

   public boolean matches(CraftingContainer craftingContainer, Level level) {
      if(!super.matches(craftingContainer, level)) {
         return false;
      } else {
         ItemStack var3 = ItemStack.EMPTY;

         for(int var4 = 0; var4 < craftingContainer.getContainerSize() && var3.isEmpty(); ++var4) {
            ItemStack var5 = craftingContainer.getItem(var4);
            if(var5.getItem() == Items.FILLED_MAP) {
               var3 = var5;
            }
         }

         if(var3.isEmpty()) {
            return false;
         } else {
            MapItemSavedData var4 = MapItem.getOrCreateSavedData(var3, level);
            return var4 == null?false:(this.isExplorationMap(var4)?false:var4.scale < 4);
         }
      }
   }

   private boolean isExplorationMap(MapItemSavedData mapItemSavedData) {
      if(mapItemSavedData.decorations != null) {
         for(MapDecoration var3 : mapItemSavedData.decorations.values()) {
            if(var3.getType() == MapDecoration.Type.MANSION || var3.getType() == MapDecoration.Type.MONUMENT) {
               return true;
            }
         }
      }

      return false;
   }

   public ItemStack assemble(CraftingContainer craftingContainer) {
      ItemStack itemStack = ItemStack.EMPTY;

      for(int var3 = 0; var3 < craftingContainer.getContainerSize() && itemStack.isEmpty(); ++var3) {
         ItemStack var4 = craftingContainer.getItem(var3);
         if(var4.getItem() == Items.FILLED_MAP) {
            itemStack = var4;
         }
      }

      itemStack = itemStack.copy();
      itemStack.setCount(1);
      itemStack.getOrCreateTag().putInt("map_scale_direction", 1);
      return itemStack;
   }

   public boolean isSpecial() {
      return true;
   }

   public RecipeSerializer getSerializer() {
      return RecipeSerializer.MAP_EXTENDING;
   }
}
