package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.CoralFeature;

public class CoralClawFeature extends CoralFeature {
   public CoralClawFeature(Function function) {
      super(function);
   }

   protected boolean placeFeature(LevelAccessor levelAccessor, Random random, BlockPos blockPos, BlockState blockState) {
      if(!this.placeCoralBlock(levelAccessor, random, blockPos, blockState)) {
         return false;
      } else {
         Direction var5 = Direction.Plane.HORIZONTAL.getRandomDirection(random);
         int var6 = random.nextInt(2) + 2;
         List<Direction> var7 = Lists.newArrayList(new Direction[]{var5, var5.getClockWise(), var5.getCounterClockWise()});
         Collections.shuffle(var7, random);

         for(Direction var10 : var7.subList(0, var6)) {
            BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos(blockPos);
            int var12 = random.nextInt(2) + 1;
            var11.move(var10);
            int var13;
            Direction var14;
            if(var10 == var5) {
               var14 = var5;
               var13 = random.nextInt(3) + 2;
            } else {
               var11.move(Direction.UP);
               Direction[] vars15 = new Direction[]{var10, Direction.UP};
               var14 = vars15[random.nextInt(vars15.length)];
               var13 = random.nextInt(3) + 3;
            }

            for(int var15 = 0; var15 < var12 && this.placeCoralBlock(levelAccessor, random, var11, blockState); ++var15) {
               var11.move(var14);
            }

            var11.move(var14.getOpposite());
            var11.move(Direction.UP);

            for(int var15 = 0; var15 < var13; ++var15) {
               var11.move(var5);
               if(!this.placeCoralBlock(levelAccessor, random, var11, blockState)) {
                  break;
               }

               if(random.nextFloat() < 0.25F) {
                  var11.move(Direction.UP);
               }
            }
         }

         return true;
      }
   }
}
