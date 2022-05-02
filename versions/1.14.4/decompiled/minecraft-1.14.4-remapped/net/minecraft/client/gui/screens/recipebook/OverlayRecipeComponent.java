package net.minecraft.client.gui.screens.recipebook;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

@ClientJarOnly
public class OverlayRecipeComponent extends GuiComponent implements Widget, GuiEventListener {
   private static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
   private final List recipeButtons = Lists.newArrayList();
   private boolean isVisible;
   private int x;
   private int y;
   private Minecraft minecraft;
   private RecipeCollection collection;
   private Recipe lastRecipeClicked;
   private float time;
   private boolean isFurnaceMenu;

   public void init(Minecraft minecraft, RecipeCollection collection, int x, int y, int var5, int var6, float var7) {
      this.minecraft = minecraft;
      this.collection = collection;
      if(minecraft.player.containerMenu instanceof AbstractFurnaceMenu) {
         this.isFurnaceMenu = true;
      }

      boolean var8 = minecraft.player.getRecipeBook().isFilteringCraftable((RecipeBookMenu)minecraft.player.containerMenu);
      List<Recipe<?>> var9 = collection.getDisplayRecipes(true);
      List<Recipe<?>> var10 = var8?Collections.emptyList():collection.getDisplayRecipes(false);
      int var11 = var9.size();
      int var12 = var11 + var10.size();
      int var13 = var12 <= 16?4:5;
      int var14 = (int)Math.ceil((double)((float)var12 / (float)var13));
      this.x = x;
      this.y = y;
      int var15 = 25;
      float var16 = (float)(this.x + Math.min(var12, var13) * 25);
      float var17 = (float)(var5 + 50);
      if(var16 > var17) {
         this.x = (int)((float)this.x - var7 * (float)((int)((var16 - var17) / var7)));
      }

      float var18 = (float)(this.y + var14 * 25);
      float var19 = (float)(var6 + 50);
      if(var18 > var19) {
         this.y = (int)((float)this.y - var7 * (float)Mth.ceil((var18 - var19) / var7));
      }

      float var20 = (float)this.y;
      float var21 = (float)(var6 - 100);
      if(var20 < var21) {
         this.y = (int)((float)this.y - var7 * (float)Mth.ceil((var20 - var21) / var7));
      }

      this.isVisible = true;
      this.recipeButtons.clear();

      for(int var22 = 0; var22 < var12; ++var22) {
         boolean var23 = var22 < var11;
         Recipe<?> var24 = var23?(Recipe)var9.get(var22):(Recipe)var10.get(var22 - var11);
         int var25 = this.x + 4 + 25 * (var22 % var13);
         int var26 = this.y + 5 + 25 * (var22 / var13);
         if(this.isFurnaceMenu) {
            this.recipeButtons.add(new OverlayRecipeComponent.OverlaySmeltingRecipeButton(var25, var26, var24, var23));
         } else {
            this.recipeButtons.add(new OverlayRecipeComponent.OverlayRecipeButton(var25, var26, var24, var23));
         }
      }

      this.lastRecipeClicked = null;
   }

   public boolean changeFocus(boolean b) {
      return false;
   }

   public RecipeCollection getRecipeCollection() {
      return this.collection;
   }

   public Recipe getLastRecipeClicked() {
      return this.lastRecipeClicked;
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if(var5 != 0) {
         return false;
      } else {
         for(OverlayRecipeComponent.OverlayRecipeButton var7 : this.recipeButtons) {
            if(var7.mouseClicked(var1, var3, var5)) {
               this.lastRecipeClicked = var7.recipe;
               return true;
            }
         }

         return false;
      }
   }

   public boolean isMouseOver(double var1, double var3) {
      return false;
   }

   public void render(int var1, int var2, float var3) {
      if(this.isVisible) {
         this.time += var3;
         Lighting.turnOnGui();
         GlStateManager.enableBlend();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.minecraft.getTextureManager().bind(RECIPE_BOOK_LOCATION);
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, 0.0F, 170.0F);
         int var4 = this.recipeButtons.size() <= 16?4:5;
         int var5 = Math.min(this.recipeButtons.size(), var4);
         int var6 = Mth.ceil((float)this.recipeButtons.size() / (float)var4);
         int var7 = 24;
         int var8 = 4;
         int var9 = 82;
         int var10 = 208;
         this.nineInchSprite(var5, var6, 24, 4, 82, 208);
         GlStateManager.disableBlend();
         Lighting.turnOff();

         for(OverlayRecipeComponent.OverlayRecipeButton var12 : this.recipeButtons) {
            var12.render(var1, var2, var3);
         }

         GlStateManager.popMatrix();
      }
   }

   private void nineInchSprite(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.blit(this.x, this.y, var5, var6, var4, var4);
      this.blit(this.x + var4 * 2 + var1 * var3, this.y, var5 + var3 + var4, var6, var4, var4);
      this.blit(this.x, this.y + var4 * 2 + var2 * var3, var5, var6 + var3 + var4, var4, var4);
      this.blit(this.x + var4 * 2 + var1 * var3, this.y + var4 * 2 + var2 * var3, var5 + var3 + var4, var6 + var3 + var4, var4, var4);

      for(int var7 = 0; var7 < var1; ++var7) {
         this.blit(this.x + var4 + var7 * var3, this.y, var5 + var4, var6, var3, var4);
         this.blit(this.x + var4 + (var7 + 1) * var3, this.y, var5 + var4, var6, var4, var4);

         for(int var8 = 0; var8 < var2; ++var8) {
            if(var7 == 0) {
               this.blit(this.x, this.y + var4 + var8 * var3, var5, var6 + var4, var4, var3);
               this.blit(this.x, this.y + var4 + (var8 + 1) * var3, var5, var6 + var4, var4, var4);
            }

            this.blit(this.x + var4 + var7 * var3, this.y + var4 + var8 * var3, var5 + var4, var6 + var4, var3, var3);
            this.blit(this.x + var4 + (var7 + 1) * var3, this.y + var4 + var8 * var3, var5 + var4, var6 + var4, var4, var3);
            this.blit(this.x + var4 + var7 * var3, this.y + var4 + (var8 + 1) * var3, var5 + var4, var6 + var4, var3, var4);
            this.blit(this.x + var4 + (var7 + 1) * var3 - 1, this.y + var4 + (var8 + 1) * var3 - 1, var5 + var4, var6 + var4, var4 + 1, var4 + 1);
            if(var7 == var1 - 1) {
               this.blit(this.x + var4 * 2 + var1 * var3, this.y + var4 + var8 * var3, var5 + var3 + var4, var6 + var4, var4, var3);
               this.blit(this.x + var4 * 2 + var1 * var3, this.y + var4 + (var8 + 1) * var3, var5 + var3 + var4, var6 + var4, var4, var4);
            }
         }

         this.blit(this.x + var4 + var7 * var3, this.y + var4 * 2 + var2 * var3, var5 + var4, var6 + var3 + var4, var3, var4);
         this.blit(this.x + var4 + (var7 + 1) * var3, this.y + var4 * 2 + var2 * var3, var5 + var4, var6 + var3 + var4, var4, var4);
      }

   }

   public void setVisible(boolean visible) {
      this.isVisible = visible;
   }

   public boolean isVisible() {
      return this.isVisible;
   }

   @ClientJarOnly
   class OverlayRecipeButton extends AbstractWidget implements PlaceRecipe {
      private final Recipe recipe;
      private final boolean isCraftable;
      protected final List ingredientPos = Lists.newArrayList();

      public OverlayRecipeButton(int var2, int var3, Recipe recipe, boolean isCraftable) {
         super(var2, var3, 200, 20, "");
         this.width = 24;
         this.height = 24;
         this.recipe = recipe;
         this.isCraftable = isCraftable;
         this.calculateIngredientsPositions(recipe);
      }

      protected void calculateIngredientsPositions(Recipe recipe) {
         this.placeRecipe(3, 3, -1, recipe, recipe.getIngredients().iterator(), 0);
      }

      public void addItemToSlot(Iterator iterator, int var2, int var3, int var4, int var5) {
         ItemStack[] vars6 = ((Ingredient)iterator.next()).getItems();
         if(vars6.length != 0) {
            this.ingredientPos.add(new OverlayRecipeComponent.OverlayRecipeButton.Pos(3 + var5 * 7, 3 + var4 * 7, vars6));
         }

      }

      public void renderButton(int var1, int var2, float var3) {
         Lighting.turnOnGui();
         GlStateManager.enableAlphaTest();
         OverlayRecipeComponent.this.minecraft.getTextureManager().bind(OverlayRecipeComponent.RECIPE_BOOK_LOCATION);
         int var4 = 152;
         if(!this.isCraftable) {
            var4 += 26;
         }

         int var5 = OverlayRecipeComponent.this.isFurnaceMenu?130:78;
         if(this.isHovered()) {
            var5 += 26;
         }

         this.blit(this.x, this.y, var4, var5, this.width, this.height);

         for(OverlayRecipeComponent.OverlayRecipeButton.Pos var7 : this.ingredientPos) {
            GlStateManager.pushMatrix();
            float var8 = 0.42F;
            int var9 = (int)((float)(this.x + var7.x) / 0.42F - 3.0F);
            int var10 = (int)((float)(this.y + var7.y) / 0.42F - 3.0F);
            GlStateManager.scalef(0.42F, 0.42F, 1.0F);
            GlStateManager.enableLighting();
            OverlayRecipeComponent.this.minecraft.getItemRenderer().renderAndDecorateItem(var7.ingredients[Mth.floor(OverlayRecipeComponent.this.time / 30.0F) % var7.ingredients.length], var9, var10);
            GlStateManager.disableLighting();
            GlStateManager.popMatrix();
         }

         GlStateManager.disableAlphaTest();
         Lighting.turnOff();
      }

      @ClientJarOnly
      public class Pos {
         public final ItemStack[] ingredients;
         public final int x;
         public final int y;

         public Pos(int x, int y, ItemStack[] ingredients) {
            this.x = x;
            this.y = y;
            this.ingredients = ingredients;
         }
      }
   }

   @ClientJarOnly
   class OverlaySmeltingRecipeButton extends OverlayRecipeComponent.OverlayRecipeButton {
      public OverlaySmeltingRecipeButton(int var2, int var3, Recipe recipe, boolean var5) {
         super(var2, var3, recipe, var5);
      }

      protected void calculateIngredientsPositions(Recipe recipe) {
         ItemStack[] vars2 = ((Ingredient)recipe.getIngredients().get(0)).getItems();
         this.ingredientPos.add(new OverlayRecipeComponent.OverlayRecipeButton.Pos(10, 10, vars2));
      }
   }
}
