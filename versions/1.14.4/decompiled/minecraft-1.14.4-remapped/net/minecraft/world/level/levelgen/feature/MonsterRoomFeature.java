package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MonsterRoomFeature extends Feature {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final EntityType[] MOBS = new EntityType[]{EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER};
   private static final BlockState AIR = Blocks.CAVE_AIR.defaultBlockState();

   public MonsterRoomFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      int var6 = 3;
      int var7 = random.nextInt(2) + 2;
      int var8 = -var7 - 1;
      int var9 = var7 + 1;
      int var10 = -1;
      int var11 = 4;
      int var12 = random.nextInt(2) + 2;
      int var13 = -var12 - 1;
      int var14 = var12 + 1;
      int var15 = 0;

      for(int var16 = var8; var16 <= var9; ++var16) {
         for(int var17 = -1; var17 <= 4; ++var17) {
            for(int var18 = var13; var18 <= var14; ++var18) {
               BlockPos var19 = blockPos.offset(var16, var17, var18);
               Material var20 = levelAccessor.getBlockState(var19).getMaterial();
               boolean var21 = var20.isSolid();
               if(var17 == -1 && !var21) {
                  return false;
               }

               if(var17 == 4 && !var21) {
                  return false;
               }

               if((var16 == var8 || var16 == var9 || var18 == var13 || var18 == var14) && var17 == 0 && levelAccessor.isEmptyBlock(var19) && levelAccessor.isEmptyBlock(var19.above())) {
                  ++var15;
               }
            }
         }
      }

      if(var15 >= 1 && var15 <= 5) {
         for(int var16 = var8; var16 <= var9; ++var16) {
            for(int var17 = 3; var17 >= -1; --var17) {
               for(int var18 = var13; var18 <= var14; ++var18) {
                  BlockPos var19 = blockPos.offset(var16, var17, var18);
                  if(var16 != var8 && var17 != -1 && var18 != var13 && var16 != var9 && var17 != 4 && var18 != var14) {
                     if(levelAccessor.getBlockState(var19).getBlock() != Blocks.CHEST) {
                        levelAccessor.setBlock(var19, AIR, 2);
                     }
                  } else if(var19.getY() >= 0 && !levelAccessor.getBlockState(var19.below()).getMaterial().isSolid()) {
                     levelAccessor.setBlock(var19, AIR, 2);
                  } else if(levelAccessor.getBlockState(var19).getMaterial().isSolid() && levelAccessor.getBlockState(var19).getBlock() != Blocks.CHEST) {
                     if(var17 == -1 && random.nextInt(4) != 0) {
                        levelAccessor.setBlock(var19, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);
                     } else {
                        levelAccessor.setBlock(var19, Blocks.COBBLESTONE.defaultBlockState(), 2);
                     }
                  }
               }
            }
         }

         for(int var16 = 0; var16 < 2; ++var16) {
            for(int var17 = 0; var17 < 3; ++var17) {
               int var18 = blockPos.getX() + random.nextInt(var7 * 2 + 1) - var7;
               int var19 = blockPos.getY();
               int var20 = blockPos.getZ() + random.nextInt(var12 * 2 + 1) - var12;
               BlockPos var21 = new BlockPos(var18, var19, var20);
               if(levelAccessor.isEmptyBlock(var21)) {
                  int var22 = 0;

                  for(Direction var24 : Direction.Plane.HORIZONTAL) {
                     if(levelAccessor.getBlockState(var21.relative(var24)).getMaterial().isSolid()) {
                        ++var22;
                     }
                  }

                  if(var22 == 1) {
                     levelAccessor.setBlock(var21, StructurePiece.reorient(levelAccessor, var21, Blocks.CHEST.defaultBlockState()), 2);
                     RandomizableContainerBlockEntity.setLootTable(levelAccessor, random, var21, BuiltInLootTables.SIMPLE_DUNGEON);
                     break;
                  }
               }
            }
         }

         levelAccessor.setBlock(blockPos, Blocks.SPAWNER.defaultBlockState(), 2);
         BlockEntity var16 = levelAccessor.getBlockEntity(blockPos);
         if(var16 instanceof SpawnerBlockEntity) {
            ((SpawnerBlockEntity)var16).getSpawner().setEntityId(this.randomEntityId(random));
         } else {
            LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", Integer.valueOf(blockPos.getX()), Integer.valueOf(blockPos.getY()), Integer.valueOf(blockPos.getZ()));
         }

         return true;
      } else {
         return false;
      }
   }

   private EntityType randomEntityId(Random random) {
      return MOBS[random.nextInt(MOBS.length)];
   }
}
