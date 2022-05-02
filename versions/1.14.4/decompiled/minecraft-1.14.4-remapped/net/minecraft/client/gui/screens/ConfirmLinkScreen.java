package net.minecraft.client.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

@ClientJarOnly
public class ConfirmLinkScreen extends ConfirmScreen {
   private final String warning;
   private final String copyButton;
   private final String url;
   private final boolean showWarning;

   public ConfirmLinkScreen(BooleanConsumer booleanConsumer, String url, boolean var3) {
      super(booleanConsumer, new TranslatableComponent(var3?"chat.link.confirmTrusted":"chat.link.confirm", new Object[0]), new TextComponent(url));
      this.yesButton = I18n.get(var3?"chat.link.open":"gui.yes", new Object[0]);
      this.noButton = I18n.get(var3?"gui.cancel":"gui.no", new Object[0]);
      this.copyButton = I18n.get("chat.copy", new Object[0]);
      this.warning = I18n.get("chat.link.warning", new Object[0]);
      this.showWarning = !var3;
      this.url = url;
   }

   protected void init() {
      super.init();
      this.buttons.clear();
      this.children.clear();
      this.addButton(new Button(this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.yesButton, (button) -> {
         this.callback.accept(true);
      }));
      this.addButton(new Button(this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.copyButton, (button) -> {
         this.copyToClipboard();
         this.callback.accept(false);
      }));
      this.addButton(new Button(this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.noButton, (button) -> {
         this.callback.accept(false);
      }));
   }

   public void copyToClipboard() {
      this.minecraft.keyboardHandler.setClipboard(this.url);
   }

   public void render(int var1, int var2, float var3) {
      super.render(var1, var2, var3);
      if(this.showWarning) {
         this.drawCenteredString(this.font, this.warning, this.width / 2, 110, 16764108);
      }

   }
}
