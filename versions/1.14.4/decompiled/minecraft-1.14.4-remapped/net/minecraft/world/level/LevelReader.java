package net.minecraft.world.level;

import com.google.common.collect.Streams;
import java.util.Collections;
import java.util.Set;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface LevelReader extends BlockAndBiomeGetter {
   default boolean isEmptyBlock(BlockPos blockPos) {
      return this.getBlockState(blockPos).isAir();
   }

   default boolean canSeeSkyFromBelowWater(BlockPos blockPos) {
      if(blockPos.getY() >= this.getSeaLevel()) {
         return this.canSeeSky(blockPos);
      } else {
         BlockPos blockPos = new BlockPos(blockPos.getX(), this.getSeaLevel(), blockPos.getZ());
         if(!this.canSeeSky(blockPos)) {
            return false;
         } else {
            for(blockPos = blockPos.below(); blockPos.getY() > blockPos.getY(); blockPos = blockPos.below()) {
               BlockState var3 = this.getBlockState(blockPos);
               if(var3.getLightBlock(this, blockPos) > 0 && !var3.getMaterial().isLiquid()) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   int getRawBrightness(BlockPos var1, int var2);

   @Nullable
   ChunkAccess getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

   @Deprecated
   boolean hasChunk(int var1, int var2);

   BlockPos getHeightmapPos(Heightmap.Types var1, BlockPos var2);

   int getHeight(Heightmap.Types var1, int var2, int var3);

   default float getBrightness(BlockPos blockPos) {
      return this.getDimension().getBrightnessRamp()[this.getMaxLocalRawBrightness(blockPos)];
   }

   int getSkyDarken();

   WorldBorder getWorldBorder();

   boolean isUnobstructed(@Nullable Entity var1, VoxelShape var2);

   default int getDirectSignal(BlockPos blockPos, Direction direction) {
      return this.getBlockState(blockPos).getDirectSignal(this, blockPos, direction);
   }

   boolean isClientSide();

   int getSeaLevel();

   default ChunkAccess getChunk(BlockPos blockPos) {
      return this.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
   }

   default ChunkAccess getChunk(int var1, int var2) {
      return this.getChunk(var1, var2, ChunkStatus.FULL, true);
   }

   default ChunkAccess getChunk(int var1, int var2, ChunkStatus chunkStatus) {
      return this.getChunk(var1, var2, chunkStatus, true);
   }

   default ChunkStatus statusForCollisions() {
      return ChunkStatus.EMPTY;
   }

   default boolean isUnobstructed(BlockState blockState, BlockPos blockPos, CollisionContext collisionContext) {
      VoxelShape var4 = blockState.getCollisionShape(this, blockPos, collisionContext);
      return var4.isEmpty() || this.isUnobstructed((Entity)null, var4.move((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
   }

   default boolean isUnobstructed(Entity entity) {
      return this.isUnobstructed(entity, Shapes.create(entity.getBoundingBox()));
   }

   default boolean noCollision(AABB aABB) {
      return this.noCollision((Entity)null, aABB, Collections.emptySet());
   }

   default boolean noCollision(Entity entity) {
      return this.noCollision(entity, entity.getBoundingBox(), Collections.emptySet());
   }

   default boolean noCollision(Entity entity, AABB aABB) {
      return this.noCollision(entity, aABB, Collections.emptySet());
   }

   default boolean noCollision(@Nullable Entity entity, AABB aABB, Set set) {
      return this.getCollisions(entity, aABB, set).allMatch(VoxelShape::isEmpty);
   }

   default Stream getEntityCollisions(@Nullable Entity entity, AABB aABB, Set set) {
      return Stream.empty();
   }

   default Stream getCollisions(@Nullable Entity entity, AABB aABB, Set set) {
      return Streams.concat(new Stream[]{this.getBlockCollisions(entity, aABB), this.getEntityCollisions(entity, aABB, set)});
   }

   default Stream getBlockCollisions(@Nullable final Entity entity, AABB aABB) {
      int var3 = Mth.floor(aABB.minX - 1.0E-7D) - 1;
      int var4 = Mth.floor(aABB.maxX + 1.0E-7D) + 1;
      int var5 = Mth.floor(aABB.minY - 1.0E-7D) - 1;
      int var6 = Mth.floor(aABB.maxY + 1.0E-7D) + 1;
      int var7 = Mth.floor(aABB.minZ - 1.0E-7D) - 1;
      int var8 = Mth.floor(aABB.maxZ + 1.0E-7D) + 1;
      final CollisionContext var9 = entity == null?CollisionContext.empty():CollisionContext.of(entity);
      final Cursor3D var10 = new Cursor3D(var3, var5, var7, var4, var6, var8);
      final BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();
      final VoxelShape var12 = Shapes.create(aABB);
      return StreamSupport.stream(new AbstractSpliterator(Long.MAX_VALUE, (int)entity) {
         boolean checkedBorder = entity == null;

         public boolean tryAdvance(Consumer consumer) {
            if(!this.checkedBorder) {
               this.checkedBorder = true;
               VoxelShape var2 = LevelReader.this.getWorldBorder().getCollisionShape();
               boolean var3 = Shapes.joinIsNotEmpty(var2, Shapes.create(entity.getBoundingBox().deflate(1.0E-7D)), BooleanOp.AND);
               boolean var4 = Shapes.joinIsNotEmpty(var2, Shapes.create(entity.getBoundingBox().inflate(1.0E-7D)), BooleanOp.AND);
               if(!var3 && var4) {
                  consumer.accept(var2);
                  return true;
               }
            }

            VoxelShape var11;
            while(true) {
               if(!var10.advance()) {
                  return false;
               }

               int var2 = var10.nextX();
               int var3 = var10.nextY();
               int var4 = var10.nextZ();
               int var5 = var10.getNextType();
               if(var5 != 3) {
                  int var6 = var2 >> 4;
                  int var7 = var4 >> 4;
                  ChunkAccess var8 = LevelReader.this.getChunk(var6, var7, LevelReader.this.statusForCollisions(), false);
                  if(var8 != null) {
                     var11.set(var2, var3, var4);
                     BlockState var9 = var8.getBlockState(var11);
                     if((var5 != 1 || var9.hasLargeCollisionShape()) && (var5 != 2 || var9.getBlock() == Blocks.MOVING_PISTON)) {
                        VoxelShape var10 = var9.getCollisionShape(LevelReader.this, var11, var9);
                        var11 = var10.move((double)var2, (double)var3, (double)var4);
                        if(Shapes.joinIsNotEmpty(var12, var11, BooleanOp.AND)) {
                           break;
                        }
                     }
                  }
               }
            }

            consumer.accept(var11);
            return true;
         }
      }, false);
   }

   default boolean isWaterAt(BlockPos blockPos) {
      return this.getFluidState(blockPos).is(FluidTags.WATER);
   }

   default boolean containsAnyLiquid(AABB aABB) {
      int var2 = Mth.floor(aABB.minX);
      int var3 = Mth.ceil(aABB.maxX);
      int var4 = Mth.floor(aABB.minY);
      int var5 = Mth.ceil(aABB.maxY);
      int var6 = Mth.floor(aABB.minZ);
      int var7 = Mth.ceil(aABB.maxZ);
      BlockPos.PooledMutableBlockPos var8 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var9 = null;

      try {
         for(int var10 = var2; var10 < var3; ++var10) {
            for(int var11 = var4; var11 < var5; ++var11) {
               for(int var12 = var6; var12 < var7; ++var12) {
                  BlockState var13 = this.getBlockState(var8.set(var10, var11, var12));
                  if(!var13.getFluidState().isEmpty()) {
                     boolean var14 = true;
                     return var14;
                  }
               }
            }
         }

         return false;
      } catch (Throwable var24) {
         var9 = var24;
         throw var24;
      } finally {
         if(var8 != null) {
            if(var9 != null) {
               try {
                  var8.close();
               } catch (Throwable var23) {
                  var9.addSuppressed(var23);
               }
            } else {
               var8.close();
            }
         }

      }
   }

   default int getMaxLocalRawBrightness(BlockPos blockPos) {
      return this.getMaxLocalRawBrightness(blockPos, this.getSkyDarken());
   }

   default int getMaxLocalRawBrightness(BlockPos blockPos, int var2) {
      return blockPos.getX() >= -30000000 && blockPos.getZ() >= -30000000 && blockPos.getX() < 30000000 && blockPos.getZ() < 30000000?this.getRawBrightness(blockPos, var2):15;
   }

   @Deprecated
   default boolean hasChunkAt(BlockPos blockPos) {
      return this.hasChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4);
   }

   @Deprecated
   default boolean hasChunksAt(BlockPos var1, BlockPos var2) {
      return this.hasChunksAt(var1.getX(), var1.getY(), var1.getZ(), var2.getX(), var2.getY(), var2.getZ());
   }

   @Deprecated
   default boolean hasChunksAt(int var1, int var2, int var3, int var4, int var5, int var6) {
      if(var5 >= 0 && var2 < 256) {
         var1 = var1 >> 4;
         var3 = var3 >> 4;
         var4 = var4 >> 4;
         var6 = var6 >> 4;

         for(int var7 = var1; var7 <= var4; ++var7) {
            for(int var8 = var3; var8 <= var6; ++var8) {
               if(!this.hasChunk(var7, var8)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   Dimension getDimension();
}
