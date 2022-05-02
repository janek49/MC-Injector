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
public class SuspendedTownParticle extends TextureSheetParticle {
   private SuspendedTownParticle(Level level, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(level, var2, var4, var6, var8, var10, var12);
      float var14 = this.random.nextFloat() * 0.1F + 0.2F;
      this.rCol = var14;
      this.gCol = var14;
      this.bCol = var14;
      this.setSize(0.02F, 0.02F);
      this.quadSize *= this.random.nextFloat() * 0.6F + 0.5F;
      this.xd *= 0.019999999552965164D;
      this.yd *= 0.019999999552965164D;
      this.zd *= 0.019999999552965164D;
      this.lifetime = (int)(20.0D / (Math.random() * 0.8D + 0.2D));
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void move(double var1, double var3, double var5) {
      this.setBoundingBox(this.getBoundingBox().move(var1, var3, var5));
      this.setLocationFromBoundingbox();
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if(this.lifetime-- <= 0) {
         this.remove();
      } else {
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.99D;
         this.yd *= 0.99D;
         this.zd *= 0.99D;
      }
   }

   @ClientJarOnly
   public static class ComposterFillProvider implements ParticleProvider {
      private final SpriteSet sprite;

      public ComposterFillProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         SuspendedTownParticle var15 = new SuspendedTownParticle(level, var3, var5, var7, var9, var11, var13);
         var15.pickSprite(this.sprite);
         var15.setColor(1.0F, 1.0F, 1.0F);
         var15.setLifetime(3 + level.getRandom().nextInt(5));
         return var15;
      }
   }

   @ClientJarOnly
   public static class DolphinSpeedProvider implements ParticleProvider {
      private final SpriteSet sprite;

      public DolphinSpeedProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         SuspendedTownParticle var15 = new SuspendedTownParticle(level, var3, var5, var7, var9, var11, var13);
         var15.setColor(0.3F, 0.5F, 1.0F);
         var15.pickSprite(this.sprite);
         var15.setAlpha(1.0F - level.random.nextFloat() * 0.7F);
         var15.setLifetime(var15.getLifetime() / 2);
         return var15;
      }
   }

   @ClientJarOnly
   public static class HappyVillagerProvider implements ParticleProvider {
      private final SpriteSet sprite;

      public HappyVillagerProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         SuspendedTownParticle var15 = new SuspendedTownParticle(level, var3, var5, var7, var9, var11, var13);
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
         SuspendedTownParticle var15 = new SuspendedTownParticle(level, var3, var5, var7, var9, var11, var13);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }
}
