package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnderDragonPhaseManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final EnderDragon dragon;
   private final DragonPhaseInstance[] phases = new DragonPhaseInstance[EnderDragonPhase.getCount()];
   private DragonPhaseInstance currentPhase;

   public EnderDragonPhaseManager(EnderDragon dragon) {
      this.dragon = dragon;
      this.setPhase(EnderDragonPhase.HOVERING);
   }

   public void setPhase(EnderDragonPhase phase) {
      if(this.currentPhase == null || phase != this.currentPhase.getPhase()) {
         if(this.currentPhase != null) {
            this.currentPhase.end();
         }

         this.currentPhase = this.getPhase(phase);
         if(!this.dragon.level.isClientSide) {
            this.dragon.getEntityData().set(EnderDragon.DATA_PHASE, Integer.valueOf(phase.getId()));
         }

         LOGGER.debug("Dragon is now in phase {} on the {}", phase, this.dragon.level.isClientSide?"client":"server");
         this.currentPhase.begin();
      }
   }

   public DragonPhaseInstance getCurrentPhase() {
      return this.currentPhase;
   }

   public DragonPhaseInstance getPhase(EnderDragonPhase enderDragonPhase) {
      int var2 = enderDragonPhase.getId();
      if(this.phases[var2] == null) {
         this.phases[var2] = enderDragonPhase.createInstance(this.dragon);
      }

      return this.phases[var2];
   }
}
