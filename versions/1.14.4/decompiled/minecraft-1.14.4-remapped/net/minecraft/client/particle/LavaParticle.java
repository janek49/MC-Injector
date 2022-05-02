package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

@ClientJarOnly
public class LavaParticle extends TextureSheetParticle {
   private LavaParticle(Level level, double var2, double var4, double var6) {
      super(level, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.xd *= 0.800000011920929D;
      this.yd *= 0.800000011920929D;
      this.zd *= 0.800000011920929D;
      this.yd = (double)(this.random.nextFloat() * 0.4F + 0.05F);
      this.quadSize *= this.random.nextFloat() * 2.0F + 0.2F;
      this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public int getLightColor(float f) {
      int var2 = super.getLightColor(f);
      int var3 = 240;
      int var4 = var2 >> 16 & 255;
      return 240 | var4 << 16;
   }

   public float getQuadSize(float f) {
      float var2 = ((float)this.age + f) / (float)this.lifetime;
      return this.quadSize * (1.0F - var2 * var2);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      float var1 = (float)this.age / (float)this.lifetime;
      if(this.random.nextFloat() > var1) {
         this.level.addParticle(ParticleTypes.SMOKE, this.x, this.y, this.z, this.xd, this.yd, this.zd);
      }

      if(this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.yd -= 0.03D;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.9990000128746033D;
         this.yd *= 0.9990000128746033D;
         this.zd *= 0.9990000128746033D;
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
         LavaParticle var15 = new LavaParticle(level, var3, var5, var7);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }
}
