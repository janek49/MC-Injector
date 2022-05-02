package net.minecraft.client.resources;

import com.fox2code.repacker.ClientJarOnly;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.resources.AssetIndex;
import net.minecraft.resources.ResourceLocation;

@ClientJarOnly
public class DirectAssetIndex extends AssetIndex {
   private final File assetsDirectory;

   public DirectAssetIndex(File assetsDirectory) {
      this.assetsDirectory = assetsDirectory;
   }

   public File getFile(ResourceLocation resourceLocation) {
      return new File(this.assetsDirectory, resourceLocation.toString().replace(':', '/'));
   }

   public File getFile(String string) {
      return new File(this.assetsDirectory, string);
   }

   public Collection getFiles(String string, int var2, Predicate predicate) {
      Path var4 = this.assetsDirectory.toPath().resolve("minecraft/");

      try {
         Stream<Path> var5 = Files.walk(var4.resolve(string), var2, new FileVisitOption[0]);
         Throwable var6 = null;

         Collection var7;
         try {
            Stream var10000 = var5.filter((path) -> {
               return Files.isRegularFile(path, new LinkOption[0]);
            }).filter((path) -> {
               return !path.endsWith(".mcmeta");
            });
            var4.getClass();
            var7 = (Collection)var10000.map(var4::relativize).map(Object::toString).map((string) -> {
               return string.replaceAll("\\\\", "/");
            }).filter(predicate).collect(Collectors.toList());
         } catch (Throwable var18) {
            var6 = var18;
            throw var18;
         } finally {
            if(var5 != null) {
               if(var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var17) {
                     var6.addSuppressed(var17);
                  }
               } else {
                  var5.close();
               }
            }

         }

         return var7;
      } catch (NoSuchFileException var20) {
         ;
      } catch (IOException var21) {
         LOGGER.warn("Unable to getFiles on {}", string, var21);
      }

      return Collections.emptyList();
   }
}
