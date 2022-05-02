package com.mojang.blaze3d.platform;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.VideoMode;
import java.util.List;
import java.util.Optional;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

@ClientJarOnly
public final class Monitor {
   private final long monitor;
   private final List videoModes;
   private VideoMode currentMode;
   private int x;
   private int y;

   public Monitor(long monitor) {
      this.monitor = monitor;
      this.videoModes = Lists.newArrayList();
      this.refreshVideoModes();
   }

   private void refreshVideoModes() {
      this.videoModes.clear();
      Buffer var1 = GLFW.glfwGetVideoModes(this.monitor);

      for(int var2 = var1.limit() - 1; var2 >= 0; --var2) {
         var1.position(var2);
         VideoMode var3 = new VideoMode(var1);
         if(var3.getRedBits() >= 8 && var3.getGreenBits() >= 8 && var3.getBlueBits() >= 8) {
            this.videoModes.add(var3);
         }
      }

      int[] vars2 = new int[1];
      int[] vars3 = new int[1];
      GLFW.glfwGetMonitorPos(this.monitor, vars2, vars3);
      this.x = vars2[0];
      this.y = vars3[0];
      GLFWVidMode var4 = GLFW.glfwGetVideoMode(this.monitor);
      this.currentMode = new VideoMode(var4);
   }

   public VideoMode getPreferredVidMode(Optional optional) {
      if(optional.isPresent()) {
         VideoMode videoMode = (VideoMode)optional.get();

         for(VideoMode var4 : this.videoModes) {
            if(var4.equals(videoMode)) {
               return var4;
            }
         }
      }

      return this.getCurrentMode();
   }

   public int getVideoModeIndex(VideoMode videoMode) {
      return this.videoModes.indexOf(videoMode);
   }

   public VideoMode getCurrentMode() {
      return this.currentMode;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public VideoMode getMode(int i) {
      return (VideoMode)this.videoModes.get(i);
   }

   public int getModeCount() {
      return this.videoModes.size();
   }

   public long getMonitor() {
      return this.monitor;
   }

   public String toString() {
      return String.format("Monitor[%s %sx%s %s]", new Object[]{Long.valueOf(this.monitor), Integer.valueOf(this.x), Integer.valueOf(this.y), this.currentMode});
   }
}
