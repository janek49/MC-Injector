package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Material;

public class BlueIceFeature extends Feature {
   public BlueIceFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      if(blockPos.getY() > levelAccessor.getSeaLevel() - 1) {
         return false;
      } else if(levelAccessor.getBlockState(blockPos).getBlock() != Blocks.WATER && levelAccessor.getBlockState(blockPos.below()).getBlock() != Blocks.WATER) {
         return false;
      } else {
         boolean var6 = false;

         for(Direction var10 : Direction.values()) {
            if(var10 != Direction.DOWN && levelAccessor.getBlockState(blockPos.relative(var10)).getBlock() == Blocks.PACKED_ICE) {
               var6 = true;
               break;
            }
         }

         if(!var6) {
            return false;
         } else {
            levelAccessor.setBlock(blockPos, Blocks.BLUE_ICE.defaultBlockState(), 2);

            for(int var7 = 0; var7 < 200; ++var7) {
               int var8 = random.nextInt(5) - random.nextInt(6);
               int var9 = 3;
               if(var8 < 2) {
                  var9 += var8 / 2;
               }

               if(var9 >= 1) {
                  BlockPos var10 = blockPos.offset(random.nextInt(var9) - random.nextInt(var9), var8, random.nextInt(var9) - random.nextInt(var9));
                  BlockState var11 = levelAccessor.getBlockState(var10);
                  Block var12 = var11.getBlock();
                  if(var11.getMaterial() == Material.AIR || var12 == Blocks.WATER || var12 == Blocks.PACKED_ICE || var12 == Blocks.ICE) {
                     for(Direction var16 : Direction.values()) {
                        Block var17 = levelAccessor.getBlockState(var10.relative(var16)).getBlock();
                        if(var17 == Blocks.BLUE_ICE) {
                           levelAccessor.setBlock(var10, Blocks.BLUE_ICE.defaultBlockState(), 2);
                           break;
                        }
                     }
                  }
               }
            }

            return true;
         }
      }
   }
}
