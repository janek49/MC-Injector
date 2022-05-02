package net.minecraft.data.structures;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.TagParser;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SnbtToNbt implements DataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final DataGenerator generator;
   private final List filters = Lists.newArrayList();

   public SnbtToNbt(DataGenerator generator) {
      this.generator = generator;
   }

   public SnbtToNbt addFilter(SnbtToNbt.Filter snbtToNbt$Filter) {
      this.filters.add(snbtToNbt$Filter);
      return this;
   }

   private CompoundTag applyFilters(String string, CompoundTag var2) {
      CompoundTag var3 = var2;

      for(SnbtToNbt.Filter var5 : this.filters) {
         var3 = var5.apply(string, var3);
      }

      return var3;
   }

   public void run(HashCache hashCache) throws IOException {
      Path var2 = this.generator.getOutputFolder();

      for(Path var4 : this.generator.getInputFolders()) {
         Files.walk(var4, new FileVisitOption[0]).filter((path) -> {
            return path.toString().endsWith(".snbt");
         }).forEach((var4x) -> {
            this.convertStructure(hashCache, var4x, this.getName(var4, var4x), var2);
         });
      }

   }

   public String getName() {
      return "SNBT -> NBT";
   }

   private String getName(Path var1, Path var2) {
      String string = var1.relativize(var2).toString().replaceAll("\\\\", "/");
      return string.substring(0, string.length() - ".snbt".length());
   }

   private void convertStructure(HashCache hashCache, Path var2, String string, Path var4) {
      try {
         Path var5 = var4.resolve(string + ".nbt");
         BufferedReader var6 = Files.newBufferedReader(var2);
         Throwable var7 = null;

         try {
            String var8 = IOUtils.toString(var6);
            ByteArrayOutputStream var9 = new ByteArrayOutputStream();
            NbtIo.writeCompressed(this.applyFilters(string, TagParser.parseTag(var8)), var9);
            String var10 = SHA1.hashBytes(var9.toByteArray()).toString();
            if(!Objects.equals(hashCache.getHash(var5), var10) || !Files.exists(var5, new LinkOption[0])) {
               Files.createDirectories(var5.getParent(), new FileAttribute[0]);
               OutputStream var11 = Files.newOutputStream(var5, new OpenOption[0]);
               Throwable var12 = null;

               try {
                  var11.write(var9.toByteArray());
               } catch (Throwable var39) {
                  var12 = var39;
                  throw var39;
               } finally {
                  if(var11 != null) {
                     if(var12 != null) {
                        try {
                           var11.close();
                        } catch (Throwable var38) {
                           var12.addSuppressed(var38);
                        }
                     } else {
                        var11.close();
                     }
                  }

               }
            }

            hashCache.putNew(var5, var10);
         } catch (Throwable var41) {
            var7 = var41;
            throw var41;
         } finally {
            if(var6 != null) {
               if(var7 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var37) {
                     var7.addSuppressed(var37);
                  }
               } else {
                  var6.close();
               }
            }

         }
      } catch (CommandSyntaxException var43) {
         LOGGER.error("Couldn\'t convert {} from SNBT to NBT at {} as it\'s invalid SNBT", string, var2, var43);
      } catch (IOException var44) {
         LOGGER.error("Couldn\'t convert {} from SNBT to NBT at {}", string, var2, var44);
      }

   }

   @FunctionalInterface
   public interface Filter {
      CompoundTag apply(String var1, CompoundTag var2);
   }
}
