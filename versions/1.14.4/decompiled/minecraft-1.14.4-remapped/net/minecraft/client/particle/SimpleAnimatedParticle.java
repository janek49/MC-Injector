package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.level.Level;

@ClientJarOnly
public class SimpleAnimatedParticle extends TextureSheetParticle {
   protected final SpriteSet sprites;
   private final float baseGravity;
   private float baseAirFriction = 0.91F;
   private float fadeR;
   private float fadeG;
   private float fadeB;
   private boolean hasFade;

   protected SimpleAnimatedParticle(Level level, double var2, double var4, double var6, SpriteSet sprites, float baseGravity) {
      super(level, var2, var4, var6);
      this.sprites = sprites;
      this.baseGravity = baseGravity;
   }

   public void setColor(int color) {
      float var2 = (float)((color & 16711680) >> 16) / 255.0F;
      float var3 = (float)((color & '\uff00') >> 8) / 255.0F;
      float var4 = (float)((color & 255) >> 0) / 255.0F;
      float var5 = 1.0F;
      this.setColor(var2 * 1.0F, var3 * 1.0F, var4 * 1.0F);
   }

   public void setFadeColor(int fadeColor) {
      this.fadeR = (float)((fadeColor & 16711680) >> 16) / 255.0F;
      this.fadeG = (float)((fadeColor & '\uff00') >> 8) / 255.0F;
      this.fadeB = (float)((fadeColor & 255) >> 0) / 255.0F;
      this.hasFade = true;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
            if(this.hasFade) {
               this.rCol += (this.fadeR - this.rCol) * 0.2F;
               this.gCol += (this.fadeG - this.gCol) * 0.2F;
               this.bCol += (this.fadeB - this.bCol) * 0.2F;
            }
         }

         this.yd += (double)this.baseGravity;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= (double)this.baseAirFriction;
         this.yd *= (double)this.baseAirFriction;
         this.zd *= (double)this.baseAirFriction;
         if(this.onGround) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
         }

      }
   }

   public int getLightColor(float f) {
      return 15728880;
   }

   protected void setBaseAirFriction(float baseAirFriction) {
      this.baseAirFriction = baseAirFriction;
   }
}
