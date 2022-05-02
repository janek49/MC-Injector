package com.mojang.realmsclient.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

@ClientJarOnly
public class RealmsConfirmScreen extends RealmsScreen {
   protected RealmsScreen parent;
   protected String title1;
   private final String title2;
   protected String yesButton;
   protected String noButton;
   protected int id;
   private int delayTicker;

   public RealmsConfirmScreen(RealmsScreen parent, String title1, String title2, int id) {
      this.parent = parent;
      this.title1 = title1;
      this.title2 = title2;
      this.id = id;
      this.yesButton = getLocalizedString("gui.yes");
      this.noButton = getLocalizedString("gui.no");
   }

   public void init() {
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 105, RealmsConstants.row(9), 100, 20, this.yesButton) {
         public void onPress() {
            RealmsConfirmScreen.this.parent.confirmResult(true, RealmsConfirmScreen.this.id);
         }
      });
      this.buttonsAdd(new RealmsButton(1, this.width() / 2 + 5, RealmsConstants.row(9), 100, 20, this.noButton) {
         public void onPress() {
            RealmsConfirmScreen.this.parent.confirmResult(false, RealmsConfirmScreen.this.id);
         }
      });
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.title1, this.width() / 2, RealmsConstants.row(3), 16777215);
      this.drawCenteredString(this.title2, this.width() / 2, RealmsConstants.row(5), 16777215);
      super.render(var1, var2, var3);
   }

   public void tick() {
      super.tick();
      if(--this.delayTicker == 0) {
         for(AbstractRealmsButton<?> var2 : this.buttons()) {
            var2.active(true);
         }
      }

   }
}
