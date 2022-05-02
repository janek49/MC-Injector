package net.minecraft.world.level.block.state.pattern;

import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.Vec3;

public class BlockPattern {
   private final Predicate[][][] pattern;
   private final int depth;
   private final int height;
   private final int width;

   public BlockPattern(Predicate[][][] pattern) {
      this.pattern = pattern;
      this.depth = pattern.length;
      if(this.depth > 0) {
         this.height = pattern[0].length;
         if(this.height > 0) {
            this.width = pattern[0][0].length;
         } else {
            this.width = 0;
         }
      } else {
         this.height = 0;
         this.width = 0;
      }

   }

   public int getDepth() {
      return this.depth;
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   @Nullable
   private BlockPattern.BlockPatternMatch matches(BlockPos blockPos, Direction var2, Direction var3, LoadingCache loadingCache) {
      for(int var5 = 0; var5 < this.width; ++var5) {
         for(int var6 = 0; var6 < this.height; ++var6) {
            for(int var7 = 0; var7 < this.depth; ++var7) {
               if(!this.pattern[var7][var6][var5].test(loadingCache.getUnchecked(translateAndRotate(blockPos, var2, var3, var5, var6, var7)))) {
                  return null;
               }
            }
         }
      }

      return new BlockPattern.BlockPatternMatch(blockPos, var2, var3, loadingCache, this.width, this.height, this.depth);
   }

   @Nullable
   public BlockPattern.BlockPatternMatch find(LevelReader levelReader, BlockPos blockPos) {
      LoadingCache<BlockPos, BlockInWorld> var3 = createLevelCache(levelReader, false);
      int var4 = Math.max(Math.max(this.width, this.height), this.depth);

      for(BlockPos var6 : BlockPos.betweenClosed(blockPos, blockPos.offset(var4 - 1, var4 - 1, var4 - 1))) {
         for(Direction var10 : Direction.values()) {
            for(Direction var14 : Direction.values()) {
               if(var14 != var10 && var14 != var10.getOpposite()) {
                  BlockPattern.BlockPatternMatch var15 = this.matches(var6, var10, var14, var3);
                  if(var15 != null) {
                     return var15;
                  }
               }
            }
         }
      }

      return null;
   }

   public static LoadingCache createLevelCache(LevelReader levelReader, boolean var1) {
      return CacheBuilder.newBuilder().build(new BlockPattern.BlockCacheLoader(levelReader, var1));
   }

   protected static BlockPos translateAndRotate(BlockPos var0, Direction var1, Direction var2, int var3, int var4, int var5) {
      if(var1 != var2 && var1 != var2.getOpposite()) {
         Vec3i var6 = new Vec3i(var1.getStepX(), var1.getStepY(), var1.getStepZ());
         Vec3i var7 = new Vec3i(var2.getStepX(), var2.getStepY(), var2.getStepZ());
         Vec3i var8 = var6.cross(var7);
         return var0.offset(var7.getX() * -var4 + var8.getX() * var3 + var6.getX() * var5, var7.getY() * -var4 + var8.getY() * var3 + var6.getY() * var5, var7.getZ() * -var4 + var8.getZ() * var3 + var6.getZ() * var5);
      } else {
         throw new IllegalArgumentException("Invalid forwards & up combination");
      }
   }

   static class BlockCacheLoader extends CacheLoader {
      private final LevelReader level;
      private final boolean loadChunks;

      public BlockCacheLoader(LevelReader level, boolean loadChunks) {
         this.level = level;
         this.loadChunks = loadChunks;
      }

      public BlockInWorld load(BlockPos blockPos) throws Exception {
         return new BlockInWorld(this.level, blockPos, this.loadChunks);
      }

      // $FF: synthetic method
      public Object load(Object var1) throws Exception {
         return this.load((BlockPos)var1);
      }
   }

   public static class BlockPatternMatch {
      private final BlockPos frontTopLeft;
      private final Direction forwards;
      private final Direction up;
      private final LoadingCache cache;
      private final int width;
      private final int height;
      private final int depth;

      public BlockPatternMatch(BlockPos frontTopLeft, Direction forwards, Direction up, LoadingCache cache, int width, int height, int depth) {
         this.frontTopLeft = frontTopLeft;
         this.forwards = forwards;
         this.up = up;
         this.cache = cache;
         this.width = width;
         this.height = height;
         this.depth = depth;
      }

      public BlockPos getFrontTopLeft() {
         return this.frontTopLeft;
      }

      public Direction getForwards() {
         return this.forwards;
      }

      public Direction getUp() {
         return this.up;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public BlockInWorld getBlock(int var1, int var2, int var3) {
         return (BlockInWorld)this.cache.getUnchecked(BlockPattern.translateAndRotate(this.frontTopLeft, this.getForwards(), this.getUp(), var1, var2, var3));
      }

      public String toString() {
         return MoreObjects.toStringHelper(this).add("up", this.up).add("forwards", this.forwards).add("frontTopLeft", this.frontTopLeft).toString();
      }

      public BlockPattern.PortalInfo getPortalOutput(Direction direction, BlockPos blockPos, double var3, Vec3 vec3, double var6) {
         Direction direction = this.getForwards();
         Direction var9 = direction.getClockWise();
         double var12 = (double)(this.getFrontTopLeft().getY() + 1) - var3 * (double)this.getHeight();
         double var10;
         double var14;
         if(var9 == Direction.NORTH) {
            var10 = (double)blockPos.getX() + 0.5D;
            var14 = (double)(this.getFrontTopLeft().getZ() + 1) - (1.0D - var6) * (double)this.getWidth();
         } else if(var9 == Direction.SOUTH) {
            var10 = (double)blockPos.getX() + 0.5D;
            var14 = (double)this.getFrontTopLeft().getZ() + (1.0D - var6) * (double)this.getWidth();
         } else if(var9 == Direction.WEST) {
            var10 = (double)(this.getFrontTopLeft().getX() + 1) - (1.0D - var6) * (double)this.getWidth();
            var14 = (double)blockPos.getZ() + 0.5D;
         } else {
            var10 = (double)this.getFrontTopLeft().getX() + (1.0D - var6) * (double)this.getWidth();
            var14 = (double)blockPos.getZ() + 0.5D;
         }

         double var16;
         double var18;
         if(direction.getOpposite() == direction) {
            var16 = vec3.x;
            var18 = vec3.z;
         } else if(direction.getOpposite() == direction.getOpposite()) {
            var16 = -vec3.x;
            var18 = -vec3.z;
         } else if(direction.getOpposite() == direction.getClockWise()) {
            var16 = -vec3.z;
            var18 = vec3.x;
         } else {
            var16 = vec3.z;
            var18 = -vec3.x;
         }

         int var20 = (direction.get2DDataValue() - direction.getOpposite().get2DDataValue()) * 90;
         return new BlockPattern.PortalInfo(new Vec3(var10, var12, var14), new Vec3(var16, vec3.y, var18), var20);
      }
   }

   public static class PortalInfo {
      public final Vec3 pos;
      public final Vec3 speed;
      public final int angle;

      public PortalInfo(Vec3 pos, Vec3 speed, int angle) {
         this.pos = pos;
         this.speed = speed;
         this.angle = angle;
      }
   }
}
