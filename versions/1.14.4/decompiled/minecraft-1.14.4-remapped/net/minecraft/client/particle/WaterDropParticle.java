package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

@ClientJarOnly
public class WaterDropParticle extends TextureSheetParticle {
   protected WaterDropParticle(Level level, double var2, double var4, double var6) {
      super(level, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.xd *= 0.30000001192092896D;
      this.yd = Math.random() * 0.20000000298023224D + 0.10000000149011612D;
      this.zd *= 0.30000001192092896D;
      this.setSize(0.01F, 0.01F);
      this.gravity = 0.06F;
      this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if(this.lifetime-- <= 0) {
         this.remove();
      } else {
         this.yd -= (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.9800000190734863D;
         this.yd *= 0.9800000190734863D;
         this.zd *= 0.9800000190734863D;
         if(this.onGround) {
            if(Math.random() < 0.5D) {
               this.remove();
            }

            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
         }

         BlockPos var1 = new BlockPos(this.x, this.y, this.z);
         double var2 = Math.max(this.level.getBlockState(var1).getCollisionShape(this.level, var1).max(Direction.Axis.Y, this.x - (double)var1.getX(), this.z - (double)var1.getZ()), (double)this.level.getFluidState(var1).getHeight(this.level, var1));
         if(var2 > 0.0D && this.y < (double)var1.getY() + var2) {
            this.remove();
         }

      }
   }

   @ClientJarOnly
   public static class Provider implements ParticleProvider {
      private final SpriteSet sprite;

      public Provider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         WaterDropParticle var15 = new WaterDropParticle(level, var3, var5, var7);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }
}
