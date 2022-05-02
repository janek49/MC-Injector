package net.minecraft.world.level.levelgen.carver;

import com.google.common.collect.ImmutableSet;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.carver.CanyonWorldCarver;
import net.minecraft.world.level.levelgen.carver.UnderwaterCaveWorldCarver;

public class UnderwaterCanyonWorldCarver extends CanyonWorldCarver {
   public UnderwaterCanyonWorldCarver(Function function) {
      super(function);
      this.replaceableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.SAND, Blocks.GRAVEL, Blocks.WATER, Blocks.LAVA, Blocks.OBSIDIAN, Blocks.AIR, Blocks.CAVE_AIR});
   }

   protected boolean hasWater(ChunkAccess chunkAccess, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      return false;
   }

   protected boolean carveBlock(ChunkAccess chunkAccess, BitSet bitSet, Random random, BlockPos.MutableBlockPos var4, BlockPos.MutableBlockPos var5, BlockPos.MutableBlockPos var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, AtomicBoolean atomicBoolean) {
      return UnderwaterCaveWorldCarver.carveBlock(this, chunkAccess, bitSet, random, var4, var7, var8, var9, var10, var11, var12, var13, var14);
   }
}
