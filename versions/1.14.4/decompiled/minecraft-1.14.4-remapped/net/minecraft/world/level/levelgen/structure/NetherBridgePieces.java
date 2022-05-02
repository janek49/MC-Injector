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
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class NetherBridgePieces {
   private static final NetherBridgePieces.PieceWeight[] BRIDGE_PIECE_WEIGHTS = new NetherBridgePieces.PieceWeight[]{new NetherBridgePieces.PieceWeight(NetherBridgePieces.BridgeStraight.class, 30, 0, true), new NetherBridgePieces.PieceWeight(NetherBridgePieces.BridgeCrossing.class, 10, 4), new NetherBridgePieces.PieceWeight(NetherBridgePieces.RoomCrossing.class, 10, 4), new NetherBridgePieces.PieceWeight(NetherBridgePieces.StairsRoom.class, 10, 3), new NetherBridgePieces.PieceWeight(NetherBridgePieces.MonsterThrone.class, 5, 2), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleEntrance.class, 5, 1)};
   private static final NetherBridgePieces.PieceWeight[] CASTLE_PIECE_WEIGHTS = new NetherBridgePieces.PieceWeight[]{new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleSmallCorridorPiece.class, 25, 0, true), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleSmallCorridorCrossingPiece.class, 15, 5), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleSmallCorridorRightTurnPiece.class, 5, 10), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleSmallCorridorLeftTurnPiece.class, 5, 10), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleCorridorStairsPiece.class, 10, 3, true), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleCorridorTBalconyPiece.class, 7, 2), new NetherBridgePieces.PieceWeight(NetherBridgePieces.CastleStalkRoom.class, 5, 2)};

   private static NetherBridgePieces.NetherBridgePiece findAndCreateBridgePieceFactory(NetherBridgePieces.PieceWeight netherBridgePieces$PieceWeight, List list, Random random, int var3, int var4, int var5, Direction direction, int var7) {
      Class<? extends NetherBridgePieces.NetherBridgePiece> var8 = netherBridgePieces$PieceWeight.pieceClass;
      NetherBridgePieces.NetherBridgePiece var9 = null;
      if(var8 == NetherBridgePieces.BridgeStraight.class) {
         var9 = NetherBridgePieces.BridgeStraight.createPiece(list, random, var3, var4, var5, direction, var7);
      } else if(var8 == NetherBridgePieces.BridgeCrossing.class) {
         var9 = NetherBridgePieces.BridgeCrossing.createPiece(list, var3, var4, var5, direction, var7);
      } else if(var8 == NetherBridgePieces.RoomCrossing.class) {
         var9 = NetherBridgePieces.RoomCrossing.createPiece(list, var3, var4, var5, direction, var7);
      } else if(var8 == NetherBridgePieces.StairsRoom.class) {
         var9 = NetherBridgePieces.StairsRoom.createPiece(list, var3, var4, var5, var7, direction);
      } else if(var8 == NetherBridgePieces.MonsterThrone.class) {
         var9 = NetherBridgePieces.MonsterThrone.createPiece(list, var3, var4, var5, var7, direction);
      } else if(var8 == NetherBridgePieces.CastleEntrance.class) {
         var9 = NetherBridgePieces.CastleEntrance.createPiece(list, random, var3, var4, var5, direction, var7);
      } else if(var8 == NetherBridgePieces.CastleSmallCorridorPiece.class) {
         var9 = NetherBridgePieces.CastleSmallCorridorPiece.createPiece(list, var3, var4, var5, direction, var7);
      } else if(var8 == NetherBridgePieces.CastleSmallCorridorRightTurnPiece.class) {
         var9 = NetherBridgePieces.CastleSmallCorridorRightTurnPiece.createPiece(list, random, var3, var4, var5, direction, var7);
      } else if(var8 == NetherBridgePieces.CastleSmallCorridorLeftTurnPiece.class) {
         var9 = NetherBridgePieces.CastleSmallCorridorLeftTurnPiece.createPiece(list, random, var3, var4, var5, direction, var7);
      } else if(var8 == NetherBridgePieces.CastleCorridorStairsPiece.class) {
         var9 = NetherBridgePieces.CastleCorridorStairsPiece.createPiece(list, var3, var4, var5, direction, var7);
      } else if(var8 == NetherBridgePieces.CastleCorridorTBalconyPiece.class) {
         var9 = NetherBridgePieces.CastleCorridorTBalconyPiece.createPiece(list, var3, var4, var5, direction, var7);
      } else if(var8 == NetherBridgePieces.CastleSmallCorridorCrossingPiece.class) {
         var9 = NetherBridgePieces.CastleSmallCorridorCrossingPiece.createPiece(list, var3, var4, var5, direction, var7);
      } else if(var8 == NetherBridgePieces.CastleStalkRoom.class) {
         var9 = NetherBridgePieces.CastleStalkRoom.createPiece(list, var3, var4, var5, direction, var7);
      }

      return var9;
   }

   public static class BridgeCrossing extends NetherBridgePieces.NetherBridgePiece {
      public BridgeCrossing(int var1, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, var1);
         this.setOrientation(orientation);
         this.boundingBox = boundingBox;
      }

      protected BridgeCrossing(Random random, int var2, int var3) {
         super(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, 0);
         this.setOrientation(Direction.Plane.HORIZONTAL.getRandomDirection(random));
         if(this.getOrientation().getAxis() == Direction.Axis.Z) {
            this.boundingBox = new BoundingBox(var2, 64, var3, var2 + 19 - 1, 73, var3 + 19 - 1);
         } else {
            this.boundingBox = new BoundingBox(var2, 64, var3, var2 + 19 - 1, 73, var3 + 19 - 1);
         }

      }

      protected BridgeCrossing(StructurePieceType structurePieceType, CompoundTag compoundTag) {
         super(structurePieceType, compoundTag);
      }

      public BridgeCrossing(StructureManager structureManager, CompoundTag compoundTag) {
         this(StructurePieceType.NETHER_FORTRESS_BRIDGE_CROSSING, compoundTag);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         this.generateChildForward((NetherBridgePieces.StartPiece)structurePiece, list, random, 8, 3, false);
         this.generateChildLeft((NetherBridgePieces.StartPiece)structurePiece, list, random, 3, 8, false);
         this.generateChildRight((NetherBridgePieces.StartPiece)structurePiece, list, random, 3, 8, false);
      }

      public static NetherBridgePieces.BridgeCrossing createPiece(List list, int var1, int var2, int var3, Direction direction, int var5) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -8, -3, 0, 19, 10, 19, direction);
         return isOkBox(var6) && StructurePiece.findCollisionPiece(list, var6) == null?new NetherBridgePieces.BridgeCrossing(var5, var6, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 7, 3, 0, 11, 4, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 3, 7, 18, 4, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 8, 5, 0, 10, 7, 18, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 5, 8, 18, 7, 10, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 7, 5, 0, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 7, 5, 11, 7, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 11, 5, 0, 11, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 11, 5, 11, 11, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 5, 7, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 11, 5, 7, 18, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 5, 11, 7, 5, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 11, 5, 11, 18, 5, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 7, 2, 0, 11, 2, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 7, 2, 13, 11, 2, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 7, 0, 0, 11, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 7, 0, 15, 11, 1, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int var5 = 7; var5 <= 11; ++var5) {
            for(int var6 = 0; var6 <= 2; ++var6) {
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var5, -1, var6, boundingBox);
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var5, -1, 18 - var6, boundingBox);
            }
         }

         this.generateBox(levelAccessor, boundingBox, 0, 2, 7, 5, 2, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 13, 2, 7, 18, 2, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 0, 7, 3, 1, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 15, 0, 7, 18, 1, 11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int var5 = 0; var5 <= 2; ++var5) {
            for(int var6 = 7; var6 <= 11; ++var6) {
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var5, -1, var6, boundingBox);
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), 18 - var5, -1, var6, boundingBox);
            }
         }

         return true;
      }
   }

   public static class BridgeEndFiller extends NetherBridgePieces.NetherBridgePiece {
      private final int selfSeed;

      public BridgeEndFiller(int var1, Random random, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END_FILLER, var1);
         this.setOrientation(orientation);
         this.boundingBox = boundingBox;
         this.selfSeed = random.nextInt();
      }

      public BridgeEndFiller(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.NETHER_FORTRESS_BRIDGE_END_FILLER, compoundTag);
         this.selfSeed = compoundTag.getInt("Seed");
      }

      public static NetherBridgePieces.BridgeEndFiller createPiece(List list, Random random, int var2, int var3, int var4, Direction direction, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -3, 0, 5, 10, 8, direction);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(list, var7) == null?new NetherBridgePieces.BridgeEndFiller(var6, random, var7, direction):null;
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         super.addAdditionalSaveData(compoundTag);
         compoundTag.putInt("Seed", this.selfSeed);
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         Random random = new Random((long)this.selfSeed);

         for(int var6 = 0; var6 <= 4; ++var6) {
            for(int var7 = 3; var7 <= 4; ++var7) {
               int var8 = random.nextInt(8);
               this.generateBox(levelAccessor, boundingBox, var6, var7, 0, var6, var7, var8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            }
         }

         int var6 = random.nextInt(8);
         this.generateBox(levelAccessor, boundingBox, 0, 5, 0, 0, 5, var6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         var6 = random.nextInt(8);
         this.generateBox(levelAccessor, boundingBox, 4, 5, 0, 4, 5, var6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(var6 = 0; var6 <= 4; ++var6) {
            int var7 = random.nextInt(5);
            this.generateBox(levelAccessor, boundingBox, var6, 2, 0, var6, 2, var7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         }

         for(var6 = 0; var6 <= 4; ++var6) {
            for(int var7 = 0; var7 <= 1; ++var7) {
               int var8 = random.nextInt(3);
               this.generateBox(levelAccessor, boundingBox, var6, var7, 0, var6, var7, var8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            }
         }

         return true;
      }
   }

   public static class BridgeStraight extends NetherBridgePieces.NetherBridgePiece {
      public BridgeStraight(int var1, Random random, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STRAIGHT, var1);
         this.setOrientation(orientation);
         this.boundingBox = boundingBox;
      }

      public BridgeStraight(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.NETHER_FORTRESS_BRIDGE_STRAIGHT, compoundTag);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         this.generateChildForward((NetherBridgePieces.StartPiece)structurePiece, list, random, 1, 3, false);
      }

      public static NetherBridgePieces.BridgeStraight createPiece(List list, Random random, int var2, int var3, int var4, Direction direction, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -3, 0, 5, 10, 19, direction);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(list, var7) == null?new NetherBridgePieces.BridgeStraight(var6, random, var7, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 3, 0, 4, 4, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 5, 0, 3, 7, 18, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 5, 0, 0, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 4, 5, 0, 4, 5, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 4, 2, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 13, 4, 2, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 4, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 0, 15, 4, 1, 18, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int var5 = 0; var5 <= 4; ++var5) {
            for(int var6 = 0; var6 <= 2; ++var6) {
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var5, -1, var6, boundingBox);
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var5, -1, 18 - var6, boundingBox);
            }
         }

         BlockState var5 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true))).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         BlockState var6 = (BlockState)var5.setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState var7 = (BlockState)var5.setValue(FenceBlock.WEST, Boolean.valueOf(true));
         this.generateBox(levelAccessor, boundingBox, 0, 1, 1, 0, 4, 1, var6, var6, false);
         this.generateBox(levelAccessor, boundingBox, 0, 3, 4, 0, 4, 4, var6, var6, false);
         this.generateBox(levelAccessor, boundingBox, 0, 3, 14, 0, 4, 14, var6, var6, false);
         this.generateBox(levelAccessor, boundingBox, 0, 1, 17, 0, 4, 17, var6, var6, false);
         this.generateBox(levelAccessor, boundingBox, 4, 1, 1, 4, 4, 1, var7, var7, false);
         this.generateBox(levelAccessor, boundingBox, 4, 3, 4, 4, 4, 4, var7, var7, false);
         this.generateBox(levelAccessor, boundingBox, 4, 3, 14, 4, 4, 14, var7, var7, false);
         this.generateBox(levelAccessor, boundingBox, 4, 1, 17, 4, 4, 17, var7, var7, false);
         return true;
      }
   }

   public static class CastleCorridorStairsPiece extends NetherBridgePieces.NetherBridgePiece {
      public CastleCorridorStairsPiece(int var1, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_STAIRS, var1);
         this.setOrientation(orientation);
         this.boundingBox = boundingBox;
      }

      public CastleCorridorStairsPiece(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_STAIRS, compoundTag);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         this.generateChildForward((NetherBridgePieces.StartPiece)structurePiece, list, random, 1, 0, true);
      }

      public static NetherBridgePieces.CastleCorridorStairsPiece createPiece(List list, int var1, int var2, int var3, Direction direction, int var5) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -1, -7, 0, 5, 14, 10, direction);
         return isOkBox(var6) && StructurePiece.findCollisionPiece(list, var6) == null?new NetherBridgePieces.CastleCorridorStairsPiece(var5, var6, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         BlockState var5 = (BlockState)Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
         BlockState var6 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true))).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));

         for(int var7 = 0; var7 <= 9; ++var7) {
            int var8 = Math.max(1, 7 - var7);
            int var9 = Math.min(Math.max(var8 + 5, 14 - var7), 13);
            int var10 = var7;
            this.generateBox(levelAccessor, boundingBox, 0, 0, var7, 4, var8, var7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(levelAccessor, boundingBox, 1, var8 + 1, var7, 3, var9 - 1, var7, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            if(var7 <= 6) {
               this.placeBlock(levelAccessor, var5, 1, var8 + 1, var7, boundingBox);
               this.placeBlock(levelAccessor, var5, 2, var8 + 1, var7, boundingBox);
               this.placeBlock(levelAccessor, var5, 3, var8 + 1, var7, boundingBox);
            }

            this.generateBox(levelAccessor, boundingBox, 0, var9, var7, 4, var9, var7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(levelAccessor, boundingBox, 0, var8 + 1, var7, 0, var9 - 1, var7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            this.generateBox(levelAccessor, boundingBox, 4, var8 + 1, var7, 4, var9 - 1, var7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            if((var7 & 1) == 0) {
               this.generateBox(levelAccessor, boundingBox, 0, var8 + 2, var7, 0, var8 + 3, var7, var6, var6, false);
               this.generateBox(levelAccessor, boundingBox, 4, var8 + 2, var7, 4, var8 + 3, var7, var6, var6, false);
            }

            for(int var11 = 0; var11 <= 4; ++var11) {
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var11, -1, var10, boundingBox);
            }
         }

         return true;
      }
   }

   public static class CastleCorridorTBalconyPiece extends NetherBridgePieces.NetherBridgePiece {
      public CastleCorridorTBalconyPiece(int var1, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY, var1);
         this.setOrientation(orientation);
         this.boundingBox = boundingBox;
      }

      public CastleCorridorTBalconyPiece(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_CORRIDOR_T_BALCONY, compoundTag);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         int var4 = 1;
         Direction var5 = this.getOrientation();
         if(var5 == Direction.WEST || var5 == Direction.NORTH) {
            var4 = 5;
         }

         this.generateChildLeft((NetherBridgePieces.StartPiece)structurePiece, list, random, 0, var4, random.nextInt(8) > 0);
         this.generateChildRight((NetherBridgePieces.StartPiece)structurePiece, list, random, 0, var4, random.nextInt(8) > 0);
      }

      public static NetherBridgePieces.CastleCorridorTBalconyPiece createPiece(List list, int var1, int var2, int var3, Direction direction, int var5) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -3, 0, 0, 9, 7, 9, direction);
         return isOkBox(var6) && StructurePiece.findCollisionPiece(list, var6) == null?new NetherBridgePieces.CastleCorridorTBalconyPiece(var5, var6, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         BlockState var5 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true))).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         BlockState var6 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true))).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 8, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 8, 5, 8, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 6, 0, 8, 6, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 2, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 6, 2, 0, 8, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 3, 0, 1, 4, 0, var6, var6, false);
         this.generateBox(levelAccessor, boundingBox, 7, 3, 0, 7, 4, 0, var6, var6, false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 4, 8, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 1, 4, 2, 2, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 6, 1, 4, 7, 2, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 3, 8, 7, 3, 8, var6, var6, false);
         this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true))).setValue(FenceBlock.SOUTH, Boolean.valueOf(true)), 0, 3, 8, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true))).setValue(FenceBlock.SOUTH, Boolean.valueOf(true)), 8, 3, 8, boundingBox);
         this.generateBox(levelAccessor, boundingBox, 0, 3, 6, 0, 3, 7, var5, var5, false);
         this.generateBox(levelAccessor, boundingBox, 8, 3, 6, 8, 3, 7, var5, var5, false);
         this.generateBox(levelAccessor, boundingBox, 0, 3, 4, 0, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 8, 3, 4, 8, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 3, 5, 2, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 6, 3, 5, 7, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 4, 5, 1, 5, 5, var6, var6, false);
         this.generateBox(levelAccessor, boundingBox, 7, 4, 5, 7, 5, 5, var6, var6, false);

         for(int var7 = 0; var7 <= 5; ++var7) {
            for(int var8 = 0; var8 <= 8; ++var8) {
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var8, -1, var7, boundingBox);
            }
         }

         return true;
      }
   }

   public static class CastleEntrance extends NetherBridgePieces.NetherBridgePiece {
      public CastleEntrance(int var1, Random random, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_ENTRANCE, var1);
         this.setOrientation(orientation);
         this.boundingBox = boundingBox;
      }

      public CastleEntrance(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_ENTRANCE, compoundTag);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         this.generateChildForward((NetherBridgePieces.StartPiece)structurePiece, list, random, 5, 3, true);
      }

      public static NetherBridgePieces.CastleEntrance createPiece(List list, Random random, int var2, int var3, int var4, Direction direction, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -5, -3, 0, 13, 14, 13, direction);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(list, var7) == null?new NetherBridgePieces.CastleEntrance(var6, random, var7, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 5, 0, 12, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 5, 8, 0, 7, 8, 0, Blocks.NETHER_BRICK_FENCE.defaultBlockState(), Blocks.NETHER_BRICK_FENCE.defaultBlockState(), false);
         BlockState var5 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true))).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState var6 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true))).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));

         for(int var7 = 1; var7 <= 11; var7 += 2) {
            this.generateBox(levelAccessor, boundingBox, var7, 10, 0, var7, 11, 0, var5, var5, false);
            this.generateBox(levelAccessor, boundingBox, var7, 10, 12, var7, 11, 12, var5, var5, false);
            this.generateBox(levelAccessor, boundingBox, 0, 10, var7, 0, 11, var7, var6, var6, false);
            this.generateBox(levelAccessor, boundingBox, 12, 10, var7, 12, 11, var7, var6, var6, false);
            this.placeBlock(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var7, 13, 0, boundingBox);
            this.placeBlock(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var7, 13, 12, boundingBox);
            this.placeBlock(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), 0, 13, var7, boundingBox);
            this.placeBlock(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), 12, 13, var7, boundingBox);
            if(var7 != 11) {
               this.placeBlock(levelAccessor, var5, var7 + 1, 13, 0, boundingBox);
               this.placeBlock(levelAccessor, var5, var7 + 1, 13, 12, boundingBox);
               this.placeBlock(levelAccessor, var6, 0, 13, var7 + 1, boundingBox);
               this.placeBlock(levelAccessor, var6, 12, 13, var7 + 1, boundingBox);
            }
         }

         this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true))).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 0, 13, 0, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, Boolean.valueOf(true))).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 0, 13, 12, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, Boolean.valueOf(true))).setValue(FenceBlock.WEST, Boolean.valueOf(true)), 12, 13, 12, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true))).setValue(FenceBlock.WEST, Boolean.valueOf(true)), 12, 13, 0, boundingBox);

         for(int var7 = 3; var7 <= 9; var7 += 2) {
            this.generateBox(levelAccessor, boundingBox, 1, 7, var7, 1, 8, var7, (BlockState)var6.setValue(FenceBlock.WEST, Boolean.valueOf(true)), (BlockState)var6.setValue(FenceBlock.WEST, Boolean.valueOf(true)), false);
            this.generateBox(levelAccessor, boundingBox, 11, 7, var7, 11, 8, var7, (BlockState)var6.setValue(FenceBlock.EAST, Boolean.valueOf(true)), (BlockState)var6.setValue(FenceBlock.EAST, Boolean.valueOf(true)), false);
         }

         this.generateBox(levelAccessor, boundingBox, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int var7 = 4; var7 <= 8; ++var7) {
            for(int var8 = 0; var8 <= 2; ++var8) {
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var7, -1, var8, boundingBox);
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var7, -1, 12 - var8, boundingBox);
            }
         }

         for(int var7 = 0; var7 <= 2; ++var7) {
            for(int var8 = 4; var8 <= 8; ++var8) {
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var7, -1, var8, boundingBox);
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), 12 - var7, -1, var8, boundingBox);
            }
         }

         this.generateBox(levelAccessor, boundingBox, 5, 5, 5, 7, 5, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 6, 1, 6, 6, 4, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.placeBlock(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), 6, 0, 6, boundingBox);
         this.placeBlock(levelAccessor, Blocks.LAVA.defaultBlockState(), 6, 5, 6, boundingBox);
         BlockPos var7 = new BlockPos(this.getWorldX(6, 6), this.getWorldY(5), this.getWorldZ(6, 6));
         if(boundingBox.isInside(var7)) {
            levelAccessor.getLiquidTicks().scheduleTick(var7, Fluids.LAVA, 0);
         }

         return true;
      }
   }

   public static class CastleSmallCorridorCrossingPiece extends NetherBridgePieces.NetherBridgePiece {
      public CastleSmallCorridorCrossingPiece(int var1, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_CROSSING, var1);
         this.setOrientation(orientation);
         this.boundingBox = boundingBox;
      }

      public CastleSmallCorridorCrossingPiece(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_CROSSING, compoundTag);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         this.generateChildForward((NetherBridgePieces.StartPiece)structurePiece, list, random, 1, 0, true);
         this.generateChildLeft((NetherBridgePieces.StartPiece)structurePiece, list, random, 0, 1, true);
         this.generateChildRight((NetherBridgePieces.StartPiece)structurePiece, list, random, 0, 1, true);
      }

      public static NetherBridgePieces.CastleSmallCorridorCrossingPiece createPiece(List list, int var1, int var2, int var3, Direction direction, int var5) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -1, 0, 0, 5, 7, 5, direction);
         return isOkBox(var6) && StructurePiece.findCollisionPiece(list, var6) == null?new NetherBridgePieces.CastleSmallCorridorCrossingPiece(var5, var6, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 4, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 4, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int var5 = 0; var5 <= 4; ++var5) {
            for(int var6 = 0; var6 <= 4; ++var6) {
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var5, -1, var6, boundingBox);
            }
         }

         return true;
      }
   }

   public static class CastleSmallCorridorLeftTurnPiece extends NetherBridgePieces.NetherBridgePiece {
      private boolean isNeedingChest;

      public CastleSmallCorridorLeftTurnPiece(int var1, Random random, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN, var1);
         this.setOrientation(orientation);
         this.boundingBox = boundingBox;
         this.isNeedingChest = random.nextInt(3) == 0;
      }

      public CastleSmallCorridorLeftTurnPiece(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_LEFT_TURN, compoundTag);
         this.isNeedingChest = compoundTag.getBoolean("Chest");
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         super.addAdditionalSaveData(compoundTag);
         compoundTag.putBoolean("Chest", this.isNeedingChest);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         this.generateChildLeft((NetherBridgePieces.StartPiece)structurePiece, list, random, 0, 1, true);
      }

      public static NetherBridgePieces.CastleSmallCorridorLeftTurnPiece createPiece(List list, Random random, int var2, int var3, int var4, Direction direction, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, 0, 0, 5, 7, 5, direction);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(list, var7) == null?new NetherBridgePieces.CastleSmallCorridorLeftTurnPiece(var6, random, var7, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         BlockState var5 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true))).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState var6 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true))).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         this.generateBox(levelAccessor, boundingBox, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 4, 3, 1, 4, 4, 1, var6, var6, false);
         this.generateBox(levelAccessor, boundingBox, 4, 3, 3, 4, 4, 3, var6, var6, false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 0, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 4, 3, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 3, 4, 1, 4, 4, var5, var5, false);
         this.generateBox(levelAccessor, boundingBox, 3, 3, 4, 3, 4, 4, var5, var5, false);
         if(this.isNeedingChest && boundingBox.isInside(new BlockPos(this.getWorldX(3, 3), this.getWorldY(2), this.getWorldZ(3, 3)))) {
            this.isNeedingChest = false;
            this.createChest(levelAccessor, boundingBox, random, 3, 2, 3, BuiltInLootTables.NETHER_BRIDGE);
         }

         this.generateBox(levelAccessor, boundingBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int var7 = 0; var7 <= 4; ++var7) {
            for(int var8 = 0; var8 <= 4; ++var8) {
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var7, -1, var8, boundingBox);
            }
         }

         return true;
      }
   }

   public static class CastleSmallCorridorPiece extends NetherBridgePieces.NetherBridgePiece {
      public CastleSmallCorridorPiece(int var1, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR, var1);
         this.setOrientation(orientation);
         this.boundingBox = boundingBox;
      }

      public CastleSmallCorridorPiece(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR, compoundTag);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         this.generateChildForward((NetherBridgePieces.StartPiece)structurePiece, list, random, 1, 0, true);
      }

      public static NetherBridgePieces.CastleSmallCorridorPiece createPiece(List list, int var1, int var2, int var3, Direction direction, int var5) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -1, 0, 0, 5, 7, 5, direction);
         return isOkBox(var6) && StructurePiece.findCollisionPiece(list, var6) == null?new NetherBridgePieces.CastleSmallCorridorPiece(var5, var6, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         BlockState var5 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true))).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 4, 2, 0, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 3, 1, 0, 4, 1, var5, var5, false);
         this.generateBox(levelAccessor, boundingBox, 0, 3, 3, 0, 4, 3, var5, var5, false);
         this.generateBox(levelAccessor, boundingBox, 4, 3, 1, 4, 4, 1, var5, var5, false);
         this.generateBox(levelAccessor, boundingBox, 4, 3, 3, 4, 4, 3, var5, var5, false);
         this.generateBox(levelAccessor, boundingBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int var6 = 0; var6 <= 4; ++var6) {
            for(int var7 = 0; var7 <= 4; ++var7) {
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var6, -1, var7, boundingBox);
            }
         }

         return true;
      }
   }

   public static class CastleSmallCorridorRightTurnPiece extends NetherBridgePieces.NetherBridgePiece {
      private boolean isNeedingChest;

      public CastleSmallCorridorRightTurnPiece(int var1, Random random, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN, var1);
         this.setOrientation(orientation);
         this.boundingBox = boundingBox;
         this.isNeedingChest = random.nextInt(3) == 0;
      }

      public CastleSmallCorridorRightTurnPiece(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_SMALL_CORRIDOR_RIGHT_TURN, compoundTag);
         this.isNeedingChest = compoundTag.getBoolean("Chest");
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         super.addAdditionalSaveData(compoundTag);
         compoundTag.putBoolean("Chest", this.isNeedingChest);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         this.generateChildRight((NetherBridgePieces.StartPiece)structurePiece, list, random, 0, 1, true);
      }

      public static NetherBridgePieces.CastleSmallCorridorRightTurnPiece createPiece(List list, Random random, int var2, int var3, int var4, Direction direction, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, 0, 0, 5, 7, 5, direction);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(list, var7) == null?new NetherBridgePieces.CastleSmallCorridorRightTurnPiece(var6, random, var7, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 4, 1, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 4, 5, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         BlockState var5 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true))).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState var6 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true))).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 0, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 3, 1, 0, 4, 1, var6, var6, false);
         this.generateBox(levelAccessor, boundingBox, 0, 3, 3, 0, 4, 3, var6, var6, false);
         this.generateBox(levelAccessor, boundingBox, 4, 2, 0, 4, 5, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 2, 4, 4, 5, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 3, 4, 1, 4, 4, var5, var5, false);
         this.generateBox(levelAccessor, boundingBox, 3, 3, 4, 3, 4, 4, var5, var5, false);
         if(this.isNeedingChest && boundingBox.isInside(new BlockPos(this.getWorldX(1, 3), this.getWorldY(2), this.getWorldZ(1, 3)))) {
            this.isNeedingChest = false;
            this.createChest(levelAccessor, boundingBox, random, 1, 2, 3, BuiltInLootTables.NETHER_BRIDGE);
         }

         this.generateBox(levelAccessor, boundingBox, 0, 6, 0, 4, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int var7 = 0; var7 <= 4; ++var7) {
            for(int var8 = 0; var8 <= 4; ++var8) {
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var7, -1, var8, boundingBox);
            }
         }

         return true;
      }
   }

   public static class CastleStalkRoom extends NetherBridgePieces.NetherBridgePiece {
      public CastleStalkRoom(int var1, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_STALK_ROOM, var1);
         this.setOrientation(orientation);
         this.boundingBox = boundingBox;
      }

      public CastleStalkRoom(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.NETHER_FORTRESS_CASTLE_STALK_ROOM, compoundTag);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         this.generateChildForward((NetherBridgePieces.StartPiece)structurePiece, list, random, 5, 3, true);
         this.generateChildForward((NetherBridgePieces.StartPiece)structurePiece, list, random, 5, 11, true);
      }

      public static NetherBridgePieces.CastleStalkRoom createPiece(List list, int var1, int var2, int var3, Direction direction, int var5) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -5, -3, 0, 13, 14, 13, direction);
         return isOkBox(var6) && StructurePiece.findCollisionPiece(list, var6) == null?new NetherBridgePieces.CastleStalkRoom(var5, var6, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 3, 0, 12, 4, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 5, 0, 12, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 5, 0, 1, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 11, 5, 0, 12, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 2, 5, 11, 4, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 8, 5, 11, 10, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 5, 9, 11, 7, 12, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 2, 5, 0, 4, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 8, 5, 0, 10, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 5, 9, 0, 7, 12, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 2, 11, 2, 10, 12, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         BlockState var5 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true))).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState var6 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true))).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         BlockState var7 = (BlockState)var6.setValue(FenceBlock.WEST, Boolean.valueOf(true));
         BlockState var8 = (BlockState)var6.setValue(FenceBlock.EAST, Boolean.valueOf(true));

         for(int var9 = 1; var9 <= 11; var9 += 2) {
            this.generateBox(levelAccessor, boundingBox, var9, 10, 0, var9, 11, 0, var5, var5, false);
            this.generateBox(levelAccessor, boundingBox, var9, 10, 12, var9, 11, 12, var5, var5, false);
            this.generateBox(levelAccessor, boundingBox, 0, 10, var9, 0, 11, var9, var6, var6, false);
            this.generateBox(levelAccessor, boundingBox, 12, 10, var9, 12, 11, var9, var6, var6, false);
            this.placeBlock(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var9, 13, 0, boundingBox);
            this.placeBlock(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var9, 13, 12, boundingBox);
            this.placeBlock(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), 0, 13, var9, boundingBox);
            this.placeBlock(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), 12, 13, var9, boundingBox);
            if(var9 != 11) {
               this.placeBlock(levelAccessor, var5, var9 + 1, 13, 0, boundingBox);
               this.placeBlock(levelAccessor, var5, var9 + 1, 13, 12, boundingBox);
               this.placeBlock(levelAccessor, var6, 0, 13, var9 + 1, boundingBox);
               this.placeBlock(levelAccessor, var6, 12, 13, var9 + 1, boundingBox);
            }
         }

         this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true))).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 0, 13, 0, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, Boolean.valueOf(true))).setValue(FenceBlock.EAST, Boolean.valueOf(true)), 0, 13, 12, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, Boolean.valueOf(true))).setValue(FenceBlock.WEST, Boolean.valueOf(true)), 12, 13, 12, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true))).setValue(FenceBlock.WEST, Boolean.valueOf(true)), 12, 13, 0, boundingBox);

         for(int var9 = 3; var9 <= 9; var9 += 2) {
            this.generateBox(levelAccessor, boundingBox, 1, 7, var9, 1, 8, var9, var7, var7, false);
            this.generateBox(levelAccessor, boundingBox, 11, 7, var9, 11, 8, var9, var8, var8, false);
         }

         BlockState var9 = (BlockState)Blocks.NETHER_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);

         for(int var10 = 0; var10 <= 6; ++var10) {
            int var11 = var10 + 4;

            for(int var12 = 5; var12 <= 7; ++var12) {
               this.placeBlock(levelAccessor, var9, var12, 5 + var10, var11, boundingBox);
            }

            if(var11 >= 5 && var11 <= 8) {
               this.generateBox(levelAccessor, boundingBox, 5, 5, var11, 7, var10 + 4, var11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            } else if(var11 >= 9 && var11 <= 10) {
               this.generateBox(levelAccessor, boundingBox, 5, 8, var11, 7, var10 + 4, var11, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
            }

            if(var10 >= 1) {
               this.generateBox(levelAccessor, boundingBox, 5, 6 + var10, var11, 7, 9 + var10, var11, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
            }
         }

         for(int var10 = 5; var10 <= 7; ++var10) {
            this.placeBlock(levelAccessor, var9, var10, 12, 11, boundingBox);
         }

         this.generateBox(levelAccessor, boundingBox, 5, 6, 7, 5, 7, 7, var8, var8, false);
         this.generateBox(levelAccessor, boundingBox, 7, 6, 7, 7, 7, 7, var7, var7, false);
         this.generateBox(levelAccessor, boundingBox, 5, 13, 12, 7, 13, 12, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 2, 5, 2, 3, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 2, 5, 9, 3, 5, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 2, 5, 4, 2, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 9, 5, 2, 10, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 9, 5, 9, 10, 5, 10, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 10, 5, 4, 10, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         BlockState var10 = (BlockState)var9.setValue(StairBlock.FACING, Direction.EAST);
         BlockState var11 = (BlockState)var9.setValue(StairBlock.FACING, Direction.WEST);
         this.placeBlock(levelAccessor, var11, 4, 5, 2, boundingBox);
         this.placeBlock(levelAccessor, var11, 4, 5, 3, boundingBox);
         this.placeBlock(levelAccessor, var11, 4, 5, 9, boundingBox);
         this.placeBlock(levelAccessor, var11, 4, 5, 10, boundingBox);
         this.placeBlock(levelAccessor, var10, 8, 5, 2, boundingBox);
         this.placeBlock(levelAccessor, var10, 8, 5, 3, boundingBox);
         this.placeBlock(levelAccessor, var10, 8, 5, 9, boundingBox);
         this.placeBlock(levelAccessor, var10, 8, 5, 10, boundingBox);
         this.generateBox(levelAccessor, boundingBox, 3, 4, 4, 4, 4, 8, Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 8, 4, 4, 9, 4, 8, Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 3, 5, 4, 4, 5, 8, Blocks.NETHER_WART.defaultBlockState(), Blocks.NETHER_WART.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 8, 5, 4, 9, 5, 8, Blocks.NETHER_WART.defaultBlockState(), Blocks.NETHER_WART.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 4, 2, 0, 8, 2, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 4, 12, 2, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 4, 0, 0, 8, 1, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 4, 0, 9, 8, 1, 12, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 0, 4, 3, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 9, 0, 4, 12, 1, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);

         for(int var12 = 4; var12 <= 8; ++var12) {
            for(int var13 = 0; var13 <= 2; ++var13) {
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var12, -1, var13, boundingBox);
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var12, -1, 12 - var13, boundingBox);
            }
         }

         for(int var12 = 0; var12 <= 2; ++var12) {
            for(int var13 = 4; var13 <= 8; ++var13) {
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var12, -1, var13, boundingBox);
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), 12 - var12, -1, var13, boundingBox);
            }
         }

         return true;
      }
   }

   public static class MonsterThrone extends NetherBridgePieces.NetherBridgePiece {
      private boolean hasPlacedSpawner;

      public MonsterThrone(int var1, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.NETHER_FORTRESS_MONSTER_THRONE, var1);
         this.setOrientation(orientation);
         this.boundingBox = boundingBox;
      }

      public MonsterThrone(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.NETHER_FORTRESS_MONSTER_THRONE, compoundTag);
         this.hasPlacedSpawner = compoundTag.getBoolean("Mob");
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         super.addAdditionalSaveData(compoundTag);
         compoundTag.putBoolean("Mob", this.hasPlacedSpawner);
      }

      public static NetherBridgePieces.MonsterThrone createPiece(List list, int var1, int var2, int var3, int var4, Direction direction) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -2, 0, 0, 7, 8, 9, direction);
         return isOkBox(var6) && StructurePiece.findCollisionPiece(list, var6) == null?new NetherBridgePieces.MonsterThrone(var4, var6, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 6, 7, 7, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 0, 0, 5, 1, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 2, 1, 5, 2, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 3, 2, 5, 3, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 4, 3, 5, 4, 7, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 2, 0, 1, 4, 2, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 5, 2, 0, 5, 4, 2, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 5, 2, 1, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 5, 5, 2, 5, 5, 3, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 5, 3, 0, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 6, 5, 3, 6, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 5, 8, 5, 5, 8, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         BlockState var5 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true))).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState var6 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true))).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         this.placeBlock(levelAccessor, (BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)), 1, 6, 3, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true)), 5, 6, 3, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true))).setValue(FenceBlock.NORTH, Boolean.valueOf(true)), 0, 6, 3, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true))).setValue(FenceBlock.NORTH, Boolean.valueOf(true)), 6, 6, 3, boundingBox);
         this.generateBox(levelAccessor, boundingBox, 0, 6, 4, 0, 6, 7, var6, var6, false);
         this.generateBox(levelAccessor, boundingBox, 6, 6, 4, 6, 6, 7, var6, var6, false);
         this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true))).setValue(FenceBlock.SOUTH, Boolean.valueOf(true)), 0, 6, 8, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true))).setValue(FenceBlock.SOUTH, Boolean.valueOf(true)), 6, 6, 8, boundingBox);
         this.generateBox(levelAccessor, boundingBox, 1, 6, 8, 5, 6, 8, var5, var5, false);
         this.placeBlock(levelAccessor, (BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true)), 1, 7, 8, boundingBox);
         this.generateBox(levelAccessor, boundingBox, 2, 7, 8, 4, 7, 8, var5, var5, false);
         this.placeBlock(levelAccessor, (BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)), 5, 7, 8, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true)), 2, 8, 8, boundingBox);
         this.placeBlock(levelAccessor, var5, 3, 8, 8, boundingBox);
         this.placeBlock(levelAccessor, (BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true)), 4, 8, 8, boundingBox);
         if(!this.hasPlacedSpawner) {
            BlockPos var7 = new BlockPos(this.getWorldX(3, 5), this.getWorldY(5), this.getWorldZ(3, 5));
            if(boundingBox.isInside(var7)) {
               this.hasPlacedSpawner = true;
               levelAccessor.setBlock(var7, Blocks.SPAWNER.defaultBlockState(), 2);
               BlockEntity var8 = levelAccessor.getBlockEntity(var7);
               if(var8 instanceof SpawnerBlockEntity) {
                  ((SpawnerBlockEntity)var8).getSpawner().setEntityId(EntityType.BLAZE);
               }
            }
         }

         for(int var7 = 0; var7 <= 6; ++var7) {
            for(int var8 = 0; var8 <= 6; ++var8) {
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var7, -1, var8, boundingBox);
            }
         }

         return true;
      }
   }

   abstract static class NetherBridgePiece extends StructurePiece {
      protected NetherBridgePiece(StructurePieceType structurePieceType, int var2) {
         super(structurePieceType, var2);
      }

      public NetherBridgePiece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
         super(structurePieceType, compoundTag);
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
      }

      private int updatePieceWeight(List list) {
         boolean var2 = false;
         int var3 = 0;

         for(NetherBridgePieces.PieceWeight var5 : list) {
            if(var5.maxPlaceCount > 0 && var5.placeCount < var5.maxPlaceCount) {
               var2 = true;
            }

            var3 += var5.weight;
         }

         return var2?var3:-1;
      }

      private NetherBridgePieces.NetherBridgePiece generatePiece(NetherBridgePieces.StartPiece netherBridgePieces$StartPiece, List var2, List var3, Random random, int var5, int var6, int var7, Direction direction, int var9) {
         int var10 = this.updatePieceWeight(var2);
         boolean var11 = var10 > 0 && var9 <= 30;
         int var12 = 0;

         while(var12 < 5 && var11) {
            ++var12;
            int var13 = random.nextInt(var10);

            for(NetherBridgePieces.PieceWeight var15 : var2) {
               var13 -= var15.weight;
               if(var13 < 0) {
                  if(!var15.doPlace(var9) || var15 == netherBridgePieces$StartPiece.previousPiece && !var15.allowInRow) {
                     break;
                  }

                  NetherBridgePieces.NetherBridgePiece var16 = NetherBridgePieces.findAndCreateBridgePieceFactory(var15, var3, random, var5, var6, var7, direction, var9);
                  if(var16 != null) {
                     ++var15.placeCount;
                     netherBridgePieces$StartPiece.previousPiece = var15;
                     if(!var15.isValid()) {
                        var2.remove(var15);
                     }

                     return var16;
                  }
               }
            }
         }

         return NetherBridgePieces.BridgeEndFiller.createPiece(var3, random, var5, var6, var7, direction, var9);
      }

      private StructurePiece generateAndAddPiece(NetherBridgePieces.StartPiece netherBridgePieces$StartPiece, List list, Random random, int var4, int var5, int var6, @Nullable Direction direction, int var8, boolean var9) {
         if(Math.abs(var4 - netherBridgePieces$StartPiece.getBoundingBox().x0) <= 112 && Math.abs(var6 - netherBridgePieces$StartPiece.getBoundingBox().z0) <= 112) {
            List<NetherBridgePieces.PieceWeight> list = netherBridgePieces$StartPiece.availableBridgePieces;
            if(var9) {
               list = netherBridgePieces$StartPiece.availableCastlePieces;
            }

            StructurePiece var11 = this.generatePiece(netherBridgePieces$StartPiece, list, list, random, var4, var5, var6, direction, var8 + 1);
            if(var11 != null) {
               list.add(var11);
               netherBridgePieces$StartPiece.pendingChildren.add(var11);
            }

            return var11;
         } else {
            return NetherBridgePieces.BridgeEndFiller.createPiece(list, random, var4, var5, var6, direction, var8);
         }
      }

      @Nullable
      protected StructurePiece generateChildForward(NetherBridgePieces.StartPiece netherBridgePieces$StartPiece, List list, Random random, int var4, int var5, boolean var6) {
         Direction var7 = this.getOrientation();
         if(var7 != null) {
            switch(var7) {
            case NORTH:
               return this.generateAndAddPiece(netherBridgePieces$StartPiece, list, random, this.boundingBox.x0 + var4, this.boundingBox.y0 + var5, this.boundingBox.z0 - 1, var7, this.getGenDepth(), var6);
            case SOUTH:
               return this.generateAndAddPiece(netherBridgePieces$StartPiece, list, random, this.boundingBox.x0 + var4, this.boundingBox.y0 + var5, this.boundingBox.z1 + 1, var7, this.getGenDepth(), var6);
            case WEST:
               return this.generateAndAddPiece(netherBridgePieces$StartPiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + var5, this.boundingBox.z0 + var4, var7, this.getGenDepth(), var6);
            case EAST:
               return this.generateAndAddPiece(netherBridgePieces$StartPiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + var5, this.boundingBox.z0 + var4, var7, this.getGenDepth(), var6);
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece generateChildLeft(NetherBridgePieces.StartPiece netherBridgePieces$StartPiece, List list, Random random, int var4, int var5, boolean var6) {
         Direction var7 = this.getOrientation();
         if(var7 != null) {
            switch(var7) {
            case NORTH:
               return this.generateAndAddPiece(netherBridgePieces$StartPiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + var4, this.boundingBox.z0 + var5, Direction.WEST, this.getGenDepth(), var6);
            case SOUTH:
               return this.generateAndAddPiece(netherBridgePieces$StartPiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + var4, this.boundingBox.z0 + var5, Direction.WEST, this.getGenDepth(), var6);
            case WEST:
               return this.generateAndAddPiece(netherBridgePieces$StartPiece, list, random, this.boundingBox.x0 + var5, this.boundingBox.y0 + var4, this.boundingBox.z0 - 1, Direction.NORTH, this.getGenDepth(), var6);
            case EAST:
               return this.generateAndAddPiece(netherBridgePieces$StartPiece, list, random, this.boundingBox.x0 + var5, this.boundingBox.y0 + var4, this.boundingBox.z0 - 1, Direction.NORTH, this.getGenDepth(), var6);
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece generateChildRight(NetherBridgePieces.StartPiece netherBridgePieces$StartPiece, List list, Random random, int var4, int var5, boolean var6) {
         Direction var7 = this.getOrientation();
         if(var7 != null) {
            switch(var7) {
            case NORTH:
               return this.generateAndAddPiece(netherBridgePieces$StartPiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + var4, this.boundingBox.z0 + var5, Direction.EAST, this.getGenDepth(), var6);
            case SOUTH:
               return this.generateAndAddPiece(netherBridgePieces$StartPiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + var4, this.boundingBox.z0 + var5, Direction.EAST, this.getGenDepth(), var6);
            case WEST:
               return this.generateAndAddPiece(netherBridgePieces$StartPiece, list, random, this.boundingBox.x0 + var5, this.boundingBox.y0 + var4, this.boundingBox.z1 + 1, Direction.SOUTH, this.getGenDepth(), var6);
            case EAST:
               return this.generateAndAddPiece(netherBridgePieces$StartPiece, list, random, this.boundingBox.x0 + var5, this.boundingBox.y0 + var4, this.boundingBox.z1 + 1, Direction.SOUTH, this.getGenDepth(), var6);
            }
         }

         return null;
      }

      protected static boolean isOkBox(BoundingBox boundingBox) {
         return boundingBox != null && boundingBox.y0 > 10;
      }
   }

   static class PieceWeight {
      public final Class pieceClass;
      public final int weight;
      public int placeCount;
      public final int maxPlaceCount;
      public final boolean allowInRow;

      public PieceWeight(Class pieceClass, int weight, int maxPlaceCount, boolean allowInRow) {
         this.pieceClass = pieceClass;
         this.weight = weight;
         this.maxPlaceCount = maxPlaceCount;
         this.allowInRow = allowInRow;
      }

      public PieceWeight(Class class, int var2, int var3) {
         this(class, var2, var3, false);
      }

      public boolean doPlace(int i) {
         return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
      }

      public boolean isValid() {
         return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
      }
   }

   public static class RoomCrossing extends NetherBridgePieces.NetherBridgePiece {
      public RoomCrossing(int var1, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.NETHER_FORTRESS_ROOM_CROSSING, var1);
         this.setOrientation(orientation);
         this.boundingBox = boundingBox;
      }

      public RoomCrossing(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.NETHER_FORTRESS_ROOM_CROSSING, compoundTag);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         this.generateChildForward((NetherBridgePieces.StartPiece)structurePiece, list, random, 2, 0, false);
         this.generateChildLeft((NetherBridgePieces.StartPiece)structurePiece, list, random, 0, 2, false);
         this.generateChildRight((NetherBridgePieces.StartPiece)structurePiece, list, random, 0, 2, false);
      }

      public static NetherBridgePieces.RoomCrossing createPiece(List list, int var1, int var2, int var3, Direction direction, int var5) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -2, 0, 0, 7, 9, 7, direction);
         return isOkBox(var6) && StructurePiece.findCollisionPiece(list, var6) == null?new NetherBridgePieces.RoomCrossing(var5, var6, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 6, 7, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 1, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 6, 1, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 5, 2, 0, 6, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 5, 2, 6, 6, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 0, 6, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 5, 0, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 6, 2, 0, 6, 6, 1, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 6, 2, 5, 6, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         BlockState var5 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true))).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState var6 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true))).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         this.generateBox(levelAccessor, boundingBox, 2, 6, 0, 4, 6, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 2, 5, 0, 4, 5, 0, var5, var5, false);
         this.generateBox(levelAccessor, boundingBox, 2, 6, 6, 4, 6, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 2, 5, 6, 4, 5, 6, var5, var5, false);
         this.generateBox(levelAccessor, boundingBox, 0, 6, 2, 0, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 5, 2, 0, 5, 4, var6, var6, false);
         this.generateBox(levelAccessor, boundingBox, 6, 6, 2, 6, 6, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 6, 5, 2, 6, 5, 4, var6, var6, false);

         for(int var7 = 0; var7 <= 6; ++var7) {
            for(int var8 = 0; var8 <= 6; ++var8) {
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var7, -1, var8, boundingBox);
            }
         }

         return true;
      }
   }

   public static class StairsRoom extends NetherBridgePieces.NetherBridgePiece {
      public StairsRoom(int var1, BoundingBox boundingBox, Direction orientation) {
         super(StructurePieceType.NETHER_FORTRESS_STAIRS_ROOM, var1);
         this.setOrientation(orientation);
         this.boundingBox = boundingBox;
      }

      public StairsRoom(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.NETHER_FORTRESS_STAIRS_ROOM, compoundTag);
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         this.generateChildRight((NetherBridgePieces.StartPiece)structurePiece, list, random, 6, 2, false);
      }

      public static NetherBridgePieces.StairsRoom createPiece(List list, int var1, int var2, int var3, int var4, Direction direction) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -2, 0, 0, 7, 11, 7, direction);
         return isOkBox(var6) && StructurePiece.findCollisionPiece(list, var6) == null?new NetherBridgePieces.StairsRoom(var4, var6, direction):null;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 6, 1, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 6, 10, 6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 1, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 5, 2, 0, 6, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 1, 0, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 6, 2, 1, 6, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 2, 6, 5, 8, 6, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         BlockState var5 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true))).setValue(FenceBlock.EAST, Boolean.valueOf(true));
         BlockState var6 = (BlockState)((BlockState)Blocks.NETHER_BRICK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true))).setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
         this.generateBox(levelAccessor, boundingBox, 0, 3, 2, 0, 5, 4, var6, var6, false);
         this.generateBox(levelAccessor, boundingBox, 6, 3, 2, 6, 5, 2, var6, var6, false);
         this.generateBox(levelAccessor, boundingBox, 6, 3, 4, 6, 5, 4, var6, var6, false);
         this.placeBlock(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), 5, 2, 5, boundingBox);
         this.generateBox(levelAccessor, boundingBox, 4, 2, 5, 4, 3, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 3, 2, 5, 3, 4, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 2, 2, 5, 2, 5, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 2, 5, 1, 6, 5, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 1, 7, 1, 5, 7, 4, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 6, 8, 2, 6, 8, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 2, 6, 0, 4, 8, 0, Blocks.NETHER_BRICKS.defaultBlockState(), Blocks.NETHER_BRICKS.defaultBlockState(), false);
         this.generateBox(levelAccessor, boundingBox, 2, 5, 0, 4, 5, 0, var5, var5, false);

         for(int var7 = 0; var7 <= 6; ++var7) {
            for(int var8 = 0; var8 <= 6; ++var8) {
               this.fillColumnDown(levelAccessor, Blocks.NETHER_BRICKS.defaultBlockState(), var7, -1, var8, boundingBox);
            }
         }

         return true;
      }
   }

   public static class StartPiece extends NetherBridgePieces.BridgeCrossing {
      public NetherBridgePieces.PieceWeight previousPiece;
      public List availableBridgePieces;
      public List availableCastlePieces;
      public final List pendingChildren = Lists.newArrayList();

      public StartPiece(Random random, int var2, int var3) {
         super(random, var2, var3);
         this.availableBridgePieces = Lists.newArrayList();

         for(NetherBridgePieces.PieceWeight var7 : NetherBridgePieces.BRIDGE_PIECE_WEIGHTS) {
            var7.placeCount = 0;
            this.availableBridgePieces.add(var7);
         }

         this.availableCastlePieces = Lists.newArrayList();

         for(NetherBridgePieces.PieceWeight var7 : NetherBridgePieces.CASTLE_PIECE_WEIGHTS) {
            var7.placeCount = 0;
            this.availableCastlePieces.add(var7);
         }

      }

      public StartPiece(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.NETHER_FORTRESS_START, compoundTag);
      }
   }
}
