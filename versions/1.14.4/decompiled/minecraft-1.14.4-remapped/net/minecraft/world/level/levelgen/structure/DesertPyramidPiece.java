package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class DesertPyramidPiece extends ScatteredFeaturePiece {
   private final boolean[] hasPlacedChest = new boolean[4];

   public DesertPyramidPiece(Random random, int var2, int var3) {
      super(StructurePieceType.DESERT_PYRAMID_PIECE, random, var2, 64, var3, 21, 15, 21);
   }

   public DesertPyramidPiece(StructureManager structureManager, CompoundTag compoundTag) {
      super(StructurePieceType.DESERT_PYRAMID_PIECE, compoundTag);
      this.hasPlacedChest[0] = compoundTag.getBoolean("hasPlacedChest0");
      this.hasPlacedChest[1] = compoundTag.getBoolean("hasPlacedChest1");
      this.hasPlacedChest[2] = compoundTag.getBoolean("hasPlacedChest2");
      this.hasPlacedChest[3] = compoundTag.getBoolean("hasPlacedChest3");
   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putBoolean("hasPlacedChest0", this.hasPlacedChest[0]);
      compoundTag.putBoolean("hasPlacedChest1", this.hasPlacedChest[1]);
      compoundTag.putBoolean("hasPlacedChest2", this.hasPlacedChest[2]);
      compoundTag.putBoolean("hasPlacedChest3", this.hasPlacedChest[3]);
   }

   public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
      this.generateBox(levelAccessor, boundingBox, 0, -4, 0, this.width - 1, 0, this.depth - 1, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);

      for(int var5 = 1; var5 <= 9; ++var5) {
         this.generateBox(levelAccessor, boundingBox, var5, var5, var5, this.width - 1 - var5, var5, this.depth - 1 - var5, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, var5 + 1, var5, var5 + 1, this.width - 2 - var5, var5, this.depth - 2 - var5, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      }

      for(int var5 = 0; var5 < this.width; ++var5) {
         for(int var6 = 0; var6 < this.depth; ++var6) {
            int var7 = -5;
            this.fillColumnDown(levelAccessor, Blocks.SANDSTONE.defaultBlockState(), var5, -5, var6, boundingBox);
         }
      }

      BlockState var5 = (BlockState)Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
      BlockState var6 = (BlockState)Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
      BlockState var7 = (BlockState)Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST);
      BlockState var8 = (BlockState)Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST);
      this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.placeBlock(levelAccessor, var5, 2, 10, 0, boundingBox);
      this.placeBlock(levelAccessor, var6, 2, 10, 4, boundingBox);
      this.placeBlock(levelAccessor, var7, 0, 10, 2, boundingBox);
      this.placeBlock(levelAccessor, var8, 4, 10, 2, boundingBox);
      this.generateBox(levelAccessor, boundingBox, this.width - 5, 0, 0, this.width - 1, 9, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, this.width - 4, 10, 1, this.width - 2, 10, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.placeBlock(levelAccessor, var5, this.width - 3, 10, 0, boundingBox);
      this.placeBlock(levelAccessor, var6, this.width - 3, 10, 4, boundingBox);
      this.placeBlock(levelAccessor, var7, this.width - 5, 10, 2, boundingBox);
      this.placeBlock(levelAccessor, var8, this.width - 1, 10, 2, boundingBox);
      this.generateBox(levelAccessor, boundingBox, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, 9, 1, 0, 11, 3, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 1, 1, boundingBox);
      this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 2, 1, boundingBox);
      this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 3, 1, boundingBox);
      this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, 3, 1, boundingBox);
      this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 3, 1, boundingBox);
      this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 2, 1, boundingBox);
      this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 1, 1, boundingBox);
      this.generateBox(levelAccessor, boundingBox, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, 4, 1, 2, 8, 2, 2, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, 12, 1, 2, 16, 2, 2, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, 5, 4, 5, this.width - 6, 4, this.depth - 6, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, 9, 4, 9, 11, 4, 11, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, 8, 1, 8, 8, 3, 8, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, 12, 1, 8, 12, 3, 8, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, 8, 1, 12, 8, 3, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, 12, 1, 12, 12, 3, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, this.width - 5, 1, 5, this.width - 2, 4, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, this.width - 7, 7, 9, this.width - 7, 7, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, 5, 5, 9, 5, 7, 11, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, this.width - 6, 5, 9, this.width - 6, 7, 11, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), 5, 5, 10, boundingBox);
      this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), 5, 6, 10, boundingBox);
      this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), 6, 6, 10, boundingBox);
      this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), this.width - 6, 5, 10, boundingBox);
      this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), this.width - 6, 6, 10, boundingBox);
      this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), this.width - 7, 6, 10, boundingBox);
      this.generateBox(levelAccessor, boundingBox, 2, 4, 4, 2, 6, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, this.width - 3, 4, 4, this.width - 3, 6, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.placeBlock(levelAccessor, var5, 2, 4, 5, boundingBox);
      this.placeBlock(levelAccessor, var5, 2, 3, 4, boundingBox);
      this.placeBlock(levelAccessor, var5, this.width - 3, 4, 5, boundingBox);
      this.placeBlock(levelAccessor, var5, this.width - 3, 3, 4, boundingBox);
      this.generateBox(levelAccessor, boundingBox, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, this.width - 3, 1, 3, this.width - 2, 2, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.placeBlock(levelAccessor, Blocks.SANDSTONE.defaultBlockState(), 1, 1, 2, boundingBox);
      this.placeBlock(levelAccessor, Blocks.SANDSTONE.defaultBlockState(), this.width - 2, 1, 2, boundingBox);
      this.placeBlock(levelAccessor, Blocks.SANDSTONE_SLAB.defaultBlockState(), 1, 2, 2, boundingBox);
      this.placeBlock(levelAccessor, Blocks.SANDSTONE_SLAB.defaultBlockState(), this.width - 2, 2, 2, boundingBox);
      this.placeBlock(levelAccessor, var8, 2, 1, 2, boundingBox);
      this.placeBlock(levelAccessor, var7, this.width - 3, 1, 2, boundingBox);
      this.generateBox(levelAccessor, boundingBox, 4, 3, 5, 4, 3, 17, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, this.width - 5, 3, 5, this.width - 5, 3, 17, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, 3, 1, 5, 4, 2, 16, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, this.width - 6, 1, 5, this.width - 5, 2, 16, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

      for(int var9 = 5; var9 <= 17; var9 += 2) {
         this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), 4, 1, var9, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 4, 2, var9, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), this.width - 5, 1, var9, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CHISELED_SANDSTONE.defaultBlockState(), this.width - 5, 2, var9, boundingBox);
      }

      this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 7, boundingBox);
      this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 8, boundingBox);
      this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 0, 9, boundingBox);
      this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 0, 9, boundingBox);
      this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 8, 0, 10, boundingBox);
      this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 12, 0, 10, boundingBox);
      this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 7, 0, 10, boundingBox);
      this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 13, 0, 10, boundingBox);
      this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 0, 11, boundingBox);
      this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 0, 11, boundingBox);
      this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 12, boundingBox);
      this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 13, boundingBox);
      this.placeBlock(levelAccessor, Blocks.BLUE_TERRACOTTA.defaultBlockState(), 10, 0, 10, boundingBox);

      for(int var9 = 0; var9 <= this.width - 1; var9 += this.width - 1) {
         this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), var9, 2, 1, boundingBox);
         this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var9, 2, 2, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), var9, 2, 3, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), var9, 3, 1, boundingBox);
         this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var9, 3, 2, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), var9, 3, 3, boundingBox);
         this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var9, 4, 1, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CHISELED_SANDSTONE.defaultBlockState(), var9, 4, 2, boundingBox);
         this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var9, 4, 3, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), var9, 5, 1, boundingBox);
         this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var9, 5, 2, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), var9, 5, 3, boundingBox);
         this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var9, 6, 1, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CHISELED_SANDSTONE.defaultBlockState(), var9, 6, 2, boundingBox);
         this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var9, 6, 3, boundingBox);
         this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var9, 7, 1, boundingBox);
         this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var9, 7, 2, boundingBox);
         this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var9, 7, 3, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), var9, 8, 1, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), var9, 8, 2, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), var9, 8, 3, boundingBox);
      }

      for(int var9 = 2; var9 <= this.width - 3; var9 += this.width - 3 - 2) {
         this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), var9 - 1, 2, 0, boundingBox);
         this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var9, 2, 0, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), var9 + 1, 2, 0, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), var9 - 1, 3, 0, boundingBox);
         this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var9, 3, 0, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), var9 + 1, 3, 0, boundingBox);
         this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var9 - 1, 4, 0, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CHISELED_SANDSTONE.defaultBlockState(), var9, 4, 0, boundingBox);
         this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var9 + 1, 4, 0, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), var9 - 1, 5, 0, boundingBox);
         this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var9, 5, 0, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), var9 + 1, 5, 0, boundingBox);
         this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var9 - 1, 6, 0, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CHISELED_SANDSTONE.defaultBlockState(), var9, 6, 0, boundingBox);
         this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var9 + 1, 6, 0, boundingBox);
         this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var9 - 1, 7, 0, boundingBox);
         this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var9, 7, 0, boundingBox);
         this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var9 + 1, 7, 0, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), var9 - 1, 8, 0, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), var9, 8, 0, boundingBox);
         this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), var9 + 1, 8, 0, boundingBox);
      }

      this.generateBox(levelAccessor, boundingBox, 8, 4, 0, 12, 6, 0, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), 8, 6, 0, boundingBox);
      this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), 12, 6, 0, boundingBox);
      this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 5, 0, boundingBox);
      this.placeBlock(levelAccessor, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, 5, 0, boundingBox);
      this.placeBlock(levelAccessor, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 5, 0, boundingBox);
      this.generateBox(levelAccessor, boundingBox, 8, -14, 8, 12, -11, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, 8, -10, 8, 12, -10, 12, Blocks.CHISELED_SANDSTONE.defaultBlockState(), Blocks.CHISELED_SANDSTONE.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, 8, -9, 8, 12, -9, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(levelAccessor, boundingBox, 9, -11, 9, 11, -1, 11, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.placeBlock(levelAccessor, Blocks.STONE_PRESSURE_PLATE.defaultBlockState(), 10, -11, 10, boundingBox);
      this.generateBox(levelAccessor, boundingBox, 9, -13, 9, 11, -13, 11, Blocks.TNT.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), 8, -11, 10, boundingBox);
      this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), 8, -10, 10, boundingBox);
      this.placeBlock(levelAccessor, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 7, -10, 10, boundingBox);
      this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), 7, -11, 10, boundingBox);
      this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), 12, -11, 10, boundingBox);
      this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), 12, -10, 10, boundingBox);
      this.placeBlock(levelAccessor, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 13, -10, 10, boundingBox);
      this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), 13, -11, 10, boundingBox);
      this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), 10, -11, 8, boundingBox);
      this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), 10, -10, 8, boundingBox);
      this.placeBlock(levelAccessor, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, -10, 7, boundingBox);
      this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, -11, 7, boundingBox);
      this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), 10, -11, 12, boundingBox);
      this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), 10, -10, 12, boundingBox);
      this.placeBlock(levelAccessor, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, -10, 13, boundingBox);
      this.placeBlock(levelAccessor, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, -11, 13, boundingBox);

      for(Direction var10 : Direction.Plane.HORIZONTAL) {
         if(!this.hasPlacedChest[var10.get2DDataValue()]) {
            int var11 = var10.getStepX() * 2;
            int var12 = var10.getStepZ() * 2;
            this.hasPlacedChest[var10.get2DDataValue()] = this.createChest(levelAccessor, boundingBox, random, 10 + var11, -11, 10 + var12, BuiltInLootTables.DESERT_PYRAMID);
         }
      }

      return true;
   }
}
