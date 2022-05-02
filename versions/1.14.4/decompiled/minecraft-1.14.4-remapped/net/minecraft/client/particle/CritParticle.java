package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

@ClientJarOnly
public class CritParticle extends TextureSheetParticle {
   private CritParticle(Level level, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(level, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.xd *= 0.10000000149011612D;
      this.yd *= 0.10000000149011612D;
      this.zd *= 0.10000000149011612D;
      this.xd += var8 * 0.4D;
      this.yd += var10 * 0.4D;
      this.zd += var12 * 0.4D;
      float var14 = (float)(Math.random() * 0.30000001192092896D + 0.6000000238418579D);
      this.rCol = var14;
      this.gCol = var14;
      this.bCol = var14;
      this.quadSize *= 0.75F;
      this.lifetime = Math.max((int)(6.0D / (Math.random() * 0.8D + 0.6D)), 1);
      this.hasPhysics = false;
      this.tick();
   }

   public float getQuadSize(float f) {
      return this.quadSize * Mth.clamp(((float)this.age + f) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if(this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.move(this.xd, this.yd, this.zd);
         this.gCol = (float)((double)this.gCol * 0.96D);
         this.bCol = (float)((double)this.bCol * 0.9D);
         this.xd *= 0.699999988079071D;
         this.yd *= 0.699999988079071D;
         this.zd *= 0.699999988079071D;
         this.yd -= 0.019999999552965164D;
         if(this.onGround) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
         }

      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @ClientJarOnly
   public static class DamageIndicatorProvider implements ParticleProvider {
      private final SpriteSet sprite;

      public DamageIndicatorProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         CritParticle var15 = new CritParticle(level, var3, var5, var7, var9, var11 + 1.0D, var13);
         var15.setLifetime(20);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }

   @ClientJarOnly
   public static class MagicProvider implements ParticleProvider {
      private final SpriteSet sprite;

      public MagicProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         CritParticle var15 = new CritParticle(level, var3, var5, var7, var9, var11, var13);
         var15.rCol *= 0.3F;
         var15.gCol *= 0.8F;
         var15.pickSprite(this.sprite);
         return var15;
      }
   }

   @ClientJarOnly
   public static class Provider implements ParticleProvider {
      private final SpriteSet sprite;

      public Provider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         CritParticle var15 = new CritParticle(level, var3, var5, var7, var9, var11, var13);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }
}
