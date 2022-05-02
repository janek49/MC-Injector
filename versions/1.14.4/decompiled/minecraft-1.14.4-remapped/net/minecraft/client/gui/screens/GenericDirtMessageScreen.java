package net.minecraft.client.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@ClientJarOnly
public class GenericDirtMessageScreen extends Screen {
   public GenericDirtMessageScreen(Component component) {
      super(component);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void render(int var1, int var2, float var3) {
      this.renderDirtBackground(0);
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 70, 16777215);
      super.render(var1, var2, var3);
   }
}
