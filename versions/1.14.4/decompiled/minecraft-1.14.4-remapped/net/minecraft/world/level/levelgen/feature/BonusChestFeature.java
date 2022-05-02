package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class BonusChestFeature extends Feature {
   public BonusChestFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      for(BlockState var6 = levelAccessor.getBlockState(blockPos); (var6.isAir() || var6.is(BlockTags.LEAVES)) && blockPos.getY() > 1; var6 = levelAccessor.getBlockState(blockPos)) {
         blockPos = blockPos.below();
      }

      if(blockPos.getY() < 1) {
         return false;
      } else {
         blockPos = blockPos.above();

         for(int var7 = 0; var7 < 4; ++var7) {
            BlockPos var8 = blockPos.offset(random.nextInt(4) - random.nextInt(4), random.nextInt(3) - random.nextInt(3), random.nextInt(4) - random.nextInt(4));
            if(levelAccessor.isEmptyBlock(var8)) {
               levelAccessor.setBlock(var8, Blocks.CHEST.defaultBlockState(), 2);
               RandomizableContainerBlockEntity.setLootTable(levelAccessor, random, var8, BuiltInLootTables.SPAWN_BONUS_CHEST);
               BlockState var9 = Blocks.TORCH.defaultBlockState();

               for(Direction var11 : Direction.Plane.HORIZONTAL) {
                  BlockPos var12 = var8.relative(var11);
                  if(var9.canSurvive(levelAccessor, var12)) {
                     levelAccessor.setBlock(var12, var9, 2);
                  }
               }

               return true;
            }
         }

         return false;
      }
   }
}
