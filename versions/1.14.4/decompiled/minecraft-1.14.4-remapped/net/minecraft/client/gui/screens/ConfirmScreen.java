package net.minecraft.client.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

@ClientJarOnly
public class ConfirmScreen extends Screen {
   private final Component title2;
   private final List lines;
   protected String yesButton;
   protected String noButton;
   private int delayTicker;
   protected final BooleanConsumer callback;

   public ConfirmScreen(BooleanConsumer booleanConsumer, Component var2, Component var3) {
      this(booleanConsumer, var2, var3, I18n.get("gui.yes", new Object[0]), I18n.get("gui.no", new Object[0]));
   }

   public ConfirmScreen(BooleanConsumer callback, Component var2, Component title2, String yesButton, String noButton) {
      super(var2);
      this.lines = Lists.newArrayList();
      this.callback = callback;
      this.title2 = title2;
      this.yesButton = yesButton;
      this.noButton = noButton;
   }

   public String getNarrationMessage() {
      return super.getNarrationMessage() + ". " + this.title2.getString();
   }

   protected void init() {
      super.init();
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 96, 150, 20, this.yesButton, (button) -> {
         this.callback.accept(true);
      }));
      this.addButton(new Button(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, this.noButton, (button) -> {
         this.callback.accept(false);
      }));
      this.lines.clear();
      this.lines.addAll(this.font.split(this.title2.getColoredString(), this.width - 50));
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 70, 16777215);
      int var4 = 90;

      for(String var6 : this.lines) {
         this.drawCenteredString(this.font, var6, this.width / 2, var4, 16777215);
         this.font.getClass();
         var4 += 9;
      }

      super.render(var1, var2, var3);
   }

   public void setDelay(int delay) {
      this.delayTicker = delay;

      for(AbstractWidget var3 : this.buttons) {
         var3.active = false;
      }

   }

   public void tick() {
      super.tick();
      if(--this.delayTicker == 0) {
         for(AbstractWidget var2 : this.buttons) {
            var2.active = true;
         }
      }

   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if(var1 == 256) {
         this.callback.accept(false);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }
}
