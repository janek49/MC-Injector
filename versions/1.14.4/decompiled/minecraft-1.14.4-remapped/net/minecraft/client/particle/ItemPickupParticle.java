package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@ClientJarOnly
public class ItemPickupParticle extends Particle {
   private final Entity itemEntity;
   private final Entity target;
   private int life;
   private final int lifeTime;
   private final float yOffs;
   private final EntityRenderDispatcher entityRenderDispatcher;

   public ItemPickupParticle(Level level, Entity var2, Entity var3, float var4) {
      this(level, var2, var3, var4, var2.getDeltaMovement());
   }

   private ItemPickupParticle(Level level, Entity itemEntity, Entity target, float yOffs, Vec3 vec3) {
      super(level, itemEntity.x, itemEntity.y, itemEntity.z, vec3.x, vec3.y, vec3.z);
      this.entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      this.itemEntity = itemEntity;
      this.target = target;
      this.lifeTime = 3;
      this.yOffs = yOffs;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.CUSTOM;
   }

   public void render(BufferBuilder bufferBuilder, Camera camera, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = ((float)this.life + var3) / (float)this.lifeTime;
      var9 = var9 * var9;
      double var10 = this.itemEntity.x;
      double var12 = this.itemEntity.y;
      double var14 = this.itemEntity.z;
      double var16 = Mth.lerp((double)var3, this.target.xOld, this.target.x);
      double var18 = Mth.lerp((double)var3, this.target.yOld, this.target.y) + (double)this.yOffs;
      double var20 = Mth.lerp((double)var3, this.target.zOld, this.target.z);
      double var22 = Mth.lerp((double)var9, var10, var16);
      double var24 = Mth.lerp((double)var9, var12, var18);
      double var26 = Mth.lerp((double)var9, var14, var20);
      int var28 = this.getLightColor(var3);
      int var29 = var28 % 65536;
      int var30 = var28 / 65536;
      GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)var29, (float)var30);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      var22 = var22 - xOff;
      var24 = var24 - yOff;
      var26 = var26 - zOff;
      GlStateManager.enableLighting();
      this.entityRenderDispatcher.render(this.itemEntity, var22, var24, var26, this.itemEntity.yRot, var3, false);
   }

   public void tick() {
      ++this.life;
      if(this.life == this.lifeTime) {
         this.remove();
      }

   }
}
