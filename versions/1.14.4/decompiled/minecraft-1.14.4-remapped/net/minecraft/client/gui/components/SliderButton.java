package net.minecraft.client.gui.components;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.Options;
import net.minecraft.client.ProgressOption;
import net.minecraft.client.gui.components.AbstractSliderButton;

@ClientJarOnly
public class SliderButton extends AbstractSliderButton {
   private final ProgressOption option;

   public SliderButton(Options options, int var2, int var3, int var4, int var5, ProgressOption option) {
      super(options, var2, var3, var4, var5, (double)((float)option.toPct(option.get(options))));
      this.option = option;
      this.updateMessage();
   }

   protected void applyValue() {
      this.option.set(this.options, this.option.toValue(this.value));
      this.options.save();
   }

   protected void updateMessage() {
      this.setMessage(this.option.getMessage(this.options));
   }
}
