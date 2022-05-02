package net.minecraft.world.level.pathfinder;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.Target;

public class SwimNodeEvaluator extends NodeEvaluator {
   private final boolean allowBreaching;

   public SwimNodeEvaluator(boolean allowBreaching) {
      this.allowBreaching = allowBreaching;
   }

   public Node getStart() {
      return super.getNode(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY + 0.5D), Mth.floor(this.mob.getBoundingBox().minZ));
   }

   public Target getGoal(double var1, double var3, double var5) {
      return new Target(super.getNode(Mth.floor(var1 - (double)(this.mob.getBbWidth() / 2.0F)), Mth.floor(var3 + 0.5D), Mth.floor(var5 - (double)(this.mob.getBbWidth() / 2.0F))));
   }

   public int getNeighbors(Node[] nodes, Node var2) {
      int var3 = 0;

      for(Direction var7 : Direction.values()) {
         Node var8 = this.getWaterNode(var2.x + var7.getStepX(), var2.y + var7.getStepY(), var2.z + var7.getStepZ());
         if(var8 != null && !var8.closed) {
            nodes[var3++] = var8;
         }
      }

      return var3;
   }

   public BlockPathTypes getBlockPathType(BlockGetter blockGetter, int var2, int var3, int var4, Mob mob, int var6, int var7, int var8, boolean var9, boolean var10) {
      return this.getBlockPathType(blockGetter, var2, var3, var4);
   }

   public BlockPathTypes getBlockPathType(BlockGetter blockGetter, int var2, int var3, int var4) {
      BlockPos var5 = new BlockPos(var2, var3, var4);
      FluidState var6 = blockGetter.getFluidState(var5);
      BlockState var7 = blockGetter.getBlockState(var5);
      return var6.isEmpty() && var7.isPathfindable(blockGetter, var5.below(), PathComputationType.WATER) && var7.isAir()?BlockPathTypes.BREACH:(var6.is(FluidTags.WATER) && var7.isPathfindable(blockGetter, var5, PathComputationType.WATER)?BlockPathTypes.WATER:BlockPathTypes.BLOCKED);
   }

   @Nullable
   private Node getWaterNode(int var1, int var2, int var3) {
      BlockPathTypes var4 = this.isFree(var1, var2, var3);
      return (!this.allowBreaching || var4 != BlockPathTypes.BREACH) && var4 != BlockPathTypes.WATER?null:this.getNode(var1, var2, var3);
   }

   @Nullable
   protected Node getNode(int var1, int var2, int var3) {
      Node node = null;
      BlockPathTypes var5 = this.getBlockPathType(this.mob.level, var1, var2, var3);
      float var6 = this.mob.getPathfindingMalus(var5);
      if(var6 >= 0.0F) {
         node = super.getNode(var1, var2, var3);
         node.type = var5;
         node.costMalus = Math.max(node.costMalus, var6);
         if(this.level.getFluidState(new BlockPos(var1, var2, var3)).isEmpty()) {
            node.costMalus += 8.0F;
         }
      }

      return var5 == BlockPathTypes.OPEN?node:node;
   }

   private BlockPathTypes isFree(int var1, int var2, int var3) {
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();

      for(int var5 = var1; var5 < var1 + this.entityWidth; ++var5) {
         for(int var6 = var2; var6 < var2 + this.entityHeight; ++var6) {
            for(int var7 = var3; var7 < var3 + this.entityDepth; ++var7) {
               FluidState var8 = this.level.getFluidState(var4.set(var5, var6, var7));
               BlockState var9 = this.level.getBlockState(var4.set(var5, var6, var7));
               if(var8.isEmpty() && var9.isPathfindable(this.level, var4.below(), PathComputationType.WATER) && var9.isAir()) {
                  return BlockPathTypes.BREACH;
               }

               if(!var8.is(FluidTags.WATER)) {
                  return BlockPathTypes.BLOCKED;
               }
            }
         }
      }

      BlockState var5 = this.level.getBlockState(var4);
      if(var5.isPathfindable(this.level, var4, PathComputationType.WATER)) {
         return BlockPathTypes.WATER;
      } else {
         return BlockPathTypes.BLOCKED;
      }
   }
}
