package net.minecraft.world.level.pathfinder;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Target;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class FlyNodeEvaluator extends WalkNodeEvaluator {
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

         for(Block var3 = this.level.getBlockState(var2).getBlock(); var3 == Blocks.WATER; var3 = this.level.getBlockState(var2).getBlock()) {
            ++var1;
            var2.set(this.mob.x, (double)var1, this.mob.z);
         }
      } else {
         var1 = Mth.floor(this.mob.getBoundingBox().minY + 0.5D);
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
               return super.getNode(var6.getX(), var6.getY(), var6.getZ());
            }
         }
      }

      return super.getNode(var2.getX(), var1, var2.getZ());
   }

   public Target getGoal(double var1, double var3, double var5) {
      return new Target(super.getNode(Mth.floor(var1), Mth.floor(var3), Mth.floor(var5)));
   }

   public int getNeighbors(Node[] nodes, Node var2) {
      int var3 = 0;
      Node var4 = this.getNode(var2.x, var2.y, var2.z + 1);
      Node var5 = this.getNode(var2.x - 1, var2.y, var2.z);
      Node var6 = this.getNode(var2.x + 1, var2.y, var2.z);
      Node var7 = this.getNode(var2.x, var2.y, var2.z - 1);
      Node var8 = this.getNode(var2.x, var2.y + 1, var2.z);
      Node var9 = this.getNode(var2.x, var2.y - 1, var2.z);
      if(var4 != null && !var4.closed) {
         nodes[var3++] = var4;
      }

      if(var5 != null && !var5.closed) {
         nodes[var3++] = var5;
      }

      if(var6 != null && !var6.closed) {
         nodes[var3++] = var6;
      }

      if(var7 != null && !var7.closed) {
         nodes[var3++] = var7;
      }

      if(var8 != null && !var8.closed) {
         nodes[var3++] = var8;
      }

      if(var9 != null && !var9.closed) {
         nodes[var3++] = var9;
      }

      boolean var10 = var7 == null || var7.costMalus != 0.0F;
      boolean var11 = var4 == null || var4.costMalus != 0.0F;
      boolean var12 = var6 == null || var6.costMalus != 0.0F;
      boolean var13 = var5 == null || var5.costMalus != 0.0F;
      boolean var14 = var8 == null || var8.costMalus != 0.0F;
      boolean var15 = var9 == null || var9.costMalus != 0.0F;
      if(var10 && var13) {
         Node var16 = this.getNode(var2.x - 1, var2.y, var2.z - 1);
         if(var16 != null && !var16.closed) {
            nodes[var3++] = var16;
         }
      }

      if(var10 && var12) {
         Node var16 = this.getNode(var2.x + 1, var2.y, var2.z - 1);
         if(var16 != null && !var16.closed) {
            nodes[var3++] = var16;
         }
      }

      if(var11 && var13) {
         Node var16 = this.getNode(var2.x - 1, var2.y, var2.z + 1);
         if(var16 != null && !var16.closed) {
            nodes[var3++] = var16;
         }
      }

      if(var11 && var12) {
         Node var16 = this.getNode(var2.x + 1, var2.y, var2.z + 1);
         if(var16 != null && !var16.closed) {
            nodes[var3++] = var16;
         }
      }

      if(var10 && var14) {
         Node var16 = this.getNode(var2.x, var2.y + 1, var2.z - 1);
         if(var16 != null && !var16.closed) {
            nodes[var3++] = var16;
         }
      }

      if(var11 && var14) {
         Node var16 = this.getNode(var2.x, var2.y + 1, var2.z + 1);
         if(var16 != null && !var16.closed) {
            nodes[var3++] = var16;
         }
      }

      if(var12 && var14) {
         Node var16 = this.getNode(var2.x + 1, var2.y + 1, var2.z);
         if(var16 != null && !var16.closed) {
            nodes[var3++] = var16;
         }
      }

      if(var13 && var14) {
         Node var16 = this.getNode(var2.x - 1, var2.y + 1, var2.z);
         if(var16 != null && !var16.closed) {
            nodes[var3++] = var16;
         }
      }

      if(var10 && var15) {
         Node var16 = this.getNode(var2.x, var2.y - 1, var2.z - 1);
         if(var16 != null && !var16.closed) {
            nodes[var3++] = var16;
         }
      }

      if(var11 && var15) {
         Node var16 = this.getNode(var2.x, var2.y - 1, var2.z + 1);
         if(var16 != null && !var16.closed) {
            nodes[var3++] = var16;
         }
      }

      if(var12 && var15) {
         Node var16 = this.getNode(var2.x + 1, var2.y - 1, var2.z);
         if(var16 != null && !var16.closed) {
            nodes[var3++] = var16;
         }
      }

      if(var13 && var15) {
         Node var16 = this.getNode(var2.x - 1, var2.y - 1, var2.z);
         if(var16 != null && !var16.closed) {
            nodes[var3++] = var16;
         }
      }

      return var3;
   }

   @Nullable
   protected Node getNode(int var1, int var2, int var3) {
      Node node = null;
      BlockPathTypes var5 = this.getBlockPathType(this.mob, var1, var2, var3);
      float var6 = this.mob.getPathfindingMalus(var5);
      if(var6 >= 0.0F) {
         node = super.getNode(var1, var2, var3);
         node.type = var5;
         node.costMalus = Math.max(node.costMalus, var6);
         if(var5 == BlockPathTypes.WALKABLE) {
            ++node.costMalus;
         }
      }

      return var5 != BlockPathTypes.OPEN && var5 != BlockPathTypes.WALKABLE?node:node;
   }

   public BlockPathTypes getBlockPathType(BlockGetter blockGetter, int var2, int var3, int var4, Mob mob, int var6, int var7, int var8, boolean var9, boolean var10) {
      EnumSet<BlockPathTypes> var11 = EnumSet.noneOf(BlockPathTypes.class);
      BlockPathTypes var12 = BlockPathTypes.BLOCKED;
      BlockPos var13 = new BlockPos(mob);
      var12 = this.getBlockPathTypes(blockGetter, var2, var3, var4, var6, var7, var8, var9, var10, var11, var12, var13);
      if(var11.contains(BlockPathTypes.FENCE)) {
         return BlockPathTypes.FENCE;
      } else {
         BlockPathTypes var14 = BlockPathTypes.BLOCKED;

         for(BlockPathTypes var16 : var11) {
            if(mob.getPathfindingMalus(var16) < 0.0F) {
               return var16;
            }

            if(mob.getPathfindingMalus(var16) >= mob.getPathfindingMalus(var14)) {
               var14 = var16;
            }
         }

         if(var12 == BlockPathTypes.OPEN && mob.getPathfindingMalus(var14) == 0.0F) {
            return BlockPathTypes.OPEN;
         } else {
            return var14;
         }
      }
   }

   public BlockPathTypes getBlockPathType(BlockGetter blockGetter, int var2, int var3, int var4) {
      BlockPathTypes blockPathTypes = this.getBlockPathTypeRaw(blockGetter, var2, var3, var4);
      if(blockPathTypes == BlockPathTypes.OPEN && var3 >= 1) {
         Block var6 = blockGetter.getBlockState(new BlockPos(var2, var3 - 1, var4)).getBlock();
         BlockPathTypes var7 = this.getBlockPathTypeRaw(blockGetter, var2, var3 - 1, var4);
         if(var7 != BlockPathTypes.DAMAGE_FIRE && var6 != Blocks.MAGMA_BLOCK && var7 != BlockPathTypes.LAVA && var6 != Blocks.CAMPFIRE) {
            if(var7 == BlockPathTypes.DAMAGE_CACTUS) {
               blockPathTypes = BlockPathTypes.DAMAGE_CACTUS;
            } else if(var7 == BlockPathTypes.DAMAGE_OTHER) {
               blockPathTypes = BlockPathTypes.DAMAGE_OTHER;
            } else {
               blockPathTypes = var7 != BlockPathTypes.WALKABLE && var7 != BlockPathTypes.OPEN && var7 != BlockPathTypes.WATER?BlockPathTypes.WALKABLE:BlockPathTypes.OPEN;
            }
         } else {
            blockPathTypes = BlockPathTypes.DAMAGE_FIRE;
         }
      }

      blockPathTypes = this.checkNeighbourBlocks(blockGetter, var2, var3, var4, blockPathTypes);
      return blockPathTypes;
   }

   private BlockPathTypes getBlockPathType(Mob mob, BlockPos blockPos) {
      return this.getBlockPathType(mob, blockPos.getX(), blockPos.getY(), blockPos.getZ());
   }

   private BlockPathTypes getBlockPathType(Mob mob, int var2, int var3, int var4) {
      return this.getBlockPathType(this.level, var2, var3, var4, mob, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors());
   }
}
