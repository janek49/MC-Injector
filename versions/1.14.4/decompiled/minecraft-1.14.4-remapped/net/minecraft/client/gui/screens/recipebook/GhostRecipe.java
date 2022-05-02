package net.minecraft.client.gui.screens.recipebook;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

@ClientJarOnly
public class GhostRecipe {
   private Recipe recipe;
   private final List ingredients = Lists.newArrayList();
   private float time;

   public void clear() {
      this.recipe = null;
      this.ingredients.clear();
      this.time = 0.0F;
   }

   public void addIngredient(Ingredient ingredient, int var2, int var3) {
      this.ingredients.add(new GhostRecipe.GhostIngredient(ingredient, var2, var3));
   }

   public GhostRecipe.GhostIngredient get(int i) {
      return (GhostRecipe.GhostIngredient)this.ingredients.get(i);
   }

   public int size() {
      return this.ingredients.size();
   }

   @Nullable
   public Recipe getRecipe() {
      return this.recipe;
   }

   public void setRecipe(Recipe recipe) {
      this.recipe = recipe;
   }

   public void render(Minecraft minecraft, int var2, int var3, boolean var4, float var5) {
      if(!Screen.hasControlDown()) {
         this.time += var5;
      }

      Lighting.turnOnGui();
      GlStateManager.disableLighting();

      for(int var6 = 0; var6 < this.ingredients.size(); ++var6) {
         GhostRecipe.GhostIngredient var7 = (GhostRecipe.GhostIngredient)this.ingredients.get(var6);
         int var8 = var7.getX() + var2;
         int var9 = var7.getY() + var3;
         if(var6 == 0 && var4) {
            GuiComponent.fill(var8 - 4, var9 - 4, var8 + 20, var9 + 20, 822018048);
         } else {
            GuiComponent.fill(var8, var9, var8 + 16, var9 + 16, 822018048);
         }

         ItemStack var10 = var7.getItem();
         ItemRenderer var11 = minecraft.getItemRenderer();
         var11.renderAndDecorateItem(minecraft.player, var10, var8, var9);
         GlStateManager.depthFunc(516);
         GuiComponent.fill(var8, var9, var8 + 16, var9 + 16, 822083583);
         GlStateManager.depthFunc(515);
         if(var6 == 0) {
            var11.renderGuiItemDecorations(minecraft.font, var10, var8, var9);
         }

         GlStateManager.enableLighting();
      }

      Lighting.turnOff();
   }

   @ClientJarOnly
   public class GhostIngredient {
      private final Ingredient ingredient;
      private final int x;
      private final int y;

      public GhostIngredient(Ingredient ingredient, int x, int y) {
         this.ingredient = ingredient;
         this.x = x;
         this.y = y;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public ItemStack getItem() {
         ItemStack[] vars1 = this.ingredient.getItems();
         return vars1[Mth.floor(GhostRecipe.this.time / 30.0F) % vars1.length];
      }
   }
}
