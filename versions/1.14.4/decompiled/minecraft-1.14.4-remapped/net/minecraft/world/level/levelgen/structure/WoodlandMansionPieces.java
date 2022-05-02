package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class WoodlandMansionPieces {
   public static void generateMansion(StructureManager structureManager, BlockPos blockPos, Rotation rotation, List list, Random random) {
      WoodlandMansionPieces.MansionGrid var5 = new WoodlandMansionPieces.MansionGrid(random);
      WoodlandMansionPieces.MansionPiecePlacer var6 = new WoodlandMansionPieces.MansionPiecePlacer(structureManager, random);
      var6.createMansion(blockPos, rotation, list, var5);
   }

   static class FirstFloorRoomCollection extends WoodlandMansionPieces.FloorRoomCollection {
      private FirstFloorRoomCollection() {
         super(null);
      }

      public String get1x1(Random random) {
         return "1x1_a" + (random.nextInt(5) + 1);
      }

      public String get1x1Secret(Random random) {
         return "1x1_as" + (random.nextInt(4) + 1);
      }

      public String get1x2SideEntrance(Random random, boolean var2) {
         return "1x2_a" + (random.nextInt(9) + 1);
      }

      public String get1x2FrontEntrance(Random random, boolean var2) {
         return "1x2_b" + (random.nextInt(5) + 1);
      }

      public String get1x2Secret(Random random) {
         return "1x2_s" + (random.nextInt(2) + 1);
      }

      public String get2x2(Random random) {
         return "2x2_a" + (random.nextInt(4) + 1);
      }

      public String get2x2Secret(Random random) {
         return "2x2_s1";
      }
   }

   abstract static class FloorRoomCollection {
      private FloorRoomCollection() {
      }

      public abstract String get1x1(Random var1);

      public abstract String get1x1Secret(Random var1);

      public abstract String get1x2SideEntrance(Random var1, boolean var2);

      public abstract String get1x2FrontEntrance(Random var1, boolean var2);

      public abstract String get1x2Secret(Random var1);

      public abstract String get2x2(Random var1);

      public abstract String get2x2Secret(Random var1);
   }

   static class MansionGrid {
      private final Random random;
      private final WoodlandMansionPieces.SimpleGrid baseGrid;
      private final WoodlandMansionPieces.SimpleGrid thirdFloorGrid;
      private final WoodlandMansionPieces.SimpleGrid[] floorRooms;
      private final int entranceX;
      private final int entranceY;

      public MansionGrid(Random random) {
         this.random = random;
         int var2 = 11;
         this.entranceX = 7;
         this.entranceY = 4;
         this.baseGrid = new WoodlandMansionPieces.SimpleGrid(11, 11, 5);
         this.baseGrid.set(this.entranceX, this.entranceY, this.entranceX + 1, this.entranceY + 1, 3);
         this.baseGrid.set(this.entranceX - 1, this.entranceY, this.entranceX - 1, this.entranceY + 1, 2);
         this.baseGrid.set(this.entranceX + 2, this.entranceY - 2, this.entranceX + 3, this.entranceY + 3, 5);
         this.baseGrid.set(this.entranceX + 1, this.entranceY - 2, this.entranceX + 1, this.entranceY - 1, 1);
         this.baseGrid.set(this.entranceX + 1, this.entranceY + 2, this.entranceX + 1, this.entranceY + 3, 1);
         this.baseGrid.set(this.entranceX - 1, this.entranceY - 1, 1);
         this.baseGrid.set(this.entranceX - 1, this.entranceY + 2, 1);
         this.baseGrid.set(0, 0, 11, 1, 5);
         this.baseGrid.set(0, 9, 11, 11, 5);
         this.recursiveCorridor(this.baseGrid, this.entranceX, this.entranceY - 2, Direction.WEST, 6);
         this.recursiveCorridor(this.baseGrid, this.entranceX, this.entranceY + 3, Direction.WEST, 6);
         this.recursiveCorridor(this.baseGrid, this.entranceX - 2, this.entranceY - 1, Direction.WEST, 3);
         this.recursiveCorridor(this.baseGrid, this.entranceX - 2, this.entranceY + 2, Direction.WEST, 3);

         while(this.cleanEdges(this.baseGrid)) {
            ;
         }

         this.floorRooms = new WoodlandMansionPieces.SimpleGrid[3];
         this.floorRooms[0] = new WoodlandMansionPieces.SimpleGrid(11, 11, 5);
         this.floorRooms[1] = new WoodlandMansionPieces.SimpleGrid(11, 11, 5);
         this.floorRooms[2] = new WoodlandMansionPieces.SimpleGrid(11, 11, 5);
         this.identifyRooms(this.baseGrid, this.floorRooms[0]);
         this.identifyRooms(this.baseGrid, this.floorRooms[1]);
         this.floorRooms[0].set(this.entranceX + 1, this.entranceY, this.entranceX + 1, this.entranceY + 1, 8388608);
         this.floorRooms[1].set(this.entranceX + 1, this.entranceY, this.entranceX + 1, this.entranceY + 1, 8388608);
         this.thirdFloorGrid = new WoodlandMansionPieces.SimpleGrid(this.baseGrid.width, this.baseGrid.height, 5);
         this.setupThirdFloor();
         this.identifyRooms(this.thirdFloorGrid, this.floorRooms[2]);
      }

      public static boolean isHouse(WoodlandMansionPieces.SimpleGrid woodlandMansionPieces$SimpleGrid, int var1, int var2) {
         int var3 = woodlandMansionPieces$SimpleGrid.get(var1, var2);
         return var3 == 1 || var3 == 2 || var3 == 3 || var3 == 4;
      }

      public boolean isRoomId(WoodlandMansionPieces.SimpleGrid woodlandMansionPieces$SimpleGrid, int var2, int var3, int var4, int var5) {
         return (this.floorRooms[var4].get(var2, var3) & '\uffff') == var5;
      }

      @Nullable
      public Direction get1x2RoomDirection(WoodlandMansionPieces.SimpleGrid woodlandMansionPieces$SimpleGrid, int var2, int var3, int var4, int var5) {
         for(Direction var7 : Direction.Plane.HORIZONTAL) {
            if(this.isRoomId(woodlandMansionPieces$SimpleGrid, var2 + var7.getStepX(), var3 + var7.getStepZ(), var4, var5)) {
               return var7;
            }
         }

         return null;
      }

      private void recursiveCorridor(WoodlandMansionPieces.SimpleGrid woodlandMansionPieces$SimpleGrid, int var2, int var3, Direction direction, int var5) {
         if(var5 > 0) {
            woodlandMansionPieces$SimpleGrid.set(var2, var3, 1);
            woodlandMansionPieces$SimpleGrid.setif(var2 + direction.getStepX(), var3 + direction.getStepZ(), 0, 1);

            for(int var6 = 0; var6 < 8; ++var6) {
               Direction var7 = Direction.from2DDataValue(this.random.nextInt(4));
               if(var7 != direction.getOpposite() && (var7 != Direction.EAST || !this.random.nextBoolean())) {
                  int var8 = var2 + direction.getStepX();
                  int var9 = var3 + direction.getStepZ();
                  if(woodlandMansionPieces$SimpleGrid.get(var8 + var7.getStepX(), var9 + var7.getStepZ()) == 0 && woodlandMansionPieces$SimpleGrid.get(var8 + var7.getStepX() * 2, var9 + var7.getStepZ() * 2) == 0) {
                     this.recursiveCorridor(woodlandMansionPieces$SimpleGrid, var2 + direction.getStepX() + var7.getStepX(), var3 + direction.getStepZ() + var7.getStepZ(), var7, var5 - 1);
                     break;
                  }
               }
            }

            Direction direction = direction.getClockWise();
            Direction var7 = direction.getCounterClockWise();
            woodlandMansionPieces$SimpleGrid.setif(var2 + direction.getStepX(), var3 + direction.getStepZ(), 0, 2);
            woodlandMansionPieces$SimpleGrid.setif(var2 + var7.getStepX(), var3 + var7.getStepZ(), 0, 2);
            woodlandMansionPieces$SimpleGrid.setif(var2 + direction.getStepX() + direction.getStepX(), var3 + direction.getStepZ() + direction.getStepZ(), 0, 2);
            woodlandMansionPieces$SimpleGrid.setif(var2 + direction.getStepX() + var7.getStepX(), var3 + direction.getStepZ() + var7.getStepZ(), 0, 2);
            woodlandMansionPieces$SimpleGrid.setif(var2 + direction.getStepX() * 2, var3 + direction.getStepZ() * 2, 0, 2);
            woodlandMansionPieces$SimpleGrid.setif(var2 + direction.getStepX() * 2, var3 + direction.getStepZ() * 2, 0, 2);
            woodlandMansionPieces$SimpleGrid.setif(var2 + var7.getStepX() * 2, var3 + var7.getStepZ() * 2, 0, 2);
         }
      }

      private boolean cleanEdges(WoodlandMansionPieces.SimpleGrid woodlandMansionPieces$SimpleGrid) {
         boolean var2 = false;

         for(int var3 = 0; var3 < woodlandMansionPieces$SimpleGrid.height; ++var3) {
            for(int var4 = 0; var4 < woodlandMansionPieces$SimpleGrid.width; ++var4) {
               if(woodlandMansionPieces$SimpleGrid.get(var4, var3) == 0) {
                  int var5 = 0;
                  var5 = var5 + (isHouse(woodlandMansionPieces$SimpleGrid, var4 + 1, var3)?1:0);
                  var5 = var5 + (isHouse(woodlandMansionPieces$SimpleGrid, var4 - 1, var3)?1:0);
                  var5 = var5 + (isHouse(woodlandMansionPieces$SimpleGrid, var4, var3 + 1)?1:0);
                  var5 = var5 + (isHouse(woodlandMansionPieces$SimpleGrid, var4, var3 - 1)?1:0);
                  if(var5 >= 3) {
                     woodlandMansionPieces$SimpleGrid.set(var4, var3, 2);
                     var2 = true;
                  } else if(var5 == 2) {
                     int var6 = 0;
                     var6 = var6 + (isHouse(woodlandMansionPieces$SimpleGrid, var4 + 1, var3 + 1)?1:0);
                     var6 = var6 + (isHouse(woodlandMansionPieces$SimpleGrid, var4 - 1, var3 + 1)?1:0);
                     var6 = var6 + (isHouse(woodlandMansionPieces$SimpleGrid, var4 + 1, var3 - 1)?1:0);
                     var6 = var6 + (isHouse(woodlandMansionPieces$SimpleGrid, var4 - 1, var3 - 1)?1:0);
                     if(var6 <= 1) {
                        woodlandMansionPieces$SimpleGrid.set(var4, var3, 2);
                        var2 = true;
                     }
                  }
               }
            }
         }

         return var2;
      }

      private void setupThirdFloor() {
         List<Tuple<Integer, Integer>> var1 = Lists.newArrayList();
         WoodlandMansionPieces.SimpleGrid var2 = this.floorRooms[1];

         for(int var3 = 0; var3 < this.thirdFloorGrid.height; ++var3) {
            for(int var4 = 0; var4 < this.thirdFloorGrid.width; ++var4) {
               int var5 = var2.get(var4, var3);
               int var6 = var5 & 983040;
               if(var6 == 131072 && (var5 & 2097152) == 2097152) {
                  var1.add(new Tuple(Integer.valueOf(var4), Integer.valueOf(var3)));
               }
            }
         }

         if(var1.isEmpty()) {
            this.thirdFloorGrid.set(0, 0, this.thirdFloorGrid.width, this.thirdFloorGrid.height, 5);
         } else {
            Tuple<Integer, Integer> var3 = (Tuple)var1.get(this.random.nextInt(var1.size()));
            int var4 = var2.get(((Integer)var3.getA()).intValue(), ((Integer)var3.getB()).intValue());
            var2.set(((Integer)var3.getA()).intValue(), ((Integer)var3.getB()).intValue(), var4 | 4194304);
            Direction var5 = this.get1x2RoomDirection(this.baseGrid, ((Integer)var3.getA()).intValue(), ((Integer)var3.getB()).intValue(), 1, var4 & '\uffff');
            int var6 = ((Integer)var3.getA()).intValue() + var5.getStepX();
            int var7 = ((Integer)var3.getB()).intValue() + var5.getStepZ();

            for(int var8 = 0; var8 < this.thirdFloorGrid.height; ++var8) {
               for(int var9 = 0; var9 < this.thirdFloorGrid.width; ++var9) {
                  if(!isHouse(this.baseGrid, var9, var8)) {
                     this.thirdFloorGrid.set(var9, var8, 5);
                  } else if(var9 == ((Integer)var3.getA()).intValue() && var8 == ((Integer)var3.getB()).intValue()) {
                     this.thirdFloorGrid.set(var9, var8, 3);
                  } else if(var9 == var6 && var8 == var7) {
                     this.thirdFloorGrid.set(var9, var8, 3);
                     this.floorRooms[2].set(var9, var8, 8388608);
                  }
               }
            }

            List<Direction> var8 = Lists.newArrayList();

            for(Direction var10 : Direction.Plane.HORIZONTAL) {
               if(this.thirdFloorGrid.get(var6 + var10.getStepX(), var7 + var10.getStepZ()) == 0) {
                  var8.add(var10);
               }
            }

            if(var8.isEmpty()) {
               this.thirdFloorGrid.set(0, 0, this.thirdFloorGrid.width, this.thirdFloorGrid.height, 5);
               var2.set(((Integer)var3.getA()).intValue(), ((Integer)var3.getB()).intValue(), var4);
            } else {
               Direction var9 = (Direction)var8.get(this.random.nextInt(var8.size()));
               this.recursiveCorridor(this.thirdFloorGrid, var6 + var9.getStepX(), var7 + var9.getStepZ(), var9, 4);

               while(this.cleanEdges(this.thirdFloorGrid)) {
                  ;
               }

            }
         }
      }

      private void identifyRooms(WoodlandMansionPieces.SimpleGrid var1, WoodlandMansionPieces.SimpleGrid var2) {
         List<Tuple<Integer, Integer>> var3 = Lists.newArrayList();

         for(int var4 = 0; var4 < var1.height; ++var4) {
            for(int var5 = 0; var5 < var1.width; ++var5) {
               if(var1.get(var5, var4) == 2) {
                  var3.add(new Tuple(Integer.valueOf(var5), Integer.valueOf(var4)));
               }
            }
         }

         Collections.shuffle(var3, this.random);
         int var4 = 10;

         for(Tuple<Integer, Integer> var6 : var3) {
            int var7 = ((Integer)var6.getA()).intValue();
            int var8 = ((Integer)var6.getB()).intValue();
            if(var2.get(var7, var8) == 0) {
               int var9 = var7;
               int var10 = var7;
               int var11 = var8;
               int var12 = var8;
               int var13 = 65536;
               if(var2.get(var7 + 1, var8) == 0 && var2.get(var7, var8 + 1) == 0 && var2.get(var7 + 1, var8 + 1) == 0 && var1.get(var7 + 1, var8) == 2 && var1.get(var7, var8 + 1) == 2 && var1.get(var7 + 1, var8 + 1) == 2) {
                  var10 = var7 + 1;
                  var12 = var8 + 1;
                  var13 = 262144;
               } else if(var2.get(var7 - 1, var8) == 0 && var2.get(var7, var8 + 1) == 0 && var2.get(var7 - 1, var8 + 1) == 0 && var1.get(var7 - 1, var8) == 2 && var1.get(var7, var8 + 1) == 2 && var1.get(var7 - 1, var8 + 1) == 2) {
                  var9 = var7 - 1;
                  var12 = var8 + 1;
                  var13 = 262144;
               } else if(var2.get(var7 - 1, var8) == 0 && var2.get(var7, var8 - 1) == 0 && var2.get(var7 - 1, var8 - 1) == 0 && var1.get(var7 - 1, var8) == 2 && var1.get(var7, var8 - 1) == 2 && var1.get(var7 - 1, var8 - 1) == 2) {
                  var9 = var7 - 1;
                  var11 = var8 - 1;
                  var13 = 262144;
               } else if(var2.get(var7 + 1, var8) == 0 && var1.get(var7 + 1, var8) == 2) {
                  var10 = var7 + 1;
                  var13 = 131072;
               } else if(var2.get(var7, var8 + 1) == 0 && var1.get(var7, var8 + 1) == 2) {
                  var12 = var8 + 1;
                  var13 = 131072;
               } else if(var2.get(var7 - 1, var8) == 0 && var1.get(var7 - 1, var8) == 2) {
                  var9 = var7 - 1;
                  var13 = 131072;
               } else if(var2.get(var7, var8 - 1) == 0 && var1.get(var7, var8 - 1) == 2) {
                  var11 = var8 - 1;
                  var13 = 131072;
               }

               int var14 = this.random.nextBoolean()?var9:var10;
               int var15 = this.random.nextBoolean()?var11:var12;
               int var16 = 2097152;
               if(!var1.edgesTo(var14, var15, 1)) {
                  var14 = var14 == var9?var10:var9;
                  var15 = var15 == var11?var12:var11;
                  if(!var1.edgesTo(var14, var15, 1)) {
                     var15 = var15 == var11?var12:var11;
                     if(!var1.edgesTo(var14, var15, 1)) {
                        var14 = var14 == var9?var10:var9;
                        var15 = var15 == var11?var12:var11;
                        if(!var1.edgesTo(var14, var15, 1)) {
                           var16 = 0;
                           var14 = var9;
                           var15 = var11;
                        }
                     }
                  }
               }

               for(int var17 = var11; var17 <= var12; ++var17) {
                  for(int var18 = var9; var18 <= var10; ++var18) {
                     if(var18 == var14 && var17 == var15) {
                        var2.set(var18, var17, 1048576 | var16 | var13 | var4);
                     } else {
                        var2.set(var18, var17, var13 | var4);
                     }
                  }
               }

               ++var4;
            }
         }

      }
   }

   static class MansionPiecePlacer {
      private final StructureManager structureManager;
      private final Random random;
      private int startX;
      private int startY;

      public MansionPiecePlacer(StructureManager structureManager, Random random) {
         this.structureManager = structureManager;
         this.random = random;
      }

      public void createMansion(BlockPos blockPos, Rotation rotation, List list, WoodlandMansionPieces.MansionGrid woodlandMansionPieces$MansionGrid) {
         WoodlandMansionPieces.PlacementData var5 = new WoodlandMansionPieces.PlacementData();
         var5.position = blockPos;
         var5.rotation = rotation;
         var5.wallType = "wall_flat";
         WoodlandMansionPieces.PlacementData var6 = new WoodlandMansionPieces.PlacementData();
         this.entrance(list, var5);
         var6.position = var5.position.above(8);
         var6.rotation = var5.rotation;
         var6.wallType = "wall_window";
         if(!list.isEmpty()) {
            ;
         }

         WoodlandMansionPieces.SimpleGrid var7 = woodlandMansionPieces$MansionGrid.baseGrid;
         WoodlandMansionPieces.SimpleGrid var8 = woodlandMansionPieces$MansionGrid.thirdFloorGrid;
         this.startX = woodlandMansionPieces$MansionGrid.entranceX + 1;
         this.startY = woodlandMansionPieces$MansionGrid.entranceY + 1;
         int var9 = woodlandMansionPieces$MansionGrid.entranceX + 1;
         int var10 = woodlandMansionPieces$MansionGrid.entranceY;
         this.traverseOuterWalls(list, var5, var7, Direction.SOUTH, this.startX, this.startY, var9, var10);
         this.traverseOuterWalls(list, var6, var7, Direction.SOUTH, this.startX, this.startY, var9, var10);
         WoodlandMansionPieces.PlacementData var11 = new WoodlandMansionPieces.PlacementData();
         var11.position = var5.position.above(19);
         var11.rotation = var5.rotation;
         var11.wallType = "wall_window";
         boolean var12 = false;

         for(int var13 = 0; var13 < var8.height && !var12; ++var13) {
            for(int var14 = var8.width - 1; var14 >= 0 && !var12; --var14) {
               if(WoodlandMansionPieces.MansionGrid.isHouse(var8, var14, var13)) {
                  var11.position = var11.position.relative(rotation.rotate(Direction.SOUTH), 8 + (var13 - this.startY) * 8);
                  var11.position = var11.position.relative(rotation.rotate(Direction.EAST), (var14 - this.startX) * 8);
                  this.traverseWallPiece(list, var11);
                  this.traverseOuterWalls(list, var11, var8, Direction.SOUTH, var14, var13, var14, var13);
                  var12 = true;
               }
            }
         }

         this.createRoof(list, blockPos.above(16), rotation, var7, var8);
         this.createRoof(list, blockPos.above(27), rotation, var8, (WoodlandMansionPieces.SimpleGrid)null);
         if(!list.isEmpty()) {
            ;
         }

         WoodlandMansionPieces.FloorRoomCollection[] vars13 = new WoodlandMansionPieces.FloorRoomCollection[]{new WoodlandMansionPieces.FirstFloorRoomCollection(), new WoodlandMansionPieces.SecondFloorRoomCollection(), new WoodlandMansionPieces.ThirdFloorRoomCollection()};

         for(int var14 = 0; var14 < 3; ++var14) {
            BlockPos var15 = blockPos.above(8 * var14 + (var14 == 2?3:0));
            WoodlandMansionPieces.SimpleGrid var16 = woodlandMansionPieces$MansionGrid.floorRooms[var14];
            WoodlandMansionPieces.SimpleGrid var17 = var14 == 2?var8:var7;
            String var18 = var14 == 0?"carpet_south_1":"carpet_south_2";
            String var19 = var14 == 0?"carpet_west_1":"carpet_west_2";

            for(int var20 = 0; var20 < var17.height; ++var20) {
               for(int var21 = 0; var21 < var17.width; ++var21) {
                  if(var17.get(var21, var20) == 1) {
                     BlockPos var22 = var15.relative(rotation.rotate(Direction.SOUTH), 8 + (var20 - this.startY) * 8);
                     var22 = var22.relative(rotation.rotate(Direction.EAST), (var21 - this.startX) * 8);
                     list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "corridor_floor", var22, rotation));
                     if(var17.get(var21, var20 - 1) == 1 || (var16.get(var21, var20 - 1) & 8388608) == 8388608) {
                        list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "carpet_north", var22.relative(rotation.rotate(Direction.EAST), 1).above(), rotation));
                     }

                     if(var17.get(var21 + 1, var20) == 1 || (var16.get(var21 + 1, var20) & 8388608) == 8388608) {
                        list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "carpet_east", var22.relative(rotation.rotate(Direction.SOUTH), 1).relative(rotation.rotate(Direction.EAST), 5).above(), rotation));
                     }

                     if(var17.get(var21, var20 + 1) == 1 || (var16.get(var21, var20 + 1) & 8388608) == 8388608) {
                        list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var18, var22.relative(rotation.rotate(Direction.SOUTH), 5).relative(rotation.rotate(Direction.WEST), 1), rotation));
                     }

                     if(var17.get(var21 - 1, var20) == 1 || (var16.get(var21 - 1, var20) & 8388608) == 8388608) {
                        list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var19, var22.relative(rotation.rotate(Direction.WEST), 1).relative(rotation.rotate(Direction.NORTH), 1), rotation));
                     }
                  }
               }
            }

            String var20 = var14 == 0?"indoors_wall_1":"indoors_wall_2";
            String var21 = var14 == 0?"indoors_door_1":"indoors_door_2";
            List<Direction> var22 = Lists.newArrayList();

            for(int var23 = 0; var23 < var17.height; ++var23) {
               for(int var24 = 0; var24 < var17.width; ++var24) {
                  boolean var25 = var14 == 2 && var17.get(var24, var23) == 3;
                  if(var17.get(var24, var23) == 2 || var25) {
                     int var26 = var16.get(var24, var23);
                     int var27 = var26 & 983040;
                     int var28 = var26 & '\uffff';
                     var25 = var25 && (var26 & 8388608) == 8388608;
                     var22.clear();
                     if((var26 & 2097152) == 2097152) {
                        for(Direction var30 : Direction.Plane.HORIZONTAL) {
                           if(var17.get(var24 + var30.getStepX(), var23 + var30.getStepZ()) == 1) {
                              var22.add(var30);
                           }
                        }
                     }

                     Direction var29 = null;
                     if(!var22.isEmpty()) {
                        var29 = (Direction)var22.get(this.random.nextInt(var22.size()));
                     } else if((var26 & 1048576) == 1048576) {
                        var29 = Direction.UP;
                     }

                     BlockPos var30 = var15.relative(rotation.rotate(Direction.SOUTH), 8 + (var23 - this.startY) * 8);
                     var30 = var30.relative(rotation.rotate(Direction.EAST), -1 + (var24 - this.startX) * 8);
                     if(WoodlandMansionPieces.MansionGrid.isHouse(var17, var24 - 1, var23) && !woodlandMansionPieces$MansionGrid.isRoomId(var17, var24 - 1, var23, var14, var28)) {
                        list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var29 == Direction.WEST?var21:var20, var30, rotation));
                     }

                     if(var17.get(var24 + 1, var23) == 1 && !var25) {
                        BlockPos var31 = var30.relative(rotation.rotate(Direction.EAST), 8);
                        list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var29 == Direction.EAST?var21:var20, var31, rotation));
                     }

                     if(WoodlandMansionPieces.MansionGrid.isHouse(var17, var24, var23 + 1) && !woodlandMansionPieces$MansionGrid.isRoomId(var17, var24, var23 + 1, var14, var28)) {
                        BlockPos var31 = var30.relative(rotation.rotate(Direction.SOUTH), 7);
                        var31 = var31.relative(rotation.rotate(Direction.EAST), 7);
                        list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var29 == Direction.SOUTH?var21:var20, var31, rotation.getRotated(Rotation.CLOCKWISE_90)));
                     }

                     if(var17.get(var24, var23 - 1) == 1 && !var25) {
                        BlockPos var31 = var30.relative(rotation.rotate(Direction.NORTH), 1);
                        var31 = var31.relative(rotation.rotate(Direction.EAST), 7);
                        list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var29 == Direction.NORTH?var21:var20, var31, rotation.getRotated(Rotation.CLOCKWISE_90)));
                     }

                     if(var27 == 65536) {
                        this.addRoom1x1(list, var30, rotation, var29, vars13[var14]);
                     } else if(var27 == 131072 && var29 != null) {
                        Direction var31 = woodlandMansionPieces$MansionGrid.get1x2RoomDirection(var17, var24, var23, var14, var28);
                        boolean var32 = (var26 & 4194304) == 4194304;
                        this.addRoom1x2(list, var30, rotation, var31, var29, vars13[var14], var32);
                     } else if(var27 == 262144 && var29 != null && var29 != Direction.UP) {
                        Direction var31 = var29.getClockWise();
                        if(!woodlandMansionPieces$MansionGrid.isRoomId(var17, var24 + var31.getStepX(), var23 + var31.getStepZ(), var14, var28)) {
                           var31 = var31.getOpposite();
                        }

                        this.addRoom2x2(list, var30, rotation, var31, var29, vars13[var14]);
                     } else if(var27 == 262144 && var29 == Direction.UP) {
                        this.addRoom2x2Secret(list, var30, rotation, vars13[var14]);
                     }
                  }
               }
            }
         }

      }

      private void traverseOuterWalls(List list, WoodlandMansionPieces.PlacementData woodlandMansionPieces$PlacementData, WoodlandMansionPieces.SimpleGrid woodlandMansionPieces$SimpleGrid, Direction direction, int var5, int var6, int var7, int var8) {
         int var9 = var5;
         int var10 = var6;
         Direction var11 = direction;

         while(true) {
            if(!WoodlandMansionPieces.MansionGrid.isHouse(woodlandMansionPieces$SimpleGrid, var9 + direction.getStepX(), var10 + direction.getStepZ())) {
               this.traverseTurn(list, woodlandMansionPieces$PlacementData);
               direction = direction.getClockWise();
               if(var9 != var7 || var10 != var8 || var11 != direction) {
                  this.traverseWallPiece(list, woodlandMansionPieces$PlacementData);
               }
            } else if(WoodlandMansionPieces.MansionGrid.isHouse(woodlandMansionPieces$SimpleGrid, var9 + direction.getStepX(), var10 + direction.getStepZ()) && WoodlandMansionPieces.MansionGrid.isHouse(woodlandMansionPieces$SimpleGrid, var9 + direction.getStepX() + direction.getCounterClockWise().getStepX(), var10 + direction.getStepZ() + direction.getCounterClockWise().getStepZ())) {
               this.traverseInnerTurn(list, woodlandMansionPieces$PlacementData);
               var9 += direction.getStepX();
               var10 += direction.getStepZ();
               direction = direction.getCounterClockWise();
            } else {
               var9 += direction.getStepX();
               var10 += direction.getStepZ();
               if(var9 != var7 || var10 != var8 || var11 != direction) {
                  this.traverseWallPiece(list, woodlandMansionPieces$PlacementData);
               }
            }

            if(var9 == var7 && var10 == var8 && var11 == direction) {
               break;
            }
         }

      }

      private void createRoof(List list, BlockPos blockPos, Rotation rotation, WoodlandMansionPieces.SimpleGrid var4, @Nullable WoodlandMansionPieces.SimpleGrid var5) {
         for(int var6 = 0; var6 < var4.height; ++var6) {
            for(int var7 = 0; var7 < var4.width; ++var7) {
               BlockPos var8 = blockPos.relative(rotation.rotate(Direction.SOUTH), 8 + (var6 - this.startY) * 8);
               var8 = var8.relative(rotation.rotate(Direction.EAST), (var7 - this.startX) * 8);
               boolean var9 = var5 != null && WoodlandMansionPieces.MansionGrid.isHouse(var5, var7, var6);
               if(WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6) && !var9) {
                  list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof", var8.above(3), rotation));
                  if(!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 + 1, var6)) {
                     BlockPos var10 = var8.relative(rotation.rotate(Direction.EAST), 6);
                     list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_front", var10, rotation));
                  }

                  if(!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 - 1, var6)) {
                     BlockPos var10 = var8.relative(rotation.rotate(Direction.EAST), 0);
                     var10 = var10.relative(rotation.rotate(Direction.SOUTH), 7);
                     list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_front", var10, rotation.getRotated(Rotation.CLOCKWISE_180)));
                  }

                  if(!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 - 1)) {
                     BlockPos var10 = var8.relative(rotation.rotate(Direction.WEST), 1);
                     list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_front", var10, rotation.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                  }

                  if(!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 + 1)) {
                     BlockPos var10 = var8.relative(rotation.rotate(Direction.EAST), 6);
                     var10 = var10.relative(rotation.rotate(Direction.SOUTH), 6);
                     list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_front", var10, rotation.getRotated(Rotation.CLOCKWISE_90)));
                  }
               }
            }
         }

         if(var5 != null) {
            for(int var6 = 0; var6 < var4.height; ++var6) {
               for(int var7 = 0; var7 < var4.width; ++var7) {
                  BlockPos var8 = blockPos.relative(rotation.rotate(Direction.SOUTH), 8 + (var6 - this.startY) * 8);
                  var8 = var8.relative(rotation.rotate(Direction.EAST), (var7 - this.startX) * 8);
                  boolean var9 = WoodlandMansionPieces.MansionGrid.isHouse(var5, var7, var6);
                  if(WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6) && var9) {
                     if(!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 + 1, var6)) {
                        BlockPos var10 = var8.relative(rotation.rotate(Direction.EAST), 7);
                        list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall", var10, rotation));
                     }

                     if(!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 - 1, var6)) {
                        BlockPos var10 = var8.relative(rotation.rotate(Direction.WEST), 1);
                        var10 = var10.relative(rotation.rotate(Direction.SOUTH), 6);
                        list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall", var10, rotation.getRotated(Rotation.CLOCKWISE_180)));
                     }

                     if(!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 - 1)) {
                        BlockPos var10 = var8.relative(rotation.rotate(Direction.WEST), 0);
                        var10 = var10.relative(rotation.rotate(Direction.NORTH), 1);
                        list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall", var10, rotation.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                     }

                     if(!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 + 1)) {
                        BlockPos var10 = var8.relative(rotation.rotate(Direction.EAST), 6);
                        var10 = var10.relative(rotation.rotate(Direction.SOUTH), 7);
                        list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall", var10, rotation.getRotated(Rotation.CLOCKWISE_90)));
                     }

                     if(!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 + 1, var6)) {
                        if(!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 - 1)) {
                           BlockPos var10 = var8.relative(rotation.rotate(Direction.EAST), 7);
                           var10 = var10.relative(rotation.rotate(Direction.NORTH), 2);
                           list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall_corner", var10, rotation));
                        }

                        if(!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 + 1)) {
                           BlockPos var10 = var8.relative(rotation.rotate(Direction.EAST), 8);
                           var10 = var10.relative(rotation.rotate(Direction.SOUTH), 7);
                           list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall_corner", var10, rotation.getRotated(Rotation.CLOCKWISE_90)));
                        }
                     }

                     if(!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 - 1, var6)) {
                        if(!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 - 1)) {
                           BlockPos var10 = var8.relative(rotation.rotate(Direction.WEST), 2);
                           var10 = var10.relative(rotation.rotate(Direction.NORTH), 1);
                           list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall_corner", var10, rotation.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                        }

                        if(!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 + 1)) {
                           BlockPos var10 = var8.relative(rotation.rotate(Direction.WEST), 1);
                           var10 = var10.relative(rotation.rotate(Direction.SOUTH), 8);
                           list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall_corner", var10, rotation.getRotated(Rotation.CLOCKWISE_180)));
                        }
                     }
                  }
               }
            }
         }

         for(int var6 = 0; var6 < var4.height; ++var6) {
            for(int var7 = 0; var7 < var4.width; ++var7) {
               BlockPos var19 = blockPos.relative(rotation.rotate(Direction.SOUTH), 8 + (var6 - this.startY) * 8);
               var19 = var19.relative(rotation.rotate(Direction.EAST), (var7 - this.startX) * 8);
               boolean var9 = var5 != null && WoodlandMansionPieces.MansionGrid.isHouse(var5, var7, var6);
               if(WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6) && !var9) {
                  if(!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 + 1, var6)) {
                     BlockPos var10 = var19.relative(rotation.rotate(Direction.EAST), 6);
                     if(!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 + 1)) {
                        BlockPos var11 = var10.relative(rotation.rotate(Direction.SOUTH), 6);
                        list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_corner", var11, rotation));
                     } else if(WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 + 1, var6 + 1)) {
                        BlockPos var11 = var10.relative(rotation.rotate(Direction.SOUTH), 5);
                        list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_inner_corner", var11, rotation));
                     }

                     if(!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 - 1)) {
                        list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_corner", var10, rotation.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                     } else if(WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 + 1, var6 - 1)) {
                        BlockPos var11 = var19.relative(rotation.rotate(Direction.EAST), 9);
                        var11 = var11.relative(rotation.rotate(Direction.NORTH), 2);
                        list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_inner_corner", var11, rotation.getRotated(Rotation.CLOCKWISE_90)));
                     }
                  }

                  if(!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 - 1, var6)) {
                     BlockPos var10 = var19.relative(rotation.rotate(Direction.EAST), 0);
                     var10 = var10.relative(rotation.rotate(Direction.SOUTH), 0);
                     if(!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 + 1)) {
                        BlockPos var11 = var10.relative(rotation.rotate(Direction.SOUTH), 6);
                        list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_corner", var11, rotation.getRotated(Rotation.CLOCKWISE_90)));
                     } else if(WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 - 1, var6 + 1)) {
                        BlockPos var11 = var10.relative(rotation.rotate(Direction.SOUTH), 8);
                        var11 = var11.relative(rotation.rotate(Direction.WEST), 3);
                        list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_inner_corner", var11, rotation.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                     }

                     if(!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 - 1)) {
                        list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_corner", var10, rotation.getRotated(Rotation.CLOCKWISE_180)));
                     } else if(WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 - 1, var6 - 1)) {
                        BlockPos var11 = var10.relative(rotation.rotate(Direction.SOUTH), 1);
                        list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_inner_corner", var11, rotation.getRotated(Rotation.CLOCKWISE_180)));
                     }
                  }
               }
            }
         }

      }

      private void entrance(List list, WoodlandMansionPieces.PlacementData woodlandMansionPieces$PlacementData) {
         Direction var3 = woodlandMansionPieces$PlacementData.rotation.rotate(Direction.WEST);
         list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "entrance", woodlandMansionPieces$PlacementData.position.relative(var3, 9), woodlandMansionPieces$PlacementData.rotation));
         woodlandMansionPieces$PlacementData.position = woodlandMansionPieces$PlacementData.position.relative(woodlandMansionPieces$PlacementData.rotation.rotate(Direction.SOUTH), 16);
      }

      private void traverseWallPiece(List list, WoodlandMansionPieces.PlacementData woodlandMansionPieces$PlacementData) {
         list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, woodlandMansionPieces$PlacementData.wallType, woodlandMansionPieces$PlacementData.position.relative(woodlandMansionPieces$PlacementData.rotation.rotate(Direction.EAST), 7), woodlandMansionPieces$PlacementData.rotation));
         woodlandMansionPieces$PlacementData.position = woodlandMansionPieces$PlacementData.position.relative(woodlandMansionPieces$PlacementData.rotation.rotate(Direction.SOUTH), 8);
      }

      private void traverseTurn(List list, WoodlandMansionPieces.PlacementData woodlandMansionPieces$PlacementData) {
         woodlandMansionPieces$PlacementData.position = woodlandMansionPieces$PlacementData.position.relative(woodlandMansionPieces$PlacementData.rotation.rotate(Direction.SOUTH), -1);
         list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "wall_corner", woodlandMansionPieces$PlacementData.position, woodlandMansionPieces$PlacementData.rotation));
         woodlandMansionPieces$PlacementData.position = woodlandMansionPieces$PlacementData.position.relative(woodlandMansionPieces$PlacementData.rotation.rotate(Direction.SOUTH), -7);
         woodlandMansionPieces$PlacementData.position = woodlandMansionPieces$PlacementData.position.relative(woodlandMansionPieces$PlacementData.rotation.rotate(Direction.WEST), -6);
         woodlandMansionPieces$PlacementData.rotation = woodlandMansionPieces$PlacementData.rotation.getRotated(Rotation.CLOCKWISE_90);
      }

      private void traverseInnerTurn(List list, WoodlandMansionPieces.PlacementData woodlandMansionPieces$PlacementData) {
         woodlandMansionPieces$PlacementData.position = woodlandMansionPieces$PlacementData.position.relative(woodlandMansionPieces$PlacementData.rotation.rotate(Direction.SOUTH), 6);
         woodlandMansionPieces$PlacementData.position = woodlandMansionPieces$PlacementData.position.relative(woodlandMansionPieces$PlacementData.rotation.rotate(Direction.EAST), 8);
         woodlandMansionPieces$PlacementData.rotation = woodlandMansionPieces$PlacementData.rotation.getRotated(Rotation.COUNTERCLOCKWISE_90);
      }

      private void addRoom1x1(List list, BlockPos blockPos, Rotation rotation, Direction direction, WoodlandMansionPieces.FloorRoomCollection woodlandMansionPieces$FloorRoomCollection) {
         Rotation rotation = Rotation.NONE;
         String var7 = woodlandMansionPieces$FloorRoomCollection.get1x1(this.random);
         if(direction != Direction.EAST) {
            if(direction == Direction.NORTH) {
               rotation = rotation.getRotated(Rotation.COUNTERCLOCKWISE_90);
            } else if(direction == Direction.WEST) {
               rotation = rotation.getRotated(Rotation.CLOCKWISE_180);
            } else if(direction == Direction.SOUTH) {
               rotation = rotation.getRotated(Rotation.CLOCKWISE_90);
            } else {
               var7 = woodlandMansionPieces$FloorRoomCollection.get1x1Secret(this.random);
            }
         }

         BlockPos var8 = StructureTemplate.getZeroPositionWithTransform(new BlockPos(1, 0, 0), Mirror.NONE, rotation, 7, 7);
         rotation = rotation.getRotated(rotation);
         var8 = var8.rotate(rotation);
         BlockPos var9 = blockPos.offset(var8.getX(), 0, var8.getZ());
         list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var7, var9, rotation));
      }

      private void addRoom1x2(List list, BlockPos blockPos, Rotation rotation, Direction var4, Direction var5, WoodlandMansionPieces.FloorRoomCollection woodlandMansionPieces$FloorRoomCollection, boolean var7) {
         if(var5 == Direction.EAST && var4 == Direction.SOUTH) {
            BlockPos blockPos = blockPos.relative(rotation.rotate(Direction.EAST), 1);
            list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, woodlandMansionPieces$FloorRoomCollection.get1x2SideEntrance(this.random, var7), blockPos, rotation));
         } else if(var5 == Direction.EAST && var4 == Direction.NORTH) {
            BlockPos blockPos = blockPos.relative(rotation.rotate(Direction.EAST), 1);
            blockPos = blockPos.relative(rotation.rotate(Direction.SOUTH), 6);
            list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, woodlandMansionPieces$FloorRoomCollection.get1x2SideEntrance(this.random, var7), blockPos, rotation, Mirror.LEFT_RIGHT));
         } else if(var5 == Direction.WEST && var4 == Direction.NORTH) {
            BlockPos blockPos = blockPos.relative(rotation.rotate(Direction.EAST), 7);
            blockPos = blockPos.relative(rotation.rotate(Direction.SOUTH), 6);
            list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, woodlandMansionPieces$FloorRoomCollection.get1x2SideEntrance(this.random, var7), blockPos, rotation.getRotated(Rotation.CLOCKWISE_180)));
         } else if(var5 == Direction.WEST && var4 == Direction.SOUTH) {
            BlockPos blockPos = blockPos.relative(rotation.rotate(Direction.EAST), 7);
            list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, woodlandMansionPieces$FloorRoomCollection.get1x2SideEntrance(this.random, var7), blockPos, rotation, Mirror.FRONT_BACK));
         } else if(var5 == Direction.SOUTH && var4 == Direction.EAST) {
            BlockPos blockPos = blockPos.relative(rotation.rotate(Direction.EAST), 1);
            list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, woodlandMansionPieces$FloorRoomCollection.get1x2SideEntrance(this.random, var7), blockPos, rotation.getRotated(Rotation.CLOCKWISE_90), Mirror.LEFT_RIGHT));
         } else if(var5 == Direction.SOUTH && var4 == Direction.WEST) {
            BlockPos blockPos = blockPos.relative(rotation.rotate(Direction.EAST), 7);
            list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, woodlandMansionPieces$FloorRoomCollection.get1x2SideEntrance(this.random, var7), blockPos, rotation.getRotated(Rotation.CLOCKWISE_90)));
         } else if(var5 == Direction.NORTH && var4 == Direction.WEST) {
            BlockPos blockPos = blockPos.relative(rotation.rotate(Direction.EAST), 7);
            blockPos = blockPos.relative(rotation.rotate(Direction.SOUTH), 6);
            list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, woodlandMansionPieces$FloorRoomCollection.get1x2SideEntrance(this.random, var7), blockPos, rotation.getRotated(Rotation.CLOCKWISE_90), Mirror.FRONT_BACK));
         } else if(var5 == Direction.NORTH && var4 == Direction.EAST) {
            BlockPos blockPos = blockPos.relative(rotation.rotate(Direction.EAST), 1);
            blockPos = blockPos.relative(rotation.rotate(Direction.SOUTH), 6);
            list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, woodlandMansionPieces$FloorRoomCollection.get1x2SideEntrance(this.random, var7), blockPos, rotation.getRotated(Rotation.COUNTERCLOCKWISE_90)));
         } else if(var5 == Direction.SOUTH && var4 == Direction.NORTH) {
            BlockPos blockPos = blockPos.relative(rotation.rotate(Direction.EAST), 1);
            blockPos = blockPos.relative(rotation.rotate(Direction.NORTH), 8);
            list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, woodlandMansionPieces$FloorRoomCollection.get1x2FrontEntrance(this.random, var7), blockPos, rotation));
         } else if(var5 == Direction.NORTH && var4 == Direction.SOUTH) {
            BlockPos blockPos = blockPos.relative(rotation.rotate(Direction.EAST), 7);
            blockPos = blockPos.relative(rotation.rotate(Direction.SOUTH), 14);
            list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, woodlandMansionPieces$FloorRoomCollection.get1x2FrontEntrance(this.random, var7), blockPos, rotation.getRotated(Rotation.CLOCKWISE_180)));
         } else if(var5 == Direction.WEST && var4 == Direction.EAST) {
            BlockPos blockPos = blockPos.relative(rotation.rotate(Direction.EAST), 15);
            list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, woodlandMansionPieces$FloorRoomCollection.get1x2FrontEntrance(this.random, var7), blockPos, rotation.getRotated(Rotation.CLOCKWISE_90)));
         } else if(var5 == Direction.EAST && var4 == Direction.WEST) {
            BlockPos blockPos = blockPos.relative(rotation.rotate(Direction.WEST), 7);
            blockPos = blockPos.relative(rotation.rotate(Direction.SOUTH), 6);
            list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, woodlandMansionPieces$FloorRoomCollection.get1x2FrontEntrance(this.random, var7), blockPos, rotation.getRotated(Rotation.COUNTERCLOCKWISE_90)));
         } else if(var5 == Direction.UP && var4 == Direction.EAST) {
            BlockPos blockPos = blockPos.relative(rotation.rotate(Direction.EAST), 15);
            list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, woodlandMansionPieces$FloorRoomCollection.get1x2Secret(this.random), blockPos, rotation.getRotated(Rotation.CLOCKWISE_90)));
         } else if(var5 == Direction.UP && var4 == Direction.SOUTH) {
            BlockPos blockPos = blockPos.relative(rotation.rotate(Direction.EAST), 1);
            blockPos = blockPos.relative(rotation.rotate(Direction.NORTH), 0);
            list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, woodlandMansionPieces$FloorRoomCollection.get1x2Secret(this.random), blockPos, rotation));
         }

      }

      private void addRoom2x2(List list, BlockPos blockPos, Rotation rotation, Direction var4, Direction var5, WoodlandMansionPieces.FloorRoomCollection woodlandMansionPieces$FloorRoomCollection) {
         int var7 = 0;
         int var8 = 0;
         Rotation var9 = rotation;
         Mirror var10 = Mirror.NONE;
         if(var5 == Direction.EAST && var4 == Direction.SOUTH) {
            var7 = -7;
         } else if(var5 == Direction.EAST && var4 == Direction.NORTH) {
            var7 = -7;
            var8 = 6;
            var10 = Mirror.LEFT_RIGHT;
         } else if(var5 == Direction.NORTH && var4 == Direction.EAST) {
            var7 = 1;
            var8 = 14;
            var9 = rotation.getRotated(Rotation.COUNTERCLOCKWISE_90);
         } else if(var5 == Direction.NORTH && var4 == Direction.WEST) {
            var7 = 7;
            var8 = 14;
            var9 = rotation.getRotated(Rotation.COUNTERCLOCKWISE_90);
            var10 = Mirror.LEFT_RIGHT;
         } else if(var5 == Direction.SOUTH && var4 == Direction.WEST) {
            var7 = 7;
            var8 = -8;
            var9 = rotation.getRotated(Rotation.CLOCKWISE_90);
         } else if(var5 == Direction.SOUTH && var4 == Direction.EAST) {
            var7 = 1;
            var8 = -8;
            var9 = rotation.getRotated(Rotation.CLOCKWISE_90);
            var10 = Mirror.LEFT_RIGHT;
         } else if(var5 == Direction.WEST && var4 == Direction.NORTH) {
            var7 = 15;
            var8 = 6;
            var9 = rotation.getRotated(Rotation.CLOCKWISE_180);
         } else if(var5 == Direction.WEST && var4 == Direction.SOUTH) {
            var7 = 15;
            var10 = Mirror.FRONT_BACK;
         }

         BlockPos var11 = blockPos.relative(rotation.rotate(Direction.EAST), var7);
         var11 = var11.relative(rotation.rotate(Direction.SOUTH), var8);
         list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, woodlandMansionPieces$FloorRoomCollection.get2x2(this.random), var11, var9, var10));
      }

      private void addRoom2x2Secret(List list, BlockPos blockPos, Rotation rotation, WoodlandMansionPieces.FloorRoomCollection woodlandMansionPieces$FloorRoomCollection) {
         BlockPos blockPos = blockPos.relative(rotation.rotate(Direction.EAST), 1);
         list.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, woodlandMansionPieces$FloorRoomCollection.get2x2Secret(this.random), blockPos, rotation, Mirror.NONE));
      }
   }

   static class PlacementData {
      public Rotation rotation;
      public BlockPos position;
      public String wallType;

      private PlacementData() {
      }
   }

   static class SecondFloorRoomCollection extends WoodlandMansionPieces.FloorRoomCollection {
      private SecondFloorRoomCollection() {
         super(null);
      }

      public String get1x1(Random random) {
         return "1x1_b" + (random.nextInt(4) + 1);
      }

      public String get1x1Secret(Random random) {
         return "1x1_as" + (random.nextInt(4) + 1);
      }

      public String get1x2SideEntrance(Random random, boolean var2) {
         return var2?"1x2_c_stairs":"1x2_c" + (random.nextInt(4) + 1);
      }

      public String get1x2FrontEntrance(Random random, boolean var2) {
         return var2?"1x2_d_stairs":"1x2_d" + (random.nextInt(5) + 1);
      }

      public String get1x2Secret(Random random) {
         return "1x2_se" + (random.nextInt(1) + 1);
      }

      public String get2x2(Random random) {
         return "2x2_b" + (random.nextInt(5) + 1);
      }

      public String get2x2Secret(Random random) {
         return "2x2_s1";
      }
   }

   static class SimpleGrid {
      private final int[][] grid;
      private final int width;
      private final int height;
      private final int valueIfOutside;

      public SimpleGrid(int width, int height, int valueIfOutside) {
         this.width = width;
         this.height = height;
         this.valueIfOutside = valueIfOutside;
         this.grid = new int[width][height];
      }

      public void set(int var1, int var2, int var3) {
         if(var1 >= 0 && var1 < this.width && var2 >= 0 && var2 < this.height) {
            this.grid[var1][var2] = var3;
         }

      }

      public void set(int var1, int var2, int var3, int var4, int var5) {
         for(int var6 = var2; var6 <= var4; ++var6) {
            for(int var7 = var1; var7 <= var3; ++var7) {
               this.set(var7, var6, var5);
            }
         }

      }

      public int get(int var1, int var2) {
         return var1 >= 0 && var1 < this.width && var2 >= 0 && var2 < this.height?this.grid[var1][var2]:this.valueIfOutside;
      }

      public void setif(int var1, int var2, int var3, int var4) {
         if(this.get(var1, var2) == var3) {
            this.set(var1, var2, var4);
         }

      }

      public boolean edgesTo(int var1, int var2, int var3) {
         return this.get(var1 - 1, var2) == var3 || this.get(var1 + 1, var2) == var3 || this.get(var1, var2 + 1) == var3 || this.get(var1, var2 - 1) == var3;
      }
   }

   static class ThirdFloorRoomCollection extends WoodlandMansionPieces.SecondFloorRoomCollection {
      private ThirdFloorRoomCollection() {
         super(null);
      }
   }

   public static class WoodlandMansionPiece extends TemplateStructurePiece {
      private final String templateName;
      private final Rotation rotation;
      private final Mirror mirror;

      public WoodlandMansionPiece(StructureManager structureManager, String string, BlockPos blockPos, Rotation rotation) {
         this(structureManager, string, blockPos, rotation, Mirror.NONE);
      }

      public WoodlandMansionPiece(StructureManager structureManager, String templateName, BlockPos templatePosition, Rotation rotation, Mirror mirror) {
         super(StructurePieceType.WOODLAND_MANSION_PIECE, 0);
         this.templateName = templateName;
         this.templatePosition = templatePosition;
         this.rotation = rotation;
         this.mirror = mirror;
         this.loadTemplate(structureManager);
      }

      public WoodlandMansionPiece(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.WOODLAND_MANSION_PIECE, compoundTag);
         this.templateName = compoundTag.getString("Template");
         this.rotation = Rotation.valueOf(compoundTag.getString("Rot"));
         this.mirror = Mirror.valueOf(compoundTag.getString("Mi"));
         this.loadTemplate(structureManager);
      }

      private void loadTemplate(StructureManager structureManager) {
         StructureTemplate var2 = structureManager.getOrCreate(new ResourceLocation("woodland_mansion/" + this.templateName));
         StructurePlaceSettings var3 = (new StructurePlaceSettings()).setIgnoreEntities(true).setRotation(this.rotation).setMirror(this.mirror).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
         this.setup(var2, this.templatePosition, var3);
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         super.addAdditionalSaveData(compoundTag);
         compoundTag.putString("Template", this.templateName);
         compoundTag.putString("Rot", this.placeSettings.getRotation().name());
         compoundTag.putString("Mi", this.placeSettings.getMirror().name());
      }

      protected void handleDataMarker(String string, BlockPos blockPos, LevelAccessor levelAccessor, Random random, BoundingBox boundingBox) {
         if(string.startsWith("Chest")) {
            Rotation var6 = this.placeSettings.getRotation();
            BlockState var7 = Blocks.CHEST.defaultBlockState();
            if("ChestWest".equals(string)) {
               var7 = (BlockState)var7.setValue(ChestBlock.FACING, var6.rotate(Direction.WEST));
            } else if("ChestEast".equals(string)) {
               var7 = (BlockState)var7.setValue(ChestBlock.FACING, var6.rotate(Direction.EAST));
            } else if("ChestSouth".equals(string)) {
               var7 = (BlockState)var7.setValue(ChestBlock.FACING, var6.rotate(Direction.SOUTH));
            } else if("ChestNorth".equals(string)) {
               var7 = (BlockState)var7.setValue(ChestBlock.FACING, var6.rotate(Direction.NORTH));
            }

            this.createChest(levelAccessor, boundingBox, random, blockPos, BuiltInLootTables.WOODLAND_MANSION, var7);
         } else {
            byte var8 = -1;
            switch(string.hashCode()) {
            case -1505748702:
               if(string.equals("Warrior")) {
                  var8 = 1;
               }
               break;
            case 2390418:
               if(string.equals("Mage")) {
                  var8 = 0;
               }
            }

            AbstractIllager var6;
            switch(var8) {
            case 0:
               var6 = (AbstractIllager)EntityType.EVOKER.create(levelAccessor.getLevel());
               break;
            case 1:
               var6 = (AbstractIllager)EntityType.VINDICATOR.create(levelAccessor.getLevel());
               break;
            default:
               return;
            }

            var6.setPersistenceRequired();
            var6.moveTo(blockPos, 0.0F, 0.0F);
            var6.finalizeSpawn(levelAccessor, levelAccessor.getCurrentDifficultyAt(new BlockPos(var6)), MobSpawnType.STRUCTURE, (SpawnGroupData)null, (CompoundTag)null);
            levelAccessor.addFreshEntity(var6);
            levelAccessor.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 2);
         }

      }
   }
}
