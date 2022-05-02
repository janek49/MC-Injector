package net.minecraft.world.level.levelgen;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.DebugGeneratorSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;

public class DebugLevelSource extends ChunkGenerator {
   private static final List ALL_BLOCKS = (List)StreamSupport.stream(Registry.BLOCK.spliterator(), false).flatMap((block) -> {
      return block.getStateDefinition().getPossibleStates().stream();
   }).collect(Collectors.toList());
   private static final int GRID_WIDTH = Mth.ceil(Mth.sqrt((float)ALL_BLOCKS.size()));
   private static final int GRID_HEIGHT = Mth.ceil((float)ALL_BLOCKS.size() / (float)GRID_WIDTH);
   protected static final BlockState AIR = Blocks.AIR.defaultBlockState();
   protected static final BlockState BARRIER = Blocks.BARRIER.defaultBlockState();

   public DebugLevelSource(LevelAccessor levelAccessor, BiomeSource biomeSource, DebugGeneratorSettings debugGeneratorSettings) {
      super(levelAccessor, biomeSource, debugGeneratorSettings);
   }

   public void buildSurfaceAndBedrock(ChunkAccess chunkAccess) {
   }

   public void applyCarvers(ChunkAccess chunkAccess, GenerationStep.Carving generationStep$Carving) {
   }

   public int getSpawnHeight() {
      return this.level.getSeaLevel() + 1;
   }

   public void applyBiomeDecoration(WorldGenRegion worldGenRegion) {
      BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();
      int var3 = worldGenRegion.getCenterX();
      int var4 = worldGenRegion.getCenterZ();

      for(int var5 = 0; var5 < 16; ++var5) {
         for(int var6 = 0; var6 < 16; ++var6) {
            int var7 = (var3 << 4) + var5;
            int var8 = (var4 << 4) + var6;
            worldGenRegion.setBlock(var2.set(var7, 60, var8), BARRIER, 2);
            BlockState var9 = getBlockStateFor(var7, var8);
            if(var9 != null) {
               worldGenRegion.setBlock(var2.set(var7, 70, var8), var9, 2);
            }
         }
      }

   }

   public void fillFromNoise(LevelAccessor levelAccessor, ChunkAccess chunkAccess) {
   }

   public int getBaseHeight(int var1, int var2, Heightmap.Types heightmap$Types) {
      return 0;
   }

   public static BlockState getBlockStateFor(int var0, int var1) {
      BlockState blockState = AIR;
      if(var0 > 0 && var1 > 0 && var0 % 2 != 0 && var1 % 2 != 0) {
         var0 = var0 / 2;
         var1 = var1 / 2;
         if(var0 <= GRID_WIDTH && var1 <= GRID_HEIGHT) {
            int var3 = Mth.abs(var0 * GRID_WIDTH + var1);
            if(var3 < ALL_BLOCKS.size()) {
               blockState = (BlockState)ALL_BLOCKS.get(var3);
            }
         }
      }

      return blockState;
   }
}
