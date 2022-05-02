package net.minecraft.server.packs.resources;

import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.Pack;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FallbackResourceManager implements ResourceManager {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final List fallbacks = Lists.newArrayList();
   private final PackType type;

   public FallbackResourceManager(PackType type) {
      this.type = type;
   }

   public void add(Pack pack) {
      this.fallbacks.add(pack);
   }

   public Set getNamespaces() {
      return Collections.emptySet();
   }

   public Resource getResource(ResourceLocation resourceLocation) throws IOException {
      this.validateLocation(resourceLocation);
      Pack var2 = null;
      ResourceLocation var3 = getMetadataLocation(resourceLocation);

      for(int var4 = this.fallbacks.size() - 1; var4 >= 0; --var4) {
         Pack var5 = (Pack)this.fallbacks.get(var4);
         if(var2 == null && var5.hasResource(this.type, var3)) {
            var2 = var5;
         }

         if(var5.hasResource(this.type, resourceLocation)) {
            InputStream var6 = null;
            if(var2 != null) {
               var6 = this.getWrappedResource(var3, var2);
            }

            return new SimpleResource(var5.getName(), resourceLocation, this.getWrappedResource(resourceLocation, var5), var6);
         }
      }

      throw new FileNotFoundException(resourceLocation.toString());
   }

   public boolean hasResource(ResourceLocation resourceLocation) {
      if(!this.isValidLocation(resourceLocation)) {
         return false;
      } else {
         for(int var2 = this.fallbacks.size() - 1; var2 >= 0; --var2) {
            Pack var3 = (Pack)this.fallbacks.get(var2);
            if(var3.hasResource(this.type, resourceLocation)) {
               return true;
            }
         }

         return false;
      }
   }

   protected InputStream getWrappedResource(ResourceLocation resourceLocation, Pack pack) throws IOException {
      InputStream inputStream = pack.getResource(this.type, resourceLocation);
      return (InputStream)(LOGGER.isDebugEnabled()?new FallbackResourceManager.LeakedResourceWarningInputStream(inputStream, resourceLocation, pack.getName()):inputStream);
   }

   private void validateLocation(ResourceLocation resourceLocation) throws IOException {
      if(!this.isValidLocation(resourceLocation)) {
         throw new IOException("Invalid relative path to resource: " + resourceLocation);
      }
   }

   private boolean isValidLocation(ResourceLocation resourceLocation) {
      return !resourceLocation.getPath().contains("..");
   }

   public List getResources(ResourceLocation resourceLocation) throws IOException {
      this.validateLocation(resourceLocation);
      List<Resource> list = Lists.newArrayList();
      ResourceLocation var3 = getMetadataLocation(resourceLocation);

      for(Pack var5 : this.fallbacks) {
         if(var5.hasResource(this.type, resourceLocation)) {
            InputStream var6 = var5.hasResource(this.type, var3)?this.getWrappedResource(var3, var5):null;
            list.add(new SimpleResource(var5.getName(), resourceLocation, this.getWrappedResource(resourceLocation, var5), var6));
         }
      }

      if(list.isEmpty()) {
         throw new FileNotFoundException(resourceLocation.toString());
      } else {
         return list;
      }
   }

   public Collection listResources(String string, Predicate predicate) {
      List<ResourceLocation> var3 = Lists.newArrayList();

      for(Pack var5 : this.fallbacks) {
         var3.addAll(var5.getResources(this.type, string, Integer.MAX_VALUE, predicate));
      }

      Collections.sort(var3);
      return var3;
   }

   static ResourceLocation getMetadataLocation(ResourceLocation resourceLocation) {
      return new ResourceLocation(resourceLocation.getNamespace(), resourceLocation.getPath() + ".mcmeta");
   }

   static class LeakedResourceWarningInputStream extends InputStream {
      private final InputStream wrapped;
      private final String message;
      private boolean closed;

      public LeakedResourceWarningInputStream(InputStream wrapped, ResourceLocation resourceLocation, String string) {
         this.wrapped = wrapped;
         ByteArrayOutputStream var4 = new ByteArrayOutputStream();
         (new Exception()).printStackTrace(new PrintStream(var4));
         this.message = "Leaked resource: \'" + resourceLocation + "\' loaded from pack: \'" + string + "\'\n" + var4;
      }

      public void close() throws IOException {
         this.wrapped.close();
         this.closed = true;
      }

      protected void finalize() throws Throwable {
         if(!this.closed) {
            FallbackResourceManager.LOGGER.warn(this.message);
         }

         super.finalize();
      }

      public int read() throws IOException {
         return this.wrapped.read();
      }
   }
}
