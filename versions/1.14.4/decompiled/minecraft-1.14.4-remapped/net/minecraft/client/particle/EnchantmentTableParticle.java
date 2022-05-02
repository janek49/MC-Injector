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
public class EnchantmentTableParticle extends TextureSheetParticle {
   private final double xStart;
   private final double yStart;
   private final double zStart;

   private EnchantmentTableParticle(Level level, double xStart, double yStart, double zStart, double xd, double yd, double zd) {
      super(level, xStart, yStart, zStart);
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      this.xStart = xStart;
      this.yStart = yStart;
      this.zStart = zStart;
      this.xo = xStart + xd;
      this.yo = yStart + yd;
      this.zo = zStart + zd;
      this.x = this.xo;
      this.y = this.yo;
      this.z = this.zo;
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.2F);
      float var14 = this.random.nextFloat() * 0.6F + 0.4F;
      this.rCol = 0.9F * var14;
      this.gCol = 0.9F * var14;
      this.bCol = var14;
      this.hasPhysics = false;
      this.lifetime = (int)(Math.random() * 10.0D) + 30;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void move(double var1, double var3, double var5) {
      this.setBoundingBox(this.getBoundingBox().move(var1, var3, var5));
      this.setLocationFromBoundingbox();
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
         var1 = 1.0F - var1;
         float var2 = 1.0F - var1;
         var2 = var2 * var2;
         var2 = var2 * var2;
         this.x = this.xStart + this.xd * (double)var1;
         this.y = this.yStart + this.yd * (double)var1 - (double)(var2 * 1.2F);
         this.z = this.zStart + this.zd * (double)var1;
      }
   }

   @ClientJarOnly
   public static class NautilusProvider implements ParticleProvider {
      private final SpriteSet sprite;

      public NautilusProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         EnchantmentTableParticle var15 = new EnchantmentTableParticle(level, var3, var5, var7, var9, var11, var13);
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
         EnchantmentTableParticle var15 = new EnchantmentTableParticle(level, var3, var5, var7, var9, var11, var13);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }
}
