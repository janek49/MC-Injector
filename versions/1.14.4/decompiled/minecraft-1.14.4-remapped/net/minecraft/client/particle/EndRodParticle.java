package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

@ClientJarOnly
public class EndRodParticle extends SimpleAnimatedParticle {
   private EndRodParticle(Level level, double var2, double var4, double var6, double xd, double yd, double zd, SpriteSet spriteFromAge) {
      super(level, var2, var4, var6, spriteFromAge, -5.0E-4F);
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      this.quadSize *= 0.75F;
      this.lifetime = 60 + this.random.nextInt(12);
      this.setFadeColor(15916745);
      this.setSpriteFromAge(spriteFromAge);
   }

   public void move(double var1, double var3, double var5) {
      this.setBoundingBox(this.getBoundingBox().move(var1, var3, var5));
      this.setLocationFromBoundingbox();
   }

   @ClientJarOnly
   public static class Provider implements ParticleProvider {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new EndRodParticle(level, var3, var5, var7, var9, var11, var13, this.sprites);
      }
   }
}
