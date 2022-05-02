package net.minecraft.client.gui.components;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.Option;
import net.minecraft.client.gui.components.Button;

@ClientJarOnly
public class OptionButton extends Button {
   private final Option option;

   public OptionButton(int var1, int var2, int var3, int var4, Option option, String string, Button.OnPress button$OnPress) {
      super(var1, var2, var3, var4, string, button$OnPress);
      this.option = option;
   }
}
