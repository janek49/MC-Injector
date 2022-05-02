package net.minecraft.client.gui.screens.recipebook;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Set;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

@ClientJarOnly
public class RecipeCollection {
   private final List recipes = Lists.newArrayList();
   private final Set craftable = Sets.newHashSet();
   private final Set fitsDimensions = Sets.newHashSet();
   private final Set known = Sets.newHashSet();
   private boolean singleResultItem = true;

   public boolean hasKnownRecipes() {
      return !this.known.isEmpty();
   }

   public void updateKnownRecipes(RecipeBook recipeBook) {
      for(Recipe<?> var3 : this.recipes) {
         if(recipeBook.contains(var3)) {
            this.known.add(var3);
         }
      }

   }

   public void canCraft(StackedContents stackedContents, int var2, int var3, RecipeBook recipeBook) {
      for(int var5 = 0; var5 < this.recipes.size(); ++var5) {
         Recipe<?> var6 = (Recipe)this.recipes.get(var5);
         boolean var7 = var6.canCraftInDimensions(var2, var3) && recipeBook.contains(var6);
         if(var7) {
            this.fitsDimensions.add(var6);
         } else {
            this.fitsDimensions.remove(var6);
         }

         if(var7 && stackedContents.canCraft(var6, (IntList)null)) {
            this.craftable.add(var6);
         } else {
            this.craftable.remove(var6);
         }
      }

   }

   public boolean isCraftable(Recipe recipe) {
      return this.craftable.contains(recipe);
   }

   public boolean hasCraftable() {
      return !this.craftable.isEmpty();
   }

   public boolean hasFitting() {
      return !this.fitsDimensions.isEmpty();
   }

   public List getRecipes() {
      return this.recipes;
   }

   public List getRecipes(boolean b) {
      List<Recipe<?>> list = Lists.newArrayList();
      Set<Recipe<?>> var3 = b?this.craftable:this.fitsDimensions;

      for(Recipe<?> var5 : this.recipes) {
         if(var3.contains(var5)) {
            list.add(var5);
         }
      }

      return list;
   }

   public List getDisplayRecipes(boolean b) {
      List<Recipe<?>> list = Lists.newArrayList();

      for(Recipe<?> var4 : this.recipes) {
         if(this.fitsDimensions.contains(var4) && this.craftable.contains(var4) == b) {
            list.add(var4);
         }
      }

      return list;
   }

   public void add(Recipe recipe) {
      this.recipes.add(recipe);
      if(this.singleResultItem) {
         ItemStack var2 = ((Recipe)this.recipes.get(0)).getResultItem();
         ItemStack var3 = recipe.getResultItem();
         this.singleResultItem = ItemStack.isSame(var2, var3) && ItemStack.tagMatches(var2, var3);
      }

   }

   public boolean hasSingleResultItem() {
      return this.singleResultItem;
   }
}
