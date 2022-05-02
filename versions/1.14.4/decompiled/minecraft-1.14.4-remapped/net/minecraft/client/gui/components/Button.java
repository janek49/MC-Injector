package net.minecraft.client.gui.components;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.gui.components.AbstractButton;

@ClientJarOnly
public class Button extends AbstractButton {
   protected final Button.OnPress onPress;

   public Button(int var1, int var2, int var3, int var4, String string, Button.OnPress onPress) {
      super(var1, var2, var3, var4, string);
      this.onPress = onPress;
   }

   public void onPress() {
      this.onPress.onPress(this);
   }

   @ClientJarOnly
   public interface OnPress {
      void onPress(Button var1);
   }
}
