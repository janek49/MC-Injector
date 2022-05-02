package com.mojang.blaze3d.platform;

import com.fox2code.repacker.ClientJarOnly;
import java.util.OptionalInt;

@ClientJarOnly
public class DisplayData {
   public final int width;
   public final int height;
   public final OptionalInt fullscreenWidth;
   public final OptionalInt fullscreenHeight;
   public final boolean isFullscreen;

   public DisplayData(int width, int height, OptionalInt fullscreenWidth, OptionalInt fullscreenHeight, boolean isFullscreen) {
      this.width = width;
      this.height = height;
      this.fullscreenWidth = fullscreenWidth;
      this.fullscreenHeight = fullscreenHeight;
      this.isFullscreen = isFullscreen;
   }
}
