package net.minecraft.world.level.chunk.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionFileStorage;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.structure.LegacyStructureDataHandler;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class ChunkStorage extends RegionFileStorage {
   protected final DataFixer fixerUpper;
   @Nullable
   private LegacyStructureDataHandler legacyStructureHandler;

   public ChunkStorage(File file, DataFixer fixerUpper) {
      super(file);
      this.fixerUpper = fixerUpper;
   }

   public CompoundTag upgradeChunkTag(DimensionType dimensionType, Supplier supplier, CompoundTag var3) {
      int var4 = getVersion(var3);
      int var5 = 1493;
      if(var4 < 1493) {
         var3 = NbtUtils.update(this.fixerUpper, DataFixTypes.CHUNK, var3, var4, 1493);
         if(var3.getCompound("Level").getBoolean("hasLegacyStructureData")) {
            if(this.legacyStructureHandler == null) {
               this.legacyStructureHandler = LegacyStructureDataHandler.getLegacyStructureHandler(dimensionType, (DimensionDataStorage)supplier.get());
            }

            var3 = this.legacyStructureHandler.updateFromLegacy(var3);
         }
      }

      var3 = NbtUtils.update(this.fixerUpper, DataFixTypes.CHUNK, var3, Math.max(1493, var4));
      if(var4 < SharedConstants.getCurrentVersion().getWorldVersion()) {
         var3.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      }

      return var3;
   }

   public static int getVersion(CompoundTag compoundTag) {
      return compoundTag.contains("DataVersion", 99)?compoundTag.getInt("DataVersion"):-1;
   }

   public void write(ChunkPos chunkPos, CompoundTag compoundTag) throws IOException {
      super.write(chunkPos, compoundTag);
      if(this.legacyStructureHandler != null) {
         this.legacyStructureHandler.removeIndex(chunkPos.toLong());
      }

   }
}
