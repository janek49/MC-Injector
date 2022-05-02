package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.HugeMushroomFeatureConfig;

public class HugeRedMushroomFeature extends Feature {
   public HugeRedMushroomFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, HugeMushroomFeatureConfig hugeMushroomFeatureConfig) {
      int var6 = random.nextInt(3) + 4;
      if(random.nextInt(12) == 0) {
         var6 *= 2;
      }

      int var7 = blockPos.getY();
      if(var7 >= 1 && var7 + var6 + 1 < 256) {
         Block var8 = levelAccessor.getBlockState(blockPos.below()).getBlock();
         if(!Block.equalsDirt(var8) && var8 != Blocks.GRASS_BLOCK && var8 != Blocks.MYCELIUM) {
            return false;
         } else {
            BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

            for(int var10 = 0; var10 <= var6; ++var10) {
               int var11 = 0;
               if(var10 < var6 && var10 >= var6 - 3) {
                  var11 = 2;
               } else if(var10 == var6) {
                  var11 = 1;
               }

               for(int var12 = -var11; var12 <= var11; ++var12) {
                  for(int var13 = -var11; var13 <= var11; ++var13) {
                     BlockState var14 = levelAccessor.getBlockState(var9.set((Vec3i)blockPos).move(var12, var10, var13));
                     if(!var14.isAir() && !var14.is(BlockTags.LEAVES)) {
                        return false;
                     }
                  }
               }
            }

            BlockState var10 = (BlockState)Blocks.RED_MUSHROOM_BLOCK.defaultBlockState().setValue(HugeMushroomBlock.DOWN, Boolean.valueOf(false));

            for(int var11 = var6 - 3; var11 <= var6; ++var11) {
               int var12 = var11 < var6?2:1;
               int var13 = 0;

               for(int var14 = -var12; var14 <= var12; ++var14) {
                  for(int var15 = -var12; var15 <= var12; ++var15) {
                     boolean var16 = var14 == -var12;
                     boolean var17 = var14 == var12;
                     boolean var18 = var15 == -var12;
                     boolean var19 = var15 == var12;
                     boolean var20 = var16 || var17;
                     boolean var21 = var18 || var19;
                     if(var11 >= var6 || var20 != var21) {
                        var9.set((Vec3i)blockPos).move(var14, var11, var15);
                        if(!levelAccessor.getBlockState(var9).isSolidRender(levelAccessor, var9)) {
                           this.setBlock(levelAccessor, var9, (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)var10.setValue(HugeMushroomBlock.UP, Boolean.valueOf(var11 >= var6 - 1))).setValue(HugeMushroomBlock.WEST, Boolean.valueOf(var14 < 0))).setValue(HugeMushroomBlock.EAST, Boolean.valueOf(var14 > 0))).setValue(HugeMushroomBlock.NORTH, Boolean.valueOf(var15 < 0))).setValue(HugeMushroomBlock.SOUTH, Boolean.valueOf(var15 > 0)));
                        }
                     }
                  }
               }
            }

            BlockState var11 = (BlockState)((BlockState)Blocks.MUSHROOM_STEM.defaultBlockState().setValue(HugeMushroomBlock.UP, Boolean.valueOf(false))).setValue(HugeMushroomBlock.DOWN, Boolean.valueOf(false));

            for(int var12 = 0; var12 < var6; ++var12) {
               var9.set((Vec3i)blockPos).move(Direction.UP, var12);
               if(!levelAccessor.getBlockState(var9).isSolidRender(levelAccessor, var9)) {
                  if(hugeMushroomFeatureConfig.planted) {
                     levelAccessor.setBlock(var9, var11, 3);
                  } else {
                     this.setBlock(levelAccessor, var9, var11);
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }
}
