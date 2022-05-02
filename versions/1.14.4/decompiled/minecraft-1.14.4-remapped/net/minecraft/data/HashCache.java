package net.minecraft.data;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HashCache {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Path path;
   private final Path cachePath;
   private int hits;
   private final Map oldCache = Maps.newHashMap();
   private final Map newCache = Maps.newHashMap();
   private final Set keep = Sets.newHashSet();

   public HashCache(Path path, String string) throws IOException {
      this.path = path;
      Path path = path.resolve(".cache");
      Files.createDirectories(path, new FileAttribute[0]);
      this.cachePath = path.resolve(string);
      this.walkOutputFiles().forEach((path) -> {
         String var10000 = (String)this.oldCache.put(path, "");
      });
      if(Files.isReadable(this.cachePath)) {
         IOUtils.readLines(Files.newInputStream(this.cachePath, new OpenOption[0]), Charsets.UTF_8).forEach((string) -> {
            int var3 = string.indexOf(32);
            this.oldCache.put(path.resolve(string.substring(var3 + 1)), string.substring(0, var3));
         });
      }

   }

   public void purgeStaleAndWrite() throws IOException {
      this.removeStale();

      Writer var1;
      try {
         var1 = Files.newBufferedWriter(this.cachePath, new OpenOption[0]);
      } catch (IOException var3) {
         LOGGER.warn("Unable write cachefile {}: {}", this.cachePath, var3.toString());
         return;
      }

      IOUtils.writeLines((Collection)this.newCache.entrySet().stream().map((map$Entry) -> {
         return (String)map$Entry.getValue() + ' ' + this.path.relativize((Path)map$Entry.getKey());
      }).collect(Collectors.toList()), System.lineSeparator(), var1);
      var1.close();
      LOGGER.debug("Caching: cache hits: {}, created: {} removed: {}", Integer.valueOf(this.hits), Integer.valueOf(this.newCache.size() - this.hits), Integer.valueOf(this.oldCache.size()));
   }

   @Nullable
   public String getHash(Path path) {
      return (String)this.oldCache.get(path);
   }

   public void putNew(Path path, String string) {
      this.newCache.put(path, string);
      if(Objects.equals(this.oldCache.remove(path), string)) {
         ++this.hits;
      }

   }

   public boolean had(Path path) {
      return this.oldCache.containsKey(path);
   }

   public void keep(Path path) {
      this.keep.add(path);
   }

   private void removeStale() throws IOException {
      this.walkOutputFiles().forEach((path) -> {
         if(this.had(path) && !this.keep.contains(path)) {
            try {
               Files.delete(path);
            } catch (IOException var3) {
               LOGGER.debug("Unable to delete: {} ({})", path, var3.toString());
            }
         }

      });
   }

   private Stream walkOutputFiles() throws IOException {
      return Files.walk(this.path, new FileVisitOption[0]).filter((path) -> {
         return !Objects.equals(this.cachePath, path) && !Files.isDirectory(path, new LinkOption[0]);
      });
   }
}
