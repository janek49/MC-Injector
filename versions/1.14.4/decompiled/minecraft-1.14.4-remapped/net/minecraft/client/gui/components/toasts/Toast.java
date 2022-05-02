package net.minecraft.client.gui.components.toasts;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

@ClientJarOnly
public interface Toast {
   ResourceLocation TEXTURE = new ResourceLocation("textures/gui/toasts.png");
   Object NO_TOKEN = new Object();

   Toast.Visibility render(ToastComponent var1, long var2);

   default Object getToken() {
      return NO_TOKEN;
   }

   @ClientJarOnly
   public static enum Visibility {
      SHOW(SoundEvents.UI_TOAST_IN),
      HIDE(SoundEvents.UI_TOAST_OUT);

      private final SoundEvent soundEvent;

      private Visibility(SoundEvent soundEvent) {
         this.soundEvent = soundEvent;
      }

      public void playSound(SoundManager soundManager) {
         soundManager.play(SimpleSoundInstance.forUI(this.soundEvent, 1.0F, 1.0F));
      }
   }
}
