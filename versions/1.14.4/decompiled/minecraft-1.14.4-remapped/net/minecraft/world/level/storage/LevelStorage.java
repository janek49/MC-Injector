package net.minecraft.world.level.storage;

import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelConflictException;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PlayerIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LevelStorage implements PlayerIO {
   private static final Logger LOGGER = LogManager.getLogger();
   private final File worldDir;
   private final File playerDir;
   private final long sessionId = Util.getMillis();
   private final String levelId;
   private final StructureManager structureManager;
   protected final DataFixer fixerUpper;

   public LevelStorage(File file, String levelId, @Nullable MinecraftServer minecraftServer, DataFixer fixerUpper) {
      this.fixerUpper = fixerUpper;
      this.worldDir = new File(file, levelId);
      this.worldDir.mkdirs();
      this.playerDir = new File(this.worldDir, "playerdata");
      this.levelId = levelId;
      if(minecraftServer != null) {
         this.playerDir.mkdirs();
         this.structureManager = new StructureManager(minecraftServer, this.worldDir, fixerUpper);
      } else {
         this.structureManager = null;
      }

      this.initiateSession();
   }

   public void saveLevelData(LevelData levelData, @Nullable CompoundTag compoundTag) {
      levelData.setVersion(19133);
      CompoundTag compoundTag = levelData.createTag(compoundTag);
      CompoundTag var4 = new CompoundTag();
      var4.put("Data", compoundTag);

      try {
         File var5 = new File(this.worldDir, "level.dat_new");
         File var6 = new File(this.worldDir, "level.dat_old");
         File var7 = new File(this.worldDir, "level.dat");
         NbtIo.writeCompressed(var4, new FileOutputStream(var5));
         if(var6.exists()) {
            var6.delete();
         }

         var7.renameTo(var6);
         if(var7.exists()) {
            var7.delete();
         }

         var5.renameTo(var7);
         if(var5.exists()) {
            var5.delete();
         }
      } catch (Exception var8) {
         var8.printStackTrace();
      }

   }

   private void initiateSession() {
      try {
         File var1 = new File(this.worldDir, "session.lock");
         DataOutputStream var2 = new DataOutputStream(new FileOutputStream(var1));

         try {
            var2.writeLong(this.sessionId);
         } finally {
            var2.close();
         }

      } catch (IOException var7) {
         var7.printStackTrace();
         throw new RuntimeException("Failed to check session lock, aborting");
      }
   }

   public File getFolder() {
      return this.worldDir;
   }

   public void checkSession() throws LevelConflictException {
      try {
         File var1 = new File(this.worldDir, "session.lock");
         DataInputStream var2 = new DataInputStream(new FileInputStream(var1));

         try {
            if(var2.readLong() != this.sessionId) {
               throw new LevelConflictException("The save is being accessed from another location, aborting");
            }
         } finally {
            var2.close();
         }

      } catch (IOException var7) {
         throw new LevelConflictException("Failed to check session lock, aborting");
      }
   }

   @Nullable
   public LevelData prepareLevel() {
      File var1 = new File(this.worldDir, "level.dat");
      if(var1.exists()) {
         LevelData var2 = LevelStorageSource.getLevelData(var1, this.fixerUpper);
         if(var2 != null) {
            return var2;
         }
      }

      var1 = new File(this.worldDir, "level.dat_old");
      return var1.exists()?LevelStorageSource.getLevelData(var1, this.fixerUpper):null;
   }

   public void saveLevelData(LevelData levelData) {
      this.saveLevelData(levelData, (CompoundTag)null);
   }

   public void save(Player player) {
      try {
         CompoundTag var2 = player.saveWithoutId(new CompoundTag());
         File var3 = new File(this.playerDir, player.getStringUUID() + ".dat.tmp");
         File var4 = new File(this.playerDir, player.getStringUUID() + ".dat");
         NbtIo.writeCompressed(var2, new FileOutputStream(var3));
         if(var4.exists()) {
            var4.delete();
         }

         var3.renameTo(var4);
      } catch (Exception var5) {
         LOGGER.warn("Failed to save player data for {}", player.getName().getString());
      }

   }

   @Nullable
   public CompoundTag load(Player player) {
      CompoundTag compoundTag = null;

      try {
         File var3 = new File(this.playerDir, player.getStringUUID() + ".dat");
         if(var3.exists() && var3.isFile()) {
            compoundTag = NbtIo.readCompressed(new FileInputStream(var3));
         }
      } catch (Exception var4) {
         LOGGER.warn("Failed to load player data for {}", player.getName().getString());
      }

      if(compoundTag != null) {
         int var3 = compoundTag.contains("DataVersion", 3)?compoundTag.getInt("DataVersion"):-1;
         player.load(NbtUtils.update(this.fixerUpper, DataFixTypes.PLAYER, compoundTag, var3));
      }

      return compoundTag;
   }

   public String[] getSeenPlayers() {
      String[] strings = this.playerDir.list();
      if(strings == null) {
         strings = new String[0];
      }

      for(int var2 = 0; var2 < strings.length; ++var2) {
         if(strings[var2].endsWith(".dat")) {
            strings[var2] = strings[var2].substring(0, strings[var2].length() - 4);
         }
      }

      return strings;
   }

   public StructureManager getStructureManager() {
      return this.structureManager;
   }

   public DataFixer getFixerUpper() {
      return this.fixerUpper;
   }
}
