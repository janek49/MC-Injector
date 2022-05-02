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
public class FlameParticle extends TextureSheetParticle {
   private FlameParticle(Level level, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(level, var2, var4, var6, var8, var10, var12);
      this.xd = this.xd * 0.009999999776482582D + var8;
      this.yd = this.yd * 0.009999999776482582D + var10;
      this.zd = this.zd * 0.009999999776482582D + var12;
      this.x += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.y += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.z += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void move(double var1, double var3, double var5) {
      this.setBoundingBox(this.getBoundingBox().move(var1, var3, var5));
      this.setLocationFromBoundingbox();
   }

   public float getQuadSize(float f) {
      float var2 = ((float)this.age + f) / (float)this.lifetime;
      return this.quadSize * (1.0F - var2 * var2 * 0.5F);
   }

   public int getLightColor(float f) {
      float var2 = ((float)this.age + f) / (float)this.lifetime;
      var2 = Mth.clamp(var2, 0.0F, 1.0F);
      int var3 = super.getLightColor(f);
      int var4 = var3 & 255;
      int var5 = var3 >> 16 & 255;
      var4 = var4 + (int)(var2 * 15.0F * 16.0F);
      if(var4 > 240) {
         var4 = 240;
      }

      return var4 | var5 << 16;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if(this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.9599999785423279D;
         this.yd *= 0.9599999785423279D;
         this.zd *= 0.9599999785423279D;
         if(this.onGround) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
         }

      }
   }

   @ClientJarOnly
   public static class Provider implements ParticleProvider {
      private final SpriteSet sprite;

      public Provider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         FlameParticle var15 = new FlameParticle(level, var3, var5, var7, var9, var11, var13);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }
}
