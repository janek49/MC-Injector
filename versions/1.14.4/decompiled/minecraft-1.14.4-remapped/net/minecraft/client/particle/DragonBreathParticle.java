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
public class DragonBreathParticle extends TextureSheetParticle {
   private boolean hasHitGround;
   private final SpriteSet sprites;

   private DragonBreathParticle(Level level, double var2, double var4, double var6, double xd, double yd, double zd, SpriteSet sprites) {
      super(level, var2, var4, var6);
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      this.rCol = Mth.nextFloat(this.random, 0.7176471F, 0.8745098F);
      this.gCol = Mth.nextFloat(this.random, 0.0F, 0.0F);
      this.bCol = Mth.nextFloat(this.random, 0.8235294F, 0.9764706F);
      this.quadSize *= 0.75F;
      this.lifetime = (int)(20.0D / ((double)this.random.nextFloat() * 0.8D + 0.2D));
      this.hasHitGround = false;
      this.hasPhysics = false;
      this.sprites = sprites;
      this.setSpriteFromAge(sprites);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if(this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
         if(this.onGround) {
            this.yd = 0.0D;
            this.hasHitGround = true;
         }

         if(this.hasHitGround) {
            this.yd += 0.002D;
         }

         this.move(this.xd, this.yd, this.zd);
         if(this.y == this.yo) {
            this.xd *= 1.1D;
            this.zd *= 1.1D;
         }

         this.xd *= 0.9599999785423279D;
         this.zd *= 0.9599999785423279D;
         if(this.hasHitGround) {
            this.yd *= 0.9599999785423279D;
         }

      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public float getQuadSize(float f) {
      return this.quadSize * Mth.clamp(((float)this.age + f) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   @ClientJarOnly
   public static class Provider implements ParticleProvider {
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new DragonBreathParticle(level, var3, var5, var7, var9, var11, var13, this.sprites);
      }
   }
}
