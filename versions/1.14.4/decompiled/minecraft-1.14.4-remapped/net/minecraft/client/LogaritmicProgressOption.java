package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.ProgressOption;

@ClientJarOnly
public class LogaritmicProgressOption extends ProgressOption {
   public LogaritmicProgressOption(String string, double var2, double var4, float var6, Function function, BiConsumer biConsumer, BiFunction biFunction) {
      super(string, var2, var4, var6, function, biConsumer, biFunction);
   }

   public double toPct(double d) {
      return Math.log(d / this.minValue) / Math.log(this.maxValue / this.minValue);
   }

   public double toValue(double d) {
      return this.minValue * Math.pow(2.718281828459045D, Math.log(this.maxValue / this.minValue) * d);
   }
}
