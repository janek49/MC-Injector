package net.minecraft.server.packs;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractResourcePack;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.ResourcePackFileNotFoundException;
import org.apache.commons.io.IOUtils;

public class FileResourcePack extends AbstractResourcePack {
   public static final Splitter SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);
   private ZipFile zipFile;

   public FileResourcePack(File file) {
      super(file);
   }

   private ZipFile getOrCreateZipFile() throws IOException {
      if(this.zipFile == null) {
         this.zipFile = new ZipFile(this.file);
      }

      return this.zipFile;
   }

   protected InputStream getResource(String string) throws IOException {
      ZipFile var2 = this.getOrCreateZipFile();
      ZipEntry var3 = var2.getEntry(string);
      if(var3 == null) {
         throw new ResourcePackFileNotFoundException(this.file, string);
      } else {
         return var2.getInputStream(var3);
      }
   }

   public boolean hasResource(String string) {
      try {
         return this.getOrCreateZipFile().getEntry(string) != null;
      } catch (IOException var3) {
         return false;
      }
   }

   public Set getNamespaces(PackType packType) {
      ZipFile var2;
      try {
         var2 = this.getOrCreateZipFile();
      } catch (IOException var9) {
         return Collections.emptySet();
      }

      Enumeration<? extends ZipEntry> var3 = var2.entries();
      Set<String> var4 = Sets.newHashSet();

      while(var3.hasMoreElements()) {
         ZipEntry var5 = (ZipEntry)var3.nextElement();
         String var6 = var5.getName();
         if(var6.startsWith(packType.getDirectory() + "/")) {
            List<String> var7 = Lists.newArrayList(SPLITTER.split(var6));
            if(var7.size() > 1) {
               String var8 = (String)var7.get(1);
               if(var8.equals(var8.toLowerCase(Locale.ROOT))) {
                  var4.add(var8);
               } else {
                  this.logWarning(var8);
               }
            }
         }
      }

      return var4;
   }

   protected void finalize() throws Throwable {
      this.close();
      super.finalize();
   }

   public void close() {
      if(this.zipFile != null) {
         IOUtils.closeQuietly(this.zipFile);
         this.zipFile = null;
      }

   }

   public Collection getResources(PackType packType, String string, int var3, Predicate predicate) {
      ZipFile var5;
      try {
         var5 = this.getOrCreateZipFile();
      } catch (IOException var15) {
         return Collections.emptySet();
      }

      Enumeration<? extends ZipEntry> var6 = var5.entries();
      List<ResourceLocation> var7 = Lists.newArrayList();
      String var8 = packType.getDirectory() + "/";

      while(var6.hasMoreElements()) {
         ZipEntry var9 = (ZipEntry)var6.nextElement();
         if(!var9.isDirectory() && var9.getName().startsWith(var8)) {
            String var10 = var9.getName().substring(var8.length());
            if(!var10.endsWith(".mcmeta")) {
               int var11 = var10.indexOf(47);
               if(var11 >= 0) {
                  String var12 = var10.substring(var11 + 1);
                  if(var12.startsWith(string + "/")) {
                     String[] vars13 = var12.substring(string.length() + 2).split("/");
                     if(vars13.length >= var3 + 1 && predicate.test(var12)) {
                        String var14 = var10.substring(0, var11);
                        var7.add(new ResourceLocation(var14, var12));
                     }
                  }
               }
            }
         }
      }

      return var7;
   }
}
