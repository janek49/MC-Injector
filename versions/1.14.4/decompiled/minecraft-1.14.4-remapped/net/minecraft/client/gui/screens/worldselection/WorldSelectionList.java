package net.minecraft.client.gui.screens.worldselection;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ErrorScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class WorldSelectionList extends ObjectSelectionList {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
   private static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
   private static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/world_selection.png");
   private final SelectWorldScreen screen;
   @Nullable
   private List cachedList;

   public WorldSelectionList(SelectWorldScreen screen, Minecraft minecraft, int var3, int var4, int var5, int var6, int var7, Supplier supplier, @Nullable WorldSelectionList worldSelectionList) {
      super(minecraft, var3, var4, var5, var6, var7);
      this.screen = screen;
      if(worldSelectionList != null) {
         this.cachedList = worldSelectionList.cachedList;
      }

      this.refreshList(supplier, false);
   }

   public void refreshList(Supplier supplier, boolean var2) {
      this.clearEntries();
      LevelStorageSource var3 = this.minecraft.getLevelSource();
      if(this.cachedList == null || var2) {
         try {
            this.cachedList = var3.getLevelList();
         } catch (LevelStorageException var7) {
            LOGGER.error("Couldn\'t load level list", var7);
            this.minecraft.setScreen(new ErrorScreen(new TranslatableComponent("selectWorld.unable_to_load", new Object[0]), var7.getMessage()));
            return;
         }

         Collections.sort(this.cachedList);
      }

      String var4 = ((String)supplier.get()).toLowerCase(Locale.ROOT);

      for(LevelSummary var6 : this.cachedList) {
         if(var6.getLevelName().toLowerCase(Locale.ROOT).contains(var4) || var6.getLevelId().toLowerCase(Locale.ROOT).contains(var4)) {
            this.addEntry(new WorldSelectionList.WorldListEntry(this, var6, this.minecraft.getLevelSource()));
         }
      }

   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 20;
   }

   public int getRowWidth() {
      return super.getRowWidth() + 50;
   }

   protected boolean isFocused() {
      return this.screen.getFocused() == this;
   }

   public void setSelected(@Nullable WorldSelectionList.WorldListEntry selected) {
      super.setSelected(selected);
      if(selected != null) {
         LevelSummary var2 = selected.summary;
         NarratorChatListener.INSTANCE.sayNow((new TranslatableComponent("narrator.select", new Object[]{new TranslatableComponent("narrator.select.world", new Object[]{var2.getLevelName(), new Date(var2.getLastPlayed()), var2.isHardcore()?I18n.get("gameMode.hardcore", new Object[0]):I18n.get("gameMode." + var2.getGameMode().getName(), new Object[0]), var2.hasCheats()?I18n.get("selectWorld.cheats", new Object[0]):"", var2.getWorldVersionName()})})).getString());
      }

   }

   protected void moveSelection(int i) {
      super.moveSelection(i);
      this.screen.updateButtonStatus(true);
   }

   public Optional getSelectedOpt() {
      return Optional.ofNullable(this.getSelected());
   }

   public SelectWorldScreen getScreen() {
      return this.screen;
   }

   // $FF: synthetic method
   public void setSelected(@Nullable AbstractSelectionList.Entry var1) {
      this.setSelected((WorldSelectionList.WorldListEntry)var1);
   }

   @ClientJarOnly
   public final class WorldListEntry extends ObjectSelectionList.Entry implements AutoCloseable {
      private final Minecraft minecraft;
      private final SelectWorldScreen screen;
      private final LevelSummary summary;
      private final ResourceLocation iconLocation;
      private File iconFile;
      @Nullable
      private final DynamicTexture icon;
      private long lastClickTime;

      public WorldListEntry(WorldSelectionList var2, LevelSummary summary, LevelStorageSource levelStorageSource) {
         this.screen = var2.getScreen();
         this.summary = summary;
         this.minecraft = Minecraft.getInstance();
         this.iconLocation = new ResourceLocation("worlds/" + Hashing.sha1().hashUnencodedChars(summary.getLevelId()) + "/icon");
         this.iconFile = levelStorageSource.getFile(summary.getLevelId(), "icon.png");
         if(!this.iconFile.isFile()) {
            this.iconFile = null;
         }

         this.icon = this.loadServerIcon();
      }

      public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
         String var10 = this.summary.getLevelName();
         String var11 = this.summary.getLevelId() + " (" + WorldSelectionList.DATE_FORMAT.format(new Date(this.summary.getLastPlayed())) + ")";
         if(StringUtils.isEmpty(var10)) {
            var10 = I18n.get("selectWorld.world", new Object[0]) + " " + (var1 + 1);
         }

         String var12 = "";
         if(this.summary.isRequiresConversion()) {
            var12 = I18n.get("selectWorld.conversion", new Object[0]) + " " + var12;
         } else {
            var12 = I18n.get("gameMode." + this.summary.getGameMode().getName(), new Object[0]);
            if(this.summary.isHardcore()) {
               var12 = ChatFormatting.DARK_RED + I18n.get("gameMode.hardcore", new Object[0]) + ChatFormatting.RESET;
            }

            if(this.summary.hasCheats()) {
               var12 = var12 + ", " + I18n.get("selectWorld.cheats", new Object[0]);
            }

            String var13 = this.summary.getWorldVersionName().getColoredString();
            if(this.summary.markVersionInList()) {
               if(this.summary.askToOpenWorld()) {
                  var12 = var12 + ", " + I18n.get("selectWorld.version", new Object[0]) + " " + ChatFormatting.RED + var13 + ChatFormatting.RESET;
               } else {
                  var12 = var12 + ", " + I18n.get("selectWorld.version", new Object[0]) + " " + ChatFormatting.ITALIC + var13 + ChatFormatting.RESET;
               }
            } else {
               var12 = var12 + ", " + I18n.get("selectWorld.version", new Object[0]) + " " + var13;
            }
         }

         this.minecraft.font.draw(var10, (float)(var3 + 32 + 3), (float)(var2 + 1), 16777215);
         Font var10000 = this.minecraft.font;
         float var10002 = (float)(var3 + 32 + 3);
         this.minecraft.font.getClass();
         var10000.draw(var11, var10002, (float)(var2 + 9 + 3), 8421504);
         var10000 = this.minecraft.font;
         var10002 = (float)(var3 + 32 + 3);
         this.minecraft.font.getClass();
         int var10003 = var2 + 9;
         this.minecraft.font.getClass();
         var10000.draw(var12, var10002, (float)(var10003 + 9 + 3), 8421504);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.minecraft.getTextureManager().bind(this.icon != null?this.iconLocation:WorldSelectionList.ICON_MISSING);
         GlStateManager.enableBlend();
         GuiComponent.blit(var3, var2, 0.0F, 0.0F, 32, 32, 32, 32);
         GlStateManager.disableBlend();
         if(this.minecraft.options.touchscreen || var8) {
            this.minecraft.getTextureManager().bind(WorldSelectionList.ICON_OVERLAY_LOCATION);
            GuiComponent.fill(var3, var2, var3 + 32, var2 + 32, -1601138544);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int var13 = var6 - var3;
            int var14 = var13 < 32?32:0;
            if(this.summary.markVersionInList()) {
               GuiComponent.blit(var3, var2, 32.0F, (float)var14, 32, 32, 256, 256);
               if(this.summary.isOldCustomizedWorld()) {
                  GuiComponent.blit(var3, var2, 96.0F, (float)var14, 32, 32, 256, 256);
                  if(var13 < 32) {
                     Component var15 = (new TranslatableComponent("selectWorld.tooltip.unsupported", new Object[]{this.summary.getWorldVersionName()})).withStyle(ChatFormatting.RED);
                     this.screen.setToolTip(this.minecraft.font.insertLineBreaks(var15.getColoredString(), 175));
                  }
               } else if(this.summary.askToOpenWorld()) {
                  GuiComponent.blit(var3, var2, 96.0F, (float)var14, 32, 32, 256, 256);
                  if(var13 < 32) {
                     this.screen.setToolTip(ChatFormatting.RED + I18n.get("selectWorld.tooltip.fromNewerVersion1", new Object[0]) + "\n" + ChatFormatting.RED + I18n.get("selectWorld.tooltip.fromNewerVersion2", new Object[0]));
                  }
               } else if(!SharedConstants.getCurrentVersion().isStable()) {
                  GuiComponent.blit(var3, var2, 64.0F, (float)var14, 32, 32, 256, 256);
                  if(var13 < 32) {
                     this.screen.setToolTip(ChatFormatting.GOLD + I18n.get("selectWorld.tooltip.snapshot1", new Object[0]) + "\n" + ChatFormatting.GOLD + I18n.get("selectWorld.tooltip.snapshot2", new Object[0]));
                  }
               }
            } else {
               GuiComponent.blit(var3, var2, 0.0F, (float)var14, 32, 32, 256, 256);
            }
         }

      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         WorldSelectionList.this.setSelected(this);
         this.screen.updateButtonStatus(WorldSelectionList.this.getSelectedOpt().isPresent());
         if(var1 - (double)WorldSelectionList.this.getRowLeft() <= 32.0D) {
            this.joinWorld();
            return true;
         } else if(Util.getMillis() - this.lastClickTime < 250L) {
            this.joinWorld();
            return true;
         } else {
            this.lastClickTime = Util.getMillis();
            return false;
         }
      }

      public void joinWorld() {
         if(!this.summary.shouldBackup() && !this.summary.isOldCustomizedWorld()) {
            if(this.summary.askToOpenWorld()) {
               this.minecraft.setScreen(new ConfirmScreen((b) -> {
                  if(b) {
                     try {
                        this.loadWorld();
                     } catch (Exception var3) {
                        WorldSelectionList.LOGGER.error("Failure to open \'future world\'", var3);
                        this.minecraft.setScreen(new AlertScreen(() -> {
                           this.minecraft.setScreen(this.screen);
                        }, new TranslatableComponent("selectWorld.futureworld.error.title", new Object[0]), new TranslatableComponent("selectWorld.futureworld.error.text", new Object[0])));
                     }
                  } else {
                     this.minecraft.setScreen(this.screen);
                  }

               }, new TranslatableComponent("selectWorld.versionQuestion", new Object[0]), new TranslatableComponent("selectWorld.versionWarning", new Object[]{this.summary.getWorldVersionName().getColoredString()}), I18n.get("selectWorld.versionJoinButton", new Object[0]), I18n.get("gui.cancel", new Object[0])));
            } else {
               this.loadWorld();
            }
         } else {
            Component var1 = new TranslatableComponent("selectWorld.backupQuestion", new Object[0]);
            Component var2 = new TranslatableComponent("selectWorld.backupWarning", new Object[]{this.summary.getWorldVersionName().getColoredString(), SharedConstants.getCurrentVersion().getName()});
            if(this.summary.isOldCustomizedWorld()) {
               var1 = new TranslatableComponent("selectWorld.backupQuestion.customized", new Object[0]);
               var2 = new TranslatableComponent("selectWorld.backupWarning.customized", new Object[0]);
            }

            this.minecraft.setScreen(new BackupConfirmScreen(this.screen, (var1, var2) -> {
               if(var1) {
                  String var3 = this.summary.getLevelId();
                  EditWorldScreen.makeBackupAndShowToast(this.minecraft.getLevelSource(), var3);
               }

               this.loadWorld();
            }, var1, var2, false));
         }

      }

      public void deleteWorld() {
         this.minecraft.setScreen(new ConfirmScreen((b) -> {
            if(b) {
               this.minecraft.setScreen(new ProgressScreen());
               LevelStorageSource var2 = this.minecraft.getLevelSource();
               var2.deleteLevel(this.summary.getLevelId());
               WorldSelectionList.this.refreshList(() -> {
                  return this.screen.searchBox.getValue();
               }, true);
            }

            this.minecraft.setScreen(this.screen);
         }, new TranslatableComponent("selectWorld.deleteQuestion", new Object[0]), new TranslatableComponent("selectWorld.deleteWarning", new Object[]{this.summary.getLevelName()}), I18n.get("selectWorld.deleteButton", new Object[0]), I18n.get("gui.cancel", new Object[0])));
      }

      public void editWorld() {
         this.minecraft.setScreen(new EditWorldScreen((b) -> {
            if(b) {
               WorldSelectionList.this.refreshList(() -> {
                  return this.screen.searchBox.getValue();
               }, true);
            }

            this.minecraft.setScreen(this.screen);
         }, this.summary.getLevelId()));
      }

      public void recreateWorld() {
         try {
            this.minecraft.setScreen(new ProgressScreen());
            CreateWorldScreen var1 = new CreateWorldScreen(this.screen);
            LevelStorage var2 = this.minecraft.getLevelSource().selectLevel(this.summary.getLevelId(), (MinecraftServer)null);
            LevelData var3 = var2.prepareLevel();
            if(var3 != null) {
               var1.copyFromWorld(var3);
               if(this.summary.isOldCustomizedWorld()) {
                  this.minecraft.setScreen(new ConfirmScreen((var2) -> {
                     this.minecraft.setScreen((Screen)(var2?var1:this.screen));
                  }, new TranslatableComponent("selectWorld.recreate.customized.title", new Object[0]), new TranslatableComponent("selectWorld.recreate.customized.text", new Object[0]), I18n.get("gui.proceed", new Object[0]), I18n.get("gui.cancel", new Object[0])));
               } else {
                  this.minecraft.setScreen(var1);
               }
            }
         } catch (Exception var4) {
            WorldSelectionList.LOGGER.error("Unable to recreate world", var4);
            this.minecraft.setScreen(new AlertScreen(() -> {
               this.minecraft.setScreen(this.screen);
            }, new TranslatableComponent("selectWorld.recreate.error.title", new Object[0]), new TranslatableComponent("selectWorld.recreate.error.text", new Object[0])));
         }

      }

      private void loadWorld() {
         this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         if(this.minecraft.getLevelSource().levelExists(this.summary.getLevelId())) {
            this.minecraft.selectLevel(this.summary.getLevelId(), this.summary.getLevelName(), (LevelSettings)null);
         }

      }

      @Nullable
      private DynamicTexture loadServerIcon() {
         boolean var1 = this.iconFile != null && this.iconFile.isFile();
         if(var1) {
            try {
               InputStream var2 = new FileInputStream(this.iconFile);
               Throwable var3 = null;

               DynamicTexture var6;
               try {
                  NativeImage var4 = NativeImage.read(var2);
                  Validate.validState(var4.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                  Validate.validState(var4.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                  DynamicTexture var5 = new DynamicTexture(var4);
                  this.minecraft.getTextureManager().register((ResourceLocation)this.iconLocation, (TextureObject)var5);
                  var6 = var5;
               } catch (Throwable var16) {
                  var3 = var16;
                  throw var16;
               } finally {
                  if(var2 != null) {
                     if(var3 != null) {
                        try {
                           var2.close();
                        } catch (Throwable var15) {
                           var3.addSuppressed(var15);
                        }
                     } else {
                        var2.close();
                     }
                  }

               }

               return var6;
            } catch (Throwable var18) {
               WorldSelectionList.LOGGER.error("Invalid icon for world {}", this.summary.getLevelId(), var18);
               this.iconFile = null;
               return null;
            }
         } else {
            this.minecraft.getTextureManager().release(this.iconLocation);
            return null;
         }
      }

      public void close() {
         if(this.icon != null) {
            this.icon.close();
         }

      }
   }
}
