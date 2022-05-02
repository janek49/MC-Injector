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
public class HeartParticle extends TextureSheetParticle {
   private HeartParticle(Level level, double var2, double var4, double var6) {
      super(level, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.xd *= 0.009999999776482582D;
      this.yd *= 0.009999999776482582D;
      this.zd *= 0.009999999776482582D;
      this.yd += 0.1D;
      this.quadSize *= 1.5F;
      this.lifetime = 16;
      this.hasPhysics = false;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
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
         if(this.y == this.yo) {
            this.xd *= 1.1D;
            this.zd *= 1.1D;
         }

         this.xd *= 0.8600000143051147D;
         this.yd *= 0.8600000143051147D;
         this.zd *= 0.8600000143051147D;
         if(this.onGround) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
         }

      }
   }

   @ClientJarOnly
   public static class AngryVillagerProvider implements ParticleProvider {
      private final SpriteSet sprite;

      public AngryVillagerProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         HeartParticle var15 = new HeartParticle(level, var3, var5 + 0.5D, var7);
         var15.pickSprite(this.sprite);
         var15.setColor(1.0F, 1.0F, 1.0F);
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
         HeartParticle var15 = new HeartParticle(level, var3, var5, var7);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }
}
