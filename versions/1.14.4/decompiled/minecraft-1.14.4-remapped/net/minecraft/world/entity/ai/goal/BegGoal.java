package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class BegGoal extends Goal {
   private final Wolf wolf;
   private Player player;
   private final Level level;
   private final float lookDistance;
   private int lookTime;
   private final TargetingConditions begTargeting;

   public BegGoal(Wolf wolf, float lookDistance) {
      this.wolf = wolf;
      this.level = wolf.level;
      this.lookDistance = lookDistance;
      this.begTargeting = (new TargetingConditions()).range((double)lookDistance).allowInvulnerable().allowSameTeam().allowNonAttackable();
      this.setFlags(EnumSet.of(Goal.Flag.LOOK));
   }

   public boolean canUse() {
      this.player = this.level.getNearestPlayer(this.begTargeting, this.wolf);
      return this.player == null?false:this.playerHoldingInteresting(this.player);
   }

   public boolean canContinueToUse() {
      return !this.player.isAlive()?false:(this.wolf.distanceToSqr(this.player) > (double)(this.lookDistance * this.lookDistance)?false:this.lookTime > 0 && this.playerHoldingInteresting(this.player));
   }

   public void start() {
      this.wolf.setIsInterested(true);
      this.lookTime = 40 + this.wolf.getRandom().nextInt(40);
   }

   public void stop() {
      this.wolf.setIsInterested(false);
      this.player = null;
   }

   public void tick() {
      this.wolf.getLookControl().setLookAt(this.player.x, this.player.y + (double)this.player.getEyeHeight(), this.player.z, 10.0F, (float)this.wolf.getMaxHeadXRot());
      --this.lookTime;
   }

   private boolean playerHoldingInteresting(Player player) {
      for(InteractionHand var5 : InteractionHand.values()) {
         ItemStack var6 = player.getItemInHand(var5);
         if(this.wolf.isTame() && var6.getItem() == Items.BONE) {
            return true;
         }

         if(this.wolf.isFood(var6)) {
            return true;
         }
      }

      return false;
   }
}
