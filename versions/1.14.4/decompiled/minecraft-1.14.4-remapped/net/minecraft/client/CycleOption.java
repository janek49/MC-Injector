package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionButton;

@ClientJarOnly
public class CycleOption extends Option {
   private final BiConsumer setter;
   private final BiFunction toString;

   public CycleOption(String string, BiConsumer setter, BiFunction toString) {
      super(string);
      this.setter = setter;
      this.toString = toString;
   }

   public void toggle(Options options, int var2) {
      this.setter.accept(options, Integer.valueOf(var2));
      options.save();
   }

   public AbstractWidget createButton(Options options, int var2, int var3, int var4) {
      return new OptionButton(var2, var3, var4, 20, this, this.getMessage(options), (button) -> {
         this.toggle(options, 1);
         button.setMessage(this.getMessage(options));
      });
   }

   public String getMessage(Options options) {
      return (String)this.toString.apply(options, this);
   }
}
