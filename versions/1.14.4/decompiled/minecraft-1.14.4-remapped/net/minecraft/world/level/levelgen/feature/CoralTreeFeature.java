package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.CoralFeature;

public class CoralTreeFeature extends CoralFeature {
   public CoralTreeFeature(Function function) {
      super(function);
   }

   protected boolean placeFeature(LevelAccessor levelAccessor, Random random, BlockPos blockPos, BlockState blockState) {
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos(blockPos);
      int var6 = random.nextInt(3) + 1;

      for(int var7 = 0; var7 < var6; ++var7) {
         if(!this.placeCoralBlock(levelAccessor, random, var5, blockState)) {
            return true;
         }

         var5.move(Direction.UP);
      }

      BlockPos var7 = var5.immutable();
      int var8 = random.nextInt(3) + 2;
      List<Direction> var9 = Lists.newArrayList(Direction.Plane.HORIZONTAL);
      Collections.shuffle(var9, random);

      for(Direction var12 : var9.subList(0, var8)) {
         var5.set((Vec3i)var7);
         var5.move(var12);
         int var13 = random.nextInt(5) + 2;
         int var14 = 0;

         for(int var15 = 0; var15 < var13 && this.placeCoralBlock(levelAccessor, random, var5, blockState); ++var15) {
            ++var14;
            var5.move(Direction.UP);
            if(var15 == 0 || var14 >= 2 && random.nextFloat() < 0.25F) {
               var5.move(var12);
               var14 = 0;
            }
         }
      }

      return true;
   }
}
