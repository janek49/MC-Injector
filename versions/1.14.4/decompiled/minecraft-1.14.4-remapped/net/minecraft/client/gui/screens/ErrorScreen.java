package net.minecraft.client.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

@ClientJarOnly
public class ErrorScreen extends Screen {
   private final String message;

   public ErrorScreen(Component component, String message) {
      super(component);
      this.message = message;
   }

   protected void init() {
      super.init();
      this.addButton(new Button(this.width / 2 - 100, 140, 200, 20, I18n.get("gui.cancel", new Object[0]), (button) -> {
         this.minecraft.setScreen((Screen)null);
      }));
   }

   public void render(int var1, int var2, float var3) {
      this.fillGradient(0, 0, this.width, this.height, -12574688, -11530224);
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 90, 16777215);
      this.drawCenteredString(this.font, this.message, this.width / 2, 110, 16777215);
      super.render(var1, var2, var3);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }
}
