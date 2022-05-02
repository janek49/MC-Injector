package net.minecraft.world.level.pathfinder;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Target;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TurtleNodeEvaluator extends WalkNodeEvaluator {
   private float oldWalkableCost;
   private float oldWaterBorderCost;

   public void prepare(LevelReader levelReader, Mob mob) {
      super.prepare(levelReader, mob);
      mob.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
      this.oldWalkableCost = mob.getPathfindingMalus(BlockPathTypes.WALKABLE);
      mob.setPathfindingMalus(BlockPathTypes.WALKABLE, 6.0F);
      this.oldWaterBorderCost = mob.getPathfindingMalus(BlockPathTypes.WATER_BORDER);
      mob.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 4.0F);
   }

   public void done() {
      this.mob.setPathfindingMalus(BlockPathTypes.WALKABLE, this.oldWalkableCost);
      this.mob.setPathfindingMalus(BlockPathTypes.WATER_BORDER, this.oldWaterBorderCost);
      super.done();
   }

   public Node getStart() {
      return this.getNode(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5D), Mth.floor(this.mob.getBoundingBox().minZ));
   }

   public Target getGoal(double var1, double var3, double var5) {
      return new Target(this.getNode(Mth.floor(var1), Mth.floor(var3 + 0.5D), Mth.floor(var5)));
   }

   public int getNeighbors(Node[] nodes, Node var2) {
      int var3 = 0;
      int var4 = 1;
      BlockPos var5 = new BlockPos(var2.x, var2.y, var2.z);
      double var6 = this.inWaterDependentPosHeight(var5);
      Node var8 = this.getAcceptedNode(var2.x, var2.y, var2.z + 1, 1, var6);
      Node var9 = this.getAcceptedNode(var2.x - 1, var2.y, var2.z, 1, var6);
      Node var10 = this.getAcceptedNode(var2.x + 1, var2.y, var2.z, 1, var6);
      Node var11 = this.getAcceptedNode(var2.x, var2.y, var2.z - 1, 1, var6);
      Node var12 = this.getAcceptedNode(var2.x, var2.y + 1, var2.z, 0, var6);
      Node var13 = this.getAcceptedNode(var2.x, var2.y - 1, var2.z, 1, var6);
      if(var8 != null && !var8.closed) {
         nodes[var3++] = var8;
      }

      if(var9 != null && !var9.closed) {
         nodes[var3++] = var9;
      }

      if(var10 != null && !var10.closed) {
         nodes[var3++] = var10;
      }

      if(var11 != null && !var11.closed) {
         nodes[var3++] = var11;
      }

      if(var12 != null && !var12.closed) {
         nodes[var3++] = var12;
      }

      if(var13 != null && !var13.closed) {
         nodes[var3++] = var13;
      }

      boolean var14 = var11 == null || var11.type == BlockPathTypes.OPEN || var11.costMalus != 0.0F;
      boolean var15 = var8 == null || var8.type == BlockPathTypes.OPEN || var8.costMalus != 0.0F;
      boolean var16 = var10 == null || var10.type == BlockPathTypes.OPEN || var10.costMalus != 0.0F;
      boolean var17 = var9 == null || var9.type == BlockPathTypes.OPEN || var9.costMalus != 0.0F;
      if(var14 && var17) {
         Node var18 = this.getAcceptedNode(var2.x - 1, var2.y, var2.z - 1, 1, var6);
         if(var18 != null && !var18.closed) {
            nodes[var3++] = var18;
         }
      }

      if(var14 && var16) {
         Node var18 = this.getAcceptedNode(var2.x + 1, var2.y, var2.z - 1, 1, var6);
         if(var18 != null && !var18.closed) {
            nodes[var3++] = var18;
         }
      }

      if(var15 && var17) {
         Node var18 = this.getAcceptedNode(var2.x - 1, var2.y, var2.z + 1, 1, var6);
         if(var18 != null && !var18.closed) {
            nodes[var3++] = var18;
         }
      }

      if(var15 && var16) {
         Node var18 = this.getAcceptedNode(var2.x + 1, var2.y, var2.z + 1, 1, var6);
         if(var18 != null && !var18.closed) {
            nodes[var3++] = var18;
         }
      }

      return var3;
   }

   private double inWaterDependentPosHeight(BlockPos blockPos) {
      if(!this.mob.isInWater()) {
         BlockPos blockPos = blockPos.below();
         VoxelShape var3 = this.level.getBlockState(blockPos).getCollisionShape(this.level, blockPos);
         return (double)blockPos.getY() + (var3.isEmpty()?0.0D:var3.max(Direction.Axis.Y));
      } else {
         return (double)blockPos.getY() + 0.5D;
      }
   }

   @Nullable
   private Node getAcceptedNode(int var1, int var2, int var3, int var4, double var5) {
      Node node = null;
      BlockPos var8 = new BlockPos(var1, var2, var3);
      double var9 = this.inWaterDependentPosHeight(var8);
      if(var9 - var5 > 1.125D) {
         return null;
      } else {
         BlockPathTypes var11 = this.getBlockPathType(this.level, var1, var2, var3, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, false, false);
         float var12 = this.mob.getPathfindingMalus(var11);
         double var13 = (double)this.mob.getBbWidth() / 2.0D;
         if(var12 >= 0.0F) {
            node = this.getNode(var1, var2, var3);
            node.type = var11;
            node.costMalus = Math.max(node.costMalus, var12);
         }

         if(var11 != BlockPathTypes.WATER && var11 != BlockPathTypes.WALKABLE) {
            if(node == null && var4 > 0 && var11 != BlockPathTypes.FENCE && var11 != BlockPathTypes.TRAPDOOR) {
               node = this.getAcceptedNode(var1, var2 + 1, var3, var4 - 1, var5);
            }

            if(var11 == BlockPathTypes.OPEN) {
               AABB var15 = new AABB((double)var1 - var13 + 0.5D, (double)var2 + 0.001D, (double)var3 - var13 + 0.5D, (double)var1 + var13 + 0.5D, (double)((float)var2 + this.mob.getBbHeight()), (double)var3 + var13 + 0.5D);
               if(!this.mob.level.noCollision(this.mob, var15)) {
                  return null;
               }

               BlockPathTypes var16 = this.getBlockPathType(this.level, var1, var2 - 1, var3, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, false, false);
               if(var16 == BlockPathTypes.BLOCKED) {
                  node = this.getNode(var1, var2, var3);
                  node.type = BlockPathTypes.WALKABLE;
                  node.costMalus = Math.max(node.costMalus, var12);
                  return node;
               }

               if(var16 == BlockPathTypes.WATER) {
                  node = this.getNode(var1, var2, var3);
                  node.type = BlockPathTypes.WATER;
                  node.costMalus = Math.max(node.costMalus, var12);
                  return node;
               }

               int var17 = 0;

               while(var2 > 0 && var11 == BlockPathTypes.OPEN) {
                  --var2;
                  if(var17++ >= this.mob.getMaxFallDistance()) {
                     return null;
                  }

                  var11 = this.getBlockPathType(this.level, var1, var2, var3, this.mob, this.entityWidth, this.entityHeight, this.entityDepth, false, false);
                  var12 = this.mob.getPathfindingMalus(var11);
                  if(var11 != BlockPathTypes.OPEN && var12 >= 0.0F) {
                     node = this.getNode(var1, var2, var3);
                     node.type = var11;
                     node.costMalus = Math.max(node.costMalus, var12);
                     break;
                  }

                  if(var12 < 0.0F) {
                     return null;
                  }
               }
            }

            return node;
         } else {
            if(var2 < this.mob.level.getSeaLevel() - 10 && node != null) {
               ++node.costMalus;
            }

            return node;
         }
      }
   }

   protected BlockPathTypes evaluateBlockPathType(BlockGetter blockGetter, boolean var2, boolean var3, BlockPos blockPos, BlockPathTypes var5) {
      if(var5 == BlockPathTypes.RAIL && !(blockGetter.getBlockState(blockPos).getBlock() instanceof BaseRailBlock) && !(blockGetter.getBlockState(blockPos.below()).getBlock() instanceof BaseRailBlock)) {
         var5 = BlockPathTypes.FENCE;
      }

      if(var5 == BlockPathTypes.DOOR_OPEN || var5 == BlockPathTypes.DOOR_WOOD_CLOSED || var5 == BlockPathTypes.DOOR_IRON_CLOSED) {
         var5 = BlockPathTypes.BLOCKED;
      }

      if(var5 == BlockPathTypes.LEAVES) {
         var5 = BlockPathTypes.BLOCKED;
      }

      return var5;
   }

   public BlockPathTypes getBlockPathType(BlockGetter blockGetter, int var2, int var3, int var4) {
      BlockPathTypes blockPathTypes = this.getBlockPathTypeRaw(blockGetter, var2, var3, var4);
      if(blockPathTypes == BlockPathTypes.WATER) {
         for(Direction var9 : Direction.values()) {
            BlockPathTypes var10 = this.getBlockPathTypeRaw(blockGetter, var2 + var9.getStepX(), var3 + var9.getStepY(), var4 + var9.getStepZ());
            if(var10 == BlockPathTypes.BLOCKED) {
               return BlockPathTypes.WATER_BORDER;
            }
         }

         return BlockPathTypes.WATER;
      } else {
         if(blockPathTypes == BlockPathTypes.OPEN && var3 >= 1) {
            Block var6 = blockGetter.getBlockState(new BlockPos(var2, var3 - 1, var4)).getBlock();
            BlockPathTypes var7 = this.getBlockPathTypeRaw(blockGetter, var2, var3 - 1, var4);
            if(var7 != BlockPathTypes.WALKABLE && var7 != BlockPathTypes.OPEN && var7 != BlockPathTypes.LAVA) {
               blockPathTypes = BlockPathTypes.WALKABLE;
            } else {
               blockPathTypes = BlockPathTypes.OPEN;
            }

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
   }
}
