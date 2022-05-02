package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

@ClientJarOnly
public class TotemParticle extends SimpleAnimatedParticle {
   private TotemParticle(Level level, double var2, double var4, double var6, double xd, double yd, double zd, SpriteSet spriteFromAge) {
      super(level, var2, var4, var6, spriteFromAge, -0.05F);
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      this.quadSize *= 0.75F;
      this.lifetime = 60 + this.random.nextInt(12);
      this.setSpriteFromAge(spriteFromAge);
      if(this.random.nextInt(4) == 0) {
         this.setColor(0.6F + this.random.nextFloat() * 0.2F, 0.6F + this.random.nextFloat() * 0.3F, this.random.nextFloat() * 0.2F);
      } else {
         this.setColor(0.1F + this.random.nextFloat() * 0.2F, 0.4F + this.random.nextFloat() * 0.3F, this.random.nextFloat() * 0.2F);
      }

      this.setBaseAirFriction(0.6F);
   }

   @ClientJarOnly
   public static class Provider implements ParticleProvider {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new TotemParticle(level, var3, var5, var7, var9, var11, var13, this.sprites);
      }
   }
}
