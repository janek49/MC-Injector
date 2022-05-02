package com.mojang.realmsclient.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

@ClientJarOnly
public class RealmsGenericErrorScreen extends RealmsScreen {
   private final RealmsScreen nextScreen;
   private String line1;
   private String line2;

   public RealmsGenericErrorScreen(RealmsServiceException realmsServiceException, RealmsScreen nextScreen) {
      this.nextScreen = nextScreen;
      this.errorMessage(realmsServiceException);
   }

   public RealmsGenericErrorScreen(String string, RealmsScreen nextScreen) {
      this.nextScreen = nextScreen;
      this.errorMessage(string);
   }

   public RealmsGenericErrorScreen(String var1, String var2, RealmsScreen nextScreen) {
      this.nextScreen = nextScreen;
      this.errorMessage(var1, var2);
   }

   private void errorMessage(RealmsServiceException realmsServiceException) {
      if(realmsServiceException.errorCode == -1) {
         this.line1 = "An error occurred (" + realmsServiceException.httpResultCode + "):";
         this.line2 = realmsServiceException.httpResponseContent;
      } else {
         this.line1 = "Realms (" + realmsServiceException.errorCode + "):";
         String var2 = "mco.errorMessage." + realmsServiceException.errorCode;
         String var3 = getLocalizedString(var2);
         this.line2 = var3.equals(var2)?realmsServiceException.errorMsg:var3;
      }

   }

   private void errorMessage(String line2) {
      this.line1 = "An error occurred: ";
      this.line2 = line2;
   }

   private void errorMessage(String line1, String line2) {
      this.line1 = line1;
      this.line2 = line2;
   }

   public void init() {
      Realms.narrateNow(this.line1 + ": " + this.line2);
      this.buttonsAdd(new RealmsButton(10, this.width() / 2 - 100, this.height() - 52, 200, 20, "Ok") {
         public void onPress() {
            Realms.setScreen(RealmsGenericErrorScreen.this.nextScreen);
         }
      });
   }

   public void tick() {
      super.tick();
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.line1, this.width() / 2, 80, 16777215);
      this.drawCenteredString(this.line2, this.width() / 2, 100, 16711680);
      super.render(var1, var2, var3);
   }
}
