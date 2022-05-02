package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

@ClientJarOnly
public class HugeExplosionSeedParticle extends NoRenderParticle {
   private int life;
   private final int lifeTime;

   private HugeExplosionSeedParticle(Level level, double var2, double var4, double var6) {
      super(level, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.lifeTime = 8;
   }

   public void tick() {
      for(int var1 = 0; var1 < 6; ++var1) {
         double var2 = this.x + (this.random.nextDouble() - this.random.nextDouble()) * 4.0D;
         double var4 = this.y + (this.random.nextDouble() - this.random.nextDouble()) * 4.0D;
         double var6 = this.z + (this.random.nextDouble() - this.random.nextDouble()) * 4.0D;
         this.level.addParticle(ParticleTypes.EXPLOSION, var2, var4, var6, (double)((float)this.life / (float)this.lifeTime), 0.0D, 0.0D);
      }

      ++this.life;
      if(this.life == this.lifeTime) {
         this.remove();
      }

   }

   @ClientJarOnly
   public static class Provider implements ParticleProvider {
      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new HugeExplosionSeedParticle(level, var3, var5, var7);
      }
   }
}
