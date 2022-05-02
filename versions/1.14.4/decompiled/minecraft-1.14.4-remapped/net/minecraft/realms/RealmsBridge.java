package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsScreenProxy;

@ClientJarOnly
public class RealmsBridge extends RealmsScreen {
   private Screen previousScreen;

   public void switchToRealms(Screen previousScreen) {
      this.previousScreen = previousScreen;
      Realms.setScreen(new RealmsMainScreen(this));
   }

   @Nullable
   public RealmsScreenProxy getNotificationScreen(Screen previousScreen) {
      this.previousScreen = previousScreen;
      return (new RealmsNotificationsScreen(this)).getProxy();
   }

   public void init() {
      Minecraft.getInstance().setScreen(this.previousScreen);
   }
}
