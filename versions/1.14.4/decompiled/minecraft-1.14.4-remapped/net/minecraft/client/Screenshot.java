package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.resources.SimpleResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class Screenshot {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

   public static void grab(File file, int var1, int var2, RenderTarget renderTarget, Consumer consumer) {
      grab(file, (String)null, var1, var2, renderTarget, consumer);
   }

   public static void grab(File file, @Nullable String string, int var2, int var3, RenderTarget renderTarget, Consumer consumer) {
      NativeImage var6 = takeScreenshot(var2, var3, renderTarget);
      File var7 = new File(file, "screenshots");
      var7.mkdir();
      File var8;
      if(string == null) {
         var8 = getFile(var7);
      } else {
         var8 = new File(var7, string);
      }

      SimpleResource.IO_EXECUTOR.execute(() -> {
         try {
            var6.writeToFile(var8);
            Component var3 = (new TextComponent(var8.getName())).withStyle(ChatFormatting.UNDERLINE).withStyle((style) -> {
               style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, var8.getAbsolutePath()));
            });
            consumer.accept(new TranslatableComponent("screenshot.success", new Object[]{var3}));
         } catch (Exception var7) {
            LOGGER.warn("Couldn\'t save screenshot", var7);
            consumer.accept(new TranslatableComponent("screenshot.failure", new Object[]{var7.getMessage()}));
         } finally {
            var6.close();
         }

      });
   }

   public static NativeImage takeScreenshot(int var0, int var1, RenderTarget renderTarget) {
      if(GLX.isUsingFBOs()) {
         var0 = renderTarget.width;
         var1 = renderTarget.height;
      }

      NativeImage nativeImage = new NativeImage(var0, var1, false);
      if(GLX.isUsingFBOs()) {
         GlStateManager.bindTexture(renderTarget.colorTextureId);
         nativeImage.downloadTexture(0, true);
      } else {
         nativeImage.downloadFrameBuffer(true);
      }

      nativeImage.flipY();
      return nativeImage;
   }

   private static File getFile(File file) {
      String var1 = DATE_FORMAT.format(new Date());
      int var2 = 1;

      while(true) {
         File var3 = new File(file, var1 + (var2 == 1?"":"_" + var2) + ".png");
         if(!var3.exists()) {
            return var3;
         }

         ++var2;
      }
   }
}
