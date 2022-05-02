package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

@ClientJarOnly
public class ClientRecipeBook extends RecipeBook {
   private final RecipeManager recipes;
   private final Map collectionsByTab = Maps.newHashMap();
   private final List collections = Lists.newArrayList();

   public ClientRecipeBook(RecipeManager recipes) {
      this.recipes = recipes;
   }

   public void setupCollections() {
      this.collections.clear();
      this.collectionsByTab.clear();
      Table<RecipeBookCategories, String, RecipeCollection> var1 = HashBasedTable.create();

      for(Recipe<?> var3 : this.recipes.getRecipes()) {
         if(!var3.isSpecial()) {
            RecipeBookCategories var4 = getCategory(var3);
            String var5 = var3.getGroup();
            RecipeCollection var6;
            if(var5.isEmpty()) {
               var6 = this.createCollection(var4);
            } else {
               var6 = (RecipeCollection)var1.get(var4, var5);
               if(var6 == null) {
                  var6 = this.createCollection(var4);
                  var1.put(var4, var5, var6);
               }
            }

            var6.add(var3);
         }
      }

   }

   private RecipeCollection createCollection(RecipeBookCategories recipeBookCategories) {
      RecipeCollection recipeCollection = new RecipeCollection();
      this.collections.add(recipeCollection);
      ((List)this.collectionsByTab.computeIfAbsent(recipeBookCategories, (recipeBookCategories) -> {
         return Lists.newArrayList();
      })).add(recipeCollection);
      if(recipeBookCategories != RecipeBookCategories.FURNACE_BLOCKS && recipeBookCategories != RecipeBookCategories.FURNACE_FOOD && recipeBookCategories != RecipeBookCategories.FURNACE_MISC) {
         if(recipeBookCategories != RecipeBookCategories.BLAST_FURNACE_BLOCKS && recipeBookCategories != RecipeBookCategories.BLAST_FURNACE_MISC) {
            if(recipeBookCategories == RecipeBookCategories.SMOKER_FOOD) {
               this.addToCollection(RecipeBookCategories.SMOKER_SEARCH, recipeCollection);
            } else if(recipeBookCategories == RecipeBookCategories.STONECUTTER) {
               this.addToCollection(RecipeBookCategories.STONECUTTER, recipeCollection);
            } else if(recipeBookCategories == RecipeBookCategories.CAMPFIRE) {
               this.addToCollection(RecipeBookCategories.CAMPFIRE, recipeCollection);
            } else {
               this.addToCollection(RecipeBookCategories.SEARCH, recipeCollection);
            }
         } else {
            this.addToCollection(RecipeBookCategories.BLAST_FURNACE_SEARCH, recipeCollection);
         }
      } else {
         this.addToCollection(RecipeBookCategories.FURNACE_SEARCH, recipeCollection);
      }

      return recipeCollection;
   }

   private void addToCollection(RecipeBookCategories recipeBookCategories, RecipeCollection recipeCollection) {
      ((List)this.collectionsByTab.computeIfAbsent(recipeBookCategories, (recipeBookCategories) -> {
         return Lists.newArrayList();
      })).add(recipeCollection);
   }

   private static RecipeBookCategories getCategory(Recipe recipe) {
      RecipeType<?> var1 = recipe.getType();
      if(var1 == RecipeType.SMELTING) {
         return recipe.getResultItem().getItem().isEdible()?RecipeBookCategories.FURNACE_FOOD:(recipe.getResultItem().getItem() instanceof BlockItem?RecipeBookCategories.FURNACE_BLOCKS:RecipeBookCategories.FURNACE_MISC);
      } else if(var1 == RecipeType.BLASTING) {
         return recipe.getResultItem().getItem() instanceof BlockItem?RecipeBookCategories.BLAST_FURNACE_BLOCKS:RecipeBookCategories.BLAST_FURNACE_MISC;
      } else if(var1 == RecipeType.SMOKING) {
         return RecipeBookCategories.SMOKER_FOOD;
      } else if(var1 == RecipeType.STONECUTTING) {
         return RecipeBookCategories.STONECUTTER;
      } else if(var1 == RecipeType.CAMPFIRE_COOKING) {
         return RecipeBookCategories.CAMPFIRE;
      } else {
         ItemStack var2 = recipe.getResultItem();
         CreativeModeTab var3 = var2.getItem().getItemCategory();
         return var3 == CreativeModeTab.TAB_BUILDING_BLOCKS?RecipeBookCategories.BUILDING_BLOCKS:(var3 != CreativeModeTab.TAB_TOOLS && var3 != CreativeModeTab.TAB_COMBAT?(var3 == CreativeModeTab.TAB_REDSTONE?RecipeBookCategories.REDSTONE:RecipeBookCategories.MISC):RecipeBookCategories.EQUIPMENT);
      }
   }

   public static List getCategories(RecipeBookMenu recipeBookMenu) {
      return !(recipeBookMenu instanceof CraftingMenu) && !(recipeBookMenu instanceof InventoryMenu)?(recipeBookMenu instanceof FurnaceMenu?Lists.newArrayList(new RecipeBookCategories[]{RecipeBookCategories.FURNACE_SEARCH, RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.FURNACE_MISC}):(recipeBookMenu instanceof BlastFurnaceMenu?Lists.newArrayList(new RecipeBookCategories[]{RecipeBookCategories.BLAST_FURNACE_SEARCH, RecipeBookCategories.BLAST_FURNACE_BLOCKS, RecipeBookCategories.BLAST_FURNACE_MISC}):(recipeBookMenu instanceof SmokerMenu?Lists.newArrayList(new RecipeBookCategories[]{RecipeBookCategories.SMOKER_SEARCH, RecipeBookCategories.SMOKER_FOOD}):Lists.newArrayList()))):Lists.newArrayList(new RecipeBookCategories[]{RecipeBookCategories.SEARCH, RecipeBookCategories.EQUIPMENT, RecipeBookCategories.BUILDING_BLOCKS, RecipeBookCategories.MISC, RecipeBookCategories.REDSTONE});
   }

   public List getCollections() {
      return this.collections;
   }

   public List getCollection(RecipeBookCategories recipeBookCategories) {
      return (List)this.collectionsByTab.getOrDefault(recipeBookCategories, Collections.emptyList());
   }
}
