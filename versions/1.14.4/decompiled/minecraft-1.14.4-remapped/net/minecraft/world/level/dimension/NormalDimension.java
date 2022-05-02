package net.minecraft.world.level.dimension;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.BiomeSourceType;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.CheckerboardBiomeSource;
import net.minecraft.world.level.biome.CheckerboardBiomeSourceSettings;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSourceSettings;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import net.minecraft.world.level.biome.OverworldBiomeSourceSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DebugGeneratorSettings;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NetherGeneratorSettings;
import net.minecraft.world.level.levelgen.NetherLevelSource;
import net.minecraft.world.level.levelgen.OverworldGeneratorSettings;
import net.minecraft.world.level.levelgen.OverworldLevelSource;
import net.minecraft.world.level.levelgen.TheEndGeneratorSettings;
import net.minecraft.world.level.levelgen.TheEndLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.phys.Vec3;

public class NormalDimension extends Dimension {
   public NormalDimension(Level level, DimensionType dimensionType) {
      super(level, dimensionType);
   }

   public DimensionType getType() {
      return DimensionType.OVERWORLD;
   }

   public ChunkGenerator createRandomLevelGenerator() {
      LevelType var1 = this.level.getLevelData().getGeneratorType();
      ChunkGeneratorType<FlatLevelGeneratorSettings, FlatLevelSource> var2 = ChunkGeneratorType.FLAT;
      ChunkGeneratorType<DebugGeneratorSettings, DebugLevelSource> var3 = ChunkGeneratorType.DEBUG;
      ChunkGeneratorType<NetherGeneratorSettings, NetherLevelSource> var4 = ChunkGeneratorType.CAVES;
      ChunkGeneratorType<TheEndGeneratorSettings, TheEndLevelSource> var5 = ChunkGeneratorType.FLOATING_ISLANDS;
      ChunkGeneratorType<OverworldGeneratorSettings, OverworldLevelSource> var6 = ChunkGeneratorType.SURFACE;
      BiomeSourceType<FixedBiomeSourceSettings, FixedBiomeSource> var7 = BiomeSourceType.FIXED;
      BiomeSourceType<OverworldBiomeSourceSettings, OverworldBiomeSource> var8 = BiomeSourceType.VANILLA_LAYERED;
      BiomeSourceType<CheckerboardBiomeSourceSettings, CheckerboardBiomeSource> var9 = BiomeSourceType.CHECKERBOARD;
      if(var1 == LevelType.FLAT) {
         FlatLevelGeneratorSettings var10 = FlatLevelGeneratorSettings.fromObject(new Dynamic(NbtOps.INSTANCE, this.level.getLevelData().getGeneratorOptions()));
         FixedBiomeSourceSettings var11 = ((FixedBiomeSourceSettings)var7.createSettings()).setBiome(var10.getBiome());
         return var2.create(this.level, var7.create(var11), var10);
      } else if(var1 == LevelType.DEBUG_ALL_BLOCK_STATES) {
         FixedBiomeSourceSettings var10 = ((FixedBiomeSourceSettings)var7.createSettings()).setBiome(Biomes.PLAINS);
         return var3.create(this.level, var7.create(var10), var3.createSettings());
      } else if(var1 != LevelType.BUFFET) {
         OverworldGeneratorSettings var10 = (OverworldGeneratorSettings)var6.createSettings();
         OverworldBiomeSourceSettings var11 = ((OverworldBiomeSourceSettings)var8.createSettings()).setLevelData(this.level.getLevelData()).setGeneratorSettings(var10);
         return var6.create(this.level, var8.create(var11), var10);
      } else {
         BiomeSource var10 = null;
         JsonElement var11 = (JsonElement)Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, this.level.getLevelData().getGeneratorOptions());
         JsonObject var12 = var11.getAsJsonObject();
         JsonObject var13 = var12.getAsJsonObject("biome_source");
         if(var13 != null && var13.has("type") && var13.has("options")) {
            BiomeSourceType<?, ?> var14 = (BiomeSourceType)Registry.BIOME_SOURCE_TYPE.get(new ResourceLocation(var13.getAsJsonPrimitive("type").getAsString()));
            JsonObject var15 = var13.getAsJsonObject("options");
            Biome[] vars16 = new Biome[]{Biomes.OCEAN};
            if(var15.has("biomes")) {
               JsonArray var17 = var15.getAsJsonArray("biomes");
               vars16 = var17.size() > 0?new Biome[var17.size()]:new Biome[]{Biomes.OCEAN};

               for(int var18 = 0; var18 < var17.size(); ++var18) {
                  vars16[var18] = (Biome)Registry.BIOME.getOptional(new ResourceLocation(var17.get(var18).getAsString())).orElse(Biomes.OCEAN);
               }
            }

            if(BiomeSourceType.FIXED == var14) {
               FixedBiomeSourceSettings var17 = ((FixedBiomeSourceSettings)var7.createSettings()).setBiome(vars16[0]);
               var10 = var7.create(var17);
            }

            if(BiomeSourceType.CHECKERBOARD == var14) {
               int var17 = var15.has("size")?var15.getAsJsonPrimitive("size").getAsInt():2;
               CheckerboardBiomeSourceSettings var18 = ((CheckerboardBiomeSourceSettings)var9.createSettings()).setAllowedBiomes(vars16).setSize(var17);
               var10 = var9.create(var18);
            }

            if(BiomeSourceType.VANILLA_LAYERED == var14) {
               OverworldBiomeSourceSettings var17 = ((OverworldBiomeSourceSettings)var8.createSettings()).setGeneratorSettings(new OverworldGeneratorSettings()).setLevelData(this.level.getLevelData());
               var10 = var8.create(var17);
            }
         }

         if(var10 == null) {
            var10 = var7.create(((FixedBiomeSourceSettings)var7.createSettings()).setBiome(Biomes.OCEAN));
         }

         BlockState var14 = Blocks.STONE.defaultBlockState();
         BlockState var15 = Blocks.WATER.defaultBlockState();
         JsonObject var16 = var12.getAsJsonObject("chunk_generator");
         if(var16 != null && var16.has("options")) {
            JsonObject var17 = var16.getAsJsonObject("options");
            if(var17.has("default_block")) {
               String var18 = var17.getAsJsonPrimitive("default_block").getAsString();
               var14 = ((Block)Registry.BLOCK.get(new ResourceLocation(var18))).defaultBlockState();
            }

            if(var17.has("default_fluid")) {
               String var18 = var17.getAsJsonPrimitive("default_fluid").getAsString();
               var15 = ((Block)Registry.BLOCK.get(new ResourceLocation(var18))).defaultBlockState();
            }
         }

         if(var16 != null && var16.has("type")) {
            ChunkGeneratorType<?, ?> var17 = (ChunkGeneratorType)Registry.CHUNK_GENERATOR_TYPE.get(new ResourceLocation(var16.getAsJsonPrimitive("type").getAsString()));
            if(ChunkGeneratorType.CAVES == var17) {
               NetherGeneratorSettings var18 = (NetherGeneratorSettings)var4.createSettings();
               var18.setDefaultBlock(var14);
               var18.setDefaultFluid(var15);
               return var4.create(this.level, var10, var18);
            }

            if(ChunkGeneratorType.FLOATING_ISLANDS == var17) {
               TheEndGeneratorSettings var18 = (TheEndGeneratorSettings)var5.createSettings();
               var18.setSpawnPosition(new BlockPos(0, 64, 0));
               var18.setDefaultBlock(var14);
               var18.setDefaultFluid(var15);
               return var5.create(this.level, var10, var18);
            }
         }

         OverworldGeneratorSettings var17 = (OverworldGeneratorSettings)var6.createSettings();
         var17.setDefaultBlock(var14);
         var17.setDefaultFluid(var15);
         return var6.create(this.level, var10, var17);
      }
   }

   @Nullable
   public BlockPos getSpawnPosInChunk(ChunkPos chunkPos, boolean var2) {
      for(int var3 = chunkPos.getMinBlockX(); var3 <= chunkPos.getMaxBlockX(); ++var3) {
         for(int var4 = chunkPos.getMinBlockZ(); var4 <= chunkPos.getMaxBlockZ(); ++var4) {
            BlockPos var5 = this.getValidSpawnPosition(var3, var4, var2);
            if(var5 != null) {
               return var5;
            }
         }
      }

      return null;
   }

   @Nullable
   public BlockPos getValidSpawnPosition(int var1, int var2, boolean var3) {
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos(var1, 0, var2);
      Biome var5 = this.level.getBiome(var4);
      BlockState var6 = var5.getSurfaceBuilderConfig().getTopMaterial();
      if(var3 && !var6.getBlock().is(BlockTags.VALID_SPAWN)) {
         return null;
      } else {
         LevelChunk var7 = this.level.getChunk(var1 >> 4, var2 >> 4);
         int var8 = var7.getHeight(Heightmap.Types.MOTION_BLOCKING, var1 & 15, var2 & 15);
         if(var8 < 0) {
            return null;
         } else if(var7.getHeight(Heightmap.Types.WORLD_SURFACE, var1 & 15, var2 & 15) > var7.getHeight(Heightmap.Types.OCEAN_FLOOR, var1 & 15, var2 & 15)) {
            return null;
         } else {
            for(int var9 = var8 + 1; var9 >= 0; --var9) {
               var4.set(var1, var9, var2);
               BlockState var10 = this.level.getBlockState(var4);
               if(!var10.getFluidState().isEmpty()) {
                  break;
               }

               if(var10.equals(var6)) {
                  return var4.above().immutable();
               }
            }

            return null;
         }
      }
   }

   public float getTimeOfDay(long var1, float var3) {
      double var4 = Mth.frac((double)var1 / 24000.0D - 0.25D);
      double var6 = 0.5D - Math.cos(var4 * 3.141592653589793D) / 2.0D;
      return (float)(var4 * 2.0D + var6) / 3.0F;
   }

   public boolean isNaturalDimension() {
      return true;
   }

   public Vec3 getFogColor(float var1, float var2) {
      float var3 = Mth.cos(var1 * 6.2831855F) * 2.0F + 0.5F;
      var3 = Mth.clamp(var3, 0.0F, 1.0F);
      float var4 = 0.7529412F;
      float var5 = 0.84705883F;
      float var6 = 1.0F;
      var4 = var4 * (var3 * 0.94F + 0.06F);
      var5 = var5 * (var3 * 0.94F + 0.06F);
      var6 = var6 * (var3 * 0.91F + 0.09F);
      return new Vec3((double)var4, (double)var5, (double)var6);
   }

   public boolean mayRespawn() {
      return true;
   }

   public boolean isFoggyAt(int var1, int var2) {
      return false;
   }
}
