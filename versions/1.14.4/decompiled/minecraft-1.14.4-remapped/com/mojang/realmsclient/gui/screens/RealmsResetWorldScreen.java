package com.mojang.realmsclient.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetNormalWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsScreenWithCallback;
import com.mojang.realmsclient.gui.screens.RealmsSelectFileToUploadScreen;
import com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsTextureManager;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class RealmsResetWorldScreen extends RealmsScreenWithCallback {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen lastScreen;
   private final RealmsServer serverData;
   private final RealmsScreen returnScreen;
   private RealmsLabel titleLabel;
   private RealmsLabel subtitleLabel;
   private String title;
   private String subtitle;
   private String buttonTitle;
   private int subtitleColor;
   private final int BUTTON_CANCEL_ID;
   private final int BUTTON_FRAME_START;
   private WorldTemplatePaginatedList templates;
   private WorldTemplatePaginatedList adventuremaps;
   private WorldTemplatePaginatedList experiences;
   private WorldTemplatePaginatedList inspirations;
   public int slot;
   private RealmsResetWorldScreen.ResetType typeToReset;
   private RealmsResetWorldScreen.ResetWorldInfo worldInfoToReset;
   private WorldTemplate worldTemplateToReset;
   private String resetTitle;
   private int confirmationId;

   public RealmsResetWorldScreen(RealmsScreen lastScreen, RealmsServer serverData, RealmsScreen returnScreen) {
      this.title = getLocalizedString("mco.reset.world.title");
      this.subtitle = getLocalizedString("mco.reset.world.warning");
      this.buttonTitle = getLocalizedString("gui.cancel");
      this.subtitleColor = 16711680;
      this.BUTTON_CANCEL_ID = 0;
      this.BUTTON_FRAME_START = 100;
      this.templates = null;
      this.adventuremaps = null;
      this.experiences = null;
      this.inspirations = null;
      this.slot = -1;
      this.typeToReset = RealmsResetWorldScreen.ResetType.NONE;
      this.worldInfoToReset = null;
      this.worldTemplateToReset = null;
      this.resetTitle = null;
      this.confirmationId = -1;
      this.lastScreen = lastScreen;
      this.serverData = serverData;
      this.returnScreen = returnScreen;
   }

   public RealmsResetWorldScreen(RealmsScreen var1, RealmsServer realmsServer, RealmsScreen var3, String title, String subtitle, int subtitleColor, String buttonTitle) {
      this(var1, realmsServer, var3);
      this.title = title;
      this.subtitle = subtitle;
      this.subtitleColor = subtitleColor;
      this.buttonTitle = buttonTitle;
   }

   public void setConfirmationId(int confirmationId) {
      this.confirmationId = confirmationId;
   }

   public void setSlot(int slot) {
      this.slot = slot;
   }

   public void setResetTitle(String resetTitle) {
      this.resetTitle = resetTitle;
   }

   public void init() {
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 40, RealmsConstants.row(14) - 10, 80, 20, this.buttonTitle) {
         public void onPress() {
            Realms.setScreen(RealmsResetWorldScreen.this.lastScreen);
         }
      });
      (new Thread("Realms-reset-world-fetcher") {
         public void run() {
            RealmsClient var1 = RealmsClient.createRealmsClient();

            try {
               WorldTemplatePaginatedList var2 = var1.fetchWorldTemplates(1, 10, RealmsServer.WorldType.NORMAL);
               WorldTemplatePaginatedList var3 = var1.fetchWorldTemplates(1, 10, RealmsServer.WorldType.ADVENTUREMAP);
               WorldTemplatePaginatedList var4 = var1.fetchWorldTemplates(1, 10, RealmsServer.WorldType.EXPERIENCE);
               WorldTemplatePaginatedList var5 = var1.fetchWorldTemplates(1, 10, RealmsServer.WorldType.INSPIRATION);
               Realms.execute(() -> {
                  RealmsResetWorldScreen.this.templates = var2;
                  RealmsResetWorldScreen.this.adventuremaps = var3;
                  RealmsResetWorldScreen.this.experiences = var4;
                  RealmsResetWorldScreen.this.inspirations = var5;
               });
            } catch (RealmsServiceException var6) {
               RealmsResetWorldScreen.LOGGER.error("Couldn\'t fetch templates in reset world", var6);
            }

         }
      }).start();
      this.addWidget(this.titleLabel = new RealmsLabel(this.title, this.width() / 2, 7, 16777215));
      this.addWidget(this.subtitleLabel = new RealmsLabel(this.subtitle, this.width() / 2, 22, this.subtitleColor));
      this.buttonsAdd(new RealmsResetWorldScreen.FrameButton(this.frame(1), RealmsConstants.row(0) + 10, getLocalizedString("mco.reset.world.generate"), -1L, RealmsResetWorldScreen.ResetType.GENERATE, realmsResetWorldScreen$ResetType) {
         public void onPress() {
            Realms.setScreen(new RealmsResetNormalWorldScreen(RealmsResetWorldScreen.this, RealmsResetWorldScreen.this.title));
         }
      });
      this.buttonsAdd(new RealmsResetWorldScreen.FrameButton(this.frame(2), RealmsConstants.row(0) + 10, getLocalizedString("mco.reset.world.upload"), -1L, RealmsResetWorldScreen.ResetType.UPLOAD, realmsResetWorldScreen$ResetType) {
         public void onPress() {
            Realms.setScreen(new RealmsSelectFileToUploadScreen(RealmsResetWorldScreen.this.serverData.id, RealmsResetWorldScreen.this.slot != -1?RealmsResetWorldScreen.this.slot:RealmsResetWorldScreen.this.serverData.activeSlot, RealmsResetWorldScreen.this));
         }
      });
      this.buttonsAdd(new RealmsResetWorldScreen.FrameButton(this.frame(3), RealmsConstants.row(0) + 10, getLocalizedString("mco.reset.world.template"), -1L, RealmsResetWorldScreen.ResetType.SURVIVAL_SPAWN, realmsResetWorldScreen$ResetType) {
         public void onPress() {
            RealmsSelectWorldTemplateScreen var1 = new RealmsSelectWorldTemplateScreen(RealmsResetWorldScreen.this, RealmsServer.WorldType.NORMAL, RealmsResetWorldScreen.this.templates);
            var1.setTitle(RealmsScreen.getLocalizedString("mco.reset.world.template"));
            Realms.setScreen(var1);
         }
      });
      this.buttonsAdd(new RealmsResetWorldScreen.FrameButton(this.frame(1), RealmsConstants.row(6) + 20, getLocalizedString("mco.reset.world.adventure"), -1L, RealmsResetWorldScreen.ResetType.ADVENTURE, realmsResetWorldScreen$ResetType) {
         public void onPress() {
            RealmsSelectWorldTemplateScreen var1 = new RealmsSelectWorldTemplateScreen(RealmsResetWorldScreen.this, RealmsServer.WorldType.ADVENTUREMAP, RealmsResetWorldScreen.this.adventuremaps);
            var1.setTitle(RealmsScreen.getLocalizedString("mco.reset.world.adventure"));
            Realms.setScreen(var1);
         }
      });
      this.buttonsAdd(new RealmsResetWorldScreen.FrameButton(this.frame(2), RealmsConstants.row(6) + 20, getLocalizedString("mco.reset.world.experience"), -1L, RealmsResetWorldScreen.ResetType.EXPERIENCE, realmsResetWorldScreen$ResetType) {
         public void onPress() {
            RealmsSelectWorldTemplateScreen var1 = new RealmsSelectWorldTemplateScreen(RealmsResetWorldScreen.this, RealmsServer.WorldType.EXPERIENCE, RealmsResetWorldScreen.this.experiences);
            var1.setTitle(RealmsScreen.getLocalizedString("mco.reset.world.experience"));
            Realms.setScreen(var1);
         }
      });
      this.buttonsAdd(new RealmsResetWorldScreen.FrameButton(this.frame(3), RealmsConstants.row(6) + 20, getLocalizedString("mco.reset.world.inspiration"), -1L, RealmsResetWorldScreen.ResetType.INSPIRATION, realmsResetWorldScreen$ResetType) {
         public void onPress() {
            RealmsSelectWorldTemplateScreen var1 = new RealmsSelectWorldTemplateScreen(RealmsResetWorldScreen.this, RealmsServer.WorldType.INSPIRATION, RealmsResetWorldScreen.this.inspirations);
            var1.setTitle(RealmsScreen.getLocalizedString("mco.reset.world.inspiration"));
            Realms.setScreen(var1);
         }
      });
      this.narrateLabels();
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if(var1 == 256) {
         Realms.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return super.mouseClicked(var1, var3, var5);
   }

   private int frame(int i) {
      return this.width() / 2 - 130 + (i - 1) * 100;
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.titleLabel.render(this);
      this.subtitleLabel.render(this);
      super.render(var1, var2, var3);
   }

   private void drawFrame(int var1, int var2, String var3, long var4, String var6, RealmsResetWorldScreen.ResetType realmsResetWorldScreen$ResetType, boolean var8, boolean var9) {
      if(var4 == -1L) {
         bind(var6);
      } else {
         RealmsTextureManager.bindWorldTemplate(String.valueOf(var4), var6);
      }

      if(var8) {
         GlStateManager.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      } else {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      RealmsScreen.blit(var1 + 2, var2 + 14, 0.0F, 0.0F, 56, 56, 56, 56);
      bind("realms:textures/gui/realms/slot_frame.png");
      if(var8) {
         GlStateManager.color4f(0.56F, 0.56F, 0.56F, 1.0F);
      } else {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      RealmsScreen.blit(var1, var2 + 12, 0.0F, 0.0F, 60, 60, 60, 60);
      this.drawCenteredString(var3, var1 + 30, var2, var8?10526880:16777215);
   }

   void callback(WorldTemplate worldTemplateToReset) {
      if(worldTemplateToReset != null) {
         if(this.slot == -1) {
            this.resetWorldWithTemplate(worldTemplateToReset);
         } else {
            switch(worldTemplateToReset.type) {
            case WORLD_TEMPLATE:
               this.typeToReset = RealmsResetWorldScreen.ResetType.SURVIVAL_SPAWN;
               break;
            case ADVENTUREMAP:
               this.typeToReset = RealmsResetWorldScreen.ResetType.ADVENTURE;
               break;
            case EXPERIENCE:
               this.typeToReset = RealmsResetWorldScreen.ResetType.EXPERIENCE;
               break;
            case INSPIRATION:
               this.typeToReset = RealmsResetWorldScreen.ResetType.INSPIRATION;
            }

            this.worldTemplateToReset = worldTemplateToReset;
            this.switchSlot();
         }
      }

   }

   private void switchSlot() {
      this.switchSlot(this);
   }

   public void switchSlot(RealmsScreen realmsScreen) {
      RealmsTasks.SwitchSlotTask var2 = new RealmsTasks.SwitchSlotTask(this.serverData.id, this.slot, realmsScreen, 100);
      RealmsLongRunningMcoTaskScreen var3 = new RealmsLongRunningMcoTaskScreen(this.lastScreen, var2);
      var3.start();
      Realms.setScreen(var3);
   }

   public void confirmResult(boolean var1, int var2) {
      if(var2 == 100 && var1) {
         switch(this.typeToReset) {
         case ADVENTURE:
         case SURVIVAL_SPAWN:
         case EXPERIENCE:
         case INSPIRATION:
            if(this.worldTemplateToReset != null) {
               this.resetWorldWithTemplate(this.worldTemplateToReset);
            }
            break;
         case GENERATE:
            if(this.worldInfoToReset != null) {
               this.triggerResetWorld(this.worldInfoToReset);
            }
            break;
         default:
            return;
         }

      } else {
         if(var1) {
            Realms.setScreen(this.returnScreen);
            if(this.confirmationId != -1) {
               this.returnScreen.confirmResult(true, this.confirmationId);
            }
         }

      }
   }

   public void resetWorldWithTemplate(WorldTemplate worldTemplate) {
      RealmsTasks.ResettingWorldTask var2 = new RealmsTasks.ResettingWorldTask(this.serverData.id, this.returnScreen, worldTemplate);
      if(this.resetTitle != null) {
         var2.setResetTitle(this.resetTitle);
      }

      if(this.confirmationId != -1) {
         var2.setConfirmationId(this.confirmationId);
      }

      RealmsLongRunningMcoTaskScreen var3 = new RealmsLongRunningMcoTaskScreen(this.lastScreen, var2);
      var3.start();
      Realms.setScreen(var3);
   }

   public void resetWorld(RealmsResetWorldScreen.ResetWorldInfo worldInfoToReset) {
      if(this.slot == -1) {
         this.triggerResetWorld(worldInfoToReset);
      } else {
         this.typeToReset = RealmsResetWorldScreen.ResetType.GENERATE;
         this.worldInfoToReset = worldInfoToReset;
         this.switchSlot();
      }

   }

   private void triggerResetWorld(RealmsResetWorldScreen.ResetWorldInfo realmsResetWorldScreen$ResetWorldInfo) {
      RealmsTasks.ResettingWorldTask var2 = new RealmsTasks.ResettingWorldTask(this.serverData.id, this.returnScreen, realmsResetWorldScreen$ResetWorldInfo.seed, realmsResetWorldScreen$ResetWorldInfo.levelType, realmsResetWorldScreen$ResetWorldInfo.generateStructures);
      if(this.resetTitle != null) {
         var2.setResetTitle(this.resetTitle);
      }

      if(this.confirmationId != -1) {
         var2.setConfirmationId(this.confirmationId);
      }

      RealmsLongRunningMcoTaskScreen var3 = new RealmsLongRunningMcoTaskScreen(this.lastScreen, var2);
      var3.start();
      Realms.setScreen(var3);
   }

   // $FF: synthetic method
   static void access$800(RealmsResetWorldScreen realmsResetWorldScreen, int var1, int var2, String var3, long var4, String var6, RealmsResetWorldScreen.ResetType realmsResetWorldScreen$ResetType, boolean var8, boolean var9) {
      realmsResetWorldScreen.drawFrame(var1, var2, var3, var4, var6, realmsResetWorldScreen$ResetType, var8, var9);
   }

   @ClientJarOnly
   abstract class FrameButton extends RealmsButton {
      private final long imageId;
      private final String image;
      private final RealmsResetWorldScreen.ResetType resetType;

      public FrameButton(int var2, int var3, String var4, long imageId, String image, RealmsResetWorldScreen.ResetType resetType) {
         super(100 + resetType.ordinal(), var2, var3, 60, 72, var4);
         this.imageId = imageId;
         this.image = image;
         this.resetType = resetType;
      }

      public void tick() {
         super.tick();
      }

      public void render(int var1, int var2, float var3) {
         super.render(var1, var2, var3);
      }

      public void renderButton(int var1, int var2, float var3) {
         RealmsResetWorldScreen.access$800(RealmsResetWorldScreen.this, this.x(), this.y(), this.getProxy().getMessage(), this.imageId, this.image, this.resetType, this.getProxy().isHovered(), this.getProxy().isMouseOver((double)var1, (double)var2));
      }
   }

   @ClientJarOnly
   static enum ResetType {
      NONE,
      GENERATE,
      UPLOAD,
      ADVENTURE,
      SURVIVAL_SPAWN,
      EXPERIENCE,
      INSPIRATION;
   }

   @ClientJarOnly
   public static class ResetWorldInfo {
      String seed;
      int levelType;
      boolean generateStructures;

      public ResetWorldInfo(String seed, int levelType, boolean generateStructures) {
         this.seed = seed;
         this.levelType = levelType;
         this.generateStructures = generateStructures;
      }
   }
}
