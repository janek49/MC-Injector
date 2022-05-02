package net.minecraft.core;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Spliterator.OfInt;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.concurrent.Immutable;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.Serializable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Immutable
public class BlockPos extends Vec3i implements Serializable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final BlockPos ZERO = new BlockPos(0, 0, 0);
   private static final int PACKED_X_LENGTH = 1 + Mth.log2(Mth.smallestEncompassingPowerOfTwo(30000000));
   private static final int PACKED_Z_LENGTH = PACKED_X_LENGTH;
   private static final int PACKED_Y_LENGTH = 64 - PACKED_X_LENGTH - PACKED_Z_LENGTH;
   private static final long PACKED_X_MASK = (1L << PACKED_X_LENGTH) - 1L;
   private static final long PACKED_Y_MASK = (1L << PACKED_Y_LENGTH) - 1L;
   private static final long PACKED_Z_MASK = (1L << PACKED_Z_LENGTH) - 1L;
   private static final int Z_OFFSET = PACKED_Y_LENGTH;
   private static final int X_OFFSET = PACKED_Y_LENGTH + PACKED_Z_LENGTH;

   public BlockPos(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   public BlockPos(double var1, double var3, double var5) {
      super(var1, var3, var5);
   }

   public BlockPos(Entity entity) {
      this(entity.x, entity.y, entity.z);
   }

   public BlockPos(Vec3 vec3) {
      this(vec3.x, vec3.y, vec3.z);
   }

   public BlockPos(Position position) {
      this(position.x(), position.y(), position.z());
   }

   public BlockPos(Vec3i vec3i) {
      this(vec3i.getX(), vec3i.getY(), vec3i.getZ());
   }

   public static BlockPos deserialize(Dynamic dynamic) {
      OfInt var1 = dynamic.asIntStream().spliterator();
      int[] vars2 = new int[3];
      if(var1.tryAdvance((var1) -> {
         vars2[0] = var1;
      }) && var1.tryAdvance((var1) -> {
         vars2[1] = var1;
      })) {
         var1.tryAdvance((var1) -> {
            vars2[2] = var1;
         });
      }

      return new BlockPos(vars2[0], vars2[1], vars2[2]);
   }

   public Object serialize(DynamicOps dynamicOps) {
      return dynamicOps.createIntList(IntStream.of(new int[]{this.getX(), this.getY(), this.getZ()}));
   }

   public static long offset(long var0, Direction direction) {
      return offset(var0, direction.getStepX(), direction.getStepY(), direction.getStepZ());
   }

   public static long offset(long var0, int var2, int var3, int var4) {
      return asLong(getX(var0) + var2, getY(var0) + var3, getZ(var0) + var4);
   }

   public static int getX(long l) {
      return (int)(l << 64 - X_OFFSET - PACKED_X_LENGTH >> 64 - PACKED_X_LENGTH);
   }

   public static int getY(long l) {
      return (int)(l << 64 - PACKED_Y_LENGTH >> 64 - PACKED_Y_LENGTH);
   }

   public static int getZ(long l) {
      return (int)(l << 64 - Z_OFFSET - PACKED_Z_LENGTH >> 64 - PACKED_Z_LENGTH);
   }

   public static BlockPos of(long l) {
      return new BlockPos(getX(l), getY(l), getZ(l));
   }

   public static long asLong(int var0, int var1, int var2) {
      long var3 = 0L;
      var3 = var3 | ((long)var0 & PACKED_X_MASK) << X_OFFSET;
      var3 = var3 | ((long)var1 & PACKED_Y_MASK) << 0;
      var3 = var3 | ((long)var2 & PACKED_Z_MASK) << Z_OFFSET;
      return var3;
   }

   public static long getFlatIndex(long l) {
      return l & -16L;
   }

   public long asLong() {
      return asLong(this.getX(), this.getY(), this.getZ());
   }

   public BlockPos offset(double var1, double var3, double var5) {
      return var1 == 0.0D && var3 == 0.0D && var5 == 0.0D?this:new BlockPos((double)this.getX() + var1, (double)this.getY() + var3, (double)this.getZ() + var5);
   }

   public BlockPos offset(int var1, int var2, int var3) {
      return var1 == 0 && var2 == 0 && var3 == 0?this:new BlockPos(this.getX() + var1, this.getY() + var2, this.getZ() + var3);
   }

   public BlockPos offset(Vec3i vec3i) {
      return this.offset(vec3i.getX(), vec3i.getY(), vec3i.getZ());
   }

   public BlockPos subtract(Vec3i vec3i) {
      return this.offset(-vec3i.getX(), -vec3i.getY(), -vec3i.getZ());
   }

   public BlockPos above() {
      return this.above(1);
   }

   public BlockPos above(int i) {
      return this.relative(Direction.UP, i);
   }

   public BlockPos below() {
      return this.below(1);
   }

   public BlockPos below(int i) {
      return this.relative(Direction.DOWN, i);
   }

   public BlockPos north() {
      return this.north(1);
   }

   public BlockPos north(int i) {
      return this.relative(Direction.NORTH, i);
   }

   public BlockPos south() {
      return this.south(1);
   }

   public BlockPos south(int i) {
      return this.relative(Direction.SOUTH, i);
   }

   public BlockPos west() {
      return this.west(1);
   }

   public BlockPos west(int i) {
      return this.relative(Direction.WEST, i);
   }

   public BlockPos east() {
      return this.east(1);
   }

   public BlockPos east(int i) {
      return this.relative(Direction.EAST, i);
   }

   public BlockPos relative(Direction direction) {
      return this.relative(direction, 1);
   }

   public BlockPos relative(Direction direction, int var2) {
      return var2 == 0?this:new BlockPos(this.getX() + direction.getStepX() * var2, this.getY() + direction.getStepY() * var2, this.getZ() + direction.getStepZ() * var2);
   }

   public BlockPos rotate(Rotation rotation) {
      switch(rotation) {
      case NONE:
      default:
         return this;
      case CLOCKWISE_90:
         return new BlockPos(-this.getZ(), this.getY(), this.getX());
      case CLOCKWISE_180:
         return new BlockPos(-this.getX(), this.getY(), -this.getZ());
      case COUNTERCLOCKWISE_90:
         return new BlockPos(this.getZ(), this.getY(), -this.getX());
      }
   }

   public BlockPos cross(Vec3i vec3i) {
      return new BlockPos(this.getY() * vec3i.getZ() - this.getZ() * vec3i.getY(), this.getZ() * vec3i.getX() - this.getX() * vec3i.getZ(), this.getX() * vec3i.getY() - this.getY() * vec3i.getX());
   }

   public BlockPos immutable() {
      return this;
   }

   public static Iterable betweenClosed(BlockPos var0, BlockPos var1) {
      return betweenClosed(Math.min(var0.getX(), var1.getX()), Math.min(var0.getY(), var1.getY()), Math.min(var0.getZ(), var1.getZ()), Math.max(var0.getX(), var1.getX()), Math.max(var0.getY(), var1.getY()), Math.max(var0.getZ(), var1.getZ()));
   }

   public static Stream betweenClosedStream(BlockPos var0, BlockPos var1) {
      return betweenClosedStream(Math.min(var0.getX(), var1.getX()), Math.min(var0.getY(), var1.getY()), Math.min(var0.getZ(), var1.getZ()), Math.max(var0.getX(), var1.getX()), Math.max(var0.getY(), var1.getY()), Math.max(var0.getZ(), var1.getZ()));
   }

   public static Stream betweenClosedStream(final int var0, final int var1, final int var2, final int var3, final int var4, final int var5) {
      return StreamSupport.stream(new AbstractSpliterator((long)((var3 - var0 + 1) * (var4 - var1 + 1) * (var5 - var2 + 1)), var0) {
         final Cursor3D cursor = new Cursor3D(var0, var1, var2, var3, var4, var5);
         final BlockPos.MutableBlockPos nextPos = new BlockPos.MutableBlockPos();

         public boolean tryAdvance(Consumer consumer) {
            if(this.cursor.advance()) {
               consumer.accept(this.nextPos.set(this.cursor.nextX(), this.cursor.nextY(), this.cursor.nextZ()));
               return true;
            } else {
               return false;
            }
         }
      }, false);
   }

   public static Iterable betweenClosed(int var0, int var1, int var2, int var3, int var4, int var5) {
      return () -> {
         return new AbstractIterator() {
            final Cursor3D cursor = new Cursor3D(var0, var1, var2, var3, var4, var5);
            final BlockPos.MutableBlockPos nextPos = new BlockPos.MutableBlockPos();

            protected BlockPos computeNext() {
               return (BlockPos)(this.cursor.advance()?this.nextPos.set(this.cursor.nextX(), this.cursor.nextY(), this.cursor.nextZ()):(BlockPos)this.endOfData());
            }

            // $FF: synthetic method
            protected Object computeNext() {
               return this.computeNext();
            }
         };
      };
   }

   // $FF: synthetic method
   public Vec3i cross(Vec3i var1) {
      return this.cross(var1);
   }

   public static class MutableBlockPos extends BlockPos {
      protected int x;
      protected int y;
      protected int z;

      public MutableBlockPos() {
         this(0, 0, 0);
      }

      public MutableBlockPos(BlockPos blockPos) {
         this(blockPos.getX(), blockPos.getY(), blockPos.getZ());
      }

      public MutableBlockPos(int x, int y, int z) {
         super(0, 0, 0);
         this.x = x;
         this.y = y;
         this.z = z;
      }

      public MutableBlockPos(double var1, double var3, double var5) {
         this(Mth.floor(var1), Mth.floor(var3), Mth.floor(var5));
      }

      public BlockPos offset(double var1, double var3, double var5) {
         return super.offset(var1, var3, var5).immutable();
      }

      public BlockPos offset(int var1, int var2, int var3) {
         return super.offset(var1, var2, var3).immutable();
      }

      public BlockPos relative(Direction direction, int var2) {
         return super.relative(direction, var2).immutable();
      }

      public BlockPos rotate(Rotation rotation) {
         return super.rotate(rotation).immutable();
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public int getZ() {
         return this.z;
      }

      public BlockPos.MutableBlockPos set(int x, int y, int z) {
         this.x = x;
         this.y = y;
         this.z = z;
         return this;
      }

      public BlockPos.MutableBlockPos set(Entity entity) {
         return this.set(entity.x, entity.y, entity.z);
      }

      public BlockPos.MutableBlockPos set(double var1, double var3, double var5) {
         return this.set(Mth.floor(var1), Mth.floor(var3), Mth.floor(var5));
      }

      public BlockPos.MutableBlockPos set(Vec3i vec3i) {
         return this.set(vec3i.getX(), vec3i.getY(), vec3i.getZ());
      }

      public BlockPos.MutableBlockPos set(long l) {
         return this.set(getX(l), getY(l), getZ(l));
      }

      public BlockPos.MutableBlockPos set(AxisCycle axisCycle, int var2, int var3, int var4) {
         return this.set(axisCycle.cycle(var2, var3, var4, Direction.Axis.X), axisCycle.cycle(var2, var3, var4, Direction.Axis.Y), axisCycle.cycle(var2, var3, var4, Direction.Axis.Z));
      }

      public BlockPos.MutableBlockPos move(Direction direction) {
         return this.move(direction, 1);
      }

      public BlockPos.MutableBlockPos move(Direction direction, int var2) {
         return this.set(this.x + direction.getStepX() * var2, this.y + direction.getStepY() * var2, this.z + direction.getStepZ() * var2);
      }

      public BlockPos.MutableBlockPos move(int var1, int var2, int var3) {
         return this.set(this.x + var1, this.y + var2, this.z + var3);
      }

      public void setX(int x) {
         this.x = x;
      }

      public void setY(int y) {
         this.y = y;
      }

      public void setZ(int z) {
         this.z = z;
      }

      public BlockPos immutable() {
         return new BlockPos(this);
      }

      // $FF: synthetic method
      public Vec3i cross(Vec3i var1) {
         return super.cross(var1);
      }
   }

   public static final class PooledMutableBlockPos extends BlockPos.MutableBlockPos implements AutoCloseable {
      private boolean free;
      private static final List POOL = Lists.newArrayList();

      private PooledMutableBlockPos(int var1, int var2, int var3) {
         super(var1, var2, var3);
      }

      public static BlockPos.PooledMutableBlockPos acquire() {
         return acquire(0, 0, 0);
      }

      public static BlockPos.PooledMutableBlockPos acquire(Entity entity) {
         return acquire(entity.x, entity.y, entity.z);
      }

      public static BlockPos.PooledMutableBlockPos acquire(double x, double x, double x) {
         return acquire(Mth.floor(x), Mth.floor(x), Mth.floor(x));
      }

      public static BlockPos.PooledMutableBlockPos acquire(int var0, int var1, int var2) {
         synchronized(POOL) {
            if(!POOL.isEmpty()) {
               BlockPos.PooledMutableBlockPos var4 = (BlockPos.PooledMutableBlockPos)POOL.remove(POOL.size() - 1);
               if(var4 != null && var4.free) {
                  var4.free = false;
                  var4.set(var0, var1, var2);
                  return var4;
               }
            }
         }

         return new BlockPos.PooledMutableBlockPos(var0, var1, var2);
      }

      public BlockPos.PooledMutableBlockPos set(int var1, int var2, int var3) {
         return (BlockPos.PooledMutableBlockPos)super.set(var1, var2, var3);
      }

      public BlockPos.PooledMutableBlockPos set(Entity entity) {
         return (BlockPos.PooledMutableBlockPos)super.set(entity);
      }

      public BlockPos.PooledMutableBlockPos set(double var1, double var3, double var5) {
         return (BlockPos.PooledMutableBlockPos)super.set(var1, var3, var5);
      }

      public BlockPos.PooledMutableBlockPos set(Vec3i vec3i) {
         return (BlockPos.PooledMutableBlockPos)super.set(vec3i);
      }

      public BlockPos.PooledMutableBlockPos move(Direction direction) {
         return (BlockPos.PooledMutableBlockPos)super.move(direction);
      }

      public BlockPos.PooledMutableBlockPos move(Direction direction, int var2) {
         return (BlockPos.PooledMutableBlockPos)super.move(direction, var2);
      }

      public BlockPos.PooledMutableBlockPos move(int var1, int var2, int var3) {
         return (BlockPos.PooledMutableBlockPos)super.move(var1, var2, var3);
      }

      public void close() {
         synchronized(POOL) {
            if(POOL.size() < 100) {
               POOL.add(this);
            }

            this.free = true;
         }
      }

      // $FF: synthetic method
      public BlockPos.MutableBlockPos move(int var1, int var2, int var3) {
         return this.move(var1, var2, var3);
      }

      // $FF: synthetic method
      public BlockPos.MutableBlockPos move(Direction var1, int var2) {
         return this.move(var1, var2);
      }

      // $FF: synthetic method
      public BlockPos.MutableBlockPos move(Direction var1) {
         return this.move(var1);
      }

      // $FF: synthetic method
      public BlockPos.MutableBlockPos set(Vec3i var1) {
         return this.set(var1);
      }

      // $FF: synthetic method
      public BlockPos.MutableBlockPos set(double var1, double var3, double var5) {
         return this.set(var1, var3, var5);
      }

      // $FF: synthetic method
      public BlockPos.MutableBlockPos set(Entity var1) {
         return this.set(var1);
      }

      // $FF: synthetic method
      public BlockPos.MutableBlockPos set(int var1, int var2, int var3) {
         return this.set(var1, var2, var3);
      }
   }
}
