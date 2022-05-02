package net.minecraft.client.gui.screens.inventory;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;

@ClientJarOnly
public class PageButton extends Button {
   private final boolean isForward;
   private final boolean playTurnSound;

   public PageButton(int var1, int var2, boolean isForward, Button.OnPress button$OnPress, boolean playTurnSound) {
      super(var1, var2, 23, 13, "", button$OnPress);
      this.isForward = isForward;
      this.playTurnSound = playTurnSound;
   }

   public void renderButton(int var1, int var2, float var3) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      Minecraft.getInstance().getTextureManager().bind(BookViewScreen.BOOK_LOCATION);
      int var4 = 0;
      int var5 = 192;
      if(this.isHovered()) {
         var4 += 23;
      }

      if(!this.isForward) {
         var5 += 13;
      }

      this.blit(this.x, this.y, var4, var5, 23, 13);
   }

   public void playDownSound(SoundManager soundManager) {
      if(this.playTurnSound) {
         soundManager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
      }

   }
}
