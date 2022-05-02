package net.minecraft.world.entity.monster;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.level.Level;

public class ElderGuardian extends Guardian {
   public static final float ELDER_SIZE_SCALE = EntityType.ELDER_GUARDIAN.getWidth() / EntityType.GUARDIAN.getWidth();

   public ElderGuardian(EntityType entityType, Level level) {
      super(entityType, level);
      this.setPersistenceRequired();
      if(this.randomStrollGoal != null) {
         this.randomStrollGoal.setInterval(400);
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(80.0D);
   }

   public int getAttackDuration() {
      return 60;
   }

   public void setGhost() {
      this.clientSideSpikesAnimation = 1.0F;
      this.clientSideSpikesAnimationO = this.clientSideSpikesAnimation;
   }

   protected SoundEvent getAmbientSound() {
      return this.isInWaterOrBubble()?SoundEvents.ELDER_GUARDIAN_AMBIENT:SoundEvents.ELDER_GUARDIAN_AMBIENT_LAND;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return this.isInWaterOrBubble()?SoundEvents.ELDER_GUARDIAN_HURT:SoundEvents.ELDER_GUARDIAN_HURT_LAND;
   }

   protected SoundEvent getDeathSound() {
      return this.isInWaterOrBubble()?SoundEvents.ELDER_GUARDIAN_DEATH:SoundEvents.ELDER_GUARDIAN_DEATH_LAND;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ELDER_GUARDIAN_FLOP;
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      int var1 = 1200;
      if((this.tickCount + this.getId()) % 1200 == 0) {
         MobEffect var2 = MobEffects.DIG_SLOWDOWN;
         List<ServerPlayer> var3 = ((ServerLevel)this.level).getPlayers((serverPlayer) -> {
            return this.distanceToSqr(serverPlayer) < 2500.0D && serverPlayer.gameMode.isSurvival();
         });
         int var4 = 2;
         int var5 = 6000;
         int var6 = 1200;

         for(ServerPlayer var8 : var3) {
            if(!var8.hasEffect(var2) || var8.getEffect(var2).getAmplifier() < 2 || var8.getEffect(var2).getDuration() < 1200) {
               var8.connection.send(new ClientboundGameEventPacket(10, 0.0F));
               var8.addEffect(new MobEffectInstance(var2, 6000, 2));
            }
         }
      }

      if(!this.hasRestriction()) {
         this.restrictTo(new BlockPos(this), 16);
      }

   }
}
