package net.minecraft.world.entity.boss.enderdragon.phases;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonChargePlayerPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonDeathPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonHoldingPatternPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonHoverPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonLandingApproachPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonLandingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonSittingAttackingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonSittingFlamingPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonSittingScanningPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonStrafePlayerPhase;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonTakeoffPhase;

public class EnderDragonPhase {
   private static EnderDragonPhase[] phases = new EnderDragonPhase[0];
   public static final EnderDragonPhase HOLDING_PATTERN = create(DragonHoldingPatternPhase.class, "HoldingPattern");
   public static final EnderDragonPhase STRAFE_PLAYER = create(DragonStrafePlayerPhase.class, "StrafePlayer");
   public static final EnderDragonPhase LANDING_APPROACH = create(DragonLandingApproachPhase.class, "LandingApproach");
   public static final EnderDragonPhase LANDING = create(DragonLandingPhase.class, "Landing");
   public static final EnderDragonPhase TAKEOFF = create(DragonTakeoffPhase.class, "Takeoff");
   public static final EnderDragonPhase SITTING_FLAMING = create(DragonSittingFlamingPhase.class, "SittingFlaming");
   public static final EnderDragonPhase SITTING_SCANNING = create(DragonSittingScanningPhase.class, "SittingScanning");
   public static final EnderDragonPhase SITTING_ATTACKING = create(DragonSittingAttackingPhase.class, "SittingAttacking");
   public static final EnderDragonPhase CHARGING_PLAYER = create(DragonChargePlayerPhase.class, "ChargingPlayer");
   public static final EnderDragonPhase DYING = create(DragonDeathPhase.class, "Dying");
   public static final EnderDragonPhase HOVERING = create(DragonHoverPhase.class, "Hover");
   private final Class instanceClass;
   private final int id;
   private final String name;

   private EnderDragonPhase(int id, Class instanceClass, String name) {
      this.id = id;
      this.instanceClass = instanceClass;
      this.name = name;
   }

   public DragonPhaseInstance createInstance(EnderDragon enderDragon) {
      try {
         Constructor<? extends DragonPhaseInstance> var2 = this.getConstructor();
         return (DragonPhaseInstance)var2.newInstance(new Object[]{enderDragon});
      } catch (Exception var3) {
         throw new Error(var3);
      }
   }

   protected Constructor getConstructor() throws NoSuchMethodException {
      return this.instanceClass.getConstructor(new Class[]{EnderDragon.class});
   }

   public int getId() {
      return this.id;
   }

   public String toString() {
      return this.name + " (#" + this.id + ")";
   }

   public static EnderDragonPhase getById(int id) {
      return id >= 0 && id < phases.length?phases[id]:HOLDING_PATTERN;
   }

   public static int getCount() {
      return phases.length;
   }

   private static EnderDragonPhase create(Class class, String string) {
      EnderDragonPhase<T> enderDragonPhase = new EnderDragonPhase(phases.length, class, string);
      phases = (EnderDragonPhase[])Arrays.copyOf(phases, phases.length + 1);
      phases[enderDragonPhase.getId()] = enderDragonPhase;
      return enderDragonPhase;
   }
}
