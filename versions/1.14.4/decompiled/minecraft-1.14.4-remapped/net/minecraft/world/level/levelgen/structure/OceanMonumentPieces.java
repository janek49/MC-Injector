package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class OceanMonumentPieces {
   static class FitDoubleXRoom implements OceanMonumentPieces.MonumentRoomFitter {
      private FitDoubleXRoom() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition) {
         return oceanMonumentPieces$RoomDefinition.hasOpening[Direction.EAST.get3DDataValue()] && !oceanMonumentPieces$RoomDefinition.connections[Direction.EAST.get3DDataValue()].claimed;
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction direction, OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition, Random random) {
         oceanMonumentPieces$RoomDefinition.claimed = true;
         oceanMonumentPieces$RoomDefinition.connections[Direction.EAST.get3DDataValue()].claimed = true;
         return new OceanMonumentPieces.OceanMonumentDoubleXRoom(direction, oceanMonumentPieces$RoomDefinition);
      }
   }

   static class FitDoubleXYRoom implements OceanMonumentPieces.MonumentRoomFitter {
      private FitDoubleXYRoom() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition) {
         if(oceanMonumentPieces$RoomDefinition.hasOpening[Direction.EAST.get3DDataValue()] && !oceanMonumentPieces$RoomDefinition.connections[Direction.EAST.get3DDataValue()].claimed && oceanMonumentPieces$RoomDefinition.hasOpening[Direction.UP.get3DDataValue()] && !oceanMonumentPieces$RoomDefinition.connections[Direction.UP.get3DDataValue()].claimed) {
            OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition = oceanMonumentPieces$RoomDefinition.connections[Direction.EAST.get3DDataValue()];
            return oceanMonumentPieces$RoomDefinition.hasOpening[Direction.UP.get3DDataValue()] && !oceanMonumentPieces$RoomDefinition.connections[Direction.UP.get3DDataValue()].claimed;
         } else {
            return false;
         }
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction direction, OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition, Random random) {
         oceanMonumentPieces$RoomDefinition.claimed = true;
         oceanMonumentPieces$RoomDefinition.connections[Direction.EAST.get3DDataValue()].claimed = true;
         oceanMonumentPieces$RoomDefinition.connections[Direction.UP.get3DDataValue()].claimed = true;
         oceanMonumentPieces$RoomDefinition.connections[Direction.EAST.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
         return new OceanMonumentPieces.OceanMonumentDoubleXYRoom(direction, oceanMonumentPieces$RoomDefinition);
      }
   }

   static class FitDoubleYRoom implements OceanMonumentPieces.MonumentRoomFitter {
      private FitDoubleYRoom() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition) {
         return oceanMonumentPieces$RoomDefinition.hasOpening[Direction.UP.get3DDataValue()] && !oceanMonumentPieces$RoomDefinition.connections[Direction.UP.get3DDataValue()].claimed;
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction direction, OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition, Random random) {
         oceanMonumentPieces$RoomDefinition.claimed = true;
         oceanMonumentPieces$RoomDefinition.connections[Direction.UP.get3DDataValue()].claimed = true;
         return new OceanMonumentPieces.OceanMonumentDoubleYRoom(direction, oceanMonumentPieces$RoomDefinition);
      }
   }

   static class FitDoubleYZRoom implements OceanMonumentPieces.MonumentRoomFitter {
      private FitDoubleYZRoom() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition) {
         if(oceanMonumentPieces$RoomDefinition.hasOpening[Direction.NORTH.get3DDataValue()] && !oceanMonumentPieces$RoomDefinition.connections[Direction.NORTH.get3DDataValue()].claimed && oceanMonumentPieces$RoomDefinition.hasOpening[Direction.UP.get3DDataValue()] && !oceanMonumentPieces$RoomDefinition.connections[Direction.UP.get3DDataValue()].claimed) {
            OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition = oceanMonumentPieces$RoomDefinition.connections[Direction.NORTH.get3DDataValue()];
            return oceanMonumentPieces$RoomDefinition.hasOpening[Direction.UP.get3DDataValue()] && !oceanMonumentPieces$RoomDefinition.connections[Direction.UP.get3DDataValue()].claimed;
         } else {
            return false;
         }
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction direction, OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition, Random random) {
         oceanMonumentPieces$RoomDefinition.claimed = true;
         oceanMonumentPieces$RoomDefinition.connections[Direction.NORTH.get3DDataValue()].claimed = true;
         oceanMonumentPieces$RoomDefinition.connections[Direction.UP.get3DDataValue()].claimed = true;
         oceanMonumentPieces$RoomDefinition.connections[Direction.NORTH.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
         return new OceanMonumentPieces.OceanMonumentDoubleYZRoom(direction, oceanMonumentPieces$RoomDefinition);
      }
   }

   static class FitDoubleZRoom implements OceanMonumentPieces.MonumentRoomFitter {
      private FitDoubleZRoom() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition) {
         return oceanMonumentPieces$RoomDefinition.hasOpening[Direction.NORTH.get3DDataValue()] && !oceanMonumentPieces$RoomDefinition.connections[Direction.NORTH.get3DDataValue()].claimed;
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction direction, OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition, Random random) {
         OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition = oceanMonumentPieces$RoomDefinition;
         if(!oceanMonumentPieces$RoomDefinition.hasOpening[Direction.NORTH.get3DDataValue()] || oceanMonumentPieces$RoomDefinition.connections[Direction.NORTH.get3DDataValue()].claimed) {
            oceanMonumentPieces$RoomDefinition = oceanMonumentPieces$RoomDefinition.connections[Direction.SOUTH.get3DDataValue()];
         }

         oceanMonumentPieces$RoomDefinition.claimed = true;
         oceanMonumentPieces$RoomDefinition.connections[Direction.NORTH.get3DDataValue()].claimed = true;
         return new OceanMonumentPieces.OceanMonumentDoubleZRoom(direction, oceanMonumentPieces$RoomDefinition);
      }
   }

   static class FitSimpleRoom implements OceanMonumentPieces.MonumentRoomFitter {
      private FitSimpleRoom() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition) {
         return true;
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction direction, OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition, Random random) {
         oceanMonumentPieces$RoomDefinition.claimed = true;
         return new OceanMonumentPieces.OceanMonumentSimpleRoom(direction, oceanMonumentPieces$RoomDefinition, random);
      }
   }

   static class FitSimpleTopRoom implements OceanMonumentPieces.MonumentRoomFitter {
      private FitSimpleTopRoom() {
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition) {
         return !oceanMonumentPieces$RoomDefinition.hasOpening[Direction.WEST.get3DDataValue()] && !oceanMonumentPieces$RoomDefinition.hasOpening[Direction.EAST.get3DDataValue()] && !oceanMonumentPieces$RoomDefinition.hasOpening[Direction.NORTH.get3DDataValue()] && !oceanMonumentPieces$RoomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()] && !oceanMonumentPieces$RoomDefinition.hasOpening[Direction.UP.get3DDataValue()];
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction direction, OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition, Random random) {
         oceanMonumentPieces$RoomDefinition.claimed = true;
         return new OceanMonumentPieces.OceanMonumentSimpleTopRoom(direction, oceanMonumentPieces$RoomDefinition);
      }
   }

   public static class MonumentBuilding extends OceanMonumentPieces.OceanMonumentPiece {
      private OceanMonumentPieces.RoomDefinition sourceRoom;
      private OceanMonumentPieces.RoomDefinition coreRoom;
      private final List childPieces = Lists.newArrayList();

      public MonumentBuilding(Random random, int var2, int var3, Direction orientation) {
         super(StructurePieceType.OCEAN_MONUMENT_BUILDING, 0);
         this.setOrientation(orientation);
         Direction direction = this.getOrientation();
         if(direction.getAxis() == Direction.Axis.Z) {
            this.boundingBox = new BoundingBox(var2, 39, var3, var2 + 58 - 1, 61, var3 + 58 - 1);
         } else {
            this.boundingBox = new BoundingBox(var2, 39, var3, var2 + 58 - 1, 61, var3 + 58 - 1);
         }

         List<OceanMonumentPieces.RoomDefinition> var6 = this.generateRoomGraph(random);
         this.sourceRoom.claimed = true;
         this.childPieces.add(new OceanMonumentPieces.OceanMonumentEntryRoom(direction, this.sourceRoom));
         this.childPieces.add(new OceanMonumentPieces.OceanMonumentCoreRoom(direction, this.coreRoom));
         List<OceanMonumentPieces.MonumentRoomFitter> var7 = Lists.newArrayList();
         var7.add(new OceanMonumentPieces.FitDoubleXYRoom());
         var7.add(new OceanMonumentPieces.FitDoubleYZRoom());
         var7.add(new OceanMonumentPieces.FitDoubleZRoom());
         var7.add(new OceanMonumentPieces.FitDoubleXRoom());
         var7.add(new OceanMonumentPieces.FitDoubleYRoom());
         var7.add(new OceanMonumentPieces.FitSimpleTopRoom());
         var7.add(new OceanMonumentPieces.FitSimpleRoom());

         label297:
         for(OceanMonumentPieces.RoomDefinition var9 : var6) {
            if(!var9.claimed && !var9.isSpecial()) {
               Iterator var10 = var7.iterator();

               OceanMonumentPieces.MonumentRoomFitter var11;
               while(true) {
                  if(!var10.hasNext()) {
                     continue label297;
                  }

                  var11 = (OceanMonumentPieces.MonumentRoomFitter)var10.next();
                  if(var11.fits(var9)) {
                     break;
                  }
               }

               this.childPieces.add(var11.create(direction, var9, random));
            }
         }

         int var8 = this.boundingBox.y0;
         int var9 = this.getWorldX(9, 22);
         int var10 = this.getWorldZ(9, 22);

         for(OceanMonumentPieces.OceanMonumentPiece var12 : this.childPieces) {
            var12.getBoundingBox().move(var9, var8, var10);
         }

         BoundingBox var11 = BoundingBox.createProper(this.getWorldX(1, 1), this.getWorldY(1), this.getWorldZ(1, 1), this.getWorldX(23, 21), this.getWorldY(8), this.getWorldZ(23, 21));
         BoundingBox var12 = BoundingBox.createProper(this.getWorldX(34, 1), this.getWorldY(1), this.getWorldZ(34, 1), this.getWorldX(56, 21), this.getWorldY(8), this.getWorldZ(56, 21));
         BoundingBox var13 = BoundingBox.createProper(this.getWorldX(22, 22), this.getWorldY(13), this.getWorldZ(22, 22), this.getWorldX(35, 35), this.getWorldY(17), this.getWorldZ(35, 35));
         int var14 = random.nextInt();
         this.childPieces.add(new OceanMonumentPieces.OceanMonumentWingRoom(direction, var11, var14++));
         this.childPieces.add(new OceanMonumentPieces.OceanMonumentWingRoom(direction, var12, var14++));
         this.childPieces.add(new OceanMonumentPieces.OceanMonumentPenthouse(direction, var13));
      }

      public MonumentBuilding(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.OCEAN_MONUMENT_BUILDING, compoundTag);
      }

      private List generateRoomGraph(Random random) {
         OceanMonumentPieces.RoomDefinition[] vars2 = new OceanMonumentPieces.RoomDefinition[75];

         for(int var3 = 0; var3 < 5; ++var3) {
            for(int var4 = 0; var4 < 4; ++var4) {
               int var5 = 0;
               int var6 = getRoomIndex(var3, 0, var4);
               vars2[var6] = new OceanMonumentPieces.RoomDefinition(var6);
            }
         }

         for(int var3 = 0; var3 < 5; ++var3) {
            for(int var4 = 0; var4 < 4; ++var4) {
               int var5 = 1;
               int var6 = getRoomIndex(var3, 1, var4);
               vars2[var6] = new OceanMonumentPieces.RoomDefinition(var6);
            }
         }

         for(int var3 = 1; var3 < 4; ++var3) {
            for(int var4 = 0; var4 < 2; ++var4) {
               int var5 = 2;
               int var6 = getRoomIndex(var3, 2, var4);
               vars2[var6] = new OceanMonumentPieces.RoomDefinition(var6);
            }
         }

         this.sourceRoom = vars2[GRIDROOM_SOURCE_INDEX];

         for(int var3 = 0; var3 < 5; ++var3) {
            for(int var4 = 0; var4 < 5; ++var4) {
               for(int var5 = 0; var5 < 3; ++var5) {
                  int var6 = getRoomIndex(var3, var5, var4);
                  if(vars2[var6] != null) {
                     for(Direction var10 : Direction.values()) {
                        int var11 = var3 + var10.getStepX();
                        int var12 = var5 + var10.getStepY();
                        int var13 = var4 + var10.getStepZ();
                        if(var11 >= 0 && var11 < 5 && var13 >= 0 && var13 < 5 && var12 >= 0 && var12 < 3) {
                           int var14 = getRoomIndex(var11, var12, var13);
                           if(vars2[var14] != null) {
                              if(var13 == var4) {
                                 vars2[var6].setConnection(var10, vars2[var14]);
                              } else {
                                 vars2[var6].setConnection(var10.getOpposite(), vars2[var14]);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         OceanMonumentPieces.RoomDefinition var3 = new OceanMonumentPieces.RoomDefinition(1003);
         OceanMonumentPieces.RoomDefinition var4 = new OceanMonumentPieces.RoomDefinition(1001);
         OceanMonumentPieces.RoomDefinition var5 = new OceanMonumentPieces.RoomDefinition(1002);
         vars2[GRIDROOM_TOP_CONNECT_INDEX].setConnection(Direction.UP, var3);
         vars2[GRIDROOM_LEFTWING_CONNECT_INDEX].setConnection(Direction.SOUTH, var4);
         vars2[GRIDROOM_RIGHTWING_CONNECT_INDEX].setConnection(Direction.SOUTH, var5);
         var3.claimed = true;
         var4.claimed = true;
         var5.claimed = true;
         this.sourceRoom.isSource = true;
         this.coreRoom = vars2[getRoomIndex(random.nextInt(4), 0, 2)];
         this.coreRoom.claimed = true;
         this.coreRoom.connections[Direction.EAST.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.NORTH.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.EAST.get3DDataValue()].connections[Direction.NORTH.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.UP.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.EAST.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.NORTH.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.EAST.get3DDataValue()].connections[Direction.NORTH.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
         List<OceanMonumentPieces.RoomDefinition> var6 = Lists.newArrayList();

         for(OceanMonumentPieces.RoomDefinition var10 : vars2) {
            if(var10 != null) {
               var10.updateOpenings();
               var6.add(var10);
            }
         }

         var3.updateOpenings();
         Collections.shuffle(var6, random);
         int var7 = 1;

         for(OceanMonumentPieces.RoomDefinition var9 : var6) {
            int var10 = 0;
            int var11 = 0;

            while(var10 < 2 && var11 < 5) {
               ++var11;
               int var12 = random.nextInt(6);
               if(var9.hasOpening[var12]) {
                  int var13 = Direction.from3DDataValue(var12).getOpposite().get3DDataValue();
                  var9.hasOpening[var12] = false;
                  var9.connections[var12].hasOpening[var13] = false;
                  if(var9.findSource(var7++) && var9.connections[var12].findSource(var7++)) {
                     ++var10;
                  } else {
                     var9.hasOpening[var12] = true;
                     var9.connections[var12].hasOpening[var13] = true;
                  }
               }
            }
         }

         var6.add(var3);
         var6.add(var4);
         var6.add(var5);
         return var6;
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         int var5 = Math.max(levelAccessor.getSeaLevel(), 64) - this.boundingBox.y0;
         this.generateWaterBox(levelAccessor, boundingBox, 0, 0, 0, 58, var5, 58);
         this.generateWing(false, 0, levelAccessor, random, boundingBox);
         this.generateWing(true, 33, levelAccessor, random, boundingBox);
         this.generateEntranceArchs(levelAccessor, random, boundingBox);
         this.generateEntranceWall(levelAccessor, random, boundingBox);
         this.generateRoofPiece(levelAccessor, random, boundingBox);
         this.generateLowerWall(levelAccessor, random, boundingBox);
         this.generateMiddleWall(levelAccessor, random, boundingBox);
         this.generateUpperWall(levelAccessor, random, boundingBox);

         for(int var6 = 0; var6 < 7; ++var6) {
            int var7 = 0;

            while(var7 < 7) {
               if(var7 == 0 && var6 == 3) {
                  var7 = 6;
               }

               int var8 = var6 * 9;
               int var9 = var7 * 9;

               for(int var10 = 0; var10 < 4; ++var10) {
                  for(int var11 = 0; var11 < 4; ++var11) {
                     this.placeBlock(levelAccessor, BASE_LIGHT, var8 + var10, 0, var9 + var11, boundingBox);
                     this.fillColumnDown(levelAccessor, BASE_LIGHT, var8 + var10, -1, var9 + var11, boundingBox);
                  }
               }

               if(var6 != 0 && var6 != 6) {
                  var7 += 6;
               } else {
                  ++var7;
               }
            }
         }

         for(int var6 = 0; var6 < 5; ++var6) {
            this.generateWaterBox(levelAccessor, boundingBox, -1 - var6, 0 + var6 * 2, -1 - var6, -1 - var6, 23, 58 + var6);
            this.generateWaterBox(levelAccessor, boundingBox, 58 + var6, 0 + var6 * 2, -1 - var6, 58 + var6, 23, 58 + var6);
            this.generateWaterBox(levelAccessor, boundingBox, 0 - var6, 0 + var6 * 2, -1 - var6, 57 + var6, 23, -1 - var6);
            this.generateWaterBox(levelAccessor, boundingBox, 0 - var6, 0 + var6 * 2, 58 + var6, 57 + var6, 23, 58 + var6);
         }

         for(OceanMonumentPieces.OceanMonumentPiece var7 : this.childPieces) {
            if(var7.getBoundingBox().intersects(boundingBox)) {
               var7.postProcess(levelAccessor, random, boundingBox, chunkPos);
            }
         }

         return true;
      }

      private void generateWing(boolean var1, int var2, LevelAccessor levelAccessor, Random random, BoundingBox boundingBox) {
         int var6 = 24;
         if(this.chunkIntersects(boundingBox, var2, 0, var2 + 23, 20)) {
            this.generateBox(levelAccessor, boundingBox, var2 + 0, 0, 0, var2 + 24, 0, 20, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(levelAccessor, boundingBox, var2 + 0, 1, 0, var2 + 24, 10, 20);

            for(int var7 = 0; var7 < 4; ++var7) {
               this.generateBox(levelAccessor, boundingBox, var2 + var7, var7 + 1, var7, var2 + var7, var7 + 1, 20, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, var2 + var7 + 7, var7 + 5, var7 + 7, var2 + var7 + 7, var7 + 5, 20, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, var2 + 17 - var7, var7 + 5, var7 + 7, var2 + 17 - var7, var7 + 5, 20, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, var2 + 24 - var7, var7 + 1, var7, var2 + 24 - var7, var7 + 1, 20, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, var2 + var7 + 1, var7 + 1, var7, var2 + 23 - var7, var7 + 1, var7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, var2 + var7 + 8, var7 + 5, var7 + 7, var2 + 16 - var7, var7 + 5, var7 + 7, BASE_LIGHT, BASE_LIGHT, false);
            }

            this.generateBox(levelAccessor, boundingBox, var2 + 4, 4, 4, var2 + 6, 4, 20, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, var2 + 7, 4, 4, var2 + 17, 4, 6, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, var2 + 18, 4, 4, var2 + 20, 4, 20, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, var2 + 11, 8, 11, var2 + 13, 8, 20, BASE_GRAY, BASE_GRAY, false);
            this.placeBlock(levelAccessor, DOT_DECO_DATA, var2 + 12, 9, 12, boundingBox);
            this.placeBlock(levelAccessor, DOT_DECO_DATA, var2 + 12, 9, 15, boundingBox);
            this.placeBlock(levelAccessor, DOT_DECO_DATA, var2 + 12, 9, 18, boundingBox);
            int var7 = var2 + (var1?19:5);
            int var8 = var2 + (var1?5:19);

            for(int var9 = 20; var9 >= 5; var9 -= 3) {
               this.placeBlock(levelAccessor, DOT_DECO_DATA, var7, 5, var9, boundingBox);
            }

            for(int var9 = 19; var9 >= 7; var9 -= 3) {
               this.placeBlock(levelAccessor, DOT_DECO_DATA, var8, 5, var9, boundingBox);
            }

            for(int var9 = 0; var9 < 4; ++var9) {
               int var10 = var1?var2 + 24 - (17 - var9 * 3):var2 + 17 - var9 * 3;
               this.placeBlock(levelAccessor, DOT_DECO_DATA, var10, 5, 5, boundingBox);
            }

            this.placeBlock(levelAccessor, DOT_DECO_DATA, var8, 5, 5, boundingBox);
            this.generateBox(levelAccessor, boundingBox, var2 + 11, 1, 12, var2 + 13, 7, 12, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, var2 + 12, 1, 11, var2 + 12, 7, 13, BASE_GRAY, BASE_GRAY, false);
         }

      }

      private void generateEntranceArchs(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox) {
         if(this.chunkIntersects(boundingBox, 22, 5, 35, 17)) {
            this.generateWaterBox(levelAccessor, boundingBox, 25, 0, 0, 32, 8, 20);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.generateBox(levelAccessor, boundingBox, 24, 2, 5 + var4 * 4, 24, 4, 5 + var4 * 4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, 22, 4, 5 + var4 * 4, 23, 4, 5 + var4 * 4, BASE_LIGHT, BASE_LIGHT, false);
               this.placeBlock(levelAccessor, BASE_LIGHT, 25, 5, 5 + var4 * 4, boundingBox);
               this.placeBlock(levelAccessor, BASE_LIGHT, 26, 6, 5 + var4 * 4, boundingBox);
               this.placeBlock(levelAccessor, LAMP_BLOCK, 26, 5, 5 + var4 * 4, boundingBox);
               this.generateBox(levelAccessor, boundingBox, 33, 2, 5 + var4 * 4, 33, 4, 5 + var4 * 4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, 34, 4, 5 + var4 * 4, 35, 4, 5 + var4 * 4, BASE_LIGHT, BASE_LIGHT, false);
               this.placeBlock(levelAccessor, BASE_LIGHT, 32, 5, 5 + var4 * 4, boundingBox);
               this.placeBlock(levelAccessor, BASE_LIGHT, 31, 6, 5 + var4 * 4, boundingBox);
               this.placeBlock(levelAccessor, LAMP_BLOCK, 31, 5, 5 + var4 * 4, boundingBox);
               this.generateBox(levelAccessor, boundingBox, 27, 6, 5 + var4 * 4, 30, 6, 5 + var4 * 4, BASE_GRAY, BASE_GRAY, false);
            }
         }

      }

      private void generateEntranceWall(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox) {
         if(this.chunkIntersects(boundingBox, 15, 20, 42, 21)) {
            this.generateBox(levelAccessor, boundingBox, 15, 0, 21, 42, 0, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(levelAccessor, boundingBox, 26, 1, 21, 31, 3, 21);
            this.generateBox(levelAccessor, boundingBox, 21, 12, 21, 36, 12, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 17, 11, 21, 40, 11, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 16, 10, 21, 41, 10, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 15, 7, 21, 42, 9, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 16, 6, 21, 41, 6, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 17, 5, 21, 40, 5, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 21, 4, 21, 36, 4, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 22, 3, 21, 26, 3, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 31, 3, 21, 35, 3, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 23, 2, 21, 25, 2, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 32, 2, 21, 34, 2, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 28, 4, 20, 29, 4, 21, BASE_LIGHT, BASE_LIGHT, false);
            this.placeBlock(levelAccessor, BASE_LIGHT, 27, 3, 21, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 30, 3, 21, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 26, 2, 21, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 31, 2, 21, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 25, 1, 21, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 32, 1, 21, boundingBox);

            for(int var4 = 0; var4 < 7; ++var4) {
               this.placeBlock(levelAccessor, BASE_BLACK, 28 - var4, 6 + var4, 21, boundingBox);
               this.placeBlock(levelAccessor, BASE_BLACK, 29 + var4, 6 + var4, 21, boundingBox);
            }

            for(int var4 = 0; var4 < 4; ++var4) {
               this.placeBlock(levelAccessor, BASE_BLACK, 28 - var4, 9 + var4, 21, boundingBox);
               this.placeBlock(levelAccessor, BASE_BLACK, 29 + var4, 9 + var4, 21, boundingBox);
            }

            this.placeBlock(levelAccessor, BASE_BLACK, 28, 12, 21, boundingBox);
            this.placeBlock(levelAccessor, BASE_BLACK, 29, 12, 21, boundingBox);

            for(int var4 = 0; var4 < 3; ++var4) {
               this.placeBlock(levelAccessor, BASE_BLACK, 22 - var4 * 2, 8, 21, boundingBox);
               this.placeBlock(levelAccessor, BASE_BLACK, 22 - var4 * 2, 9, 21, boundingBox);
               this.placeBlock(levelAccessor, BASE_BLACK, 35 + var4 * 2, 8, 21, boundingBox);
               this.placeBlock(levelAccessor, BASE_BLACK, 35 + var4 * 2, 9, 21, boundingBox);
            }

            this.generateWaterBox(levelAccessor, boundingBox, 15, 13, 21, 42, 15, 21);
            this.generateWaterBox(levelAccessor, boundingBox, 15, 1, 21, 15, 6, 21);
            this.generateWaterBox(levelAccessor, boundingBox, 16, 1, 21, 16, 5, 21);
            this.generateWaterBox(levelAccessor, boundingBox, 17, 1, 21, 20, 4, 21);
            this.generateWaterBox(levelAccessor, boundingBox, 21, 1, 21, 21, 3, 21);
            this.generateWaterBox(levelAccessor, boundingBox, 22, 1, 21, 22, 2, 21);
            this.generateWaterBox(levelAccessor, boundingBox, 23, 1, 21, 24, 1, 21);
            this.generateWaterBox(levelAccessor, boundingBox, 42, 1, 21, 42, 6, 21);
            this.generateWaterBox(levelAccessor, boundingBox, 41, 1, 21, 41, 5, 21);
            this.generateWaterBox(levelAccessor, boundingBox, 37, 1, 21, 40, 4, 21);
            this.generateWaterBox(levelAccessor, boundingBox, 36, 1, 21, 36, 3, 21);
            this.generateWaterBox(levelAccessor, boundingBox, 33, 1, 21, 34, 1, 21);
            this.generateWaterBox(levelAccessor, boundingBox, 35, 1, 21, 35, 2, 21);
         }

      }

      private void generateRoofPiece(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox) {
         if(this.chunkIntersects(boundingBox, 21, 21, 36, 36)) {
            this.generateBox(levelAccessor, boundingBox, 21, 0, 22, 36, 0, 36, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(levelAccessor, boundingBox, 21, 1, 22, 36, 23, 36);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.generateBox(levelAccessor, boundingBox, 21 + var4, 13 + var4, 21 + var4, 36 - var4, 13 + var4, 21 + var4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, 21 + var4, 13 + var4, 36 - var4, 36 - var4, 13 + var4, 36 - var4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, 21 + var4, 13 + var4, 22 + var4, 21 + var4, 13 + var4, 35 - var4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, 36 - var4, 13 + var4, 22 + var4, 36 - var4, 13 + var4, 35 - var4, BASE_LIGHT, BASE_LIGHT, false);
            }

            this.generateBox(levelAccessor, boundingBox, 25, 16, 25, 32, 16, 32, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 25, 17, 25, 25, 19, 25, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 32, 17, 25, 32, 19, 25, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 25, 17, 32, 25, 19, 32, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 32, 17, 32, 32, 19, 32, BASE_LIGHT, BASE_LIGHT, false);
            this.placeBlock(levelAccessor, BASE_LIGHT, 26, 20, 26, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 27, 21, 27, boundingBox);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 27, 20, 27, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 26, 20, 31, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 27, 21, 30, boundingBox);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 27, 20, 30, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 31, 20, 31, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 30, 21, 30, boundingBox);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 30, 20, 30, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 31, 20, 26, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 30, 21, 27, boundingBox);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 30, 20, 27, boundingBox);
            this.generateBox(levelAccessor, boundingBox, 28, 21, 27, 29, 21, 27, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 27, 21, 28, 27, 21, 29, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 28, 21, 30, 29, 21, 30, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 30, 21, 28, 30, 21, 29, BASE_GRAY, BASE_GRAY, false);
         }

      }

      private void generateLowerWall(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox) {
         if(this.chunkIntersects(boundingBox, 0, 21, 6, 58)) {
            this.generateBox(levelAccessor, boundingBox, 0, 0, 21, 6, 0, 57, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(levelAccessor, boundingBox, 0, 1, 21, 6, 7, 57);
            this.generateBox(levelAccessor, boundingBox, 4, 4, 21, 6, 4, 53, BASE_GRAY, BASE_GRAY, false);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.generateBox(levelAccessor, boundingBox, var4, var4 + 1, 21, var4, var4 + 1, 57 - var4, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int var4 = 23; var4 < 53; var4 += 3) {
               this.placeBlock(levelAccessor, DOT_DECO_DATA, 5, 5, var4, boundingBox);
            }

            this.placeBlock(levelAccessor, DOT_DECO_DATA, 5, 5, 52, boundingBox);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.generateBox(levelAccessor, boundingBox, var4, var4 + 1, 21, var4, var4 + 1, 57 - var4, BASE_LIGHT, BASE_LIGHT, false);
            }

            this.generateBox(levelAccessor, boundingBox, 4, 1, 52, 6, 3, 52, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 5, 1, 51, 5, 3, 53, BASE_GRAY, BASE_GRAY, false);
         }

         if(this.chunkIntersects(boundingBox, 51, 21, 58, 58)) {
            this.generateBox(levelAccessor, boundingBox, 51, 0, 21, 57, 0, 57, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(levelAccessor, boundingBox, 51, 1, 21, 57, 7, 57);
            this.generateBox(levelAccessor, boundingBox, 51, 4, 21, 53, 4, 53, BASE_GRAY, BASE_GRAY, false);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.generateBox(levelAccessor, boundingBox, 57 - var4, var4 + 1, 21, 57 - var4, var4 + 1, 57 - var4, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int var4 = 23; var4 < 53; var4 += 3) {
               this.placeBlock(levelAccessor, DOT_DECO_DATA, 52, 5, var4, boundingBox);
            }

            this.placeBlock(levelAccessor, DOT_DECO_DATA, 52, 5, 52, boundingBox);
            this.generateBox(levelAccessor, boundingBox, 51, 1, 52, 53, 3, 52, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 52, 1, 51, 52, 3, 53, BASE_GRAY, BASE_GRAY, false);
         }

         if(this.chunkIntersects(boundingBox, 0, 51, 57, 57)) {
            this.generateBox(levelAccessor, boundingBox, 7, 0, 51, 50, 0, 57, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(levelAccessor, boundingBox, 7, 1, 51, 50, 10, 57);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.generateBox(levelAccessor, boundingBox, var4 + 1, var4 + 1, 57 - var4, 56 - var4, var4 + 1, 57 - var4, BASE_LIGHT, BASE_LIGHT, false);
            }
         }

      }

      private void generateMiddleWall(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox) {
         if(this.chunkIntersects(boundingBox, 7, 21, 13, 50)) {
            this.generateBox(levelAccessor, boundingBox, 7, 0, 21, 13, 0, 50, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(levelAccessor, boundingBox, 7, 1, 21, 13, 10, 50);
            this.generateBox(levelAccessor, boundingBox, 11, 8, 21, 13, 8, 53, BASE_GRAY, BASE_GRAY, false);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.generateBox(levelAccessor, boundingBox, var4 + 7, var4 + 5, 21, var4 + 7, var4 + 5, 54, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int var4 = 21; var4 <= 45; var4 += 3) {
               this.placeBlock(levelAccessor, DOT_DECO_DATA, 12, 9, var4, boundingBox);
            }
         }

         if(this.chunkIntersects(boundingBox, 44, 21, 50, 54)) {
            this.generateBox(levelAccessor, boundingBox, 44, 0, 21, 50, 0, 50, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(levelAccessor, boundingBox, 44, 1, 21, 50, 10, 50);
            this.generateBox(levelAccessor, boundingBox, 44, 8, 21, 46, 8, 53, BASE_GRAY, BASE_GRAY, false);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.generateBox(levelAccessor, boundingBox, 50 - var4, var4 + 5, 21, 50 - var4, var4 + 5, 54, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int var4 = 21; var4 <= 45; var4 += 3) {
               this.placeBlock(levelAccessor, DOT_DECO_DATA, 45, 9, var4, boundingBox);
            }
         }

         if(this.chunkIntersects(boundingBox, 8, 44, 49, 54)) {
            this.generateBox(levelAccessor, boundingBox, 14, 0, 44, 43, 0, 50, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(levelAccessor, boundingBox, 14, 1, 44, 43, 10, 50);

            for(int var4 = 12; var4 <= 45; var4 += 3) {
               this.placeBlock(levelAccessor, DOT_DECO_DATA, var4, 9, 45, boundingBox);
               this.placeBlock(levelAccessor, DOT_DECO_DATA, var4, 9, 52, boundingBox);
               if(var4 == 12 || var4 == 18 || var4 == 24 || var4 == 33 || var4 == 39 || var4 == 45) {
                  this.placeBlock(levelAccessor, DOT_DECO_DATA, var4, 9, 47, boundingBox);
                  this.placeBlock(levelAccessor, DOT_DECO_DATA, var4, 9, 50, boundingBox);
                  this.placeBlock(levelAccessor, DOT_DECO_DATA, var4, 10, 45, boundingBox);
                  this.placeBlock(levelAccessor, DOT_DECO_DATA, var4, 10, 46, boundingBox);
                  this.placeBlock(levelAccessor, DOT_DECO_DATA, var4, 10, 51, boundingBox);
                  this.placeBlock(levelAccessor, DOT_DECO_DATA, var4, 10, 52, boundingBox);
                  this.placeBlock(levelAccessor, DOT_DECO_DATA, var4, 11, 47, boundingBox);
                  this.placeBlock(levelAccessor, DOT_DECO_DATA, var4, 11, 50, boundingBox);
                  this.placeBlock(levelAccessor, DOT_DECO_DATA, var4, 12, 48, boundingBox);
                  this.placeBlock(levelAccessor, DOT_DECO_DATA, var4, 12, 49, boundingBox);
               }
            }

            for(int var4 = 0; var4 < 3; ++var4) {
               this.generateBox(levelAccessor, boundingBox, 8 + var4, 5 + var4, 54, 49 - var4, 5 + var4, 54, BASE_GRAY, BASE_GRAY, false);
            }

            this.generateBox(levelAccessor, boundingBox, 11, 8, 54, 46, 8, 54, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 14, 8, 44, 43, 8, 53, BASE_GRAY, BASE_GRAY, false);
         }

      }

      private void generateUpperWall(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox) {
         if(this.chunkIntersects(boundingBox, 14, 21, 20, 43)) {
            this.generateBox(levelAccessor, boundingBox, 14, 0, 21, 20, 0, 43, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(levelAccessor, boundingBox, 14, 1, 22, 20, 14, 43);
            this.generateBox(levelAccessor, boundingBox, 18, 12, 22, 20, 12, 39, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 18, 12, 21, 20, 12, 21, BASE_LIGHT, BASE_LIGHT, false);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.generateBox(levelAccessor, boundingBox, var4 + 14, var4 + 9, 21, var4 + 14, var4 + 9, 43 - var4, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int var4 = 23; var4 <= 39; var4 += 3) {
               this.placeBlock(levelAccessor, DOT_DECO_DATA, 19, 13, var4, boundingBox);
            }
         }

         if(this.chunkIntersects(boundingBox, 37, 21, 43, 43)) {
            this.generateBox(levelAccessor, boundingBox, 37, 0, 21, 43, 0, 43, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(levelAccessor, boundingBox, 37, 1, 22, 43, 14, 43);
            this.generateBox(levelAccessor, boundingBox, 37, 12, 22, 39, 12, 39, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 37, 12, 21, 39, 12, 21, BASE_LIGHT, BASE_LIGHT, false);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.generateBox(levelAccessor, boundingBox, 43 - var4, var4 + 9, 21, 43 - var4, var4 + 9, 43 - var4, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int var4 = 23; var4 <= 39; var4 += 3) {
               this.placeBlock(levelAccessor, DOT_DECO_DATA, 38, 13, var4, boundingBox);
            }
         }

         if(this.chunkIntersects(boundingBox, 15, 37, 42, 43)) {
            this.generateBox(levelAccessor, boundingBox, 21, 0, 37, 36, 0, 43, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(levelAccessor, boundingBox, 21, 1, 37, 36, 14, 43);
            this.generateBox(levelAccessor, boundingBox, 21, 12, 37, 36, 12, 39, BASE_GRAY, BASE_GRAY, false);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.generateBox(levelAccessor, boundingBox, 15 + var4, var4 + 9, 43 - var4, 42 - var4, var4 + 9, 43 - var4, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int var4 = 21; var4 <= 36; var4 += 3) {
               this.placeBlock(levelAccessor, DOT_DECO_DATA, var4, 13, 38, boundingBox);
            }
         }

      }
   }

   interface MonumentRoomFitter {
      boolean fits(OceanMonumentPieces.RoomDefinition var1);

      OceanMonumentPieces.OceanMonumentPiece create(Direction var1, OceanMonumentPieces.RoomDefinition var2, Random var3);
   }

   public static class OceanMonumentCoreRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentCoreRoom(Direction direction, OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition) {
         super(StructurePieceType.OCEAN_MONUMENT_CORE_ROOM, 1, direction, oceanMonumentPieces$RoomDefinition, 2, 2, 2);
      }

      public OceanMonumentCoreRoom(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.OCEAN_MONUMENT_CORE_ROOM, compoundTag);
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBoxOnFillOnly(levelAccessor, boundingBox, 1, 8, 0, 14, 8, 14, BASE_GRAY);
         int var5 = 7;
         BlockState var6 = BASE_LIGHT;
         this.generateBox(levelAccessor, boundingBox, 0, 7, 0, 0, 7, 15, var6, var6, false);
         this.generateBox(levelAccessor, boundingBox, 15, 7, 0, 15, 7, 15, var6, var6, false);
         this.generateBox(levelAccessor, boundingBox, 1, 7, 0, 15, 7, 0, var6, var6, false);
         this.generateBox(levelAccessor, boundingBox, 1, 7, 15, 14, 7, 15, var6, var6, false);

         for(var5 = 1; var5 <= 6; ++var5) {
            var6 = BASE_LIGHT;
            if(var5 == 2 || var5 == 6) {
               var6 = BASE_GRAY;
            }

            for(int var7 = 0; var7 <= 15; var7 += 15) {
               this.generateBox(levelAccessor, boundingBox, var7, var5, 0, var7, var5, 1, var6, var6, false);
               this.generateBox(levelAccessor, boundingBox, var7, var5, 6, var7, var5, 9, var6, var6, false);
               this.generateBox(levelAccessor, boundingBox, var7, var5, 14, var7, var5, 15, var6, var6, false);
            }

            this.generateBox(levelAccessor, boundingBox, 1, var5, 0, 1, var5, 0, var6, var6, false);
            this.generateBox(levelAccessor, boundingBox, 6, var5, 0, 9, var5, 0, var6, var6, false);
            this.generateBox(levelAccessor, boundingBox, 14, var5, 0, 14, var5, 0, var6, var6, false);
            this.generateBox(levelAccessor, boundingBox, 1, var5, 15, 14, var5, 15, var6, var6, false);
         }

         this.generateBox(levelAccessor, boundingBox, 6, 3, 6, 9, 6, 9, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(levelAccessor, boundingBox, 7, 4, 7, 8, 5, 8, Blocks.GOLD_BLOCK.defaultBlockState(), Blocks.GOLD_BLOCK.defaultBlockState(), false);

         for(var5 = 3; var5 <= 6; var5 += 3) {
            for(int var6 = 6; var6 <= 9; var6 += 3) {
               this.placeBlock(levelAccessor, LAMP_BLOCK, var6, var5, 6, boundingBox);
               this.placeBlock(levelAccessor, LAMP_BLOCK, var6, var5, 9, boundingBox);
            }
         }

         this.generateBox(levelAccessor, boundingBox, 5, 1, 6, 5, 2, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 5, 1, 9, 5, 2, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 10, 1, 6, 10, 2, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 10, 1, 9, 10, 2, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 6, 1, 5, 6, 2, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 9, 1, 5, 9, 2, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 6, 1, 10, 6, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 9, 1, 10, 9, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 5, 2, 5, 5, 6, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 5, 2, 10, 5, 6, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 10, 2, 5, 10, 6, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 10, 2, 10, 10, 6, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 5, 7, 1, 5, 7, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 10, 7, 1, 10, 7, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 5, 7, 9, 5, 7, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 10, 7, 9, 10, 7, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 7, 5, 6, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 7, 10, 6, 7, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 9, 7, 5, 14, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 9, 7, 10, 14, 7, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 2, 1, 2, 2, 1, 3, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 3, 1, 2, 3, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 13, 1, 2, 13, 1, 3, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 12, 1, 2, 12, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 2, 1, 12, 2, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 3, 1, 13, 3, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 13, 1, 12, 13, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 12, 1, 13, 12, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
         return true;
      }
   }

   public static class OceanMonumentDoubleXRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentDoubleXRoom(Direction direction, OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, 1, direction, oceanMonumentPieces$RoomDefinition, 2, 1, 1);
      }

      public OceanMonumentDoubleXRoom(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, compoundTag);
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         OceanMonumentPieces.RoomDefinition var5 = this.roomDefinition.connections[Direction.EAST.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition var6 = this.roomDefinition;
         if(this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(levelAccessor, boundingBox, 8, 0, var5.hasOpening[Direction.DOWN.get3DDataValue()]);
            this.generateDefaultFloor(levelAccessor, boundingBox, 0, 0, var6.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if(var6.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(levelAccessor, boundingBox, 1, 4, 1, 7, 4, 6, BASE_GRAY);
         }

         if(var5.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(levelAccessor, boundingBox, 8, 4, 1, 14, 4, 6, BASE_GRAY);
         }

         this.generateBox(levelAccessor, boundingBox, 0, 3, 0, 0, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 15, 3, 0, 15, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 3, 0, 15, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 3, 7, 14, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 0, 2, 7, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(levelAccessor, boundingBox, 15, 2, 0, 15, 2, 7, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(levelAccessor, boundingBox, 1, 2, 0, 15, 2, 0, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(levelAccessor, boundingBox, 1, 2, 7, 14, 2, 7, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(levelAccessor, boundingBox, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 15, 1, 0, 15, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 1, 0, 15, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 1, 7, 14, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 5, 1, 0, 10, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 6, 2, 0, 9, 2, 3, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(levelAccessor, boundingBox, 5, 3, 0, 10, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.placeBlock(levelAccessor, LAMP_BLOCK, 6, 2, 3, boundingBox);
         this.placeBlock(levelAccessor, LAMP_BLOCK, 9, 2, 3, boundingBox);
         if(var6.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 3, 1, 0, 4, 2, 0);
         }

         if(var6.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 3, 1, 7, 4, 2, 7);
         }

         if(var6.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 0, 1, 3, 0, 2, 4);
         }

         if(var5.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 11, 1, 0, 12, 2, 0);
         }

         if(var5.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 11, 1, 7, 12, 2, 7);
         }

         if(var5.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 15, 1, 3, 15, 2, 4);
         }

         return true;
      }
   }

   public static class OceanMonumentDoubleXYRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentDoubleXYRoom(Direction direction, OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_XY_ROOM, 1, direction, oceanMonumentPieces$RoomDefinition, 2, 2, 1);
      }

      public OceanMonumentDoubleXYRoom(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_XY_ROOM, compoundTag);
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         OceanMonumentPieces.RoomDefinition var5 = this.roomDefinition.connections[Direction.EAST.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition var6 = this.roomDefinition;
         OceanMonumentPieces.RoomDefinition var7 = var6.connections[Direction.UP.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition var8 = var5.connections[Direction.UP.get3DDataValue()];
         if(this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(levelAccessor, boundingBox, 8, 0, var5.hasOpening[Direction.DOWN.get3DDataValue()]);
            this.generateDefaultFloor(levelAccessor, boundingBox, 0, 0, var6.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if(var7.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(levelAccessor, boundingBox, 1, 8, 1, 7, 8, 6, BASE_GRAY);
         }

         if(var8.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(levelAccessor, boundingBox, 8, 8, 1, 14, 8, 6, BASE_GRAY);
         }

         for(int var9 = 1; var9 <= 7; ++var9) {
            BlockState var10 = BASE_LIGHT;
            if(var9 == 2 || var9 == 6) {
               var10 = BASE_GRAY;
            }

            this.generateBox(levelAccessor, boundingBox, 0, var9, 0, 0, var9, 7, var10, var10, false);
            this.generateBox(levelAccessor, boundingBox, 15, var9, 0, 15, var9, 7, var10, var10, false);
            this.generateBox(levelAccessor, boundingBox, 1, var9, 0, 15, var9, 0, var10, var10, false);
            this.generateBox(levelAccessor, boundingBox, 1, var9, 7, 14, var9, 7, var10, var10, false);
         }

         this.generateBox(levelAccessor, boundingBox, 2, 1, 3, 2, 7, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 3, 1, 2, 4, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 3, 1, 5, 4, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 13, 1, 3, 13, 7, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 11, 1, 2, 12, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 11, 1, 5, 12, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 5, 1, 3, 5, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 10, 1, 3, 10, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 5, 7, 2, 10, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 5, 5, 2, 5, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 10, 5, 2, 10, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 5, 5, 5, 5, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 10, 5, 5, 10, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.placeBlock(levelAccessor, BASE_LIGHT, 6, 6, 2, boundingBox);
         this.placeBlock(levelAccessor, BASE_LIGHT, 9, 6, 2, boundingBox);
         this.placeBlock(levelAccessor, BASE_LIGHT, 6, 6, 5, boundingBox);
         this.placeBlock(levelAccessor, BASE_LIGHT, 9, 6, 5, boundingBox);
         this.generateBox(levelAccessor, boundingBox, 5, 4, 3, 6, 4, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 9, 4, 3, 10, 4, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.placeBlock(levelAccessor, LAMP_BLOCK, 5, 4, 2, boundingBox);
         this.placeBlock(levelAccessor, LAMP_BLOCK, 5, 4, 5, boundingBox);
         this.placeBlock(levelAccessor, LAMP_BLOCK, 10, 4, 2, boundingBox);
         this.placeBlock(levelAccessor, LAMP_BLOCK, 10, 4, 5, boundingBox);
         if(var6.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 3, 1, 0, 4, 2, 0);
         }

         if(var6.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 3, 1, 7, 4, 2, 7);
         }

         if(var6.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 0, 1, 3, 0, 2, 4);
         }

         if(var5.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 11, 1, 0, 12, 2, 0);
         }

         if(var5.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 11, 1, 7, 12, 2, 7);
         }

         if(var5.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 15, 1, 3, 15, 2, 4);
         }

         if(var7.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 3, 5, 0, 4, 6, 0);
         }

         if(var7.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 3, 5, 7, 4, 6, 7);
         }

         if(var7.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 0, 5, 3, 0, 6, 4);
         }

         if(var8.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 11, 5, 0, 12, 6, 0);
         }

         if(var8.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 11, 5, 7, 12, 6, 7);
         }

         if(var8.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 15, 5, 3, 15, 6, 4);
         }

         return true;
      }
   }

   public static class OceanMonumentDoubleYRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentDoubleYRoom(Direction direction, OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, 1, direction, oceanMonumentPieces$RoomDefinition, 1, 2, 1);
      }

      public OceanMonumentDoubleYRoom(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, compoundTag);
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         if(this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(levelAccessor, boundingBox, 0, 0, this.roomDefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         OceanMonumentPieces.RoomDefinition var5 = this.roomDefinition.connections[Direction.UP.get3DDataValue()];
         if(var5.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(levelAccessor, boundingBox, 1, 8, 1, 6, 8, 6, BASE_GRAY);
         }

         this.generateBox(levelAccessor, boundingBox, 0, 4, 0, 0, 4, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 7, 4, 0, 7, 4, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 4, 0, 6, 4, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 4, 7, 6, 4, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 2, 4, 1, 2, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 4, 2, 1, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 5, 4, 1, 5, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 6, 4, 2, 6, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 2, 4, 5, 2, 4, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 4, 5, 1, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 5, 4, 5, 5, 4, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 6, 4, 5, 6, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
         OceanMonumentPieces.RoomDefinition var6 = this.roomDefinition;

         for(int var7 = 1; var7 <= 5; var7 += 4) {
            int var8 = 0;
            if(var6.hasOpening[Direction.SOUTH.get3DDataValue()]) {
               this.generateBox(levelAccessor, boundingBox, 2, var7, var8, 2, var7 + 2, var8, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, 5, var7, var8, 5, var7 + 2, var8, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, 3, var7 + 2, var8, 4, var7 + 2, var8, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(levelAccessor, boundingBox, 0, var7, var8, 7, var7 + 2, var8, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, 0, var7 + 1, var8, 7, var7 + 1, var8, BASE_GRAY, BASE_GRAY, false);
            }

            var8 = 7;
            if(var6.hasOpening[Direction.NORTH.get3DDataValue()]) {
               this.generateBox(levelAccessor, boundingBox, 2, var7, var8, 2, var7 + 2, var8, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, 5, var7, var8, 5, var7 + 2, var8, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, 3, var7 + 2, var8, 4, var7 + 2, var8, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(levelAccessor, boundingBox, 0, var7, var8, 7, var7 + 2, var8, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, 0, var7 + 1, var8, 7, var7 + 1, var8, BASE_GRAY, BASE_GRAY, false);
            }

            int var9 = 0;
            if(var6.hasOpening[Direction.WEST.get3DDataValue()]) {
               this.generateBox(levelAccessor, boundingBox, var9, var7, 2, var9, var7 + 2, 2, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, var9, var7, 5, var9, var7 + 2, 5, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, var9, var7 + 2, 3, var9, var7 + 2, 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(levelAccessor, boundingBox, var9, var7, 0, var9, var7 + 2, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, var9, var7 + 1, 0, var9, var7 + 1, 7, BASE_GRAY, BASE_GRAY, false);
            }

            var9 = 7;
            if(var6.hasOpening[Direction.EAST.get3DDataValue()]) {
               this.generateBox(levelAccessor, boundingBox, var9, var7, 2, var9, var7 + 2, 2, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, var9, var7, 5, var9, var7 + 2, 5, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, var9, var7 + 2, 3, var9, var7 + 2, 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(levelAccessor, boundingBox, var9, var7, 0, var9, var7 + 2, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, var9, var7 + 1, 0, var9, var7 + 1, 7, BASE_GRAY, BASE_GRAY, false);
            }

            var6 = var5;
         }

         return true;
      }
   }

   public static class OceanMonumentDoubleYZRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentDoubleYZRoom(Direction direction, OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_YZ_ROOM, 1, direction, oceanMonumentPieces$RoomDefinition, 1, 2, 2);
      }

      public OceanMonumentDoubleYZRoom(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_YZ_ROOM, compoundTag);
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         OceanMonumentPieces.RoomDefinition var5 = this.roomDefinition.connections[Direction.NORTH.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition var6 = this.roomDefinition;
         OceanMonumentPieces.RoomDefinition var7 = var5.connections[Direction.UP.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition var8 = var6.connections[Direction.UP.get3DDataValue()];
         if(this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(levelAccessor, boundingBox, 0, 8, var5.hasOpening[Direction.DOWN.get3DDataValue()]);
            this.generateDefaultFloor(levelAccessor, boundingBox, 0, 0, var6.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if(var8.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(levelAccessor, boundingBox, 1, 8, 1, 6, 8, 7, BASE_GRAY);
         }

         if(var7.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(levelAccessor, boundingBox, 1, 8, 8, 6, 8, 14, BASE_GRAY);
         }

         for(int var9 = 1; var9 <= 7; ++var9) {
            BlockState var10 = BASE_LIGHT;
            if(var9 == 2 || var9 == 6) {
               var10 = BASE_GRAY;
            }

            this.generateBox(levelAccessor, boundingBox, 0, var9, 0, 0, var9, 15, var10, var10, false);
            this.generateBox(levelAccessor, boundingBox, 7, var9, 0, 7, var9, 15, var10, var10, false);
            this.generateBox(levelAccessor, boundingBox, 1, var9, 0, 6, var9, 0, var10, var10, false);
            this.generateBox(levelAccessor, boundingBox, 1, var9, 15, 6, var9, 15, var10, var10, false);
         }

         for(int var9 = 1; var9 <= 7; ++var9) {
            BlockState var10 = BASE_BLACK;
            if(var9 == 2 || var9 == 6) {
               var10 = LAMP_BLOCK;
            }

            this.generateBox(levelAccessor, boundingBox, 3, var9, 7, 4, var9, 8, var10, var10, false);
         }

         if(var6.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 3, 1, 0, 4, 2, 0);
         }

         if(var6.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 7, 1, 3, 7, 2, 4);
         }

         if(var6.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 0, 1, 3, 0, 2, 4);
         }

         if(var5.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 3, 1, 15, 4, 2, 15);
         }

         if(var5.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 0, 1, 11, 0, 2, 12);
         }

         if(var5.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 7, 1, 11, 7, 2, 12);
         }

         if(var8.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 3, 5, 0, 4, 6, 0);
         }

         if(var8.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 7, 5, 3, 7, 6, 4);
            this.generateBox(levelAccessor, boundingBox, 5, 4, 2, 6, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 6, 1, 2, 6, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 6, 1, 5, 6, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
         }

         if(var8.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 0, 5, 3, 0, 6, 4);
            this.generateBox(levelAccessor, boundingBox, 1, 4, 2, 2, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 1, 1, 2, 1, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 1, 1, 5, 1, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
         }

         if(var7.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 3, 5, 15, 4, 6, 15);
         }

         if(var7.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 0, 5, 11, 0, 6, 12);
            this.generateBox(levelAccessor, boundingBox, 1, 4, 10, 2, 4, 13, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 1, 1, 10, 1, 3, 10, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 1, 1, 13, 1, 3, 13, BASE_LIGHT, BASE_LIGHT, false);
         }

         if(var7.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 7, 5, 11, 7, 6, 12);
            this.generateBox(levelAccessor, boundingBox, 5, 4, 10, 6, 4, 13, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 6, 1, 10, 6, 3, 10, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 6, 1, 13, 6, 3, 13, BASE_LIGHT, BASE_LIGHT, false);
         }

         return true;
      }
   }

   public static class OceanMonumentDoubleZRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentDoubleZRoom(Direction direction, OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, 1, direction, oceanMonumentPieces$RoomDefinition, 1, 1, 2);
      }

      public OceanMonumentDoubleZRoom(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, compoundTag);
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         OceanMonumentPieces.RoomDefinition var5 = this.roomDefinition.connections[Direction.NORTH.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition var6 = this.roomDefinition;
         if(this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(levelAccessor, boundingBox, 0, 8, var5.hasOpening[Direction.DOWN.get3DDataValue()]);
            this.generateDefaultFloor(levelAccessor, boundingBox, 0, 0, var6.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if(var6.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(levelAccessor, boundingBox, 1, 4, 1, 6, 4, 7, BASE_GRAY);
         }

         if(var5.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(levelAccessor, boundingBox, 1, 4, 8, 6, 4, 14, BASE_GRAY);
         }

         this.generateBox(levelAccessor, boundingBox, 0, 3, 0, 0, 3, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 7, 3, 0, 7, 3, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 3, 0, 7, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 3, 15, 6, 3, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 0, 2, 15, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(levelAccessor, boundingBox, 7, 2, 0, 7, 2, 15, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(levelAccessor, boundingBox, 1, 2, 0, 7, 2, 0, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(levelAccessor, boundingBox, 1, 2, 15, 6, 2, 15, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(levelAccessor, boundingBox, 0, 1, 0, 0, 1, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 7, 1, 0, 7, 1, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 1, 0, 7, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 1, 15, 6, 1, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 1, 1, 1, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 6, 1, 1, 6, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 3, 1, 1, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 6, 3, 1, 6, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 1, 13, 1, 1, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 6, 1, 13, 6, 1, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 3, 13, 1, 3, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 6, 3, 13, 6, 3, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 2, 1, 6, 2, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 5, 1, 6, 5, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 2, 1, 9, 2, 3, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 5, 1, 9, 5, 3, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 3, 2, 6, 4, 2, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 3, 2, 9, 4, 2, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 2, 2, 7, 2, 2, 8, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 5, 2, 7, 5, 2, 8, BASE_LIGHT, BASE_LIGHT, false);
         this.placeBlock(levelAccessor, LAMP_BLOCK, 2, 2, 5, boundingBox);
         this.placeBlock(levelAccessor, LAMP_BLOCK, 5, 2, 5, boundingBox);
         this.placeBlock(levelAccessor, LAMP_BLOCK, 2, 2, 10, boundingBox);
         this.placeBlock(levelAccessor, LAMP_BLOCK, 5, 2, 10, boundingBox);
         this.placeBlock(levelAccessor, BASE_LIGHT, 2, 3, 5, boundingBox);
         this.placeBlock(levelAccessor, BASE_LIGHT, 5, 3, 5, boundingBox);
         this.placeBlock(levelAccessor, BASE_LIGHT, 2, 3, 10, boundingBox);
         this.placeBlock(levelAccessor, BASE_LIGHT, 5, 3, 10, boundingBox);
         if(var6.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 3, 1, 0, 4, 2, 0);
         }

         if(var6.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 7, 1, 3, 7, 2, 4);
         }

         if(var6.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 0, 1, 3, 0, 2, 4);
         }

         if(var5.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 3, 1, 15, 4, 2, 15);
         }

         if(var5.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 0, 1, 11, 0, 2, 12);
         }

         if(var5.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 7, 1, 11, 7, 2, 12);
         }

         return true;
      }
   }

   public static class OceanMonumentEntryRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentEntryRoom(Direction direction, OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition) {
         super(StructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, 1, direction, oceanMonumentPieces$RoomDefinition, 1, 1, 1);
      }

      public OceanMonumentEntryRoom(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, compoundTag);
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 0, 3, 0, 2, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 5, 3, 0, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 1, 2, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 6, 2, 0, 7, 2, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 7, 1, 0, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 0, 1, 7, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 1, 0, 2, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 5, 1, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         if(this.roomDefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 3, 1, 7, 4, 2, 7);
         }

         if(this.roomDefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 0, 1, 3, 1, 2, 4);
         }

         if(this.roomDefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 6, 1, 3, 7, 2, 4);
         }

         return true;
      }
   }

   public static class OceanMonumentPenthouse extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentPenthouse(Direction direction, BoundingBox boundingBox) {
         super(StructurePieceType.OCEAN_MONUMENT_PENTHOUSE, direction, boundingBox);
      }

      public OceanMonumentPenthouse(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.OCEAN_MONUMENT_PENTHOUSE, compoundTag);
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.generateBox(levelAccessor, boundingBox, 2, -1, 2, 11, -1, 11, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 0, -1, 0, 1, -1, 11, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(levelAccessor, boundingBox, 12, -1, 0, 13, -1, 11, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(levelAccessor, boundingBox, 2, -1, 0, 11, -1, 1, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(levelAccessor, boundingBox, 2, -1, 12, 11, -1, 13, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 0, 0, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 13, 0, 0, 13, 0, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 0, 0, 12, 0, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 0, 13, 12, 0, 13, BASE_LIGHT, BASE_LIGHT, false);

         for(int var5 = 2; var5 <= 11; var5 += 3) {
            this.placeBlock(levelAccessor, LAMP_BLOCK, 0, 0, var5, boundingBox);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 13, 0, var5, boundingBox);
            this.placeBlock(levelAccessor, LAMP_BLOCK, var5, 0, 0, boundingBox);
         }

         this.generateBox(levelAccessor, boundingBox, 2, 0, 3, 4, 0, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 9, 0, 3, 11, 0, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 4, 0, 9, 9, 0, 11, BASE_LIGHT, BASE_LIGHT, false);
         this.placeBlock(levelAccessor, BASE_LIGHT, 5, 0, 8, boundingBox);
         this.placeBlock(levelAccessor, BASE_LIGHT, 8, 0, 8, boundingBox);
         this.placeBlock(levelAccessor, BASE_LIGHT, 10, 0, 10, boundingBox);
         this.placeBlock(levelAccessor, BASE_LIGHT, 3, 0, 10, boundingBox);
         this.generateBox(levelAccessor, boundingBox, 3, 0, 3, 3, 0, 7, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(levelAccessor, boundingBox, 10, 0, 3, 10, 0, 7, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(levelAccessor, boundingBox, 6, 0, 10, 7, 0, 10, BASE_BLACK, BASE_BLACK, false);
         int var5 = 3;

         for(int var6 = 0; var6 < 2; ++var6) {
            for(int var7 = 2; var7 <= 8; var7 += 3) {
               this.generateBox(levelAccessor, boundingBox, var5, 0, var7, var5, 2, var7, BASE_LIGHT, BASE_LIGHT, false);
            }

            var5 = 10;
         }

         this.generateBox(levelAccessor, boundingBox, 5, 0, 10, 5, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 8, 0, 10, 8, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 6, -1, 7, 7, -1, 8, BASE_BLACK, BASE_BLACK, false);
         this.generateWaterBox(levelAccessor, boundingBox, 6, -1, 3, 7, -1, 4);
         this.spawnElder(levelAccessor, boundingBox, 6, 1, 6);
         return true;
      }
   }

   public abstract static class OceanMonumentPiece extends StructurePiece {
      protected static final BlockState BASE_GRAY = Blocks.PRISMARINE.defaultBlockState();
      protected static final BlockState BASE_LIGHT = Blocks.PRISMARINE_BRICKS.defaultBlockState();
      protected static final BlockState BASE_BLACK = Blocks.DARK_PRISMARINE.defaultBlockState();
      protected static final BlockState DOT_DECO_DATA = BASE_LIGHT;
      protected static final BlockState LAMP_BLOCK = Blocks.SEA_LANTERN.defaultBlockState();
      protected static final BlockState FILL_BLOCK = Blocks.WATER.defaultBlockState();
      protected static final Set FILL_KEEP = ImmutableSet.builder().add(Blocks.ICE).add(Blocks.PACKED_ICE).add(Blocks.BLUE_ICE).add(FILL_BLOCK.getBlock()).build();
      protected static final int GRIDROOM_SOURCE_INDEX = getRoomIndex(2, 0, 0);
      protected static final int GRIDROOM_TOP_CONNECT_INDEX = getRoomIndex(2, 2, 0);
      protected static final int GRIDROOM_LEFTWING_CONNECT_INDEX = getRoomIndex(0, 1, 0);
      protected static final int GRIDROOM_RIGHTWING_CONNECT_INDEX = getRoomIndex(4, 1, 0);
      protected OceanMonumentPieces.RoomDefinition roomDefinition;

      protected static final int getRoomIndex(int var0, int var1, int var2) {
         return var1 * 25 + var2 * 5 + var0;
      }

      public OceanMonumentPiece(StructurePieceType structurePieceType, int var2) {
         super(structurePieceType, var2);
      }

      public OceanMonumentPiece(StructurePieceType structurePieceType, Direction orientation, BoundingBox boundingBox) {
         super(structurePieceType, 1);
         this.setOrientation(orientation);
         this.boundingBox = boundingBox;
      }

      protected OceanMonumentPiece(StructurePieceType structurePieceType, int var2, Direction orientation, OceanMonumentPieces.RoomDefinition roomDefinition, int var5, int var6, int var7) {
         super(structurePieceType, var2);
         this.setOrientation(orientation);
         this.roomDefinition = roomDefinition;
         int var8 = roomDefinition.index;
         int var9 = var8 % 5;
         int var10 = var8 / 5 % 5;
         int var11 = var8 / 25;
         if(orientation != Direction.NORTH && orientation != Direction.SOUTH) {
            this.boundingBox = new BoundingBox(0, 0, 0, var7 * 8 - 1, var6 * 4 - 1, var5 * 8 - 1);
         } else {
            this.boundingBox = new BoundingBox(0, 0, 0, var5 * 8 - 1, var6 * 4 - 1, var7 * 8 - 1);
         }

         switch(orientation) {
         case NORTH:
            this.boundingBox.move(var9 * 8, var11 * 4, -(var10 + var7) * 8 + 1);
            break;
         case SOUTH:
            this.boundingBox.move(var9 * 8, var11 * 4, var10 * 8);
            break;
         case WEST:
            this.boundingBox.move(-(var10 + var7) * 8 + 1, var11 * 4, var9 * 8);
            break;
         default:
            this.boundingBox.move(var10 * 8, var11 * 4, var9 * 8);
         }

      }

      public OceanMonumentPiece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
         super(structurePieceType, compoundTag);
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
      }

      protected void generateWaterBox(LevelAccessor levelAccessor, BoundingBox boundingBox, int var3, int var4, int var5, int var6, int var7, int var8) {
         for(int var9 = var4; var9 <= var7; ++var9) {
            for(int var10 = var3; var10 <= var6; ++var10) {
               for(int var11 = var5; var11 <= var8; ++var11) {
                  BlockState var12 = this.getBlock(levelAccessor, var10, var9, var11, boundingBox);
                  if(!FILL_KEEP.contains(var12.getBlock())) {
                     if(this.getWorldY(var9) >= levelAccessor.getSeaLevel() && var12 != FILL_BLOCK) {
                        this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), var10, var9, var11, boundingBox);
                     } else {
                        this.placeBlock(levelAccessor, FILL_BLOCK, var10, var9, var11, boundingBox);
                     }
                  }
               }
            }
         }

      }

      protected void generateDefaultFloor(LevelAccessor levelAccessor, BoundingBox boundingBox, int var3, int var4, boolean var5) {
         if(var5) {
            this.generateBox(levelAccessor, boundingBox, var3 + 0, 0, var4 + 0, var3 + 2, 0, var4 + 8 - 1, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, var3 + 5, 0, var4 + 0, var3 + 8 - 1, 0, var4 + 8 - 1, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, var3 + 3, 0, var4 + 0, var3 + 4, 0, var4 + 2, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, var3 + 3, 0, var4 + 5, var3 + 4, 0, var4 + 8 - 1, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, var3 + 3, 0, var4 + 2, var3 + 4, 0, var4 + 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, var3 + 3, 0, var4 + 5, var3 + 4, 0, var4 + 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, var3 + 2, 0, var4 + 3, var3 + 2, 0, var4 + 4, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, var3 + 5, 0, var4 + 3, var3 + 5, 0, var4 + 4, BASE_LIGHT, BASE_LIGHT, false);
         } else {
            this.generateBox(levelAccessor, boundingBox, var3 + 0, 0, var4 + 0, var3 + 8 - 1, 0, var4 + 8 - 1, BASE_GRAY, BASE_GRAY, false);
         }

      }

      protected void generateBoxOnFillOnly(LevelAccessor levelAccessor, BoundingBox boundingBox, int var3, int var4, int var5, int var6, int var7, int var8, BlockState blockState) {
         for(int var10 = var4; var10 <= var7; ++var10) {
            for(int var11 = var3; var11 <= var6; ++var11) {
               for(int var12 = var5; var12 <= var8; ++var12) {
                  if(this.getBlock(levelAccessor, var11, var10, var12, boundingBox) == FILL_BLOCK) {
                     this.placeBlock(levelAccessor, blockState, var11, var10, var12, boundingBox);
                  }
               }
            }
         }

      }

      protected boolean chunkIntersects(BoundingBox boundingBox, int var2, int var3, int var4, int var5) {
         int var6 = this.getWorldX(var2, var3);
         int var7 = this.getWorldZ(var2, var3);
         int var8 = this.getWorldX(var4, var5);
         int var9 = this.getWorldZ(var4, var5);
         return boundingBox.intersects(Math.min(var6, var8), Math.min(var7, var9), Math.max(var6, var8), Math.max(var7, var9));
      }

      protected boolean spawnElder(LevelAccessor levelAccessor, BoundingBox boundingBox, int var3, int var4, int var5) {
         int var6 = this.getWorldX(var3, var5);
         int var7 = this.getWorldY(var4);
         int var8 = this.getWorldZ(var3, var5);
         if(boundingBox.isInside(new BlockPos(var6, var7, var8))) {
            ElderGuardian var9 = (ElderGuardian)EntityType.ELDER_GUARDIAN.create(levelAccessor.getLevel());
            var9.heal(var9.getMaxHealth());
            var9.moveTo((double)var6 + 0.5D, (double)var7, (double)var8 + 0.5D, 0.0F, 0.0F);
            var9.finalizeSpawn(levelAccessor, levelAccessor.getCurrentDifficultyAt(new BlockPos(var9)), MobSpawnType.STRUCTURE, (SpawnGroupData)null, (CompoundTag)null);
            levelAccessor.addFreshEntity(var9);
            return true;
         } else {
            return false;
         }
      }
   }

   public static class OceanMonumentSimpleRoom extends OceanMonumentPieces.OceanMonumentPiece {
      private int mainDesign;

      public OceanMonumentSimpleRoom(Direction direction, OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition, Random random) {
         super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, 1, direction, oceanMonumentPieces$RoomDefinition, 1, 1, 1);
         this.mainDesign = random.nextInt(3);
      }

      public OceanMonumentSimpleRoom(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, compoundTag);
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         if(this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(levelAccessor, boundingBox, 0, 0, this.roomDefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if(this.roomDefinition.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(levelAccessor, boundingBox, 1, 4, 1, 6, 4, 6, BASE_GRAY);
         }

         boolean var5 = this.mainDesign != 0 && random.nextBoolean() && !this.roomDefinition.hasOpening[Direction.DOWN.get3DDataValue()] && !this.roomDefinition.hasOpening[Direction.UP.get3DDataValue()] && this.roomDefinition.countOpenings() > 1;
         if(this.mainDesign == 0) {
            this.generateBox(levelAccessor, boundingBox, 0, 1, 0, 2, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 0, 3, 0, 2, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 0, 2, 2, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 1, 2, 0, 2, 2, 0, BASE_GRAY, BASE_GRAY, false);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 1, 2, 1, boundingBox);
            this.generateBox(levelAccessor, boundingBox, 5, 1, 0, 7, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 5, 3, 0, 7, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 7, 2, 0, 7, 2, 2, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 5, 2, 0, 6, 2, 0, BASE_GRAY, BASE_GRAY, false);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 6, 2, 1, boundingBox);
            this.generateBox(levelAccessor, boundingBox, 0, 1, 5, 2, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 0, 3, 5, 2, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 0, 2, 5, 0, 2, 7, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 1, 2, 7, 2, 2, 7, BASE_GRAY, BASE_GRAY, false);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 1, 2, 6, boundingBox);
            this.generateBox(levelAccessor, boundingBox, 5, 1, 5, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 5, 3, 5, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 7, 2, 5, 7, 2, 7, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 5, 2, 7, 6, 2, 7, BASE_GRAY, BASE_GRAY, false);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 6, 2, 6, boundingBox);
            if(this.roomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
               this.generateBox(levelAccessor, boundingBox, 3, 3, 0, 4, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(levelAccessor, boundingBox, 3, 3, 0, 4, 3, 1, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, 3, 2, 0, 4, 2, 0, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(levelAccessor, boundingBox, 3, 1, 0, 4, 1, 1, BASE_LIGHT, BASE_LIGHT, false);
            }

            if(this.roomDefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
               this.generateBox(levelAccessor, boundingBox, 3, 3, 7, 4, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(levelAccessor, boundingBox, 3, 3, 6, 4, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, 3, 2, 7, 4, 2, 7, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(levelAccessor, boundingBox, 3, 1, 6, 4, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            }

            if(this.roomDefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
               this.generateBox(levelAccessor, boundingBox, 0, 3, 3, 0, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(levelAccessor, boundingBox, 0, 3, 3, 1, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, 0, 2, 3, 0, 2, 4, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(levelAccessor, boundingBox, 0, 1, 3, 1, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
            }

            if(this.roomDefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
               this.generateBox(levelAccessor, boundingBox, 7, 3, 3, 7, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(levelAccessor, boundingBox, 6, 3, 3, 7, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, 7, 2, 3, 7, 2, 4, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(levelAccessor, boundingBox, 6, 1, 3, 7, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
            }
         } else if(this.mainDesign == 1) {
            this.generateBox(levelAccessor, boundingBox, 2, 1, 2, 2, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 2, 1, 5, 2, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 5, 1, 5, 5, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 5, 1, 2, 5, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 2, 2, 2, boundingBox);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 2, 2, 5, boundingBox);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 5, 2, 5, boundingBox);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 5, 2, 2, boundingBox);
            this.generateBox(levelAccessor, boundingBox, 0, 1, 0, 1, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 0, 1, 1, 0, 3, 1, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 0, 1, 7, 1, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 0, 1, 6, 0, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 6, 1, 7, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 7, 1, 6, 7, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 6, 1, 0, 7, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 7, 1, 1, 7, 3, 1, BASE_LIGHT, BASE_LIGHT, false);
            this.placeBlock(levelAccessor, BASE_GRAY, 1, 2, 0, boundingBox);
            this.placeBlock(levelAccessor, BASE_GRAY, 0, 2, 1, boundingBox);
            this.placeBlock(levelAccessor, BASE_GRAY, 1, 2, 7, boundingBox);
            this.placeBlock(levelAccessor, BASE_GRAY, 0, 2, 6, boundingBox);
            this.placeBlock(levelAccessor, BASE_GRAY, 6, 2, 7, boundingBox);
            this.placeBlock(levelAccessor, BASE_GRAY, 7, 2, 6, boundingBox);
            this.placeBlock(levelAccessor, BASE_GRAY, 6, 2, 0, boundingBox);
            this.placeBlock(levelAccessor, BASE_GRAY, 7, 2, 1, boundingBox);
            if(!this.roomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
               this.generateBox(levelAccessor, boundingBox, 1, 3, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, 1, 2, 0, 6, 2, 0, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(levelAccessor, boundingBox, 1, 1, 0, 6, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
            }

            if(!this.roomDefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
               this.generateBox(levelAccessor, boundingBox, 1, 3, 7, 6, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, 1, 2, 7, 6, 2, 7, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(levelAccessor, boundingBox, 1, 1, 7, 6, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            }

            if(!this.roomDefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
               this.generateBox(levelAccessor, boundingBox, 0, 3, 1, 0, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, 0, 2, 1, 0, 2, 6, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(levelAccessor, boundingBox, 0, 1, 1, 0, 1, 6, BASE_LIGHT, BASE_LIGHT, false);
            }

            if(!this.roomDefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
               this.generateBox(levelAccessor, boundingBox, 7, 3, 1, 7, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, 7, 2, 1, 7, 2, 6, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(levelAccessor, boundingBox, 7, 1, 1, 7, 1, 6, BASE_LIGHT, BASE_LIGHT, false);
            }
         } else if(this.mainDesign == 2) {
            this.generateBox(levelAccessor, boundingBox, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 7, 1, 0, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 1, 1, 0, 6, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 1, 1, 7, 6, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 0, 2, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(levelAccessor, boundingBox, 7, 2, 0, 7, 2, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(levelAccessor, boundingBox, 1, 2, 0, 6, 2, 0, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(levelAccessor, boundingBox, 1, 2, 7, 6, 2, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(levelAccessor, boundingBox, 0, 3, 0, 0, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 7, 3, 0, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 1, 3, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 1, 3, 7, 6, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 0, 1, 3, 0, 2, 4, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(levelAccessor, boundingBox, 7, 1, 3, 7, 2, 4, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(levelAccessor, boundingBox, 3, 1, 0, 4, 2, 0, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(levelAccessor, boundingBox, 3, 1, 7, 4, 2, 7, BASE_BLACK, BASE_BLACK, false);
            if(this.roomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
               this.generateWaterBox(levelAccessor, boundingBox, 3, 1, 0, 4, 2, 0);
            }

            if(this.roomDefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
               this.generateWaterBox(levelAccessor, boundingBox, 3, 1, 7, 4, 2, 7);
            }

            if(this.roomDefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
               this.generateWaterBox(levelAccessor, boundingBox, 0, 1, 3, 0, 2, 4);
            }

            if(this.roomDefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
               this.generateWaterBox(levelAccessor, boundingBox, 7, 1, 3, 7, 2, 4);
            }
         }

         if(var5) {
            this.generateBox(levelAccessor, boundingBox, 3, 1, 3, 4, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 3, 2, 3, 4, 2, 4, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(levelAccessor, boundingBox, 3, 3, 3, 4, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
         }

         return true;
      }
   }

   public static class OceanMonumentSimpleTopRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentSimpleTopRoom(Direction direction, OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition) {
         super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, 1, direction, oceanMonumentPieces$RoomDefinition, 1, 1, 1);
      }

      public OceanMonumentSimpleTopRoom(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, compoundTag);
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         if(this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(levelAccessor, boundingBox, 0, 0, this.roomDefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if(this.roomDefinition.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(levelAccessor, boundingBox, 1, 4, 1, 6, 4, 6, BASE_GRAY);
         }

         for(int var5 = 1; var5 <= 6; ++var5) {
            for(int var6 = 1; var6 <= 6; ++var6) {
               if(random.nextInt(3) != 0) {
                  int var7 = 2 + (random.nextInt(4) == 0?0:1);
                  BlockState var8 = Blocks.WET_SPONGE.defaultBlockState();
                  this.generateBox(levelAccessor, boundingBox, var5, var7, var6, var5, 3, var6, var8, var8, false);
               }
            }
         }

         this.generateBox(levelAccessor, boundingBox, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 7, 1, 0, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 1, 0, 6, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 1, 7, 6, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 0, 2, 0, 0, 2, 7, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(levelAccessor, boundingBox, 7, 2, 0, 7, 2, 7, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(levelAccessor, boundingBox, 1, 2, 0, 6, 2, 0, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(levelAccessor, boundingBox, 1, 2, 7, 6, 2, 7, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(levelAccessor, boundingBox, 0, 3, 0, 0, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 7, 3, 0, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 3, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 1, 3, 7, 6, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(levelAccessor, boundingBox, 0, 1, 3, 0, 2, 4, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(levelAccessor, boundingBox, 7, 1, 3, 7, 2, 4, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(levelAccessor, boundingBox, 3, 1, 0, 4, 2, 0, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(levelAccessor, boundingBox, 3, 1, 7, 4, 2, 7, BASE_BLACK, BASE_BLACK, false);
         if(this.roomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(levelAccessor, boundingBox, 3, 1, 0, 4, 2, 0);
         }

         return true;
      }
   }

   public static class OceanMonumentWingRoom extends OceanMonumentPieces.OceanMonumentPiece {
      private int mainDesign;

      public OceanMonumentWingRoom(Direction direction, BoundingBox boundingBox, int var3) {
         super(StructurePieceType.OCEAN_MONUMENT_WING_ROOM, direction, boundingBox);
         this.mainDesign = var3 & 1;
      }

      public OceanMonumentWingRoom(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.OCEAN_MONUMENT_WING_ROOM, compoundTag);
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         if(this.mainDesign == 0) {
            for(int var5 = 0; var5 < 4; ++var5) {
               this.generateBox(levelAccessor, boundingBox, 10 - var5, 3 - var5, 20 - var5, 12 + var5, 3 - var5, 20, BASE_LIGHT, BASE_LIGHT, false);
            }

            this.generateBox(levelAccessor, boundingBox, 7, 0, 6, 15, 0, 16, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 6, 0, 6, 6, 3, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 16, 0, 6, 16, 3, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 7, 1, 7, 7, 1, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 15, 1, 7, 15, 1, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 7, 1, 6, 9, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 13, 1, 6, 15, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 8, 1, 7, 9, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 13, 1, 7, 14, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 9, 0, 5, 13, 0, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 10, 0, 7, 12, 0, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(levelAccessor, boundingBox, 8, 0, 10, 8, 0, 12, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(levelAccessor, boundingBox, 14, 0, 10, 14, 0, 12, BASE_BLACK, BASE_BLACK, false);

            for(int var5 = 18; var5 >= 7; var5 -= 3) {
               this.placeBlock(levelAccessor, LAMP_BLOCK, 6, 3, var5, boundingBox);
               this.placeBlock(levelAccessor, LAMP_BLOCK, 16, 3, var5, boundingBox);
            }

            this.placeBlock(levelAccessor, LAMP_BLOCK, 10, 0, 10, boundingBox);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 12, 0, 10, boundingBox);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 10, 0, 12, boundingBox);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 12, 0, 12, boundingBox);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 8, 3, 6, boundingBox);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 14, 3, 6, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 4, 2, 4, boundingBox);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 4, 1, 4, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 4, 0, 4, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 18, 2, 4, boundingBox);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 18, 1, 4, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 18, 0, 4, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 4, 2, 18, boundingBox);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 4, 1, 18, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 4, 0, 18, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 18, 2, 18, boundingBox);
            this.placeBlock(levelAccessor, LAMP_BLOCK, 18, 1, 18, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 18, 0, 18, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 9, 7, 20, boundingBox);
            this.placeBlock(levelAccessor, BASE_LIGHT, 13, 7, 20, boundingBox);
            this.generateBox(levelAccessor, boundingBox, 6, 0, 21, 7, 4, 21, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 15, 0, 21, 16, 4, 21, BASE_LIGHT, BASE_LIGHT, false);
            this.spawnElder(levelAccessor, boundingBox, 11, 2, 16);
         } else if(this.mainDesign == 1) {
            this.generateBox(levelAccessor, boundingBox, 9, 3, 18, 13, 3, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 9, 0, 18, 9, 2, 18, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(levelAccessor, boundingBox, 13, 0, 18, 13, 2, 18, BASE_LIGHT, BASE_LIGHT, false);
            int var5 = 9;
            int var6 = 20;
            int var7 = 5;

            for(int var8 = 0; var8 < 2; ++var8) {
               this.placeBlock(levelAccessor, BASE_LIGHT, var5, 6, 20, boundingBox);
               this.placeBlock(levelAccessor, LAMP_BLOCK, var5, 5, 20, boundingBox);
               this.placeBlock(levelAccessor, BASE_LIGHT, var5, 4, 20, boundingBox);
               var5 = 13;
            }

            this.generateBox(levelAccessor, boundingBox, 7, 3, 7, 15, 3, 14, BASE_LIGHT, BASE_LIGHT, false);
            var5 = 10;

            for(int var8 = 0; var8 < 2; ++var8) {
               this.generateBox(levelAccessor, boundingBox, var5, 0, 10, var5, 6, 10, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, var5, 0, 12, var5, 6, 12, BASE_LIGHT, BASE_LIGHT, false);
               this.placeBlock(levelAccessor, LAMP_BLOCK, var5, 0, 10, boundingBox);
               this.placeBlock(levelAccessor, LAMP_BLOCK, var5, 0, 12, boundingBox);
               this.placeBlock(levelAccessor, LAMP_BLOCK, var5, 4, 10, boundingBox);
               this.placeBlock(levelAccessor, LAMP_BLOCK, var5, 4, 12, boundingBox);
               var5 = 12;
            }

            var5 = 8;

            for(int var8 = 0; var8 < 2; ++var8) {
               this.generateBox(levelAccessor, boundingBox, var5, 0, 7, var5, 2, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(levelAccessor, boundingBox, var5, 0, 14, var5, 2, 14, BASE_LIGHT, BASE_LIGHT, false);
               var5 = 14;
            }

            this.generateBox(levelAccessor, boundingBox, 8, 3, 8, 8, 3, 13, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(levelAccessor, boundingBox, 14, 3, 8, 14, 3, 13, BASE_BLACK, BASE_BLACK, false);
            this.spawnElder(levelAccessor, boundingBox, 11, 5, 13);
         }

         return true;
      }
   }

   static class RoomDefinition {
      private final int index;
      private final OceanMonumentPieces.RoomDefinition[] connections = new OceanMonumentPieces.RoomDefinition[6];
      private final boolean[] hasOpening = new boolean[6];
      private boolean claimed;
      private boolean isSource;
      private int scanIndex;

      public RoomDefinition(int index) {
         this.index = index;
      }

      public void setConnection(Direction direction, OceanMonumentPieces.RoomDefinition oceanMonumentPieces$RoomDefinition) {
         this.connections[direction.get3DDataValue()] = oceanMonumentPieces$RoomDefinition;
         oceanMonumentPieces$RoomDefinition.connections[direction.getOpposite().get3DDataValue()] = this;
      }

      public void updateOpenings() {
         for(int var1 = 0; var1 < 6; ++var1) {
            this.hasOpening[var1] = this.connections[var1] != null;
         }

      }

      public boolean findSource(int scanIndex) {
         if(this.isSource) {
            return true;
         } else {
            this.scanIndex = scanIndex;

            for(int var2 = 0; var2 < 6; ++var2) {
               if(this.connections[var2] != null && this.hasOpening[var2] && this.connections[var2].scanIndex != scanIndex && this.connections[var2].findSource(scanIndex)) {
                  return true;
               }
            }

            return false;
         }
      }

      public boolean isSpecial() {
         return this.index >= 75;
      }

      public int countOpenings() {
         int var1 = 0;

         for(int var2 = 0; var2 < 6; ++var2) {
            if(this.hasOpening[var2]) {
               ++var1;
            }
         }

         return var1;
      }
   }
}
