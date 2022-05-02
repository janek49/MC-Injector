package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.level.block.state.BlockState;

public class FollowOwnerFlyingGoal extends FollowOwnerGoal {
   public FollowOwnerFlyingGoal(TamableAnimal tamableAnimal, double var2, float var4, float var5) {
      super(tamableAnimal, var2, var4, var5);
   }

   protected boolean isTeleportFriendlyBlock(BlockPos blockPos) {
      BlockState var2 = this.level.getBlockState(blockPos);
      return (var2.entityCanStandOn(this.level, blockPos, this.tamable) || var2.is(BlockTags.LEAVES)) && this.level.isEmptyBlock(blockPos.above()) && this.level.isEmptyBlock(blockPos.above(2));
   }
}
