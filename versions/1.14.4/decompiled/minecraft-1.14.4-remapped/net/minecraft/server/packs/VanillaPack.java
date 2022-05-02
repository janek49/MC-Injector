package net.minecraft.server.packs;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractResourcePack;
import net.minecraft.server.packs.FolderResourcePack;
import net.minecraft.server.packs.Pack;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VanillaPack implements Pack {
   public static Path generatedDir;
   private static final Logger LOGGER = LogManager.getLogger();
   public static Class clientObject;
   private static final Map JAR_FILESYSTEM_BY_TYPE = (Map)Util.make(Maps.newHashMap(), (hashMap) -> {
      synchronized(VanillaPack.class) {
         for(PackType var5 : PackType.values()) {
            URL var6 = VanillaPack.class.getResource("/" + var5.getDirectory() + "/.mcassetsroot");

            try {
               URI var7 = var6.toURI();
               if("jar".equals(var7.getScheme())) {
                  FileSystem var8;
                  try {
                     var8 = FileSystems.getFileSystem(var7);
                  } catch (FileSystemNotFoundException var11) {
                     var8 = FileSystems.newFileSystem(var7, Collections.emptyMap());
                  }

                  hashMap.put(var5, var8);
               }
            } catch (IOException | URISyntaxException var12) {
               LOGGER.error("Couldn\'t get a list of all vanilla resources", var12);
            }
         }

      }
   });
   public final Set namespaces;

   public VanillaPack(String... strings) {
      this.namespaces = ImmutableSet.copyOf(strings);
   }

   public InputStream getRootResource(String string) throws IOException {
      if(!string.contains("/") && !string.contains("\\")) {
         if(generatedDir != null) {
            Path var2 = generatedDir.resolve(string);
            if(Files.exists(var2, new LinkOption[0])) {
               return Files.newInputStream(var2, new OpenOption[0]);
            }
         }

         return this.getResourceAsStream(string);
      } else {
         throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
      }
   }

   public InputStream getResource(PackType packType, ResourceLocation resourceLocation) throws IOException {
      InputStream inputStream = this.getResourceAsStream(packType, resourceLocation);
      if(inputStream != null) {
         return inputStream;
      } else {
         throw new FileNotFoundException(resourceLocation.getPath());
      }
   }

   public Collection getResources(PackType packType, String string, int var3, Predicate predicate) {
      Set<ResourceLocation> var5 = Sets.newHashSet();
      if(generatedDir != null) {
         try {
            var5.addAll(this.getResources(var3, "minecraft", generatedDir.resolve(packType.getDirectory()).resolve("minecraft"), string, predicate));
         } catch (IOException var14) {
            ;
         }

         if(packType == PackType.CLIENT_RESOURCES) {
            Enumeration<URL> var6 = null;

            try {
               var6 = clientObject.getClassLoader().getResources(packType.getDirectory() + "/minecraft");
            } catch (IOException var13) {
               ;
            }

            while(var6 != null && var6.hasMoreElements()) {
               try {
                  URI var7 = ((URL)var6.nextElement()).toURI();
                  if("file".equals(var7.getScheme())) {
                     var5.addAll(this.getResources(var3, "minecraft", Paths.get(var7), string, predicate));
                  }
               } catch (IOException | URISyntaxException var12) {
                  ;
               }
            }
         }
      }

      try {
         URL var6 = VanillaPack.class.getResource("/" + packType.getDirectory() + "/.mcassetsroot");
         if(var6 == null) {
            LOGGER.error("Couldn\'t find .mcassetsroot, cannot load vanilla resources");
            return var5;
         }

         URI var7 = var6.toURI();
         if("file".equals(var7.getScheme())) {
            URL var8 = new URL(var6.toString().substring(0, var6.toString().length() - ".mcassetsroot".length()) + "minecraft");
            if(var8 == null) {
               return var5;
            }

            Path var9 = Paths.get(var8.toURI());
            var5.addAll(this.getResources(var3, "minecraft", var9, string, predicate));
         } else if("jar".equals(var7.getScheme())) {
            Path var8 = ((FileSystem)JAR_FILESYSTEM_BY_TYPE.get(packType)).getPath("/" + packType.getDirectory() + "/minecraft", new String[0]);
            var5.addAll(this.getResources(var3, "minecraft", var8, string, predicate));
         } else {
            LOGGER.error("Unsupported scheme {} trying to list vanilla resources (NYI?)", var7);
         }
      } catch (NoSuchFileException | FileNotFoundException var10) {
         ;
      } catch (IOException | URISyntaxException var11) {
         LOGGER.error("Couldn\'t get a list of all vanilla resources", var11);
      }

      return var5;
   }

   private Collection getResources(int var1, String var2, Path path, String var4, Predicate predicate) throws IOException {
      List<ResourceLocation> var6 = Lists.newArrayList();

      for(Path var8 : Files.walk(path.resolve(var4), var1, new FileVisitOption[0])) {
         if(!var8.endsWith(".mcmeta") && Files.isRegularFile(var8, new LinkOption[0]) && predicate.test(var8.getFileName().toString())) {
            var6.add(new ResourceLocation(var2, path.relativize(var8).toString().replaceAll("\\\\", "/")));
         }
      }

      return var6;
   }

   @Nullable
   protected InputStream getResourceAsStream(PackType packType, ResourceLocation resourceLocation) {
      String var3 = createPath(packType, resourceLocation);
      if(generatedDir != null) {
         Path var4 = generatedDir.resolve(packType.getDirectory() + "/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath());
         if(Files.exists(var4, new LinkOption[0])) {
            try {
               return Files.newInputStream(var4, new OpenOption[0]);
            } catch (IOException var7) {
               ;
            }
         }
      }

      try {
         URL var4 = VanillaPack.class.getResource(var3);
         return isResourceUrlValid(var3, var4)?var4.openStream():null;
      } catch (IOException var6) {
         return VanillaPack.class.getResourceAsStream(var3);
      }
   }

   private static String createPath(PackType packType, ResourceLocation resourceLocation) {
      return "/" + packType.getDirectory() + "/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath();
   }

   private static boolean isResourceUrlValid(String string, @Nullable URL uRL) throws IOException {
      return uRL != null && (uRL.getProtocol().equals("jar") || FolderResourcePack.validatePath(new File(uRL.getFile()), string));
   }

   @Nullable
   protected InputStream getResourceAsStream(String string) {
      return VanillaPack.class.getResourceAsStream("/" + string);
   }

   public boolean hasResource(PackType packType, ResourceLocation resourceLocation) {
      String var3 = createPath(packType, resourceLocation);
      if(generatedDir != null) {
         Path var4 = generatedDir.resolve(packType.getDirectory() + "/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath());
         if(Files.exists(var4, new LinkOption[0])) {
            return true;
         }
      }

      try {
         URL var4 = VanillaPack.class.getResource(var3);
         return isResourceUrlValid(var3, var4);
      } catch (IOException var5) {
         return false;
      }
   }

   public Set getNamespaces(PackType packType) {
      return this.namespaces;
   }

   @Nullable
   public Object getMetadataSection(MetadataSectionSerializer metadataSectionSerializer) throws IOException {
      try {
         InputStream var2 = this.getRootResource("pack.mcmeta");
         Throwable var3 = null;

         Object var4;
         try {
            var4 = AbstractResourcePack.getMetadataFromStream(metadataSectionSerializer, var2);
         } catch (Throwable var14) {
            var3 = var14;
            throw var14;
         } finally {
            if(var2 != null) {
               if(var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var13) {
                     var3.addSuppressed(var13);
                  }
               } else {
                  var2.close();
               }
            }

         }

         return var4;
      } catch (FileNotFoundException | RuntimeException var16) {
         return null;
      }
   }

   public String getName() {
      return "Default";
   }

   public void close() {
   }
}
