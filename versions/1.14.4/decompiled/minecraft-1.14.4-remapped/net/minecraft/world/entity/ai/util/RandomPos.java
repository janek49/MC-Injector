package net.minecraft.world.entity.ai.util;

import java.util.Random;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.phys.Vec3;

public class RandomPos {
   @Nullable
   public static Vec3 getPos(PathfinderMob pathfinderMob, int var1, int var2) {
      return generateRandomPos(pathfinderMob, var1, var2, (Vec3)null);
   }

   @Nullable
   public static Vec3 getLandPos(PathfinderMob pathfinderMob, int var1, int var2) {
      pathfinderMob.getClass();
      return getLandPos(pathfinderMob, var1, var2, pathfinderMob::getWalkTargetValue);
   }

   @Nullable
   public static Vec3 getLandPos(PathfinderMob pathfinderMob, int var1, int var2, ToDoubleFunction toDoubleFunction) {
      return generateRandomPos(pathfinderMob, var1, var2, (Vec3)null, false, 0.0D, toDoubleFunction);
   }

   @Nullable
   public static Vec3 getPosTowards(PathfinderMob pathfinderMob, int var1, int var2, Vec3 var3) {
      Vec3 var4 = var3.subtract(pathfinderMob.x, pathfinderMob.y, pathfinderMob.z);
      return generateRandomPos(pathfinderMob, var1, var2, var4);
   }

   @Nullable
   public static Vec3 getPosTowards(PathfinderMob pathfinderMob, int var1, int var2, Vec3 var3, double var4) {
      Vec3 var6 = var3.subtract(pathfinderMob.x, pathfinderMob.y, pathfinderMob.z);
      pathfinderMob.getClass();
      return generateRandomPos(pathfinderMob, var1, var2, var6, true, var4, pathfinderMob::getWalkTargetValue);
   }

   @Nullable
   public static Vec3 getLandPosAvoid(PathfinderMob pathfinderMob, int var1, int var2, Vec3 var3) {
      Vec3 var4 = (new Vec3(pathfinderMob.x, pathfinderMob.y, pathfinderMob.z)).subtract(var3);
      pathfinderMob.getClass();
      return generateRandomPos(pathfinderMob, var1, var2, var4, false, 1.5707963705062866D, pathfinderMob::getWalkTargetValue);
   }

   @Nullable
   public static Vec3 getPosAvoid(PathfinderMob pathfinderMob, int var1, int var2, Vec3 var3) {
      Vec3 var4 = (new Vec3(pathfinderMob.x, pathfinderMob.y, pathfinderMob.z)).subtract(var3);
      return generateRandomPos(pathfinderMob, var1, var2, var4);
   }

   @Nullable
   private static Vec3 generateRandomPos(PathfinderMob pathfinderMob, int var1, int var2, @Nullable Vec3 var3) {
      pathfinderMob.getClass();
      return generateRandomPos(pathfinderMob, var1, var2, var3, true, 1.5707963705062866D, pathfinderMob::getWalkTargetValue);
   }

   @Nullable
   private static Vec3 generateRandomPos(PathfinderMob pathfinderMob, int var1, int var2, @Nullable Vec3 var3, boolean var4, double var5, ToDoubleFunction toDoubleFunction) {
      PathNavigation var8 = pathfinderMob.getNavigation();
      Random var9 = pathfinderMob.getRandom();
      boolean var10;
      if(pathfinderMob.hasRestriction()) {
         var10 = pathfinderMob.getRestrictCenter().closerThan(pathfinderMob.position(), (double)(pathfinderMob.getRestrictRadius() + (float)var1) + 1.0D);
      } else {
         var10 = false;
      }

      boolean var11 = false;
      double var12 = Double.NEGATIVE_INFINITY;
      BlockPos var14 = new BlockPos(pathfinderMob);

      for(int var15 = 0; var15 < 10; ++var15) {
         BlockPos var16 = getRandomDelta(var9, var1, var2, var3, var5);
         if(var16 != null) {
            int var17 = var16.getX();
            int var18 = var16.getY();
            int var19 = var16.getZ();
            if(pathfinderMob.hasRestriction() && var1 > 1) {
               BlockPos var20 = pathfinderMob.getRestrictCenter();
               if(pathfinderMob.x > (double)var20.getX()) {
                  var17 -= var9.nextInt(var1 / 2);
               } else {
                  var17 += var9.nextInt(var1 / 2);
               }

               if(pathfinderMob.z > (double)var20.getZ()) {
                  var19 -= var9.nextInt(var1 / 2);
               } else {
                  var19 += var9.nextInt(var1 / 2);
               }
            }

            BlockPos var20 = new BlockPos((double)var17 + pathfinderMob.x, (double)var18 + pathfinderMob.y, (double)var19 + pathfinderMob.z);
            if((!var10 || pathfinderMob.isWithinRestriction(var20)) && var8.isStableDestination(var20)) {
               if(!var4) {
                  var20 = moveAboveSolid(var20, pathfinderMob);
                  if(isWaterDestination(var20, pathfinderMob)) {
                     continue;
                  }
               }

               double var21 = toDoubleFunction.applyAsDouble(var20);
               if(var21 > var12) {
                  var12 = var21;
                  var14 = var20;
                  var11 = true;
               }
            }
         }
      }

      if(var11) {
         return new Vec3(var14);
      } else {
         return null;
      }
   }

   @Nullable
   private static BlockPos getRandomDelta(Random random, int var1, int var2, @Nullable Vec3 vec3, double var4) {
      if(vec3 != null && var4 < 3.141592653589793D) {
         double var6 = Mth.atan2(vec3.z, vec3.x) - 1.5707963705062866D;
         double var8 = var6 + (double)(2.0F * random.nextFloat() - 1.0F) * var4;
         double var10 = Math.sqrt(random.nextDouble()) * (double)Mth.SQRT_OF_TWO * (double)var1;
         double var12 = -var10 * Math.sin(var8);
         double var14 = var10 * Math.cos(var8);
         if(Math.abs(var12) <= (double)var1 && Math.abs(var14) <= (double)var1) {
            int var16 = random.nextInt(2 * var2 + 1) - var2;
            return new BlockPos(var12, (double)var16, var14);
         } else {
            return null;
         }
      } else {
         int var6 = random.nextInt(2 * var1 + 1) - var1;
         int var7 = random.nextInt(2 * var2 + 1) - var2;
         int var8 = random.nextInt(2 * var1 + 1) - var1;
         return new BlockPos(var6, var7, var8);
      }
   }

   private static BlockPos moveAboveSolid(BlockPos var0, PathfinderMob pathfinderMob) {
      if(!pathfinderMob.level.getBlockState(var0).getMaterial().isSolid()) {
         return var0;
      } else {
         BlockPos var2;
         for(var2 = var0.above(); var2.getY() < pathfinderMob.level.getMaxBuildHeight() && pathfinderMob.level.getBlockState(var2).getMaterial().isSolid(); var2 = var2.above()) {
            ;
         }

         return var2;
      }
   }

   private static boolean isWaterDestination(BlockPos blockPos, PathfinderMob pathfinderMob) {
      return pathfinderMob.level.getFluidState(blockPos).is(FluidTags.WATER);
   }
}
