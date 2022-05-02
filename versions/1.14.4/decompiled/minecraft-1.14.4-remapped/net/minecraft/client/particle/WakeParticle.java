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
public class WakeParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   private WakeParticle(Level level, double var2, double var4, double var6, double xd, double yd, double zd, SpriteSet sprites) {
      super(level, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.sprites = sprites;
      this.xd *= 0.30000001192092896D;
      this.yd = Math.random() * 0.20000000298023224D + 0.10000000149011612D;
      this.zd *= 0.30000001192092896D;
      this.setSize(0.01F, 0.01F);
      this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.setSpriteFromAge(sprites);
      this.gravity = 0.0F;
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      int var1 = 60 - this.lifetime;
      if(this.lifetime-- <= 0) {
         this.remove();
      } else {
         this.yd -= (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.9800000190734863D;
         this.yd *= 0.9800000190734863D;
         this.zd *= 0.9800000190734863D;
         float var2 = (float)var1 * 0.001F;
         this.setSize(var2, var2);
         this.setSprite(this.sprites.get(var1 % 4, 4));
      }
   }

   @ClientJarOnly
   public static class Provider implements ParticleProvider {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new WakeParticle(level, var3, var5, var7, var9, var11, var13, this.sprites);
      }
   }
}
