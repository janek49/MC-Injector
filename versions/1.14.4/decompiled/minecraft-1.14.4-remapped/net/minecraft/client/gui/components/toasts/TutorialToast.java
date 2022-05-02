package net.minecraft.client.gui.components.toasts;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

@ClientJarOnly
public class TutorialToast implements Toast {
   private final TutorialToast.Icons icon;
   private final String title;
   private final String message;
   private Toast.Visibility visibility = Toast.Visibility.SHOW;
   private long lastProgressTime;
   private float lastProgress;
   private float progress;
   private final boolean progressable;

   public TutorialToast(TutorialToast.Icons icon, Component var2, @Nullable Component var3, boolean progressable) {
      this.icon = icon;
      this.title = var2.getColoredString();
      this.message = var3 == null?null:var3.getColoredString();
      this.progressable = progressable;
   }

   public Toast.Visibility render(ToastComponent toastComponent, long lastProgressTime) {
      toastComponent.getMinecraft().getTextureManager().bind(TEXTURE);
      GlStateManager.color3f(1.0F, 1.0F, 1.0F);
      toastComponent.blit(0, 0, 0, 96, 160, 32);
      this.icon.render(toastComponent, 6, 6);
      if(this.message == null) {
         toastComponent.getMinecraft().font.draw(this.title, 30.0F, 12.0F, -11534256);
      } else {
         toastComponent.getMinecraft().font.draw(this.title, 30.0F, 7.0F, -11534256);
         toastComponent.getMinecraft().font.draw(this.message, 30.0F, 18.0F, -16777216);
      }

      if(this.progressable) {
         GuiComponent.fill(3, 28, 157, 29, -1);
         float var4 = (float)Mth.clampedLerp((double)this.lastProgress, (double)this.progress, (double)((float)(lastProgressTime - this.lastProgressTime) / 100.0F));
         int var5;
         if(this.progress >= this.lastProgress) {
            var5 = -16755456;
         } else {
            var5 = -11206656;
         }

         GuiComponent.fill(3, 28, (int)(3.0F + 154.0F * var4), 29, var5);
         this.lastProgress = var4;
         this.lastProgressTime = lastProgressTime;
      }

      return this.visibility;
   }

   public void hide() {
      this.visibility = Toast.Visibility.HIDE;
   }

   public void updateProgress(float progress) {
      this.progress = progress;
   }

   @ClientJarOnly
   public static enum Icons {
      MOVEMENT_KEYS(0, 0),
      MOUSE(1, 0),
      TREE(2, 0),
      RECIPE_BOOK(0, 1),
      WOODEN_PLANKS(1, 1);

      private final int x;
      private final int y;

      private Icons(int x, int y) {
         this.x = x;
         this.y = y;
      }

      public void render(GuiComponent guiComponent, int var2, int var3) {
         GlStateManager.enableBlend();
         guiComponent.blit(var2, var3, 176 + this.x * 20, this.y * 20, 20, 20);
         GlStateManager.enableBlend();
      }
   }
}
