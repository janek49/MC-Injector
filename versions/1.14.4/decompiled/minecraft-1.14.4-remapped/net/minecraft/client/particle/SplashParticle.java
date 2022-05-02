package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

@ClientJarOnly
public class SplashParticle extends WaterDropParticle {
   private SplashParticle(Level level, double var2, double var4, double var6, double xd, double var10, double zd) {
      super(level, var2, var4, var6);
      this.gravity = 0.04F;
      if(var10 == 0.0D && (xd != 0.0D || zd != 0.0D)) {
         this.xd = xd;
         this.yd = 0.1D;
         this.zd = zd;
      }

   }

   @ClientJarOnly
   public static class Provider implements ParticleProvider {
      private final SpriteSet sprite;

      public Provider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         SplashParticle var15 = new SplashParticle(level, var3, var5, var7, var9, var11, var13);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }
}
