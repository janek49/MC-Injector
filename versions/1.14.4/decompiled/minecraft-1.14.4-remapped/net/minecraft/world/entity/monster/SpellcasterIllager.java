package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.level.Level;

public abstract class SpellcasterIllager extends AbstractIllager {
   private static final EntityDataAccessor DATA_SPELL_CASTING_ID = SynchedEntityData.defineId(SpellcasterIllager.class, EntityDataSerializers.BYTE);
   protected int spellCastingTickCount;
   private SpellcasterIllager.IllagerSpell currentSpell = SpellcasterIllager.IllagerSpell.NONE;

   protected SpellcasterIllager(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_SPELL_CASTING_ID, Byte.valueOf((byte)0));
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.spellCastingTickCount = compoundTag.getInt("SpellTicks");
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("SpellTicks", this.spellCastingTickCount);
   }

   public AbstractIllager.IllagerArmPose getArmPose() {
      return this.isCastingSpell()?AbstractIllager.IllagerArmPose.SPELLCASTING:(this.isCelebrating()?AbstractIllager.IllagerArmPose.CELEBRATING:AbstractIllager.IllagerArmPose.CROSSED);
   }

   public boolean isCastingSpell() {
      return this.level.isClientSide?((Byte)this.entityData.get(DATA_SPELL_CASTING_ID)).byteValue() > 0:this.spellCastingTickCount > 0;
   }

   public void setIsCastingSpell(SpellcasterIllager.IllagerSpell isCastingSpell) {
      this.currentSpell = isCastingSpell;
      this.entityData.set(DATA_SPELL_CASTING_ID, Byte.valueOf((byte)isCastingSpell.id));
   }

   protected SpellcasterIllager.IllagerSpell getCurrentSpell() {
      return !this.level.isClientSide?this.currentSpell:SpellcasterIllager.IllagerSpell.byId(((Byte)this.entityData.get(DATA_SPELL_CASTING_ID)).byteValue());
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      if(this.spellCastingTickCount > 0) {
         --this.spellCastingTickCount;
      }

   }

   public void tick() {
      super.tick();
      if(this.level.isClientSide && this.isCastingSpell()) {
         SpellcasterIllager.IllagerSpell var1 = this.getCurrentSpell();
         double var2 = var1.spellColor[0];
         double var4 = var1.spellColor[1];
         double var6 = var1.spellColor[2];
         float var8 = this.yBodyRot * 0.017453292F + Mth.cos((float)this.tickCount * 0.6662F) * 0.25F;
         float var9 = Mth.cos(var8);
         float var10 = Mth.sin(var8);
         this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.x + (double)var9 * 0.6D, this.y + 1.8D, this.z + (double)var10 * 0.6D, var2, var4, var6);
         this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.x - (double)var9 * 0.6D, this.y + 1.8D, this.z - (double)var10 * 0.6D, var2, var4, var6);
      }

   }

   protected int getSpellCastingTime() {
      return this.spellCastingTickCount;
   }

   protected abstract SoundEvent getCastingSoundEvent();

   public static enum IllagerSpell {
      NONE(0, 0.0D, 0.0D, 0.0D),
      SUMMON_VEX(1, 0.7D, 0.7D, 0.8D),
      FANGS(2, 0.4D, 0.3D, 0.35D),
      WOLOLO(3, 0.7D, 0.5D, 0.2D),
      DISAPPEAR(4, 0.3D, 0.3D, 0.8D),
      BLINDNESS(5, 0.1D, 0.1D, 0.2D);

      private final int id;
      private final double[] spellColor;

      private IllagerSpell(int id, double var4, double var6, double var8) {
         this.id = id;
         this.spellColor = new double[]{var4, var6, var8};
      }

      public static SpellcasterIllager.IllagerSpell byId(int id) {
         for(SpellcasterIllager.IllagerSpell var4 : values()) {
            if(id == var4.id) {
               return var4;
            }
         }

         return NONE;
      }
   }

   public class SpellcasterCastingSpellGoal extends Goal {
      public SpellcasterCastingSpellGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         return SpellcasterIllager.this.getSpellCastingTime() > 0;
      }

      public void start() {
         super.start();
         SpellcasterIllager.this.navigation.stop();
      }

      public void stop() {
         super.stop();
         SpellcasterIllager.this.setIsCastingSpell(SpellcasterIllager.IllagerSpell.NONE);
      }

      public void tick() {
         if(SpellcasterIllager.this.getTarget() != null) {
            SpellcasterIllager.this.getLookControl().setLookAt(SpellcasterIllager.this.getTarget(), (float)SpellcasterIllager.this.getMaxHeadYRot(), (float)SpellcasterIllager.this.getMaxHeadXRot());
         }

      }
   }

   public abstract class SpellcasterUseSpellGoal extends Goal {
      protected int attackWarmupDelay;
      protected int nextAttackTickCount;

      public boolean canUse() {
         LivingEntity var1 = SpellcasterIllager.this.getTarget();
         return var1 != null && var1.isAlive()?(SpellcasterIllager.this.isCastingSpell()?false:SpellcasterIllager.this.tickCount >= this.nextAttackTickCount):false;
      }

      public boolean canContinueToUse() {
         LivingEntity var1 = SpellcasterIllager.this.getTarget();
         return var1 != null && var1.isAlive() && this.attackWarmupDelay > 0;
      }

      public void start() {
         this.attackWarmupDelay = this.getCastWarmupTime();
         SpellcasterIllager.this.spellCastingTickCount = this.getCastingTime();
         this.nextAttackTickCount = SpellcasterIllager.this.tickCount + this.getCastingInterval();
         SoundEvent var1 = this.getSpellPrepareSound();
         if(var1 != null) {
            SpellcasterIllager.this.playSound(var1, 1.0F, 1.0F);
         }

         SpellcasterIllager.this.setIsCastingSpell(this.getSpell());
      }

      public void tick() {
         --this.attackWarmupDelay;
         if(this.attackWarmupDelay == 0) {
            this.performSpellCasting();
            SpellcasterIllager.this.playSound(SpellcasterIllager.this.getCastingSoundEvent(), 1.0F, 1.0F);
         }

      }

      protected abstract void performSpellCasting();

      protected int getCastWarmupTime() {
         return 20;
      }

      protected abstract int getCastingTime();

      protected abstract int getCastingInterval();

      @Nullable
      protected abstract SoundEvent getSpellPrepareSound();

      protected abstract SpellcasterIllager.IllagerSpell getSpell();
   }
}
