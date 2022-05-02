package com.mojang.blaze3d.platform;

import com.fox2code.repacker.ClientJarOnly;
import java.nio.ByteBuffer;
import net.minecraft.SharedConstants;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;

@ClientJarOnly
public class ClipboardManager {
   private final ByteBuffer clipboardScratchBuffer = ByteBuffer.allocateDirect(1024);

   public String getClipboard(long var1, GLFWErrorCallbackI gLFWErrorCallbackI) {
      GLFWErrorCallback var4 = GLFW.glfwSetErrorCallback(gLFWErrorCallbackI);
      String var5 = GLFW.glfwGetClipboardString(var1);
      var5 = var5 != null?SharedConstants.filterUnicodeSupplementary(var5):"";
      GLFWErrorCallback var6 = GLFW.glfwSetErrorCallback(var4);
      if(var6 != null) {
         var6.free();
      }

      return var5;
   }

   private void setClipboard(long var1, ByteBuffer byteBuffer, String string) {
      MemoryUtil.memUTF8(string, true, byteBuffer);
      GLFW.glfwSetClipboardString(var1, byteBuffer);
   }

   public void setClipboard(long var1, String string) {
      int var4 = MemoryUtil.memLengthUTF8(string, true);
      if(var4 < this.clipboardScratchBuffer.capacity()) {
         this.setClipboard(var1, this.clipboardScratchBuffer, string);
         this.clipboardScratchBuffer.clear();
      } else {
         ByteBuffer var5 = ByteBuffer.allocateDirect(var4);
         this.setClipboard(var1, var5, string);
      }

   }
}
