package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Random;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

@ClientJarOnly
public class SpellParticle extends TextureSheetParticle {
   private static final Random RANDOM = new Random();
   private final SpriteSet sprites;

   private SpellParticle(Level level, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet sprites) {
      super(level, var2, var4, var6, 0.5D - RANDOM.nextDouble(), var10, 0.5D - RANDOM.nextDouble());
      this.sprites = sprites;
      this.yd *= 0.20000000298023224D;
      if(var8 == 0.0D && var12 == 0.0D) {
         this.xd *= 0.10000000149011612D;
         this.zd *= 0.10000000149011612D;
      }

      this.quadSize *= 0.75F;
      this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.hasPhysics = false;
      this.setSpriteFromAge(sprites);
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
         this.yd += 0.004D;
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
   public static class AmbientMobProvider implements ParticleProvider {
      private final SpriteSet sprite;

      public AmbientMobProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         Particle particle = new SpellParticle(level, var3, var5, var7, var9, var11, var13, this.sprite);
         particle.setAlpha(0.15F);
         particle.setColor((float)var9, (float)var11, (float)var13);
         return particle;
      }
   }

   @ClientJarOnly
   public static class InstantProvider implements ParticleProvider {
      private final SpriteSet sprite;

      public InstantProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new SpellParticle(level, var3, var5, var7, var9, var11, var13, this.sprite);
      }
   }

   @ClientJarOnly
   public static class MobProvider implements ParticleProvider {
      private final SpriteSet sprite;

      public MobProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         Particle particle = new SpellParticle(level, var3, var5, var7, var9, var11, var13, this.sprite);
         particle.setColor((float)var9, (float)var11, (float)var13);
         return particle;
      }
   }

   @ClientJarOnly
   public static class Provider implements ParticleProvider {
      private final SpriteSet sprite;

      public Provider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new SpellParticle(level, var3, var5, var7, var9, var11, var13, this.sprite);
      }
   }

   @ClientJarOnly
   public static class WitchProvider implements ParticleProvider {
      private final SpriteSet sprite;

      public WitchProvider(SpriteSet sprite) {
         this.sprite = sprite;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         SpellParticle var15 = new SpellParticle(level, var3, var5, var7, var9, var11, var13, this.sprite);
         float var16 = level.random.nextFloat() * 0.5F + 0.35F;
         var15.setColor(1.0F * var16, 0.0F * var16, 1.0F * var16);
         return var15;
      }
   }
}
