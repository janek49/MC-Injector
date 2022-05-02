package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

@ClientJarOnly
public class DripParticle extends TextureSheetParticle {
   private final Fluid type;

   private DripParticle(Level level, double var2, double var4, double var6, Fluid type) {
      super(level, var2, var4, var6);
      this.setSize(0.01F, 0.01F);
      this.gravity = 0.06F;
      this.type = type;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public int getLightColor(float f) {
      return this.type.is(FluidTags.LAVA)?240:super.getLightColor(f);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.preMoveUpdate();
      if(!this.removed) {
         this.yd -= (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.postMoveUpdate();
         if(!this.removed) {
            this.xd *= 0.9800000190734863D;
            this.yd *= 0.9800000190734863D;
            this.zd *= 0.9800000190734863D;
            BlockPos var1 = new BlockPos(this.x, this.y, this.z);
            FluidState var2 = this.level.getFluidState(var1);
            if(var2.getType() == this.type && this.y < (double)((float)var1.getY() + var2.getHeight(this.level, var1))) {
               this.remove();
            }

         }
      }
   }

   protected void preMoveUpdate() {
      if(this.lifetime-- <= 0) {
         this.remove();
      }

   }

   protected void postMoveUpdate() {
   }

   @ClientJarOnly
   static class CoolingDripHangParticle extends DripParticle.DripHangParticle {
      private CoolingDripHangParticle(Level level, double var2, double var4, double var6, Fluid fluid, ParticleOptions particleOptions) {
         super(level, var2, var4, var6, fluid, particleOptions, null);
      }

      protected void preMoveUpdate() {
         this.rCol = 1.0F;
         this.gCol = 16.0F / (float)(40 - this.lifetime + 16);
         this.bCol = 4.0F / (float)(40 - this.lifetime + 8);
         super.preMoveUpdate();
      }
   }

   @ClientJarOnly
   static class DripFallParticle extends DripParticle {
      private final ParticleOptions landParticle;

      private DripFallParticle(Level level, double var2, double var4, double var6, Fluid fluid, ParticleOptions landParticle) {
         super(level, var2, var4, var6, fluid, null);
         this.landParticle = landParticle;
         this.lifetime = (int)(64.0D / (Math.random() * 0.8D + 0.2D));
      }

      protected void postMoveUpdate() {
         if(this.onGround) {
            this.remove();
            this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
         }

      }
   }

   @ClientJarOnly
   static class DripHangParticle extends DripParticle {
      private final ParticleOptions fallingParticle;

      private DripHangParticle(Level level, double var2, double var4, double var6, Fluid fluid, ParticleOptions fallingParticle) {
         super(level, var2, var4, var6, fluid, null);
         this.fallingParticle = fallingParticle;
         this.gravity *= 0.02F;
         this.lifetime = 40;
      }

      protected void preMoveUpdate() {
         if(this.lifetime-- <= 0) {
            this.remove();
            this.level.addParticle(this.fallingParticle, this.x, this.y, this.z, this.xd, this.yd, this.zd);
         }

      }

      protected void postMoveUpdate() {
         this.xd *= 0.02D;
         this.yd *= 0.02D;
         this.zd *= 0.02D;
      }
   }

   @ClientJarOnly
   static class DripLandParticle extends DripParticle {
      private DripLandParticle(Level level, double var2, double var4, double var6, Fluid fluid) {
         super(level, var2, var4, var6, fluid, null);
         this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
      }
   }

   @ClientJarOnly
   public static class LavaFallProvider implements ParticleProvider {
      protected final SpriteSet sprite;

      public LavaFallProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         DripParticle var15 = new DripParticle.DripFallParticle(level, var3, var5, var7, Fluids.LAVA, ParticleTypes.LANDING_LAVA);
         var15.setColor(1.0F, 0.2857143F, 0.083333336F);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }

   @ClientJarOnly
   public static class LavaHangProvider implements ParticleProvider {
      protected final SpriteSet sprite;

      public LavaHangProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         DripParticle.CoolingDripHangParticle var15 = new DripParticle.CoolingDripHangParticle(level, var3, var5, var7, Fluids.LAVA, ParticleTypes.FALLING_LAVA);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }

   @ClientJarOnly
   public static class LavaLandProvider implements ParticleProvider {
      protected final SpriteSet sprite;

      public LavaLandProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         DripParticle var15 = new DripParticle.DripLandParticle(level, var3, var5, var7, Fluids.LAVA);
         var15.setColor(1.0F, 0.2857143F, 0.083333336F);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }

   @ClientJarOnly
   public static class WaterFallProvider implements ParticleProvider {
      protected final SpriteSet sprite;

      public WaterFallProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         DripParticle var15 = new DripParticle.DripFallParticle(level, var3, var5, var7, Fluids.WATER, ParticleTypes.SPLASH);
         var15.setColor(0.2F, 0.3F, 1.0F);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }

   @ClientJarOnly
   public static class WaterHangProvider implements ParticleProvider {
      protected final SpriteSet sprite;

      public WaterHangProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         DripParticle var15 = new DripParticle.DripHangParticle(level, var3, var5, var7, Fluids.WATER, ParticleTypes.FALLING_WATER);
         var15.setColor(0.2F, 0.3F, 1.0F);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }
}
