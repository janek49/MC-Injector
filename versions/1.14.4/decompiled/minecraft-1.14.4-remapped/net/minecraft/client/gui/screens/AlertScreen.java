package net.minecraft.client.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

@ClientJarOnly
public class AlertScreen extends Screen {
   private final Runnable callback;
   protected final Component text;
   private final List lines;
   protected final String okButton;
   private int delayTicker;

   public AlertScreen(Runnable runnable, Component var2, Component var3) {
      this(runnable, var2, var3, "gui.back");
   }

   public AlertScreen(Runnable callback, Component var2, Component text, String string) {
      super(var2);
      this.lines = Lists.newArrayList();
      this.callback = callback;
      this.text = text;
      this.okButton = I18n.get(string, new Object[0]);
   }

   protected void init() {
      super.init();
      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, this.okButton, (button) -> {
         this.callback.run();
      }));
      this.lines.clear();
      this.lines.addAll(this.font.split(this.text.getColoredString(), this.width - 50));
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

   public void tick() {
      super.tick();
      if(--this.delayTicker == 0) {
         for(AbstractWidget var2 : this.buttons) {
            var2.active = true;
         }
      }

   }
}
