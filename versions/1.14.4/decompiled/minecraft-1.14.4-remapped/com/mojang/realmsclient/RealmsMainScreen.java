package com.mojang.realmsclient;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.KeyCombo;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerPlayerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RegionPingResult;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsCreateRealmScreen;
import com.mojang.realmsclient.gui.screens.RealmsCreateTrialScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsParentalConsentScreen;
import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
import com.mojang.realmsclient.util.RealmsPersistence;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.RealmsUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsMth;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class RealmsMainScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static boolean overrideConfigure;
   private final RateLimiter inviteNarrationLimiter;
   private boolean dontSetConnectedToRealms;
   private static final String[] IMAGES_LOCATION = new String[]{"realms:textures/gui/realms/images/sand_castle.png", "realms:textures/gui/realms/images/factory_floor.png", "realms:textures/gui/realms/images/escher_tunnel.png", "realms:textures/gui/realms/images/tree_houses.png", "realms:textures/gui/realms/images/balloon_trip.png", "realms:textures/gui/realms/images/halloween_woods.png", "realms:textures/gui/realms/images/flower_mountain.png", "realms:textures/gui/realms/images/dornenstein_estate.png", "realms:textures/gui/realms/images/desert.png", "realms:textures/gui/realms/images/gray.png", "realms:textures/gui/realms/images/imperium.png", "realms:textures/gui/realms/images/ludo.png", "realms:textures/gui/realms/images/makersspleef.png", "realms:textures/gui/realms/images/negentropy.png", "realms:textures/gui/realms/images/pumpkin_party.png", "realms:textures/gui/realms/images/sparrenhout.png", "realms:textures/gui/realms/images/spindlewood.png"};
   private static final RealmsDataFetcher realmsDataFetcher = new RealmsDataFetcher();
   private static int lastScrollYPosition = -1;
   private final RealmsScreen lastScreen;
   private volatile RealmsMainScreen.RealmSelectionList realmSelectionList;
   private long selectedServerId = -1L;
   private RealmsButton playButton;
   private RealmsButton backButton;
   private RealmsButton renewButton;
   private RealmsButton configureButton;
   private RealmsButton leaveButton;
   private String toolTip;
   private List realmsServers = Lists.newArrayList();
   private volatile int numberOfPendingInvites;
   private int animTick;
   private static volatile boolean hasParentalConsent;
   private static volatile boolean checkedParentalConsent;
   private static volatile boolean checkedClientCompatability;
   private boolean hasFetchedServers;
   private boolean popupOpenedByUser;
   private boolean justClosedPopup;
   private volatile boolean trialsAvailable;
   private volatile boolean createdTrial;
   private volatile boolean showingPopup;
   private volatile boolean hasUnreadNews;
   private volatile String newsLink;
   private int carouselIndex;
   private int carouselTick;
   private boolean hasSwitchedCarouselImage;
   private static RealmsScreen realmsGenericErrorScreen;
   private static boolean regionsPinged;
   private List keyCombos;
   private int clicks;
   private ReentrantLock connectLock = new ReentrantLock();
   private boolean expiredHover;
   private RealmsMainScreen.ShowPopupButton showPopupButton;
   private RealmsMainScreen.PendingInvitesButton pendingInvitesButton;
   private RealmsMainScreen.NewsButton newsButton;
   private RealmsButton createTrialButton;
   private RealmsButton buyARealmButton;
   private RealmsButton closeButton;

   public RealmsMainScreen(RealmsScreen lastScreen) {
      this.lastScreen = lastScreen;
      this.inviteNarrationLimiter = RateLimiter.create(0.01666666753590107D);
   }

   public boolean shouldShowMessageInList() {
      if(this.hasParentalConsent() && this.hasFetchedServers) {
         if(this.trialsAvailable && !this.createdTrial) {
            return true;
         } else {
            for(RealmsServer var2 : this.realmsServers) {
               if(var2.ownerUUID.equals(Realms.getUUID())) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public boolean shouldShowPopup() {
      return this.hasParentalConsent() && this.hasFetchedServers?(this.popupOpenedByUser?true:(this.trialsAvailable && !this.createdTrial && this.realmsServers.isEmpty()?true:this.realmsServers.isEmpty())):false;
   }

   public void init() {
      this.keyCombos = Lists.newArrayList(new KeyCombo[]{new KeyCombo(new char[]{'3', '2', '1', '4', '5', '6'}, () -> {
         overrideConfigure = !overrideConfigure;
      }), new KeyCombo(new char[]{'9', '8', '7', '1', '2', '3'}, () -> {
         if(RealmsClient.currentEnvironment.equals(RealmsClient.Environment.STAGE)) {
            this.switchToProd();
         } else {
            this.switchToStage();
         }

      }), new KeyCombo(new char[]{'9', '8', '7', '4', '5', '6'}, () -> {
         if(RealmsClient.currentEnvironment.equals(RealmsClient.Environment.LOCAL)) {
            this.switchToProd();
         } else {
            this.switchToLocal();
         }

      })});
      if(realmsGenericErrorScreen != null) {
         Realms.setScreen(realmsGenericErrorScreen);
      } else {
         this.connectLock = new ReentrantLock();
         if(checkedClientCompatability && !this.hasParentalConsent()) {
            this.checkParentalConsent();
         }

         this.checkClientCompatability();
         this.checkUnreadNews();
         if(!this.dontSetConnectedToRealms) {
            Realms.setConnectedToRealms(false);
         }

         this.setKeyboardHandlerSendRepeatsToGui(true);
         if(this.hasParentalConsent()) {
            realmsDataFetcher.forceUpdate();
         }

         this.showingPopup = false;
         this.postInit();
      }
   }

   private boolean hasParentalConsent() {
      return checkedParentalConsent && hasParentalConsent;
   }

   public void addButtons() {
      this.buttonsAdd(this.configureButton = new RealmsButton(1, this.width() / 2 - 190, this.height() - 32, 90, 20, getLocalizedString("mco.selectServer.configure")) {
         public void onPress() {
            RealmsMainScreen.this.configureClicked(RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId));
         }
      });
      this.buttonsAdd(this.playButton = new RealmsButton(3, this.width() / 2 - 93, this.height() - 32, 90, 20, getLocalizedString("mco.selectServer.play")) {
         public void onPress() {
            RealmsMainScreen.this.onPlay();
         }
      });
      this.buttonsAdd(this.backButton = new RealmsButton(2, this.width() / 2 + 4, this.height() - 32, 90, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            if(!RealmsMainScreen.this.justClosedPopup) {
               Realms.setScreen(RealmsMainScreen.this.lastScreen);
            }

         }
      });
      this.buttonsAdd(this.renewButton = new RealmsButton(0, this.width() / 2 + 100, this.height() - 32, 90, 20, getLocalizedString("mco.selectServer.expiredRenew")) {
         public void onPress() {
            RealmsMainScreen.this.onRenew();
         }
      });
      this.buttonsAdd(this.leaveButton = new RealmsButton(7, this.width() / 2 - 202, this.height() - 32, 90, 20, getLocalizedString("mco.selectServer.leave")) {
         public void onPress() {
            RealmsMainScreen.this.leaveClicked(RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId));
         }
      });
      this.buttonsAdd(this.pendingInvitesButton = new RealmsMainScreen.PendingInvitesButton());
      this.buttonsAdd(this.newsButton = new RealmsMainScreen.NewsButton());
      this.buttonsAdd(this.showPopupButton = new RealmsMainScreen.ShowPopupButton());
      this.buttonsAdd(this.closeButton = new RealmsMainScreen.CloseButton());
      this.buttonsAdd(this.createTrialButton = new RealmsButton(6, this.width() / 2 + 52, this.popupY0() + 137 - 20, 98, 20, getLocalizedString("mco.selectServer.trial")) {
         public void onPress() {
            RealmsMainScreen.this.createTrial();
         }
      });
      this.buttonsAdd(this.buyARealmButton = new RealmsButton(5, this.width() / 2 + 52, this.popupY0() + 160 - 20, 98, 20, getLocalizedString("mco.selectServer.buy")) {
         public void onPress() {
            RealmsUtil.browseTo("https://minecraft.net/realms");
         }
      });
      RealmsServer var1 = this.findServer(this.selectedServerId);
      this.updateButtonStates(var1);
   }

   private void updateButtonStates(RealmsServer realmsServer) {
      this.playButton.active(this.shouldPlayButtonBeActive(realmsServer) && !this.shouldShowPopup());
      this.renewButton.setVisible(this.shouldRenewButtonBeActive(realmsServer));
      this.configureButton.setVisible(this.shouldConfigureButtonBeVisible(realmsServer));
      this.leaveButton.setVisible(this.shouldLeaveButtonBeVisible(realmsServer));
      boolean var2 = this.shouldShowPopup() && this.trialsAvailable && !this.createdTrial;
      this.createTrialButton.setVisible(var2);
      this.createTrialButton.active(var2);
      this.buyARealmButton.setVisible(this.shouldShowPopup());
      this.closeButton.setVisible(this.shouldShowPopup() && this.popupOpenedByUser);
      this.renewButton.active(!this.shouldShowPopup());
      this.configureButton.active(!this.shouldShowPopup());
      this.leaveButton.active(!this.shouldShowPopup());
      this.newsButton.active(true);
      this.pendingInvitesButton.active(true);
      this.backButton.active(true);
      this.showPopupButton.active(!this.shouldShowPopup());
   }

   private boolean shouldShowPopupButton() {
      return (!this.shouldShowPopup() || this.popupOpenedByUser) && this.hasParentalConsent() && this.hasFetchedServers;
   }

   private boolean shouldPlayButtonBeActive(RealmsServer realmsServer) {
      return realmsServer != null && !realmsServer.expired && realmsServer.state == RealmsServer.State.OPEN;
   }

   private boolean shouldRenewButtonBeActive(RealmsServer realmsServer) {
      return realmsServer != null && realmsServer.expired && this.isSelfOwnedServer(realmsServer);
   }

   private boolean shouldConfigureButtonBeVisible(RealmsServer realmsServer) {
      return realmsServer != null && this.isSelfOwnedServer(realmsServer);
   }

   private boolean shouldLeaveButtonBeVisible(RealmsServer realmsServer) {
      return realmsServer != null && !this.isSelfOwnedServer(realmsServer);
   }

   public void postInit() {
      if(this.hasParentalConsent() && this.hasFetchedServers) {
         this.addButtons();
      }

      this.realmSelectionList = new RealmsMainScreen.RealmSelectionList();
      if(lastScrollYPosition != -1) {
         this.realmSelectionList.scroll(lastScrollYPosition);
      }

      this.addWidget(this.realmSelectionList);
      this.focusOn(this.realmSelectionList);
   }

   public void tick() {
      this.tickButtons();
      this.justClosedPopup = false;
      ++this.animTick;
      --this.clicks;
      if(this.clicks < 0) {
         this.clicks = 0;
      }

      if(this.hasParentalConsent()) {
         realmsDataFetcher.init();
         if(realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.SERVER_LIST)) {
            List<RealmsServer> var1 = realmsDataFetcher.getServers();
            this.realmSelectionList.clear();
            boolean var2 = !this.hasFetchedServers;
            if(var2) {
               this.hasFetchedServers = true;
            }

            if(var1 != null) {
               boolean var3 = false;

               for(RealmsServer var5 : var1) {
                  if(this.isSelfOwnedNonExpiredServer(var5)) {
                     var3 = true;
                  }
               }

               this.realmsServers = var1;
               if(this.shouldShowMessageInList()) {
                  this.realmSelectionList.addEntry(new RealmsMainScreen.RealmSelectionListTrialEntry());
               }

               for(RealmsServer var5 : this.realmsServers) {
                  this.realmSelectionList.addEntry(new RealmsMainScreen.RealmSelectionListEntry(var5));
               }

               if(!regionsPinged && var3) {
                  regionsPinged = true;
                  this.pingRegions();
               }
            }

            if(var2) {
               this.addButtons();
            }
         }

         if(realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.numberOfPendingInvites = realmsDataFetcher.getPendingInvitesCount();
            if(this.numberOfPendingInvites > 0 && this.inviteNarrationLimiter.tryAcquire(1)) {
               Realms.narrateNow(getLocalizedString("mco.configure.world.invite.narration", new Object[]{Integer.valueOf(this.numberOfPendingInvites)}));
            }
         }

         if(realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE) && !this.createdTrial) {
            boolean var1 = realmsDataFetcher.isTrialAvailable();
            if(var1 != this.trialsAvailable && this.shouldShowPopup()) {
               this.trialsAvailable = var1;
               this.showingPopup = false;
            } else {
               this.trialsAvailable = var1;
            }
         }

         if(realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.LIVE_STATS)) {
            RealmsServerPlayerLists var1 = realmsDataFetcher.getLivestats();

            label422:
            for(RealmsServerPlayerList var3 : var1.servers) {
               Iterator var11 = this.realmsServers.iterator();

               RealmsServer var5;
               while(true) {
                  if(!var11.hasNext()) {
                     continue label422;
                  }

                  var5 = (RealmsServer)var11.next();
                  if(var5.id == var3.serverId) {
                     break;
                  }
               }

               var5.updateServerPing(var3);
            }
         }

         if(realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.UNREAD_NEWS)) {
            this.hasUnreadNews = realmsDataFetcher.hasUnreadNews();
            this.newsLink = realmsDataFetcher.newsLink();
         }

         realmsDataFetcher.markClean();
         if(this.shouldShowPopup()) {
            ++this.carouselTick;
         }

         if(this.showPopupButton != null) {
            this.showPopupButton.setVisible(this.shouldShowPopupButton());
         }

      }
   }

   private void browseURL(String string) {
      Realms.setClipboard(string);
      RealmsUtil.browseTo(string);
   }

   private void pingRegions() {
      (new Thread() {
         public void run() {
            List<RegionPingResult> var1 = Ping.pingAllRegions();
            RealmsClient var2 = RealmsClient.createRealmsClient();
            PingResult var3 = new PingResult();
            var3.pingResults = var1;
            var3.worldIds = RealmsMainScreen.this.getOwnedNonExpiredWorldIds();

            try {
               var2.sendPingResults(var3);
            } catch (Throwable var5) {
               RealmsMainScreen.LOGGER.warn("Could not send ping result to Realms: ", var5);
            }

         }
      }).start();
   }

   private List getOwnedNonExpiredWorldIds() {
      List<Long> list = new ArrayList();

      for(RealmsServer var3 : this.realmsServers) {
         if(this.isSelfOwnedNonExpiredServer(var3)) {
            list.add(Long.valueOf(var3.id));
         }
      }

      return list;
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
      this.stopRealmsFetcher();
   }

   public void setCreatedTrial(boolean createdTrial) {
      this.createdTrial = createdTrial;
   }

   private void onPlay() {
      RealmsServer var1 = this.findServer(this.selectedServerId);
      if(var1 != null) {
         this.play(var1, this);
      }
   }

   private void onRenew() {
      RealmsServer var1 = this.findServer(this.selectedServerId);
      if(var1 != null) {
         String var2 = "https://account.mojang.com/buy/realms?sid=" + var1.remoteSubscriptionId + "&pid=" + Realms.getUUID() + "&ref=" + (var1.expiredTrial?"expiredTrial":"expiredRealm");
         this.browseURL(var2);
      }
   }

   private void createTrial() {
      if(this.trialsAvailable && !this.createdTrial) {
         Realms.setScreen(new RealmsCreateTrialScreen(this));
      }
   }

   private void checkClientCompatability() {
      if(!checkedClientCompatability) {
         checkedClientCompatability = true;
         (new Thread("MCO Compatability Checker #1") {
            public void run() {
               RealmsClient var1 = RealmsClient.createRealmsClient();

               try {
                  RealmsClient.CompatibleVersionResponse var2 = var1.clientCompatible();
                  if(var2.equals(RealmsClient.CompatibleVersionResponse.OUTDATED)) {
                     RealmsMainScreen.realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, true);
                     Realms.setScreen(RealmsMainScreen.realmsGenericErrorScreen);
                  } else if(var2.equals(RealmsClient.CompatibleVersionResponse.OTHER)) {
                     RealmsMainScreen.realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, false);
                     Realms.setScreen(RealmsMainScreen.realmsGenericErrorScreen);
                  } else {
                     RealmsMainScreen.this.checkParentalConsent();
                  }
               } catch (RealmsServiceException var3) {
                  RealmsMainScreen.checkedClientCompatability = false;
                  RealmsMainScreen.LOGGER.error("Couldn\'t connect to realms: ", var3.toString());
                  if(var3.httpResultCode == 401) {
                     RealmsMainScreen.realmsGenericErrorScreen = new RealmsGenericErrorScreen(RealmsScreen.getLocalizedString("mco.error.invalid.session.title"), RealmsScreen.getLocalizedString("mco.error.invalid.session.message"), RealmsMainScreen.this.lastScreen);
                     Realms.setScreen(RealmsMainScreen.realmsGenericErrorScreen);
                  } else {
                     Realms.setScreen(new RealmsGenericErrorScreen(var3, RealmsMainScreen.this.lastScreen));
                  }
               } catch (IOException var4) {
                  RealmsMainScreen.checkedClientCompatability = false;
                  RealmsMainScreen.LOGGER.error("Couldn\'t connect to realms: ", var4.getMessage());
                  Realms.setScreen(new RealmsGenericErrorScreen(var4.getMessage(), RealmsMainScreen.this.lastScreen));
               }
            }
         }).start();
      }

   }

   private void checkUnreadNews() {
   }

   private void checkParentalConsent() {
      (new Thread("MCO Compatability Checker #1") {
         public void run() {
            RealmsClient var1 = RealmsClient.createRealmsClient();

            try {
               Boolean var2 = var1.mcoEnabled();
               if(var2.booleanValue()) {
                  RealmsMainScreen.LOGGER.info("Realms is available for this user");
                  RealmsMainScreen.hasParentalConsent = true;
               } else {
                  RealmsMainScreen.LOGGER.info("Realms is not available for this user");
                  RealmsMainScreen.hasParentalConsent = false;
                  Realms.setScreen(new RealmsParentalConsentScreen(RealmsMainScreen.this.lastScreen));
               }

               RealmsMainScreen.checkedParentalConsent = true;
            } catch (RealmsServiceException var3) {
               RealmsMainScreen.LOGGER.error("Couldn\'t connect to realms: ", var3.toString());
               Realms.setScreen(new RealmsGenericErrorScreen(var3, RealmsMainScreen.this.lastScreen));
            } catch (IOException var4) {
               RealmsMainScreen.LOGGER.error("Couldn\'t connect to realms: ", var4.getMessage());
               Realms.setScreen(new RealmsGenericErrorScreen(var4.getMessage(), RealmsMainScreen.this.lastScreen));
            }

         }
      }).start();
   }

   private void switchToStage() {
      if(!RealmsClient.currentEnvironment.equals(RealmsClient.Environment.STAGE)) {
         (new Thread("MCO Stage Availability Checker #1") {
            public void run() {
               RealmsClient var1 = RealmsClient.createRealmsClient();

               try {
                  Boolean var2 = var1.stageAvailable();
                  if(var2.booleanValue()) {
                     RealmsClient.switchToStage();
                     RealmsMainScreen.LOGGER.info("Switched to stage");
                     RealmsMainScreen.realmsDataFetcher.forceUpdate();
                  }
               } catch (RealmsServiceException var3) {
                  RealmsMainScreen.LOGGER.error("Couldn\'t connect to Realms: " + var3);
               } catch (IOException var4) {
                  RealmsMainScreen.LOGGER.error("Couldn\'t parse response connecting to Realms: " + var4.getMessage());
               }

            }
         }).start();
      }

   }

   private void switchToLocal() {
      if(!RealmsClient.currentEnvironment.equals(RealmsClient.Environment.LOCAL)) {
         (new Thread("MCO Local Availability Checker #1") {
            public void run() {
               RealmsClient var1 = RealmsClient.createRealmsClient();

               try {
                  Boolean var2 = var1.stageAvailable();
                  if(var2.booleanValue()) {
                     RealmsClient.switchToLocal();
                     RealmsMainScreen.LOGGER.info("Switched to local");
                     RealmsMainScreen.realmsDataFetcher.forceUpdate();
                  }
               } catch (RealmsServiceException var3) {
                  RealmsMainScreen.LOGGER.error("Couldn\'t connect to Realms: " + var3);
               } catch (IOException var4) {
                  RealmsMainScreen.LOGGER.error("Couldn\'t parse response connecting to Realms: " + var4.getMessage());
               }

            }
         }).start();
      }

   }

   private void switchToProd() {
      RealmsClient.switchToProd();
      realmsDataFetcher.forceUpdate();
   }

   private void stopRealmsFetcher() {
      realmsDataFetcher.stop();
   }

   private void configureClicked(RealmsServer realmsServer) {
      if(Realms.getUUID().equals(realmsServer.ownerUUID) || overrideConfigure) {
         this.saveListScrollPosition();
         Minecraft var2 = Minecraft.getInstance();
         var2.execute(() -> {
            var2.setScreen((new RealmsConfigureWorldScreen(this, realmsServer.id)).getProxy());
         });
      }

   }

   private void leaveClicked(RealmsServer realmsServer) {
      if(!Realms.getUUID().equals(realmsServer.ownerUUID)) {
         this.saveListScrollPosition();
         String var2 = getLocalizedString("mco.configure.world.leave.question.line1");
         String var3 = getLocalizedString("mco.configure.world.leave.question.line2");
         Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, var2, var3, true, 4));
      }

   }

   private void saveListScrollPosition() {
      lastScrollYPosition = this.realmSelectionList.getScroll();
   }

   private RealmsServer findServer(long l) {
      for(RealmsServer var4 : this.realmsServers) {
         if(var4.id == l) {
            return var4;
         }
      }

      return null;
   }

   public void confirmResult(boolean var1, int var2) {
      if(var2 == 4) {
         if(var1) {
            (new Thread("Realms-leave-server") {
               public void run() {
                  try {
                     RealmsServer var1 = RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId);
                     if(var1 != null) {
                        RealmsClient var2 = RealmsClient.createRealmsClient();
                        var2.uninviteMyselfFrom(var1.id);
                        RealmsMainScreen.realmsDataFetcher.removeItem(var1);
                        RealmsMainScreen.this.realmsServers.remove(var1);
                        RealmsMainScreen.this.selectedServerId = -1L;
                        RealmsMainScreen.this.playButton.active(false);
                     }
                  } catch (RealmsServiceException var3) {
                     RealmsMainScreen.LOGGER.error("Couldn\'t configure world");
                     Realms.setScreen(new RealmsGenericErrorScreen(var3, RealmsMainScreen.this));
                  }

               }
            }).start();
         }

         Realms.setScreen(this);
      }

   }

   public void removeSelection() {
      this.selectedServerId = -1L;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      switch(var1) {
      case 256:
         this.keyCombos.forEach(KeyCombo::reset);
         this.onClosePopup();
         return true;
      default:
         return super.keyPressed(var1, var2, var3);
      }
   }

   private void onClosePopup() {
      if(this.shouldShowPopup() && this.popupOpenedByUser) {
         this.popupOpenedByUser = false;
      } else {
         Realms.setScreen(this.lastScreen);
      }

   }

   public boolean charTyped(char var1, int var2) {
      this.keyCombos.forEach((keyCombo) -> {
         keyCombo.keyPressed(var1);
      });
      return true;
   }

   public void render(int var1, int var2, float var3) {
      this.expiredHover = false;
      this.toolTip = null;
      this.renderBackground();
      this.realmSelectionList.render(var1, var2, var3);
      this.drawRealmsLogo(this.width() / 2 - 50, 7);
      if(RealmsClient.currentEnvironment.equals(RealmsClient.Environment.STAGE)) {
         this.renderStage();
      }

      if(RealmsClient.currentEnvironment.equals(RealmsClient.Environment.LOCAL)) {
         this.renderLocal();
      }

      if(this.shouldShowPopup()) {
         this.drawPopup(var1, var2);
      } else {
         if(this.showingPopup) {
            this.updateButtonStates((RealmsServer)null);
            if(!this.hasWidget(this.realmSelectionList)) {
               this.addWidget(this.realmSelectionList);
            }

            RealmsServer var4 = this.findServer(this.selectedServerId);
            this.playButton.active(this.shouldPlayButtonBeActive(var4));
         }

         this.showingPopup = false;
      }

      super.render(var1, var2, var3);
      if(this.toolTip != null) {
         this.renderMousehoverTooltip(this.toolTip, var1, var2);
      }

      if(this.trialsAvailable && !this.createdTrial && this.shouldShowPopup()) {
         RealmsScreen.bind("realms:textures/gui/realms/trial_icon.png");
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.pushMatrix();
         int var4 = 8;
         int var5 = 8;
         int var6 = 0;
         if((System.currentTimeMillis() / 800L & 1L) == 1L) {
            var6 = 8;
         }

         RealmsScreen.blit(this.createTrialButton.x() + this.createTrialButton.getWidth() - 8 - 4, this.createTrialButton.y() + this.createTrialButton.getHeight() / 2 - 4, 0.0F, (float)var6, 8, 8, 8, 16);
         GlStateManager.popMatrix();
      }

   }

   private void drawRealmsLogo(int var1, int var2) {
      RealmsScreen.bind("realms:textures/gui/title/realms.png");
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.scalef(0.5F, 0.5F, 0.5F);
      RealmsScreen.blit(var1 * 2, var2 * 2 - 5, 0.0F, 0.0F, 200, 50, 200, 50);
      GlStateManager.popMatrix();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if(this.isOutsidePopup(var1, var3) && this.popupOpenedByUser) {
         this.popupOpenedByUser = false;
         this.justClosedPopup = true;
         return true;
      } else {
         return super.mouseClicked(var1, var3, var5);
      }
   }

   private boolean isOutsidePopup(double var1, double var3) {
      int var5 = this.popupX0();
      int var6 = this.popupY0();
      return var1 < (double)(var5 - 5) || var1 > (double)(var5 + 315) || var3 < (double)(var6 - 5) || var3 > (double)(var6 + 171);
   }

   private void drawPopup(int var1, int var2) {
      int var3 = this.popupX0();
      int var4 = this.popupY0();
      String var5 = getLocalizedString("mco.selectServer.popup");
      List<String> var6 = this.fontSplit(var5, 100);
      if(!this.showingPopup) {
         this.carouselIndex = 0;
         this.carouselTick = 0;
         this.hasSwitchedCarouselImage = true;
         this.updateButtonStates((RealmsServer)null);
         if(this.hasWidget(this.realmSelectionList)) {
            this.removeWidget(this.realmSelectionList);
         }

         Realms.narrateNow(var5);
      }

      if(this.hasFetchedServers) {
         this.showingPopup = true;
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.7F);
      GlStateManager.enableBlend();
      RealmsScreen.bind("realms:textures/gui/realms/darken.png");
      GlStateManager.pushMatrix();
      int var7 = 0;
      int var8 = 32;
      RealmsScreen.blit(0, 32, 0.0F, 0.0F, this.width(), this.height() - 40 - 32, 310, 166);
      GlStateManager.popMatrix();
      GlStateManager.disableBlend();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RealmsScreen.bind("realms:textures/gui/realms/popup.png");
      GlStateManager.pushMatrix();
      RealmsScreen.blit(var3, var4, 0.0F, 0.0F, 310, 166, 310, 166);
      GlStateManager.popMatrix();
      RealmsScreen.bind(IMAGES_LOCATION[this.carouselIndex]);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      RealmsScreen.blit(var3 + 7, var4 + 7, 0.0F, 0.0F, 195, 152, 195, 152);
      GlStateManager.popMatrix();
      if(this.carouselTick % 95 < 5) {
         if(!this.hasSwitchedCarouselImage) {
            if(this.carouselIndex == IMAGES_LOCATION.length - 1) {
               this.carouselIndex = 0;
            } else {
               ++this.carouselIndex;
            }

            this.hasSwitchedCarouselImage = true;
         }
      } else {
         this.hasSwitchedCarouselImage = false;
      }

      int var9 = 0;

      for(String var11 : var6) {
         int var10002 = this.width() / 2 + 52;
         ++var9;
         this.drawString(var11, var10002, var4 + 10 * var9 - 3, 5000268, false);
      }

   }

   private int popupX0() {
      return (this.width() - 310) / 2;
   }

   private int popupY0() {
      return this.height() / 2 - 80;
   }

   private void drawInvitationPendingIcon(int var1, int var2, int var3, int var4, boolean var5, boolean var6) {
      int var7 = this.numberOfPendingInvites;
      boolean var8 = this.inPendingInvitationArea((double)var1, (double)var2);
      boolean var9 = var6 && var5;
      if(var9) {
         float var10 = 0.25F + (1.0F + RealmsMth.sin((float)this.animTick * 0.5F)) * 0.25F;
         int var11 = -16777216 | (int)(var10 * 64.0F) << 16 | (int)(var10 * 64.0F) << 8 | (int)(var10 * 64.0F) << 0;
         this.fillGradient(var3 - 2, var4 - 2, var3 + 18, var4 + 18, var11, var11);
         var11 = -16777216 | (int)(var10 * 255.0F) << 16 | (int)(var10 * 255.0F) << 8 | (int)(var10 * 255.0F) << 0;
         this.fillGradient(var3 - 2, var4 - 2, var3 + 18, var4 - 1, var11, var11);
         this.fillGradient(var3 - 2, var4 - 2, var3 - 1, var4 + 18, var11, var11);
         this.fillGradient(var3 + 17, var4 - 2, var3 + 18, var4 + 18, var11, var11);
         this.fillGradient(var3 - 2, var4 + 17, var3 + 18, var4 + 18, var11, var11);
      }

      RealmsScreen.bind("realms:textures/gui/realms/invite_icon.png");
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      boolean var10 = var6 && var5;
      RealmsScreen.blit(var3, var4 - 6, var10?16.0F:0.0F, 0.0F, 15, 25, 31, 25);
      GlStateManager.popMatrix();
      boolean var11 = var6 && var7 != 0;
      if(var11) {
         int var12 = (Math.min(var7, 6) - 1) * 8;
         int var13 = (int)(Math.max(0.0F, Math.max(RealmsMth.sin((float)(10 + this.animTick) * 0.57F), RealmsMth.cos((float)this.animTick * 0.35F))) * -6.0F);
         RealmsScreen.bind("realms:textures/gui/realms/invitation_icons.png");
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.pushMatrix();
         RealmsScreen.blit(var3 + 4, var4 + 4 + var13, (float)var12, var8?8.0F:0.0F, 8, 8, 48, 16);
         GlStateManager.popMatrix();
      }

      int var12 = var1 + 12;
      boolean var14 = var6 && var8;
      if(var14) {
         String var15 = getLocalizedString(var7 == 0?"mco.invites.nopending":"mco.invites.pending");
         int var16 = this.fontWidth(var15);
         this.fillGradient(var12 - 3, var2 - 3, var12 + var16 + 3, var2 + 8 + 3, -1073741824, -1073741824);
         this.fontDrawShadow(var15, var12, var2, -1);
      }

   }

   private boolean inPendingInvitationArea(double var1, double var3) {
      int var5 = this.width() / 2 + 50;
      int var6 = this.width() / 2 + 66;
      int var7 = 11;
      int var8 = 23;
      if(this.numberOfPendingInvites != 0) {
         var5 -= 3;
         var6 += 3;
         var7 -= 5;
         var8 += 5;
      }

      return (double)var5 <= var1 && var1 <= (double)var6 && (double)var7 <= var3 && var3 <= (double)var8;
   }

   public void play(RealmsServer realmsServer, RealmsScreen realmsScreen) {
      if(realmsServer != null) {
         try {
            if(!this.connectLock.tryLock(1L, TimeUnit.SECONDS)) {
               return;
            }

            if(this.connectLock.getHoldCount() > 1) {
               return;
            }
         } catch (InterruptedException var4) {
            return;
         }

         this.dontSetConnectedToRealms = true;
         this.connectToServer(realmsServer, realmsScreen);
      }

   }

   private void connectToServer(RealmsServer realmsServer, RealmsScreen realmsScreen) {
      RealmsLongRunningMcoTaskScreen var3 = new RealmsLongRunningMcoTaskScreen(realmsScreen, new RealmsTasks.RealmsGetServerDetailsTask(this, realmsScreen, realmsServer, this.connectLock));
      var3.start();
      Realms.setScreen(var3);
   }

   private boolean isSelfOwnedServer(RealmsServer realmsServer) {
      return realmsServer.ownerUUID != null && realmsServer.ownerUUID.equals(Realms.getUUID());
   }

   private boolean isSelfOwnedNonExpiredServer(RealmsServer realmsServer) {
      return realmsServer.ownerUUID != null && realmsServer.ownerUUID.equals(Realms.getUUID()) && !realmsServer.expired;
   }

   private void drawExpired(int var1, int var2, int var3, int var4) {
      RealmsScreen.bind("realms:textures/gui/realms/expired_icon.png");
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      RealmsScreen.blit(var1, var2, 0.0F, 0.0F, 10, 28, 10, 28);
      GlStateManager.popMatrix();
      if(var3 >= var1 && var3 <= var1 + 9 && var4 >= var2 && var4 <= var2 + 27 && var4 < this.height() - 40 && var4 > 32 && !this.shouldShowPopup()) {
         this.toolTip = getLocalizedString("mco.selectServer.expired");
      }

   }

   private void drawExpiring(int var1, int var2, int var3, int var4, int var5) {
      RealmsScreen.bind("realms:textures/gui/realms/expires_soon_icon.png");
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      if(this.animTick % 20 < 10) {
         RealmsScreen.blit(var1, var2, 0.0F, 0.0F, 10, 28, 20, 28);
      } else {
         RealmsScreen.blit(var1, var2, 10.0F, 0.0F, 10, 28, 20, 28);
      }

      GlStateManager.popMatrix();
      if(var3 >= var1 && var3 <= var1 + 9 && var4 >= var2 && var4 <= var2 + 27 && var4 < this.height() - 40 && var4 > 32 && !this.shouldShowPopup()) {
         if(var5 <= 0) {
            this.toolTip = getLocalizedString("mco.selectServer.expires.soon");
         } else if(var5 == 1) {
            this.toolTip = getLocalizedString("mco.selectServer.expires.day");
         } else {
            this.toolTip = getLocalizedString("mco.selectServer.expires.days", new Object[]{Integer.valueOf(var5)});
         }
      }

   }

   private void drawOpen(int var1, int var2, int var3, int var4) {
      RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      RealmsScreen.blit(var1, var2, 0.0F, 0.0F, 10, 28, 10, 28);
      GlStateManager.popMatrix();
      if(var3 >= var1 && var3 <= var1 + 9 && var4 >= var2 && var4 <= var2 + 27 && var4 < this.height() - 40 && var4 > 32 && !this.shouldShowPopup()) {
         this.toolTip = getLocalizedString("mco.selectServer.open");
      }

   }

   private void drawClose(int var1, int var2, int var3, int var4) {
      RealmsScreen.bind("realms:textures/gui/realms/off_icon.png");
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      RealmsScreen.blit(var1, var2, 0.0F, 0.0F, 10, 28, 10, 28);
      GlStateManager.popMatrix();
      if(var3 >= var1 && var3 <= var1 + 9 && var4 >= var2 && var4 <= var2 + 27 && var4 < this.height() - 40 && var4 > 32 && !this.shouldShowPopup()) {
         this.toolTip = getLocalizedString("mco.selectServer.closed");
      }

   }

   private void drawLeave(int var1, int var2, int var3, int var4) {
      boolean var5 = false;
      if(var3 >= var1 && var3 <= var1 + 28 && var4 >= var2 && var4 <= var2 + 28 && var4 < this.height() - 40 && var4 > 32 && !this.shouldShowPopup()) {
         var5 = true;
      }

      RealmsScreen.bind("realms:textures/gui/realms/leave_icon.png");
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      RealmsScreen.blit(var1, var2, var5?28.0F:0.0F, 0.0F, 28, 28, 56, 28);
      GlStateManager.popMatrix();
      if(var5) {
         this.toolTip = getLocalizedString("mco.selectServer.leave");
      }

   }

   private void drawConfigure(int var1, int var2, int var3, int var4) {
      boolean var5 = false;
      if(var3 >= var1 && var3 <= var1 + 28 && var4 >= var2 && var4 <= var2 + 28 && var4 < this.height() - 40 && var4 > 32 && !this.shouldShowPopup()) {
         var5 = true;
      }

      RealmsScreen.bind("realms:textures/gui/realms/configure_icon.png");
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      RealmsScreen.blit(var1, var2, var5?28.0F:0.0F, 0.0F, 28, 28, 56, 28);
      GlStateManager.popMatrix();
      if(var5) {
         this.toolTip = getLocalizedString("mco.selectServer.configure");
      }

   }

   protected void renderMousehoverTooltip(String string, int var2, int var3) {
      if(string != null) {
         int var4 = 0;
         int var5 = 0;

         for(String var9 : string.split("\n")) {
            int var10 = this.fontWidth(var9);
            if(var10 > var5) {
               var5 = var10;
            }
         }

         int var6 = var2 - var5 - 5;
         int var7 = var3;
         if(var6 < 0) {
            var6 = var2 + 12;
         }

         for(String var11 : string.split("\n")) {
            this.fillGradient(var6 - 3, var7 - (var4 == 0?3:0) + var4, var6 + var5 + 3, var7 + 8 + 3 + var4, -1073741824, -1073741824);
            this.fontDrawShadow(var11, var6, var7 + var4, 16777215);
            var4 += 10;
         }

      }
   }

   private void renderMoreInfo(int var1, int var2, int var3, int var4, boolean var5) {
      boolean var6 = false;
      if(var1 >= var3 && var1 <= var3 + 20 && var2 >= var4 && var2 <= var4 + 20) {
         var6 = true;
      }

      RealmsScreen.bind("realms:textures/gui/realms/questionmark.png");
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      RealmsScreen.blit(var3, var4, var5?20.0F:0.0F, 0.0F, 20, 20, 40, 20);
      GlStateManager.popMatrix();
      if(var6) {
         this.toolTip = getLocalizedString("mco.selectServer.info");
      }

   }

   private void renderNews(int var1, int var2, boolean var3, int var4, int var5, boolean var6, boolean var7) {
      boolean var8 = false;
      if(var1 >= var4 && var1 <= var4 + 20 && var2 >= var5 && var2 <= var5 + 20) {
         var8 = true;
      }

      RealmsScreen.bind("realms:textures/gui/realms/news_icon.png");
      if(var7) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         GlStateManager.color4f(0.5F, 0.5F, 0.5F, 1.0F);
      }

      GlStateManager.pushMatrix();
      boolean var9 = var7 && var6;
      RealmsScreen.blit(var4, var5, var9?20.0F:0.0F, 0.0F, 20, 20, 40, 20);
      GlStateManager.popMatrix();
      if(var8 && var7) {
         this.toolTip = getLocalizedString("mco.news");
      }

      if(var3 && var7) {
         int var10 = var8?0:(int)(Math.max(0.0F, Math.max(RealmsMth.sin((float)(10 + this.animTick) * 0.57F), RealmsMth.cos((float)this.animTick * 0.35F))) * -6.0F);
         RealmsScreen.bind("realms:textures/gui/realms/invitation_icons.png");
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.pushMatrix();
         RealmsScreen.blit(var4 + 10, var5 + 2 + var10, 40.0F, 0.0F, 8, 8, 48, 16);
         GlStateManager.popMatrix();
      }

   }

   private void renderLocal() {
      String var1 = "LOCAL!";
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)(this.width() / 2 - 25), 20.0F, 0.0F);
      GlStateManager.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.scalef(1.5F, 1.5F, 1.5F);
      this.drawString("LOCAL!", 0, 0, 8388479);
      GlStateManager.popMatrix();
   }

   private void renderStage() {
      String var1 = "STAGE!";
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)(this.width() / 2 - 25), 20.0F, 0.0F);
      GlStateManager.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.scalef(1.5F, 1.5F, 1.5F);
      this.drawString("STAGE!", 0, 0, -256);
      GlStateManager.popMatrix();
   }

   public RealmsMainScreen newScreen() {
      return new RealmsMainScreen(this.lastScreen);
   }

   public void closePopup() {
      if(this.shouldShowPopup() && this.popupOpenedByUser) {
         this.popupOpenedByUser = false;
      }

   }

   @ClientJarOnly
   class CloseButton extends RealmsButton {
      public CloseButton() {
         super(11, RealmsMainScreen.this.popupX0() + 4, RealmsMainScreen.this.popupY0() + 4, 12, 12, RealmsScreen.getLocalizedString("mco.selectServer.close"));
      }

      public void tick() {
         super.tick();
      }

      public void render(int var1, int var2, float var3) {
         super.render(var1, var2, var3);
      }

      public void renderButton(int var1, int var2, float var3) {
         RealmsScreen.bind("realms:textures/gui/realms/cross_icon.png");
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.pushMatrix();
         RealmsScreen.blit(this.x(), this.y(), 0.0F, this.getProxy().isHovered()?12.0F:0.0F, 12, 12, 12, 24);
         GlStateManager.popMatrix();
         if(this.getProxy().isMouseOver((double)var1, (double)var2)) {
            RealmsMainScreen.this.toolTip = this.getProxy().getMessage();
         }

      }

      public void onPress() {
         RealmsMainScreen.this.onClosePopup();
      }
   }

   @ClientJarOnly
   class NewsButton extends RealmsButton {
      public NewsButton() {
         super(9, RealmsMainScreen.this.width() - 62, 6, 20, 20, "");
      }

      public void tick() {
         this.setMessage(Realms.getLocalizedString("mco.news", new Object[0]));
      }

      public void render(int var1, int var2, float var3) {
         super.render(var1, var2, var3);
      }

      public void onPress() {
         if(RealmsMainScreen.this.newsLink != null) {
            RealmsUtil.browseTo(RealmsMainScreen.this.newsLink);
            if(RealmsMainScreen.this.hasUnreadNews) {
               RealmsPersistence.RealmsPersistenceData var1 = RealmsPersistence.readFile();
               var1.hasUnreadNews = false;
               RealmsMainScreen.this.hasUnreadNews = false;
               RealmsPersistence.writeFile(var1);
            }

         }
      }

      public void renderButton(int var1, int var2, float var3) {
         RealmsMainScreen.this.renderNews(var1, var2, RealmsMainScreen.this.hasUnreadNews, this.x(), this.y(), this.getProxy().isHovered(), this.active());
      }
   }

   @ClientJarOnly
   class PendingInvitesButton extends RealmsButton {
      public PendingInvitesButton() {
         super(8, RealmsMainScreen.this.width() / 2 + 47, 6, 22, 22, "");
      }

      public void tick() {
         this.setMessage(Realms.getLocalizedString(RealmsMainScreen.this.numberOfPendingInvites == 0?"mco.invites.nopending":"mco.invites.pending", new Object[0]));
      }

      public void render(int var1, int var2, float var3) {
         super.render(var1, var2, var3);
      }

      public void onPress() {
         RealmsPendingInvitesScreen var1 = new RealmsPendingInvitesScreen(RealmsMainScreen.this.lastScreen);
         Realms.setScreen(var1);
      }

      public void renderButton(int var1, int var2, float var3) {
         RealmsMainScreen.this.drawInvitationPendingIcon(var1, var2, this.x(), this.y(), this.getProxy().isHovered(), this.active());
      }
   }

   @ClientJarOnly
   class RealmSelectionList extends RealmsObjectSelectionList {
      public RealmSelectionList() {
         super(RealmsMainScreen.this.width(), RealmsMainScreen.this.height(), 32, RealmsMainScreen.this.height() - 40, 36);
      }

      public boolean isFocused() {
         return RealmsMainScreen.this.isFocused(this);
      }

      public boolean keyPressed(int var1, int var2, int var3) {
         if(var1 != 257 && var1 != 32 && var1 != 335) {
            return false;
         } else {
            RealmListEntry var4 = this.getSelected();
            return var4 == null?super.keyPressed(var1, var2, var3):var4.mouseClicked(0.0D, 0.0D, 0);
         }
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         if(var5 == 0 && var1 < (double)this.getScrollbarPosition() && var3 >= (double)this.y0() && var3 <= (double)this.y1()) {
            int var6 = RealmsMainScreen.this.realmSelectionList.getRowLeft();
            int var7 = this.getScrollbarPosition();
            int var8 = (int)Math.floor(var3 - (double)this.y0()) - this.headerHeight() + this.getScroll() - 4;
            int var9 = var8 / this.itemHeight();
            if(var1 >= (double)var6 && var1 <= (double)var7 && var9 >= 0 && var8 >= 0 && var9 < this.getItemCount()) {
               this.itemClicked(var8, var9, var1, var3, this.width());
               RealmsMainScreen.this.clicks = RealmsMainScreen.this.clicks + 7;
               this.selectItem(var9);
            }

            return true;
         } else {
            return super.mouseClicked(var1, var3, var5);
         }
      }

      public void selectItem(int selected) {
         this.setSelected(selected);
         if(selected != -1) {
            RealmsServer var2;
            if(RealmsMainScreen.this.shouldShowMessageInList()) {
               if(selected == 0) {
                  Realms.narrateNow(new String[]{RealmsScreen.getLocalizedString("mco.trial.message.line1"), RealmsScreen.getLocalizedString("mco.trial.message.line2")});
                  var2 = null;
               } else {
                  var2 = (RealmsServer)RealmsMainScreen.this.realmsServers.get(selected - 1);
               }
            } else {
               var2 = (RealmsServer)RealmsMainScreen.this.realmsServers.get(selected);
            }

            RealmsMainScreen.this.updateButtonStates(var2);
            if(var2 == null) {
               RealmsMainScreen.this.selectedServerId = -1L;
            } else if(var2.state == RealmsServer.State.UNINITIALIZED) {
               Realms.narrateNow(RealmsScreen.getLocalizedString("mco.selectServer.uninitialized") + RealmsScreen.getLocalizedString("mco.gui.button"));
               RealmsMainScreen.this.selectedServerId = -1L;
            } else {
               RealmsMainScreen.this.selectedServerId = var2.id;
               if(RealmsMainScreen.this.clicks >= 10 && RealmsMainScreen.this.playButton.active()) {
                  RealmsMainScreen.this.play(RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId), RealmsMainScreen.this);
               }

               Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", new Object[]{var2.name}));
            }
         }
      }

      public void itemClicked(int var1, int var2, double var3, double var5, int var7) {
         if(RealmsMainScreen.this.shouldShowMessageInList()) {
            if(var2 == 0) {
               RealmsMainScreen.this.popupOpenedByUser = true;
               return;
            }

            --var2;
         }

         if(var2 < RealmsMainScreen.this.realmsServers.size()) {
            RealmsServer var8 = (RealmsServer)RealmsMainScreen.this.realmsServers.get(var2);
            if(var8 != null) {
               if(var8.state == RealmsServer.State.UNINITIALIZED) {
                  RealmsMainScreen.this.selectedServerId = -1L;
                  Realms.setScreen(new RealmsCreateRealmScreen(var8, RealmsMainScreen.this));
               } else {
                  RealmsMainScreen.this.selectedServerId = var8.id;
               }

               if(RealmsMainScreen.this.toolTip != null && RealmsMainScreen.this.toolTip.equals(RealmsScreen.getLocalizedString("mco.selectServer.configure"))) {
                  RealmsMainScreen.this.selectedServerId = var8.id;
                  RealmsMainScreen.this.configureClicked(var8);
               } else if(RealmsMainScreen.this.toolTip != null && RealmsMainScreen.this.toolTip.equals(RealmsScreen.getLocalizedString("mco.selectServer.leave"))) {
                  RealmsMainScreen.this.selectedServerId = var8.id;
                  RealmsMainScreen.this.leaveClicked(var8);
               } else if(RealmsMainScreen.this.isSelfOwnedServer(var8) && var8.expired && RealmsMainScreen.this.expiredHover) {
                  RealmsMainScreen.this.onRenew();
               }

            }
         }
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public int getRowWidth() {
         return 300;
      }
   }

   @ClientJarOnly
   class RealmSelectionListEntry extends RealmListEntry {
      final RealmsServer mServerData;

      public RealmSelectionListEntry(RealmsServer mServerData) {
         this.mServerData = mServerData;
      }

      public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
         this.renderMcoServerItem(this.mServerData, var3, var2, var6, var7);
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         if(this.mServerData.state == RealmsServer.State.UNINITIALIZED) {
            RealmsMainScreen.this.selectedServerId = -1L;
            Realms.setScreen(new RealmsCreateRealmScreen(this.mServerData, RealmsMainScreen.this));
         } else {
            RealmsMainScreen.this.selectedServerId = this.mServerData.id;
         }

         return true;
      }

      private void renderMcoServerItem(RealmsServer realmsServer, int var2, int var3, int var4, int var5) {
         this.renderLegacy(realmsServer, var2 + 36, var3, var4, var5);
      }

      private void renderLegacy(RealmsServer realmsServer, int var2, int var3, int var4, int var5) {
         if(realmsServer.state == RealmsServer.State.UNINITIALIZED) {
            RealmsScreen.bind("realms:textures/gui/realms/world_icon.png");
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableAlphaTest();
            GlStateManager.pushMatrix();
            RealmsScreen.blit(var2 + 10, var3 + 6, 0.0F, 0.0F, 40, 20, 40, 20);
            GlStateManager.popMatrix();
            float var6 = 0.5F + (1.0F + RealmsMth.sin((float)RealmsMainScreen.this.animTick * 0.25F)) * 0.25F;
            int var7 = -16777216 | (int)(127.0F * var6) << 16 | (int)(255.0F * var6) << 8 | (int)(127.0F * var6);
            RealmsMainScreen.this.drawCenteredString(RealmsScreen.getLocalizedString("mco.selectServer.uninitialized"), var2 + 10 + 40 + 75, var3 + 12, var7);
         } else {
            int var6 = 225;
            int var7 = 2;
            if(realmsServer.expired) {
               RealmsMainScreen.this.drawExpired(var2 + 225 - 14, var3 + 2, var4, var5);
            } else if(realmsServer.state == RealmsServer.State.CLOSED) {
               RealmsMainScreen.this.drawClose(var2 + 225 - 14, var3 + 2, var4, var5);
            } else if(RealmsMainScreen.this.isSelfOwnedServer(realmsServer) && realmsServer.daysLeft < 7) {
               RealmsMainScreen.this.drawExpiring(var2 + 225 - 14, var3 + 2, var4, var5, realmsServer.daysLeft);
            } else if(realmsServer.state == RealmsServer.State.OPEN) {
               RealmsMainScreen.this.drawOpen(var2 + 225 - 14, var3 + 2, var4, var5);
            }

            if(!RealmsMainScreen.this.isSelfOwnedServer(realmsServer) && !RealmsMainScreen.overrideConfigure) {
               RealmsMainScreen.this.drawLeave(var2 + 225, var3 + 2, var4, var5);
            } else {
               RealmsMainScreen.this.drawConfigure(var2 + 225, var3 + 2, var4, var5);
            }

            if(!"0".equals(realmsServer.serverPing.nrOfPlayers)) {
               String var8 = ChatFormatting.GRAY + "" + realmsServer.serverPing.nrOfPlayers;
               RealmsMainScreen.this.drawString(var8, var2 + 207 - RealmsMainScreen.this.fontWidth(var8), var3 + 3, 8421504);
               if(var4 >= var2 + 207 - RealmsMainScreen.this.fontWidth(var8) && var4 <= var2 + 207 && var5 >= var3 + 1 && var5 <= var3 + 10 && var5 < RealmsMainScreen.this.height() - 40 && var5 > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                  RealmsMainScreen.this.toolTip = realmsServer.serverPing.playerList;
               }
            }

            if(RealmsMainScreen.this.isSelfOwnedServer(realmsServer) && realmsServer.expired) {
               boolean var8 = false;
               GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.enableBlend();
               RealmsScreen.bind("minecraft:textures/gui/widgets.png");
               GlStateManager.pushMatrix();
               GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
               String var9 = RealmsScreen.getLocalizedString("mco.selectServer.expiredList");
               String var10 = RealmsScreen.getLocalizedString("mco.selectServer.expiredRenew");
               if(realmsServer.expiredTrial) {
                  var9 = RealmsScreen.getLocalizedString("mco.selectServer.expiredTrial");
                  var10 = RealmsScreen.getLocalizedString("mco.selectServer.expiredSubscribe");
               }

               int var11 = RealmsMainScreen.this.fontWidth(var10) + 17;
               int var12 = 16;
               int var13 = var2 + RealmsMainScreen.this.fontWidth(var9) + 8;
               int var14 = var3 + 13;
               if(var4 >= var13 && var4 < var13 + var11 && var5 > var14 && var5 <= var14 + 16 & var5 < RealmsMainScreen.this.height() - 40 && var5 > 32 && !RealmsMainScreen.this.shouldShowPopup()) {
                  var8 = true;
                  RealmsMainScreen.this.expiredHover = true;
               }

               int var15 = var8?2:1;
               RealmsScreen.blit(var13, var14, 0.0F, (float)(46 + var15 * 20), var11 / 2, 8, 256, 256);
               RealmsScreen.blit(var13 + var11 / 2, var14, (float)(200 - var11 / 2), (float)(46 + var15 * 20), var11 / 2, 8, 256, 256);
               RealmsScreen.blit(var13, var14 + 8, 0.0F, (float)(46 + var15 * 20 + 12), var11 / 2, 8, 256, 256);
               RealmsScreen.blit(var13 + var11 / 2, var14 + 8, (float)(200 - var11 / 2), (float)(46 + var15 * 20 + 12), var11 / 2, 8, 256, 256);
               GlStateManager.popMatrix();
               GlStateManager.disableBlend();
               int var16 = var3 + 11 + 5;
               int var17 = var8?16777120:16777215;
               RealmsMainScreen.this.drawString(var9, var2 + 2, var16 + 1, 15553363);
               RealmsMainScreen.this.drawCenteredString(var10, var13 + var11 / 2, var16 + 1, var17);
            } else {
               if(realmsServer.worldType.equals(RealmsServer.WorldType.MINIGAME)) {
                  int var8 = 13413468;
                  String var9 = RealmsScreen.getLocalizedString("mco.selectServer.minigame") + " ";
                  int var10 = RealmsMainScreen.this.fontWidth(var9);
                  RealmsMainScreen.this.drawString(var9, var2 + 2, var3 + 12, 13413468);
                  RealmsMainScreen.this.drawString(realmsServer.getMinigameName(), var2 + 2 + var10, var3 + 12, 7105644);
               } else {
                  RealmsMainScreen.this.drawString(realmsServer.getDescription(), var2 + 2, var3 + 12, 7105644);
               }

               if(!RealmsMainScreen.this.isSelfOwnedServer(realmsServer)) {
                  RealmsMainScreen.this.drawString(realmsServer.owner, var2 + 2, var3 + 12 + 11, 5000268);
               }
            }

            RealmsMainScreen.this.drawString(realmsServer.getName(), var2 + 2, var3 + 1, 16777215);
            RealmsTextureManager.withBoundFace(realmsServer.ownerUUID, () -> {
               GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               RealmsScreen.blit(var2 - 36, var3, 8.0F, 8.0F, 8, 8, 32, 32, 64, 64);
               RealmsScreen.blit(var2 - 36, var3, 40.0F, 8.0F, 8, 8, 32, 32, 64, 64);
            });
         }
      }
   }

   @ClientJarOnly
   class RealmSelectionListTrialEntry extends RealmListEntry {
      public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
         this.renderTrialItem(var1, var3, var2, var6, var7);
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         RealmsMainScreen.this.popupOpenedByUser = true;
         return true;
      }

      private void renderTrialItem(int var1, int var2, int var3, int var4, int var5) {
         int var6 = var3 + 8;
         int var7 = 0;
         String var8 = RealmsScreen.getLocalizedString("mco.trial.message.line1") + "\\n" + RealmsScreen.getLocalizedString("mco.trial.message.line2");
         boolean var9 = false;
         if(var2 <= var4 && var4 <= RealmsMainScreen.this.realmSelectionList.getScroll() && var3 <= var5 && var5 <= var3 + 32) {
            var9 = true;
         }

         int var10 = 8388479;
         if(var9 && !RealmsMainScreen.this.shouldShowPopup()) {
            var10 = 6077788;
         }

         for(String var14 : var8.split("\\\\n")) {
            RealmsMainScreen.this.drawCenteredString(var14, RealmsMainScreen.this.width() / 2, var6 + var7, var10);
            var7 += 10;
         }

      }
   }

   @ClientJarOnly
   class ShowPopupButton extends RealmsButton {
      public ShowPopupButton() {
         super(10, RealmsMainScreen.this.width() - 37, 6, 20, 20, RealmsScreen.getLocalizedString("mco.selectServer.info"));
      }

      public void tick() {
         super.tick();
      }

      public void render(int var1, int var2, float var3) {
         super.render(var1, var2, var3);
      }

      public void renderButton(int var1, int var2, float var3) {
         RealmsMainScreen.this.renderMoreInfo(var1, var2, this.x(), this.y(), this.getProxy().isHovered());
      }

      public void onPress() {
         RealmsMainScreen.this.popupOpenedByUser = !RealmsMainScreen.this.popupOpenedByUser;
      }
   }
}
