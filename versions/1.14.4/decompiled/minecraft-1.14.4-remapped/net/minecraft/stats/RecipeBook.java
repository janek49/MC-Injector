package net.minecraft.stats;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.SmokerMenu;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeBook {
   protected final Set known = Sets.newHashSet();
   protected final Set highlight = Sets.newHashSet();
   protected boolean guiOpen;
   protected boolean filteringCraftable;
   protected boolean furnaceGuiOpen;
   protected boolean furnaceFilteringCraftable;
   protected boolean blastingFurnaceGuiOpen;
   protected boolean blastingFurnaceFilteringCraftable;
   protected boolean smokerGuiOpen;
   protected boolean smokerFilteringCraftable;

   public void copyOverData(RecipeBook recipeBook) {
      this.known.clear();
      this.highlight.clear();
      this.known.addAll(recipeBook.known);
      this.highlight.addAll(recipeBook.highlight);
   }

   public void add(Recipe recipe) {
      if(!recipe.isSpecial()) {
         this.add(recipe.getId());
      }

   }

   protected void add(ResourceLocation resourceLocation) {
      this.known.add(resourceLocation);
   }

   public boolean contains(@Nullable Recipe recipe) {
      return recipe == null?false:this.known.contains(recipe.getId());
   }

   public void remove(Recipe recipe) {
      this.remove(recipe.getId());
   }

   protected void remove(ResourceLocation resourceLocation) {
      this.known.remove(resourceLocation);
      this.highlight.remove(resourceLocation);
   }

   public boolean willHighlight(Recipe recipe) {
      return this.highlight.contains(recipe.getId());
   }

   public void removeHighlight(Recipe recipe) {
      this.highlight.remove(recipe.getId());
   }

   public void addHighlight(Recipe recipe) {
      this.addHighlight(recipe.getId());
   }

   protected void addHighlight(ResourceLocation resourceLocation) {
      this.highlight.add(resourceLocation);
   }

   public boolean isGuiOpen() {
      return this.guiOpen;
   }

   public void setGuiOpen(boolean guiOpen) {
      this.guiOpen = guiOpen;
   }

   public boolean isFilteringCraftable(RecipeBookMenu recipeBookMenu) {
      return recipeBookMenu instanceof FurnaceMenu?this.furnaceFilteringCraftable:(recipeBookMenu instanceof BlastFurnaceMenu?this.blastingFurnaceFilteringCraftable:(recipeBookMenu instanceof SmokerMenu?this.smokerFilteringCraftable:this.filteringCraftable));
   }

   public boolean isFilteringCraftable() {
      return this.filteringCraftable;
   }

   public void setFilteringCraftable(boolean filteringCraftable) {
      this.filteringCraftable = filteringCraftable;
   }

   public boolean isFurnaceGuiOpen() {
      return this.furnaceGuiOpen;
   }

   public void setFurnaceGuiOpen(boolean furnaceGuiOpen) {
      this.furnaceGuiOpen = furnaceGuiOpen;
   }

   public boolean isFurnaceFilteringCraftable() {
      return this.furnaceFilteringCraftable;
   }

   public void setFurnaceFilteringCraftable(boolean furnaceFilteringCraftable) {
      this.furnaceFilteringCraftable = furnaceFilteringCraftable;
   }

   public boolean isBlastingFurnaceGuiOpen() {
      return this.blastingFurnaceGuiOpen;
   }

   public void setBlastingFurnaceGuiOpen(boolean blastingFurnaceGuiOpen) {
      this.blastingFurnaceGuiOpen = blastingFurnaceGuiOpen;
   }

   public boolean isBlastingFurnaceFilteringCraftable() {
      return this.blastingFurnaceFilteringCraftable;
   }

   public void setBlastingFurnaceFilteringCraftable(boolean blastingFurnaceFilteringCraftable) {
      this.blastingFurnaceFilteringCraftable = blastingFurnaceFilteringCraftable;
   }

   public boolean isSmokerGuiOpen() {
      return this.smokerGuiOpen;
   }

   public void setSmokerGuiOpen(boolean smokerGuiOpen) {
      this.smokerGuiOpen = smokerGuiOpen;
   }

   public boolean isSmokerFilteringCraftable() {
      return this.smokerFilteringCraftable;
   }

   public void setSmokerFilteringCraftable(boolean smokerFilteringCraftable) {
      this.smokerFilteringCraftable = smokerFilteringCraftable;
   }
}
