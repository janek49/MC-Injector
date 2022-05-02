package net.minecraft.util;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.ProgressListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpUtil {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ListeningExecutorService DOWNLOAD_EXECUTOR = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool((new ThreadFactoryBuilder()).setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).setNameFormat("Downloader %d").build()));

   public static CompletableFuture downloadTo(File file, String string, Map map, int var3, @Nullable ProgressListener progressListener, Proxy proxy) {
      return CompletableFuture.supplyAsync(() -> {
         HttpURLConnection var6 = null;
         InputStream var7 = null;
         OutputStream var8 = null;
         if(progressListener != null) {
            progressListener.progressStart(new TranslatableComponent("resourcepack.downloading", new Object[0]));
            progressListener.progressStage(new TranslatableComponent("resourcepack.requesting", new Object[0]));
         }

         try {
            try {
               byte[] vars9 = new byte[4096];
               URL var10 = new URL(string);
               var6 = (HttpURLConnection)var10.openConnection(proxy);
               var6.setInstanceFollowRedirects(true);
               float var11 = 0.0F;
               float var12 = (float)map.entrySet().size();

               for(Entry<String, String> var14 : map.entrySet()) {
                  var6.setRequestProperty((String)var14.getKey(), (String)var14.getValue());
                  if(progressListener != null) {
                     progressListener.progressStagePercentage((int)(++var11 / var12 * 100.0F));
                  }
               }

               var7 = var6.getInputStream();
               var12 = (float)var6.getContentLength();
               int var13 = var6.getContentLength();
               if(progressListener != null) {
                  progressListener.progressStage(new TranslatableComponent("resourcepack.progress", new Object[]{String.format(Locale.ROOT, "%.2f", new Object[]{Float.valueOf(var12 / 1000.0F / 1000.0F)})}));
               }

               if(file.exists()) {
                  long var14 = file.length();
                  if(var14 == (long)var13) {
                     if(progressListener != null) {
                        progressListener.stop();
                     }

                     Object var16 = null;
                     return var16;
                  }

                  LOGGER.warn("Deleting {} as it does not match what we currently have ({} vs our {}).", file, Integer.valueOf(var13), Long.valueOf(var14));
                  FileUtils.deleteQuietly(file);
               } else if(file.getParentFile() != null) {
                  file.getParentFile().mkdirs();
               }

               var8 = new DataOutputStream(new FileOutputStream(file));
               if(var3 > 0 && var12 > (float)var3) {
                  if(progressListener != null) {
                     progressListener.stop();
                  }

                  throw new IOException("Filesize is bigger than maximum allowed (file is " + var11 + ", limit is " + var3 + ")");
               }

               int var14;
               while((var14 = var7.read(vars9)) >= 0) {
                  var11 += (float)var14;
                  if(progressListener != null) {
                     progressListener.progressStagePercentage((int)(var11 / var12 * 100.0F));
                  }

                  if(var3 > 0 && var11 > (float)var3) {
                     if(progressListener != null) {
                        progressListener.stop();
                     }

                     throw new IOException("Filesize was bigger than maximum allowed (got >= " + var11 + ", limit was " + var3 + ")");
                  }

                  if(Thread.interrupted()) {
                     LOGGER.error("INTERRUPTED");
                     if(progressListener != null) {
                        progressListener.stop();
                     }

                     Object var15 = null;
                     return var15;
                  }

                  var8.write(vars9, 0, var14);
               }

               if(progressListener != null) {
                  progressListener.stop();
                  return null;
               }
            } catch (Throwable var22) {
               var22.printStackTrace();
               if(var6 != null) {
                  InputStream var10 = var6.getErrorStream();

                  try {
                     LOGGER.error(IOUtils.toString(var10));
                  } catch (IOException var21) {
                     var21.printStackTrace();
                  }
               }

               if(progressListener != null) {
                  progressListener.stop();
                  return null;
               }
            }

            return null;
         } finally {
            IOUtils.closeQuietly(var7);
            IOUtils.closeQuietly(var8);
         }
      }, DOWNLOAD_EXECUTOR);
   }

   public static int getAvailablePort() {
      try {
         ServerSocket var0 = new ServerSocket(0);
         Throwable var1 = null;

         int var2;
         try {
            var2 = var0.getLocalPort();
         } catch (Throwable var12) {
            var1 = var12;
            throw var12;
         } finally {
            if(var0 != null) {
               if(var1 != null) {
                  try {
                     var0.close();
                  } catch (Throwable var11) {
                     var1.addSuppressed(var11);
                  }
               } else {
                  var0.close();
               }
            }

         }

         return var2;
      } catch (IOException var14) {
         return 25564;
      }
   }
}
