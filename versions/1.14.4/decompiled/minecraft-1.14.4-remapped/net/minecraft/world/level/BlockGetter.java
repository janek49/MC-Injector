package net.minecraft.world.level;

import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface BlockGetter {
   @Nullable
   BlockEntity getBlockEntity(BlockPos var1);

   BlockState getBlockState(BlockPos var1);

   FluidState getFluidState(BlockPos var1);

   default int getLightEmission(BlockPos blockPos) {
      return this.getBlockState(blockPos).getLightEmission();
   }

   default int getMaxLightLevel() {
      return 15;
   }

   default int getMaxBuildHeight() {
      return 256;
   }

   default BlockHitResult clip(ClipContext clipContext) {
      return (BlockHitResult)traverseBlocks(clipContext, (clipContext, blockPos) -> {
         BlockState var3 = this.getBlockState(blockPos);
         FluidState var4 = this.getFluidState(blockPos);
         Vec3 var5 = clipContext.getFrom();
         Vec3 var6 = clipContext.getTo();
         VoxelShape var7 = clipContext.getBlockShape(var3, this, blockPos);
         BlockHitResult var8 = this.clipWithInteractionOverride(var5, var6, blockPos, var7, var3);
         VoxelShape var9 = clipContext.getFluidShape(var4, this, blockPos);
         BlockHitResult var10 = var9.clip(var5, var6, blockPos);
         double var11 = var8 == null?Double.MAX_VALUE:clipContext.getFrom().distanceToSqr(var8.getLocation());
         double var13 = var10 == null?Double.MAX_VALUE:clipContext.getFrom().distanceToSqr(var10.getLocation());
         return var11 <= var13?var8:var10;
      }, (clipContext) -> {
         Vec3 var1 = clipContext.getFrom().subtract(clipContext.getTo());
         return BlockHitResult.miss(clipContext.getTo(), Direction.getNearest(var1.x, var1.y, var1.z), new BlockPos(clipContext.getTo()));
      });
   }

   @Nullable
   default BlockHitResult clipWithInteractionOverride(Vec3 var1, Vec3 var2, BlockPos blockPos, VoxelShape voxelShape, BlockState blockState) {
      BlockHitResult blockHitResult = voxelShape.clip(var1, var2, blockPos);
      if(blockHitResult != null) {
         BlockHitResult var7 = blockState.getInteractionShape(this, blockPos).clip(var1, var2, blockPos);
         if(var7 != null && var7.getLocation().subtract(var1).lengthSqr() < blockHitResult.getLocation().subtract(var1).lengthSqr()) {
            return blockHitResult.withDirection(var7.getDirection());
         }
      }

      return blockHitResult;
   }

   static default Object traverseBlocks(ClipContext clipContext, BiFunction biFunction, Function function) {
      Vec3 var3 = clipContext.getFrom();
      Vec3 var4 = clipContext.getTo();
      if(var3.equals(var4)) {
         return function.apply(clipContext);
      } else {
         double var5 = Mth.lerp(-1.0E-7D, var4.x, var3.x);
         double var7 = Mth.lerp(-1.0E-7D, var4.y, var3.y);
         double var9 = Mth.lerp(-1.0E-7D, var4.z, var3.z);
         double var11 = Mth.lerp(-1.0E-7D, var3.x, var4.x);
         double var13 = Mth.lerp(-1.0E-7D, var3.y, var4.y);
         double var15 = Mth.lerp(-1.0E-7D, var3.z, var4.z);
         int var17 = Mth.floor(var11);
         int var18 = Mth.floor(var13);
         int var19 = Mth.floor(var15);
         BlockPos.MutableBlockPos var20 = new BlockPos.MutableBlockPos(var17, var18, var19);
         T var21 = biFunction.apply(clipContext, var20);
         if(var21 != null) {
            return var21;
         } else {
            double var22 = var5 - var11;
            double var24 = var7 - var13;
            double var26 = var9 - var15;
            int var28 = Mth.sign(var22);
            int var29 = Mth.sign(var24);
            int var30 = Mth.sign(var26);
            double var31 = var28 == 0?Double.MAX_VALUE:(double)var28 / var22;
            double var33 = var29 == 0?Double.MAX_VALUE:(double)var29 / var24;
            double var35 = var30 == 0?Double.MAX_VALUE:(double)var30 / var26;
            double var37 = var31 * (var28 > 0?1.0D - Mth.frac(var11):Mth.frac(var11));
            double var39 = var33 * (var29 > 0?1.0D - Mth.frac(var13):Mth.frac(var13));
            double var41 = var35 * (var30 > 0?1.0D - Mth.frac(var15):Mth.frac(var15));

            while(var37 <= 1.0D || var39 <= 1.0D || var41 <= 1.0D) {
               if(var37 < var39) {
                  if(var37 < var41) {
                     var17 += var28;
                     var37 += var31;
                  } else {
                     var19 += var30;
                     var41 += var35;
                  }
               } else if(var39 < var41) {
                  var18 += var29;
                  var39 += var33;
               } else {
                  var19 += var30;
                  var41 += var35;
               }

               T var43 = biFunction.apply(clipContext, var20.set(var17, var18, var19));
               if(var43 != null) {
                  return var43;
               }
            }

            return function.apply(clipContext);
         }
      }
   }
}
