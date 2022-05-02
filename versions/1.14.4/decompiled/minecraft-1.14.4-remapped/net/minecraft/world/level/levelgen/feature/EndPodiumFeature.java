package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public class EndPodiumFeature extends Feature {
   public static final BlockPos END_PODIUM_LOCATION = BlockPos.ZERO;
   private final boolean active;

   public EndPodiumFeature(boolean active) {
      super(NoneFeatureConfiguration::deserialize);
      this.active = active;
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      for(BlockPos var7 : BlockPos.betweenClosed(new BlockPos(blockPos.getX() - 4, blockPos.getY() - 1, blockPos.getZ() - 4), new BlockPos(blockPos.getX() + 4, blockPos.getY() + 32, blockPos.getZ() + 4))) {
         boolean var8 = var7.closerThan(blockPos, 2.5D);
         if(var8 || var7.closerThan(blockPos, 3.5D)) {
            if(var7.getY() < blockPos.getY()) {
               if(var8) {
                  this.setBlock(levelAccessor, var7, Blocks.BEDROCK.defaultBlockState());
               } else if(var7.getY() < blockPos.getY()) {
                  this.setBlock(levelAccessor, var7, Blocks.END_STONE.defaultBlockState());
               }
            } else if(var7.getY() > blockPos.getY()) {
               this.setBlock(levelAccessor, var7, Blocks.AIR.defaultBlockState());
            } else if(!var8) {
               this.setBlock(levelAccessor, var7, Blocks.BEDROCK.defaultBlockState());
            } else if(this.active) {
               this.setBlock(levelAccessor, new BlockPos(var7), Blocks.END_PORTAL.defaultBlockState());
            } else {
               this.setBlock(levelAccessor, new BlockPos(var7), Blocks.AIR.defaultBlockState());
            }
         }
      }

      for(int var6 = 0; var6 < 4; ++var6) {
         this.setBlock(levelAccessor, blockPos.above(var6), Blocks.BEDROCK.defaultBlockState());
      }

      BlockPos blockPos = blockPos.above(2);

      for(Direction var8 : Direction.Plane.HORIZONTAL) {
         this.setBlock(levelAccessor, blockPos.relative(var8), (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, var8));
      }

      return true;
   }
}
