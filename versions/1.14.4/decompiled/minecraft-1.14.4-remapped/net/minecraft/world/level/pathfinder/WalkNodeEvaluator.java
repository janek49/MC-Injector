package net.minecraft.world.level.pathfinder;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.Target;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WalkNodeEvaluator extends NodeEvaluator {
   protected float oldWaterCost;

   public void prepare(LevelReader levelReader, Mob mob) {
      super.prepare(levelReader, mob);
      this.oldWaterCost = mob.getPathfindingMalus(BlockPathTypes.WATER);
   }

   public void done() {
      this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
      super.done();
   }

   public Node getStart() {
      int var1;
      if(this.canFloat() && this.mob.isInWater()) {
         var1 = Mth.floor(this.mob.getBoundingBox().minY);
         BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos(this.mob.x, (double)var1, this.mob.z);

         for(BlockState var3 = this.level.getBlockState(var2); var3.getBlock() == Blocks.WATER || var3.getFluidState() == Fluids.WATER.getSource(false); var3 = this.level.getBlockState(var2)) {
            ++var1;
            var2.set(this.mob.x, (double)var1, this.mob.z);
         }

         --var1;
      } else if(this.mob.onGround) {
         var1 = Mth.floor(this.mob.getBoundingBox().minY + 0.5D);
      } else {
         BlockPos var2;
         for(var2 = new BlockPos(this.mob); (this.level.getBlockState(var2).isAir() || this.level.getBlockState(var2).isPathfindable(this.level, var2, PathComputationType.LAND)) && var2.getY() > 0; var2 = var2.below()) {
            ;
         }

         var1 = var2.above().getY();
      }

      BlockPos var2 = new BlockPos(this.mob);
      BlockPathTypes var3 = this.getBlockPathType(this.mob, var2.getX(), var1, var2.getZ());
      if(this.mob.getPathfindingMalus(var3) < 0.0F) {
         Set<BlockPos> var4 = Sets.newHashSet();
         var4.add(new BlockPos(this.mob.getBoundingBox().minX, (double)var1, this.mob.getBoundingBox().minZ));
         var4.add(new BlockPos(this.mob.getBoundingBox().minX, (double)var1, this.mob.getBoundingBox().maxZ));
         var4.add(new BlockPos(this.mob.getBoundingBox().maxX, (double)var1, this.mob.getBoundingBox().minZ));
         var4.add(new BlockPos(this.mob.getBoundingBox().maxX, (double)var1, this.mob.getBoundingBox().maxZ));

         for(BlockPos var6 : var4) {
            BlockPathTypes var7 = this.getBlockPathType(this.mob, var6);
            if(this.mob.getPathfindingMalus(var7) >= 0.0F) {
               return this.getNode(var6.getX(), var6.getY(), var6.getZ());
            }
         }
      }

      return this.getNode(var2.getX(), var1, var2.getZ());
   }

   public Target getGoal(double var1, double var3, double var5) {
      return new Target(this.getNode(Mth.floor(var1), Mth.floor(var3), Mth.floor(var5)));
   }

   public int getNeighbors(Node[] nodes, Node var2) {
      int var3 = 0;
      int var4 = 0;
      BlockPathTypes var5 = this.getBlockPathType(this.mob, var2.x, var2.y + 1, var2.z);
      if(this.mob.getPathfindingMalus(var5) >= 0.0F) {
         var4 = Mth.floor(Math.max(1.0F, this.mob.maxUpStep));
      }

      double var6 = getFloorLevel(this.level, new BlockPos(var2.x, var2.y, var2.z));
      Node var8 = this.getLandNode(var2.x, var2.y, var2.z + 1, var4, var6, Direction.SOUTH);
      if(var8 != null && !var8.closed && var8.costMalus >= 0.0F) {
         nodes[var3++] = var8;
      }

      Node var9 = this.getLandNode(var2.x - 1, var2.y, var2.z, var4, var6, Direction.WEST);
      if(var9 != null && !var9.closed && var9.costMalus >= 0.0F) {
         nodes[var3++] = var9;
      }

      Node var10 = this.getLandNode(var2.x + 1, var2.y, var2.z, var4, var6, Direction.EAST);
      if(var10 != null && !var10.closed && var10.costMalus >= 0.0F) {
         nodes[var3++] = var10;
      }

      Node var11 = this.getLandNode(var2.x, var2.y, var2.z - 1, var4, var6, Direction.NORTH);
      if(var11 != null && !var11.closed && var11.costMalus >= 0.0F) {
         nodes[var3++] = var11;
      }

      Node var12 = this.getLandNode(var2.x - 1, var2.y, var2.z - 1, var4, var6, Direction.NORTH);
      if(this.isDiagonalValid(var2, var9, var11, var12)) {
         nodes[var3++] = var12;
      }

      Node var13 = this.getLandNode(var2.x + 1, var2.y, var2.z - 1, var4, var6, Direction.NORTH);
      if(this.isDiagonalValid(var2, var10, var11, var13)) {
         nodes[var3++] = var13;
      }

      Node var14 = this.getLandNode(var2.x - 1, var2.y, var2.z + 1, var4, var6, Direction.SOUTH);
      if(this.isDiagonalValid(var2, var9, var8, var14)) {
         nodes[var3++] = var14;
      }

      Node var15 = this.getLandNode(var2.x + 1, var2.y, var2.z + 1, var4, var6, Direction.SOUTH);
      if(this.isDiagonalValid(var2, var10, var8, var15)) {
         nodes[var3++] = var15;
      }

      return var3;
   }

   private boolean isDiagonalValid(Node var1, @Nullable Node var2, @Nullable Node var3, @Nullable Node var4) {
      return var4 != null && var3 != null && var2 != null?(var4.closed?false:(var3.y <= var1.y && var2.y <= var1.y?var4.costMalus >= 0.0F && (var3.y < var1.y || var3.costMalus >= 0.0F) && (var2.y < var1.y || var2.costMalus >= 0.0F):false)):false;
   }

   public static double getFloorLevel(BlockGetter blockGetter, BlockPos blockPos) {
      BlockPos blockPos = blockPos.below();
      VoxelShape var3 = blockGetter.getBlockState(blockPos).getCollisionShape(blockGetter, blockPos);
      return (double)blockPos.getY() + (var3.isEmpty()?0.0D:var3.max(Direction.Axis.Y));
   }

   @Nullable
   private Node getLandNode(int var1, int var2, int var3, int var4, double var5, Direction direction) {
      Node node = null;
      BlockPos var9 = new BlockPos(var1, var2, var3);
      double var10 = getFloorLevel(this.level, var9);
      if(var10 - var5 > 1.125D) {
         return null;
      } else {
         BlockPathTypes var12 = this.getBlockPathType(this.mob, var1, var2, var3);
         float var13 = this.mob.getPathfindingMalus(var12);
         double var14 = (double)this.mob.getBbWidth() / 2.0D;
         if(var13 >= 0.0F) {
            node = this.getNode(var1, var2, var3);
            node.type = var12;
            node.costMalus = Math.max(node.costMalus, var13);
         }

         if(var12 == BlockPathTypes.WALKABLE) {
            return node;
         } else {
            if((node == null || node.costMalus < 0.0F) && var4 > 0 && var12 != BlockPathTypes.FENCE && var12 != BlockPathTypes.TRAPDOOR) {
               node = this.getLandNode(var1, var2 + 1, var3, var4 - 1, var5, direction);
               if(node != null && (node.type == BlockPathTypes.OPEN || node.type == BlockPathTypes.WALKABLE) && this.mob.getBbWidth() < 1.0F) {
                  double var16 = (double)(var1 - direction.getStepX()) + 0.5D;
                  double var18 = (double)(var3 - direction.getStepZ()) + 0.5D;
                  AABB var20 = new AABB(var16 - var14, getFloorLevel(this.level, new BlockPos(var16, (double)(var2 + 1), var18)) + 0.001D, var18 - var14, var16 + var14, (double)this.mob.getBbHeight() + getFloorLevel(this.level, new BlockPos(node.x, node.y, node.z)) - 0.002D, var18 + var14);
                  if(!this.level.noCollision(this.mob, var20)) {
                     node = null;
                  }
               }
            }

            if(var12 == BlockPathTypes.WATER && !this.canFloat()) {
               if(this.getBlockPathType(this.mob, var1, var2 - 1, var3) != BlockPathTypes.WATER) {
                  return node;
               }

               while(var2 > 0) {
                  --var2;
                  var12 = this.getBlockPathType(this.mob, var1, var2, var3);
                  if(var12 != BlockPathTypes.WATER) {
                     return node;
                  }

                  node = this.getNode(var1, var2, var3);
                  node.type = var12;
                  node.costMalus = Math.max(node.costMalus, this.mob.getPathfindingMalus(var12));
               }
            }

            if(var12 == BlockPathTypes.OPEN) {
               AABB var16 = new AABB((double)var1 - var14 + 0.5D, (double)var2 + 0.001D, (double)var3 - var14 + 0.5D, (double)var1 + var14 + 0.5D, (double)((float)var2 + this.mob.getBbHeight()), (double)var3 + var14 + 0.5D);
               if(!this.level.noCollision(this.mob, var16)) {
                  return null;
               }

               if(this.mob.getBbWidth() >= 1.0F) {
                  BlockPathTypes var17 = this.getBlockPathType(this.mob, var1, var2 - 1, var3);
                  if(var17 == BlockPathTypes.BLOCKED) {
                     node = this.getNode(var1, var2, var3);
                     node.type = BlockPathTypes.WALKABLE;
                     node.costMalus = Math.max(node.costMalus, var13);
                     return node;
                  }
               }

               int var17 = 0;
               int var18 = var2;

               while(var12 == BlockPathTypes.OPEN) {
                  --var2;
                  if(var2 < 0) {
                     Node var19 = this.getNode(var1, var18, var3);
                     var19.type = BlockPathTypes.BLOCKED;
                     var19.costMalus = -1.0F;
                     return var19;
                  }

                  Node var19 = this.getNode(var1, var2, var3);
                  if(var17++ >= this.mob.getMaxFallDistance()) {
                     var19.type = BlockPathTypes.BLOCKED;
                     var19.costMalus = -1.0F;
                     return var19;
                  }

                  var12 = this.getBlockPathType(this.mob, var1, var2, var3);
                  var13 = this.mob.getPathfindingMalus(var12);
                  if(var12 != BlockPathTypes.OPEN && var13 >= 0.0F) {
                     node = var19;
                     var19.type = var12;
                     var19.costMalus = Math.max(var19.costMalus, var13);
                     break;
                  }

                  if(var13 < 0.0F) {
                     var19.type = BlockPathTypes.BLOCKED;
                     var19.costMalus = -1.0F;
                     return var19;
                  }
               }
            }

            return node;
         }
      }
   }

   public BlockPathTypes getBlockPathType(BlockGetter blockGetter, int var2, int var3, int var4, Mob mob, int var6, int var7, int var8, boolean var9, boolean var10) {
      EnumSet<BlockPathTypes> var11 = EnumSet.noneOf(BlockPathTypes.class);
      BlockPathTypes var12 = BlockPathTypes.BLOCKED;
      double var13 = (double)mob.getBbWidth() / 2.0D;
      BlockPos var15 = new BlockPos(mob);
      var12 = this.getBlockPathTypes(blockGetter, var2, var3, var4, var6, var7, var8, var9, var10, var11, var12, var15);
      if(var11.contains(BlockPathTypes.FENCE)) {
         return BlockPathTypes.FENCE;
      } else {
         BlockPathTypes var16 = BlockPathTypes.BLOCKED;

         for(BlockPathTypes var18 : var11) {
            if(mob.getPathfindingMalus(var18) < 0.0F) {
               return var18;
            }

            if(mob.getPathfindingMalus(var18) >= mob.getPathfindingMalus(var16)) {
               var16 = var18;
            }
         }

         if(var12 == BlockPathTypes.OPEN && mob.getPathfindingMalus(var16) == 0.0F) {
            return BlockPathTypes.OPEN;
         } else {
            return var16;
         }
      }
   }

   public BlockPathTypes getBlockPathTypes(BlockGetter blockGetter, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, boolean var9, EnumSet enumSet, BlockPathTypes var11, BlockPos blockPos) {
      for(int var13 = 0; var13 < var5; ++var13) {
         for(int var14 = 0; var14 < var6; ++var14) {
            for(int var15 = 0; var15 < var7; ++var15) {
               int var16 = var13 + var2;
               int var17 = var14 + var3;
               int var18 = var15 + var4;
               BlockPathTypes var19 = this.getBlockPathType(blockGetter, var16, var17, var18);
               var19 = this.evaluateBlockPathType(blockGetter, var8, var9, blockPos, var19);
               if(var13 == 0 && var14 == 0 && var15 == 0) {
                  var11 = var19;
               }

               enumSet.add(var19);
            }
         }
      }

      return var11;
   }

   protected BlockPathTypes evaluateBlockPathType(BlockGetter blockGetter, boolean var2, boolean var3, BlockPos blockPos, BlockPathTypes var5) {
      if(var5 == BlockPathTypes.DOOR_WOOD_CLOSED && var2 && var3) {
         var5 = BlockPathTypes.WALKABLE;
      }

      if(var5 == BlockPathTypes.DOOR_OPEN && !var3) {
         var5 = BlockPathTypes.BLOCKED;
      }

      if(var5 == BlockPathTypes.RAIL && !(blockGetter.getBlockState(blockPos).getBlock() instanceof BaseRailBlock) && !(blockGetter.getBlockState(blockPos.below()).getBlock() instanceof BaseRailBlock)) {
         var5 = BlockPathTypes.FENCE;
      }

      if(var5 == BlockPathTypes.LEAVES) {
         var5 = BlockPathTypes.BLOCKED;
      }

      return var5;
   }

   private BlockPathTypes getBlockPathType(Mob mob, BlockPos blockPos) {
      return this.getBlockPathType(mob, blockPos.getX(), blockPos.getY(), blockPos.getZ());
   }

   private BlockPathTypes getBlockPathType(Mob mob, int var2, int var3, int var4) {
      return this.getBlockPathType(this.level, var2, var3, var4, mob, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors());
   }

   public BlockPathTypes getBlockPathType(BlockGetter blockGetter, int var2, int var3, int var4) {
      BlockPathTypes blockPathTypes = this.getBlockPathTypeRaw(blockGetter, var2, var3, var4);
      if(blockPathTypes == BlockPathTypes.OPEN && var3 >= 1) {
         Block var6 = blockGetter.getBlockState(new BlockPos(var2, var3 - 1, var4)).getBlock();
         BlockPathTypes var7 = this.getBlockPathTypeRaw(blockGetter, var2, var3 - 1, var4);
         blockPathTypes = var7 != BlockPathTypes.WALKABLE && var7 != BlockPathTypes.OPEN && var7 != BlockPathTypes.WATER && var7 != BlockPathTypes.LAVA?BlockPathTypes.WALKABLE:BlockPathTypes.OPEN;
         if(var7 == BlockPathTypes.DAMAGE_FIRE || var6 == Blocks.MAGMA_BLOCK || var6 == Blocks.CAMPFIRE) {
            blockPathTypes = BlockPathTypes.DAMAGE_FIRE;
         }

         if(var7 == BlockPathTypes.DAMAGE_CACTUS) {
            blockPathTypes = BlockPathTypes.DAMAGE_CACTUS;
         }

         if(var7 == BlockPathTypes.DAMAGE_OTHER) {
            blockPathTypes = BlockPathTypes.DAMAGE_OTHER;
         }
      }

      blockPathTypes = this.checkNeighbourBlocks(blockGetter, var2, var3, var4, blockPathTypes);
      return blockPathTypes;
   }

   public BlockPathTypes checkNeighbourBlocks(BlockGetter blockGetter, int var2, int var3, int var4, BlockPathTypes var5) {
      if(var5 == BlockPathTypes.WALKABLE) {
         BlockPos.PooledMutableBlockPos var6 = BlockPos.PooledMutableBlockPos.acquire();
         Throwable var7 = null;

         try {
            for(int var8 = -1; var8 <= 1; ++var8) {
               for(int var9 = -1; var9 <= 1; ++var9) {
                  if(var8 != 0 || var9 != 0) {
                     Block var10 = blockGetter.getBlockState(var6.set(var8 + var2, var3, var9 + var4)).getBlock();
                     if(var10 == Blocks.CACTUS) {
                        var5 = BlockPathTypes.DANGER_CACTUS;
                     } else if(var10 == Blocks.FIRE) {
                        var5 = BlockPathTypes.DANGER_FIRE;
                     } else if(var10 == Blocks.SWEET_BERRY_BUSH) {
                        var5 = BlockPathTypes.DANGER_OTHER;
                     }
                  }
               }
            }
         } catch (Throwable var18) {
            var7 = var18;
            throw var18;
         } finally {
            if(var6 != null) {
               if(var7 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var17) {
                     var7.addSuppressed(var17);
                  }
               } else {
                  var6.close();
               }
            }

         }
      }

      return var5;
   }

   protected BlockPathTypes getBlockPathTypeRaw(BlockGetter blockGetter, int var2, int var3, int var4) {
      BlockPos var5 = new BlockPos(var2, var3, var4);
      BlockState var6 = blockGetter.getBlockState(var5);
      Block var7 = var6.getBlock();
      Material var8 = var6.getMaterial();
      if(var6.isAir()) {
         return BlockPathTypes.OPEN;
      } else if(!var7.is(BlockTags.TRAPDOORS) && var7 != Blocks.LILY_PAD) {
         if(var7 == Blocks.FIRE) {
            return BlockPathTypes.DAMAGE_FIRE;
         } else if(var7 == Blocks.CACTUS) {
            return BlockPathTypes.DAMAGE_CACTUS;
         } else if(var7 == Blocks.SWEET_BERRY_BUSH) {
            return BlockPathTypes.DAMAGE_OTHER;
         } else if(var7 instanceof DoorBlock && var8 == Material.WOOD && !((Boolean)var6.getValue(DoorBlock.OPEN)).booleanValue()) {
            return BlockPathTypes.DOOR_WOOD_CLOSED;
         } else if(var7 instanceof DoorBlock && var8 == Material.METAL && !((Boolean)var6.getValue(DoorBlock.OPEN)).booleanValue()) {
            return BlockPathTypes.DOOR_IRON_CLOSED;
         } else if(var7 instanceof DoorBlock && ((Boolean)var6.getValue(DoorBlock.OPEN)).booleanValue()) {
            return BlockPathTypes.DOOR_OPEN;
         } else if(var7 instanceof BaseRailBlock) {
            return BlockPathTypes.RAIL;
         } else if(var7 instanceof LeavesBlock) {
            return BlockPathTypes.LEAVES;
         } else if(!var7.is(BlockTags.FENCES) && !var7.is(BlockTags.WALLS) && (!(var7 instanceof FenceGateBlock) || ((Boolean)var6.getValue(FenceGateBlock.OPEN)).booleanValue())) {
            FluidState var9 = blockGetter.getFluidState(var5);
            return var9.is(FluidTags.WATER)?BlockPathTypes.WATER:(var9.is(FluidTags.LAVA)?BlockPathTypes.LAVA:(var6.isPathfindable(blockGetter, var5, PathComputationType.LAND)?BlockPathTypes.OPEN:BlockPathTypes.BLOCKED));
         } else {
            return BlockPathTypes.FENCE;
         }
      } else {
         return BlockPathTypes.TRAPDOOR;
      }
   }
}
