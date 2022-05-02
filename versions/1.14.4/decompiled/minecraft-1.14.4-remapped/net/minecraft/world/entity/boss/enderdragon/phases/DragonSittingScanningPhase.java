package net.minecraft.world.entity.boss.enderdragon.phases;

import java.util.function.Predicate;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonSittingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonChargePlayerPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.phys.Vec3;

public class DragonSittingScanningPhase extends AbstractDragonSittingPhase {
   private static final TargetingConditions CHARGE_TARGETING = (new TargetingConditions()).range(150.0D);
   private final TargetingConditions scanTargeting;
   private int scanningTime;

   public DragonSittingScanningPhase(EnderDragon enderDragon) {
      super(enderDragon);
      this.scanTargeting = (new TargetingConditions()).range(20.0D).selector((livingEntity) -> {
         return Math.abs(livingEntity.y - enderDragon.y) <= 10.0D;
      });
   }

   public void doServerTick() {
      ++this.scanningTime;
      LivingEntity var1 = this.dragon.level.getNearestPlayer(this.scanTargeting, this.dragon, this.dragon.x, this.dragon.y, this.dragon.z);
      if(var1 != null) {
         if(this.scanningTime > 25) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.SITTING_ATTACKING);
         } else {
            Vec3 var2 = (new Vec3(var1.x - this.dragon.x, 0.0D, var1.z - this.dragon.z)).normalize();
            Vec3 var3 = (new Vec3((double)Mth.sin(this.dragon.yRot * 0.017453292F), 0.0D, (double)(-Mth.cos(this.dragon.yRot * 0.017453292F)))).normalize();
            float var4 = (float)var3.dot(var2);
            float var5 = (float)(Math.acos((double)var4) * 57.2957763671875D) + 0.5F;
            if(var5 < 0.0F || var5 > 10.0F) {
               double var6 = var1.x - this.dragon.head.x;
               double var8 = var1.z - this.dragon.head.z;
               double var10 = Mth.clamp(Mth.wrapDegrees(180.0D - Mth.atan2(var6, var8) * 57.2957763671875D - (double)this.dragon.yRot), -100.0D, 100.0D);
               this.dragon.yRotA *= 0.8F;
               float var12 = Mth.sqrt(var6 * var6 + var8 * var8) + 1.0F;
               float var13 = var12;
               if(var12 > 40.0F) {
                  var12 = 40.0F;
               }

               this.dragon.yRotA = (float)((double)this.dragon.yRotA + var10 * (double)(0.7F / var12 / var13));
               this.dragon.yRot += this.dragon.yRotA;
            }
         }
      } else if(this.scanningTime >= 100) {
         var1 = this.dragon.level.getNearestPlayer(CHARGE_TARGETING, this.dragon, this.dragon.x, this.dragon.y, this.dragon.z);
         this.dragon.getPhaseManager().setPhase(EnderDragonPhase.TAKEOFF);
         if(var1 != null) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.CHARGING_PLAYER);
            ((DragonChargePlayerPhase)this.dragon.getPhaseManager().getPhase(EnderDragonPhase.CHARGING_PLAYER)).setTarget(new Vec3(var1.x, var1.y, var1.z));
         }
      }

   }

   public void begin() {
      this.scanningTime = 0;
   }

   public EnderDragonPhase getPhase() {
      return EnderDragonPhase.SITTING_SCANNING;
   }
}
