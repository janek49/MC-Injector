package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class BuriedTreasurePieces {
   public static class BuriedTreasurePiece extends StructurePiece {
      public BuriedTreasurePiece(BlockPos blockPos) {
         super(StructurePieceType.BURIED_TREASURE_PIECE, 0);
         this.boundingBox = new BoundingBox(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
      }

      public BuriedTreasurePiece(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.BURIED_TREASURE_PIECE, compoundTag);
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         int var5 = levelAccessor.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, this.boundingBox.x0, this.boundingBox.z0);
         BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos(this.boundingBox.x0, var5, this.boundingBox.z0);

         while(var6.getY() > 0) {
            BlockState var7 = levelAccessor.getBlockState(var6);
            BlockState var8 = levelAccessor.getBlockState(var6.below());
            if(var8 == Blocks.SANDSTONE.defaultBlockState() || var8 == Blocks.STONE.defaultBlockState() || var8 == Blocks.ANDESITE.defaultBlockState() || var8 == Blocks.GRANITE.defaultBlockState() || var8 == Blocks.DIORITE.defaultBlockState()) {
               BlockState var9 = !var7.isAir() && !this.isLiquid(var7)?var7:Blocks.SAND.defaultBlockState();

               for(Direction var13 : Direction.values()) {
                  BlockPos var14 = var6.relative(var13);
                  BlockState var15 = levelAccessor.getBlockState(var14);
                  if(var15.isAir() || this.isLiquid(var15)) {
                     BlockPos var16 = var14.below();
                     BlockState var17 = levelAccessor.getBlockState(var16);
                     if((var17.isAir() || this.isLiquid(var17)) && var13 != Direction.UP) {
                        levelAccessor.setBlock(var14, var8, 3);
                     } else {
                        levelAccessor.setBlock(var14, var9, 3);
                     }
                  }
               }

               this.boundingBox = new BoundingBox(var6.getX(), var6.getY(), var6.getZ(), var6.getX(), var6.getY(), var6.getZ());
               return this.createChest(levelAccessor, boundingBox, random, var6, BuiltInLootTables.BURIED_TREASURE, (BlockState)null);
            }

            var6.move(0, -1, 0);
         }

         return false;
      }

      private boolean isLiquid(BlockState blockState) {
         return blockState == Blocks.WATER.defaultBlockState() || blockState == Blocks.LAVA.defaultBlockState();
      }
   }
}
