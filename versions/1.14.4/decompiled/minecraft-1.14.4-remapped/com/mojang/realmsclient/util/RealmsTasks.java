package com.mojang.realmsclient.util;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsBrokenWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsResourcePackScreen;
import com.mojang.realmsclient.gui.screens.RealmsTermsScreen;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsConfirmResultListener;
import net.minecraft.realms.RealmsConnect;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class RealmsTasks {
   private static final Logger LOGGER = LogManager.getLogger();

   private static void pause(int i) {
      try {
         Thread.sleep((long)(i * 1000));
      } catch (InterruptedException var2) {
         LOGGER.error("", var2);
      }

   }

   @ClientJarOnly
   public static class CloseServerTask extends LongRunningTask {
      private final RealmsServer serverData;
      private final RealmsConfigureWorldScreen configureScreen;

      public CloseServerTask(RealmsServer serverData, RealmsConfigureWorldScreen configureScreen) {
         this.serverData = serverData;
         this.configureScreen = configureScreen;
      }

      public void run() {
         this.setTitle(RealmsScreen.getLocalizedString("mco.configure.world.closing"));
         RealmsClient var1 = RealmsClient.createRealmsClient();

         for(int var2 = 0; var2 < 25; ++var2) {
            if(this.aborted()) {
               return;
            }

            try {
               boolean var3 = var1.close(this.serverData.id).booleanValue();
               if(var3) {
                  this.configureScreen.stateChanged();
                  this.serverData.state = RealmsServer.State.CLOSED;
                  Realms.setScreen(this.configureScreen);
                  break;
               }
            } catch (RetryCallException var4) {
               if(this.aborted()) {
                  return;
               }

               RealmsTasks.pause(var4.delaySeconds);
            } catch (Exception var5) {
               if(this.aborted()) {
                  return;
               }

               RealmsTasks.LOGGER.error("Failed to close server", var5);
               this.error("Failed to close the server");
            }
         }

      }
   }

   @ClientJarOnly
   public static class DownloadTask extends LongRunningTask {
      private final long worldId;
      private final int slot;
      private final RealmsScreen lastScreen;
      private final String downloadName;

      public DownloadTask(long worldId, int slot, String downloadName, RealmsScreen lastScreen) {
         this.worldId = worldId;
         this.slot = slot;
         this.lastScreen = lastScreen;
         this.downloadName = downloadName;
      }

      public void run() {
         this.setTitle(RealmsScreen.getLocalizedString("mco.download.preparing"));
         RealmsClient var1 = RealmsClient.createRealmsClient();
         int var2 = 0;

         while(var2 < 25) {
            try {
               if(this.aborted()) {
                  return;
               }

               WorldDownload var3 = var1.download(this.worldId, this.slot);
               RealmsTasks.pause(1);
               if(this.aborted()) {
                  return;
               }

               Realms.setScreen(new RealmsDownloadLatestWorldScreen(this.lastScreen, var3, this.downloadName));
               return;
            } catch (RetryCallException var4) {
               if(this.aborted()) {
                  return;
               }

               RealmsTasks.pause(var4.delaySeconds);
               ++var2;
            } catch (RealmsServiceException var5) {
               if(this.aborted()) {
                  return;
               }

               RealmsTasks.LOGGER.error("Couldn\'t download world data");
               Realms.setScreen(new RealmsGenericErrorScreen(var5, this.lastScreen));
               return;
            } catch (Exception var6) {
               if(this.aborted()) {
                  return;
               }

               RealmsTasks.LOGGER.error("Couldn\'t download world data", var6);
               this.error(var6.getLocalizedMessage());
               return;
            }
         }

      }
   }

   @ClientJarOnly
   public static class OpenServerTask extends LongRunningTask {
      private final RealmsServer serverData;
      private final RealmsScreen returnScreen;
      private final boolean join;
      private final RealmsScreen mainScreen;

      public OpenServerTask(RealmsServer serverData, RealmsScreen returnScreen, RealmsScreen mainScreen, boolean join) {
         this.serverData = serverData;
         this.returnScreen = returnScreen;
         this.join = join;
         this.mainScreen = mainScreen;
      }

      public void run() {
         this.setTitle(RealmsScreen.getLocalizedString("mco.configure.world.opening"));
         RealmsClient var1 = RealmsClient.createRealmsClient();

         for(int var2 = 0; var2 < 25; ++var2) {
            if(this.aborted()) {
               return;
            }

            try {
               boolean var3 = var1.open(this.serverData.id).booleanValue();
               if(var3) {
                  if(this.returnScreen instanceof RealmsConfigureWorldScreen) {
                     ((RealmsConfigureWorldScreen)this.returnScreen).stateChanged();
                  }

                  this.serverData.state = RealmsServer.State.OPEN;
                  if(this.join) {
                     ((RealmsMainScreen)this.mainScreen).play(this.serverData, this.returnScreen);
                  } else {
                     Realms.setScreen(this.returnScreen);
                  }
                  break;
               }
            } catch (RetryCallException var4) {
               if(this.aborted()) {
                  return;
               }

               RealmsTasks.pause(var4.delaySeconds);
            } catch (Exception var5) {
               if(this.aborted()) {
                  return;
               }

               RealmsTasks.LOGGER.error("Failed to open server", var5);
               this.error("Failed to open the server");
            }
         }

      }
   }

   @ClientJarOnly
   public static class RealmsConnectTask extends LongRunningTask {
      private final RealmsConnect realmsConnect;
      private final RealmsServerAddress a;

      public RealmsConnectTask(RealmsScreen realmsScreen, RealmsServerAddress a) {
         this.a = a;
         this.realmsConnect = new RealmsConnect(realmsScreen);
      }

      public void run() {
         this.setTitle(RealmsScreen.getLocalizedString("mco.connect.connecting"));
         net.minecraft.realms.RealmsServerAddress var1 = net.minecraft.realms.RealmsServerAddress.parseString(this.a.address);
         this.realmsConnect.connect(var1.getHost(), var1.getPort());
      }

      public void abortTask() {
         this.realmsConnect.abort();
         Realms.clearResourcePack();
      }

      public void tick() {
         this.realmsConnect.tick();
      }
   }

   @ClientJarOnly
   public static class RealmsGetServerDetailsTask extends LongRunningTask {
      private final RealmsServer server;
      private final RealmsScreen lastScreen;
      private final RealmsMainScreen mainScreen;
      private final ReentrantLock connectLock;

      public RealmsGetServerDetailsTask(RealmsMainScreen mainScreen, RealmsScreen lastScreen, RealmsServer server, ReentrantLock connectLock) {
         this.lastScreen = lastScreen;
         this.mainScreen = mainScreen;
         this.server = server;
         this.connectLock = connectLock;
      }

      public void run() {
         this.setTitle(RealmsScreen.getLocalizedString("mco.connect.connecting"));
         RealmsClient var1 = RealmsClient.createRealmsClient();
         boolean var2 = false;
         boolean var3 = false;
         int var4 = 5;
         RealmsServerAddress var5 = null;
         boolean var6 = false;
         boolean var7 = false;

         for(int var8 = 0; var8 < 40 && !this.aborted(); ++var8) {
            try {
               var5 = var1.join(this.server.id);
               var2 = true;
            } catch (RetryCallException var10) {
               var4 = var10.delaySeconds;
            } catch (RealmsServiceException var11) {
               if(var11.errorCode == 6002) {
                  var6 = true;
               } else if(var11.errorCode == 6006) {
                  var7 = true;
               } else {
                  var3 = true;
                  this.error(var11.toString());
                  RealmsTasks.LOGGER.error("Couldn\'t connect to world", var11);
               }
               break;
            } catch (IOException var12) {
               RealmsTasks.LOGGER.error("Couldn\'t parse response connecting to world", var12);
            } catch (Exception var13) {
               var3 = true;
               RealmsTasks.LOGGER.error("Couldn\'t connect to world", var13);
               this.error(var13.getLocalizedMessage());
               break;
            }

            if(var2) {
               break;
            }

            this.sleep(var4);
         }

         if(var6) {
            Realms.setScreen(new RealmsTermsScreen(this.lastScreen, this.mainScreen, this.server));
         } else if(var7) {
            if(this.server.ownerUUID.equals(Realms.getUUID())) {
               RealmsBrokenWorldScreen var8 = new RealmsBrokenWorldScreen(this.lastScreen, this.mainScreen, this.server.id);
               if(this.server.worldType.equals(RealmsServer.WorldType.MINIGAME)) {
                  var8.setTitle(RealmsScreen.getLocalizedString("mco.brokenworld.minigame.title"));
               }

               Realms.setScreen(var8);
            } else {
               Realms.setScreen(new RealmsGenericErrorScreen(RealmsScreen.getLocalizedString("mco.brokenworld.nonowner.title"), RealmsScreen.getLocalizedString("mco.brokenworld.nonowner.error"), this.lastScreen));
            }
         } else if(!this.aborted() && !var3) {
            if(var2) {
               if(var5.resourcePackUrl != null && var5.resourcePackHash != null) {
                  String var8 = RealmsScreen.getLocalizedString("mco.configure.world.resourcepack.question.line1");
                  String var9 = RealmsScreen.getLocalizedString("mco.configure.world.resourcepack.question.line2");
                  Realms.setScreen(new RealmsLongConfirmationScreen(new RealmsResourcePackScreen(this.lastScreen, var5, this.connectLock), RealmsLongConfirmationScreen.Type.Info, var8, var9, true, 100));
               } else {
                  RealmsLongRunningMcoTaskScreen var8 = new RealmsLongRunningMcoTaskScreen(this.lastScreen, new RealmsTasks.RealmsConnectTask(this.lastScreen, var5));
                  var8.start();
                  Realms.setScreen(var8);
               }
            } else {
               this.error(RealmsScreen.getLocalizedString("mco.errorMessage.connectionFailure"));
            }
         }

      }

      private void sleep(int i) {
         try {
            Thread.sleep((long)(i * 1000));
         } catch (InterruptedException var3) {
            RealmsTasks.LOGGER.warn(var3.getLocalizedMessage());
         }

      }
   }

   @ClientJarOnly
   public static class ResettingWorldTask extends LongRunningTask {
      private final String seed;
      private final WorldTemplate worldTemplate;
      private final int levelType;
      private final boolean generateStructures;
      private final long serverId;
      private final RealmsScreen lastScreen;
      private int confirmationId = -1;
      private String title = RealmsScreen.getLocalizedString("mco.reset.world.resetting.screen.title");

      public ResettingWorldTask(long serverId, RealmsScreen lastScreen, WorldTemplate worldTemplate) {
         this.seed = null;
         this.worldTemplate = worldTemplate;
         this.levelType = -1;
         this.generateStructures = true;
         this.serverId = serverId;
         this.lastScreen = lastScreen;
      }

      public ResettingWorldTask(long serverId, RealmsScreen lastScreen, String seed, int levelType, boolean generateStructures) {
         this.seed = seed;
         this.worldTemplate = null;
         this.levelType = levelType;
         this.generateStructures = generateStructures;
         this.serverId = serverId;
         this.lastScreen = lastScreen;
      }

      public void setConfirmationId(int confirmationId) {
         this.confirmationId = confirmationId;
      }

      public void setResetTitle(String resetTitle) {
         this.title = resetTitle;
      }

      public void run() {
         RealmsClient var1 = RealmsClient.createRealmsClient();
         this.setTitle(this.title);
         int var2 = 0;

         while(var2 < 25) {
            try {
               if(this.aborted()) {
                  return;
               }

               if(this.worldTemplate != null) {
                  var1.resetWorldWithTemplate(this.serverId, this.worldTemplate.id);
               } else {
                  var1.resetWorldWithSeed(this.serverId, this.seed, Integer.valueOf(this.levelType), this.generateStructures);
               }

               if(this.aborted()) {
                  return;
               }

               if(this.confirmationId == -1) {
                  Realms.setScreen(this.lastScreen);
               } else {
                  this.lastScreen.confirmResult(true, this.confirmationId);
               }

               return;
            } catch (RetryCallException var4) {
               if(this.aborted()) {
                  return;
               }

               RealmsTasks.pause(var4.delaySeconds);
               ++var2;
            } catch (Exception var5) {
               if(this.aborted()) {
                  return;
               }

               RealmsTasks.LOGGER.error("Couldn\'t reset world");
               this.error(var5.toString());
               return;
            }
         }

      }
   }

   @ClientJarOnly
   public static class RestoreTask extends LongRunningTask {
      private final Backup backup;
      private final long worldId;
      private final RealmsConfigureWorldScreen lastScreen;

      public RestoreTask(Backup backup, long worldId, RealmsConfigureWorldScreen lastScreen) {
         this.backup = backup;
         this.worldId = worldId;
         this.lastScreen = lastScreen;
      }

      public void run() {
         this.setTitle(RealmsScreen.getLocalizedString("mco.backup.restoring"));
         RealmsClient var1 = RealmsClient.createRealmsClient();
         int var2 = 0;

         while(var2 < 25) {
            try {
               if(this.aborted()) {
                  return;
               }

               var1.restoreWorld(this.worldId, this.backup.backupId);
               RealmsTasks.pause(1);
               if(this.aborted()) {
                  return;
               }

               Realms.setScreen(this.lastScreen.getNewScreen());
               return;
            } catch (RetryCallException var4) {
               if(this.aborted()) {
                  return;
               }

               RealmsTasks.pause(var4.delaySeconds);
               ++var2;
            } catch (RealmsServiceException var5) {
               if(this.aborted()) {
                  return;
               }

               RealmsTasks.LOGGER.error("Couldn\'t restore backup", var5);
               Realms.setScreen(new RealmsGenericErrorScreen(var5, this.lastScreen));
               return;
            } catch (Exception var6) {
               if(this.aborted()) {
                  return;
               }

               RealmsTasks.LOGGER.error("Couldn\'t restore backup", var6);
               this.error(var6.getLocalizedMessage());
               return;
            }
         }

      }
   }

   @ClientJarOnly
   public static class SwitchMinigameTask extends LongRunningTask {
      private final long worldId;
      private final WorldTemplate worldTemplate;
      private final RealmsConfigureWorldScreen lastScreen;

      public SwitchMinigameTask(long worldId, WorldTemplate worldTemplate, RealmsConfigureWorldScreen lastScreen) {
         this.worldId = worldId;
         this.worldTemplate = worldTemplate;
         this.lastScreen = lastScreen;
      }

      public void run() {
         RealmsClient var1 = RealmsClient.createRealmsClient();
         String var2 = RealmsScreen.getLocalizedString("mco.minigame.world.starting.screen.title");
         this.setTitle(var2);

         for(int var3 = 0; var3 < 25; ++var3) {
            try {
               if(this.aborted()) {
                  return;
               }

               if(var1.putIntoMinigameMode(this.worldId, this.worldTemplate.id).booleanValue()) {
                  Realms.setScreen(this.lastScreen);
                  break;
               }
            } catch (RetryCallException var5) {
               if(this.aborted()) {
                  return;
               }

               RealmsTasks.pause(var5.delaySeconds);
            } catch (Exception var6) {
               if(this.aborted()) {
                  return;
               }

               RealmsTasks.LOGGER.error("Couldn\'t start mini game!");
               this.error(var6.toString());
            }
         }

      }
   }

   @ClientJarOnly
   public static class SwitchSlotTask extends LongRunningTask {
      private final long worldId;
      private final int slot;
      private final RealmsConfirmResultListener listener;
      private final int confirmId;

      public SwitchSlotTask(long worldId, int slot, RealmsConfirmResultListener listener, int confirmId) {
         this.worldId = worldId;
         this.slot = slot;
         this.listener = listener;
         this.confirmId = confirmId;
      }

      public void run() {
         RealmsClient var1 = RealmsClient.createRealmsClient();
         String var2 = RealmsScreen.getLocalizedString("mco.minigame.world.slot.screen.title");
         this.setTitle(var2);

         for(int var3 = 0; var3 < 25; ++var3) {
            try {
               if(this.aborted()) {
                  return;
               }

               if(var1.switchSlot(this.worldId, this.slot)) {
                  this.listener.confirmResult(true, this.confirmId);
                  break;
               }
            } catch (RetryCallException var5) {
               if(this.aborted()) {
                  return;
               }

               RealmsTasks.pause(var5.delaySeconds);
            } catch (Exception var6) {
               if(this.aborted()) {
                  return;
               }

               RealmsTasks.LOGGER.error("Couldn\'t switch world!");
               this.error(var6.toString());
            }
         }

      }
   }

   @ClientJarOnly
   public static class TrialCreationTask extends LongRunningTask {
      private final String name;
      private final String motd;
      private final RealmsMainScreen lastScreen;

      public TrialCreationTask(String name, String motd, RealmsMainScreen lastScreen) {
         this.name = name;
         this.motd = motd;
         this.lastScreen = lastScreen;
      }

      public void run() {
         String var1 = RealmsScreen.getLocalizedString("mco.create.world.wait");
         this.setTitle(var1);
         RealmsClient var2 = RealmsClient.createRealmsClient();

         try {
            RealmsServer var3 = var2.createTrial(this.name, this.motd);
            if(var3 != null) {
               this.lastScreen.setCreatedTrial(true);
               this.lastScreen.closePopup();
               RealmsResetWorldScreen var4 = new RealmsResetWorldScreen(this.lastScreen, var3, this.lastScreen.newScreen(), RealmsScreen.getLocalizedString("mco.selectServer.create"), RealmsScreen.getLocalizedString("mco.create.world.subtitle"), 10526880, RealmsScreen.getLocalizedString("mco.create.world.skip"));
               var4.setResetTitle(RealmsScreen.getLocalizedString("mco.create.world.reset.title"));
               Realms.setScreen(var4);
            } else {
               this.error(RealmsScreen.getLocalizedString("mco.trial.unavailable"));
            }
         } catch (RealmsServiceException var5) {
            RealmsTasks.LOGGER.error("Couldn\'t create trial");
            this.error(var5.toString());
         } catch (UnsupportedEncodingException var6) {
            RealmsTasks.LOGGER.error("Couldn\'t create trial");
            this.error(var6.getLocalizedMessage());
         } catch (IOException var7) {
            RealmsTasks.LOGGER.error("Could not parse response creating trial");
            this.error(var7.getLocalizedMessage());
         } catch (Exception var8) {
            RealmsTasks.LOGGER.error("Could not create trial");
            this.error(var8.getLocalizedMessage());
         }

      }
   }

   @ClientJarOnly
   public static class WorldCreationTask extends LongRunningTask {
      private final String name;
      private final String motd;
      private final long worldId;
      private final RealmsScreen lastScreen;

      public WorldCreationTask(long worldId, String name, String motd, RealmsScreen lastScreen) {
         this.worldId = worldId;
         this.name = name;
         this.motd = motd;
         this.lastScreen = lastScreen;
      }

      public void run() {
         String var1 = RealmsScreen.getLocalizedString("mco.create.world.wait");
         this.setTitle(var1);
         RealmsClient var2 = RealmsClient.createRealmsClient();

         try {
            var2.initializeWorld(this.worldId, this.name, this.motd);
            Realms.setScreen(this.lastScreen);
         } catch (RealmsServiceException var4) {
            RealmsTasks.LOGGER.error("Couldn\'t create world");
            this.error(var4.toString());
         } catch (UnsupportedEncodingException var5) {
            RealmsTasks.LOGGER.error("Couldn\'t create world");
            this.error(var5.getLocalizedMessage());
         } catch (IOException var6) {
            RealmsTasks.LOGGER.error("Could not parse response creating world");
            this.error(var6.getLocalizedMessage());
         } catch (Exception var7) {
            RealmsTasks.LOGGER.error("Could not create world");
            this.error(var7.getLocalizedMessage());
         }

      }
   }
}
