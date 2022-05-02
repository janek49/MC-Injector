package net.minecraft.world.entity.ai.goal;

import java.util.function.Predicate;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.DoorInteractGoal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;

public class BreakDoorGoal extends DoorInteractGoal {
   private final Predicate validDifficulties;
   protected int breakTime;
   protected int lastBreakProgress;
   protected int doorBreakTime;

   public BreakDoorGoal(Mob mob, Predicate validDifficulties) {
      super(mob);
      this.lastBreakProgress = -1;
      this.doorBreakTime = -1;
      this.validDifficulties = validDifficulties;
   }

   public BreakDoorGoal(Mob mob, int doorBreakTime, Predicate predicate) {
      this(mob, predicate);
      this.doorBreakTime = doorBreakTime;
   }

   protected int getDoorBreakTime() {
      return Math.max(240, this.doorBreakTime);
   }

   public boolean canUse() {
      return !super.canUse()?false:(!this.mob.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)?false:this.isValidDifficulty(this.mob.level.getDifficulty()) && !this.isOpen());
   }

   public void start() {
      super.start();
      this.breakTime = 0;
   }

   public boolean canContinueToUse() {
      return this.breakTime <= this.getDoorBreakTime() && !this.isOpen() && this.doorPos.closerThan(this.mob.position(), 2.0D) && this.isValidDifficulty(this.mob.level.getDifficulty());
   }

   public void stop() {
      super.stop();
      this.mob.level.destroyBlockProgress(this.mob.getId(), this.doorPos, -1);
   }

   public void tick() {
      super.tick();
      if(this.mob.getRandom().nextInt(20) == 0) {
         this.mob.level.levelEvent(1019, this.doorPos, 0);
         if(!this.mob.swinging) {
            this.mob.swing(this.mob.getUsedItemHand());
         }
      }

      ++this.breakTime;
      int var1 = (int)((float)this.breakTime / (float)this.getDoorBreakTime() * 10.0F);
      if(var1 != this.lastBreakProgress) {
         this.mob.level.destroyBlockProgress(this.mob.getId(), this.doorPos, var1);
         this.lastBreakProgress = var1;
      }

      if(this.breakTime == this.getDoorBreakTime() && this.isValidDifficulty(this.mob.level.getDifficulty())) {
         this.mob.level.removeBlock(this.doorPos, false);
         this.mob.level.levelEvent(1021, this.doorPos, 0);
         this.mob.level.levelEvent(2001, this.doorPos, Block.getId(this.mob.level.getBlockState(this.doorPos)));
      }

   }

   private boolean isValidDifficulty(Difficulty difficulty) {
      return this.validDifficulties.test(difficulty);
   }
}
