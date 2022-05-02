package com.mojang.blaze3d.platform;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

@ClientJarOnly
public class MemoryTracker {
   public static synchronized int genLists(int i) {
      int var1 = GlStateManager.genLists(i);
      if(var1 == 0) {
         int var2 = GlStateManager.getError();
         String var3 = "No error code reported";
         if(var2 != 0) {
            var3 = GLX.getErrorString(var2);
         }

         throw new IllegalStateException("glGenLists returned an ID of 0 for a count of " + i + ", GL error (" + var2 + "): " + var3);
      } else {
         return var1;
      }
   }

   public static synchronized void releaseLists(int var0, int var1) {
      GlStateManager.deleteLists(var0, var1);
   }

   public static synchronized void releaseList(int i) {
      releaseLists(i, 1);
   }

   public static synchronized ByteBuffer createByteBuffer(int i) {
      return ByteBuffer.allocateDirect(i).order(ByteOrder.nativeOrder());
   }

   public static FloatBuffer createFloatBuffer(int i) {
      return createByteBuffer(i << 2).asFloatBuffer();
   }
}
