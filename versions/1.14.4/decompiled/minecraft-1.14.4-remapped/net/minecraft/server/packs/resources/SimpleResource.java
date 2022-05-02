package net.minecraft.server.packs.resources;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleResource implements Resource {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Executor IO_EXECUTOR = Executors.newSingleThreadExecutor((new ThreadFactoryBuilder()).setDaemon(true).setNameFormat("Resource IO {0}").setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).build());
   private final String sourceName;
   private final ResourceLocation location;
   private final InputStream resourceStream;
   private final InputStream metadataStream;
   private boolean triedMetadata;
   private JsonObject metadata;

   public SimpleResource(String sourceName, ResourceLocation location, InputStream resourceStream, @Nullable InputStream metadataStream) {
      this.sourceName = sourceName;
      this.location = location;
      this.resourceStream = resourceStream;
      this.metadataStream = metadataStream;
   }

   public ResourceLocation getLocation() {
      return this.location;
   }

   public InputStream getInputStream() {
      return this.resourceStream;
   }

   public boolean hasMetadata() {
      return this.metadataStream != null;
   }

   @Nullable
   public Object getMetadata(MetadataSectionSerializer metadataSectionSerializer) {
      if(!this.hasMetadata()) {
         return null;
      } else {
         if(this.metadata == null && !this.triedMetadata) {
            this.triedMetadata = true;
            BufferedReader var2 = null;

            try {
               var2 = new BufferedReader(new InputStreamReader(this.metadataStream, StandardCharsets.UTF_8));
               this.metadata = GsonHelper.parse((Reader)var2);
            } finally {
               IOUtils.closeQuietly(var2);
            }
         }

         if(this.metadata == null) {
            return null;
         } else {
            String var2 = metadataSectionSerializer.getMetadataSectionName();
            return this.metadata.has(var2)?metadataSectionSerializer.fromJson(GsonHelper.getAsJsonObject(this.metadata, var2)):null;
         }
      }
   }

   public String getSourceName() {
      return this.sourceName;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof SimpleResource)) {
         return false;
      } else {
         SimpleResource var2 = (SimpleResource)object;
         if(this.location != null) {
            if(!this.location.equals(var2.location)) {
               return false;
            }
         } else if(var2.location != null) {
            return false;
         }

         if(this.sourceName != null) {
            if(!this.sourceName.equals(var2.sourceName)) {
               return false;
            }
         } else if(var2.sourceName != null) {
            return false;
         }

         return true;
      }
   }

   public int hashCode() {
      int var1 = this.sourceName != null?this.sourceName.hashCode():0;
      var1 = 31 * var1 + (this.location != null?this.location.hashCode():0);
      return var1;
   }

   public void close() throws IOException {
      this.resourceStream.close();
      if(this.metadataStream != null) {
         this.metadataStream.close();
      }

   }
}
