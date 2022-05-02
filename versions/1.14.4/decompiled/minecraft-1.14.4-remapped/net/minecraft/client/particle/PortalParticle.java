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
public class PortalParticle extends TextureSheetParticle {
   private final double xStart;
   private final double yStart;
   private final double zStart;

   private PortalParticle(Level level, double x, double y, double z, double xd, double yd, double zd) {
      super(level, x, y, z);
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      this.x = x;
      this.y = y;
      this.z = z;
      this.xStart = this.x;
      this.yStart = this.y;
      this.zStart = this.z;
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.2F + 0.5F);
      float var14 = this.random.nextFloat() * 0.6F + 0.4F;
      this.rCol = var14 * 0.9F;
      this.gCol = var14 * 0.3F;
      this.bCol = var14;
      this.lifetime = (int)(Math.random() * 10.0D) + 40;
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
      var2 = 1.0F - var2;
      var2 = var2 * var2;
      var2 = 1.0F - var2;
      return this.quadSize * var2;
   }

   public int getLightColor(float f) {
      int var2 = super.getLightColor(f);
      float var3 = (float)this.age / (float)this.lifetime;
      var3 = var3 * var3;
      var3 = var3 * var3;
      int var4 = var2 & 255;
      int var5 = var2 >> 16 & 255;
      var5 = var5 + (int)(var3 * 15.0F * 16.0F);
      if(var5 > 240) {
         var5 = 240;
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
         float var1 = (float)this.age / (float)this.lifetime;
         var1 = -var1 + var1 * var1 * 2.0F;
         var1 = 1.0F - var1;
         this.x = this.xStart + this.xd * (double)var1;
         this.y = this.yStart + this.yd * (double)var1 + (double)(1.0F - var1);
         this.z = this.zStart + this.zd * (double)var1;
      }
   }

   @ClientJarOnly
   public static class Provider implements ParticleProvider {
      private final SpriteSet sprite;

      public Provider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         PortalParticle var15 = new PortalParticle(level, var3, var5, var7, var9, var11, var13);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }
}
