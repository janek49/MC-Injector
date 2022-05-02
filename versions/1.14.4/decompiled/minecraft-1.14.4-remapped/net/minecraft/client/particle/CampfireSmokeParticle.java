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
public class CampfireSmokeParticle extends TextureSheetParticle {
   private CampfireSmokeParticle(Level level, double var2, double var4, double var6, double xd, double var10, double zd, boolean var14) {
      super(level, var2, var4, var6);
      this.scale(3.0F);
      this.setSize(0.25F, 0.25F);
      if(var14) {
         this.lifetime = this.random.nextInt(50) + 280;
      } else {
         this.lifetime = this.random.nextInt(50) + 80;
      }

      this.gravity = 3.0E-6F;
      this.xd = xd;
      this.yd = var10 + (double)(this.random.nextFloat() / 500.0F);
      this.zd = zd;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if(this.age++ < this.lifetime && this.alpha > 0.0F) {
         this.xd += (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean()?1:-1));
         this.zd += (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean()?1:-1));
         this.yd -= (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         if(this.age >= this.lifetime - 60 && this.alpha > 0.01F) {
            this.alpha -= 0.015F;
         }

      } else {
         this.remove();
      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @ClientJarOnly
   public static class CosyProvider implements ParticleProvider {
      private final SpriteSet sprites;

      public CosyProvider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         CampfireSmokeParticle var15 = new CampfireSmokeParticle(level, var3, var5, var7, var9, var11, var13, false);
         var15.setAlpha(0.9F);
         var15.pickSprite(this.sprites);
         return var15;
      }
   }

   @ClientJarOnly
   public static class SignalProvider implements ParticleProvider {
      private final SpriteSet sprites;

      public SignalProvider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         CampfireSmokeParticle var15 = new CampfireSmokeParticle(level, var3, var5, var7, var9, var11, var13, true);
         var15.setAlpha(0.95F);
         var15.pickSprite(this.sprites);
         return var15;
      }
   }
}
