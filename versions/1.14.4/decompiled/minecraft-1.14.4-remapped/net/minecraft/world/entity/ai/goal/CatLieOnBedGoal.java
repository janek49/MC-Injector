package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.LevelReader;

public class CatLieOnBedGoal extends MoveToBlockGoal {
   private final Cat cat;

   public CatLieOnBedGoal(Cat cat, double var2, int var4) {
      super(cat, var2, var4, 6);
      this.cat = cat;
      this.verticalSearchStart = -2;
      this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
   }

   public boolean canUse() {
      return this.cat.isTame() && !this.cat.isSitting() && !this.cat.isLying() && super.canUse();
   }

   public void start() {
      super.start();
      this.cat.getSitGoal().wantToSit(false);
   }

   protected int nextStartTick(PathfinderMob pathfinderMob) {
      return 40;
   }

   public void stop() {
      super.stop();
      this.cat.setLying(false);
   }

   public void tick() {
      super.tick();
      this.cat.getSitGoal().wantToSit(false);
      if(!this.isReachedTarget()) {
         this.cat.setLying(false);
      } else if(!this.cat.isLying()) {
         this.cat.setLying(true);
      }

   }

   protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
      return levelReader.isEmptyBlock(blockPos.above()) && levelReader.getBlockState(blockPos).getBlock().is(BlockTags.BEDS);
   }
}
