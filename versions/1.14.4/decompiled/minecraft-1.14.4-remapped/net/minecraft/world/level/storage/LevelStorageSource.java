package net.minecraft.world.level.storage;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.McRegionUpgrader;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelStorageSource {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateTimeFormatter FORMATTER = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
   private final Path baseDir;
   private final Path backupDir;
   private final DataFixer fixerUpper;

   public LevelStorageSource(Path baseDir, Path backupDir, DataFixer fixerUpper) {
      this.fixerUpper = fixerUpper;

      try {
         Files.createDirectories(Files.exists(baseDir, new LinkOption[0])?baseDir.toRealPath(new LinkOption[0]):baseDir, new FileAttribute[0]);
      } catch (IOException var5) {
         throw new RuntimeException(var5);
      }

      this.baseDir = baseDir;
      this.backupDir = backupDir;
   }

   public String getName() {
      return "Anvil";
   }

   public List getLevelList() throws LevelStorageException {
      if(!Files.isDirectory(this.baseDir, new LinkOption[0])) {
         throw new LevelStorageException((new TranslatableComponent("selectWorld.load_folder_access", new Object[0])).getString());
      } else {
         List<LevelSummary> list = Lists.newArrayList();
         File[] vars2 = this.baseDir.toFile().listFiles();

         for(File var6 : vars2) {
            if(var6.isDirectory()) {
               String var7 = var6.getName();
               LevelData var8 = this.getDataTagFor(var7);
               if(var8 != null && (var8.getVersion() == 19132 || var8.getVersion() == 19133)) {
                  boolean var9 = var8.getVersion() != this.getStorageVersion();
                  String var10 = var8.getLevelName();
                  if(StringUtils.isEmpty(var10)) {
                     var10 = var7;
                  }

                  long var11 = 0L;
                  list.add(new LevelSummary(var8, var7, var10, 0L, var9));
               }
            }
         }

         return list;
      }
   }

   private int getStorageVersion() {
      return 19133;
   }

   public LevelStorage selectLevel(String string, @Nullable MinecraftServer minecraftServer) {
      return selectLevel(this.baseDir, this.fixerUpper, string, minecraftServer);
   }

   protected static LevelStorage selectLevel(Path path, DataFixer dataFixer, String string, @Nullable MinecraftServer minecraftServer) {
      return new LevelStorage(path.toFile(), string, minecraftServer, dataFixer);
   }

   public boolean requiresConversion(String string) {
      LevelData var2 = this.getDataTagFor(string);
      return var2 != null && var2.getVersion() != this.getStorageVersion();
   }

   public boolean convertLevel(String string, ProgressListener progressListener) {
      return McRegionUpgrader.convertLevel(this.baseDir, this.fixerUpper, string, progressListener);
   }

   @Nullable
   public LevelData getDataTagFor(String string) {
      return getDataTagFor(this.baseDir, this.fixerUpper, string);
   }

   @Nullable
   protected static LevelData getDataTagFor(Path path, DataFixer dataFixer, String string) {
      File var3 = new File(path.toFile(), string);
      if(!var3.exists()) {
         return null;
      } else {
         File var4 = new File(var3, "level.dat");
         if(var4.exists()) {
            LevelData var5 = getLevelData(var4, dataFixer);
            if(var5 != null) {
               return var5;
            }
         }

         var4 = new File(var3, "level.dat_old");
         return var4.exists()?getLevelData(var4, dataFixer):null;
      }
   }

   @Nullable
   public static LevelData getLevelData(File file, DataFixer dataFixer) {
      try {
         CompoundTag var2 = NbtIo.readCompressed(new FileInputStream(file));
         CompoundTag var3 = var2.getCompound("Data");
         CompoundTag var4 = var3.contains("Player", 10)?var3.getCompound("Player"):null;
         var3.remove("Player");
         int var5 = var3.contains("DataVersion", 99)?var3.getInt("DataVersion"):-1;
         return new LevelData(NbtUtils.update(dataFixer, DataFixTypes.LEVEL, var3, var5), dataFixer, var5, var4);
      } catch (Exception var6) {
         LOGGER.error("Exception reading {}", file, var6);
         return null;
      }
   }

   public void renameLevel(String var1, String var2) {
      File var3 = new File(this.baseDir.toFile(), var1);
      if(var3.exists()) {
         File var4 = new File(var3, "level.dat");
         if(var4.exists()) {
            try {
               CompoundTag var5 = NbtIo.readCompressed(new FileInputStream(var4));
               CompoundTag var6 = var5.getCompound("Data");
               var6.putString("LevelName", var2);
               NbtIo.writeCompressed(var5, new FileOutputStream(var4));
            } catch (Exception var7) {
               var7.printStackTrace();
            }
         }

      }
   }

   public boolean isNewLevelIdAcceptable(String string) {
      try {
         Path var2 = this.baseDir.resolve(string);
         Files.createDirectory(var2, new FileAttribute[0]);
         Files.deleteIfExists(var2);
         return true;
      } catch (IOException var3) {
         return false;
      }
   }

   public boolean deleteLevel(String string) {
      File var2 = new File(this.baseDir.toFile(), string);
      if(!var2.exists()) {
         return true;
      } else {
         LOGGER.info("Deleting level {}", string);

         for(int var3 = 1; var3 <= 5; ++var3) {
            LOGGER.info("Attempt {}...", Integer.valueOf(var3));
            if(deleteRecursive(var2.listFiles())) {
               break;
            }

            LOGGER.warn("Unsuccessful in deleting contents.");
            if(var3 < 5) {
               try {
                  Thread.sleep(500L);
               } catch (InterruptedException var5) {
                  ;
               }
            }
         }

         return var2.delete();
      }
   }

   private static boolean deleteRecursive(File[] files) {
      for(File var4 : files) {
         LOGGER.debug("Deleting {}", var4);
         if(var4.isDirectory() && !deleteRecursive(var4.listFiles())) {
            LOGGER.warn("Couldn\'t delete directory {}", var4);
            return false;
         }

         if(!var4.delete()) {
            LOGGER.warn("Couldn\'t delete file {}", var4);
            return false;
         }
      }

      return true;
   }

   public boolean levelExists(String string) {
      return Files.isDirectory(this.baseDir.resolve(string), new LinkOption[0]);
   }

   public Path getBaseDir() {
      return this.baseDir;
   }

   public File getFile(String var1, String var2) {
      return this.baseDir.resolve(var1).resolve(var2).toFile();
   }

   private Path getLevelPath(String string) {
      return this.baseDir.resolve(string);
   }

   public Path getBackupPath() {
      return this.backupDir;
   }

   public long makeWorldBackup(String string) throws IOException {
      final Path var2 = this.getLevelPath(string);
      String var3 = LocalDateTime.now().format(FORMATTER) + "_" + string;
      Path var4 = this.getBackupPath();

      try {
         Files.createDirectories(Files.exists(var4, new LinkOption[0])?var4.toRealPath(new LinkOption[0]):var4, new FileAttribute[0]);
      } catch (IOException var18) {
         throw new RuntimeException(var18);
      }

      Path var5 = var4.resolve(FileUtil.findAvailableName(var4, var3, ".zip"));
      final ZipOutputStream var6 = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(var5, new OpenOption[0])));
      Throwable var7 = null;

      try {
         final Path var8 = Paths.get(string, new String[0]);
         Files.walkFileTree(var2, new SimpleFileVisitor() {
            public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
               String var3 = var8.resolve(var2.relativize(path)).toString().replace('\\', '/');
               ZipEntry var4 = new ZipEntry(var3);
               var6.putNextEntry(var4);
               com.google.common.io.Files.asByteSource(path.toFile()).copyTo(var6);
               var6.closeEntry();
               return FileVisitResult.CONTINUE;
            }

            // $FF: synthetic method
            public FileVisitResult visitFile(Object var1, BasicFileAttributes var2x) throws IOException {
               return this.visitFile((Path)var1, var2x);
            }
         });
      } catch (Throwable var17) {
         var7 = var17;
         throw var17;
      } finally {
         if(var6 != null) {
            if(var7 != null) {
               try {
                  var6.close();
               } catch (Throwable var16) {
                  var7.addSuppressed(var16);
               }
            } else {
               var6.close();
            }
         }

      }

      return Files.size(var5);
   }
}
