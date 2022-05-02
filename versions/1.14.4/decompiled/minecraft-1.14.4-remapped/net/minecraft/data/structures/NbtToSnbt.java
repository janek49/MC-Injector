package net.minecraft.data.structures;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NbtToSnbt implements DataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final DataGenerator generator;

   public NbtToSnbt(DataGenerator generator) {
      this.generator = generator;
   }

   public void run(HashCache hashCache) throws IOException {
      Path var2 = this.generator.getOutputFolder();

      for(Path var4 : this.generator.getInputFolders()) {
         Files.walk(var4, new FileVisitOption[0]).filter((path) -> {
            return path.toString().endsWith(".nbt");
         }).forEach((var3) -> {
            this.convertStructure(var3, this.getName(var4, var3), var2);
         });
      }

   }

   public String getName() {
      return "NBT to SNBT";
   }

   private String getName(Path var1, Path var2) {
      String string = var1.relativize(var2).toString().replaceAll("\\\\", "/");
      return string.substring(0, string.length() - ".nbt".length());
   }

   private void convertStructure(Path var1, String string, Path var3) {
      try {
         CompoundTag var4 = NbtIo.readCompressed(Files.newInputStream(var1, new OpenOption[0]));
         Component var5 = var4.getPrettyDisplay("    ", 0);
         String var6 = var5.getString() + "\n";
         Path var7 = var3.resolve(string + ".snbt");
         Files.createDirectories(var7.getParent(), new FileAttribute[0]);
         BufferedWriter var8 = Files.newBufferedWriter(var7, new OpenOption[0]);
         Throwable var9 = null;

         try {
            var8.write(var6);
         } catch (Throwable var19) {
            var9 = var19;
            throw var19;
         } finally {
            if(var8 != null) {
               if(var9 != null) {
                  try {
                     var8.close();
                  } catch (Throwable var18) {
                     var9.addSuppressed(var18);
                  }
               } else {
                  var8.close();
               }
            }

         }

         LOGGER.info("Converted {} from NBT to SNBT", string);
      } catch (IOException var21) {
         LOGGER.error("Couldn\'t convert {} from NBT to SNBT at {}", string, var1, var21);
      }

   }
}
