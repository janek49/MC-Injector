package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

@ClientJarOnly
public class BarrierParticle extends TextureSheetParticle {
   private BarrierParticle(Level level, double var2, double var4, double var6, ItemLike itemLike) {
      super(level, var2, var4, var6);
      this.setSprite(Minecraft.getInstance().getItemRenderer().getItemModelShaper().getParticleIcon(itemLike));
      this.gravity = 0.0F;
      this.lifetime = 80;
      this.hasPhysics = false;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.TERRAIN_SHEET;
   }

   public float getQuadSize(float f) {
      return 0.5F;
   }

   @ClientJarOnly
   public static class Provider implements ParticleProvider {
      public Particle createParticle(SimpleParticleType simpleParticleType, Level level, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new BarrierParticle(level, var3, var5, var7, Blocks.BARRIER.asItem());
      }
   }
}
