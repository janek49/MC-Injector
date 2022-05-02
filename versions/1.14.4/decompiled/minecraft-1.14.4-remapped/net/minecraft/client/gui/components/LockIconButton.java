package net.minecraft.client.gui.components;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;

@ClientJarOnly
public class LockIconButton extends Button {
   private boolean locked;

   public LockIconButton(int var1, int var2, Button.OnPress button$OnPress) {
      super(var1, var2, 20, 20, I18n.get("narrator.button.difficulty_lock", new Object[0]), button$OnPress);
   }

   protected String getNarrationMessage() {
      return super.getNarrationMessage() + ". " + (this.isLocked()?I18n.get("narrator.button.difficulty_lock.locked", new Object[0]):I18n.get("narrator.button.difficulty_lock.unlocked", new Object[0]));
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean locked) {
      this.locked = locked;
   }

   public void renderButton(int var1, int var2, float var3) {
      Minecraft.getInstance().getTextureManager().bind(Button.WIDGETS_LOCATION);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      LockIconButton.Icon var4;
      if(!this.active) {
         var4 = this.locked?LockIconButton.Icon.LOCKED_DISABLED:LockIconButton.Icon.UNLOCKED_DISABLED;
      } else if(this.isHovered()) {
         var4 = this.locked?LockIconButton.Icon.LOCKED_HOVER:LockIconButton.Icon.UNLOCKED_HOVER;
      } else {
         var4 = this.locked?LockIconButton.Icon.LOCKED:LockIconButton.Icon.UNLOCKED;
      }

      this.blit(this.x, this.y, var4.getX(), var4.getY(), this.width, this.height);
   }

   @ClientJarOnly
   static enum Icon {
      LOCKED(0, 146),
      LOCKED_HOVER(0, 166),
      LOCKED_DISABLED(0, 186),
      UNLOCKED(20, 146),
      UNLOCKED_HOVER(20, 166),
      UNLOCKED_DISABLED(20, 186);

      private final int x;
      private final int y;

      private Icon(int x, int y) {
         this.x = x;
         this.y = y;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }
   }
}
