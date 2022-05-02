package net.minecraft.client.gui.screens.recipebook;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

@ClientJarOnly
public class RecipeButton extends AbstractWidget {
   private static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
   private RecipeBookMenu menu;
   private RecipeBook book;
   private RecipeCollection collection;
   private float time;
   private float animationTime;
   private int currentIndex;

   public RecipeButton() {
      super(0, 0, 25, 25, "");
   }

   public void init(RecipeCollection collection, RecipeBookPage recipeBookPage) {
      this.collection = collection;
      this.menu = (RecipeBookMenu)recipeBookPage.getMinecraft().player.containerMenu;
      this.book = recipeBookPage.getRecipeBook();
      List<Recipe<?>> var3 = collection.getRecipes(this.book.isFilteringCraftable(this.menu));

      for(Recipe<?> var5 : var3) {
         if(this.book.willHighlight(var5)) {
            recipeBookPage.recipesShown(var3);
            this.animationTime = 15.0F;
            break;
         }
      }

   }

   public RecipeCollection getCollection() {
      return this.collection;
   }

   public void setPosition(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void renderButton(int var1, int var2, float var3) {
      if(!Screen.hasControlDown()) {
         this.time += var3;
      }

      Lighting.turnOnGui();
      Minecraft var4 = Minecraft.getInstance();
      var4.getTextureManager().bind(RECIPE_BOOK_LOCATION);
      GlStateManager.disableLighting();
      int var5 = 29;
      if(!this.collection.hasCraftable()) {
         var5 += 25;
      }

      int var6 = 206;
      if(this.collection.getRecipes(this.book.isFilteringCraftable(this.menu)).size() > 1) {
         var6 += 25;
      }

      boolean var7 = this.animationTime > 0.0F;
      if(var7) {
         float var8 = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
         GlStateManager.scalef(var8, var8, 1.0F);
         GlStateManager.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
         this.animationTime -= var3;
      }

      this.blit(this.x, this.y, var5, var6, this.width, this.height);
      List<Recipe<?>> var8 = this.getOrderedRecipes();
      this.currentIndex = Mth.floor(this.time / 30.0F) % var8.size();
      ItemStack var9 = ((Recipe)var8.get(this.currentIndex)).getResultItem();
      int var10 = 4;
      if(this.collection.hasSingleResultItem() && this.getOrderedRecipes().size() > 1) {
         var4.getItemRenderer().renderAndDecorateItem(var9, this.x + var10 + 1, this.y + var10 + 1);
         --var10;
      }

      var4.getItemRenderer().renderAndDecorateItem(var9, this.x + var10, this.y + var10);
      if(var7) {
         GlStateManager.popMatrix();
      }

      GlStateManager.enableLighting();
      Lighting.turnOff();
   }

   private List getOrderedRecipes() {
      List<Recipe<?>> list = this.collection.getDisplayRecipes(true);
      if(!this.book.isFilteringCraftable(this.menu)) {
         list.addAll(this.collection.getDisplayRecipes(false));
      }

      return list;
   }

   public boolean isOnlyOption() {
      return this.getOrderedRecipes().size() == 1;
   }

   public Recipe getRecipe() {
      List<Recipe<?>> var1 = this.getOrderedRecipes();
      return (Recipe)var1.get(this.currentIndex);
   }

   public List getTooltipText(Screen screen) {
      ItemStack var2 = ((Recipe)this.getOrderedRecipes().get(this.currentIndex)).getResultItem();
      List<String> var3 = screen.getTooltipFromItem(var2);
      if(this.collection.getRecipes(this.book.isFilteringCraftable(this.menu)).size() > 1) {
         var3.add(I18n.get("gui.recipebook.moreRecipes", new Object[0]));
      }

      return var3;
   }

   public int getWidth() {
      return 25;
   }

   protected boolean isValidClickButton(int i) {
      return i == 0 || i == 1;
   }
}
