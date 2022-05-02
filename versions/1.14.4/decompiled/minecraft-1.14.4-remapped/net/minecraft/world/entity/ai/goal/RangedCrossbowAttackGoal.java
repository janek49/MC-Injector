package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RangedCrossbowAttackGoal extends Goal {
   private final Monster mob;
   private RangedCrossbowAttackGoal.CrossbowState crossbowState = RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
   private final double speedModifier;
   private final float attackRadiusSqr;
   private int seeTime;
   private int attackDelay;

   public RangedCrossbowAttackGoal(Monster mob, double speedModifier, float var4) {
      this.mob = mob;
      this.speedModifier = speedModifier;
      this.attackRadiusSqr = var4 * var4;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   public boolean canUse() {
      return this.isValidTarget() && this.isHoldingCrossbow();
   }

   private boolean isHoldingCrossbow() {
      return this.mob.isHolding(Items.CROSSBOW);
   }

   public boolean canContinueToUse() {
      return this.isValidTarget() && (this.canUse() || !this.mob.getNavigation().isDone()) && this.isHoldingCrossbow();
   }

   private boolean isValidTarget() {
      return this.mob.getTarget() != null && this.mob.getTarget().isAlive();
   }

   public void stop() {
      super.stop();
      this.mob.setAggressive(false);
      this.mob.setTarget((LivingEntity)null);
      this.seeTime = 0;
      if(this.mob.isUsingItem()) {
         this.mob.stopUsingItem();
         ((CrossbowAttackMob)this.mob).setChargingCrossbow(false);
         CrossbowItem.setCharged(this.mob.getUseItem(), false);
      }

   }

   public void tick() {
      LivingEntity var1 = this.mob.getTarget();
      if(var1 != null) {
         boolean var2 = this.mob.getSensing().canSee(var1);
         boolean var3 = this.seeTime > 0;
         if(var2 != var3) {
            this.seeTime = 0;
         }

         if(var2) {
            ++this.seeTime;
         } else {
            --this.seeTime;
         }

         double var4 = this.mob.distanceToSqr(var1);
         boolean var6 = (var4 > (double)this.attackRadiusSqr || this.seeTime < 5) && this.attackDelay == 0;
         if(var6) {
            this.mob.getNavigation().moveTo((Entity)var1, this.canRun()?this.speedModifier:this.speedModifier * 0.5D);
         } else {
            this.mob.getNavigation().stop();
         }

         this.mob.getLookControl().setLookAt(var1, 30.0F, 30.0F);
         if(this.crossbowState == RangedCrossbowAttackGoal.CrossbowState.UNCHARGED) {
            if(!var6) {
               this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, Items.CROSSBOW));
               this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.CHARGING;
               ((CrossbowAttackMob)this.mob).setChargingCrossbow(true);
            }
         } else if(this.crossbowState == RangedCrossbowAttackGoal.CrossbowState.CHARGING) {
            if(!this.mob.isUsingItem()) {
               this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
            }

            int var7 = this.mob.getTicksUsingItem();
            ItemStack var8 = this.mob.getUseItem();
            if(var7 >= CrossbowItem.getChargeDuration(var8)) {
               this.mob.releaseUsingItem();
               this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.CHARGED;
               this.attackDelay = 20 + this.mob.getRandom().nextInt(20);
               ((CrossbowAttackMob)this.mob).setChargingCrossbow(false);
            }
         } else if(this.crossbowState == RangedCrossbowAttackGoal.CrossbowState.CHARGED) {
            --this.attackDelay;
            if(this.attackDelay == 0) {
               this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.READY_TO_ATTACK;
            }
         } else if(this.crossbowState == RangedCrossbowAttackGoal.CrossbowState.READY_TO_ATTACK && var2) {
            ((RangedAttackMob)this.mob).performRangedAttack(var1, 1.0F);
            ItemStack var7 = this.mob.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this.mob, Items.CROSSBOW));
            CrossbowItem.setCharged(var7, false);
            this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
         }

      }
   }

   private boolean canRun() {
      return this.crossbowState == RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
   }

   static enum CrossbowState {
      UNCHARGED,
      CHARGING,
      CHARGED,
      READY_TO_ATTACK;
   }
}
