package com.mojang.realmsclient.client;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsAnvilLevelStorageSource;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsSharedConstants;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class FileDownload {
   private static final Logger LOGGER = LogManager.getLogger();
   private volatile boolean cancelled;
   private volatile boolean finished;
   private volatile boolean error;
   private volatile boolean extracting;
   private volatile File tempFile;
   private volatile File resourcePackPath;
   private volatile HttpGet request;
   private Thread currentThread;
   private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
   private static final String[] INVALID_FILE_NAMES = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

   public long contentLength(String string) {
      CloseableHttpClient var2 = null;
      HttpGet var3 = null;

      long var5;
      try {
         var3 = new HttpGet(string);
         var2 = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
         CloseableHttpResponse var4 = var2.execute(var3);
         var5 = Long.parseLong(var4.getFirstHeader("Content-Length").getValue());
         return var5;
      } catch (Throwable var16) {
         LOGGER.error("Unable to get content length for download");
         var5 = 0L;
      } finally {
         if(var3 != null) {
            var3.releaseConnection();
         }

         if(var2 != null) {
            try {
               var2.close();
            } catch (IOException var15) {
               LOGGER.error("Could not close http client", var15);
            }
         }

      }

      return var5;
   }

   public void download(final WorldDownload worldDownload, final String string, final RealmsDownloadLatestWorldScreen.DownloadStatus realmsDownloadLatestWorldScreen$DownloadStatus, final RealmsAnvilLevelStorageSource realmsAnvilLevelStorageSource) {
      if(this.currentThread == null) {
         this.currentThread = new Thread() {
            public void run() {
               // $FF: Couldn't be decompiled
            }
         };
         this.currentThread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
         this.currentThread.start();
      }
   }

   public void cancel() {
      if(this.request != null) {
         this.request.abort();
      }

      if(this.tempFile != null) {
         this.tempFile.delete();
      }

      this.cancelled = true;
   }

   public boolean isFinished() {
      return this.finished;
   }

   public boolean isError() {
      return this.error;
   }

   public boolean isExtracting() {
      return this.extracting;
   }

   public static String findAvailableFolderName(String string) {
      string = string.replaceAll("[\\./\"]", "_");

      for(String var4 : INVALID_FILE_NAMES) {
         if(string.equalsIgnoreCase(var4)) {
            string = "_" + string + "_";
         }
      }

      return string;
   }

   private void untarGzipArchive(String string, File file, RealmsAnvilLevelStorageSource realmsAnvilLevelStorageSource) throws IOException {
      // $FF: Couldn't be decompiled
   }

   @ClientJarOnly
   class DownloadCountingOutputStream extends CountingOutputStream {
      private ActionListener listener;

      public DownloadCountingOutputStream(OutputStream outputStream) {
         super(outputStream);
      }

      public void setListener(ActionListener listener) {
         this.listener = listener;
      }

      protected void afterWrite(int i) throws IOException {
         super.afterWrite(i);
         if(this.listener != null) {
            this.listener.actionPerformed(new ActionEvent(this, 0, (String)null));
         }

      }
   }

   @ClientJarOnly
   class ProgressListener implements ActionListener {
      private final String worldName;
      private final File tempFile;
      private final RealmsAnvilLevelStorageSource levelStorageSource;
      private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
      private final WorldDownload worldDownload;

      private ProgressListener(String worldName, File tempFile, RealmsAnvilLevelStorageSource levelStorageSource, RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus, WorldDownload worldDownload) {
         this.worldName = worldName;
         this.tempFile = tempFile;
         this.levelStorageSource = levelStorageSource;
         this.downloadStatus = downloadStatus;
         this.worldDownload = worldDownload;
      }

      public void actionPerformed(ActionEvent actionEvent) {
         this.downloadStatus.bytesWritten = Long.valueOf(((FileDownload.DownloadCountingOutputStream)actionEvent.getSource()).getByteCount());
         if(this.downloadStatus.bytesWritten.longValue() >= this.downloadStatus.totalBytes.longValue() && !FileDownload.this.cancelled && !FileDownload.this.error) {
            try {
               FileDownload.this.extracting = true;
               FileDownload.this.untarGzipArchive(this.worldName, this.tempFile, this.levelStorageSource);
            } catch (IOException var3) {
               FileDownload.LOGGER.error("Error extracting archive", var3);
               FileDownload.this.error = true;
            }
         }

      }
   }

   @ClientJarOnly
   class ResourcePackProgressListener implements ActionListener {
      private final File tempFile;
      private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
      private final WorldDownload worldDownload;

      private ResourcePackProgressListener(File tempFile, RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus, WorldDownload worldDownload) {
         this.tempFile = tempFile;
         this.downloadStatus = downloadStatus;
         this.worldDownload = worldDownload;
      }

      public void actionPerformed(ActionEvent actionEvent) {
         this.downloadStatus.bytesWritten = Long.valueOf(((FileDownload.DownloadCountingOutputStream)actionEvent.getSource()).getByteCount());
         if(this.downloadStatus.bytesWritten.longValue() >= this.downloadStatus.totalBytes.longValue() && !FileDownload.this.cancelled) {
            try {
               String var2 = Hashing.sha1().hashBytes(Files.toByteArray(this.tempFile)).toString();
               if(var2.equals(this.worldDownload.resourcePackHash)) {
                  FileUtils.copyFile(this.tempFile, FileDownload.this.resourcePackPath);
                  FileDownload.this.finished = true;
               } else {
                  FileDownload.LOGGER.error("Resourcepack had wrong hash (expected " + this.worldDownload.resourcePackHash + ", found " + var2 + "). Deleting it.");
                  FileUtils.deleteQuietly(this.tempFile);
                  FileDownload.this.error = true;
               }
            } catch (IOException var3) {
               FileDownload.LOGGER.error("Error copying resourcepack file", var3.getMessage());
               FileDownload.this.error = true;
            }
         }

      }
   }
}
