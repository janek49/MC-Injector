package net.minecraft.client.gui.components;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.util.Mth;

@ClientJarOnly
public abstract class AbstractSliderButton extends AbstractWidget {
   protected final Options options;
   protected double value;

   protected AbstractSliderButton(int var1, int var2, int var3, int var4, double var5) {
      this(Minecraft.getInstance().options, var1, var2, var3, var4, var5);
   }

   protected AbstractSliderButton(Options options, int var2, int var3, int var4, int var5, double value) {
      super(var2, var3, var4, var5, "");
      this.options = options;
      this.value = value;
   }

   protected int getYImage(boolean b) {
      return 0;
   }

   protected String getNarrationMessage() {
      return I18n.get("gui.narrate.slider", new Object[]{this.getMessage()});
   }

   protected void renderBg(Minecraft minecraft, int var2, int var3) {
      minecraft.getTextureManager().bind(WIDGETS_LOCATION);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      int var4 = (this.isHovered()?2:1) * 20;
      this.blit(this.x + (int)(this.value * (double)(this.width - 8)), this.y, 0, 46 + var4, 4, 20);
      this.blit(this.x + (int)(this.value * (double)(this.width - 8)) + 4, this.y, 196, 46 + var4, 4, 20);
   }

   public void onClick(double valueFromMouse, double var3) {
      this.setValueFromMouse(valueFromMouse);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      boolean var4 = var1 == 263;
      if(var4 || var1 == 262) {
         float var5 = var4?-1.0F:1.0F;
         this.setValue(this.value + (double)(var5 / (float)(this.width - 8)));
      }

      return false;
   }

   private void setValueFromMouse(double valueFromMouse) {
      this.setValue((valueFromMouse - (double)(this.x + 4)) / (double)(this.width - 8));
   }

   private void setValue(double value) {
      double var3 = this.value;
      this.value = Mth.clamp(value, 0.0D, 1.0D);
      if(var3 != this.value) {
         this.applyValue();
      }

      this.updateMessage();
   }

   protected void onDrag(double valueFromMouse, double var3, double var5, double var7) {
      this.setValueFromMouse(valueFromMouse);
      super.onDrag(valueFromMouse, var3, var5, var7);
   }

   public void playDownSound(SoundManager soundManager) {
   }

   public void onRelease(double var1, double var3) {
      super.playDownSound(Minecraft.getInstance().getSoundManager());
   }

   protected abstract void updateMessage();

   protected abstract void applyValue();
}
