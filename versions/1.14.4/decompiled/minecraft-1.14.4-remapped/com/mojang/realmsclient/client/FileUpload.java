package com.mojang.realmsclient.client;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.client.UploadStatus;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.gui.screens.UploadResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class FileUpload {
   private static final Logger LOGGER = LogManager.getLogger();
   private final File file;
   private final long worldId;
   private final int slotId;
   private final UploadInfo uploadInfo;
   private final String sessionId;
   private final String username;
   private final String clientVersion;
   private final UploadStatus uploadStatus;
   private AtomicBoolean cancelled = new AtomicBoolean(false);
   private CompletableFuture uploadTask;
   private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout((int)TimeUnit.MINUTES.toMillis(10L)).setConnectTimeout((int)TimeUnit.SECONDS.toMillis(15L)).build();

   public FileUpload(File file, long worldId, int slotId, UploadInfo uploadInfo, String sessionId, String username, String clientVersion, UploadStatus uploadStatus) {
      this.file = file;
      this.worldId = worldId;
      this.slotId = slotId;
      this.uploadInfo = uploadInfo;
      this.sessionId = sessionId;
      this.username = username;
      this.clientVersion = clientVersion;
      this.uploadStatus = uploadStatus;
   }

   public void upload(Consumer consumer) {
      if(this.uploadTask == null) {
         this.uploadTask = CompletableFuture.supplyAsync(() -> {
            return this.requestUpload(0);
         });
         this.uploadTask.thenAccept(consumer);
      }
   }

   public void cancel() {
      this.cancelled.set(true);
      if(this.uploadTask != null) {
         this.uploadTask.cancel(false);
         this.uploadTask = null;
      }

   }

   private UploadResult requestUpload(int i) {
      UploadResult.Builder var2 = new UploadResult.Builder();
      if(this.cancelled.get()) {
         return var2.build();
      } else {
         this.uploadStatus.totalBytes = Long.valueOf(this.file.length());
         HttpPost var3 = new HttpPost("http://" + this.uploadInfo.getUploadEndpoint() + ":" + this.uploadInfo.getPort() + "/upload" + "/" + this.worldId + "/" + this.slotId);
         CloseableHttpClient var4 = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();

         UploadResult var8;
         try {
            this.setupRequest(var3);
            HttpResponse var5 = var4.execute(var3);
            long var6 = this.getRetryDelaySeconds(var5);
            if(!this.shouldRetry(var6, i)) {
               this.handleResponse(var5, var2);
               return var2.build();
            }

            var8 = this.retryUploadAfter(var6, i);
         } catch (Exception var12) {
            if(!this.cancelled.get()) {
               LOGGER.error("Caught exception while uploading: ", var12);
            }

            return var2.build();
         } finally {
            this.cleanup(var3, var4);
         }

         return var8;
      }
   }

   private void cleanup(HttpPost httpPost, CloseableHttpClient closeableHttpClient) {
      httpPost.releaseConnection();
      if(closeableHttpClient != null) {
         try {
            closeableHttpClient.close();
         } catch (IOException var4) {
            LOGGER.error("Failed to close Realms upload client");
         }
      }

   }

   private void setupRequest(HttpPost httpPost) throws FileNotFoundException {
      httpPost.setHeader("Cookie", "sid=" + this.sessionId + ";token=" + this.uploadInfo.getToken() + ";user=" + this.username + ";version=" + this.clientVersion);
      FileUpload.CustomInputStreamEntity var2 = new FileUpload.CustomInputStreamEntity(new FileInputStream(this.file), this.file.length(), this.uploadStatus);
      var2.setContentType("application/octet-stream");
      httpPost.setEntity(var2);
   }

   private void handleResponse(HttpResponse httpResponse, UploadResult.Builder uploadResult$Builder) throws IOException {
      int var3 = httpResponse.getStatusLine().getStatusCode();
      if(var3 == 401) {
         LOGGER.debug("Realms server returned 401: " + httpResponse.getFirstHeader("WWW-Authenticate"));
      }

      uploadResult$Builder.withStatusCode(var3);
      if(httpResponse.getEntity() != null) {
         String var4 = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
         if(var4 != null) {
            try {
               JsonParser var5 = new JsonParser();
               JsonElement var6 = var5.parse(var4).getAsJsonObject().get("errorMsg");
               Optional<String> var7 = Optional.ofNullable(var6).map(JsonElement::getAsString);
               uploadResult$Builder.withErrorMessage((String)var7.orElse((Object)null));
            } catch (Exception var8) {
               ;
            }
         }
      }

   }

   private boolean shouldRetry(long var1, int var3) {
      return var1 > 0L && var3 + 1 < 5;
   }

   private UploadResult retryUploadAfter(long var1, int var3) throws InterruptedException {
      Thread.sleep(Duration.ofSeconds(var1).toMillis());
      return this.requestUpload(var3 + 1);
   }

   private long getRetryDelaySeconds(HttpResponse httpResponse) {
      return ((Long)Optional.ofNullable(httpResponse.getFirstHeader("Retry-After")).map(Header::getValue).map(Long::valueOf).orElse(Long.valueOf(0L))).longValue();
   }

   public boolean isFinished() {
      return this.uploadTask.isDone() || this.uploadTask.isCancelled();
   }

   @ClientJarOnly
   static class CustomInputStreamEntity extends InputStreamEntity {
      private final long length;
      private final InputStream content;
      private final UploadStatus uploadStatus;

      public CustomInputStreamEntity(InputStream content, long length, UploadStatus uploadStatus) {
         super(content);
         this.content = content;
         this.length = length;
         this.uploadStatus = uploadStatus;
      }

      public void writeTo(OutputStream outputStream) throws IOException {
         Args.notNull(outputStream, "Output stream");
         InputStream var2 = this.content;

         try {
            byte[] vars3 = new byte[4096];
            int var4;
            if(this.length < 0L) {
               while((var4 = var2.read(vars3)) != -1) {
                  outputStream.write(vars3, 0, var4);
                  UploadStatus var12 = this.uploadStatus;
                  var12.bytesWritten = Long.valueOf(var12.bytesWritten.longValue() + (long)var4);
               }
            } else {
               long var5 = this.length;

               while(var5 > 0L) {
                  var4 = var2.read(vars3, 0, (int)Math.min(4096L, var5));
                  if(var4 == -1) {
                     break;
                  }

                  outputStream.write(vars3, 0, var4);
                  UploadStatus var7 = this.uploadStatus;
                  var7.bytesWritten = Long.valueOf(var7.bytesWritten.longValue() + (long)var4);
                  var5 -= (long)var4;
                  outputStream.flush();
               }
            }
         } finally {
            var2.close();
         }

      }
   }
}
