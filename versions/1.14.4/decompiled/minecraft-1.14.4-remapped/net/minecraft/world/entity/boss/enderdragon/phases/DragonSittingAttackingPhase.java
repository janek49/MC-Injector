package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonSittingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;

public class DragonSittingAttackingPhase extends AbstractDragonSittingPhase {
   private int attackingTicks;

   public DragonSittingAttackingPhase(EnderDragon enderDragon) {
      super(enderDragon);
   }

   public void doClientTick() {
      this.dragon.level.playLocalSound(this.dragon.x, this.dragon.y, this.dragon.z, SoundEvents.ENDER_DRAGON_GROWL, this.dragon.getSoundSource(), 2.5F, 0.8F + this.dragon.getRandom().nextFloat() * 0.3F, false);
   }

   public void doServerTick() {
      if(this.attackingTicks++ >= 40) {
         this.dragon.getPhaseManager().setPhase(EnderDragonPhase.SITTING_FLAMING);
      }

   }

   public void begin() {
      this.attackingTicks = 0;
   }

   public EnderDragonPhase getPhase() {
      return EnderDragonPhase.SITTING_ATTACKING;
   }
}
