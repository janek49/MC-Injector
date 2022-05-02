package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class StrongholdPieces {
   private static final StrongholdPieces.PieceWeight[] STRONGHOLD_PIECE_WEIGHTS = new StrongholdPieces.PieceWeight[]{new StrongholdPieces.PieceWeight(StrongholdPieces.Straight.class, 40, 0), new StrongholdPieces.PieceWeight(StrongholdPieces.PrisonHall.class, 5, 5), new StrongholdPieces.PieceWeight(StrongholdPieces.LeftTurn.class, 20, 0), new StrongholdPieces.PieceWeight(StrongholdPieces.RightTurn.class, 20, 0), new StrongholdPieces.PieceWeight(StrongholdPieces.RoomCrossing.class, 10, 6), new StrongholdPieces.PieceWeight(StrongholdPieces.StraightStairsDown.class, 5, 5), new StrongholdPieces.PieceWeight(StrongholdPieces.StairsDown.class, 5, 5), new StrongholdPieces.PieceWeight(StrongholdPieces.FiveCrossing.class, 5, 4), new StrongholdPieces.PieceWeight(StrongholdPieces.ChestCorridor.class, 5, 4), new StrongholdPieces.PieceWeight(StrongholdPieces.Library.class, 10, 2) {
      public boolean doPlace(int i) {
         return super.doPlace(i) && i > 4;
      }
   }, new StrongholdPieces.PieceWeight(StrongholdPieces.PortalRoom.class, 20, 1) {
      public boolean doPlace(int i) {
         return super.doPlace(i) && i > 5;
      }
   }};
   private static List currentPieces;
   private static Class imposedPiece;
   private static int totalWeight;
   private static final StrongholdPieces.SmoothStoneSelector SMOOTH_STONE_SELECTOR = new StrongholdPieces.SmoothStoneSelector();

   public static void resetPieces() {
      currentPieces = Lists.newArrayList();

      for(StrongholdPieces.PieceWeight var3 : STRONGHOLD_PIECE_WEIGHTS) {
         var3.placeCount = 0;
         currentPieces.add(var3);
      }

      imposedPiece = null;
   }

   private static boolean updatePieceWeight() {
      boolean var0 = false;
      totalWeight = 0;

      for(StrongholdPieces.PieceWeight var2 : currentPieces) {
         if(var2.maxPlaceCount > 0 && var2.placeCount < var2.maxPlaceCount) {
            var0 = true;
         }

         totalWeight += var2.weight;
      }

      return var0;
   }

   private static StrongholdPieces.StrongholdPiece findAndCreatePieceFactory(Class class, List list, Random random, int var3, int var4, int var5, @Nullable Direction direction, int var7) {
      StrongholdPieces.StrongholdPiece strongholdPieces$StrongholdPiece = null;
      if(class == StrongholdPieces.Straight.class) {
         strongholdPieces$StrongholdPiece = StrongholdPieces.Straight.createPiece(list, random, var3, var4, var5, direction, var7);
      } else if(class == StrongholdPieces.PrisonHall.class) {
         strongholdPieces$StrongholdPiece = StrongholdPieces.PrisonHall.createPiece(list, random, var3, var4, var5, direction, var7);
      } else if(class == StrongholdPieces.LeftTurn.class) {
         strongholdPieces$StrongholdPiece = StrongholdPieces.LeftTurn.createPiece(list, random, var3, var4, var5, direction, var7);
      } else if(class == StrongholdPieces.RightTurn.class) {
         strongholdPieces$StrongholdPiece = StrongholdPieces.RightTurn.createPiece(list, random, var3, var4, var5, direction, var7);
      } else if(class == StrongholdPieces.RoomCrossing.class) {
         strongholdPieces$StrongholdPiece = StrongholdPieces.RoomCrossing.createPiece(list, random, var3, var4, var5, direction, var7);
      } else if(class == StrongholdPieces.StraightStairsDown.class) {
         strongholdPieces$StrongholdPiece = StrongholdPieces.StraightStairsDown.createPiece(list, random, var3, var4, var5, direction, var7);
      } else if(class == StrongholdPieces.StairsDown.class) {
         strongholdPieces$StrongholdPiece = StrongholdPieces.StairsDown.createPiece(list, random, var3, var4, var5, direction, var7);
      } else if(class == StrongholdPieces.FiveCrossing.class) {
         strongholdPieces$StrongholdPiece = StrongholdPieces.FiveCrossing.createPiece(list, random, var3, var4, var5, direction, var7);
      } else if(class == StrongholdPieces.ChestCorridor.class) {
         strongholdPieces$StrongholdPiece = StrongholdPieces.ChestCorridor.createPiece(list, random, var3, var4, var5, direction, var7);
      } else if(class == StrongholdPieces.Library.class) {
         strongholdPieces$StrongholdPiece = StrongholdPieces.Library.createPiece(list, random, var3, var4, var5, direction, var7);
      } else if(class == StrongholdPieces.PortalRoom.class) {
         strongholdPieces$StrongholdPiece = StrongholdPieces.PortalRoom.createPiece(list, var3, var4, var5, direction, var7);
      }

      return strongholdPieces$StrongholdPiece;
   }

   private static StrongholdPieces.StrongholdPiece generatePieceFromSmallDoor(StrongholdPieces.StartPiece strongholdPieces$StartPiece, List list, Random random, int var3, int var4, int var5, Direction direction, int var7) {
      if(!updatePieceWeight()) {
         return null;
      } else {
         if(imposedPiece != null) {
            StrongholdPieces.StrongholdPiece strongholdPieces$StrongholdPiece = findAndCreatePieceFactory(imposedPiece, list, random, var3, var4, var5, direction, var7);
            imposedPiece = null;
            if(strongholdPieces$StrongholdPiece != null) {
               return strongholdPieces$StrongholdPiece;
            }
         }

         int var8 = 0;

         while(var8 < 5) {
            ++var8;
            int var9 = random.nextInt(totalWeight);

            for(StrongholdPieces.PieceWeight var11 : currentPieces) {
               var9 -= var11.weight;
               if(var9 < 0) {
                  if(!var11.doPlace(var7) || var11 == strongholdPieces$StartPiece.previousPiece) {
                     break;
                  }

                  StrongholdPieces.StrongholdPiece var12 = findAndCreatePieceFactory(var11.pieceClass, list, random, var3, var4, var5, direction, var7);
                  if(var12 != null) {
                     ++var11.placeCount;
                     strongholdPieces$StartPiece.previousPiece = var11;
                     if(!var11.isValid()) {
                        currentPieces.remove(var11);
                     }

                     return var12;
                  }
               }
            }
         }

         BoundingBox var9 = StrongholdPieces.FillerCorridor.findPieceBox(list, random, var3, var4, var5, direction);
         if(var9 != null && var9.y0 > 1) {
            return new StrongholdPieces.FillerCorridor(var7, var9, direction);
         } else {
            return null;
         }
      }
   }

   private static StructurePiece generateAndAddPiece(StrongholdPieces.StartPiece strongholdPieces$StartPiece, List list, Random random, int var3, int var4, int var5, @Nullable Direction direction, int var7) {
      if(var7 > 50) {
         return null;
      } else if(Math.abs(var3 - strongholdPieces$StartPiece.getBoundingBox().x0) <= 112 && Math.abs(var5 - strongholdPieces$StartPiece.getBoundingBox().z0) <= 112) {
         StructurePiece structurePiece = generatePieceFromSmallDoor(strongholdPieces$StartPiece, list, random, var3, var4, var5, direction, var7 + 1);
         if(structurePiece != null) {
            list.add(structurePiece);
            strongholdPieces$StartPiece.pendingChildren.add(structurePiece);
         }

         return structurePiece;
      } else {
         return null;
      }
   }

   public static class ChestCorridor extends StrongholdPieces.StrongholdPiece {
      private boolean hasPlacedChest;

      public ChestCorridor(int var1, Random random, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, var1);
         this.setOrientation(orientation);
         this.entryDoor = this.randomSmallDoor(random);
         this.boundingBox = boundingBox;
      }

      public ChestCorridor(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, compoundTag);
         this.hasPlacedChest = compoundTag.getBoolean("Chest");
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         super.addAdditionalSaveData(compoundTag);
         compoundTag.putBoolean("Chest", this.hasPlacedChest);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)structurePiece, list, random, 1, 1);
      }

      public static StrongholdPieces.ChestCorridor createPiece(List list, Random random, int var2, int var3, int var4, Direction direction, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, 7, direction);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(list, var7) == null?new StrongholdPieces.ChestCorridor(var6, random, var7, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 4, 4, 6, true, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(levelAccessor, random, boundingBox, this.entryDoor, 1, 1, 0);
         this.generateSmallDoor(levelAccessor, random, boundingBox, StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING, 1, 1, 6);
         this.generateBox(levelAccessor, boundingBox, 3, 1, 2, 3, 1, 4, Blocks.STONE_BRICKS.defaultBlockState(), Blocks.STONE_BRICKS.defaultBlockState(), false);
         this.placeBlock(levelAccessor, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 1, 1, boundingBox);
         this.placeBlock(levelAccessor, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 1, 5, boundingBox);
         this.placeBlock(levelAccessor, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 2, 2, boundingBox);
         this.placeBlock(levelAccessor, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 2, 4, boundingBox);

         for(int var5 = 2; var5 <= 4; ++var5) {
            this.placeBlock(levelAccessor, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 2, 1, var5, boundingBox);
         }

         if(!this.hasPlacedChest && boundingBox.isInside(new BlockPos(this.getWorldX(3, 3), this.getWorldY(2), this.getWorldZ(3, 3)))) {
            this.hasPlacedChest = true;
            this.createChest(levelAccessor, boundingBox, random, 3, 2, 3, BuiltInLootTables.STRONGHOLD_CORRIDOR);
         }

         return true;
      }
   }

   public static class FillerCorridor extends StrongholdPieces.StrongholdPiece {
      private final int steps;

      public FillerCorridor(int var1, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.STRONGHOLD_FILLER_CORRIDOR, var1);
         this.setOrientation(orientation);
         this.boundingBox = boundingBox;
         this.steps = orientation != Direction.NORTH && orientation != Direction.SOUTH?boundingBox.getXSpan():boundingBox.getZSpan();
      }

      public FillerCorridor(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.STRONGHOLD_FILLER_CORRIDOR, compoundTag);
         this.steps = compoundTag.getInt("Steps");
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         super.addAdditionalSaveData(compoundTag);
         compoundTag.putInt("Steps", this.steps);
      }

      public static BoundingBox findPieceBox(List list, Random random, int var2, int var3, int var4, Direction direction) {
         int var6 = 3;
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, 4, direction);
         StructurePiece var8 = StructurePiece.findCollisionPiece(list, var7);
         if(var8 == null) {
            return null;
         } else {
            if(var8.getBoundingBox().y0 == var7.y0) {
               for(int var9 = 3; var9 >= 1; --var9) {
                  var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, var9 - 1, direction);
                  if(!var8.getBoundingBox().intersects(var7)) {
                     return BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, var9, direction);
                  }
               }
            }

            return null;
         }
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         for(int var5 = 0; var5 < this.steps; ++var5) {
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 0, 0, var5, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 1, 0, var5, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 2, 0, var5, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 3, 0, var5, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 4, 0, var5, boundingBox);

            for(int var6 = 1; var6 <= 3; ++var6) {
               this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 0, var6, var5, boundingBox);
               this.placeBlock(levelAccessor, Blocks.CAVE_AIR.defaultBlockState(), 1, var6, var5, boundingBox);
               this.placeBlock(levelAccessor, Blocks.CAVE_AIR.defaultBlockState(), 2, var6, var5, boundingBox);
               this.placeBlock(levelAccessor, Blocks.CAVE_AIR.defaultBlockState(), 3, var6, var5, boundingBox);
               this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 4, var6, var5, boundingBox);
            }

            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 0, 4, var5, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 1, 4, var5, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 2, 4, var5, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 3, 4, var5, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 4, 4, var5, boundingBox);
         }

         return true;
      }
   }

   public static class FiveCrossing extends StrongholdPieces.StrongholdPiece {
      private final boolean leftLow;
      private final boolean leftHigh;
      private final boolean rightLow;
      private final boolean rightHigh;

      public FiveCrossing(int var1, Random random, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.STRONGHOLD_FIVE_CROSSING, var1);
         this.setOrientation(orientation);
         this.entryDoor = this.randomSmallDoor(random);
         this.boundingBox = boundingBox;
         this.leftLow = random.nextBoolean();
         this.leftHigh = random.nextBoolean();
         this.rightLow = random.nextBoolean();
         this.rightHigh = random.nextInt(3) > 0;
      }

      public FiveCrossing(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.STRONGHOLD_FIVE_CROSSING, compoundTag);
         this.leftLow = compoundTag.getBoolean("leftLow");
         this.leftHigh = compoundTag.getBoolean("leftHigh");
         this.rightLow = compoundTag.getBoolean("rightLow");
         this.rightHigh = compoundTag.getBoolean("rightHigh");
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         super.addAdditionalSaveData(compoundTag);
         compoundTag.putBoolean("leftLow", this.leftLow);
         compoundTag.putBoolean("leftHigh", this.leftHigh);
         compoundTag.putBoolean("rightLow", this.rightLow);
         compoundTag.putBoolean("rightHigh", this.rightHigh);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         int var4 = 3;
         int var5 = 5;
         Direction var6 = this.getOrientation();
         if(var6 == Direction.WEST || var6 == Direction.NORTH) {
            var4 = 8 - var4;
            var5 = 8 - var5;
         }

         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)structurePiece, list, random, 5, 1);
         if(this.leftLow) {
            this.generateSmallDoorChildLeft((StrongholdPieces.StartPiece)structurePiece, list, random, var4, 1);
         }

         if(this.leftHigh) {
            this.generateSmallDoorChildLeft((StrongholdPieces.StartPiece)structurePiece, list, random, var5, 7);
         }

         if(this.rightLow) {
            this.generateSmallDoorChildRight((StrongholdPieces.StartPiece)structurePiece, list, random, var4, 1);
         }

         if(this.rightHigh) {
            this.generateSmallDoorChildRight((StrongholdPieces.StartPiece)structurePiece, list, random, var5, 7);
         }

      }

      public static StrongholdPieces.FiveCrossing createPiece(List list, Random random, int var2, int var3, int var4, Direction direction, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -4, -3, 0, 10, 9, 11, direction);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(list, var7) == null?new StrongholdPieces.FiveCrossing(var6, random, var7, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 9, 8, 10, true, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(levelAccessor, random, boundingBox, this.entryDoor, 4, 3, 0);
         if(this.leftLow) {
            this.generateBox(levelAccessor, boundingBox, 0, 3, 1, 0, 5, 3, CAVE_AIR, CAVE_AIR, false);
         }

         if(this.rightLow) {
            this.generateBox(levelAccessor, boundingBox, 9, 3, 1, 9, 5, 3, CAVE_AIR, CAVE_AIR, false);
         }

         if(this.leftHigh) {
            this.generateBox(levelAccessor, boundingBox, 0, 5, 7, 0, 7, 9, CAVE_AIR, CAVE_AIR, false);
         }

         if(this.rightHigh) {
            this.generateBox(levelAccessor, boundingBox, 9, 5, 7, 9, 7, 9, CAVE_AIR, CAVE_AIR, false);
         }

         this.generateBox(levelAccessor, boundingBox, 5, 1, 10, 7, 3, 10, CAVE_AIR, CAVE_AIR, false);
         this.generateBox(levelAccessor, boundingBox, 1, 2, 1, 8, 2, 6, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(levelAccessor, boundingBox, 4, 1, 5, 4, 4, 9, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(levelAccessor, boundingBox, 8, 1, 5, 8, 4, 9, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(levelAccessor, boundingBox, 1, 4, 7, 3, 4, 9, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(levelAccessor, boundingBox, 1, 3, 5, 3, 3, 6, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(levelAccessor, boundingBox, 1, 3, 4, 3, 3, 4, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 4, 6, 3, 4, 6, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 5, 1, 7, 7, 1, 8, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(levelAccessor, boundingBox, 5, 1, 9, 7, 1, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 5, 2, 7, 7, 2, 7, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 4, 5, 7, 4, 5, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 8, 5, 7, 8, 5, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 5, 5, 7, 7, 5, 9, (BlockState)Blocks.SMOOTH_STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE), (BlockState)Blocks.SMOOTH_STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE), false);
         this.placeBlock(levelAccessor, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH), 6, 5, 6, boundingBox);
         return true;
      }
   }

   public static class LeftTurn extends StrongholdPieces.Turn {
      public LeftTurn(int var1, Random random, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.STRONGHOLD_LEFT_TURN, var1);
         this.setOrientation(orientation);
         this.entryDoor = this.randomSmallDoor(random);
         this.boundingBox = boundingBox;
      }

      public LeftTurn(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.STRONGHOLD_LEFT_TURN, compoundTag);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         Direction var4 = this.getOrientation();
         if(var4 != Direction.NORTH && var4 != Direction.EAST) {
            this.generateSmallDoorChildRight((StrongholdPieces.StartPiece)structurePiece, list, random, 1, 1);
         } else {
            this.generateSmallDoorChildLeft((StrongholdPieces.StartPiece)structurePiece, list, random, 1, 1);
         }

      }

      public static StrongholdPieces.LeftTurn createPiece(List list, Random random, int var2, int var3, int var4, Direction direction, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, 5, direction);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(list, var7) == null?new StrongholdPieces.LeftTurn(var6, random, var7, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 4, 4, 4, true, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(levelAccessor, random, boundingBox, this.entryDoor, 1, 1, 0);
         Direction var5 = this.getOrientation();
         if(var5 != Direction.NORTH && var5 != Direction.EAST) {
            this.generateBox(levelAccessor, boundingBox, 4, 1, 1, 4, 3, 3, CAVE_AIR, CAVE_AIR, false);
         } else {
            this.generateBox(levelAccessor, boundingBox, 0, 1, 1, 0, 3, 3, CAVE_AIR, CAVE_AIR, false);
         }

         return true;
      }
   }

   public static class Library extends StrongholdPieces.StrongholdPiece {
      private final boolean isTall;

      public Library(int var1, Random random, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.STRONGHOLD_LIBRARY, var1);
         this.setOrientation(orientation);
         this.entryDoor = this.randomSmallDoor(random);
         this.boundingBox = boundingBox;
         this.isTall = boundingBox.getYSpan() > 6;
      }

      public Library(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.STRONGHOLD_LIBRARY, compoundTag);
         this.isTall = compoundTag.getBoolean("Tall");
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         super.addAdditionalSaveData(compoundTag);
         compoundTag.putBoolean("Tall", this.isTall);
      }

      public static StrongholdPieces.Library createPiece(List list, Random random, int var2, int var3, int var4, Direction direction, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -4, -1, 0, 14, 11, 15, direction);
         if(!isOkBox(var7) || StructurePiece.findCollisionPiece(list, var7) != null) {
            var7 = BoundingBox.orientBox(var2, var3, var4, -4, -1, 0, 14, 6, 15, direction);
            if(!isOkBox(var7) || StructurePiece.findCollisionPiece(list, var7) != null) {
               return null;
            }
         }

         return new StrongholdPieces.Library(var6, random, var7, direction);
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         int var5 = 11;
         if(!this.isTall) {
            var5 = 6;
         }

         this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 13, var5 - 1, 14, true, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(levelAccessor, random, boundingBox, this.entryDoor, 4, 1, 0);
         this.generateMaybeBox(levelAccessor, boundingBox, random, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.COBWEB.defaultBlockState(), Blocks.COBWEB.defaultBlockState(), false, false);
         int var6 = 1;
         int var7 = 12;

         for(int var8 = 1; var8 <= 13; ++var8) {
            if((var8 - 1) % 4 == 0) {
               this.generateBox(levelAccessor, boundingBox, 1, 1, var8, 1, 4, var8, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
               this.generateBox(levelAccessor, boundingBox, 12, 1, var8, 12, 4, var8, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
               this.placeBlock(levelAccessor, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST), 2, 3, var8, boundingBox);
               this.placeBlock(levelAccessor, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST), 11, 3, var8, boundingBox);
               if(this.isTall) {
                  this.generateBox(levelAccessor, boundingBox, 1, 6, var8, 1, 9, var8, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                  this.generateBox(levelAccessor, boundingBox, 12, 6, var8, 12, 9, var8, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
               }
            } else {
               this.generateBox(levelAccessor, boundingBox, 1, 1, var8, 1, 4, var8, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
               this.generateBox(levelAccessor, boundingBox, 12, 1, var8, 12, 4, var8, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
               if(this.isTall) {
                  this.generateBox(levelAccessor, boundingBox, 1, 6, var8, 1, 9, var8, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
                  this.generateBox(levelAccessor, boundingBox, 12, 6, var8, 12, 9, var8, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
               }
            }
         }

         for(int var8 = 3; var8 < 12; var8 += 2) {
            this.generateBox(levelAccessor, boundingBox, 3, 1, var8, 4, 3, var8, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
            this.generateBox(levelAccessor, boundingBox, 6, 1, var8, 7, 3, var8, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
            this.generateBox(levelAccessor, boundingBox, 9, 1, var8, 10, 3, var8, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
         }

         if(this.isTall) {
            this.generateBox(levelAccessor, boundingBox, 1, 5, 1, 3, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
            this.generateBox(levelAccessor, boundingBox, 10, 5, 1, 12, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
            this.generateBox(levelAccessor, boundingBox, 4, 5, 1, 9, 5, 2, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
            this.generateBox(levelAccessor, boundingBox, 4, 5, 12, 9, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
            this.placeBlock(levelAccessor, Blocks.OAK_PLANKS.defaultBlockState(), 9, 5, 11, boundingBox);
            this.placeBlock(levelAccessor, Blocks.OAK_PLANKS.defaultBlockState(), 8, 5, 11, boundingBox);
            this.placeBlock(levelAccessor, Blocks.OAK_PLANKS.defaultBlockState(), 9, 5, 10, boundingBox);
            BlockState var8 = (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true))).setValue(FenceBlock.EAST, Boolean.valueOf(true));
            BlockState var9 = (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true))).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
            this.generateBox(levelAccessor, boundingBox, 3, 6, 3, 3, 6, 11, var9, var9, false);
            this.generateBox(levelAccessor, boundingBox, 10, 6, 3, 10, 6, 9, var9, var9, false);
            this.generateBox(levelAccessor, boundingBox, 4, 6, 2, 9, 6, 2, var8, var8, false);
            this.generateBox(levelAccessor, boundingBox, 4, 6, 12, 7, 6, 12, var8, var8, false);
            this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true))).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 3, 6, 2, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, Boolean.valueOf(true))).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 3, 6, 12, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true))).setValue(FenceBlock.WEST, Boolean.valueOf(true)), 10, 6, 2, boundingBox);

            for(int var10 = 0; var10 <= 2; ++var10) {
               this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, Boolean.valueOf(true))).setValue(FenceBlock.WEST, Boolean.valueOf(true)), 8 + var10, 6, 12 - var10, boundingBox);
               if(var10 != 2) {
                  this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true))).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 8 + var10, 6, 11 - var10, boundingBox);
               }
            }

            BlockState var10 = (BlockState)Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH);
            this.placeBlock(levelAccessor, var10, 10, 1, 13, boundingBox);
            this.placeBlock(levelAccessor, var10, 10, 2, 13, boundingBox);
            this.placeBlock(levelAccessor, var10, 10, 3, 13, boundingBox);
            this.placeBlock(levelAccessor, var10, 10, 4, 13, boundingBox);
            this.placeBlock(levelAccessor, var10, 10, 5, 13, boundingBox);
            this.placeBlock(levelAccessor, var10, 10, 6, 13, boundingBox);
            this.placeBlock(levelAccessor, var10, 10, 7, 13, boundingBox);
            int var11 = 7;
            int var12 = 7;
            BlockState var13 = (BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true));
            this.placeBlock(levelAccessor, var13, 6, 9, 7, boundingBox);
            BlockState var14 = (BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true));
            this.placeBlock(levelAccessor, var14, 7, 9, 7, boundingBox);
            this.placeBlock(levelAccessor, var13, 6, 8, 7, boundingBox);
            this.placeBlock(levelAccessor, var14, 7, 8, 7, boundingBox);
            BlockState var15 = (BlockState)((BlockState)var9.setValue(FenceBlock.WEST, Boolean.valueOf(true))).setValue(FenceBlock.EAST, Boolean.valueOf(true));
            this.placeBlock(levelAccessor, var15, 6, 7, 7, boundingBox);
            this.placeBlock(levelAccessor, var15, 7, 7, 7, boundingBox);
            this.placeBlock(levelAccessor, var13, 5, 7, 7, boundingBox);
            this.placeBlock(levelAccessor, var14, 8, 7, 7, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)var13.setValue(FenceBlock.NORTH, Boolean.valueOf(true)), 6, 7, 6, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)var13.setValue(FenceBlock.SOUTH, Boolean.valueOf(true)), 6, 7, 8, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)var14.setValue(FenceBlock.NORTH, Boolean.valueOf(true)), 7, 7, 6, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)var14.setValue(FenceBlock.SOUTH, Boolean.valueOf(true)), 7, 7, 8, boundingBox);
            BlockState var16 = Blocks.TORCH.defaultBlockState();
            this.placeBlock(levelAccessor, var16, 5, 8, 7, boundingBox);
            this.placeBlock(levelAccessor, var16, 8, 8, 7, boundingBox);
            this.placeBlock(levelAccessor, var16, 6, 8, 6, boundingBox);
            this.placeBlock(levelAccessor, var16, 6, 8, 8, boundingBox);
            this.placeBlock(levelAccessor, var16, 7, 8, 6, boundingBox);
            this.placeBlock(levelAccessor, var16, 7, 8, 8, boundingBox);
         }

         this.createChest(levelAccessor, boundingBox, random, 3, 3, 5, BuiltInLootTables.STRONGHOLD_LIBRARY);
         if(this.isTall) {
            this.placeBlock(levelAccessor, CAVE_AIR, 12, 9, 1, boundingBox);
            this.createChest(levelAccessor, boundingBox, random, 12, 8, 1, BuiltInLootTables.STRONGHOLD_LIBRARY);
         }

         return true;
      }
   }

   static class PieceWeight {
      public final Class pieceClass;
      public final int weight;
      public int placeCount;
      public final int maxPlaceCount;

      public PieceWeight(Class pieceClass, int weight, int maxPlaceCount) {
         this.pieceClass = pieceClass;
         this.weight = weight;
         this.maxPlaceCount = maxPlaceCount;
      }

      public boolean doPlace(int i) {
         return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
      }

      public boolean isValid() {
         return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
      }
   }

   public static class PortalRoom extends StrongholdPieces.StrongholdPiece {
      private boolean hasPlacedSpawner;

      public PortalRoom(int var1, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, var1);
         this.setOrientation(orientation);
         this.boundingBox = boundingBox;
      }

      public PortalRoom(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, compoundTag);
         this.hasPlacedSpawner = compoundTag.getBoolean("Mob");
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         super.addAdditionalSaveData(compoundTag);
         compoundTag.putBoolean("Mob", this.hasPlacedSpawner);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         if(structurePiece != null) {
            ((StrongholdPieces.StartPiece)structurePiece).portalRoomPiece = this;
         }

      }

      public static StrongholdPieces.PortalRoom createPiece(List list, int var1, int var2, int var3, Direction direction, int var5) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -4, -1, 0, 11, 8, 16, direction);
         return isOkBox(var6) && StructurePiece.findCollisionPiece(list, var6) == null?new StrongholdPieces.PortalRoom(var5, var6, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 10, 7, 15, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(levelAccessor, random, boundingBox, StrongholdPieces.StrongholdPiece.SmallDoorType.GRATES, 4, 1, 0);
         int var5 = 6;
         this.generateBox(levelAccessor, boundingBox, 1, var5, 1, 1, var5, 14, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(levelAccessor, boundingBox, 9, var5, 1, 9, var5, 14, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(levelAccessor, boundingBox, 2, var5, 1, 8, var5, 2, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(levelAccessor, boundingBox, 2, var5, 14, 8, var5, 14, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(levelAccessor, boundingBox, 1, 1, 1, 2, 1, 4, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(levelAccessor, boundingBox, 8, 1, 1, 9, 1, 4, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(levelAccessor, boundingBox, 1, 1, 1, 1, 1, 3, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 9, 1, 1, 9, 1, 3, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 3, 1, 8, 7, 1, 12, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(levelAccessor, boundingBox, 4, 1, 9, 6, 1, 11, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
         BlockState var6 = (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, Boolean.valueOf(true))).setValue(IronBarsBlock.SOUTH, Boolean.valueOf(true));
         BlockState var7 = (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, Boolean.valueOf(true))).setValue(IronBarsBlock.EAST, Boolean.valueOf(true));

         for(int var8 = 3; var8 < 14; var8 += 2) {
            this.generateBox(levelAccessor, boundingBox, 0, 3, var8, 0, 4, var8, var6, var6, false);
            this.generateBox(levelAccessor, boundingBox, 10, 3, var8, 10, 4, var8, var6, var6, false);
         }

         for(int var8 = 2; var8 < 9; var8 += 2) {
            this.generateBox(levelAccessor, boundingBox, var8, 3, 15, var8, 4, 15, var7, var7, false);
         }

         BlockState var8 = (BlockState)Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
         this.generateBox(levelAccessor, boundingBox, 4, 1, 5, 6, 1, 7, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(levelAccessor, boundingBox, 4, 2, 6, 6, 2, 7, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(levelAccessor, boundingBox, 4, 3, 7, 6, 3, 7, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);

         for(int var9 = 4; var9 <= 6; ++var9) {
            this.placeBlock(levelAccessor, var8, var9, 1, 4, boundingBox);
            this.placeBlock(levelAccessor, var8, var9, 2, 5, boundingBox);
            this.placeBlock(levelAccessor, var8, var9, 3, 6, boundingBox);
         }

         BlockState var9 = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.NORTH);
         BlockState var10 = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.SOUTH);
         BlockState var11 = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.EAST);
         BlockState var12 = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.WEST);
         boolean var13 = true;
         boolean[] vars14 = new boolean[12];

         for(int var15 = 0; var15 < vars14.length; ++var15) {
            vars14[var15] = random.nextFloat() > 0.9F;
            var13 &= vars14[var15];
         }

         this.placeBlock(levelAccessor, (BlockState)var9.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(vars14[0])), 4, 3, 8, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)var9.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(vars14[1])), 5, 3, 8, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)var9.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(vars14[2])), 6, 3, 8, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)var10.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(vars14[3])), 4, 3, 12, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)var10.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(vars14[4])), 5, 3, 12, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)var10.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(vars14[5])), 6, 3, 12, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)var11.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(vars14[6])), 3, 3, 9, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)var11.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(vars14[7])), 3, 3, 10, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)var11.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(vars14[8])), 3, 3, 11, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)var12.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(vars14[9])), 7, 3, 9, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)var12.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(vars14[10])), 7, 3, 10, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)var12.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(vars14[11])), 7, 3, 11, boundingBox);
         if(var13) {
            BlockState var15 = Blocks.END_PORTAL.defaultBlockState();
            this.placeBlock(levelAccessor, var15, 4, 3, 9, boundingBox);
            this.placeBlock(levelAccessor, var15, 5, 3, 9, boundingBox);
            this.placeBlock(levelAccessor, var15, 6, 3, 9, boundingBox);
            this.placeBlock(levelAccessor, var15, 4, 3, 10, boundingBox);
            this.placeBlock(levelAccessor, var15, 5, 3, 10, boundingBox);
            this.placeBlock(levelAccessor, var15, 6, 3, 10, boundingBox);
            this.placeBlock(levelAccessor, var15, 4, 3, 11, boundingBox);
            this.placeBlock(levelAccessor, var15, 5, 3, 11, boundingBox);
            this.placeBlock(levelAccessor, var15, 6, 3, 11, boundingBox);
         }

         if(!this.hasPlacedSpawner) {
            var5 = this.getWorldY(3);
            BlockPos var15 = new BlockPos(this.getWorldX(5, 6), var5, this.getWorldZ(5, 6));
            if(boundingBox.isInside(var15)) {
               this.hasPlacedSpawner = true;
               levelAccessor.setBlock(var15, Blocks.SPAWNER.defaultBlockState(), 2);
               BlockEntity var16 = levelAccessor.getBlockEntity(var15);
               if(var16 instanceof SpawnerBlockEntity) {
                  ((SpawnerBlockEntity)var16).getSpawner().setEntityId(EntityType.SILVERFISH);
               }
            }
         }

         return true;
      }
   }

   public static class PrisonHall extends StrongholdPieces.StrongholdPiece {
      public PrisonHall(int var1, Random random, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.STRONGHOLD_PRISON_HALL, var1);
         this.setOrientation(orientation);
         this.entryDoor = this.randomSmallDoor(random);
         this.boundingBox = boundingBox;
      }

      public PrisonHall(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.STRONGHOLD_PRISON_HALL, compoundTag);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)structurePiece, list, random, 1, 1);
      }

      public static StrongholdPieces.PrisonHall createPiece(List list, Random random, int var2, int var3, int var4, Direction direction, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 9, 5, 11, direction);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(list, var7) == null?new StrongholdPieces.PrisonHall(var6, random, var7, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 8, 4, 10, true, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(levelAccessor, random, boundingBox, this.entryDoor, 1, 1, 0);
         this.generateBox(levelAccessor, boundingBox, 1, 1, 10, 3, 3, 10, CAVE_AIR, CAVE_AIR, false);
         this.generateBox(levelAccessor, boundingBox, 4, 1, 1, 4, 3, 1, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(levelAccessor, boundingBox, 4, 1, 3, 4, 3, 3, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(levelAccessor, boundingBox, 4, 1, 7, 4, 3, 7, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(levelAccessor, boundingBox, 4, 1, 9, 4, 3, 9, false, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);

         for(int var5 = 1; var5 <= 3; ++var5) {
            this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, Boolean.valueOf(true))).setValue(IronBarsBlock.SOUTH, Boolean.valueOf(true)), 4, var5, 4, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)((BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, Boolean.valueOf(true))).setValue(IronBarsBlock.SOUTH, Boolean.valueOf(true))).setValue(IronBarsBlock.EAST, Boolean.valueOf(true)), 4, var5, 5, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, Boolean.valueOf(true))).setValue(IronBarsBlock.SOUTH, Boolean.valueOf(true)), 4, var5, 6, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, Boolean.valueOf(true))).setValue(IronBarsBlock.EAST, Boolean.valueOf(true)), 5, var5, 5, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, Boolean.valueOf(true))).setValue(IronBarsBlock.EAST, Boolean.valueOf(true)), 6, var5, 5, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, Boolean.valueOf(true))).setValue(IronBarsBlock.EAST, Boolean.valueOf(true)), 7, var5, 5, boundingBox);
         }

         this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, Boolean.valueOf(true))).setValue(IronBarsBlock.SOUTH, Boolean.valueOf(true)), 4, 3, 2, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, Boolean.valueOf(true))).setValue(IronBarsBlock.SOUTH, Boolean.valueOf(true)), 4, 3, 8, boundingBox);
         BlockState var5 = (BlockState)Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.WEST);
         BlockState var6 = (BlockState)((BlockState)Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.WEST)).setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);
         this.placeBlock(levelAccessor, var5, 4, 1, 2, boundingBox);
         this.placeBlock(levelAccessor, var6, 4, 2, 2, boundingBox);
         this.placeBlock(levelAccessor, var5, 4, 1, 8, boundingBox);
         this.placeBlock(levelAccessor, var6, 4, 2, 8, boundingBox);
         return true;
      }
   }

   public static class RightTurn extends StrongholdPieces.Turn {
      public RightTurn(int var1, Random random, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.STRONGHOLD_RIGHT_TURN, var1);
         this.setOrientation(orientation);
         this.entryDoor = this.randomSmallDoor(random);
         this.boundingBox = boundingBox;
      }

      public RightTurn(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.STRONGHOLD_RIGHT_TURN, compoundTag);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         Direction var4 = this.getOrientation();
         if(var4 != Direction.NORTH && var4 != Direction.EAST) {
            this.generateSmallDoorChildLeft((StrongholdPieces.StartPiece)structurePiece, list, random, 1, 1);
         } else {
            this.generateSmallDoorChildRight((StrongholdPieces.StartPiece)structurePiece, list, random, 1, 1);
         }

      }

      public static StrongholdPieces.RightTurn createPiece(List list, Random random, int var2, int var3, int var4, Direction direction, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, 5, direction);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(list, var7) == null?new StrongholdPieces.RightTurn(var6, random, var7, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 4, 4, 4, true, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(levelAccessor, random, boundingBox, this.entryDoor, 1, 1, 0);
         Direction var5 = this.getOrientation();
         if(var5 != Direction.NORTH && var5 != Direction.EAST) {
            this.generateBox(levelAccessor, boundingBox, 0, 1, 1, 0, 3, 3, CAVE_AIR, CAVE_AIR, false);
         } else {
            this.generateBox(levelAccessor, boundingBox, 4, 1, 1, 4, 3, 3, CAVE_AIR, CAVE_AIR, false);
         }

         return true;
      }
   }

   public static class RoomCrossing extends StrongholdPieces.StrongholdPiece {
      protected final int type;

      public RoomCrossing(int var1, Random random, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.STRONGHOLD_ROOM_CROSSING, var1);
         this.setOrientation(orientation);
         this.entryDoor = this.randomSmallDoor(random);
         this.boundingBox = boundingBox;
         this.type = random.nextInt(5);
      }

      public RoomCrossing(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.STRONGHOLD_ROOM_CROSSING, compoundTag);
         this.type = compoundTag.getInt("Type");
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         super.addAdditionalSaveData(compoundTag);
         compoundTag.putInt("Type", this.type);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)structurePiece, list, random, 4, 1);
         this.generateSmallDoorChildLeft((StrongholdPieces.StartPiece)structurePiece, list, random, 1, 4);
         this.generateSmallDoorChildRight((StrongholdPieces.StartPiece)structurePiece, list, random, 1, 4);
      }

      public static StrongholdPieces.RoomCrossing createPiece(List list, Random random, int var2, int var3, int var4, Direction direction, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -4, -1, 0, 11, 7, 11, direction);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(list, var7) == null?new StrongholdPieces.RoomCrossing(var6, random, var7, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 10, 6, 10, true, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(levelAccessor, random, boundingBox, this.entryDoor, 4, 1, 0);
         this.generateBox(levelAccessor, boundingBox, 4, 1, 10, 6, 3, 10, CAVE_AIR, CAVE_AIR, false);
         this.generateBox(levelAccessor, boundingBox, 0, 1, 4, 0, 3, 6, CAVE_AIR, CAVE_AIR, false);
         this.generateBox(levelAccessor, boundingBox, 10, 1, 4, 10, 3, 6, CAVE_AIR, CAVE_AIR, false);
         switch(this.type) {
         case 0:
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 5, 1, 5, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 5, 2, 5, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 5, 3, 5, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST), 4, 3, 5, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST), 6, 3, 5, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH), 5, 3, 4, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.NORTH), 5, 3, 6, boundingBox);
            this.placeBlock(levelAccessor, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 4, boundingBox);
            this.placeBlock(levelAccessor, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 5, boundingBox);
            this.placeBlock(levelAccessor, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 6, boundingBox);
            this.placeBlock(levelAccessor, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 4, boundingBox);
            this.placeBlock(levelAccessor, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 5, boundingBox);
            this.placeBlock(levelAccessor, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 6, boundingBox);
            this.placeBlock(levelAccessor, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 5, 1, 4, boundingBox);
            this.placeBlock(levelAccessor, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 5, 1, 6, boundingBox);
            break;
         case 1:
            for(int var5 = 0; var5 < 5; ++var5) {
               this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 3, 1, 3 + var5, boundingBox);
               this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 7, 1, 3 + var5, boundingBox);
               this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 3 + var5, 1, 3, boundingBox);
               this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 3 + var5, 1, 7, boundingBox);
            }

            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 5, 1, 5, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 5, 2, 5, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 5, 3, 5, boundingBox);
            this.placeBlock(levelAccessor, Blocks.WATER.defaultBlockState(), 5, 4, 5, boundingBox);
            break;
         case 2:
            for(int var5 = 1; var5 <= 9; ++var5) {
               this.placeBlock(levelAccessor, Blocks.COBBLESTONE.defaultBlockState(), 1, 3, var5, boundingBox);
               this.placeBlock(levelAccessor, Blocks.COBBLESTONE.defaultBlockState(), 9, 3, var5, boundingBox);
            }

            for(int var5 = 1; var5 <= 9; ++var5) {
               this.placeBlock(levelAccessor, Blocks.COBBLESTONE.defaultBlockState(), var5, 3, 1, boundingBox);
               this.placeBlock(levelAccessor, Blocks.COBBLESTONE.defaultBlockState(), var5, 3, 9, boundingBox);
            }

            this.placeBlock(levelAccessor, Blocks.COBBLESTONE.defaultBlockState(), 5, 1, 4, boundingBox);
            this.placeBlock(levelAccessor, Blocks.COBBLESTONE.defaultBlockState(), 5, 1, 6, boundingBox);
            this.placeBlock(levelAccessor, Blocks.COBBLESTONE.defaultBlockState(), 5, 3, 4, boundingBox);
            this.placeBlock(levelAccessor, Blocks.COBBLESTONE.defaultBlockState(), 5, 3, 6, boundingBox);
            this.placeBlock(levelAccessor, Blocks.COBBLESTONE.defaultBlockState(), 4, 1, 5, boundingBox);
            this.placeBlock(levelAccessor, Blocks.COBBLESTONE.defaultBlockState(), 6, 1, 5, boundingBox);
            this.placeBlock(levelAccessor, Blocks.COBBLESTONE.defaultBlockState(), 4, 3, 5, boundingBox);
            this.placeBlock(levelAccessor, Blocks.COBBLESTONE.defaultBlockState(), 6, 3, 5, boundingBox);

            for(int var5 = 1; var5 <= 3; ++var5) {
               this.placeBlock(levelAccessor, Blocks.COBBLESTONE.defaultBlockState(), 4, var5, 4, boundingBox);
               this.placeBlock(levelAccessor, Blocks.COBBLESTONE.defaultBlockState(), 6, var5, 4, boundingBox);
               this.placeBlock(levelAccessor, Blocks.COBBLESTONE.defaultBlockState(), 4, var5, 6, boundingBox);
               this.placeBlock(levelAccessor, Blocks.COBBLESTONE.defaultBlockState(), 6, var5, 6, boundingBox);
            }

            this.placeBlock(levelAccessor, Blocks.TORCH.defaultBlockState(), 5, 3, 5, boundingBox);

            for(int var5 = 2; var5 <= 8; ++var5) {
               this.placeBlock(levelAccessor, Blocks.OAK_PLANKS.defaultBlockState(), 2, 3, var5, boundingBox);
               this.placeBlock(levelAccessor, Blocks.OAK_PLANKS.defaultBlockState(), 3, 3, var5, boundingBox);
               if(var5 <= 3 || var5 >= 7) {
                  this.placeBlock(levelAccessor, Blocks.OAK_PLANKS.defaultBlockState(), 4, 3, var5, boundingBox);
                  this.placeBlock(levelAccessor, Blocks.OAK_PLANKS.defaultBlockState(), 5, 3, var5, boundingBox);
                  this.placeBlock(levelAccessor, Blocks.OAK_PLANKS.defaultBlockState(), 6, 3, var5, boundingBox);
               }

               this.placeBlock(levelAccessor, Blocks.OAK_PLANKS.defaultBlockState(), 7, 3, var5, boundingBox);
               this.placeBlock(levelAccessor, Blocks.OAK_PLANKS.defaultBlockState(), 8, 3, var5, boundingBox);
            }

            BlockState var5 = (BlockState)Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.WEST);
            this.placeBlock(levelAccessor, var5, 9, 1, 3, boundingBox);
            this.placeBlock(levelAccessor, var5, 9, 2, 3, boundingBox);
            this.placeBlock(levelAccessor, var5, 9, 3, 3, boundingBox);
            this.createChest(levelAccessor, boundingBox, random, 3, 4, 8, BuiltInLootTables.STRONGHOLD_CROSSING);
         }

         return true;
      }
   }

   static class SmoothStoneSelector extends StructurePiece.BlockSelector {
      private SmoothStoneSelector() {
      }

      public void next(Random random, int var2, int var3, int var4, boolean var5) {
         if(var5) {
            float var6 = random.nextFloat();
            if(var6 < 0.2F) {
               this.next = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
            } else if(var6 < 0.5F) {
               this.next = Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
            } else if(var6 < 0.55F) {
               this.next = Blocks.INFESTED_STONE_BRICKS.defaultBlockState();
            } else {
               this.next = Blocks.STONE_BRICKS.defaultBlockState();
            }
         } else {
            this.next = Blocks.CAVE_AIR.defaultBlockState();
         }

      }
   }

   public static class StairsDown extends StrongholdPieces.StrongholdPiece {
      private final boolean isSource;

      public StairsDown(StructurePieceType structurePieceType, int var2, Random random, int var4, int var5) {
         super(structurePieceType, var2);
         this.isSource = true;
         this.setOrientation(Direction.Plane.HORIZONTAL.getRandomDirection(random));
         this.entryDoor = StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING;
         if(this.getOrientation().getAxis() == Direction.Axis.Z) {
            this.boundingBox = new BoundingBox(var4, 64, var5, var4 + 5 - 1, 74, var5 + 5 - 1);
         } else {
            this.boundingBox = new BoundingBox(var4, 64, var5, var4 + 5 - 1, 74, var5 + 5 - 1);
         }

      }

      public StairsDown(int var1, Random random, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.STRONGHOLD_STAIRS_DOWN, var1);
         this.isSource = false;
         this.setOrientation(orientation);
         this.entryDoor = this.randomSmallDoor(random);
         this.boundingBox = boundingBox;
      }

      public StairsDown(StructurePieceType structurePieceType, CompoundTag compoundTag) {
         super(structurePieceType, compoundTag);
         this.isSource = compoundTag.getBoolean("Source");
      }

      public StairsDown(StructureManager structureManager, CompoundTag compoundTag) {
         this(StructurePieceType.STRONGHOLD_STAIRS_DOWN, compoundTag);
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         super.addAdditionalSaveData(compoundTag);
         compoundTag.putBoolean("Source", this.isSource);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         if(this.isSource) {
            StrongholdPieces.imposedPiece = StrongholdPieces.FiveCrossing.class;
         }

         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)structurePiece, list, random, 1, 1);
      }

      public static StrongholdPieces.StairsDown createPiece(List list, Random random, int var2, int var3, int var4, Direction direction, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -7, 0, 5, 11, 5, direction);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(list, var7) == null?new StrongholdPieces.StairsDown(var6, random, var7, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 4, 10, 4, true, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(levelAccessor, random, boundingBox, this.entryDoor, 1, 7, 0);
         this.generateSmallDoor(levelAccessor, random, boundingBox, StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING, 1, 1, 4);
         this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 2, 6, 1, boundingBox);
         this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5, 1, boundingBox);
         this.placeBlock(levelAccessor, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 6, 1, boundingBox);
         this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5, 2, boundingBox);
         this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 1, 4, 3, boundingBox);
         this.placeBlock(levelAccessor, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 5, 3, boundingBox);
         this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 2, 4, 3, boundingBox);
         this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 3, 3, 3, boundingBox);
         this.placeBlock(levelAccessor, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 3, 4, 3, boundingBox);
         this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 3, 3, 2, boundingBox);
         this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 3, 2, 1, boundingBox);
         this.placeBlock(levelAccessor, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 3, 3, 1, boundingBox);
         this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 2, 2, 1, boundingBox);
         this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 1, 1, 1, boundingBox);
         this.placeBlock(levelAccessor, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 2, 1, boundingBox);
         this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 1, 1, 2, boundingBox);
         this.placeBlock(levelAccessor, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 1, 3, boundingBox);
         return true;
      }
   }

   public static class StartPiece extends StrongholdPieces.StairsDown {
      public StrongholdPieces.PieceWeight previousPiece;
      @Nullable
      public StrongholdPieces.PortalRoom portalRoomPiece;
      public final List pendingChildren = Lists.newArrayList();

      public StartPiece(Random random, int var2, int var3) {
         super(StructurePieceType.STRONGHOLD_START, 0, random, var2, var3);
      }

      public StartPiece(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.STRONGHOLD_START, compoundTag);
      }
   }

   public static class Straight extends StrongholdPieces.StrongholdPiece {
      private final boolean leftChild;
      private final boolean rightChild;

      public Straight(int var1, Random random, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.STRONGHOLD_STRAIGHT, var1);
         this.setOrientation(orientation);
         this.entryDoor = this.randomSmallDoor(random);
         this.boundingBox = boundingBox;
         this.leftChild = random.nextInt(2) == 0;
         this.rightChild = random.nextInt(2) == 0;
      }

      public Straight(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.STRONGHOLD_STRAIGHT, compoundTag);
         this.leftChild = compoundTag.getBoolean("Left");
         this.rightChild = compoundTag.getBoolean("Right");
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         super.addAdditionalSaveData(compoundTag);
         compoundTag.putBoolean("Left", this.leftChild);
         compoundTag.putBoolean("Right", this.rightChild);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)structurePiece, list, random, 1, 1);
         if(this.leftChild) {
            this.generateSmallDoorChildLeft((StrongholdPieces.StartPiece)structurePiece, list, random, 1, 2);
         }

         if(this.rightChild) {
            this.generateSmallDoorChildRight((StrongholdPieces.StartPiece)structurePiece, list, random, 1, 2);
         }

      }

      public static StrongholdPieces.Straight createPiece(List list, Random random, int var2, int var3, int var4, Direction direction, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, 7, direction);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(list, var7) == null?new StrongholdPieces.Straight(var6, random, var7, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 4, 4, 6, true, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(levelAccessor, random, boundingBox, this.entryDoor, 1, 1, 0);
         this.generateSmallDoor(levelAccessor, random, boundingBox, StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING, 1, 1, 6);
         BlockState var5 = (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST);
         BlockState var6 = (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST);
         this.maybeGenerateBlock(levelAccessor, boundingBox, random, 0.1F, 1, 2, 1, var5);
         this.maybeGenerateBlock(levelAccessor, boundingBox, random, 0.1F, 3, 2, 1, var6);
         this.maybeGenerateBlock(levelAccessor, boundingBox, random, 0.1F, 1, 2, 5, var5);
         this.maybeGenerateBlock(levelAccessor, boundingBox, random, 0.1F, 3, 2, 5, var6);
         if(this.leftChild) {
            this.generateBox(levelAccessor, boundingBox, 0, 1, 2, 0, 3, 4, CAVE_AIR, CAVE_AIR, false);
         }

         if(this.rightChild) {
            this.generateBox(levelAccessor, boundingBox, 4, 1, 2, 4, 3, 4, CAVE_AIR, CAVE_AIR, false);
         }

         return true;
      }
   }

   public static class StraightStairsDown extends StrongholdPieces.StrongholdPiece {
      public StraightStairsDown(int var1, Random random, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.STRONGHOLD_STRAIGHT_STAIRS_DOWN, var1);
         this.setOrientation(orientation);
         this.entryDoor = this.randomSmallDoor(random);
         this.boundingBox = boundingBox;
      }

      public StraightStairsDown(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.STRONGHOLD_STRAIGHT_STAIRS_DOWN, compoundTag);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)structurePiece, list, random, 1, 1);
      }

      public static StrongholdPieces.StraightStairsDown createPiece(List list, Random random, int var2, int var3, int var4, Direction direction, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -7, 0, 5, 11, 8, direction);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(list, var7) == null?new StrongholdPieces.StraightStairsDown(var6, random, var7, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 4, 10, 7, true, random, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(levelAccessor, random, boundingBox, this.entryDoor, 1, 7, 0);
         this.generateSmallDoor(levelAccessor, random, boundingBox, StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING, 1, 1, 7);
         BlockState var5 = (BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);

         for(int var6 = 0; var6 < 6; ++var6) {
            this.placeBlock(levelAccessor, var5, 1, 6 - var6, 1 + var6, boundingBox);
            this.placeBlock(levelAccessor, var5, 2, 6 - var6, 1 + var6, boundingBox);
            this.placeBlock(levelAccessor, var5, 3, 6 - var6, 1 + var6, boundingBox);
            if(var6 < 5) {
               this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5 - var6, 1 + var6, boundingBox);
               this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 2, 5 - var6, 1 + var6, boundingBox);
               this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), 3, 5 - var6, 1 + var6, boundingBox);
            }
         }

         return true;
      }
   }

   abstract static class StrongholdPiece extends StructurePiece {
      protected StrongholdPieces.StrongholdPiece.SmallDoorType entryDoor = StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING;

      protected StrongholdPiece(StructurePieceType structurePieceType, int var2) {
         super(structurePieceType, var2);
      }

      public StrongholdPiece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
         super(structurePieceType, compoundTag);
         this.entryDoor = StrongholdPieces.StrongholdPiece.SmallDoorType.valueOf(compoundTag.getString("EntryDoor"));
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         compoundTag.putString("EntryDoor", this.entryDoor.name());
      }

      protected void generateSmallDoor(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, StrongholdPieces.StrongholdPiece.SmallDoorType strongholdPieces$StrongholdPiece$SmallDoorType, int var5, int var6, int var7) {
         switch(strongholdPieces$StrongholdPiece$SmallDoorType) {
         case OPENING:
            this.generateBox(levelAccessor, boundingBox, var5, var6, var7, var5 + 3 - 1, var6 + 3 - 1, var7, CAVE_AIR, CAVE_AIR, false);
            break;
         case WOOD_DOOR:
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), var5, var6, var7, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), var5, var6 + 1, var7, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), var5, var6 + 2, var7, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), var5 + 1, var6 + 2, var7, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), var5 + 2, var6 + 2, var7, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), var5 + 2, var6 + 1, var7, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), var5 + 2, var6, var7, boundingBox);
            this.placeBlock(levelAccessor, Blocks.OAK_DOOR.defaultBlockState(), var5 + 1, var6, var7, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)Blocks.OAK_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), var5 + 1, var6 + 1, var7, boundingBox);
            break;
         case GRATES:
            this.placeBlock(levelAccessor, Blocks.CAVE_AIR.defaultBlockState(), var5 + 1, var6, var7, boundingBox);
            this.placeBlock(levelAccessor, Blocks.CAVE_AIR.defaultBlockState(), var5 + 1, var6 + 1, var7, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, Boolean.valueOf(true)), var5, var6, var7, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, Boolean.valueOf(true)), var5, var6 + 1, var7, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, Boolean.valueOf(true))).setValue(IronBarsBlock.WEST, Boolean.valueOf(true)), var5, var6 + 2, var7, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, Boolean.valueOf(true))).setValue(IronBarsBlock.WEST, Boolean.valueOf(true)), var5 + 1, var6 + 2, var7, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, Boolean.valueOf(true))).setValue(IronBarsBlock.WEST, Boolean.valueOf(true)), var5 + 2, var6 + 2, var7, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, Boolean.valueOf(true)), var5 + 2, var6 + 1, var7, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, Boolean.valueOf(true)), var5 + 2, var6, var7, boundingBox);
            break;
         case IRON_DOOR:
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), var5, var6, var7, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), var5, var6 + 1, var7, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), var5, var6 + 2, var7, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), var5 + 1, var6 + 2, var7, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), var5 + 2, var6 + 2, var7, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), var5 + 2, var6 + 1, var7, boundingBox);
            this.placeBlock(levelAccessor, Blocks.STONE_BRICKS.defaultBlockState(), var5 + 2, var6, var7, boundingBox);
            this.placeBlock(levelAccessor, Blocks.IRON_DOOR.defaultBlockState(), var5 + 1, var6, var7, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), var5 + 1, var6 + 1, var7, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)Blocks.STONE_BUTTON.defaultBlockState().setValue(ButtonBlock.FACING, Direction.NORTH), var5 + 2, var6 + 1, var7 + 1, boundingBox);
            this.placeBlock(levelAccessor, (BlockState)Blocks.STONE_BUTTON.defaultBlockState().setValue(ButtonBlock.FACING, Direction.SOUTH), var5 + 2, var6 + 1, var7 - 1, boundingBox);
         }

      }

      protected StrongholdPieces.StrongholdPiece.SmallDoorType randomSmallDoor(Random random) {
         int var2 = random.nextInt(5);
         switch(var2) {
         case 0:
         case 1:
         default:
            return StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING;
         case 2:
            return StrongholdPieces.StrongholdPiece.SmallDoorType.WOOD_DOOR;
         case 3:
            return StrongholdPieces.StrongholdPiece.SmallDoorType.GRATES;
         case 4:
            return StrongholdPieces.StrongholdPiece.SmallDoorType.IRON_DOOR;
         }
      }

      @Nullable
      protected StructurePiece generateSmallDoorChildForward(StrongholdPieces.StartPiece strongholdPieces$StartPiece, List list, Random random, int var4, int var5) {
         Direction var6 = this.getOrientation();
         if(var6 != null) {
            switch(var6) {
            case NORTH:
               return StrongholdPieces.generateAndAddPiece(strongholdPieces$StartPiece, list, random, this.boundingBox.x0 + var4, this.boundingBox.y0 + var5, this.boundingBox.z0 - 1, var6, this.getGenDepth());
            case SOUTH:
               return StrongholdPieces.generateAndAddPiece(strongholdPieces$StartPiece, list, random, this.boundingBox.x0 + var4, this.boundingBox.y0 + var5, this.boundingBox.z1 + 1, var6, this.getGenDepth());
            case WEST:
               return StrongholdPieces.generateAndAddPiece(strongholdPieces$StartPiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + var5, this.boundingBox.z0 + var4, var6, this.getGenDepth());
            case EAST:
               return StrongholdPieces.generateAndAddPiece(strongholdPieces$StartPiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + var5, this.boundingBox.z0 + var4, var6, this.getGenDepth());
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece generateSmallDoorChildLeft(StrongholdPieces.StartPiece strongholdPieces$StartPiece, List list, Random random, int var4, int var5) {
         Direction var6 = this.getOrientation();
         if(var6 != null) {
            switch(var6) {
            case NORTH:
               return StrongholdPieces.generateAndAddPiece(strongholdPieces$StartPiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + var4, this.boundingBox.z0 + var5, Direction.WEST, this.getGenDepth());
            case SOUTH:
               return StrongholdPieces.generateAndAddPiece(strongholdPieces$StartPiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + var4, this.boundingBox.z0 + var5, Direction.WEST, this.getGenDepth());
            case WEST:
               return StrongholdPieces.generateAndAddPiece(strongholdPieces$StartPiece, list, random, this.boundingBox.x0 + var5, this.boundingBox.y0 + var4, this.boundingBox.z0 - 1, Direction.NORTH, this.getGenDepth());
            case EAST:
               return StrongholdPieces.generateAndAddPiece(strongholdPieces$StartPiece, list, random, this.boundingBox.x0 + var5, this.boundingBox.y0 + var4, this.boundingBox.z0 - 1, Direction.NORTH, this.getGenDepth());
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece generateSmallDoorChildRight(StrongholdPieces.StartPiece strongholdPieces$StartPiece, List list, Random random, int var4, int var5) {
         Direction var6 = this.getOrientation();
         if(var6 != null) {
            switch(var6) {
            case NORTH:
               return StrongholdPieces.generateAndAddPiece(strongholdPieces$StartPiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + var4, this.boundingBox.z0 + var5, Direction.EAST, this.getGenDepth());
            case SOUTH:
               return StrongholdPieces.generateAndAddPiece(strongholdPieces$StartPiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + var4, this.boundingBox.z0 + var5, Direction.EAST, this.getGenDepth());
            case WEST:
               return StrongholdPieces.generateAndAddPiece(strongholdPieces$StartPiece, list, random, this.boundingBox.x0 + var5, this.boundingBox.y0 + var4, this.boundingBox.z1 + 1, Direction.SOUTH, this.getGenDepth());
            case EAST:
               return StrongholdPieces.generateAndAddPiece(strongholdPieces$StartPiece, list, random, this.boundingBox.x0 + var5, this.boundingBox.y0 + var4, this.boundingBox.z1 + 1, Direction.SOUTH, this.getGenDepth());
            }
         }

         return null;
      }

      protected static boolean isOkBox(BoundingBox boundingBox) {
         return boundingBox != null && boundingBox.y0 > 10;
      }

      public static enum SmallDoorType {
         OPENING,
         WOOD_DOOR,
         GRATES,
         IRON_DOOR;
      }
   }

   public abstract static class Turn extends StrongholdPieces.StrongholdPiece {
      protected Turn(StructurePieceType structurePieceType, int var2) {
         super(structurePieceType, var2);
      }

      public Turn(StructurePieceType structurePieceType, CompoundTag compoundTag) {
         super(structurePieceType, compoundTag);
      }
   }
}
