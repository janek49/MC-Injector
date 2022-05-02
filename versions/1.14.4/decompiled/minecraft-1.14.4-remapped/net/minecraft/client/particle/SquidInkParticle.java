package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

@ClientJarOnly
public class SquidInkParticle extends SimpleAnimatedParticle {
   private SquidInkParticle(Level level, double var2, double var4, double var6, double xd, double yd, double zd, SpriteSet spriteFromAge) {
      super(level, var2, var4, var6, spriteFromAge, 0.0F);
      this.quadSize = 0.5F;
      this.setAlpha(1.0F);
      this.setColor(0.0F, 0.0F, 0.0F);
      this.lifetime = (int)((double)(this.quadSize * 12.0F) / (Math.random() * 0.800000011920929D + 0.20000000298023224D));
      this.setSpriteFromAge(spriteFromAge);
      this.hasPhysics = false;
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      this.setBaseAirFriction(0.0F);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if(this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
         if(this.age > this.lifetime / 2) {
            this.setAlpha(1.0F - ((float)this.age - (float)(this.lifetime / 2)) / (float)this.lifetime);
         }

         this.move(this.xd, this.yd, this.zd);
         if(this.level.getBlockState(new BlockPos(this.x, this.y, this.z)).isAir()) {
            this.yd -= 0.00800000037997961D;
         }

         this.xd *= 0.9200000166893005D;
         this.yd *= 0.9200000166893005D;
         this.zd *= 0.9200000166893005D;
         if(this.onGround) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
         }

      }
   }

   @ClientJarOnly
   public static class Provider implements ParticleProvider {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new SquidInkParticle(level, var3, var5, var7, var9, var11, var13, this.sprites);
      }
   }
}
