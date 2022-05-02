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
public class BubblePopParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   private BubblePopParticle(Level level, double var2, double var4, double var6, double xd, double yd, double zd, SpriteSet sprites) {
      super(level, var2, var4, var6);
      this.sprites = sprites;
      this.lifetime = 4;
      this.gravity = 0.008F;
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      this.setSpriteFromAge(sprites);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if(this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.yd -= (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.setSpriteFromAge(this.sprites);
      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @ClientJarOnly
   public static class Provider implements ParticleProvider {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new BubblePopParticle(level, var3, var5, var7, var9, var11, var13, this.sprites);
      }
   }
}
