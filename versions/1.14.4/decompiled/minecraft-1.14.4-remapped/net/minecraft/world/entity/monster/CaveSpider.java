package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class CaveSpider extends Spider {
   public CaveSpider(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(12.0D);
   }

   public boolean doHurtTarget(Entity entity) {
      if(super.doHurtTarget(entity)) {
         if(entity instanceof LivingEntity) {
            int var2 = 0;
            if(this.level.getDifficulty() == Difficulty.NORMAL) {
               var2 = 7;
            } else if(this.level.getDifficulty() == Difficulty.HARD) {
               var2 = 15;
            }

            if(var2 > 0) {
               ((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.POISON, var2 * 20, 0));
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      return var4;
   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return 0.45F;
   }
}
