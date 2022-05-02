package net.minecraft.core.particles;

import net.minecraft.core.particles.ParticleOptions;

public class ParticleType {
   private final boolean overrideLimiter;
   private final ParticleOptions.Deserializer deserializer;

   protected ParticleType(boolean overrideLimiter, ParticleOptions.Deserializer deserializer) {
      this.overrideLimiter = overrideLimiter;
      this.deserializer = deserializer;
   }

   public boolean getOverrideLimiter() {
      return this.overrideLimiter;
   }

   public ParticleOptions.Deserializer getDeserializer() {
      return this.deserializer;
   }
}
