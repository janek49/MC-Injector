package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.level.Level;

@ClientJarOnly
public class NoRenderParticle extends Particle {
   protected NoRenderParticle(Level level, double var2, double var4, double var6) {
      super(level, var2, var4, var6);
   }

   protected NoRenderParticle(Level level, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(level, var2, var4, var6, var8, var10, var12);
   }

   public final void render(BufferBuilder bufferBuilder, Camera camera, float var3, float var4, float var5, float var6, float var7, float var8) {
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.NO_RENDER;
   }
}
