package net.minecraft.world.level.chunk;

import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public abstract class ChunkGenerator {
   protected final LevelAccessor level;
   protected final long seed;
   protected final BiomeSource biomeSource;
   protected final ChunkGeneratorSettings settings;

   public ChunkGenerator(LevelAccessor level, BiomeSource biomeSource, ChunkGeneratorSettings settings) {
      this.level = level;
      this.seed = level.getSeed();
      this.biomeSource = biomeSource;
      this.settings = settings;
   }

   public void createBiomes(ChunkAccess chunkAccess) {
      ChunkPos var2 = chunkAccess.getPos();
      int var3 = var2.x;
      int var4 = var2.z;
      Biome[] vars5 = this.biomeSource.getBiomeBlock(var3 * 16, var4 * 16, 16, 16);
      chunkAccess.setBiomes(vars5);
   }

   protected Biome getCarvingBiome(ChunkAccess chunkAccess) {
      return chunkAccess.getBiome(BlockPos.ZERO);
   }

   protected Biome getDecorationBiome(WorldGenRegion worldGenRegion, BlockPos blockPos) {
      return this.biomeSource.getBiome(blockPos);
   }

   public void applyCarvers(ChunkAccess chunkAccess, GenerationStep.Carving generationStep$Carving) {
      WorldgenRandom var3 = new WorldgenRandom();
      int var4 = 8;
      ChunkPos var5 = chunkAccess.getPos();
      int var6 = var5.x;
      int var7 = var5.z;
      BitSet var8 = chunkAccess.getCarvingMask(generationStep$Carving);

      for(int var9 = var6 - 8; var9 <= var6 + 8; ++var9) {
         for(int var10 = var7 - 8; var10 <= var7 + 8; ++var10) {
            List<ConfiguredWorldCarver<?>> var11 = this.getCarvingBiome(chunkAccess).getCarvers(generationStep$Carving);
            ListIterator<ConfiguredWorldCarver<?>> var12 = var11.listIterator();

            while(var12.hasNext()) {
               int var13 = var12.nextIndex();
               ConfiguredWorldCarver<?> var14 = (ConfiguredWorldCarver)var12.next();
               var3.setLargeFeatureSeed(this.seed + (long)var13, var9, var10);
               if(var14.isStartChunk(var3, var9, var10)) {
                  var14.carve(chunkAccess, var3, this.getSeaLevel(), var9, var10, var6, var7, var8);
               }
            }
         }
      }

   }

   @Nullable
   public BlockPos findNearestMapFeature(Level level, String string, BlockPos var3, int var4, boolean var5) {
      StructureFeature<?> var6 = (StructureFeature)Feature.STRUCTURES_REGISTRY.get(string.toLowerCase(Locale.ROOT));
      return var6 != null?var6.getNearestGeneratedFeature(level, this, var3, var4, var5):null;
   }

   public void applyBiomeDecoration(WorldGenRegion worldGenRegion) {
      int var2 = worldGenRegion.getCenterX();
      int var3 = worldGenRegion.getCenterZ();
      int var4 = var2 * 16;
      int var5 = var3 * 16;
      BlockPos var6 = new BlockPos(var4, 0, var5);
      Biome var7 = this.getDecorationBiome(worldGenRegion, var6.offset(8, 8, 8));
      WorldgenRandom var8 = new WorldgenRandom();
      long var9 = var8.setDecorationSeed(worldGenRegion.getSeed(), var4, var5);

      for(GenerationStep.Decoration var14 : GenerationStep.Decoration.values()) {
         try {
            var7.generate(var14, this, worldGenRegion, var9, var8, var6);
         } catch (Exception var17) {
            CrashReport var16 = CrashReport.forThrowable(var17, "Biome decoration");
            var16.addCategory("Generation").setDetail("CenterX", (Object)Integer.valueOf(var2)).setDetail("CenterZ", (Object)Integer.valueOf(var3)).setDetail("Step", (Object)var14).setDetail("Seed", (Object)Long.valueOf(var9)).setDetail("Biome", (Object)Registry.BIOME.getKey(var7));
            throw new ReportedException(var16);
         }
      }

   }

   public abstract void buildSurfaceAndBedrock(ChunkAccess var1);

   public void spawnOriginalMobs(WorldGenRegion worldGenRegion) {
   }

   public ChunkGeneratorSettings getSettings() {
      return this.settings;
   }

   public abstract int getSpawnHeight();

   public void tickCustomSpawners(ServerLevel serverLevel, boolean var2, boolean var3) {
   }

   public boolean isBiomeValidStartForStructure(Biome biome, StructureFeature structureFeature) {
      return biome.isValidStart(structureFeature);
   }

   @Nullable
   public FeatureConfiguration getStructureConfiguration(Biome biome, StructureFeature structureFeature) {
      return biome.getStructureConfiguration(structureFeature);
   }

   public BiomeSource getBiomeSource() {
      return this.biomeSource;
   }

   public long getSeed() {
      return this.seed;
   }

   public int getGenDepth() {
      return 256;
   }

   public List getMobsAt(MobCategory mobCategory, BlockPos blockPos) {
      return this.level.getBiome(blockPos).getMobs(mobCategory);
   }

   public void createStructures(ChunkAccess chunkAccess, ChunkGenerator chunkGenerator, StructureManager structureManager) {
      for(StructureFeature<?> var5 : Feature.STRUCTURES_REGISTRY.values()) {
         if(chunkGenerator.getBiomeSource().canGenerateStructure(var5)) {
            WorldgenRandom var6 = new WorldgenRandom();
            ChunkPos var7 = chunkAccess.getPos();
            StructureStart var8 = StructureStart.INVALID_START;
            if(var5.isFeatureChunk(chunkGenerator, var6, var7.x, var7.z)) {
               Biome var9 = this.getBiomeSource().getBiome(new BlockPos(var7.getMinBlockX() + 9, 0, var7.getMinBlockZ() + 9));
               StructureStart var10 = var5.getStartFactory().create(var5, var7.x, var7.z, var9, BoundingBox.getUnknownBox(), 0, chunkGenerator.getSeed());
               var10.generatePieces(this, structureManager, var7.x, var7.z, var9);
               var8 = var10.isValid()?var10:StructureStart.INVALID_START;
            }

            chunkAccess.setStartForFeature(var5.getFeatureName(), var8);
         }
      }

   }

   public void createReferences(LevelAccessor levelAccessor, ChunkAccess chunkAccess) {
      int var3 = 8;
      int var4 = chunkAccess.getPos().x;
      int var5 = chunkAccess.getPos().z;
      int var6 = var4 << 4;
      int var7 = var5 << 4;

      for(int var8 = var4 - 8; var8 <= var4 + 8; ++var8) {
         for(int var9 = var5 - 8; var9 <= var5 + 8; ++var9) {
            long var10 = ChunkPos.asLong(var8, var9);

            for(Entry<String, StructureStart> var13 : levelAccessor.getChunk(var8, var9).getAllStarts().entrySet()) {
               StructureStart var14 = (StructureStart)var13.getValue();
               if(var14 != StructureStart.INVALID_START && var14.getBoundingBox().intersects(var6, var7, var6 + 15, var7 + 15)) {
                  chunkAccess.addReferenceForFeature((String)var13.getKey(), var10);
                  DebugPackets.sendStructurePacket(levelAccessor, var14);
               }
            }
         }
      }

   }

   public abstract void fillFromNoise(LevelAccessor var1, ChunkAccess var2);

   public int getSeaLevel() {
      return 63;
   }

   public abstract int getBaseHeight(int var1, int var2, Heightmap.Types var3);

   public int getFirstFreeHeight(int var1, int var2, Heightmap.Types heightmap$Types) {
      return this.getBaseHeight(var1, var2, heightmap$Types);
   }

   public int getFirstOccupiedHeight(int var1, int var2, Heightmap.Types heightmap$Types) {
      return this.getBaseHeight(var1, var2, heightmap$Types) - 1;
   }
}
