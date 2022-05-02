package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.vertex.BufferBuilder;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.level.Level;

@ClientJarOnly
public class FireworkParticles {
   @ClientJarOnly
   public static class FlashProvider implements ParticleProvider {
      private final SpriteSet sprite;

      public FlashProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         FireworkParticles.OverlayParticle var15 = new FireworkParticles.OverlayParticle(level, var3, var5, var7);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }

   @ClientJarOnly
   public static class OverlayParticle extends TextureSheetParticle {
      private OverlayParticle(Level level, double var2, double var4, double var6) {
         super(level, var2, var4, var6);
         this.lifetime = 4;
      }

      public ParticleRenderType getRenderType() {
         return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
      }

      public void render(BufferBuilder bufferBuilder, Camera camera, float var3, float var4, float var5, float var6, float var7, float var8) {
         this.setAlpha(0.6F - ((float)this.age + var3 - 1.0F) * 0.25F * 0.5F);
         super.render(bufferBuilder, camera, var3, var4, var5, var6, var7, var8);
      }

      public float getQuadSize(float f) {
         return 7.1F * Mth.sin(((float)this.age + f - 1.0F) * 0.25F * 3.1415927F);
      }
   }

   @ClientJarOnly
   static class SparkParticle extends SimpleAnimatedParticle {
      private boolean trail;
      private boolean flicker;
      private final ParticleEngine engine;
      private float fadeR;
      private float fadeG;
      private float fadeB;
      private boolean hasFade;

      private SparkParticle(Level level, double var2, double var4, double var6, double xd, double yd, double zd, ParticleEngine engine, SpriteSet spriteFromAge) {
         super(level, var2, var4, var6, spriteFromAge, -0.004F);
         this.xd = xd;
         this.yd = yd;
         this.zd = zd;
         this.engine = engine;
         this.quadSize *= 0.75F;
         this.lifetime = 48 + this.random.nextInt(12);
         this.setSpriteFromAge(spriteFromAge);
      }

      public void setTrail(boolean trail) {
         this.trail = trail;
      }

      public void setFlicker(boolean flicker) {
         this.flicker = flicker;
      }

      public void render(BufferBuilder bufferBuilder, Camera camera, float var3, float var4, float var5, float var6, float var7, float var8) {
         if(!this.flicker || this.age < this.lifetime / 3 || (this.age + this.lifetime) / 3 % 2 == 0) {
            super.render(bufferBuilder, camera, var3, var4, var5, var6, var7, var8);
         }

      }

      public void tick() {
         super.tick();
         if(this.trail && this.age < this.lifetime / 2 && (this.age + this.lifetime) % 2 == 0) {
            FireworkParticles.SparkParticle var1 = new FireworkParticles.SparkParticle(this.level, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D, this.engine, this.sprites);
            var1.setAlpha(0.99F);
            var1.setColor(this.rCol, this.gCol, this.bCol);
            var1.age = var1.lifetime / 2;
            if(this.hasFade) {
               var1.hasFade = true;
               var1.fadeR = this.fadeR;
               var1.fadeG = this.fadeG;
               var1.fadeB = this.fadeB;
            }

            var1.flicker = this.flicker;
            this.engine.add(var1);
         }

      }
   }

   @ClientJarOnly
   public static class SparkProvider implements ParticleProvider {
      private final SpriteSet sprites;

      public SparkProvider(SpriteSet sprites) {
         this.sprites = sprites;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         FireworkParticles.SparkParticle var15 = new FireworkParticles.SparkParticle(level, var3, var5, var7, var9, var11, var13, Minecraft.getInstance().particleEngine, this.sprites);
         var15.setAlpha(0.99F);
         return var15;
      }
   }

   @ClientJarOnly
   public static class Starter extends NoRenderParticle {
      private int life;
      private final ParticleEngine engine;
      private ListTag explosions;
      private boolean twinkleDelay;

      public Starter(Level level, double var2, double var4, double var6, double xd, double yd, double zd, ParticleEngine engine, @Nullable CompoundTag compoundTag) {
         super(level, var2, var4, var6);
         this.xd = xd;
         this.yd = yd;
         this.zd = zd;
         this.engine = engine;
         this.lifetime = 8;
         if(compoundTag != null) {
            this.explosions = compoundTag.getList("Explosions", 10);
            if(this.explosions.isEmpty()) {
               this.explosions = null;
            } else {
               this.lifetime = this.explosions.size() * 2 - 1;

               for(int var16 = 0; var16 < this.explosions.size(); ++var16) {
                  CompoundTag var17 = this.explosions.getCompound(var16);
                  if(var17.getBoolean("Flicker")) {
                     this.twinkleDelay = true;
                     this.lifetime += 15;
                     break;
                  }
               }
            }
         }

      }

      public void tick() {
         if(this.life == 0 && this.explosions != null) {
            boolean var1 = this.isFarAwayFromCamera();
            boolean var2 = false;
            if(this.explosions.size() >= 3) {
               var2 = true;
            } else {
               for(int var3 = 0; var3 < this.explosions.size(); ++var3) {
                  CompoundTag var4 = this.explosions.getCompound(var3);
                  if(FireworkRocketItem.Shape.byId(var4.getByte("Type")) == FireworkRocketItem.Shape.LARGE_BALL) {
                     var2 = true;
                     break;
                  }
               }
            }

            SoundEvent var3;
            if(var2) {
               var3 = var1?SoundEvents.FIREWORK_ROCKET_LARGE_BLAST_FAR:SoundEvents.FIREWORK_ROCKET_LARGE_BLAST;
            } else {
               var3 = var1?SoundEvents.FIREWORK_ROCKET_BLAST_FAR:SoundEvents.FIREWORK_ROCKET_BLAST;
            }

            this.level.playLocalSound(this.x, this.y, this.z, var3, SoundSource.AMBIENT, 20.0F, 0.95F + this.random.nextFloat() * 0.1F, true);
         }

         if(this.life % 2 == 0 && this.explosions != null && this.life / 2 < this.explosions.size()) {
            int var1 = this.life / 2;
            CompoundTag var2 = this.explosions.getCompound(var1);
            FireworkRocketItem.Shape var3 = FireworkRocketItem.Shape.byId(var2.getByte("Type"));
            boolean var4 = var2.getBoolean("Trail");
            boolean var5 = var2.getBoolean("Flicker");
            int[] vars6 = var2.getIntArray("Colors");
            int[] vars7 = var2.getIntArray("FadeColors");
            if(vars6.length == 0) {
               vars6 = new int[]{DyeColor.BLACK.getFireworkColor()};
            }

            switch(var3) {
            case SMALL_BALL:
            default:
               this.createParticleBall(0.25D, 2, vars6, vars7, var4, var5);
               break;
            case LARGE_BALL:
               this.createParticleBall(0.5D, 4, vars6, vars7, var4, var5);
               break;
            case STAR:
               this.createParticleShape(0.5D, new double[][]{{0.0D, 1.0D}, {0.3455D, 0.309D}, {0.9511D, 0.309D}, {0.3795918367346939D, -0.12653061224489795D}, {0.6122448979591837D, -0.8040816326530612D}, {0.0D, -0.35918367346938773D}}, vars6, vars7, var4, var5, false);
               break;
            case CREEPER:
               this.createParticleShape(0.5D, new double[][]{{0.0D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.6D}, {0.6D, 0.6D}, {0.6D, 0.2D}, {0.2D, 0.2D}, {0.2D, 0.0D}, {0.4D, 0.0D}, {0.4D, -0.6D}, {0.2D, -0.6D}, {0.2D, -0.4D}, {0.0D, -0.4D}}, vars6, vars7, var4, var5, true);
               break;
            case BURST:
               this.createParticleBurst(vars6, vars7, var4, var5);
            }

            int var8 = vars6[0];
            float var9 = (float)((var8 & 16711680) >> 16) / 255.0F;
            float var10 = (float)((var8 & '\uff00') >> 8) / 255.0F;
            float var11 = (float)((var8 & 255) >> 0) / 255.0F;
            Particle var12 = this.engine.createParticle(ParticleTypes.FLASH, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
            var12.setColor(var9, var10, var11);
         }

         ++this.life;
         if(this.life > this.lifetime) {
            if(this.twinkleDelay) {
               boolean var1 = this.isFarAwayFromCamera();
               SoundEvent var2 = var1?SoundEvents.FIREWORK_ROCKET_TWINKLE_FAR:SoundEvents.FIREWORK_ROCKET_TWINKLE;
               this.level.playLocalSound(this.x, this.y, this.z, var2, SoundSource.AMBIENT, 20.0F, 0.9F + this.random.nextFloat() * 0.15F, true);
            }

            this.remove();
         }

      }

      private boolean isFarAwayFromCamera() {
         Minecraft var1 = Minecraft.getInstance();
         return var1.gameRenderer.getMainCamera().getPosition().distanceToSqr(this.x, this.y, this.z) >= 256.0D;
      }

      private void createParticle(double var1, double var3, double var5, double var7, double var9, double var11, int[] vars13, int[] vars14, boolean var15, boolean var16) {
         FireworkParticles.SparkParticle var17 = (FireworkParticles.SparkParticle)this.engine.createParticle(ParticleTypes.FIREWORK, var1, var3, var5, var7, var9, var11);
         var17.setTrail(var15);
         var17.setFlicker(var16);
         var17.setAlpha(0.99F);
         int var18 = this.random.nextInt(vars13.length);
         var17.setColor(vars13[var18]);
         if(vars14.length > 0) {
            var17.setFadeColor(vars14[this.random.nextInt(vars14.length)]);
         }

      }

      private void createParticleBall(double var1, int var3, int[] vars4, int[] vars5, boolean var6, boolean var7) {
         double var8 = this.x;
         double var10 = this.y;
         double var12 = this.z;

         for(int var14 = -var3; var14 <= var3; ++var14) {
            for(int var15 = -var3; var15 <= var3; ++var15) {
               for(int var16 = -var3; var16 <= var3; ++var16) {
                  double var17 = (double)var15 + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                  double var19 = (double)var14 + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                  double var21 = (double)var16 + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                  double var23 = (double)Mth.sqrt(var17 * var17 + var19 * var19 + var21 * var21) / var1 + this.random.nextGaussian() * 0.05D;
                  this.createParticle(var8, var10, var12, var17 / var23, var19 / var23, var21 / var23, vars4, vars5, var6, var7);
                  if(var14 != -var3 && var14 != var3 && var15 != -var3 && var15 != var3) {
                     var16 += var3 * 2 - 1;
                  }
               }
            }
         }

      }

      private void createParticleShape(double var1, double[][] doubles, int[] vars4, int[] vars5, boolean var6, boolean var7, boolean var8) {
         double var9 = doubles[0][0];
         double var11 = doubles[0][1];
         this.createParticle(this.x, this.y, this.z, var9 * var1, var11 * var1, 0.0D, vars4, vars5, var6, var7);
         float var13 = this.random.nextFloat() * 3.1415927F;
         double var14 = var8?0.034D:0.34D;

         for(int var16 = 0; var16 < 3; ++var16) {
            double var17 = (double)var13 + (double)((float)var16 * 3.1415927F) * var14;
            double var19 = var9;
            double var21 = var11;

            for(int var23 = 1; var23 < doubles.length; ++var23) {
               double var24 = doubles[var23][0];
               double var26 = doubles[var23][1];

               for(double var28 = 0.25D; var28 <= 1.0D; var28 += 0.25D) {
                  double var30 = Mth.lerp(var28, var19, var24) * var1;
                  double var32 = Mth.lerp(var28, var21, var26) * var1;
                  double var34 = var30 * Math.sin(var17);
                  var30 = var30 * Math.cos(var17);

                  for(double var36 = -1.0D; var36 <= 1.0D; var36 += 2.0D) {
                     this.createParticle(this.x, this.y, this.z, var30 * var36, var32, var34 * var36, vars4, vars5, var6, var7);
                  }
               }

               var19 = var24;
               var21 = var26;
            }
         }

      }

      private void createParticleBurst(int[] vars1, int[] vars2, boolean var3, boolean var4) {
         double var5 = this.random.nextGaussian() * 0.05D;
         double var7 = this.random.nextGaussian() * 0.05D;

         for(int var9 = 0; var9 < 70; ++var9) {
            double var10 = this.xd * 0.5D + this.random.nextGaussian() * 0.15D + var5;
            double var12 = this.zd * 0.5D + this.random.nextGaussian() * 0.15D + var7;
            double var14 = this.yd * 0.5D + this.random.nextDouble() * 0.5D;
            this.createParticle(this.x, this.y, this.z, var10, var14, var12, vars1, vars2, var3, var4);
         }

      }
   }
}
