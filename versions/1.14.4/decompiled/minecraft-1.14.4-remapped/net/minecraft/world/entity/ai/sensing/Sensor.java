package net.minecraft.world.entity.ai.sensing;

import java.util.Random;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public abstract class Sensor {
   private static final Random RANDOM = new Random();
   private final int scanRate;
   private long timeToTick;

   public Sensor(int scanRate) {
      this.scanRate = scanRate;
      this.timeToTick = (long)RANDOM.nextInt(scanRate);
   }

   public Sensor() {
      this(20);
   }

   public final void tick(ServerLevel serverLevel, LivingEntity livingEntity) {
      if(--this.timeToTick <= 0L) {
         this.timeToTick = (long)this.scanRate;
         this.doTick(serverLevel, livingEntity);
      }

   }

   protected abstract void doTick(ServerLevel var1, LivingEntity var2);

   public abstract Set requires();
}
