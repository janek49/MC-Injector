package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.ProgressOption;
import net.minecraft.client.resources.language.I18n;

@ClientJarOnly
public class FullscreenResolutionProgressOption extends ProgressOption {
   public FullscreenResolutionProgressOption(Window window) {
      this(window, window.findBestMonitor());
   }

   private FullscreenResolutionProgressOption(Window window, @Nullable Monitor monitor) {
      super("options.fullscreen.resolution", -1.0D, monitor != null?(double)(monitor.getModeCount() - 1):-1.0D, 1.0F, (options) -> {
         if(monitor == null) {
            return Double.valueOf(-1.0D);
         } else {
            Optional<VideoMode> var3 = window.getPreferredFullscreenVideoMode();
            return (Double)var3.map((videoMode) -> {
               return Double.valueOf((double)monitor.getVideoModeIndex(videoMode));
            }).orElse(Double.valueOf(-1.0D));
         }
      }, (options, double) -> {
         if(monitor != null) {
            if(double.doubleValue() == -1.0D) {
               window.setPreferredFullscreenVideoMode(Optional.empty());
            } else {
               window.setPreferredFullscreenVideoMode(Optional.of(monitor.getMode(double.intValue())));
            }

         }
      }, (options, progressOption) -> {
         if(monitor == null) {
            return I18n.get("options.fullscreen.unavailable", new Object[0]);
         } else {
            double var3 = progressOption.get(options);
            String var5 = progressOption.getCaption();
            return var3 == -1.0D?var5 + I18n.get("options.fullscreen.current", new Object[0]):monitor.getMode((int)var3).toString();
         }
      });
   }
}
