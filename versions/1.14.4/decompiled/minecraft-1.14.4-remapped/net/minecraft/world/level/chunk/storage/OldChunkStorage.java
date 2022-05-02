package net.minecraft.world.level.chunk.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.OldDataLayer;

public class OldChunkStorage {
   public static OldChunkStorage.OldLevelChunk load(CompoundTag compoundTag) {
      int var1 = compoundTag.getInt("xPos");
      int var2 = compoundTag.getInt("zPos");
      OldChunkStorage.OldLevelChunk var3 = new OldChunkStorage.OldLevelChunk(var1, var2);
      var3.blocks = compoundTag.getByteArray("Blocks");
      var3.data = new OldDataLayer(compoundTag.getByteArray("Data"), 7);
      var3.skyLight = new OldDataLayer(compoundTag.getByteArray("SkyLight"), 7);
      var3.blockLight = new OldDataLayer(compoundTag.getByteArray("BlockLight"), 7);
      var3.heightmap = compoundTag.getByteArray("HeightMap");
      var3.terrainPopulated = compoundTag.getBoolean("TerrainPopulated");
      var3.entities = compoundTag.getList("Entities", 10);
      var3.blockEntities = compoundTag.getList("TileEntities", 10);
      var3.blockTicks = compoundTag.getList("TileTicks", 10);

      try {
         var3.lastUpdated = compoundTag.getLong("LastUpdate");
      } catch (ClassCastException var5) {
         var3.lastUpdated = (long)compoundTag.getInt("LastUpdate");
      }

      return var3;
   }

   public static void convertToAnvilFormat(OldChunkStorage.OldLevelChunk oldChunkStorage$OldLevelChunk, CompoundTag compoundTag, BiomeSource biomeSource) {
      compoundTag.putInt("xPos", oldChunkStorage$OldLevelChunk.x);
      compoundTag.putInt("zPos", oldChunkStorage$OldLevelChunk.z);
      compoundTag.putLong("LastUpdate", oldChunkStorage$OldLevelChunk.lastUpdated);
      int[] vars3 = new int[oldChunkStorage$OldLevelChunk.heightmap.length];

      for(int var4 = 0; var4 < oldChunkStorage$OldLevelChunk.heightmap.length; ++var4) {
         vars3[var4] = oldChunkStorage$OldLevelChunk.heightmap[var4];
      }

      compoundTag.putIntArray("HeightMap", vars3);
      compoundTag.putBoolean("TerrainPopulated", oldChunkStorage$OldLevelChunk.terrainPopulated);
      ListTag var4 = new ListTag();

      for(int var5 = 0; var5 < 8; ++var5) {
         boolean var6 = true;

         for(int var7 = 0; var7 < 16 && var6; ++var7) {
            for(int var8 = 0; var8 < 16 && var6; ++var8) {
               for(int var9 = 0; var9 < 16; ++var9) {
                  int var10 = var7 << 11 | var9 << 7 | var8 + (var5 << 4);
                  int var11 = oldChunkStorage$OldLevelChunk.blocks[var10];
                  if(var11 != 0) {
                     var6 = false;
                     break;
                  }
               }
            }
         }

         if(!var6) {
            byte[] vars7 = new byte[4096];
            DataLayer var8 = new DataLayer();
            DataLayer var9 = new DataLayer();
            DataLayer var10 = new DataLayer();

            for(int var11 = 0; var11 < 16; ++var11) {
               for(int var12 = 0; var12 < 16; ++var12) {
                  for(int var13 = 0; var13 < 16; ++var13) {
                     int var14 = var11 << 11 | var13 << 7 | var12 + (var5 << 4);
                     int var15 = oldChunkStorage$OldLevelChunk.blocks[var14];
                     vars7[var12 << 8 | var13 << 4 | var11] = (byte)(var15 & 255);
                     var8.set(var11, var12, var13, oldChunkStorage$OldLevelChunk.data.get(var11, var12 + (var5 << 4), var13));
                     var9.set(var11, var12, var13, oldChunkStorage$OldLevelChunk.skyLight.get(var11, var12 + (var5 << 4), var13));
                     var10.set(var11, var12, var13, oldChunkStorage$OldLevelChunk.blockLight.get(var11, var12 + (var5 << 4), var13));
                  }
               }
            }

            CompoundTag var11 = new CompoundTag();
            var11.putByte("Y", (byte)(var5 & 255));
            var11.putByteArray("Blocks", vars7);
            var11.putByteArray("Data", var8.getData());
            var11.putByteArray("SkyLight", var9.getData());
            var11.putByteArray("BlockLight", var10.getData());
            var4.add(var11);
         }
      }

      compoundTag.put("Sections", var4);
      byte[] vars5 = new byte[256];
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

      for(int var7 = 0; var7 < 16; ++var7) {
         for(int var8 = 0; var8 < 16; ++var8) {
            var6.set(oldChunkStorage$OldLevelChunk.x << 4 | var7, 0, oldChunkStorage$OldLevelChunk.z << 4 | var8);
            vars5[var8 << 4 | var7] = (byte)(Registry.BIOME.getId(biomeSource.getBiome(var6)) & 255);
         }
      }

      compoundTag.putByteArray("Biomes", vars5);
      compoundTag.put("Entities", oldChunkStorage$OldLevelChunk.entities);
      compoundTag.put("TileEntities", oldChunkStorage$OldLevelChunk.blockEntities);
      if(oldChunkStorage$OldLevelChunk.blockTicks != null) {
         compoundTag.put("TileTicks", oldChunkStorage$OldLevelChunk.blockTicks);
      }

      compoundTag.putBoolean("convertedFromAlphaFormat", true);
   }

   public static class OldLevelChunk {
      public long lastUpdated;
      public boolean terrainPopulated;
      public byte[] heightmap;
      public OldDataLayer blockLight;
      public OldDataLayer skyLight;
      public OldDataLayer data;
      public byte[] blocks;
      public ListTag entities;
      public ListTag blockEntities;
      public ListTag blockTicks;
      public final int x;
      public final int z;

      public OldLevelChunk(int x, int z) {
         this.x = x;
         this.z = z;
      }
   }
}
