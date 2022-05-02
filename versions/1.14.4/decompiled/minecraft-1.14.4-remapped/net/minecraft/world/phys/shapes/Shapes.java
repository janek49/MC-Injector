package net.minecraft.world.phys.shapes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.CubePointRange;
import net.minecraft.world.phys.shapes.CubeVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteCubeMerger;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.IdenticalMerger;
import net.minecraft.world.phys.shapes.IndexMerger;
import net.minecraft.world.phys.shapes.IndirectMerger;
import net.minecraft.world.phys.shapes.NonOverlappingMerger;
import net.minecraft.world.phys.shapes.SliceShape;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class Shapes {
   private static final VoxelShape BLOCK = (VoxelShape)Util.make(() -> {
      DiscreteVoxelShape var0 = new BitSetDiscreteVoxelShape(1, 1, 1);
      var0.setFull(0, 0, 0, true, true);
      return new CubeVoxelShape(var0);
   });
   public static final VoxelShape INFINITY = box(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
   private static final VoxelShape EMPTY = new ArrayVoxelShape(new BitSetDiscreteVoxelShape(0, 0, 0), new DoubleArrayList(new double[]{0.0D}), new DoubleArrayList(new double[]{0.0D}), new DoubleArrayList(new double[]{0.0D}));

   public static VoxelShape empty() {
      return EMPTY;
   }

   public static VoxelShape block() {
      return BLOCK;
   }

   public static VoxelShape box(double var0, double var2, double var4, double var6, double var8, double var10) {
      return create(new AABB(var0, var2, var4, var6, var8, var10));
   }

   public static VoxelShape create(AABB aABB) {
      int var1 = findBits(aABB.minX, aABB.maxX);
      int var2 = findBits(aABB.minY, aABB.maxY);
      int var3 = findBits(aABB.minZ, aABB.maxZ);
      if(var1 >= 0 && var2 >= 0 && var3 >= 0) {
         if(var1 == 0 && var2 == 0 && var3 == 0) {
            return aABB.contains(0.5D, 0.5D, 0.5D)?block():empty();
         } else {
            int var4 = 1 << var1;
            int var5 = 1 << var2;
            int var6 = 1 << var3;
            int var7 = (int)Math.round(aABB.minX * (double)var4);
            int var8 = (int)Math.round(aABB.maxX * (double)var4);
            int var9 = (int)Math.round(aABB.minY * (double)var5);
            int var10 = (int)Math.round(aABB.maxY * (double)var5);
            int var11 = (int)Math.round(aABB.minZ * (double)var6);
            int var12 = (int)Math.round(aABB.maxZ * (double)var6);
            BitSetDiscreteVoxelShape var13 = new BitSetDiscreteVoxelShape(var4, var5, var6, var7, var9, var11, var8, var10, var12);

            for(long var14 = (long)var7; var14 < (long)var8; ++var14) {
               for(long var16 = (long)var9; var16 < (long)var10; ++var16) {
                  for(long var18 = (long)var11; var18 < (long)var12; ++var18) {
                     var13.setFull((int)var14, (int)var16, (int)var18, false, true);
                  }
               }
            }

            return new CubeVoxelShape(var13);
         }
      } else {
         return new ArrayVoxelShape(BLOCK.shape, new double[]{aABB.minX, aABB.maxX}, new double[]{aABB.minY, aABB.maxY}, new double[]{aABB.minZ, aABB.maxZ});
      }
   }

   private static int findBits(double var0, double var2) {
      if(var0 >= -1.0E-7D && var2 <= 1.0000001D) {
         for(int var4 = 0; var4 <= 3; ++var4) {
            double var5 = var0 * (double)(1 << var4);
            double var7 = var2 * (double)(1 << var4);
            boolean var9 = Math.abs(var5 - Math.floor(var5)) < 1.0E-7D;
            boolean var10 = Math.abs(var7 - Math.floor(var7)) < 1.0E-7D;
            if(var9 && var10) {
               return var4;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   protected static long lcm(int var0, int var1) {
      return (long)var0 * (long)(var1 / IntMath.gcd(var0, var1));
   }

   public static VoxelShape or(VoxelShape var0, VoxelShape var1) {
      return join(var0, var1, BooleanOp.OR);
   }

   public static VoxelShape or(VoxelShape var0, VoxelShape... voxelShapes) {
      return (VoxelShape)Arrays.stream(voxelShapes).reduce(var0, Shapes::or);
   }

   public static VoxelShape join(VoxelShape var0, VoxelShape var1, BooleanOp booleanOp) {
      return joinUnoptimized(var0, var1, booleanOp).optimize();
   }

   public static VoxelShape joinUnoptimized(VoxelShape var0, VoxelShape var1, BooleanOp booleanOp) {
      if(booleanOp.apply(false, false)) {
         throw new IllegalArgumentException();
      } else if(var0 == var1) {
         return booleanOp.apply(true, true)?var0:empty();
      } else {
         boolean var3 = booleanOp.apply(true, false);
         boolean var4 = booleanOp.apply(false, true);
         if(var0.isEmpty()) {
            return var4?var1:empty();
         } else if(var1.isEmpty()) {
            return var3?var0:empty();
         } else {
            IndexMerger var5 = createIndexMerger(1, var0.getCoords(Direction.Axis.X), var1.getCoords(Direction.Axis.X), var3, var4);
            IndexMerger var6 = createIndexMerger(var5.getList().size() - 1, var0.getCoords(Direction.Axis.Y), var1.getCoords(Direction.Axis.Y), var3, var4);
            IndexMerger var7 = createIndexMerger((var5.getList().size() - 1) * (var6.getList().size() - 1), var0.getCoords(Direction.Axis.Z), var1.getCoords(Direction.Axis.Z), var3, var4);
            BitSetDiscreteVoxelShape var8 = BitSetDiscreteVoxelShape.join(var0.shape, var1.shape, var5, var6, var7, booleanOp);
            return (VoxelShape)(var5 instanceof DiscreteCubeMerger && var6 instanceof DiscreteCubeMerger && var7 instanceof DiscreteCubeMerger?new CubeVoxelShape(var8):new ArrayVoxelShape(var8, var5.getList(), var6.getList(), var7.getList()));
         }
      }
   }

   public static boolean joinIsNotEmpty(VoxelShape var0, VoxelShape var1, BooleanOp booleanOp) {
      if(booleanOp.apply(false, false)) {
         throw new IllegalArgumentException();
      } else if(var0 == var1) {
         return booleanOp.apply(true, true);
      } else if(var0.isEmpty()) {
         return booleanOp.apply(false, !var1.isEmpty());
      } else if(var1.isEmpty()) {
         return booleanOp.apply(!var0.isEmpty(), false);
      } else {
         boolean var3 = booleanOp.apply(true, false);
         boolean var4 = booleanOp.apply(false, true);

         for(Direction.Axis var8 : AxisCycle.AXIS_VALUES) {
            if(var0.max(var8) < var1.min(var8) - 1.0E-7D) {
               return var3 || var4;
            }

            if(var1.max(var8) < var0.min(var8) - 1.0E-7D) {
               return var3 || var4;
            }
         }

         IndexMerger var5 = createIndexMerger(1, var0.getCoords(Direction.Axis.X), var1.getCoords(Direction.Axis.X), var3, var4);
         IndexMerger var6 = createIndexMerger(var5.getList().size() - 1, var0.getCoords(Direction.Axis.Y), var1.getCoords(Direction.Axis.Y), var3, var4);
         IndexMerger var7 = createIndexMerger((var5.getList().size() - 1) * (var6.getList().size() - 1), var0.getCoords(Direction.Axis.Z), var1.getCoords(Direction.Axis.Z), var3, var4);
         return joinIsNotEmpty(var5, var6, var7, var0.shape, var1.shape, booleanOp);
      }
   }

   private static boolean joinIsNotEmpty(IndexMerger var0, IndexMerger var1, IndexMerger var2, DiscreteVoxelShape var3, DiscreteVoxelShape var4, BooleanOp booleanOp) {
      return !var0.forMergedIndexes((var5, var6, var7) -> {
         return var1.forMergedIndexes((var6x, var7, var8) -> {
            return var2.forMergedIndexes((var7x, var8, var9) -> {
               return !booleanOp.apply(var3.isFullWide(var5, var6x, var7x), var4.isFullWide(var6, var7, var8));
            });
         });
      });
   }

   public static double collide(Direction.Axis direction$Axis, AABB aABB, Stream stream, double var3) {
      for(Iterator<VoxelShape> var5 = stream.iterator(); var5.hasNext(); var3 = ((VoxelShape)var5.next()).collide(direction$Axis, aABB, var3)) {
         if(Math.abs(var3) < 1.0E-7D) {
            return 0.0D;
         }
      }

      return var3;
   }

   public static double collide(Direction.Axis direction$Axis, AABB aABB, LevelReader levelReader, double var3, CollisionContext collisionContext, Stream stream) {
      return collide(aABB, levelReader, var3, collisionContext, AxisCycle.between(direction$Axis, Direction.Axis.Z), stream);
   }

   private static double collide(AABB aABB, LevelReader levelReader, double var2, CollisionContext collisionContext, AxisCycle axisCycle, Stream stream) {
      if(aABB.getXsize() >= 1.0E-6D && aABB.getYsize() >= 1.0E-6D && aABB.getZsize() >= 1.0E-6D) {
         if(Math.abs(var2) < 1.0E-7D) {
            return 0.0D;
         } else {
            AxisCycle axisCycle = axisCycle.inverse();
            Direction.Axis var8 = axisCycle.cycle(Direction.Axis.X);
            Direction.Axis var9 = axisCycle.cycle(Direction.Axis.Y);
            Direction.Axis var10 = axisCycle.cycle(Direction.Axis.Z);
            BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();
            int var12 = Mth.floor(aABB.min(var8) - 1.0E-7D) - 1;
            int var13 = Mth.floor(aABB.max(var8) + 1.0E-7D) + 1;
            int var14 = Mth.floor(aABB.min(var9) - 1.0E-7D) - 1;
            int var15 = Mth.floor(aABB.max(var9) + 1.0E-7D) + 1;
            double var16 = aABB.min(var10) - 1.0E-7D;
            double var18 = aABB.max(var10) + 1.0E-7D;
            boolean var20 = var2 > 0.0D;
            int var21 = var20?Mth.floor(aABB.max(var10) - 1.0E-7D) - 1:Mth.floor(aABB.min(var10) + 1.0E-7D) + 1;
            int var22 = lastC(var2, var16, var18);
            int var23 = var20?1:-1;
            int var24 = var21;

            while(true) {
               if(var20) {
                  if(var24 > var22) {
                     break;
                  }
               } else if(var24 < var22) {
                  break;
               }

               for(int var25 = var12; var25 <= var13; ++var25) {
                  for(int var26 = var14; var26 <= var15; ++var26) {
                     int var27 = 0;
                     if(var25 == var12 || var25 == var13) {
                        ++var27;
                     }

                     if(var26 == var14 || var26 == var15) {
                        ++var27;
                     }

                     if(var24 == var21 || var24 == var22) {
                        ++var27;
                     }

                     if(var27 < 3) {
                        var11.set(axisCycle, var25, var26, var24);
                        BlockState var28 = levelReader.getBlockState(var11);
                        if((var27 != 1 || var28.hasLargeCollisionShape()) && (var27 != 2 || var28.getBlock() == Blocks.MOVING_PISTON)) {
                           var2 = var28.getCollisionShape(levelReader, var11, collisionContext).collide(var10, aABB.move((double)(-var11.getX()), (double)(-var11.getY()), (double)(-var11.getZ())), var2);
                           if(Math.abs(var2) < 1.0E-7D) {
                              return 0.0D;
                           }

                           var22 = lastC(var2, var16, var18);
                        }
                     }
                  }
               }

               var24 += var23;
            }

            double[] vars24 = new double[]{var2};
            stream.forEach((voxelShape) -> {
               vars24[0] = voxelShape.collide(var10, aABB, vars24[0]);
            });
            return vars24[0];
         }
      } else {
         return var2;
      }
   }

   private static int lastC(double x, double x, double x) {
      return x > 0.0D?Mth.floor(x + x) + 1:Mth.floor(x + x) - 1;
   }

   public static boolean blockOccudes(VoxelShape var0, VoxelShape var1, Direction direction) {
      if(var0 == block() && var1 == block()) {
         return true;
      } else if(var1.isEmpty()) {
         return false;
      } else {
         Direction.Axis var3 = direction.getAxis();
         Direction.AxisDirection var4 = direction.getAxisDirection();
         VoxelShape var5 = var4 == Direction.AxisDirection.POSITIVE?var0:var1;
         VoxelShape var6 = var4 == Direction.AxisDirection.POSITIVE?var1:var0;
         BooleanOp var7 = var4 == Direction.AxisDirection.POSITIVE?BooleanOp.ONLY_FIRST:BooleanOp.ONLY_SECOND;
         return DoubleMath.fuzzyEquals(var5.max(var3), 1.0D, 1.0E-7D) && DoubleMath.fuzzyEquals(var6.min(var3), 0.0D, 1.0E-7D) && !joinIsNotEmpty(new SliceShape(var5, var3, var5.shape.getSize(var3) - 1), new SliceShape(var6, var3, 0), var7);
      }
   }

   public static VoxelShape getFaceShape(VoxelShape var0, Direction direction) {
      if(var0 == block()) {
         return block();
      } else {
         Direction.Axis var4 = direction.getAxis();
         boolean var2;
         int var3;
         if(direction.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            var2 = DoubleMath.fuzzyEquals(var0.max(var4), 1.0D, 1.0E-7D);
            var3 = var0.shape.getSize(var4) - 1;
         } else {
            var2 = DoubleMath.fuzzyEquals(var0.min(var4), 0.0D, 1.0E-7D);
            var3 = 0;
         }

         return (VoxelShape)(!var2?empty():new SliceShape(var0, var4, var3));
      }
   }

   public static boolean mergedFaceOccludes(VoxelShape var0, VoxelShape var1, Direction direction) {
      if(var0 != block() && var1 != block()) {
         Direction.Axis var3 = direction.getAxis();
         Direction.AxisDirection var4 = direction.getAxisDirection();
         VoxelShape var5 = var4 == Direction.AxisDirection.POSITIVE?var0:var1;
         VoxelShape var6 = var4 == Direction.AxisDirection.POSITIVE?var1:var0;
         if(!DoubleMath.fuzzyEquals(var5.max(var3), 1.0D, 1.0E-7D)) {
            var5 = empty();
         }

         if(!DoubleMath.fuzzyEquals(var6.min(var3), 0.0D, 1.0E-7D)) {
            var6 = empty();
         }

         return !joinIsNotEmpty(block(), joinUnoptimized(new SliceShape(var5, var3, var5.shape.getSize(var3) - 1), new SliceShape(var6, var3, 0), BooleanOp.OR), BooleanOp.ONLY_FIRST);
      } else {
         return true;
      }
   }

   public static boolean faceShapeOccludes(VoxelShape var0, VoxelShape var1) {
      return var0 != block() && var1 != block()?(var0.isEmpty() && var1.isEmpty()?false:!joinIsNotEmpty(block(), joinUnoptimized(var0, var1, BooleanOp.OR), BooleanOp.ONLY_FIRST)):true;
   }

   @VisibleForTesting
   protected static IndexMerger createIndexMerger(int var0, DoubleList var1, DoubleList var2, boolean var3, boolean var4) {
      int var5 = var1.size() - 1;
      int var6 = var2.size() - 1;
      if(var1 instanceof CubePointRange && var2 instanceof CubePointRange) {
         long var7 = lcm(var5, var6);
         if((long)var0 * var7 <= 256L) {
            return new DiscreteCubeMerger(var5, var6);
         }
      }

      return (IndexMerger)(var1.getDouble(var5) < var2.getDouble(0) - 1.0E-7D?new NonOverlappingMerger(var1, var2, false):(var2.getDouble(var6) < var1.getDouble(0) - 1.0E-7D?new NonOverlappingMerger(var2, var1, true):(var5 == var6 && Objects.equals(var1, var2)?(var1 instanceof IdenticalMerger?(IndexMerger)var1:(var2 instanceof IdenticalMerger?(IndexMerger)var2:new IdenticalMerger(var1))):new IndirectMerger(var1, var2, var3, var4))));
   }

   public interface DoubleLineConsumer {
      void consume(double var1, double var3, double var5, double var7, double var9, double var11);
   }
}
