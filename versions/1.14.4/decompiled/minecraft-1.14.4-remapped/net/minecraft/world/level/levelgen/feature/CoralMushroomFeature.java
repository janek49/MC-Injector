package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.CoralFeature;

public class CoralMushroomFeature extends CoralFeature {
   public CoralMushroomFeature(Function function) {
      super(function);
   }

   protected boolean placeFeature(LevelAccessor levelAccessor, Random random, BlockPos blockPos, BlockState blockState) {
      int var5 = random.nextInt(3) + 3;
      int var6 = random.nextInt(3) + 3;
      int var7 = random.nextInt(3) + 3;
      int var8 = random.nextInt(3) + 1;
      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos(blockPos);

      for(int var10 = 0; var10 <= var6; ++var10) {
         for(int var11 = 0; var11 <= var5; ++var11) {
            for(int var12 = 0; var12 <= var7; ++var12) {
               var9.set(var10 + blockPos.getX(), var11 + blockPos.getY(), var12 + blockPos.getZ());
               var9.move(Direction.DOWN, var8);
               if((var10 != 0 && var10 != var6 || var11 != 0 && var11 != var5) && (var12 != 0 && var12 != var7 || var11 != 0 && var11 != var5) && (var10 != 0 && var10 != var6 || var12 != 0 && var12 != var7) && (var10 == 0 || var10 == var6 || var11 == 0 || var11 == var5 || var12 == 0 || var12 == var7) && random.nextFloat() >= 0.1F && !this.placeCoralBlock(levelAccessor, random, var9, blockState)) {
                  ;
               }
            }
         }
      }

      return true;
   }
}
