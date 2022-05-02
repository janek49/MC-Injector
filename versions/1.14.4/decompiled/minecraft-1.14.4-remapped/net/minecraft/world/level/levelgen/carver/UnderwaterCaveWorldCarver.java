package net.minecraft.world.level.levelgen.carver;

import com.google.common.collect.ImmutableSet;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.carver.CaveWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

public class UnderwaterCaveWorldCarver extends CaveWorldCarver {
   public UnderwaterCaveWorldCarver(Function function) {
      super(function, 256);
      this.replaceableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.SAND, Blocks.GRAVEL, Blocks.WATER, Blocks.LAVA, Blocks.OBSIDIAN, Blocks.AIR, Blocks.CAVE_AIR, Blocks.PACKED_ICE});
   }

   protected boolean hasWater(ChunkAccess chunkAccess, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      return false;
   }

   protected boolean carveBlock(ChunkAccess chunkAccess, BitSet bitSet, Random random, BlockPos.MutableBlockPos var4, BlockPos.MutableBlockPos var5, BlockPos.MutableBlockPos var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, AtomicBoolean atomicBoolean) {
      return carveBlock(this, chunkAccess, bitSet, random, var4, var7, var8, var9, var10, var11, var12, var13, var14);
   }

   protected static boolean carveBlock(WorldCarver worldCarver, ChunkAccess chunkAccess, BitSet bitSet, Random random, BlockPos.MutableBlockPos blockPos$MutableBlockPos, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) {
      if(var11 >= var5) {
         return false;
      } else {
         int var13 = var10 | var12 << 4 | var11 << 8;
         if(bitSet.get(var13)) {
            return false;
         } else {
            bitSet.set(var13);
            blockPos$MutableBlockPos.set(var8, var11, var9);
            BlockState var14 = chunkAccess.getBlockState(blockPos$MutableBlockPos);
            if(!worldCarver.canReplaceBlock(var14)) {
               return false;
            } else if(var11 == 10) {
               float var15 = random.nextFloat();
               if((double)var15 < 0.25D) {
                  chunkAccess.setBlockState(blockPos$MutableBlockPos, Blocks.MAGMA_BLOCK.defaultBlockState(), false);
                  chunkAccess.getBlockTicks().scheduleTick(blockPos$MutableBlockPos, Blocks.MAGMA_BLOCK, 0);
               } else {
                  chunkAccess.setBlockState(blockPos$MutableBlockPos, Blocks.OBSIDIAN.defaultBlockState(), false);
               }

               return true;
            } else if(var11 < 10) {
               chunkAccess.setBlockState(blockPos$MutableBlockPos, Blocks.LAVA.defaultBlockState(), false);
               return false;
            } else {
               boolean var15 = false;

               for(Direction var17 : Direction.Plane.HORIZONTAL) {
                  int var18 = var8 + var17.getStepX();
                  int var19 = var9 + var17.getStepZ();
                  if(var18 >> 4 != var6 || var19 >> 4 != var7 || chunkAccess.getBlockState(blockPos$MutableBlockPos.set(var18, var11, var19)).isAir()) {
                     chunkAccess.setBlockState(blockPos$MutableBlockPos, WATER.createLegacyBlock(), false);
                     chunkAccess.getLiquidTicks().scheduleTick(blockPos$MutableBlockPos, WATER.getType(), 0);
                     var15 = true;
                     break;
                  }
               }

               blockPos$MutableBlockPos.set(var8, var11, var9);
               if(!var15) {
                  chunkAccess.setBlockState(blockPos$MutableBlockPos, WATER.createLegacyBlock(), false);
                  return true;
               } else {
                  return true;
               }
            }
         }
      }
   }
}
