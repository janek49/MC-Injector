package net.minecraft.client.gui.components.toasts;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;

@ClientJarOnly
public class SystemToast implements Toast {
   private final SystemToast.SystemToastIds id;
   private String title;
   private String message;
   private long lastChanged;
   private boolean changed;

   public SystemToast(SystemToast.SystemToastIds id, Component var2, @Nullable Component var3) {
      this.id = id;
      this.title = var2.getString();
      this.message = var3 == null?null:var3.getString();
   }

   public Toast.Visibility render(ToastComponent toastComponent, long lastChanged) {
      if(this.changed) {
         this.lastChanged = lastChanged;
         this.changed = false;
      }

      toastComponent.getMinecraft().getTextureManager().bind(TEXTURE);
      GlStateManager.color3f(1.0F, 1.0F, 1.0F);
      toastComponent.blit(0, 0, 0, 64, 160, 32);
      if(this.message == null) {
         toastComponent.getMinecraft().font.draw(this.title, 18.0F, 12.0F, -256);
      } else {
         toastComponent.getMinecraft().font.draw(this.title, 18.0F, 7.0F, -256);
         toastComponent.getMinecraft().font.draw(this.message, 18.0F, 18.0F, -1);
      }

      return lastChanged - this.lastChanged < 5000L?Toast.Visibility.SHOW:Toast.Visibility.HIDE;
   }

   public void reset(Component var1, @Nullable Component var2) {
      this.title = var1.getString();
      this.message = var2 == null?null:var2.getString();
      this.changed = true;
   }

   public SystemToast.SystemToastIds getToken() {
      return this.id;
   }

   public static void addOrUpdate(ToastComponent toastComponent, SystemToast.SystemToastIds systemToast$SystemToastIds, Component var2, @Nullable Component var3) {
      SystemToast var4 = (SystemToast)toastComponent.getToast(SystemToast.class, systemToast$SystemToastIds);
      if(var4 == null) {
         toastComponent.addToast(new SystemToast(systemToast$SystemToastIds, var2, var3));
      } else {
         var4.reset(var2, var3);
      }

   }

   // $FF: synthetic method
   public Object getToken() {
      return this.getToken();
   }

   @ClientJarOnly
   public static enum SystemToastIds {
      TUTORIAL_HINT,
      NARRATOR_TOGGLE,
      WORLD_BACKUP;
   }
}
