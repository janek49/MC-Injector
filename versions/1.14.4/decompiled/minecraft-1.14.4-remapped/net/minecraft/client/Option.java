package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.Window;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.client.AmbientOcclusionStatus;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.BooleanOption;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.CycleOption;
import net.minecraft.client.LogaritmicProgressOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.Options;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.ProgressOption;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.ChatVisiblity;

@ClientJarOnly
public abstract class Option {
   public static final ProgressOption BIOME_BLEND_RADIUS = new ProgressOption("options.biomeBlendRadius", 0.0D, 7.0D, 1.0F, (options) -> {
      return Double.valueOf((double)options.biomeBlendRadius);
   }, (options, double) -> {
      options.biomeBlendRadius = Mth.clamp((int)double.doubleValue(), 0, 7);
      Minecraft.getInstance().levelRenderer.allChanged();
   }, (options, progressOption) -> {
      double var2 = progressOption.get(options);
      String var4 = progressOption.getCaption();
      if(var2 == 0.0D) {
         return var4 + I18n.get("options.off", new Object[0]);
      } else {
         int var5 = (int)var2 * 2 + 1;
         return var4 + var5 + "x" + var5;
      }
   });
   public static final ProgressOption CHAT_HEIGHT_FOCUSED = new ProgressOption("options.chat.height.focused", 0.0D, 1.0D, 0.0F, (options) -> {
      return Double.valueOf(options.chatHeightFocused);
   }, (options, double) -> {
      options.chatHeightFocused = double.doubleValue();
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (options, progressOption) -> {
      double var2 = progressOption.toPct(progressOption.get(options));
      return progressOption.getCaption() + ChatComponent.getHeight(var2) + "px";
   });
   public static final ProgressOption CHAT_HEIGHT_UNFOCUSED = new ProgressOption("options.chat.height.unfocused", 0.0D, 1.0D, 0.0F, (options) -> {
      return Double.valueOf(options.chatHeightUnfocused);
   }, (options, double) -> {
      options.chatHeightUnfocused = double.doubleValue();
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (options, progressOption) -> {
      double var2 = progressOption.toPct(progressOption.get(options));
      return progressOption.getCaption() + ChatComponent.getHeight(var2) + "px";
   });
   public static final ProgressOption CHAT_OPACITY = new ProgressOption("options.chat.opacity", 0.0D, 1.0D, 0.0F, (options) -> {
      return Double.valueOf(options.chatOpacity);
   }, (options, double) -> {
      options.chatOpacity = double.doubleValue();
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (options, progressOption) -> {
      double var2 = progressOption.toPct(progressOption.get(options));
      return progressOption.getCaption() + (int)(var2 * 90.0D + 10.0D) + "%";
   });
   public static final ProgressOption CHAT_SCALE = new ProgressOption("options.chat.scale", 0.0D, 1.0D, 0.0F, (options) -> {
      return Double.valueOf(options.chatScale);
   }, (options, double) -> {
      options.chatScale = double.doubleValue();
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (options, progressOption) -> {
      double var2 = progressOption.toPct(progressOption.get(options));
      String var4 = progressOption.getCaption();
      return var2 == 0.0D?var4 + I18n.get("options.off", new Object[0]):var4 + (int)(var2 * 100.0D) + "%";
   });
   public static final ProgressOption CHAT_WIDTH = new ProgressOption("options.chat.width", 0.0D, 1.0D, 0.0F, (options) -> {
      return Double.valueOf(options.chatWidth);
   }, (options, double) -> {
      options.chatWidth = double.doubleValue();
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (options, progressOption) -> {
      double var2 = progressOption.toPct(progressOption.get(options));
      return progressOption.getCaption() + ChatComponent.getWidth(var2) + "px";
   });
   public static final ProgressOption FOV = new ProgressOption("options.fov", 30.0D, 110.0D, 1.0F, (options) -> {
      return Double.valueOf(options.fov);
   }, (options, double) -> {
      options.fov = double.doubleValue();
   }, (options, progressOption) -> {
      double var2 = progressOption.get(options);
      String var4 = progressOption.getCaption();
      return var2 == 70.0D?var4 + I18n.get("options.fov.min", new Object[0]):(var2 == progressOption.getMaxValue()?var4 + I18n.get("options.fov.max", new Object[0]):var4 + (int)var2);
   });
   public static final ProgressOption FRAMERATE_LIMIT = new ProgressOption("options.framerateLimit", 10.0D, 260.0D, 10.0F, (options) -> {
      return Double.valueOf((double)options.framerateLimit);
   }, (options, double) -> {
      options.framerateLimit = (int)double.doubleValue();
      Minecraft.getInstance().window.setFramerateLimit(options.framerateLimit);
   }, (options, progressOption) -> {
      double var2 = progressOption.get(options);
      String var4 = progressOption.getCaption();
      return var2 == progressOption.getMaxValue()?var4 + I18n.get("options.framerateLimit.max", new Object[0]):var4 + I18n.get("options.framerate", new Object[]{Integer.valueOf((int)var2)});
   });
   public static final ProgressOption GAMMA = new ProgressOption("options.gamma", 0.0D, 1.0D, 0.0F, (options) -> {
      return Double.valueOf(options.gamma);
   }, (options, double) -> {
      options.gamma = double.doubleValue();
   }, (options, progressOption) -> {
      double var2 = progressOption.toPct(progressOption.get(options));
      String var4 = progressOption.getCaption();
      return var2 == 0.0D?var4 + I18n.get("options.gamma.min", new Object[0]):(var2 == 1.0D?var4 + I18n.get("options.gamma.max", new Object[0]):var4 + "+" + (int)(var2 * 100.0D) + "%");
   });
   public static final ProgressOption MIPMAP_LEVELS = new ProgressOption("options.mipmapLevels", 0.0D, 4.0D, 1.0F, (options) -> {
      return Double.valueOf((double)options.mipmapLevels);
   }, (options, double) -> {
      options.mipmapLevels = (int)double.doubleValue();
   }, (options, progressOption) -> {
      double var2 = progressOption.get(options);
      String var4 = progressOption.getCaption();
      return var2 == 0.0D?var4 + I18n.get("options.off", new Object[0]):var4 + (int)var2;
   });
   public static final ProgressOption MOUSE_WHEEL_SENSITIVITY = new LogaritmicProgressOption("options.mouseWheelSensitivity", 0.01D, 10.0D, 0.01F, (options) -> {
      return Double.valueOf(options.mouseWheelSensitivity);
   }, (options, double) -> {
      options.mouseWheelSensitivity = double.doubleValue();
   }, (options, progressOption) -> {
      double var2 = progressOption.toPct(progressOption.get(options));
      return progressOption.getCaption() + String.format("%.2f", new Object[]{Double.valueOf(progressOption.toValue(var2))});
   });
   public static final BooleanOption RAW_MOUSE_INPUT = new BooleanOption("options.rawMouseInput", (options) -> {
      return options.rawMouseInput;
   }, (options, boolean) -> {
      options.rawMouseInput = boolean.booleanValue();
      Window var2 = Minecraft.getInstance().window;
      if(var2 != null) {
         var2.updateRawMouseInput(boolean.booleanValue());
      }

   });
   public static final ProgressOption RENDER_DISTANCE = new ProgressOption("options.renderDistance", 2.0D, 16.0D, 1.0F, (options) -> {
      return Double.valueOf((double)options.renderDistance);
   }, (options, double) -> {
      options.renderDistance = (int)double.doubleValue();
      Minecraft.getInstance().levelRenderer.needsUpdate();
   }, (options, progressOption) -> {
      double var2 = progressOption.get(options);
      return progressOption.getCaption() + I18n.get("options.chunks", new Object[]{Integer.valueOf((int)var2)});
   });
   public static final ProgressOption SENSITIVITY = new ProgressOption("options.sensitivity", 0.0D, 1.0D, 0.0F, (options) -> {
      return Double.valueOf(options.sensitivity);
   }, (options, double) -> {
      options.sensitivity = double.doubleValue();
   }, (options, progressOption) -> {
      double var2 = progressOption.toPct(progressOption.get(options));
      String var4 = progressOption.getCaption();
      return var2 == 0.0D?var4 + I18n.get("options.sensitivity.min", new Object[0]):(var2 == 1.0D?var4 + I18n.get("options.sensitivity.max", new Object[0]):var4 + (int)(var2 * 200.0D) + "%");
   });
   public static final ProgressOption TEXT_BACKGROUND_OPACITY = new ProgressOption("options.accessibility.text_background_opacity", 0.0D, 1.0D, 0.0F, (options) -> {
      return Double.valueOf(options.textBackgroundOpacity);
   }, (options, double) -> {
      options.textBackgroundOpacity = double.doubleValue();
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (options, progressOption) -> {
      return progressOption.getCaption() + (int)(progressOption.toPct(progressOption.get(options)) * 100.0D) + "%";
   });
   public static final CycleOption AMBIENT_OCCLUSION = new CycleOption("options.ao", (options, integer) -> {
      options.ambientOcclusion = AmbientOcclusionStatus.byId(options.ambientOcclusion.getId() + integer.intValue());
      Minecraft.getInstance().levelRenderer.allChanged();
   }, (options, cycleOption) -> {
      return cycleOption.getCaption() + I18n.get(options.ambientOcclusion.getKey(), new Object[0]);
   });
   public static final CycleOption ATTACK_INDICATOR = new CycleOption("options.attackIndicator", (options, integer) -> {
      options.attackIndicator = AttackIndicatorStatus.byId(options.attackIndicator.getId() + integer.intValue());
   }, (options, cycleOption) -> {
      return cycleOption.getCaption() + I18n.get(options.attackIndicator.getKey(), new Object[0]);
   });
   public static final CycleOption CHAT_VISIBILITY = new CycleOption("options.chat.visibility", (options, integer) -> {
      options.chatVisibility = ChatVisiblity.byId((options.chatVisibility.getId() + integer.intValue()) % 3);
   }, (options, cycleOption) -> {
      return cycleOption.getCaption() + I18n.get(options.chatVisibility.getKey(), new Object[0]);
   });
   public static final CycleOption GRAPHICS = new CycleOption("options.graphics", (options, integer) -> {
      options.fancyGraphics = !options.fancyGraphics;
      Minecraft.getInstance().levelRenderer.allChanged();
   }, (options, cycleOption) -> {
      return options.fancyGraphics?cycleOption.getCaption() + I18n.get("options.graphics.fancy", new Object[0]):cycleOption.getCaption() + I18n.get("options.graphics.fast", new Object[0]);
   });
   public static final CycleOption GUI_SCALE = new CycleOption("options.guiScale", (options, integer) -> {
      options.guiScale = Integer.remainderUnsigned(options.guiScale + integer.intValue(), Minecraft.getInstance().window.calculateScale(0, Minecraft.getInstance().isEnforceUnicode()) + 1);
   }, (options, cycleOption) -> {
      return cycleOption.getCaption() + (options.guiScale == 0?I18n.get("options.guiScale.auto", new Object[0]):Integer.valueOf(options.guiScale));
   });
   public static final CycleOption MAIN_HAND = new CycleOption("options.mainHand", (options, integer) -> {
      options.mainHand = options.mainHand.getOpposite();
   }, (options, cycleOption) -> {
      return cycleOption.getCaption() + options.mainHand;
   });
   public static final CycleOption NARRATOR = new CycleOption("options.narrator", (options, integer) -> {
      if(NarratorChatListener.INSTANCE.isActive()) {
         options.narratorStatus = NarratorStatus.byId(options.narratorStatus.getId() + integer.intValue());
      } else {
         options.narratorStatus = NarratorStatus.OFF;
      }

      NarratorChatListener.INSTANCE.updateNarratorStatus(options.narratorStatus);
   }, (options, cycleOption) -> {
      return NarratorChatListener.INSTANCE.isActive()?cycleOption.getCaption() + I18n.get(options.narratorStatus.getKey(), new Object[0]):cycleOption.getCaption() + I18n.get("options.narrator.notavailable", new Object[0]);
   });
   public static final CycleOption PARTICLES = new CycleOption("options.particles", (options, integer) -> {
      options.particles = ParticleStatus.byId(options.particles.getId() + integer.intValue());
   }, (options, cycleOption) -> {
      return cycleOption.getCaption() + I18n.get(options.particles.getKey(), new Object[0]);
   });
   public static final CycleOption RENDER_CLOUDS = new CycleOption("options.renderClouds", (options, integer) -> {
      options.renderClouds = CloudStatus.byId(options.renderClouds.getId() + integer.intValue());
   }, (options, cycleOption) -> {
      return cycleOption.getCaption() + I18n.get(options.renderClouds.getKey(), new Object[0]);
   });
   public static final CycleOption TEXT_BACKGROUND = new CycleOption("options.accessibility.text_background", (options, integer) -> {
      options.backgroundForChatOnly = !options.backgroundForChatOnly;
   }, (options, cycleOption) -> {
      return cycleOption.getCaption() + I18n.get(options.backgroundForChatOnly?"options.accessibility.text_background.chat":"options.accessibility.text_background.everywhere", new Object[0]);
   });
   public static final BooleanOption AUTO_JUMP = new BooleanOption("options.autoJump", (options) -> {
      return options.autoJump;
   }, (options, boolean) -> {
      options.autoJump = boolean.booleanValue();
   });
   public static final BooleanOption AUTO_SUGGESTIONS = new BooleanOption("options.autoSuggestCommands", (options) -> {
      return options.autoSuggestions;
   }, (options, boolean) -> {
      options.autoSuggestions = boolean.booleanValue();
   });
   public static final BooleanOption CHAT_COLOR = new BooleanOption("options.chat.color", (options) -> {
      return options.chatColors;
   }, (options, boolean) -> {
      options.chatColors = boolean.booleanValue();
   });
   public static final BooleanOption CHAT_LINKS = new BooleanOption("options.chat.links", (options) -> {
      return options.chatLinks;
   }, (options, boolean) -> {
      options.chatLinks = boolean.booleanValue();
   });
   public static final BooleanOption CHAT_LINKS_PROMPT = new BooleanOption("options.chat.links.prompt", (options) -> {
      return options.chatLinksPrompt;
   }, (options, boolean) -> {
      options.chatLinksPrompt = boolean.booleanValue();
   });
   public static final BooleanOption DISCRETE_MOUSE_SCROLL = new BooleanOption("options.discrete_mouse_scroll", (options) -> {
      return options.discreteMouseScroll;
   }, (options, boolean) -> {
      options.discreteMouseScroll = boolean.booleanValue();
   });
   public static final BooleanOption ENABLE_VSYNC = new BooleanOption("options.vsync", (options) -> {
      return options.enableVsync;
   }, (options, boolean) -> {
      options.enableVsync = boolean.booleanValue();
      if(Minecraft.getInstance().window != null) {
         Minecraft.getInstance().window.updateVsync(options.enableVsync);
      }

   });
   public static final BooleanOption ENTITY_SHADOWS = new BooleanOption("options.entityShadows", (options) -> {
      return options.entityShadows;
   }, (options, boolean) -> {
      options.entityShadows = boolean.booleanValue();
   });
   public static final BooleanOption FORCE_UNICODE_FONT = new BooleanOption("options.forceUnicodeFont", (options) -> {
      return options.forceUnicodeFont;
   }, (options, boolean) -> {
      options.forceUnicodeFont = boolean.booleanValue();
      Minecraft var2 = Minecraft.getInstance();
      if(var2.getFontManager() != null) {
         var2.getFontManager().setForceUnicode(options.forceUnicodeFont, Util.backgroundExecutor(), var2);
      }

   });
   public static final BooleanOption INVERT_MOUSE = new BooleanOption("options.invertMouse", (options) -> {
      return options.invertYMouse;
   }, (options, boolean) -> {
      options.invertYMouse = boolean.booleanValue();
   });
   public static final BooleanOption REALMS_NOTIFICATIONS = new BooleanOption("options.realmsNotifications", (options) -> {
      return options.realmsNotifications;
   }, (options, boolean) -> {
      options.realmsNotifications = boolean.booleanValue();
   });
   public static final BooleanOption REDUCED_DEBUG_INFO = new BooleanOption("options.reducedDebugInfo", (options) -> {
      return options.reducedDebugInfo;
   }, (options, boolean) -> {
      options.reducedDebugInfo = boolean.booleanValue();
   });
   public static final BooleanOption SHOW_SUBTITLES = new BooleanOption("options.showSubtitles", (options) -> {
      return options.showSubtitles;
   }, (options, boolean) -> {
      options.showSubtitles = boolean.booleanValue();
   });
   public static final BooleanOption SNOOPER_ENABLED = new BooleanOption("options.snooper", (options) -> {
      if(options.snooperEnabled) {
         ;
      }

      return false;
   }, (options, boolean) -> {
      options.snooperEnabled = boolean.booleanValue();
   });
   public static final BooleanOption TOUCHSCREEN = new BooleanOption("options.touchscreen", (options) -> {
      return options.touchscreen;
   }, (options, boolean) -> {
      options.touchscreen = boolean.booleanValue();
   });
   public static final BooleanOption USE_FULLSCREEN = new BooleanOption("options.fullscreen", (options) -> {
      return options.fullscreen;
   }, (options, boolean) -> {
      options.fullscreen = boolean.booleanValue();
      Minecraft var2 = Minecraft.getInstance();
      if(var2.window != null && var2.window.isFullscreen() != options.fullscreen) {
         var2.window.toggleFullScreen();
         options.fullscreen = var2.window.isFullscreen();
      }

   });
   public static final BooleanOption VIEW_BOBBING = new BooleanOption("options.viewBobbing", (options) -> {
      return options.bobView;
   }, (options, boolean) -> {
      options.bobView = boolean.booleanValue();
   });
   private final String captionId;

   public Option(String captionId) {
      this.captionId = captionId;
   }

   public abstract AbstractWidget createButton(Options var1, int var2, int var3, int var4);

   public String getCaption() {
      return I18n.get(this.captionId, new Object[0]) + ": ";
   }
}
