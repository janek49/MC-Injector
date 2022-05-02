package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class TemptGoal extends Goal {
   private static final TargetingConditions TEMP_TARGETING = (new TargetingConditions()).range(10.0D).allowInvulnerable().allowSameTeam().allowNonAttackable().allowUnseeable();
   protected final PathfinderMob mob;
   private final double speedModifier;
   private double px;
   private double py;
   private double pz;
   private double pRotX;
   private double pRotY;
   protected Player player;
   private int calmDown;
   private boolean isRunning;
   private final Ingredient items;
   private final boolean canScare;

   public TemptGoal(PathfinderMob pathfinderMob, double var2, Ingredient ingredient, boolean var5) {
      this(pathfinderMob, var2, var5, ingredient);
   }

   public TemptGoal(PathfinderMob mob, double speedModifier, boolean canScare, Ingredient items) {
      this.mob = mob;
      this.speedModifier = speedModifier;
      this.items = items;
      this.canScare = canScare;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      if(!(mob.getNavigation() instanceof GroundPathNavigation)) {
         throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
      }
   }

   public boolean canUse() {
      if(this.calmDown > 0) {
         --this.calmDown;
         return false;
      } else {
         this.player = this.mob.level.getNearestPlayer(TEMP_TARGETING, this.mob);
         return this.player == null?false:this.shouldFollowItem(this.player.getMainHandItem()) || this.shouldFollowItem(this.player.getOffhandItem());
      }
   }

   protected boolean shouldFollowItem(ItemStack itemStack) {
      return this.items.test(itemStack);
   }

   public boolean canContinueToUse() {
      if(this.canScare()) {
         if(this.mob.distanceToSqr(this.player) < 36.0D) {
            if(this.player.distanceToSqr(this.px, this.py, this.pz) > 0.010000000000000002D) {
               return false;
            }

            if(Math.abs((double)this.player.xRot - this.pRotX) > 5.0D || Math.abs((double)this.player.yRot - this.pRotY) > 5.0D) {
               return false;
            }
         } else {
            this.px = this.player.x;
            this.py = this.player.y;
            this.pz = this.player.z;
         }

         this.pRotX = (double)this.player.xRot;
         this.pRotY = (double)this.player.yRot;
      }

      return this.canUse();
   }

   protected boolean canScare() {
      return this.canScare;
   }

   public void start() {
      this.px = this.player.x;
      this.py = this.player.y;
      this.pz = this.player.z;
      this.isRunning = true;
   }

   public void stop() {
      this.player = null;
      this.mob.getNavigation().stop();
      this.calmDown = 100;
      this.isRunning = false;
   }

   public void tick() {
      this.mob.getLookControl().setLookAt(this.player, (float)(this.mob.getMaxHeadYRot() + 20), (float)this.mob.getMaxHeadXRot());
      if(this.mob.distanceToSqr(this.player) < 6.25D) {
         this.mob.getNavigation().stop();
      } else {
         this.mob.getNavigation().moveTo((Entity)this.player, this.speedModifier);
      }

   }

   public boolean isRunning() {
      return this.isRunning;
   }
}
