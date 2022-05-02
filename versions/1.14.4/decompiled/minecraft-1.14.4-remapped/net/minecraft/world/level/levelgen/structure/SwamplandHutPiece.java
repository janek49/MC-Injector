package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class SwamplandHutPiece extends ScatteredFeaturePiece {
   private boolean spawnedWitch;
   private boolean spawnedCat;

   public SwamplandHutPiece(Random random, int var2, int var3) {
      super(StructurePieceType.SWAMPLAND_HUT, random, var2, 64, var3, 7, 7, 9);
   }

   public SwamplandHutPiece(StructureManager structureManager, CompoundTag compoundTag) {
      super(StructurePieceType.SWAMPLAND_HUT, compoundTag);
      this.spawnedWitch = compoundTag.getBoolean("Witch");
      this.spawnedCat = compoundTag.getBoolean("Cat");
   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putBoolean("Witch", this.spawnedWitch);
      compoundTag.putBoolean("Cat", this.spawnedCat);
   }

   public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
      if(!this.updateAverageGroundHeight(levelAccessor, boundingBox, 0)) {
         return false;
      } else {
         this.generateBox(levelAccessor, boundingBox, 1, 1, 1, 5, 1, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 4, 2, 5, 4, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 2, 1, 0, 4, 1, 0, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 2, 2, 2, 3, 3, 2, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 2, 3, 1, 3, 6, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 5, 2, 3, 5, 3, 6, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 2, 2, 7, 4, 3, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 0, 2, 1, 3, 2, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 5, 0, 2, 5, 3, 2, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 0, 7, 1, 3, 7, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 5, 0, 7, 5, 3, 7, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
         this.placeBlock(levelAccessor, Blocks.OAK_FENCE.defaultBlockState(), 2, 3, 2, boundingBox);
         this.placeBlock(levelAccessor, Blocks.OAK_FENCE.defaultBlockState(), 3, 3, 7, boundingBox);
         this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), 1, 3, 4, boundingBox);
         this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), 5, 3, 4, boundingBox);
         this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), 5, 3, 5, boundingBox);
         this.placeBlock(levelAccessor, Blocks.POTTED_RED_MUSHROOM.defaultBlockState(), 1, 3, 5, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CRAFTING_TABLE.defaultBlockState(), 3, 2, 6, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CAULDRON.defaultBlockState(), 4, 2, 6, boundingBox);
         this.placeBlock(levelAccessor, Blocks.OAK_FENCE.defaultBlockState(), 1, 2, 1, boundingBox);
         this.placeBlock(levelAccessor, Blocks.OAK_FENCE.defaultBlockState(), 5, 2, 1, boundingBox);
         BlockState var5 = (BlockState)Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
         BlockState var6 = (BlockState)Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST);
         BlockState var7 = (BlockState)Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST);
         BlockState var8 = (BlockState)Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
         this.generateBox(levelAccessor, boundingBox, 0, 4, 1, 6, 4, 1, var5, var5, false);
         this.generateBox(levelAccessor, boundingBox, 0, 4, 2, 0, 4, 7, var6, var6, false);
         this.generateBox(levelAccessor, boundingBox, 6, 4, 2, 6, 4, 7, var7, var7, false);
         this.generateBox(levelAccessor, boundingBox, 0, 4, 8, 6, 4, 8, var8, var8, false);
         this.placeBlock(levelAccessor, (BlockState)var5.setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT), 0, 4, 1, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)var5.setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT), 6, 4, 1, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)var8.setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT), 0, 4, 8, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)var8.setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT), 6, 4, 8, boundingBox);

         for(int var9 = 2; var9 <= 7; var9 += 5) {
            for(int var10 = 1; var10 <= 5; var10 += 4) {
               this.fillColumnDown(levelAccessor, Blocks.OAK_LOG.defaultBlockState(), var10, -1, var9, boundingBox);
            }
         }

         if(!this.spawnedWitch) {
            int var9 = this.getWorldX(2, 5);
            int var10 = this.getWorldY(2);
            int var11 = this.getWorldZ(2, 5);
            if(boundingBox.isInside(new BlockPos(var9, var10, var11))) {
               this.spawnedWitch = true;
               Witch var12 = (Witch)EntityType.WITCH.create(levelAccessor.getLevel());
               var12.setPersistenceRequired();
               var12.moveTo((double)var9 + 0.5D, (double)var10, (double)var11 + 0.5D, 0.0F, 0.0F);
               var12.finalizeSpawn(levelAccessor, levelAccessor.getCurrentDifficultyAt(new BlockPos(var9, var10, var11)), MobSpawnType.STRUCTURE, (SpawnGroupData)null, (CompoundTag)null);
               levelAccessor.addFreshEntity(var12);
            }
         }

         this.spawnCat(levelAccessor, boundingBox);
         return true;
      }
   }

   private void spawnCat(LevelAccessor levelAccessor, BoundingBox boundingBox) {
      if(!this.spawnedCat) {
         int var3 = this.getWorldX(2, 5);
         int var4 = this.getWorldY(2);
         int var5 = this.getWorldZ(2, 5);
         if(boundingBox.isInside(new BlockPos(var3, var4, var5))) {
            this.spawnedCat = true;
            Cat var6 = (Cat)EntityType.CAT.create(levelAccessor.getLevel());
            var6.setPersistenceRequired();
            var6.moveTo((double)var3 + 0.5D, (double)var4, (double)var5 + 0.5D, 0.0F, 0.0F);
            var6.finalizeSpawn(levelAccessor, levelAccessor.getCurrentDifficultyAt(new BlockPos(var3, var4, var5)), MobSpawnType.STRUCTURE, (SpawnGroupData)null, (CompoundTag)null);
            levelAccessor.addFreshEntity(var6);
         }
      }

   }
}
