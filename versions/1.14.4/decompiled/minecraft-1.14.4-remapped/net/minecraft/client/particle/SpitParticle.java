package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.particle.ExplodeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

@ClientJarOnly
public class SpitParticle extends ExplodeParticle {
   private SpitParticle(Level level, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet spriteSet) {
      super(level, var2, var4, var6, var8, var10, var12, spriteSet);
      this.gravity = 0.5F;
   }

   public void tick() {
      super.tick();
      this.yd -= 0.004D + 0.04D * (double)this.gravity;
   }

   @ClientJarOnly
   public static class Provider implements ParticleProvider {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new SpitParticle(level, var3, var5, var7, var9, var11, var13, this.sprites);
      }
   }
}
