package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@ClientJarOnly
public class TrackingEmitter extends NoRenderParticle {
   private final Entity entity;
   private int life;
   private final int lifeTime;
   private final ParticleOptions particleType;

   public TrackingEmitter(Level level, Entity entity, ParticleOptions particleOptions) {
      this(level, entity, particleOptions, 3);
   }

   public TrackingEmitter(Level level, Entity entity, ParticleOptions particleOptions, int var4) {
      this(level, entity, particleOptions, var4, entity.getDeltaMovement());
   }

   private TrackingEmitter(Level level, Entity entity, ParticleOptions particleType, int lifeTime, Vec3 vec3) {
      super(level, entity.x, entity.getBoundingBox().minY + (double)(entity.getBbHeight() / 2.0F), entity.z, vec3.x, vec3.y, vec3.z);
      this.entity = entity;
      this.lifeTime = lifeTime;
      this.particleType = particleType;
      this.tick();
   }

   public void tick() {
      for(int var1 = 0; var1 < 16; ++var1) {
         double var2 = (double)(this.random.nextFloat() * 2.0F - 1.0F);
         double var4 = (double)(this.random.nextFloat() * 2.0F - 1.0F);
         double var6 = (double)(this.random.nextFloat() * 2.0F - 1.0F);
         if(var2 * var2 + var4 * var4 + var6 * var6 <= 1.0D) {
            double var8 = this.entity.x + var2 * (double)this.entity.getBbWidth() / 4.0D;
            double var10 = this.entity.getBoundingBox().minY + (double)(this.entity.getBbHeight() / 2.0F) + var4 * (double)this.entity.getBbHeight() / 4.0D;
            double var12 = this.entity.z + var6 * (double)this.entity.getBbWidth() / 4.0D;
            this.level.addParticle(this.particleType, false, var8, var10, var12, var2, var4 + 0.2D, var6);
         }
      }

      ++this.life;
      if(this.life >= this.lifeTime) {
         this.remove();
      }

   }
}
