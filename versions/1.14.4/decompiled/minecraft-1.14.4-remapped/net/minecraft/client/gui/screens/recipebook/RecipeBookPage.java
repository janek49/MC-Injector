package net.minecraft.client.gui.screens.recipebook;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.RecipeShownListener;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.Recipe;

@ClientJarOnly
public class RecipeBookPage {
   private final List buttons = Lists.newArrayListWithCapacity(20);
   private RecipeButton hoveredButton;
   private final OverlayRecipeComponent overlay = new OverlayRecipeComponent();
   private Minecraft minecraft;
   private final List showListeners = Lists.newArrayList();
   private List recipeCollections;
   private StateSwitchingButton forwardButton;
   private StateSwitchingButton backButton;
   private int totalPages;
   private int currentPage;
   private RecipeBook recipeBook;
   private Recipe lastClickedRecipe;
   private RecipeCollection lastClickedRecipeCollection;

   public RecipeBookPage() {
      for(int var1 = 0; var1 < 20; ++var1) {
         this.buttons.add(new RecipeButton());
      }

   }

   public void init(Minecraft minecraft, int var2, int var3) {
      this.minecraft = minecraft;
      this.recipeBook = minecraft.player.getRecipeBook();

      for(int var4 = 0; var4 < this.buttons.size(); ++var4) {
         ((RecipeButton)this.buttons.get(var4)).setPosition(var2 + 11 + 25 * (var4 % 5), var3 + 31 + 25 * (var4 / 5));
      }

      this.forwardButton = new StateSwitchingButton(var2 + 93, var3 + 137, 12, 17, false);
      this.forwardButton.initTextureValues(1, 208, 13, 18, RecipeBookComponent.RECIPE_BOOK_LOCATION);
      this.backButton = new StateSwitchingButton(var2 + 38, var3 + 137, 12, 17, true);
      this.backButton.initTextureValues(1, 208, 13, 18, RecipeBookComponent.RECIPE_BOOK_LOCATION);
   }

   public void addListener(RecipeBookComponent recipeBookComponent) {
      this.showListeners.remove(recipeBookComponent);
      this.showListeners.add(recipeBookComponent);
   }

   public void updateCollections(List recipeCollections, boolean var2) {
      this.recipeCollections = recipeCollections;
      this.totalPages = (int)Math.ceil((double)recipeCollections.size() / 20.0D);
      if(this.totalPages <= this.currentPage || var2) {
         this.currentPage = 0;
      }

      this.updateButtonsForPage();
   }

   private void updateButtonsForPage() {
      int var1 = 20 * this.currentPage;

      for(int var2 = 0; var2 < this.buttons.size(); ++var2) {
         RecipeButton var3 = (RecipeButton)this.buttons.get(var2);
         if(var1 + var2 < this.recipeCollections.size()) {
            RecipeCollection var4 = (RecipeCollection)this.recipeCollections.get(var1 + var2);
            var3.init(var4, this);
            var3.visible = true;
         } else {
            var3.visible = false;
         }
      }

      this.updateArrowButtons();
   }

   private void updateArrowButtons() {
      this.forwardButton.visible = this.totalPages > 1 && this.currentPage < this.totalPages - 1;
      this.backButton.visible = this.totalPages > 1 && this.currentPage > 0;
   }

   public void render(int var1, int var2, int var3, int var4, float var5) {
      if(this.totalPages > 1) {
         String var6 = this.currentPage + 1 + "/" + this.totalPages;
         int var7 = this.minecraft.font.width(var6);
         this.minecraft.font.draw(var6, (float)(var1 - var7 / 2 + 73), (float)(var2 + 141), -1);
      }

      Lighting.turnOff();
      this.hoveredButton = null;

      for(RecipeButton var7 : this.buttons) {
         var7.render(var3, var4, var5);
         if(var7.visible && var7.isHovered()) {
            this.hoveredButton = var7;
         }
      }

      this.backButton.render(var3, var4, var5);
      this.forwardButton.render(var3, var4, var5);
      this.overlay.render(var3, var4, var5);
   }

   public void renderTooltip(int var1, int var2) {
      if(this.minecraft.screen != null && this.hoveredButton != null && !this.overlay.isVisible()) {
         this.minecraft.screen.renderTooltip(this.hoveredButton.getTooltipText(this.minecraft.screen), var1, var2);
      }

   }

   @Nullable
   public Recipe getLastClickedRecipe() {
      return this.lastClickedRecipe;
   }

   @Nullable
   public RecipeCollection getLastClickedRecipeCollection() {
      return this.lastClickedRecipeCollection;
   }

   public void setInvisible() {
      this.overlay.setVisible(false);
   }

   public boolean mouseClicked(double var1, double var3, int var5, int var6, int var7, int var8, int var9) {
      this.lastClickedRecipe = null;
      this.lastClickedRecipeCollection = null;
      if(this.overlay.isVisible()) {
         if(this.overlay.mouseClicked(var1, var3, var5)) {
            this.lastClickedRecipe = this.overlay.getLastRecipeClicked();
            this.lastClickedRecipeCollection = this.overlay.getRecipeCollection();
         } else {
            this.overlay.setVisible(false);
         }

         return true;
      } else if(this.forwardButton.mouseClicked(var1, var3, var5)) {
         ++this.currentPage;
         this.updateButtonsForPage();
         return true;
      } else if(this.backButton.mouseClicked(var1, var3, var5)) {
         --this.currentPage;
         this.updateButtonsForPage();
         return true;
      } else {
         for(RecipeButton var11 : this.buttons) {
            if(var11.mouseClicked(var1, var3, var5)) {
               if(var5 == 0) {
                  this.lastClickedRecipe = var11.getRecipe();
                  this.lastClickedRecipeCollection = var11.getCollection();
               } else if(var5 == 1 && !this.overlay.isVisible() && !var11.isOnlyOption()) {
                  this.overlay.init(this.minecraft, var11.getCollection(), var11.x, var11.y, var6 + var8 / 2, var7 + 13 + var9 / 2, (float)var11.getWidth());
               }

               return true;
            }
         }

         return false;
      }
   }

   public void recipesShown(List list) {
      for(RecipeShownListener var3 : this.showListeners) {
         var3.recipesShown(list);
      }

   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   public RecipeBook getRecipeBook() {
      return this.recipeBook;
   }
}
