package com.mojang.blaze3d.platform;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.WindowEventHandler;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWWindowFocusCallbackI;
import org.lwjgl.glfw.GLFWWindowPosCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@ClientJarOnly
public final class Window implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final GLFWErrorCallback defaultErrorCallback = GLFWErrorCallback.create(this::defaultErrorCallback);
   private final WindowEventHandler minecraft;
   private final ScreenManager screenManager;
   private final long window;
   private int windowedX;
   private int windowedY;
   private int windowedWidth;
   private int windowedHeight;
   private Optional preferredFullscreenVideoMode;
   private boolean fullscreen;
   private boolean actuallyFullscreen;
   private int x;
   private int y;
   private int width;
   private int height;
   private int framebufferWidth;
   private int framebufferHeight;
   private int guiScaledWidth;
   private int guiScaledHeight;
   private double guiScale;
   private String errorSection = "";
   private boolean dirty;
   private double lastDrawTime = Double.MIN_VALUE;
   private int framerateLimit;
   private boolean vsync;

   public Window(WindowEventHandler minecraft, ScreenManager screenManager, DisplayData displayData, String var4, String var5) {
      this.screenManager = screenManager;
      this.setBootGlErrorCallback();
      this.setGlErrorSection("Pre startup");
      this.minecraft = minecraft;
      Optional<VideoMode> var6 = VideoMode.read(var4);
      if(var6.isPresent()) {
         this.preferredFullscreenVideoMode = var6;
      } else if(displayData.fullscreenWidth.isPresent() && displayData.fullscreenHeight.isPresent()) {
         this.preferredFullscreenVideoMode = Optional.of(new VideoMode(displayData.fullscreenWidth.getAsInt(), displayData.fullscreenHeight.getAsInt(), 8, 8, 8, 60));
      } else {
         this.preferredFullscreenVideoMode = Optional.empty();
      }

      this.actuallyFullscreen = this.fullscreen = displayData.isFullscreen;
      Monitor var7 = screenManager.getMonitor(GLFW.glfwGetPrimaryMonitor());
      this.windowedWidth = this.width = displayData.width > 0?displayData.width:1;
      this.windowedHeight = this.height = displayData.height > 0?displayData.height:1;
      GLFW.glfwDefaultWindowHints();
      this.window = GLFW.glfwCreateWindow(this.width, this.height, var5, this.fullscreen && var7 != null?var7.getMonitor():0L, 0L);
      if(var7 != null) {
         VideoMode var8 = var7.getPreferredVidMode(this.fullscreen?this.preferredFullscreenVideoMode:Optional.empty());
         this.windowedX = this.x = var7.getX() + var8.getWidth() / 2 - this.width / 2;
         this.windowedY = this.y = var7.getY() + var8.getHeight() / 2 - this.height / 2;
      } else {
         int[] vars8 = new int[1];
         int[] vars9 = new int[1];
         GLFW.glfwGetWindowPos(this.window, vars8, vars9);
         this.windowedX = this.x = vars8[0];
         this.windowedY = this.y = vars9[0];
      }

      GLFW.glfwMakeContextCurrent(this.window);
      GL.createCapabilities();
      this.setMode();
      this.refreshFramebufferSize();
      GLFW.glfwSetFramebufferSizeCallback(this.window, this::onFramebufferResize);
      GLFW.glfwSetWindowPosCallback(this.window, this::onMove);
      GLFW.glfwSetWindowSizeCallback(this.window, this::onResize);
      GLFW.glfwSetWindowFocusCallback(this.window, this::onFocus);
   }

   public static void checkGlfwError(BiConsumer biConsumer) {
      MemoryStack var1 = MemoryStack.stackPush();
      Throwable var2 = null;

      try {
         PointerBuffer var3 = var1.mallocPointer(1);
         int var4 = GLFW.glfwGetError(var3);
         if(var4 != 0) {
            long var5 = var3.get();
            String var7 = var5 == 0L?"":MemoryUtil.memUTF8(var5);
            biConsumer.accept(Integer.valueOf(var4), var7);
         }
      } catch (Throwable var15) {
         var2 = var15;
         throw var15;
      } finally {
         if(var1 != null) {
            if(var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var14) {
                  var2.addSuppressed(var14);
               }
            } else {
               var1.close();
            }
         }

      }

   }

   public void setupGuiState(boolean b) {
      GlStateManager.clear(256, b);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.ortho(0.0D, (double)this.getWidth() / this.getGuiScale(), (double)this.getHeight() / this.getGuiScale(), 0.0D, 1000.0D, 3000.0D);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
   }

   public void setIcon(InputStream var1, InputStream var2) {
      try {
         MemoryStack var3 = MemoryStack.stackPush();
         Throwable var4 = null;

         try {
            if(var1 == null) {
               throw new FileNotFoundException("icons/icon_16x16.png");
            }

            if(var2 == null) {
               throw new FileNotFoundException("icons/icon_32x32.png");
            }

            IntBuffer var5 = var3.mallocInt(1);
            IntBuffer var6 = var3.mallocInt(1);
            IntBuffer var7 = var3.mallocInt(1);
            Buffer var8 = GLFWImage.mallocStack(2, var3);
            ByteBuffer var9 = this.readIconPixels(var1, var5, var6, var7);
            if(var9 == null) {
               throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
            }

            var8.position(0);
            var8.width(var5.get(0));
            var8.height(var6.get(0));
            var8.pixels(var9);
            ByteBuffer var10 = this.readIconPixels(var2, var5, var6, var7);
            if(var10 == null) {
               throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
            }

            var8.position(1);
            var8.width(var5.get(0));
            var8.height(var6.get(0));
            var8.pixels(var10);
            var8.position(0);
            GLFW.glfwSetWindowIcon(this.window, var8);
            STBImage.stbi_image_free(var9);
            STBImage.stbi_image_free(var10);
         } catch (Throwable var19) {
            var4 = var19;
            throw var19;
         } finally {
            if(var3 != null) {
               if(var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var18) {
                     var4.addSuppressed(var18);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (IOException var21) {
         LOGGER.error("Couldn\'t set icon", var21);
      }

   }

   @Nullable
   private ByteBuffer readIconPixels(InputStream inputStream, IntBuffer var2, IntBuffer var3, IntBuffer var4) throws IOException {
      ByteBuffer byteBuffer = null;

      ByteBuffer var6;
      try {
         byteBuffer = TextureUtil.readResource(inputStream);
         byteBuffer.rewind();
         var6 = STBImage.stbi_load_from_memory(byteBuffer, var2, var3, var4, 0);
      } finally {
         if(byteBuffer != null) {
            MemoryUtil.memFree(byteBuffer);
         }

      }

      return var6;
   }

   public void setGlErrorSection(String glErrorSection) {
      this.errorSection = glErrorSection;
   }

   private void setBootGlErrorCallback() {
      GLFW.glfwSetErrorCallback(Window::bootCrash);
   }

   private static void bootCrash(int var0, long var1) {
      throw new IllegalStateException("GLFW error " + var0 + ": " + MemoryUtil.memUTF8(var1));
   }

   public void defaultErrorCallback(int var1, long var2) {
      String var4 = MemoryUtil.memUTF8(var2);
      LOGGER.error("########## GL ERROR ##########");
      LOGGER.error("@ {}", this.errorSection);
      LOGGER.error("{}: {}", Integer.valueOf(var1), var4);
   }

   public void setDefaultGlErrorCallback() {
      GLFW.glfwSetErrorCallback(this.defaultErrorCallback).free();
   }

   public void updateVsync(boolean vsync) {
      this.vsync = vsync;
      GLFW.glfwSwapInterval(vsync?1:0);
   }

   public void close() {
      Callbacks.glfwFreeCallbacks(this.window);
      this.defaultErrorCallback.close();
      GLFW.glfwDestroyWindow(this.window);
      GLFW.glfwTerminate();
   }

   private void onMove(long var1, int x, int y) {
      this.x = x;
      this.y = y;
   }

   private void onFramebufferResize(long var1, int framebufferWidth, int framebufferHeight) {
      if(var1 == this.window) {
         int var5 = this.getWidth();
         int var6 = this.getHeight();
         if(framebufferWidth != 0 && framebufferHeight != 0) {
            this.framebufferWidth = framebufferWidth;
            this.framebufferHeight = framebufferHeight;
            if(this.getWidth() != var5 || this.getHeight() != var6) {
               this.minecraft.resizeDisplay();
            }

         }
      }
   }

   private void refreshFramebufferSize() {
      int[] vars1 = new int[1];
      int[] vars2 = new int[1];
      GLFW.glfwGetFramebufferSize(this.window, vars1, vars2);
      this.framebufferWidth = vars1[0];
      this.framebufferHeight = vars2[0];
   }

   private void onResize(long var1, int width, int height) {
      this.width = width;
      this.height = height;
   }

   private void onFocus(long var1, boolean var3) {
      if(var1 == this.window) {
         this.minecraft.setWindowActive(var3);
      }

   }

   public void setFramerateLimit(int framerateLimit) {
      this.framerateLimit = framerateLimit;
   }

   public int getFramerateLimit() {
      return this.framerateLimit;
   }

   public void updateDisplay(boolean b) {
      GLFW.glfwSwapBuffers(this.window);
      pollEventQueue();
      if(this.fullscreen != this.actuallyFullscreen) {
         this.actuallyFullscreen = this.fullscreen;
         this.updateFullscreen(this.vsync);
      }

   }

   public void limitDisplayFPS() {
      double var1 = this.lastDrawTime + 1.0D / (double)this.getFramerateLimit();

      double var3;
      for(var3 = GLFW.glfwGetTime(); var3 < var1; var3 = GLFW.glfwGetTime()) {
         GLFW.glfwWaitEventsTimeout(var1 - var3);
      }

      this.lastDrawTime = var3;
   }

   public Optional getPreferredFullscreenVideoMode() {
      return this.preferredFullscreenVideoMode;
   }

   public void setPreferredFullscreenVideoMode(Optional preferredFullscreenVideoMode) {
      boolean var2 = !preferredFullscreenVideoMode.equals(this.preferredFullscreenVideoMode);
      this.preferredFullscreenVideoMode = preferredFullscreenVideoMode;
      if(var2) {
         this.dirty = true;
      }

   }

   public void changeFullscreenVideoMode() {
      if(this.fullscreen && this.dirty) {
         this.dirty = false;
         this.setMode();
         this.minecraft.resizeDisplay();
      }

   }

   private void setMode() {
      boolean var1 = GLFW.glfwGetWindowMonitor(this.window) != 0L;
      if(this.fullscreen) {
         Monitor var2 = this.screenManager.findBestMonitor(this);
         if(var2 == null) {
            LOGGER.warn("Failed to find suitable monitor for fullscreen mode");
            this.fullscreen = false;
         } else {
            VideoMode var3 = var2.getPreferredVidMode(this.preferredFullscreenVideoMode);
            if(!var1) {
               this.windowedX = this.x;
               this.windowedY = this.y;
               this.windowedWidth = this.width;
               this.windowedHeight = this.height;
            }

            this.x = 0;
            this.y = 0;
            this.width = var3.getWidth();
            this.height = var3.getHeight();
            GLFW.glfwSetWindowMonitor(this.window, var2.getMonitor(), this.x, this.y, this.width, this.height, var3.getRefreshRate());
         }
      } else {
         this.x = this.windowedX;
         this.y = this.windowedY;
         this.width = this.windowedWidth;
         this.height = this.windowedHeight;
         GLFW.glfwSetWindowMonitor(this.window, 0L, this.x, this.y, this.width, this.height, -1);
      }

   }

   public void toggleFullScreen() {
      this.fullscreen = !this.fullscreen;
   }

   private void updateFullscreen(boolean b) {
      try {
         this.setMode();
         this.minecraft.resizeDisplay();
         this.updateVsync(b);
         this.minecraft.updateDisplay(false);
      } catch (Exception var3) {
         LOGGER.error("Couldn\'t toggle fullscreen", var3);
      }

   }

   public int calculateScale(int var1, boolean var2) {
      int var3;
      for(var3 = 1; var3 != var1 && var3 < this.framebufferWidth && var3 < this.framebufferHeight && this.framebufferWidth / (var3 + 1) >= 320 && this.framebufferHeight / (var3 + 1) >= 240; ++var3) {
         ;
      }

      if(var2 && var3 % 2 != 0) {
         ++var3;
      }

      return var3;
   }

   public void setGuiScale(double guiScale) {
      this.guiScale = guiScale;
      int var3 = (int)((double)this.framebufferWidth / guiScale);
      this.guiScaledWidth = (double)this.framebufferWidth / guiScale > (double)var3?var3 + 1:var3;
      int var4 = (int)((double)this.framebufferHeight / guiScale);
      this.guiScaledHeight = (double)this.framebufferHeight / guiScale > (double)var4?var4 + 1:var4;
   }

   public long getWindow() {
      return this.window;
   }

   public boolean isFullscreen() {
      return this.fullscreen;
   }

   public int getWidth() {
      return this.framebufferWidth;
   }

   public int getHeight() {
      return this.framebufferHeight;
   }

   public static void pollEventQueue() {
      GLFW.glfwPollEvents();
   }

   public int getScreenWidth() {
      return this.width;
   }

   public int getScreenHeight() {
      return this.height;
   }

   public int getGuiScaledWidth() {
      return this.guiScaledWidth;
   }

   public int getGuiScaledHeight() {
      return this.guiScaledHeight;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public double getGuiScale() {
      return this.guiScale;
   }

   @Nullable
   public Monitor findBestMonitor() {
      return this.screenManager.findBestMonitor(this);
   }

   public void updateRawMouseInput(boolean b) {
      InputConstants.updateRawMouseInput(this.window, b);
   }
}
