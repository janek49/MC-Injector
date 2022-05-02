package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.realms.RealmsAbstractButtonProxy;
import net.minecraft.realms.RealmsSliderButton;

@ClientJarOnly
public class RealmsSliderButtonProxy extends AbstractSliderButton implements RealmsAbstractButtonProxy {
   private final RealmsSliderButton button;

   public RealmsSliderButtonProxy(RealmsSliderButton button, int var2, int var3, int var4, int var5, double var6) {
      super(var2, var3, var4, var5, var6);
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
      this.button.onClick(var1, var3);
   }

   public void onRelease(double var1, double var3) {
      this.button.onRelease(var1, var3);
   }

   public void updateMessage() {
      this.button.updateMessage();
   }

   public void applyValue() {
      this.button.applyValue();
   }

   public double getValue() {
      return this.value;
   }

   public void setValue(double value) {
      this.value = value;
   }

   public void renderBg(Minecraft minecraft, int var2, int var3) {
      super.renderBg(minecraft, var2, var3);
   }

   public RealmsSliderButton getButton() {
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
}
