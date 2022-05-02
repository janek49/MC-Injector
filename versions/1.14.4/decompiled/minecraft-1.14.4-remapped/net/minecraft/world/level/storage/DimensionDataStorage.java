package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DimensionDataStorage {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map cache = Maps.newHashMap();
   private final DataFixer fixerUpper;
   private final File dataFolder;

   public DimensionDataStorage(File dataFolder, DataFixer fixerUpper) {
      this.fixerUpper = fixerUpper;
      this.dataFolder = dataFolder;
   }

   private File getDataFile(String string) {
      return new File(this.dataFolder, string + ".dat");
   }

   public SavedData computeIfAbsent(Supplier supplier, String string) {
      T savedData = this.get(supplier, string);
      if(savedData != null) {
         return savedData;
      } else {
         T var4 = (SavedData)supplier.get();
         this.set(var4);
         return var4;
      }
   }

   @Nullable
   public SavedData get(Supplier supplier, String string) {
      SavedData savedData = (SavedData)this.cache.get(string);
      if(savedData == null && !this.cache.containsKey(string)) {
         savedData = this.readSavedData(supplier, string);
         this.cache.put(string, savedData);
      }

      return savedData;
   }

   @Nullable
   private SavedData readSavedData(Supplier supplier, String string) {
      try {
         File var3 = this.getDataFile(string);
         if(var3.exists()) {
            T var4 = (SavedData)supplier.get();
            CompoundTag var5 = this.readTagFromDisk(string, SharedConstants.getCurrentVersion().getWorldVersion());
            var4.load(var5.getCompound("data"));
            return var4;
         }
      } catch (Exception var6) {
         LOGGER.error("Error loading saved data: {}", string, var6);
      }

      return null;
   }

   public void set(SavedData savedData) {
      this.cache.put(savedData.getId(), savedData);
   }

   public CompoundTag readTagFromDisk(String string, int var2) throws IOException {
      File var3 = this.getDataFile(string);
      PushbackInputStream var4 = new PushbackInputStream(new FileInputStream(var3), 2);
      Throwable var5 = null;

      CompoundTag var36;
      try {
         CompoundTag var6;
         if(this.isGzip(var4)) {
            var6 = NbtIo.readCompressed(var4);
         } else {
            DataInputStream var7 = new DataInputStream(var4);
            Throwable var8 = null;

            try {
               var6 = NbtIo.read(var7);
            } catch (Throwable var31) {
               var8 = var31;
               throw var31;
            } finally {
               if(var7 != null) {
                  if(var8 != null) {
                     try {
                        var7.close();
                     } catch (Throwable var30) {
                        var8.addSuppressed(var30);
                     }
                  } else {
                     var7.close();
                  }
               }

            }
         }

         int var7 = var6.contains("DataVersion", 99)?var6.getInt("DataVersion"):1343;
         var36 = NbtUtils.update(this.fixerUpper, DataFixTypes.SAVED_DATA, var6, var7, var2);
      } catch (Throwable var33) {
         var5 = var33;
         throw var33;
      } finally {
         if(var4 != null) {
            if(var5 != null) {
               try {
                  var4.close();
               } catch (Throwable var29) {
                  var5.addSuppressed(var29);
               }
            } else {
               var4.close();
            }
         }

      }

      return var36;
   }

   private boolean isGzip(PushbackInputStream pushbackInputStream) throws IOException {
      byte[] vars2 = new byte[2];
      boolean var3 = false;
      int var4 = pushbackInputStream.read(vars2, 0, 2);
      if(var4 == 2) {
         int var5 = (vars2[1] & 255) << 8 | vars2[0] & 255;
         if(var5 == '謟') {
            var3 = true;
         }
      }

      if(var4 != 0) {
         pushbackInputStream.unread(vars2, 0, var4);
      }

      return var3;
   }

   public void save() {
      for(SavedData var2 : this.cache.values()) {
         if(var2 != null) {
            var2.save(this.getDataFile(var2.getId()));
         }
      }

   }
}
