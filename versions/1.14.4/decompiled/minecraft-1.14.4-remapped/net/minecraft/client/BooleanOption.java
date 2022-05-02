package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.client.resources.language.I18n;

@ClientJarOnly
public class BooleanOption extends Option {
   private final Predicate getter;
   private final BiConsumer setter;

   public BooleanOption(String string, Predicate getter, BiConsumer setter) {
      super(string);
      this.getter = getter;
      this.setter = setter;
   }

   public void set(Options options, String string) {
      this.set(options, "true".equals(string));
   }

   public void toggle(Options options) {
      this.set(options, !this.get(options));
      options.save();
   }

   private void set(Options options, boolean var2) {
      this.setter.accept(options, Boolean.valueOf(var2));
   }

   public boolean get(Options options) {
      return this.getter.test(options);
   }

   public AbstractWidget createButton(Options options, int var2, int var3, int var4) {
      return new OptionButton(var2, var3, var4, 20, this, this.getMessage(options), (button) -> {
         this.toggle(options);
         button.setMessage(this.getMessage(options));
      });
   }

   public String getMessage(Options options) {
      return this.getCaption() + I18n.get(this.get(options)?"options.on":"options.off", new Object[0]);
   }
}
