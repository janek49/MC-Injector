package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.realms.RealmsAbstractButtonProxy;
import net.minecraft.realms.RealmsButton;

@ClientJarOnly
public class RealmsButtonProxy extends Button implements RealmsAbstractButtonProxy {
   private final RealmsButton button;

   public RealmsButtonProxy(RealmsButton button, int var2, int var3, String string, int var5, int var6, Button.OnPress button$OnPress) {
      super(var2, var3, var5, var6, string, button$OnPress);
      this.button = button;
   }

   public boolean active() {
      return this.active;
   }

   public void active(boolean active) {
      this.active = active;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public void setMessage(String message) {
      super.setMessage(message);
   }

   public int getWidth() {
      return super.getWidth();
   }

   public int y() {
      return this.y;
   }

   public void onClick(double var1, double var3) {
      this.button.onPress();
   }

   public void onRelease(double var1, double var3) {
      this.button.onRelease(var1, var3);
   }

   public void renderBg(Minecraft minecraft, int var2, int var3) {
      this.button.renderBg(var2, var3);
   }

   public void renderButton(int var1, int var2, float var3) {
      this.button.renderButton(var1, var2, var3);
   }

   public void superRenderButton(int var1, int var2, float var3) {
      super.renderButton(var1, var2, var3);
   }

   public RealmsButton getButton() {
      return this.button;
   }

   public int getYImage(boolean b) {
      return this.button.getYImage(b);
   }

   public int getSuperYImage(boolean b) {
      return super.getYImage(b);
   }

   public int getHeight() {
      return this.height;
   }

   public boolean isHovered() {
      return super.isHovered();
   }
}
