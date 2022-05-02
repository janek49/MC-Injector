package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureFeatureIndexSavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class LegacyStructureDataHandler {
   private static final Map CURRENT_TO_LEGACY_MAP = (Map)Util.make(Maps.newHashMap(), (hashMap) -> {
      hashMap.put("Village", "Village");
      hashMap.put("Mineshaft", "Mineshaft");
      hashMap.put("Mansion", "Mansion");
      hashMap.put("Igloo", "Temple");
      hashMap.put("Desert_Pyramid", "Temple");
      hashMap.put("Jungle_Pyramid", "Temple");
      hashMap.put("Swamp_Hut", "Temple");
      hashMap.put("Stronghold", "Stronghold");
      hashMap.put("Monument", "Monument");
      hashMap.put("Fortress", "Fortress");
      hashMap.put("EndCity", "EndCity");
   });
   private static final Map LEGACY_TO_CURRENT_MAP = (Map)Util.make(Maps.newHashMap(), (hashMap) -> {
      hashMap.put("Iglu", "Igloo");
      hashMap.put("TeDP", "Desert_Pyramid");
      hashMap.put("TeJP", "Jungle_Pyramid");
      hashMap.put("TeSH", "Swamp_Hut");
   });
   private final boolean hasLegacyData;
   private final Map dataMap = Maps.newHashMap();
   private final Map indexMap = Maps.newHashMap();
   private final List legacyKeys;
   private final List currentKeys;

   public LegacyStructureDataHandler(@Nullable DimensionDataStorage dimensionDataStorage, List legacyKeys, List currentKeys) {
      this.legacyKeys = legacyKeys;
      this.currentKeys = currentKeys;
      this.populateCaches(dimensionDataStorage);
      boolean var4 = false;

      for(String var6 : this.currentKeys) {
         var4 |= this.dataMap.get(var6) != null;
      }

      this.hasLegacyData = var4;
   }

   public void removeIndex(long l) {
      for(String var4 : this.legacyKeys) {
         StructureFeatureIndexSavedData var5 = (StructureFeatureIndexSavedData)this.indexMap.get(var4);
         if(var5 != null && var5.hasUnhandledIndex(l)) {
            var5.removeIndex(l);
            var5.setDirty();
         }
      }

   }

   public CompoundTag updateFromLegacy(CompoundTag compoundTag) {
      CompoundTag var2 = compoundTag.getCompound("Level");
      ChunkPos var3 = new ChunkPos(var2.getInt("xPos"), var2.getInt("zPos"));
      if(this.isUnhandledStructureStart(var3.x, var3.z)) {
         compoundTag = this.updateStructureStart(compoundTag, var3);
      }

      CompoundTag var4 = var2.getCompound("Structures");
      CompoundTag var5 = var4.getCompound("References");

      for(String var7 : this.currentKeys) {
         StructureFeature<?> var8 = (StructureFeature)Feature.STRUCTURES_REGISTRY.get(var7.toLowerCase(Locale.ROOT));
         if(!var5.contains(var7, 12) && var8 != null) {
            int var9 = var8.getLookupRange();
            LongList var10 = new LongArrayList();

            for(int var11 = var3.x - var9; var11 <= var3.x + var9; ++var11) {
               for(int var12 = var3.z - var9; var12 <= var3.z + var9; ++var12) {
                  if(this.hasLegacyStart(var11, var12, var7)) {
                     var10.add(ChunkPos.asLong(var11, var12));
                  }
               }
            }

            var5.putLongArray(var7, (List)var10);
         }
      }

      var4.put("References", var5);
      var2.put("Structures", var4);
      compoundTag.put("Level", var2);
      return compoundTag;
   }

   private boolean hasLegacyStart(int var1, int var2, String string) {
      return !this.hasLegacyData?false:this.dataMap.get(string) != null && ((StructureFeatureIndexSavedData)this.indexMap.get(CURRENT_TO_LEGACY_MAP.get(string))).hasStartIndex(ChunkPos.asLong(var1, var2));
   }

   private boolean isUnhandledStructureStart(int var1, int var2) {
      if(!this.hasLegacyData) {
         return false;
      } else {
         for(String var4 : this.currentKeys) {
            if(this.dataMap.get(var4) != null && ((StructureFeatureIndexSavedData)this.indexMap.get(CURRENT_TO_LEGACY_MAP.get(var4))).hasUnhandledIndex(ChunkPos.asLong(var1, var2))) {
               return true;
            }
         }

         return false;
      }
   }

   private CompoundTag updateStructureStart(CompoundTag var1, ChunkPos chunkPos) {
      CompoundTag var3 = var1.getCompound("Level");
      CompoundTag var4 = var3.getCompound("Structures");
      CompoundTag var5 = var4.getCompound("Starts");

      for(String var7 : this.currentKeys) {
         Long2ObjectMap<CompoundTag> var8 = (Long2ObjectMap)this.dataMap.get(var7);
         if(var8 != null) {
            long var9 = chunkPos.toLong();
            if(((StructureFeatureIndexSavedData)this.indexMap.get(CURRENT_TO_LEGACY_MAP.get(var7))).hasUnhandledIndex(var9)) {
               CompoundTag var11 = (CompoundTag)var8.get(var9);
               if(var11 != null) {
                  var5.put(var7, var11);
               }
            }
         }
      }

      var4.put("Starts", var5);
      var3.put("Structures", var4);
      var1.put("Level", var3);
      return var1;
   }

   private void populateCaches(@Nullable DimensionDataStorage dimensionDataStorage) {
      if(dimensionDataStorage != null) {
         for(String var3 : this.legacyKeys) {
            CompoundTag var4 = new CompoundTag();

            try {
               var4 = dimensionDataStorage.readTagFromDisk(var3, 1493).getCompound("data").getCompound("Features");
               if(var4.isEmpty()) {
                  continue;
               }
            } catch (IOException var13) {
               ;
            }

            for(String var6 : var4.getAllKeys()) {
               CompoundTag var7 = var4.getCompound(var6);
               long var8 = ChunkPos.asLong(var7.getInt("ChunkX"), var7.getInt("ChunkZ"));
               ListTag var10 = var7.getList("Children", 10);
               if(!var10.isEmpty()) {
                  String var11 = var10.getCompound(0).getString("id");
                  String var12 = (String)LEGACY_TO_CURRENT_MAP.get(var11);
                  if(var12 != null) {
                     var7.putString("id", var12);
                  }
               }

               String var11 = var7.getString("id");
               ((Long2ObjectMap)this.dataMap.computeIfAbsent(var11, (string) -> {
                  return new Long2ObjectOpenHashMap();
               })).put(var8, var7);
            }

            String var5 = var3 + "_index";
            StructureFeatureIndexSavedData var6 = (StructureFeatureIndexSavedData)dimensionDataStorage.computeIfAbsent(() -> {
               return new StructureFeatureIndexSavedData(var14);
            }, var5);
            if(!var6.getAll().isEmpty()) {
               this.indexMap.put(var3, var6);
            } else {
               StructureFeatureIndexSavedData var7 = new StructureFeatureIndexSavedData(var5);
               this.indexMap.put(var3, var7);

               for(String var9 : var4.getAllKeys()) {
                  CompoundTag var10 = var4.getCompound(var9);
                  var7.addIndex(ChunkPos.asLong(var10.getInt("ChunkX"), var10.getInt("ChunkZ")));
               }

               var7.setDirty();
            }
         }

      }
   }

   public static LegacyStructureDataHandler getLegacyStructureHandler(DimensionType dimensionType, @Nullable DimensionDataStorage dimensionDataStorage) {
      if(dimensionType == DimensionType.OVERWORLD) {
         return new LegacyStructureDataHandler(dimensionDataStorage, ImmutableList.of("Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"), ImmutableList.of("Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument"));
      } else if(dimensionType == DimensionType.NETHER) {
         List<String> var2 = ImmutableList.of("Fortress");
         return new LegacyStructureDataHandler(dimensionDataStorage, var2, var2);
      } else if(dimensionType == DimensionType.THE_END) {
         List<String> var2 = ImmutableList.of("EndCity");
         return new LegacyStructureDataHandler(dimensionDataStorage, var2, var2);
      } else {
         throw new RuntimeException(String.format("Unknown dimension type : %s", new Object[]{dimensionType}));
      }
   }
}
