package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class MineShaftPieces {
   private static MineShaftPieces.MineShaftPiece createRandomShaftPiece(List list, Random random, int var2, int var3, int var4, @Nullable Direction direction, int var6, MineshaftFeature.Type mineshaftFeature$Type) {
      int var8 = random.nextInt(100);
      if(var8 >= 80) {
         BoundingBox var9 = MineShaftPieces.MineShaftCrossing.findCrossing(list, random, var2, var3, var4, direction);
         if(var9 != null) {
            return new MineShaftPieces.MineShaftCrossing(var6, var9, direction, mineshaftFeature$Type);
         }
      } else if(var8 >= 70) {
         BoundingBox var9 = MineShaftPieces.MineShaftStairs.findStairs(list, random, var2, var3, var4, direction);
         if(var9 != null) {
            return new MineShaftPieces.MineShaftStairs(var6, var9, direction, mineshaftFeature$Type);
         }
      } else {
         BoundingBox var9 = MineShaftPieces.MineShaftCorridor.findCorridorSize(list, random, var2, var3, var4, direction);
         if(var9 != null) {
            return new MineShaftPieces.MineShaftCorridor(var6, random, var9, direction, mineshaftFeature$Type);
         }
      }

      return null;
   }

   private static MineShaftPieces.MineShaftPiece generateAndAddPiece(StructurePiece structurePiece, List list, Random random, int var3, int var4, int var5, Direction direction, int var7) {
      if(var7 > 8) {
         return null;
      } else if(Math.abs(var3 - structurePiece.getBoundingBox().x0) <= 80 && Math.abs(var5 - structurePiece.getBoundingBox().z0) <= 80) {
         MineshaftFeature.Type var8 = ((MineShaftPieces.MineShaftPiece)structurePiece).type;
         MineShaftPieces.MineShaftPiece var9 = createRandomShaftPiece(list, random, var3, var4, var5, direction, var7 + 1, var8);
         if(var9 != null) {
            list.add(var9);
            var9.addChildren(structurePiece, list, random);
         }

         return var9;
      } else {
         return null;
      }
   }

   public static class MineShaftCorridor extends MineShaftPieces.MineShaftPiece {
      private final boolean hasRails;
      private final boolean spiderCorridor;
      private boolean hasPlacedSpider;
      private final int numSections;

      public MineShaftCorridor(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.MINE_SHAFT_CORRIDOR, compoundTag);
         this.hasRails = compoundTag.getBoolean("hr");
         this.spiderCorridor = compoundTag.getBoolean("sc");
         this.hasPlacedSpider = compoundTag.getBoolean("hps");
         this.numSections = compoundTag.getInt("Num");
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         super.addAdditionalSaveData(compoundTag);
         compoundTag.putBoolean("hr", this.hasRails);
         compoundTag.putBoolean("sc", this.spiderCorridor);
         compoundTag.putBoolean("hps", this.hasPlacedSpider);
         compoundTag.putInt("Num", this.numSections);
      }

      public MineShaftCorridor(int var1, Random random, BoundingBox boundingBox, Direction orientation, MineshaftFeature.Type mineshaftFeature$Type) {
         super(StructurePieceType.MINE_SHAFT_CORRIDOR, var1, mineshaftFeature$Type);
         this.setOrientation(orientation);
         this.boundingBox = boundingBox;
         this.hasRails = random.nextInt(3) == 0;
         this.spiderCorridor = !this.hasRails && random.nextInt(23) == 0;
         if(this.getOrientation().getAxis() == Direction.Axis.Z) {
            this.numSections = boundingBox.getZSpan() / 5;
         } else {
            this.numSections = boundingBox.getXSpan() / 5;
         }

      }

      public static BoundingBox findCorridorSize(List list, Random random, int var2, int var3, int var4, Direction direction) {
         BoundingBox boundingBox = new BoundingBox(var2, var3, var4, var2, var3 + 3 - 1, var4);

         int var7;
         for(var7 = random.nextInt(3) + 2; var7 > 0; --var7) {
            int var8 = var7 * 5;
            switch(direction) {
            case NORTH:
            default:
               boundingBox.x1 = var2 + 3 - 1;
               boundingBox.z0 = var4 - (var8 - 1);
               break;
            case SOUTH:
               boundingBox.x1 = var2 + 3 - 1;
               boundingBox.z1 = var4 + var8 - 1;
               break;
            case WEST:
               boundingBox.x0 = var2 - (var8 - 1);
               boundingBox.z1 = var4 + 3 - 1;
               break;
            case EAST:
               boundingBox.x1 = var2 + var8 - 1;
               boundingBox.z1 = var4 + 3 - 1;
            }

            if(StructurePiece.findCollisionPiece(list, boundingBox) == null) {
               break;
            }
         }

         return var7 > 0?boundingBox:null;
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         int var4 = this.getGenDepth();
         int var5 = random.nextInt(4);
         Direction var6 = this.getOrientation();
         if(var6 != null) {
            switch(var6) {
            case NORTH:
            default:
               if(var5 <= 1) {
                  MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0 - 1, var6, var4);
               } else if(var5 == 2) {
                  MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0, Direction.WEST, var4);
               } else {
                  MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0, Direction.EAST, var4);
               }
               break;
            case SOUTH:
               if(var5 <= 1) {
                  MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z1 + 1, var6, var4);
               } else if(var5 == 2) {
                  MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z1 - 3, Direction.WEST, var4);
               } else {
                  MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z1 - 3, Direction.EAST, var4);
               }
               break;
            case WEST:
               if(var5 <= 1) {
                  MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0, var6, var4);
               } else if(var5 == 2) {
                  MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0 - 1, Direction.NORTH, var4);
               } else {
                  MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z1 + 1, Direction.SOUTH, var4);
               }
               break;
            case EAST:
               if(var5 <= 1) {
                  MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0, var6, var4);
               } else if(var5 == 2) {
                  MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 - 3, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z0 - 1, Direction.NORTH, var4);
               } else {
                  MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 - 3, this.boundingBox.y0 - 1 + random.nextInt(3), this.boundingBox.z1 + 1, Direction.SOUTH, var4);
               }
            }
         }

         if(var4 < 8) {
            if(var6 != Direction.NORTH && var6 != Direction.SOUTH) {
               for(int var7 = this.boundingBox.x0 + 3; var7 + 3 <= this.boundingBox.x1; var7 += 5) {
                  int var8 = random.nextInt(5);
                  if(var8 == 0) {
                     MineShaftPieces.generateAndAddPiece(structurePiece, list, random, var7, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, var4 + 1);
                  } else if(var8 == 1) {
                     MineShaftPieces.generateAndAddPiece(structurePiece, list, random, var7, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, var4 + 1);
                  }
               }
            } else {
               for(int var7 = this.boundingBox.z0 + 3; var7 + 3 <= this.boundingBox.z1; var7 += 5) {
                  int var8 = random.nextInt(5);
                  if(var8 == 0) {
                     MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0, var7, Direction.WEST, var4 + 1);
                  } else if(var8 == 1) {
                     MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0, var7, Direction.EAST, var4 + 1);
                  }
               }
            }
         }

      }

      protected boolean createChest(LevelAccessor levelAccessor, BoundingBox boundingBox, Random random, int var4, int var5, int var6, ResourceLocation resourceLocation) {
         BlockPos var8 = new BlockPos(this.getWorldX(var4, var6), this.getWorldY(var5), this.getWorldZ(var4, var6));
         if(boundingBox.isInside(var8) && levelAccessor.getBlockState(var8).isAir() && !levelAccessor.getBlockState(var8.below()).isAir()) {
            BlockState var9 = (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, random.nextBoolean()?RailShape.NORTH_SOUTH:RailShape.EAST_WEST);
            this.placeBlock(levelAccessor, var9, var4, var5, var6, boundingBox);
            MinecartChest var10 = new MinecartChest(levelAccessor.getLevel(), (double)((float)var8.getX() + 0.5F), (double)((float)var8.getY() + 0.5F), (double)((float)var8.getZ() + 0.5F));
            var10.setLootTable(resourceLocation, random.nextLong());
            levelAccessor.addFreshEntity(var10);
            return true;
         } else {
            return false;
         }
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         if(this.edgesLiquid(levelAccessor, boundingBox)) {
            return false;
         } else {
            int var5 = 0;
            int var6 = 2;
            int var7 = 0;
            int var8 = 2;
            int var9 = this.numSections * 5 - 1;
            BlockState var10 = this.getPlanksBlock();
            this.generateBox(levelAccessor, boundingBox, 0, 0, 0, 2, 1, var9, CAVE_AIR, CAVE_AIR, false);
            this.generateMaybeBox(levelAccessor, boundingBox, random, 0.8F, 0, 2, 0, 2, 2, var9, CAVE_AIR, CAVE_AIR, false, false);
            if(this.spiderCorridor) {
               this.generateMaybeBox(levelAccessor, boundingBox, random, 0.6F, 0, 0, 0, 2, 1, var9, Blocks.COBWEB.defaultBlockState(), CAVE_AIR, false, true);
            }

            for(int var11 = 0; var11 < this.numSections; ++var11) {
               int var12 = 2 + var11 * 5;
               this.placeSupport(levelAccessor, boundingBox, 0, 0, var12, 2, 2, random);
               this.placeCobWeb(levelAccessor, boundingBox, random, 0.1F, 0, 2, var12 - 1);
               this.placeCobWeb(levelAccessor, boundingBox, random, 0.1F, 2, 2, var12 - 1);
               this.placeCobWeb(levelAccessor, boundingBox, random, 0.1F, 0, 2, var12 + 1);
               this.placeCobWeb(levelAccessor, boundingBox, random, 0.1F, 2, 2, var12 + 1);
               this.placeCobWeb(levelAccessor, boundingBox, random, 0.05F, 0, 2, var12 - 2);
               this.placeCobWeb(levelAccessor, boundingBox, random, 0.05F, 2, 2, var12 - 2);
               this.placeCobWeb(levelAccessor, boundingBox, random, 0.05F, 0, 2, var12 + 2);
               this.placeCobWeb(levelAccessor, boundingBox, random, 0.05F, 2, 2, var12 + 2);
               if(random.nextInt(100) == 0) {
                  this.createChest(levelAccessor, boundingBox, random, 2, 0, var12 - 1, BuiltInLootTables.ABANDONED_MINESHAFT);
               }

               if(random.nextInt(100) == 0) {
                  this.createChest(levelAccessor, boundingBox, random, 0, 0, var12 + 1, BuiltInLootTables.ABANDONED_MINESHAFT);
               }

               if(this.spiderCorridor && !this.hasPlacedSpider) {
                  int var13 = this.getWorldY(0);
                  int var14 = var12 - 1 + random.nextInt(3);
                  int var15 = this.getWorldX(1, var14);
                  int var16 = this.getWorldZ(1, var14);
                  BlockPos var17 = new BlockPos(var15, var13, var16);
                  if(boundingBox.isInside(var17) && this.isInterior(levelAccessor, 1, 0, var14, boundingBox)) {
                     this.hasPlacedSpider = true;
                     levelAccessor.setBlock(var17, Blocks.SPAWNER.defaultBlockState(), 2);
                     BlockEntity var18 = levelAccessor.getBlockEntity(var17);
                     if(var18 instanceof SpawnerBlockEntity) {
                        ((SpawnerBlockEntity)var18).getSpawner().setEntityId(EntityType.CAVE_SPIDER);
                     }
                  }
               }
            }

            for(int var11 = 0; var11 <= 2; ++var11) {
               for(int var12 = 0; var12 <= var9; ++var12) {
                  int var13 = -1;
                  BlockState var14 = this.getBlock(levelAccessor, var11, -1, var12, boundingBox);
                  if(var14.isAir() && this.isInterior(levelAccessor, var11, -1, var12, boundingBox)) {
                     int var15 = -1;
                     this.placeBlock(levelAccessor, var10, var11, -1, var12, boundingBox);
                  }
               }
            }

            if(this.hasRails) {
               BlockState var11 = (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.NORTH_SOUTH);

               for(int var12 = 0; var12 <= var9; ++var12) {
                  BlockState var13 = this.getBlock(levelAccessor, 1, -1, var12, boundingBox);
                  if(!var13.isAir() && var13.isSolidRender(levelAccessor, new BlockPos(this.getWorldX(1, var12), this.getWorldY(-1), this.getWorldZ(1, var12)))) {
                     float var14 = this.isInterior(levelAccessor, 1, 0, var12, boundingBox)?0.7F:0.9F;
                     this.maybeGenerateBlock(levelAccessor, boundingBox, random, var14, 1, 0, var12, var11);
                  }
               }
            }

            return true;
         }
      }

      private void placeSupport(LevelAccessor levelAccessor, BoundingBox boundingBox, int var3, int var4, int var5, int var6, int var7, Random random) {
         if(this.isSupportingBox(levelAccessor, boundingBox, var3, var7, var6, var5)) {
            BlockState var9 = this.getPlanksBlock();
            BlockState var10 = this.getFenceBlock();
            this.generateBox(levelAccessor, boundingBox, var3, var4, var5, var3, var6 - 1, var5, (BlockState)var10.setValue(FenceBlock.WEST, Boolean.valueOf(true)), CAVE_AIR, false);
            this.generateBox(levelAccessor, boundingBox, var7, var4, var5, var7, var6 - 1, var5, (BlockState)var10.setValue(FenceBlock.EAST, Boolean.valueOf(true)), CAVE_AIR, false);
            if(random.nextInt(4) == 0) {
               this.generateBox(levelAccessor, boundingBox, var3, var6, var5, var3, var6, var5, var9, CAVE_AIR, false);
               this.generateBox(levelAccessor, boundingBox, var7, var6, var5, var7, var6, var5, var9, CAVE_AIR, false);
            } else {
               this.generateBox(levelAccessor, boundingBox, var3, var6, var5, var7, var6, var5, var9, CAVE_AIR, false);
               this.maybeGenerateBlock(levelAccessor, boundingBox, random, 0.05F, var3 + 1, var6, var5 - 1, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.NORTH));
               this.maybeGenerateBlock(levelAccessor, boundingBox, random, 0.05F, var3 + 1, var6, var5 + 1, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH));
            }

         }
      }

      private void placeCobWeb(LevelAccessor levelAccessor, BoundingBox boundingBox, Random random, float var4, int var5, int var6, int var7) {
         if(this.isInterior(levelAccessor, var5, var6, var7, boundingBox)) {
            this.maybeGenerateBlock(levelAccessor, boundingBox, random, var4, var5, var6, var7, Blocks.COBWEB.defaultBlockState());
         }

      }
   }

   public static class MineShaftCrossing extends MineShaftPieces.MineShaftPiece {
      private final Direction direction;
      private final boolean isTwoFloored;

      public MineShaftCrossing(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.MINE_SHAFT_CROSSING, compoundTag);
         this.isTwoFloored = compoundTag.getBoolean("tf");
         this.direction = Direction.from2DDataValue(compoundTag.getInt("D"));
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         super.addAdditionalSaveData(compoundTag);
         compoundTag.putBoolean("tf", this.isTwoFloored);
         compoundTag.putInt("D", this.direction.get2DDataValue());
      }

      public MineShaftCrossing(int var1, BoundingBox boundingBox, @Nullable Direction direction, MineshaftFeature.Type mineshaftFeature$Type) {
         super(StructurePieceType.MINE_SHAFT_CROSSING, var1, mineshaftFeature$Type);
         this.direction = direction;
         this.boundingBox = boundingBox;
         this.isTwoFloored = boundingBox.getYSpan() > 3;
      }

      public static BoundingBox findCrossing(List list, Random random, int var2, int var3, int var4, Direction direction) {
         BoundingBox boundingBox = new BoundingBox(var2, var3, var4, var2, var3 + 3 - 1, var4);
         if(random.nextInt(4) == 0) {
            boundingBox.y1 += 4;
         }

         switch(direction) {
         case NORTH:
         default:
            boundingBox.x0 = var2 - 1;
            boundingBox.x1 = var2 + 3;
            boundingBox.z0 = var4 - 4;
            break;
         case SOUTH:
            boundingBox.x0 = var2 - 1;
            boundingBox.x1 = var2 + 3;
            boundingBox.z1 = var4 + 3 + 1;
            break;
         case WEST:
            boundingBox.x0 = var2 - 4;
            boundingBox.z0 = var4 - 1;
            boundingBox.z1 = var4 + 3;
            break;
         case EAST:
            boundingBox.x1 = var2 + 3 + 1;
            boundingBox.z0 = var4 - 1;
            boundingBox.z1 = var4 + 3;
         }

         return StructurePiece.findCollisionPiece(list, boundingBox) != null?null:boundingBox;
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         int var4 = this.getGenDepth();
         switch(this.direction) {
         case NORTH:
         default:
            MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, var4);
            MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.WEST, var4);
            MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.EAST, var4);
            break;
         case SOUTH:
            MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, var4);
            MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.WEST, var4);
            MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.EAST, var4);
            break;
         case WEST:
            MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, var4);
            MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, var4);
            MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.WEST, var4);
            break;
         case EAST:
            MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, var4);
            MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, var4);
            MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.EAST, var4);
         }

         if(this.isTwoFloored) {
            if(random.nextBoolean()) {
               MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z0 - 1, Direction.NORTH, var4);
            }

            if(random.nextBoolean()) {
               MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z0 + 1, Direction.WEST, var4);
            }

            if(random.nextBoolean()) {
               MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z0 + 1, Direction.EAST, var4);
            }

            if(random.nextBoolean()) {
               MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 + 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z1 + 1, Direction.SOUTH, var4);
            }
         }

      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         if(this.edgesLiquid(levelAccessor, boundingBox)) {
            return false;
         } else {
            BlockState var5 = this.getPlanksBlock();
            if(this.isTwoFloored) {
               this.generateBox(levelAccessor, boundingBox, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0, this.boundingBox.x1 - 1, this.boundingBox.y0 + 3 - 1, this.boundingBox.z1, CAVE_AIR, CAVE_AIR, false);
               this.generateBox(levelAccessor, boundingBox, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.x1, this.boundingBox.y0 + 3 - 1, this.boundingBox.z1 - 1, CAVE_AIR, CAVE_AIR, false);
               this.generateBox(levelAccessor, boundingBox, this.boundingBox.x0 + 1, this.boundingBox.y1 - 2, this.boundingBox.z0, this.boundingBox.x1 - 1, this.boundingBox.y1, this.boundingBox.z1, CAVE_AIR, CAVE_AIR, false);
               this.generateBox(levelAccessor, boundingBox, this.boundingBox.x0, this.boundingBox.y1 - 2, this.boundingBox.z0 + 1, this.boundingBox.x1, this.boundingBox.y1, this.boundingBox.z1 - 1, CAVE_AIR, CAVE_AIR, false);
               this.generateBox(levelAccessor, boundingBox, this.boundingBox.x0 + 1, this.boundingBox.y0 + 3, this.boundingBox.z0 + 1, this.boundingBox.x1 - 1, this.boundingBox.y0 + 3, this.boundingBox.z1 - 1, CAVE_AIR, CAVE_AIR, false);
            } else {
               this.generateBox(levelAccessor, boundingBox, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0, this.boundingBox.x1 - 1, this.boundingBox.y1, this.boundingBox.z1, CAVE_AIR, CAVE_AIR, false);
               this.generateBox(levelAccessor, boundingBox, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.x1, this.boundingBox.y1, this.boundingBox.z1 - 1, CAVE_AIR, CAVE_AIR, false);
            }

            this.placeSupportPillar(levelAccessor, boundingBox, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.y1);
            this.placeSupportPillar(levelAccessor, boundingBox, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 - 1, this.boundingBox.y1);
            this.placeSupportPillar(levelAccessor, boundingBox, this.boundingBox.x1 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.y1);
            this.placeSupportPillar(levelAccessor, boundingBox, this.boundingBox.x1 - 1, this.boundingBox.y0, this.boundingBox.z1 - 1, this.boundingBox.y1);

            for(int var6 = this.boundingBox.x0; var6 <= this.boundingBox.x1; ++var6) {
               for(int var7 = this.boundingBox.z0; var7 <= this.boundingBox.z1; ++var7) {
                  if(this.getBlock(levelAccessor, var6, this.boundingBox.y0 - 1, var7, boundingBox).isAir() && this.isInterior(levelAccessor, var6, this.boundingBox.y0 - 1, var7, boundingBox)) {
                     this.placeBlock(levelAccessor, var5, var6, this.boundingBox.y0 - 1, var7, boundingBox);
                  }
               }
            }

            return true;
         }
      }

      private void placeSupportPillar(LevelAccessor levelAccessor, BoundingBox boundingBox, int var3, int var4, int var5, int var6) {
         if(!this.getBlock(levelAccessor, var3, var6 + 1, var5, boundingBox).isAir()) {
            this.generateBox(levelAccessor, boundingBox, var3, var4, var5, var3, var6, var5, this.getPlanksBlock(), CAVE_AIR, false);
         }

      }
   }

   abstract static class MineShaftPiece extends StructurePiece {
      protected MineshaftFeature.Type type;

      public MineShaftPiece(StructurePieceType structurePieceType, int var2, MineshaftFeature.Type type) {
         super(structurePieceType, var2);
         this.type = type;
      }

      public MineShaftPiece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
         super(structurePieceType, compoundTag);
         this.type = MineshaftFeature.Type.byId(compoundTag.getInt("MST"));
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         compoundTag.putInt("MST", this.type.ordinal());
      }

      protected BlockState getPlanksBlock() {
         switch(this.type) {
         case NORMAL:
         default:
            return Blocks.OAK_PLANKS.defaultBlockState();
         case MESA:
            return Blocks.DARK_OAK_PLANKS.defaultBlockState();
         }
      }

      protected BlockState getFenceBlock() {
         switch(this.type) {
         case NORMAL:
         default:
            return Blocks.OAK_FENCE.defaultBlockState();
         case MESA:
            return Blocks.DARK_OAK_FENCE.defaultBlockState();
         }
      }

      protected boolean isSupportingBox(BlockGetter blockGetter, BoundingBox boundingBox, int var3, int var4, int var5, int var6) {
         for(int var7 = var3; var7 <= var4; ++var7) {
            if(this.getBlock(blockGetter, var7, var5 + 1, var6, boundingBox).isAir()) {
               return false;
            }
         }

         return true;
      }
   }

   public static class MineShaftRoom extends MineShaftPieces.MineShaftPiece {
      private final List childEntranceBoxes = Lists.newLinkedList();

      public MineShaftRoom(int var1, Random random, int var3, int var4, MineshaftFeature.Type type) {
         super(StructurePieceType.MINE_SHAFT_ROOM, var1, type);
         this.type = type;
         this.boundingBox = new BoundingBox(var3, 50, var4, var3 + 7 + random.nextInt(6), 54 + random.nextInt(6), var4 + 7 + random.nextInt(6));
      }

      public MineShaftRoom(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.MINE_SHAFT_ROOM, compoundTag);
         ListTag var3 = compoundTag.getList("Entrances", 11);

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            this.childEntranceBoxes.add(new BoundingBox(var3.getIntArray(var4)));
         }

      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         int var4 = this.getGenDepth();
         int var6 = this.boundingBox.getYSpan() - 3 - 1;
         if(var6 <= 0) {
            var6 = 1;
         }

         int var9;
         for(var5 = 0; var9 < this.boundingBox.getXSpan(); var9 = var9 + 4) {
            var9 = var9 + random.nextInt(this.boundingBox.getXSpan());
            if(var9 + 3 > this.boundingBox.getXSpan()) {
               break;
            }

            MineShaftPieces.MineShaftPiece var7 = MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 + var9, this.boundingBox.y0 + random.nextInt(var6) + 1, this.boundingBox.z0 - 1, Direction.NORTH, var4);
            if(var7 != null) {
               BoundingBox var8 = var7.getBoundingBox();
               this.childEntranceBoxes.add(new BoundingBox(var8.x0, var8.y0, this.boundingBox.z0, var8.x1, var8.y1, this.boundingBox.z0 + 1));
            }
         }

         for(var9 = 0; var9 < this.boundingBox.getXSpan(); var9 = var9 + 4) {
            var9 = var9 + random.nextInt(this.boundingBox.getXSpan());
            if(var9 + 3 > this.boundingBox.getXSpan()) {
               break;
            }

            MineShaftPieces.MineShaftPiece var7 = MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 + var9, this.boundingBox.y0 + random.nextInt(var6) + 1, this.boundingBox.z1 + 1, Direction.SOUTH, var4);
            if(var7 != null) {
               BoundingBox var8 = var7.getBoundingBox();
               this.childEntranceBoxes.add(new BoundingBox(var8.x0, var8.y0, this.boundingBox.z1 - 1, var8.x1, var8.y1, this.boundingBox.z1));
            }
         }

         for(var9 = 0; var9 < this.boundingBox.getZSpan(); var9 = var9 + 4) {
            var9 = var9 + random.nextInt(this.boundingBox.getZSpan());
            if(var9 + 3 > this.boundingBox.getZSpan()) {
               break;
            }

            MineShaftPieces.MineShaftPiece var7 = MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0 + random.nextInt(var6) + 1, this.boundingBox.z0 + var9, Direction.WEST, var4);
            if(var7 != null) {
               BoundingBox var8 = var7.getBoundingBox();
               this.childEntranceBoxes.add(new BoundingBox(this.boundingBox.x0, var8.y0, var8.z0, this.boundingBox.x0 + 1, var8.y1, var8.z1));
            }
         }

         for(var9 = 0; var9 < this.boundingBox.getZSpan(); var9 = var9 + 4) {
            var9 = var9 + random.nextInt(this.boundingBox.getZSpan());
            if(var9 + 3 > this.boundingBox.getZSpan()) {
               break;
            }

            StructurePiece var7 = MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0 + random.nextInt(var6) + 1, this.boundingBox.z0 + var9, Direction.EAST, var4);
            if(var7 != null) {
               BoundingBox var8 = var7.getBoundingBox();
               this.childEntranceBoxes.add(new BoundingBox(this.boundingBox.x1 - 1, var8.y0, var8.z0, this.boundingBox.x1, var8.y1, var8.z1));
            }
         }

      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         if(this.edgesLiquid(levelAccessor, boundingBox)) {
            return false;
         } else {
            this.generateBox(levelAccessor, boundingBox, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0, this.boundingBox.x1, this.boundingBox.y0, this.boundingBox.z1, Blocks.DIRT.defaultBlockState(), CAVE_AIR, true);
            this.generateBox(levelAccessor, boundingBox, this.boundingBox.x0, this.boundingBox.y0 + 1, this.boundingBox.z0, this.boundingBox.x1, Math.min(this.boundingBox.y0 + 3, this.boundingBox.y1), this.boundingBox.z1, CAVE_AIR, CAVE_AIR, false);

            for(BoundingBox var6 : this.childEntranceBoxes) {
               this.generateBox(levelAccessor, boundingBox, var6.x0, var6.y1 - 2, var6.z0, var6.x1, var6.y1, var6.z1, CAVE_AIR, CAVE_AIR, false);
            }

            this.generateUpperHalfSphere(levelAccessor, boundingBox, this.boundingBox.x0, this.boundingBox.y0 + 4, this.boundingBox.z0, this.boundingBox.x1, this.boundingBox.y1, this.boundingBox.z1, CAVE_AIR, false);
            return true;
         }
      }

      public void move(int var1, int var2, int var3) {
         super.move(var1, var2, var3);

         for(BoundingBox var5 : this.childEntranceBoxes) {
            var5.move(var1, var2, var3);
         }

      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         super.addAdditionalSaveData(compoundTag);
         ListTag var2 = new ListTag();

         for(BoundingBox var4 : this.childEntranceBoxes) {
            var2.add(var4.createTag());
         }

         compoundTag.put("Entrances", var2);
      }
   }

   public static class MineShaftStairs extends MineShaftPieces.MineShaftPiece {
      public MineShaftStairs(int var1, BoundingBox boundingBox, Direction orientation, MineshaftFeature.Type mineshaftFeature$Type) {
         super(StructurePieceType.MINE_SHAFT_STAIRS, var1, mineshaftFeature$Type);
         this.setOrientation(orientation);
         this.boundingBox = boundingBox;
      }

      public MineShaftStairs(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.MINE_SHAFT_STAIRS, compoundTag);
      }

      public static BoundingBox findStairs(List list, Random random, int var2, int var3, int var4, Direction direction) {
         BoundingBox boundingBox = new BoundingBox(var2, var3 - 5, var4, var2, var3 + 3 - 1, var4);
         switch(direction) {
         case NORTH:
         default:
            boundingBox.x1 = var2 + 3 - 1;
            boundingBox.z0 = var4 - 8;
            break;
         case SOUTH:
            boundingBox.x1 = var2 + 3 - 1;
            boundingBox.z1 = var4 + 8;
            break;
         case WEST:
            boundingBox.x0 = var2 - 8;
            boundingBox.z1 = var4 + 3 - 1;
            break;
         case EAST:
            boundingBox.x1 = var2 + 8;
            boundingBox.z1 = var4 + 3 - 1;
         }

         return StructurePiece.findCollisionPiece(list, boundingBox) != null?null:boundingBox;
      }

      public void addChildren(StructurePiece structurePiece, List list, Random random) {
         int var4 = this.getGenDepth();
         Direction var5 = this.getOrientation();
         if(var5 != null) {
            switch(var5) {
            case NORTH:
            default:
               MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, var4);
               break;
            case SOUTH:
               MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, var4);
               break;
            case WEST:
               MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0, Direction.WEST, var4);
               break;
            case EAST:
               MineShaftPieces.generateAndAddPiece(structurePiece, list, random, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0, Direction.EAST, var4);
            }
         }

      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         if(this.edgesLiquid(levelAccessor, boundingBox)) {
            return false;
         } else {
            this.generateBox(levelAccessor, boundingBox, 0, 5, 0, 2, 7, 1, CAVE_AIR, CAVE_AIR, false);
            this.generateBox(levelAccessor, boundingBox, 0, 0, 7, 2, 2, 8, CAVE_AIR, CAVE_AIR, false);

            for(int var5 = 0; var5 < 5; ++var5) {
               this.generateBox(levelAccessor, boundingBox, 0, 5 - var5 - (var5 < 4?1:0), 2 + var5, 2, 7 - var5, 2 + var5, CAVE_AIR, CAVE_AIR, false);
            }

            return true;
         }
      }
   }
}
