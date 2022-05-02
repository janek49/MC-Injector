package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

@ClientJarOnly
public class WaterCurrentDownParticle extends TextureSheetParticle {
   private float angle;

   private WaterCurrentDownParticle(Level level, double var2, double var4, double var6) {
      super(level, var2, var4, var6);
      this.lifetime = (int)(Math.random() * 60.0D) + 30;
      this.hasPhysics = false;
      this.xd = 0.0D;
      this.yd = -0.05D;
      this.zd = 0.0D;
      this.setSize(0.02F, 0.02F);
      this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
      this.gravity = 0.002F;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if(this.age++ >= this.lifetime) {
         this.remove();
      } else {
         float var1 = 0.6F;
         this.xd += (double)(0.6F * Mth.cos(this.angle));
         this.zd += (double)(0.6F * Mth.sin(this.angle));
         this.xd *= 0.07D;
         this.zd *= 0.07D;
         this.move(this.xd, this.yd, this.zd);
         if(!this.level.getFluidState(new BlockPos(this.x, this.y, this.z)).is(FluidTags.WATER) || this.onGround) {
            this.remove();
         }

         this.angle = (float)((double)this.angle + 0.08D);
      }
   }

   @ClientJarOnly
   public static class Provider implements ParticleProvider {
      private final SpriteSet sprite;

      public Provider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         WaterCurrentDownParticle var15 = new WaterCurrentDownParticle(level, var3, var5, var7);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }
}
