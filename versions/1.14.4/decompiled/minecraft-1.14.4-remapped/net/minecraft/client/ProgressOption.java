package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.SliderButton;
import net.minecraft.util.Mth;

@ClientJarOnly
public class ProgressOption extends Option {
   protected final float steps;
   protected final double minValue;
   protected double maxValue;
   private final Function getter;
   private final BiConsumer setter;
   private final BiFunction toString;

   public ProgressOption(String string, double minValue, double maxValue, float steps, Function getter, BiConsumer setter, BiFunction toString) {
      super(string);
      this.minValue = minValue;
      this.maxValue = maxValue;
      this.steps = steps;
      this.getter = getter;
      this.setter = setter;
      this.toString = toString;
   }

   public AbstractWidget createButton(Options options, int var2, int var3, int var4) {
      return new SliderButton(options, var2, var3, var4, 20, this);
   }

   public double toPct(double d) {
      return Mth.clamp((this.clamp(d) - this.minValue) / (this.maxValue - this.minValue), 0.0D, 1.0D);
   }

   public double toValue(double d) {
      return this.clamp(Mth.lerp(Mth.clamp(d, 0.0D, 1.0D), this.minValue, this.maxValue));
   }

   private double clamp(double d) {
      if(this.steps > 0.0F) {
         d = (double)(this.steps * (float)Math.round(d / (double)this.steps));
      }

      return Mth.clamp(d, this.minValue, this.maxValue);
   }

   public double getMinValue() {
      return this.minValue;
   }

   public double getMaxValue() {
      return this.maxValue;
   }

   public void setMaxValue(float maxValue) {
      this.maxValue = (double)maxValue;
   }

   public void set(Options options, double var2) {
      this.setter.accept(options, Double.valueOf(var2));
   }

   public double get(Options options) {
      return ((Double)this.getter.apply(options)).doubleValue();
   }

   public String getMessage(Options options) {
      return (String)this.toString.apply(options, this);
   }
}
