package com.mojang.realmsclient.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsMth;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class RealmsBrokenWorldScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen lastScreen;
   private final RealmsMainScreen mainScreen;
   private RealmsServer serverData;
   private final long serverId;
   private String title = getLocalizedString("mco.brokenworld.title");
   private final String message = getLocalizedString("mco.brokenworld.message.line1") + "\\n" + getLocalizedString("mco.brokenworld.message.line2");
   private int left_x;
   private int right_x;
   private final int default_button_width = 80;
   private final int default_button_offset = 5;
   private static final List playButtonIds = Arrays.asList(new Integer[]{Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3)});
   private static final List resetButtonIds = Arrays.asList(new Integer[]{Integer.valueOf(4), Integer.valueOf(5), Integer.valueOf(6)});
   private static final List downloadButtonIds = Arrays.asList(new Integer[]{Integer.valueOf(7), Integer.valueOf(8), Integer.valueOf(9)});
   private static final List downloadConfirmationIds = Arrays.asList(new Integer[]{Integer.valueOf(10), Integer.valueOf(11), Integer.valueOf(12)});
   private final List slotsThatHasBeenDownloaded = new ArrayList();
   private int animTick;

   public RealmsBrokenWorldScreen(RealmsScreen lastScreen, RealmsMainScreen mainScreen, long serverId) {
      this.lastScreen = lastScreen;
      this.mainScreen = mainScreen;
      this.serverId = serverId;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public void init() {
      this.left_x = this.width() / 2 - 150;
      this.right_x = this.width() / 2 + 190;
      this.buttonsAdd(new RealmsButton(0, this.right_x - 80 + 8, RealmsConstants.row(13) - 5, 70, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            RealmsBrokenWorldScreen.this.backButtonClicked();
         }
      });
      if(this.serverData == null) {
         this.fetchServerData(this.serverId);
      } else {
         this.addButtons();
      }

      this.setKeyboardHandlerSendRepeatsToGui(true);
   }

   public void addButtons() {
      for(Entry<Integer, RealmsWorldOptions> var2 : this.serverData.slots.entrySet()) {
         RealmsWorldOptions var3 = (RealmsWorldOptions)var2.getValue();
         boolean var4 = ((Integer)var2.getKey()).intValue() != this.serverData.activeSlot || this.serverData.worldType.equals(RealmsServer.WorldType.MINIGAME);
         RealmsButton var5;
         if(var4) {
            var5 = new RealmsBrokenWorldScreen.PlayButton(((Integer)playButtonIds.get(((Integer)var2.getKey()).intValue() - 1)).intValue(), this.getFramePositionX(((Integer)var2.getKey()).intValue()), getLocalizedString("mco.brokenworld.play"));
         } else {
            var5 = new RealmsBrokenWorldScreen.DownloadButton(((Integer)downloadButtonIds.get(((Integer)var2.getKey()).intValue() - 1)).intValue(), this.getFramePositionX(((Integer)var2.getKey()).intValue()), getLocalizedString("mco.brokenworld.download"));
         }

         if(this.slotsThatHasBeenDownloaded.contains(var2.getKey())) {
            var5.active(false);
            var5.setMessage(getLocalizedString("mco.brokenworld.downloaded"));
         }

         this.buttonsAdd(var5);
         this.buttonsAdd(new RealmsButton(((Integer)resetButtonIds.get(((Integer)var2.getKey()).intValue() - 1)).intValue(), this.getFramePositionX(((Integer)var2.getKey()).intValue()), RealmsConstants.row(10), 80, 20, getLocalizedString("mco.brokenworld.reset")) {
            public void onPress() {
               int var1 = RealmsBrokenWorldScreen.resetButtonIds.indexOf(Integer.valueOf(this.id())) + 1;
               RealmsResetWorldScreen var2 = new RealmsResetWorldScreen(RealmsBrokenWorldScreen.this, RealmsBrokenWorldScreen.this.serverData, RealmsBrokenWorldScreen.this);
               if(var1 != RealmsBrokenWorldScreen.this.serverData.activeSlot || RealmsBrokenWorldScreen.this.serverData.worldType.equals(RealmsServer.WorldType.MINIGAME)) {
                  var2.setSlot(var1);
               }

               var2.setConfirmationId(14);
               Realms.setScreen(var2);
            }
         });
      }

   }

   public void tick() {
      ++this.animTick;
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      super.render(var1, var2, var3);
      this.drawCenteredString(this.title, this.width() / 2, 17, 16777215);
      String[] vars4 = this.message.split("\\\\n");

      for(int var5 = 0; var5 < vars4.length; ++var5) {
         this.drawCenteredString(vars4[var5], this.width() / 2, RealmsConstants.row(-1) + 3 + var5 * 12, 10526880);
      }

      if(this.serverData != null) {
         for(Entry<Integer, RealmsWorldOptions> var6 : this.serverData.slots.entrySet()) {
            if(((RealmsWorldOptions)var6.getValue()).templateImage != null && ((RealmsWorldOptions)var6.getValue()).templateId != -1L) {
               this.drawSlotFrame(this.getFramePositionX(((Integer)var6.getKey()).intValue()), RealmsConstants.row(1) + 5, var1, var2, this.serverData.activeSlot == ((Integer)var6.getKey()).intValue() && !this.isMinigame(), ((RealmsWorldOptions)var6.getValue()).getSlotName(((Integer)var6.getKey()).intValue()), ((Integer)var6.getKey()).intValue(), ((RealmsWorldOptions)var6.getValue()).templateId, ((RealmsWorldOptions)var6.getValue()).templateImage, ((RealmsWorldOptions)var6.getValue()).empty);
            } else {
               this.drawSlotFrame(this.getFramePositionX(((Integer)var6.getKey()).intValue()), RealmsConstants.row(1) + 5, var1, var2, this.serverData.activeSlot == ((Integer)var6.getKey()).intValue() && !this.isMinigame(), ((RealmsWorldOptions)var6.getValue()).getSlotName(((Integer)var6.getKey()).intValue()), ((Integer)var6.getKey()).intValue(), -1L, (String)null, ((RealmsWorldOptions)var6.getValue()).empty);
            }
         }

      }
   }

   private int getFramePositionX(int i) {
      return this.left_x + (i - 1) * 110;
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if(var1 == 256) {
         this.backButtonClicked();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   private void backButtonClicked() {
      Realms.setScreen(this.lastScreen);
   }

   private void fetchServerData(final long l) {
      (new Thread() {
         public void run() {
            RealmsClient var1 = RealmsClient.createRealmsClient();

            try {
               RealmsBrokenWorldScreen.this.serverData = var1.getOwnWorld(l);
               RealmsBrokenWorldScreen.this.addButtons();
            } catch (RealmsServiceException var3) {
               RealmsBrokenWorldScreen.LOGGER.error("Couldn\'t get own world");
               Realms.setScreen(new RealmsGenericErrorScreen(var3.getMessage(), RealmsBrokenWorldScreen.this.lastScreen));
            } catch (IOException var4) {
               RealmsBrokenWorldScreen.LOGGER.error("Couldn\'t parse response getting own world");
            }

         }
      }).start();
   }

   public void confirmResult(boolean var1, int var2) {
      if(!var1) {
         Realms.setScreen(this);
      } else {
         if(var2 != 13 && var2 != 14) {
            if(downloadButtonIds.contains(Integer.valueOf(var2))) {
               this.downloadWorld(downloadButtonIds.indexOf(Integer.valueOf(var2)) + 1);
            } else if(downloadConfirmationIds.contains(Integer.valueOf(var2))) {
               this.slotsThatHasBeenDownloaded.add(Integer.valueOf(downloadConfirmationIds.indexOf(Integer.valueOf(var2)) + 1));
               this.childrenClear();
               this.addButtons();
            }
         } else {
            (new Thread() {
               public void run() {
                  RealmsClient var1 = RealmsClient.createRealmsClient();
                  if(RealmsBrokenWorldScreen.this.serverData.state.equals(RealmsServer.State.CLOSED)) {
                     RealmsTasks.OpenServerTask var2 = new RealmsTasks.OpenServerTask(RealmsBrokenWorldScreen.this.serverData, RealmsBrokenWorldScreen.this, RealmsBrokenWorldScreen.this.lastScreen, true);
                     RealmsLongRunningMcoTaskScreen var3 = new RealmsLongRunningMcoTaskScreen(RealmsBrokenWorldScreen.this, var2);
                     var3.start();
                     Realms.setScreen(var3);
                  } else {
                     try {
                        RealmsBrokenWorldScreen.this.mainScreen.newScreen().play(var1.getOwnWorld(RealmsBrokenWorldScreen.this.serverId), RealmsBrokenWorldScreen.this);
                     } catch (RealmsServiceException var4) {
                        RealmsBrokenWorldScreen.LOGGER.error("Couldn\'t get own world");
                        Realms.setScreen(RealmsBrokenWorldScreen.this.lastScreen);
                     } catch (IOException var5) {
                        RealmsBrokenWorldScreen.LOGGER.error("Couldn\'t parse response getting own world");
                        Realms.setScreen(RealmsBrokenWorldScreen.this.lastScreen);
                     }
                  }

               }
            }).start();
         }

      }
   }

   private void downloadWorld(int i) {
      RealmsClient var2 = RealmsClient.createRealmsClient();

      try {
         WorldDownload var3 = var2.download(this.serverData.id, i);
         RealmsDownloadLatestWorldScreen var4 = new RealmsDownloadLatestWorldScreen(this, var3, this.serverData.name + " (" + ((RealmsWorldOptions)this.serverData.slots.get(Integer.valueOf(i))).getSlotName(i) + ")");
         var4.setConfirmationId(((Integer)downloadConfirmationIds.get(i - 1)).intValue());
         Realms.setScreen(var4);
      } catch (RealmsServiceException var5) {
         LOGGER.error("Couldn\'t download world data");
         Realms.setScreen(new RealmsGenericErrorScreen(var5, this));
      }

   }

   private boolean isMinigame() {
      return this.serverData != null && this.serverData.worldType.equals(RealmsServer.WorldType.MINIGAME);
   }

   private void drawSlotFrame(int var1, int var2, int var3, int var4, boolean var5, String var6, int var7, long var8, String var10, boolean var11) {
      if(var11) {
         bind("realms:textures/gui/realms/empty_frame.png");
      } else if(var10 != null && var8 != -1L) {
         RealmsTextureManager.bindWorldTemplate(String.valueOf(var8), var10);
      } else if(var7 == 1) {
         bind("textures/gui/title/background/panorama_0.png");
      } else if(var7 == 2) {
         bind("textures/gui/title/background/panorama_2.png");
      } else if(var7 == 3) {
         bind("textures/gui/title/background/panorama_3.png");
      } else {
         RealmsTextureManager.bindWorldTemplate(String.valueOf(this.serverData.minigameId), this.serverData.minigameImage);
      }

      if(!var5) {
         GlStateManager.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      } else if(var5) {
         float var12 = 0.9F + 0.1F * RealmsMth.cos((float)this.animTick * 0.2F);
         GlStateManager.color4f(var12, var12, var12, 1.0F);
      }

      RealmsScreen.blit(var1 + 3, var2 + 3, 0.0F, 0.0F, 74, 74, 74, 74);
      bind("realms:textures/gui/realms/slot_frame.png");
      if(var5) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         GlStateManager.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      }

      RealmsScreen.blit(var1, var2, 0.0F, 0.0F, 80, 80, 80, 80);
      this.drawCenteredString(var6, var1 + 40, var2 + 66, 16777215);
   }

   private void switchSlot(int i) {
      RealmsTasks.SwitchSlotTask var2 = new RealmsTasks.SwitchSlotTask(this.serverData.id, i, this, 13);
      RealmsLongRunningMcoTaskScreen var3 = new RealmsLongRunningMcoTaskScreen(this.lastScreen, var2);
      var3.start();
      Realms.setScreen(var3);
   }

   @ClientJarOnly
   class DownloadButton extends RealmsButton {
      public DownloadButton(int var2, int var3, String string) {
         super(var2, var3, RealmsConstants.row(8), 80, 20, string);
      }

      public void onPress() {
         String var1 = RealmsScreen.getLocalizedString("mco.configure.world.restore.download.question.line1");
         String var2 = RealmsScreen.getLocalizedString("mco.configure.world.restore.download.question.line2");
         Realms.setScreen(new RealmsLongConfirmationScreen(RealmsBrokenWorldScreen.this, RealmsLongConfirmationScreen.Type.Info, var1, var2, true, this.id()));
      }
   }

   @ClientJarOnly
   class PlayButton extends RealmsButton {
      public PlayButton(int var2, int var3, String string) {
         super(var2, var3, RealmsConstants.row(8), 80, 20, string);
      }

      public void onPress() {
         int var1 = RealmsBrokenWorldScreen.playButtonIds.indexOf(Integer.valueOf(this.id())) + 1;
         if(((RealmsWorldOptions)RealmsBrokenWorldScreen.this.serverData.slots.get(Integer.valueOf(var1))).empty) {
            RealmsResetWorldScreen var2 = new RealmsResetWorldScreen(RealmsBrokenWorldScreen.this, RealmsBrokenWorldScreen.this.serverData, RealmsBrokenWorldScreen.this, RealmsScreen.getLocalizedString("mco.configure.world.switch.slot"), RealmsScreen.getLocalizedString("mco.configure.world.switch.slot.subtitle"), 10526880, RealmsScreen.getLocalizedString("gui.cancel"));
            var2.setSlot(var1);
            var2.setResetTitle(RealmsScreen.getLocalizedString("mco.create.world.reset.title"));
            var2.setConfirmationId(14);
            Realms.setScreen(var2);
         } else {
            RealmsBrokenWorldScreen.this.switchSlot(var1);
         }

      }
   }
}
