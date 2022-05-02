package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

@ClientJarOnly
public class DisconnectedRealmsScreen extends RealmsScreen {
   private final String title;
   private final Component reason;
   private List lines;
   private final RealmsScreen parent;
   private int textHeight;

   public DisconnectedRealmsScreen(RealmsScreen parent, String string, Component reason) {
      this.parent = parent;
      this.title = getLocalizedString(string);
      this.reason = reason;
   }

   public void init() {
      Realms.setConnectedToRealms(false);
      Realms.clearResourcePack();
      Realms.narrateNow(this.title + ": " + this.reason.getString());
      this.lines = this.fontSplit(this.reason.getColoredString(), this.width() - 50);
      this.textHeight = this.lines.size() * this.fontLineHeight();
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, this.height() / 2 + this.textHeight / 2 + this.fontLineHeight(), getLocalizedString("gui.back")) {
         public void onPress() {
            Realms.setScreen(DisconnectedRealmsScreen.this.parent);
         }
      });
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if(var1 == 256) {
         Realms.setScreen(this.parent);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.title, this.width() / 2, this.height() / 2 - this.textHeight / 2 - this.fontLineHeight() * 2, 11184810);
      int var4 = this.height() / 2 - this.textHeight / 2;
      if(this.lines != null) {
         for(String var6 : this.lines) {
            this.drawCenteredString(var6, this.width() / 2, var4, 16777215);
            var4 += this.fontLineHeight();
         }
      }

      super.render(var1, var2, var3);
   }
}
