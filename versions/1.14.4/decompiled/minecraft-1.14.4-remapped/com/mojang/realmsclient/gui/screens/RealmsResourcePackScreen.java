package com.mojang.realmsclient.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.RealmsTasks;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class RealmsResourcePackScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen lastScreen;
   private final RealmsServerAddress serverAddress;
   private final ReentrantLock connectLock;

   public RealmsResourcePackScreen(RealmsScreen lastScreen, RealmsServerAddress serverAddress, ReentrantLock connectLock) {
      this.lastScreen = lastScreen;
      this.serverAddress = serverAddress;
      this.connectLock = connectLock;
   }

   public void confirmResult(boolean var1, int var2) {
      try {
         if(!var1) {
            Realms.setScreen(this.lastScreen);
         } else {
            try {
               Realms.downloadResourcePack(this.serverAddress.resourcePackUrl, this.serverAddress.resourcePackHash).thenRun(() -> {
                  RealmsLongRunningMcoTaskScreen var1 = new RealmsLongRunningMcoTaskScreen(this.lastScreen, new RealmsTasks.RealmsConnectTask(this.lastScreen, this.serverAddress));
                  var1.start();
                  Realms.setScreen(var1);
               }).exceptionally((throwable) -> {
                  Realms.clearResourcePack();
                  LOGGER.error(throwable);
                  Realms.setScreen(new RealmsGenericErrorScreen("Failed to download resource pack!", this.lastScreen));
                  return null;
               });
            } catch (Exception var7) {
               Realms.clearResourcePack();
               LOGGER.error(var7);
               Realms.setScreen(new RealmsGenericErrorScreen("Failed to download resource pack!", this.lastScreen));
            }
         }
      } finally {
         if(this.connectLock != null && this.connectLock.isHeldByCurrentThread()) {
            this.connectLock.unlock();
         }

      }

   }
}
