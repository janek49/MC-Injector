package net.minecraft.client.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.Option;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.PlayerModelPart;

@ClientJarOnly
public class SkinCustomizationScreen extends Screen {
   private final Screen lastScreen;

   public SkinCustomizationScreen(Screen lastScreen) {
      super(new TranslatableComponent("options.skinCustomisation.title", new Object[0]));
      this.lastScreen = lastScreen;
   }

   protected void init() {
      int var1 = 0;

      for(PlayerModelPart var5 : PlayerModelPart.values()) {
         this.addButton(new Button(this.width / 2 - 155 + var1 % 2 * 160, this.height / 6 + 24 * (var1 >> 1), 150, 20, this.getMessage(var5), (button) -> {
            this.minecraft.options.toggleModelPart(var5);
            button.setMessage(this.getMessage(var5));
         }));
         ++var1;
      }

      this.addButton(new OptionButton(this.width / 2 - 155 + var1 % 2 * 160, this.height / 6 + 24 * (var1 >> 1), 150, 20, Option.MAIN_HAND, Option.MAIN_HAND.getMessage(this.minecraft.options), (button) -> {
         Option.MAIN_HAND.toggle(this.minecraft.options, 1);
         this.minecraft.options.save();
         button.setMessage(Option.MAIN_HAND.getMessage(this.minecraft.options));
         this.minecraft.options.broadcastOptions();
      }));
      ++var1;
      if(var1 % 2 == 1) {
         ++var1;
      }

      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 24 * (var1 >> 1), 200, 20, I18n.get("gui.done", new Object[0]), (button) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void removed() {
      this.minecraft.options.save();
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 20, 16777215);
      super.render(var1, var2, var3);
   }

   private String getMessage(PlayerModelPart playerModelPart) {
      String string;
      if(this.minecraft.options.getModelParts().contains(playerModelPart)) {
         string = I18n.get("options.on", new Object[0]);
      } else {
         string = I18n.get("options.off", new Object[0]);
      }

      return playerModelPart.getName().getColoredString() + ": " + string;
   }
}
