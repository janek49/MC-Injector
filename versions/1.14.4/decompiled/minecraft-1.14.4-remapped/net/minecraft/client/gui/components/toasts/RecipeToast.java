package net.minecraft.client.gui.components.toasts;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.List;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

@ClientJarOnly
public class RecipeToast implements Toast {
   private final List recipes = Lists.newArrayList();
   private long lastChanged;
   private boolean changed;

   public RecipeToast(Recipe recipe) {
      this.recipes.add(recipe);
   }

   public Toast.Visibility render(ToastComponent toastComponent, long lastChanged) {
      if(this.changed) {
         this.lastChanged = lastChanged;
         this.changed = false;
      }

      if(this.recipes.isEmpty()) {
         return Toast.Visibility.HIDE;
      } else {
         toastComponent.getMinecraft().getTextureManager().bind(TEXTURE);
         GlStateManager.color3f(1.0F, 1.0F, 1.0F);
         toastComponent.blit(0, 0, 0, 32, 160, 32);
         toastComponent.getMinecraft().font.draw(I18n.get("recipe.toast.title", new Object[0]), 30.0F, 7.0F, -11534256);
         toastComponent.getMinecraft().font.draw(I18n.get("recipe.toast.description", new Object[0]), 30.0F, 18.0F, -16777216);
         Lighting.turnOnGui();
         Recipe<?> var4 = (Recipe)this.recipes.get((int)(lastChanged / (5000L / (long)this.recipes.size()) % (long)this.recipes.size()));
         ItemStack var5 = var4.getToastSymbol();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.6F, 0.6F, 1.0F);
         toastComponent.getMinecraft().getItemRenderer().renderAndDecorateItem((LivingEntity)null, var5, 3, 3);
         GlStateManager.popMatrix();
         toastComponent.getMinecraft().getItemRenderer().renderAndDecorateItem((LivingEntity)null, var4.getResultItem(), 8, 8);
         return lastChanged - this.lastChanged >= 5000L?Toast.Visibility.HIDE:Toast.Visibility.SHOW;
      }
   }

   public void addItem(Recipe recipe) {
      if(this.recipes.add(recipe)) {
         this.changed = true;
      }

   }

   public static void addOrUpdate(ToastComponent toastComponent, Recipe recipe) {
      RecipeToast var2 = (RecipeToast)toastComponent.getToast(RecipeToast.class, NO_TOKEN);
      if(var2 == null) {
         toastComponent.addToast(new RecipeToast(recipe));
      } else {
         var2.addItem(recipe);
      }

   }
}
