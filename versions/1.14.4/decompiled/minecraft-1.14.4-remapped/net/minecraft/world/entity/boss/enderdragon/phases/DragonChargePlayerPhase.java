package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DragonChargePlayerPhase extends AbstractDragonPhaseInstance {
   private static final Logger LOGGER = LogManager.getLogger();
   private Vec3 targetLocation;
   private int timeSinceCharge;

   public DragonChargePlayerPhase(EnderDragon enderDragon) {
      super(enderDragon);
   }

   public void doServerTick() {
      if(this.targetLocation == null) {
         LOGGER.warn("Aborting charge player as no target was set.");
         this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
      } else if(this.timeSinceCharge > 0 && this.timeSinceCharge++ >= 10) {
         this.dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
      } else {
         double var1 = this.targetLocation.distanceToSqr(this.dragon.x, this.dragon.y, this.dragon.z);
         if(var1 < 100.0D || var1 > 22500.0D || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            ++this.timeSinceCharge;
         }

      }
   }

   public void begin() {
      this.targetLocation = null;
      this.timeSinceCharge = 0;
   }

   public void setTarget(Vec3 target) {
      this.targetLocation = target;
   }

   public float getFlySpeed() {
      return 3.0F;
   }

   @Nullable
   public Vec3 getFlyTargetLocation() {
      return this.targetLocation;
   }

   public EnderDragonPhase getPhase() {
      return EnderDragonPhase.CHARGING_PLAYER;
   }
}
