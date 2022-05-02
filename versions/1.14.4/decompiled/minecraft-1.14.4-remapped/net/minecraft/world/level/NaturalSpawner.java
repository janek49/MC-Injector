package net.minecraft.world.level;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.WeighedRandom;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NaturalSpawner {
   private static final Logger LOGGER = LogManager.getLogger();

   public static void spawnCategoryForChunk(MobCategory mobCategory, Level level, LevelChunk levelChunk, BlockPos blockPos) {
      ChunkGenerator<?> var4 = level.getChunkSource().getGenerator();
      int var5 = 0;
      BlockPos var6 = getRandomPosWithin(level, levelChunk);
      int var7 = var6.getX();
      int var8 = var6.getY();
      int var9 = var6.getZ();
      if(var8 >= 1) {
         BlockState var10 = levelChunk.getBlockState(var6);
         if(!var10.isRedstoneConductor(levelChunk, var6)) {
            BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();
            int var12 = 0;

            while(var12 < 3) {
               int var13 = var7;
               int var14 = var9;
               int var15 = 6;
               Biome.SpawnerData var16 = null;
               SpawnGroupData var17 = null;
               int var18 = Mth.ceil(Math.random() * 4.0D);
               int var19 = 0;
               int var20 = 0;

               while(true) {
                  label121: {
                     if(var20 < var18) {
                        label128: {
                           var13 += level.random.nextInt(6) - level.random.nextInt(6);
                           var14 += level.random.nextInt(6) - level.random.nextInt(6);
                           var11.set(var13, var8, var14);
                           float var21 = (float)var13 + 0.5F;
                           float var22 = (float)var14 + 0.5F;
                           Player var23 = level.getNearestPlayerIgnoreY((double)var21, (double)var22, -1.0D);
                           if(var23 == null) {
                              break label121;
                           }

                           double var24 = var23.distanceToSqr((double)var21, (double)var8, (double)var22);
                           if(var24 <= 576.0D || blockPos.closerThan(new Vec3((double)var21, (double)var8, (double)var22), 24.0D)) {
                              break label121;
                           }

                           ChunkPos var26 = new ChunkPos(var11);
                           if(!Objects.equals(var26, levelChunk.getPos()) && !level.getChunkSource().isEntityTickingChunk(var26)) {
                              break label121;
                           }

                           if(var16 == null) {
                              var16 = getRandomSpawnMobAt(var4, mobCategory, level.random, var11);
                              if(var16 == null) {
                                 break label128;
                              }

                              var18 = var16.minCount + level.random.nextInt(1 + var16.maxCount - var16.minCount);
                           }

                           if(var16.type.getCategory() == MobCategory.MISC || !var16.type.canSpawnFarFromPlayer() && var24 > 16384.0D) {
                              break label121;
                           }

                           EntityType<?> var27 = var16.type;
                           if(!var27.canSummon() || !canSpawnMobAt(var4, mobCategory, var16, var11)) {
                              break label121;
                           }

                           SpawnPlacements.Type var28 = SpawnPlacements.getPlacementType(var27);
                           if(!isSpawnPositionOk(var28, level, var11, var27) || !SpawnPlacements.checkSpawnRules(var27, level, MobSpawnType.NATURAL, var11, level.random) || !level.noCollision(var27.getAABB((double)var21, (double)var8, (double)var22))) {
                              break label121;
                           }

                           Mob var29;
                           try {
                              Entity var30 = var27.create(level);
                              if(!(var30 instanceof Mob)) {
                                 throw new IllegalStateException("Trying to spawn a non-mob: " + Registry.ENTITY_TYPE.getKey(var27));
                              }

                              var29 = (Mob)var30;
                           } catch (Exception var31) {
                              LOGGER.warn("Failed to create mob", var31);
                              return;
                           }

                           var29.moveTo((double)var21, (double)var8, (double)var22, level.random.nextFloat() * 360.0F, 0.0F);
                           if(var24 > 16384.0D && var29.removeWhenFarAway(var24) || !var29.checkSpawnRules(level, MobSpawnType.NATURAL) || !var29.checkSpawnObstruction(level)) {
                              break label121;
                           }

                           var17 = var29.finalizeSpawn(level, level.getCurrentDifficultyAt(new BlockPos(var29)), MobSpawnType.NATURAL, var17, (CompoundTag)null);
                           ++var5;
                           ++var19;
                           level.addFreshEntity(var29);
                           if(var5 >= var29.getMaxSpawnClusterSize()) {
                              return;
                           }

                           if(!var29.isMaxGroupSizeReached(var19)) {
                              break label121;
                           }
                        }
                     }

                     ++var12;
                     break;
                  }

                  ++var20;
               }
            }

         }
      }
   }

   @Nullable
   private static Biome.SpawnerData getRandomSpawnMobAt(ChunkGenerator chunkGenerator, MobCategory mobCategory, Random random, BlockPos blockPos) {
      List<Biome.SpawnerData> var4 = chunkGenerator.getMobsAt(mobCategory, blockPos);
      return var4.isEmpty()?null:(Biome.SpawnerData)WeighedRandom.getRandomItem(random, var4);
   }

   private static boolean canSpawnMobAt(ChunkGenerator chunkGenerator, MobCategory mobCategory, Biome.SpawnerData biome$SpawnerData, BlockPos blockPos) {
      List<Biome.SpawnerData> var4 = chunkGenerator.getMobsAt(mobCategory, blockPos);
      return var4.isEmpty()?false:var4.contains(biome$SpawnerData);
   }

   private static BlockPos getRandomPosWithin(Level level, LevelChunk levelChunk) {
      ChunkPos var2 = levelChunk.getPos();
      int var3 = var2.getMinBlockX() + level.random.nextInt(16);
      int var4 = var2.getMinBlockZ() + level.random.nextInt(16);
      int var5 = levelChunk.getHeight(Heightmap.Types.WORLD_SURFACE, var3, var4) + 1;
      int var6 = level.random.nextInt(var5 + 1);
      return new BlockPos(var3, var6, var4);
   }

   public static boolean isValidEmptySpawnBlock(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
      return blockState.isCollisionShapeFullBlock(blockGetter, blockPos)?false:(blockState.isSignalSource()?false:(!fluidState.isEmpty()?false:!blockState.is(BlockTags.RAILS)));
   }

   public static boolean isSpawnPositionOk(SpawnPlacements.Type spawnPlacements$Type, LevelReader levelReader, BlockPos blockPos, @Nullable EntityType entityType) {
      if(spawnPlacements$Type == SpawnPlacements.Type.NO_RESTRICTIONS) {
         return true;
      } else if(entityType != null && levelReader.getWorldBorder().isWithinBounds(blockPos)) {
         BlockState var4 = levelReader.getBlockState(blockPos);
         FluidState var5 = levelReader.getFluidState(blockPos);
         BlockPos var6 = blockPos.above();
         BlockPos var7 = blockPos.below();
         switch(spawnPlacements$Type) {
         case IN_WATER:
            return var5.is(FluidTags.WATER) && levelReader.getFluidState(var7).is(FluidTags.WATER) && !levelReader.getBlockState(var6).isRedstoneConductor(levelReader, var6);
         case ON_GROUND:
         default:
            BlockState var8 = levelReader.getBlockState(var7);
            return !var8.isValidSpawn(levelReader, var7, entityType)?false:isValidEmptySpawnBlock(levelReader, blockPos, var4, var5) && isValidEmptySpawnBlock(levelReader, var6, levelReader.getBlockState(var6), levelReader.getFluidState(var6));
         }
      } else {
         return false;
      }
   }

   public static void spawnMobsForChunkGeneration(LevelAccessor levelAccessor, Biome biome, int var2, int var3, Random random) {
      List<Biome.SpawnerData> var5 = biome.getMobs(MobCategory.CREATURE);
      if(!var5.isEmpty()) {
         int var6 = var2 << 4;
         int var7 = var3 << 4;

         while(random.nextFloat() < biome.getCreatureProbability()) {
            Biome.SpawnerData var8 = (Biome.SpawnerData)WeighedRandom.getRandomItem(random, var5);
            int var9 = var8.minCount + random.nextInt(1 + var8.maxCount - var8.minCount);
            SpawnGroupData var10 = null;
            int var11 = var6 + random.nextInt(16);
            int var12 = var7 + random.nextInt(16);
            int var13 = var11;
            int var14 = var12;

            for(int var15 = 0; var15 < var9; ++var15) {
               boolean var16 = false;

               for(int var17 = 0; !var16 && var17 < 4; ++var17) {
                  BlockPos var18 = getTopNonCollidingPos(levelAccessor, var8.type, var11, var12);
                  if(var8.type.canSummon() && isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, levelAccessor, var18, var8.type)) {
                     float var19 = var8.type.getWidth();
                     double var20 = Mth.clamp((double)var11, (double)var6 + (double)var19, (double)var6 + 16.0D - (double)var19);
                     double var22 = Mth.clamp((double)var12, (double)var7 + (double)var19, (double)var7 + 16.0D - (double)var19);
                     if(!levelAccessor.noCollision(var8.type.getAABB(var20, (double)var18.getY(), var22)) || !SpawnPlacements.checkSpawnRules(var8.type, levelAccessor, MobSpawnType.CHUNK_GENERATION, new BlockPos(var20, (double)var18.getY(), var22), levelAccessor.getRandom())) {
                        continue;
                     }

                     Entity var24;
                     try {
                        var24 = var8.type.create(levelAccessor.getLevel());
                     } catch (Exception var26) {
                        LOGGER.warn("Failed to create mob", var26);
                        continue;
                     }

                     var24.moveTo(var20, (double)var18.getY(), var22, random.nextFloat() * 360.0F, 0.0F);
                     if(var24 instanceof Mob) {
                        Mob var25 = (Mob)var24;
                        if(var25.checkSpawnRules(levelAccessor, MobSpawnType.CHUNK_GENERATION) && var25.checkSpawnObstruction(levelAccessor)) {
                           var10 = var25.finalizeSpawn(levelAccessor, levelAccessor.getCurrentDifficultyAt(new BlockPos(var25)), MobSpawnType.CHUNK_GENERATION, var10, (CompoundTag)null);
                           levelAccessor.addFreshEntity(var25);
                           var16 = true;
                        }
                     }
                  }

                  var11 += random.nextInt(5) - random.nextInt(5);

                  for(var12 += random.nextInt(5) - random.nextInt(5); var11 < var6 || var11 >= var6 + 16 || var12 < var7 || var12 >= var7 + 16; var12 = var14 + random.nextInt(5) - random.nextInt(5)) {
                     var11 = var13 + random.nextInt(5) - random.nextInt(5);
                  }
               }
            }
         }

      }
   }

   private static BlockPos getTopNonCollidingPos(LevelReader levelReader, @Nullable EntityType entityType, int var2, int var3) {
      BlockPos blockPos = new BlockPos(var2, levelReader.getHeight(SpawnPlacements.getHeightmapType(entityType), var2, var3), var3);
      BlockPos var5 = blockPos.below();
      return levelReader.getBlockState(var5).isPathfindable(levelReader, var5, PathComputationType.LAND)?var5:blockPos;
   }
}
