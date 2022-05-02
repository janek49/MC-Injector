package com.mojang.blaze3d.platform;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

@ClientJarOnly
public final class VideoMode {
   private final int width;
   private final int height;
   private final int redBits;
   private final int greenBits;
   private final int blueBits;
   private final int refreshRate;
   private static final Pattern PATTERN = Pattern.compile("(\\d+)x(\\d+)(?:@(\\d+)(?::(\\d+))?)?");

   public VideoMode(int width, int height, int redBits, int greenBits, int blueBits, int refreshRate) {
      this.width = width;
      this.height = height;
      this.redBits = redBits;
      this.greenBits = greenBits;
      this.blueBits = blueBits;
      this.refreshRate = refreshRate;
   }

   public VideoMode(Buffer gLFWVidMode$Buffer) {
      this.width = gLFWVidMode$Buffer.width();
      this.height = gLFWVidMode$Buffer.height();
      this.redBits = gLFWVidMode$Buffer.redBits();
      this.greenBits = gLFWVidMode$Buffer.greenBits();
      this.blueBits = gLFWVidMode$Buffer.blueBits();
      this.refreshRate = gLFWVidMode$Buffer.refreshRate();
   }

   public VideoMode(GLFWVidMode gLFWVidMode) {
      this.width = gLFWVidMode.width();
      this.height = gLFWVidMode.height();
      this.redBits = gLFWVidMode.redBits();
      this.greenBits = gLFWVidMode.greenBits();
      this.blueBits = gLFWVidMode.blueBits();
      this.refreshRate = gLFWVidMode.refreshRate();
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getRedBits() {
      return this.redBits;
   }

   public int getGreenBits() {
      return this.greenBits;
   }

   public int getBlueBits() {
      return this.blueBits;
   }

   public int getRefreshRate() {
      return this.refreshRate;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(object != null && this.getClass() == object.getClass()) {
         VideoMode var2 = (VideoMode)object;
         return this.width == var2.width && this.height == var2.height && this.redBits == var2.redBits && this.greenBits == var2.greenBits && this.blueBits == var2.blueBits && this.refreshRate == var2.refreshRate;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{Integer.valueOf(this.width), Integer.valueOf(this.height), Integer.valueOf(this.redBits), Integer.valueOf(this.greenBits), Integer.valueOf(this.blueBits), Integer.valueOf(this.refreshRate)});
   }

   public String toString() {
      return String.format("%sx%s@%s (%sbit)", new Object[]{Integer.valueOf(this.width), Integer.valueOf(this.height), Integer.valueOf(this.refreshRate), Integer.valueOf(this.redBits + this.greenBits + this.blueBits)});
   }

   public static Optional read(@Nullable String string) {
      if(string == null) {
         return Optional.empty();
      } else {
         try {
            Matcher var1 = PATTERN.matcher(string);
            if(var1.matches()) {
               int var2 = Integer.parseInt(var1.group(1));
               int var3 = Integer.parseInt(var1.group(2));
               String var4 = var1.group(3);
               int var5;
               if(var4 == null) {
                  var5 = 60;
               } else {
                  var5 = Integer.parseInt(var4);
               }

               String var6 = var1.group(4);
               int var7;
               if(var6 == null) {
                  var7 = 24;
               } else {
                  var7 = Integer.parseInt(var6);
               }

               int var8 = var7 / 3;
               return Optional.of(new VideoMode(var2, var3, var8, var8, var8, var5));
            }
         } catch (Exception var9) {
            ;
         }

         return Optional.empty();
      }
   }

   public String write() {
      return String.format("%sx%s@%s:%s", new Object[]{Integer.valueOf(this.width), Integer.valueOf(this.height), Integer.valueOf(this.refreshRate), Integer.valueOf(this.redBits + this.greenBits + this.blueBits)});
   }
}
