package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

@ClientJarOnly
public class DustParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   private DustParticle(Level level, double var2, double var4, double var6, double var8, double var10, double var12, DustParticleOptions dustParticleOptions, SpriteSet sprites) {
      super(level, var2, var4, var6, var8, var10, var12);
      this.sprites = sprites;
      this.xd *= 0.10000000149011612D;
      this.yd *= 0.10000000149011612D;
      this.zd *= 0.10000000149011612D;
      float var16 = (float)Math.random() * 0.4F + 0.6F;
      this.rCol = ((float)(Math.random() * 0.20000000298023224D) + 0.8F) * dustParticleOptions.getR() * var16;
      this.gCol = ((float)(Math.random() * 0.20000000298023224D) + 0.8F) * dustParticleOptions.getG() * var16;
      this.bCol = ((float)(Math.random() * 0.20000000298023224D) + 0.8F) * dustParticleOptions.getB() * var16;
      this.quadSize *= 0.75F * dustParticleOptions.getScale();
      int var17 = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.lifetime = (int)Math.max((float)var17 * dustParticleOptions.getScale(), 1.0F);
      this.setSpriteFromAge(sprites);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public float getQuadSize(float f) {
      return this.quadSize * Mth.clamp(((float)this.age + f) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if(this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
         this.move(this.xd, this.yd, this.zd);
         if(this.y == this.yo) {
            this.xd *= 1.1D;
            this.zd *= 1.1D;
         }

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
      private final SpriteSet sprites;

      public Provider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(DustParticleOptions dustParticleOptions, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new DustParticle(level, var3, var5, var7, var9, var11, var13, dustParticleOptions, this.sprites);
      }
   }
}
