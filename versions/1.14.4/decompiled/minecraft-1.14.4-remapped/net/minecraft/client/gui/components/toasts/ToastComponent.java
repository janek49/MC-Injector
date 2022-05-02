package net.minecraft.client.gui.components.toasts;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Arrays;
import java.util.Deque;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.util.Mth;

@ClientJarOnly
public class ToastComponent extends GuiComponent {
   private final Minecraft minecraft;
   private final ToastComponent.ToastInstance[] visible = new ToastComponent.ToastInstance[5];
   private final Deque queued = Queues.newArrayDeque();

   public ToastComponent(Minecraft minecraft) {
      this.minecraft = minecraft;
   }

   public void render() {
      if(!this.minecraft.options.hideGui) {
         Lighting.turnOff();

         for(int var1 = 0; var1 < this.visible.length; ++var1) {
            ToastComponent.ToastInstance<?> var2 = this.visible[var1];
            if(var2 != null && var2.render(this.minecraft.window.getGuiScaledWidth(), var1)) {
               this.visible[var1] = null;
            }

            if(this.visible[var1] == null && !this.queued.isEmpty()) {
               this.visible[var1] = new ToastComponent.ToastInstance((Toast)this.queued.removeFirst());
            }
         }

      }
   }

   @Nullable
   public Toast getToast(Class class, Object object) {
      for(ToastComponent.ToastInstance<?> var6 : this.visible) {
         if(var6 != null && class.isAssignableFrom(var6.getToast().getClass()) && var6.getToast().getToken().equals(object)) {
            return var6.getToast();
         }
      }

      for(Toast var4 : this.queued) {
         if(class.isAssignableFrom(var4.getClass()) && var4.getToken().equals(object)) {
            return var4;
         }
      }

      return null;
   }

   public void clear() {
      Arrays.fill(this.visible, (Object)null);
      this.queued.clear();
   }

   public void addToast(Toast toast) {
      this.queued.add(toast);
   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   @ClientJarOnly
   class ToastInstance {
      private final Toast toast;
      private long animationTime;
      private long visibleTime;
      private Toast.Visibility visibility;

      private ToastInstance(Toast toast) {
         this.animationTime = -1L;
         this.visibleTime = -1L;
         this.visibility = Toast.Visibility.SHOW;
         this.toast = toast;
      }

      public Toast getToast() {
         return this.toast;
      }

      private float getVisibility(long l) {
         float var3 = Mth.clamp((float)(l - this.animationTime) / 600.0F, 0.0F, 1.0F);
         var3 = var3 * var3;
         return this.visibility == Toast.Visibility.HIDE?1.0F - var3:var3;
      }

      public boolean render(int var1, int var2) {
         long var3 = Util.getMillis();
         if(this.animationTime == -1L) {
            this.animationTime = var3;
            this.visibility.playSound(ToastComponent.this.minecraft.getSoundManager());
         }

         if(this.visibility == Toast.Visibility.SHOW && var3 - this.animationTime <= 600L) {
            this.visibleTime = var3;
         }

         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)var1 - 160.0F * this.getVisibility(var3), (float)(var2 * 32), (float)(500 + var2));
         Toast.Visibility var5 = this.toast.render(ToastComponent.this, var3 - this.visibleTime);
         GlStateManager.popMatrix();
         if(var5 != this.visibility) {
            this.animationTime = var3 - (long)((int)((1.0F - this.getVisibility(var3)) * 600.0F));
            this.visibility = var5;
            this.visibility.playSound(ToastComponent.this.minecraft.getSoundManager());
         }

         return this.visibility == Toast.Visibility.HIDE && var3 - this.animationTime > 600L;
      }
   }
}
