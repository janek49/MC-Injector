package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.RealmsSliderButtonProxy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

@ClientJarOnly
public abstract class RealmsSliderButton extends AbstractRealmsButton {
   protected static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   private final int id;
   private final RealmsSliderButtonProxy proxy;
   private final double minValue;
   private final double maxValue;

   public RealmsSliderButton(int id, int var2, int var3, int var4, int var5, double minValue, double maxValue) {
      this.id = id;
      this.minValue = minValue;
      this.maxValue = maxValue;
      this.proxy = new RealmsSliderButtonProxy(this, var2, var3, var4, 20, this.toPct((double)var5));
      this.getProxy().setMessage(this.getMessage());
   }

   public String getMessage() {
      return "";
   }

   public double toPct(double d) {
      return Mth.clamp((this.clamp(d) - this.minValue) / (this.maxValue - this.minValue), 0.0D, 1.0D);
   }

   public double toValue(double d) {
      return this.clamp(Mth.lerp(Mth.clamp(d, 0.0D, 1.0D), this.minValue, this.maxValue));
   }

   public double clamp(double d) {
      return Mth.clamp(d, this.minValue, this.maxValue);
   }

   public int getYImage(boolean b) {
      return 0;
   }

   public void onClick(double var1, double var3) {
   }

   public void onRelease(double var1, double var3) {
   }

   public RealmsSliderButtonProxy getProxy() {
      return this.proxy;
   }

   public double getValue() {
      return this.proxy.getValue();
   }

   public void setValue(double value) {
      this.proxy.setValue(value);
   }

   public int id() {
      return this.id;
   }

   public void setMessage(String message) {
      this.proxy.setMessage(message);
   }

   public int getWidth() {
      return this.proxy.getWidth();
   }

   public int getHeight() {
      return this.proxy.getHeight();
   }

   public int y() {
      return this.proxy.y();
   }

   public abstract void applyValue();

   public void updateMessage() {
      this.proxy.setMessage(this.getMessage());
   }
}
