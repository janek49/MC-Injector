package net.minecraft.world.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;

public abstract class AbstractIllager extends Raider {
   protected AbstractIllager(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected void registerGoals() {
      super.registerGoals();
   }

   public MobType getMobType() {
      return MobType.ILLAGER;
   }

   public AbstractIllager.IllagerArmPose getArmPose() {
      return AbstractIllager.IllagerArmPose.CROSSED;
   }

   public static enum IllagerArmPose {
      CROSSED,
      ATTACKING,
      SPELLCASTING,
      BOW_AND_ARROW,
      CROSSBOW_HOLD,
      CROSSBOW_CHARGE,
      CELEBRATING;
   }

   public class RaiderOpenDoorGoal extends OpenDoorGoal {
      public RaiderOpenDoorGoal(Raider raider) {
         super(raider, false);
      }

      public boolean canUse() {
         return super.canUse() && AbstractIllager.this.hasActiveRaid();
      }
   }
}
