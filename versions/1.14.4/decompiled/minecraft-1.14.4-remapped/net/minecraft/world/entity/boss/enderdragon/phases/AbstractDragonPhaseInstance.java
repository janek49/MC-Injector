package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractDragonPhaseInstance implements DragonPhaseInstance {
   protected final EnderDragon dragon;

   public AbstractDragonPhaseInstance(EnderDragon dragon) {
      this.dragon = dragon;
   }

   public boolean isSitting() {
      return false;
   }

   public void doClientTick() {
   }

   public void doServerTick() {
   }

   public void onCrystalDestroyed(EndCrystal endCrystal, BlockPos blockPos, DamageSource damageSource, @Nullable Player player) {
   }

   public void begin() {
   }

   public void end() {
   }

   public float getFlySpeed() {
      return 0.6F;
   }

   @Nullable
   public Vec3 getFlyTargetLocation() {
      return null;
   }

   public float onHurt(DamageSource damageSource, float var2) {
      return var2;
   }

   public float getTurnSpeed() {
      float var1 = Mth.sqrt(Entity.getHorizontalDistanceSqr(this.dragon.getDeltaMovement())) + 1.0F;
      float var2 = Math.min(var1, 40.0F);
      return 0.7F / var2 / var1;
   }
}
