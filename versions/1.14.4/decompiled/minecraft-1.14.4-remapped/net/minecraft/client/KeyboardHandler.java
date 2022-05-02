package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.ClipboardManager;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.Locale;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.ChatOptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;

@ClientJarOnly
public class KeyboardHandler {
   private final Minecraft minecraft;
   private boolean sendRepeatsToGui;
   private final ClipboardManager clipboardManager = new ClipboardManager();
   private long debugCrashKeyTime = -1L;
   private long debugCrashKeyReportedTime = -1L;
   private long debugCrashKeyReportedCount = -1L;
   private boolean handledDebugKey;

   public KeyboardHandler(Minecraft minecraft) {
      this.minecraft = minecraft;
   }

   private void debugFeedbackTranslated(String string, Object... objects) {
      this.minecraft.gui.getChat().addMessage((new TextComponent("")).append((new TranslatableComponent("debug.prefix", new Object[0])).withStyle(new ChatFormatting[]{ChatFormatting.YELLOW, ChatFormatting.BOLD})).append(" ").append((Component)(new TranslatableComponent(string, objects))));
   }

   private void debugWarningTranslated(String string, Object... objects) {
      this.minecraft.gui.getChat().addMessage((new TextComponent("")).append((new TranslatableComponent("debug.prefix", new Object[0])).withStyle(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.BOLD})).append(" ").append((Component)(new TranslatableComponent(string, objects))));
   }

   private boolean handleDebugKeys(int i) {
      if(this.debugCrashKeyTime > 0L && this.debugCrashKeyTime < Util.getMillis() - 100L) {
         return true;
      } else {
         switch(i) {
         case 65:
            this.minecraft.levelRenderer.allChanged();
            this.debugFeedbackTranslated("debug.reload_chunks.message", new Object[0]);
            return true;
         case 66:
            boolean var2 = !this.minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes();
            this.minecraft.getEntityRenderDispatcher().setRenderHitBoxes(var2);
            this.debugFeedbackTranslated(var2?"debug.show_hitboxes.on":"debug.show_hitboxes.off", new Object[0]);
            return true;
         case 67:
            if(this.minecraft.player.isReducedDebugInfo()) {
               return false;
            }

            this.debugFeedbackTranslated("debug.copy_location.message", new Object[0]);
            this.setClipboard(String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", new Object[]{DimensionType.getName(this.minecraft.player.level.dimension.getType()), Double.valueOf(this.minecraft.player.x), Double.valueOf(this.minecraft.player.y), Double.valueOf(this.minecraft.player.z), Float.valueOf(this.minecraft.player.yRot), Float.valueOf(this.minecraft.player.xRot)}));
            return true;
         case 68:
            if(this.minecraft.gui != null) {
               this.minecraft.gui.getChat().clearMessages(false);
            }

            return true;
         case 69:
         case 74:
         case 75:
         case 76:
         case 77:
         case 79:
         case 82:
         case 83:
         default:
            return false;
         case 70:
            Option.RENDER_DISTANCE.set(this.minecraft.options, Mth.clamp((double)(this.minecraft.options.renderDistance + (Screen.hasShiftDown()?-1:1)), Option.RENDER_DISTANCE.getMinValue(), Option.RENDER_DISTANCE.getMaxValue()));
            this.debugFeedbackTranslated("debug.cycle_renderdistance.message", new Object[]{Integer.valueOf(this.minecraft.options.renderDistance)});
            return true;
         case 71:
            boolean var3 = this.minecraft.debugRenderer.switchRenderChunkborder();
            this.debugFeedbackTranslated(var3?"debug.chunk_boundaries.on":"debug.chunk_boundaries.off", new Object[0]);
            return true;
         case 72:
            this.minecraft.options.advancedItemTooltips = !this.minecraft.options.advancedItemTooltips;
            this.debugFeedbackTranslated(this.minecraft.options.advancedItemTooltips?"debug.advanced_tooltips.on":"debug.advanced_tooltips.off", new Object[0]);
            this.minecraft.options.save();
            return true;
         case 73:
            if(!this.minecraft.player.isReducedDebugInfo()) {
               this.copyRecreateCommand(this.minecraft.player.hasPermissions(2), !Screen.hasShiftDown());
            }

            return true;
         case 78:
            if(!this.minecraft.player.hasPermissions(2)) {
               this.debugFeedbackTranslated("debug.creative_spectator.error", new Object[0]);
            } else if(this.minecraft.player.isCreative()) {
               this.minecraft.player.chat("/gamemode spectator");
            } else {
               this.minecraft.player.chat("/gamemode creative");
            }

            return true;
         case 80:
            this.minecraft.options.pauseOnLostFocus = !this.minecraft.options.pauseOnLostFocus;
            this.minecraft.options.save();
            this.debugFeedbackTranslated(this.minecraft.options.pauseOnLostFocus?"debug.pause_focus.on":"debug.pause_focus.off", new Object[0]);
            return true;
         case 81:
            this.debugFeedbackTranslated("debug.help.message", new Object[0]);
            ChatComponent var4 = this.minecraft.gui.getChat();
            var4.addMessage(new TranslatableComponent("debug.reload_chunks.help", new Object[0]));
            var4.addMessage(new TranslatableComponent("debug.show_hitboxes.help", new Object[0]));
            var4.addMessage(new TranslatableComponent("debug.copy_location.help", new Object[0]));
            var4.addMessage(new TranslatableComponent("debug.clear_chat.help", new Object[0]));
            var4.addMessage(new TranslatableComponent("debug.cycle_renderdistance.help", new Object[0]));
            var4.addMessage(new TranslatableComponent("debug.chunk_boundaries.help", new Object[0]));
            var4.addMessage(new TranslatableComponent("debug.advanced_tooltips.help", new Object[0]));
            var4.addMessage(new TranslatableComponent("debug.inspect.help", new Object[0]));
            var4.addMessage(new TranslatableComponent("debug.creative_spectator.help", new Object[0]));
            var4.addMessage(new TranslatableComponent("debug.pause_focus.help", new Object[0]));
            var4.addMessage(new TranslatableComponent("debug.help.help", new Object[0]));
            var4.addMessage(new TranslatableComponent("debug.reload_resourcepacks.help", new Object[0]));
            var4.addMessage(new TranslatableComponent("debug.pause.help", new Object[0]));
            return true;
         case 84:
            this.debugFeedbackTranslated("debug.reload_resourcepacks.message", new Object[0]);
            this.minecraft.reloadResourcePacks();
            return true;
         }
      }
   }

   private void copyRecreateCommand(boolean var1, boolean var2) {
      HitResult var3 = this.minecraft.hitResult;
      if(var3 != null) {
         switch(var3.getType()) {
         case BLOCK:
            BlockPos var4 = ((BlockHitResult)var3).getBlockPos();
            BlockState var5 = this.minecraft.player.level.getBlockState(var4);
            if(var1) {
               if(var2) {
                  this.minecraft.player.connection.getDebugQueryHandler().queryBlockEntityTag(var4, (compoundTag) -> {
                     this.copyCreateBlockCommand(var5, var4, compoundTag);
                     this.debugFeedbackTranslated("debug.inspect.server.block", new Object[0]);
                  });
               } else {
                  BlockEntity var6 = this.minecraft.player.level.getBlockEntity(var4);
                  CompoundTag var7 = var6 != null?var6.save(new CompoundTag()):null;
                  this.copyCreateBlockCommand(var5, var4, var7);
                  this.debugFeedbackTranslated("debug.inspect.client.block", new Object[0]);
               }
            } else {
               this.copyCreateBlockCommand(var5, var4, (CompoundTag)null);
               this.debugFeedbackTranslated("debug.inspect.client.block", new Object[0]);
            }
            break;
         case ENTITY:
            Entity var4 = ((EntityHitResult)var3).getEntity();
            ResourceLocation var5 = Registry.ENTITY_TYPE.getKey(var4.getType());
            Vec3 var6 = new Vec3(var4.x, var4.y, var4.z);
            if(var1) {
               if(var2) {
                  this.minecraft.player.connection.getDebugQueryHandler().queryEntityTag(var4.getId(), (compoundTag) -> {
                     this.copyCreateEntityCommand(var5, var6, compoundTag);
                     this.debugFeedbackTranslated("debug.inspect.server.entity", new Object[0]);
                  });
               } else {
                  CompoundTag var7 = var4.saveWithoutId(new CompoundTag());
                  this.copyCreateEntityCommand(var5, var6, var7);
                  this.debugFeedbackTranslated("debug.inspect.client.entity", new Object[0]);
               }
            } else {
               this.copyCreateEntityCommand(var5, var6, (CompoundTag)null);
               this.debugFeedbackTranslated("debug.inspect.client.entity", new Object[0]);
            }
         }

      }
   }

   private void copyCreateBlockCommand(BlockState blockState, BlockPos blockPos, @Nullable CompoundTag compoundTag) {
      if(compoundTag != null) {
         compoundTag.remove("x");
         compoundTag.remove("y");
         compoundTag.remove("z");
         compoundTag.remove("id");
      }

      StringBuilder var4 = new StringBuilder(BlockStateParser.serialize(blockState));
      if(compoundTag != null) {
         var4.append(compoundTag);
      }

      String var5 = String.format(Locale.ROOT, "/setblock %d %d %d %s", new Object[]{Integer.valueOf(blockPos.getX()), Integer.valueOf(blockPos.getY()), Integer.valueOf(blockPos.getZ()), var4});
      this.setClipboard(var5);
   }

   private void copyCreateEntityCommand(ResourceLocation resourceLocation, Vec3 vec3, @Nullable CompoundTag compoundTag) {
      String var4;
      if(compoundTag != null) {
         compoundTag.remove("UUIDMost");
         compoundTag.remove("UUIDLeast");
         compoundTag.remove("Pos");
         compoundTag.remove("Dimension");
         String var5 = compoundTag.getPrettyDisplay().getString();
         var4 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", new Object[]{resourceLocation.toString(), Double.valueOf(vec3.x), Double.valueOf(vec3.y), Double.valueOf(vec3.z), var5});
      } else {
         var4 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", new Object[]{resourceLocation.toString(), Double.valueOf(vec3.x), Double.valueOf(vec3.y), Double.valueOf(vec3.z)});
      }

      this.setClipboard(var4);
   }

   public void keyPress(long var1, int var3, int var4, int var5, int var6) {
      if(var1 == this.minecraft.window.getWindow()) {
         if(this.debugCrashKeyTime > 0L) {
            if(!InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 67) || !InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 292)) {
               this.debugCrashKeyTime = -1L;
            }
         } else if(InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 67) && InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 292)) {
            this.handledDebugKey = true;
            this.debugCrashKeyTime = Util.getMillis();
            this.debugCrashKeyReportedTime = Util.getMillis();
            this.debugCrashKeyReportedCount = 0L;
         }

         ContainerEventHandler var7 = this.minecraft.screen;
         if(var5 == 1 && (!(this.minecraft.screen instanceof ControlsScreen) || ((ControlsScreen)var7).lastKeySelection <= Util.getMillis() - 20L)) {
            if(this.minecraft.options.keyFullscreen.matches(var3, var4)) {
               this.minecraft.window.toggleFullScreen();
               this.minecraft.options.fullscreen = this.minecraft.window.isFullscreen();
               return;
            }

            if(this.minecraft.options.keyScreenshot.matches(var3, var4)) {
               if(Screen.hasControlDown()) {
                  ;
               }

               Screenshot.grab(this.minecraft.gameDirectory, this.minecraft.window.getWidth(), this.minecraft.window.getHeight(), this.minecraft.getMainRenderTarget(), (component) -> {
                  this.minecraft.execute(() -> {
                     this.minecraft.gui.getChat().addMessage(component);
                  });
               });
               return;
            }
         }

         boolean var8 = var7 == null || !(var7.getFocused() instanceof EditBox) || !((EditBox)var7.getFocused()).canConsumeInput();
         if(var5 != 0 && var3 == 66 && Screen.hasControlDown() && var8) {
            Option.NARRATOR.toggle(this.minecraft.options, 1);
            if(var7 instanceof ChatOptionsScreen) {
               ((ChatOptionsScreen)var7).updateNarratorButton();
            }

            if(var7 instanceof AccessibilityOptionsScreen) {
               ((AccessibilityOptionsScreen)var7).updateNarratorButton();
            }
         }

         if(var7 != null) {
            boolean[] vars9 = new boolean[]{false};
            Screen.wrapScreenError(() -> {
               if(var5 != 1 && (var5 != 2 || !this.sendRepeatsToGui)) {
                  if(var5 == 0) {
                     vars9[0] = var7.keyReleased(var3, var4, var6);
                  }
               } else {
                  vars9[0] = var7.keyPressed(var3, var4, var6);
               }

            }, "keyPressed event handler", var7.getClass().getCanonicalName());
            if(vars9[0]) {
               return;
            }
         }

         if(this.minecraft.screen == null || this.minecraft.screen.passEvents) {
            InputConstants.Key var9 = InputConstants.getKey(var3, var4);
            if(var5 == 0) {
               KeyMapping.set(var9, false);
               if(var3 == 292) {
                  if(this.handledDebugKey) {
                     this.handledDebugKey = false;
                  } else {
                     this.minecraft.options.renderDebug = !this.minecraft.options.renderDebug;
                     this.minecraft.options.renderDebugCharts = this.minecraft.options.renderDebug && Screen.hasShiftDown();
                     this.minecraft.options.renderFpsChart = this.minecraft.options.renderDebug && Screen.hasAltDown();
                  }
               }
            } else {
               if(var3 == 293 && this.minecraft.gameRenderer != null) {
                  this.minecraft.gameRenderer.togglePostEffect();
               }

               boolean var10 = false;
               if(this.minecraft.screen == null) {
                  if(var3 == 256) {
                     boolean var11 = InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 292);
                     this.minecraft.pauseGame(var11);
                  }

                  var10 = InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 292) && this.handleDebugKeys(var3);
                  this.handledDebugKey |= var10;
                  if(var3 == 290) {
                     this.minecraft.options.hideGui = !this.minecraft.options.hideGui;
                  }
               }

               if(var10) {
                  KeyMapping.set(var9, false);
               } else {
                  KeyMapping.set(var9, true);
                  KeyMapping.click(var9);
               }

               if(this.minecraft.options.renderDebugCharts) {
                  if(var3 == 48) {
                     this.minecraft.debugFpsMeterKeyPress(0);
                  }

                  for(int var11 = 0; var11 < 9; ++var11) {
                     if(var3 == 49 + var11) {
                        this.minecraft.debugFpsMeterKeyPress(var11 + 1);
                     }
                  }
               }
            }
         }

      }
   }

   private void charTyped(long var1, int var3, int var4) {
      if(var1 == this.minecraft.window.getWindow()) {
         GuiEventListener var5 = this.minecraft.screen;
         if(var5 != null && this.minecraft.getOverlay() == null) {
            if(Character.charCount(var3) == 1) {
               Screen.wrapScreenError(() -> {
                  var5.charTyped((char)var3, var4);
               }, "charTyped event handler", var5.getClass().getCanonicalName());
            } else {
               for(char var9 : Character.toChars(var3)) {
                  Screen.wrapScreenError(() -> {
                     var5.charTyped(var9, var4);
                  }, "charTyped event handler", var5.getClass().getCanonicalName());
               }
            }

         }
      }
   }

   public void setSendRepeatsToGui(boolean sendRepeatsToGui) {
      this.sendRepeatsToGui = sendRepeatsToGui;
   }

   public void setup(long l) {
      InputConstants.setupKeyboardCallbacks(l, this::keyPress, this::charTyped);
   }

   public String getClipboard() {
      return this.clipboardManager.getClipboard(this.minecraft.window.getWindow(), (var1, var2) -> {
         if(var1 != 65545) {
            this.minecraft.window.defaultErrorCallback(var1, var2);
         }

      });
   }

   public void setClipboard(String clipboard) {
      this.clipboardManager.setClipboard(this.minecraft.window.getWindow(), clipboard);
   }

   public void tick() {
      if(this.debugCrashKeyTime > 0L) {
         long var1 = Util.getMillis();
         long var3 = 10000L - (var1 - this.debugCrashKeyTime);
         long var5 = var1 - this.debugCrashKeyReportedTime;
         if(var3 < 0L) {
            if(Screen.hasControlDown()) {
               Blaze3D.youJustLostTheGame();
            }

            throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
         }

         if(var5 >= 1000L) {
            if(this.debugCrashKeyReportedCount == 0L) {
               this.debugFeedbackTranslated("debug.crash.message", new Object[0]);
            } else {
               this.debugWarningTranslated("debug.crash.warning", new Object[]{Integer.valueOf(Mth.ceil((float)var3 / 1000.0F))});
            }

            this.debugCrashKeyReportedTime = var1;
            ++this.debugCrashKeyReportedCount;
         }
      }

   }
}
