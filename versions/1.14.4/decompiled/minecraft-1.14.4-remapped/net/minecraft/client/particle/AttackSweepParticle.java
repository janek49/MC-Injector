package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

@ClientJarOnly
public class AttackSweepParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   private AttackSweepParticle(Level level, double var2, double var4, double var6, double var8, SpriteSet sprites) {
      super(level, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.sprites = sprites;
      this.lifetime = 4;
      float var11 = this.random.nextFloat() * 0.6F + 0.4F;
      this.rCol = var11;
      this.gCol = var11;
      this.bCol = var11;
      this.quadSize = 1.0F - (float)var8 * 0.5F;
      this.setSpriteFromAge(sprites);
   }

   public int getLightColor(float f) {
      return 15728880;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if(this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_LIT;
   }

   @ClientJarOnly
   public static class Provider implements ParticleProvider {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new AttackSweepParticle(level, var3, var5, var7, var9, this.sprites);
      }
   }
}
