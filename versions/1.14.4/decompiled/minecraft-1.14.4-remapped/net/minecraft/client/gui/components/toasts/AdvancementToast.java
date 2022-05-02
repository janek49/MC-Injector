package net.minecraft.client.gui.components.toasts;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

@ClientJarOnly
public class AdvancementToast implements Toast {
   private final Advancement advancement;
   private boolean playedSound;

   public AdvancementToast(Advancement advancement) {
      this.advancement = advancement;
   }

   public Toast.Visibility render(ToastComponent toastComponent, long var2) {
      toastComponent.getMinecraft().getTextureManager().bind(TEXTURE);
      GlStateManager.color3f(1.0F, 1.0F, 1.0F);
      DisplayInfo var4 = this.advancement.getDisplay();
      toastComponent.blit(0, 0, 0, 0, 160, 32);
      if(var4 != null) {
         List<String> var5 = toastComponent.getMinecraft().font.split(var4.getTitle().getColoredString(), 125);
         int var6 = var4.getFrame() == FrameType.CHALLENGE?16746751:16776960;
         if(var5.size() == 1) {
            toastComponent.getMinecraft().font.draw(I18n.get("advancements.toast." + var4.getFrame().getName(), new Object[0]), 30.0F, 7.0F, var6 | -16777216);
            toastComponent.getMinecraft().font.draw(var4.getTitle().getColoredString(), 30.0F, 18.0F, -1);
         } else {
            int var7 = 1500;
            float var8 = 300.0F;
            if(var2 < 1500L) {
               int var9 = Mth.floor(Mth.clamp((float)(1500L - var2) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
               toastComponent.getMinecraft().font.draw(I18n.get("advancements.toast." + var4.getFrame().getName(), new Object[0]), 30.0F, 11.0F, var6 | var9);
            } else {
               int var9 = Mth.floor(Mth.clamp((float)(var2 - 1500L) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
               int var10001 = var5.size();
               toastComponent.getMinecraft().font.getClass();
               int var10 = 16 - var10001 * 9 / 2;

               for(String var12 : var5) {
                  toastComponent.getMinecraft().font.draw(var12, 30.0F, (float)var10, 16777215 | var9);
                  toastComponent.getMinecraft().font.getClass();
                  var10 += 9;
               }
            }
         }

         if(!this.playedSound && var2 > 0L) {
            this.playedSound = true;
            if(var4.getFrame() == FrameType.CHALLENGE) {
               toastComponent.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F));
            }
         }

         Lighting.turnOnGui();
         toastComponent.getMinecraft().getItemRenderer().renderAndDecorateItem((LivingEntity)null, var4.getIcon(), 8, 8);
         return var2 >= 5000L?Toast.Visibility.HIDE:Toast.Visibility.SHOW;
      } else {
         return Toast.Visibility.HIDE;
      }
   }
}
