package net.minecraft.client.renderer.entity.layers;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Random;
import net.minecraft.client.model.geom.Cube;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;

@ClientJarOnly
public class ArrowLayer extends RenderLayer {
   private final EntityRenderDispatcher dispatcher;

   public ArrowLayer(LivingEntityRenderer livingEntityRenderer) {
      super(livingEntityRenderer);
      this.dispatcher = livingEntityRenderer.getDispatcher();
   }

   public void render(LivingEntity livingEntity, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      int var9 = livingEntity.getArrowCount();
      if(var9 > 0) {
         Entity var10 = new Arrow(livingEntity.level, livingEntity.x, livingEntity.y, livingEntity.z);
         Random var11 = new Random((long)livingEntity.getId());
         Lighting.turnOff();

         for(int var12 = 0; var12 < var9; ++var12) {
            GlStateManager.pushMatrix();
            ModelPart var13 = this.getParentModel().getRandomModelPart(var11);
            Cube var14 = (Cube)var13.cubes.get(var11.nextInt(var13.cubes.size()));
            var13.translateTo(0.0625F);
            float var15 = var11.nextFloat();
            float var16 = var11.nextFloat();
            float var17 = var11.nextFloat();
            float var18 = Mth.lerp(var15, var14.minX, var14.maxX) / 16.0F;
            float var19 = Mth.lerp(var16, var14.minY, var14.maxY) / 16.0F;
            float var20 = Mth.lerp(var17, var14.minZ, var14.maxZ) / 16.0F;
            GlStateManager.translatef(var18, var19, var20);
            var15 = var15 * 2.0F - 1.0F;
            var16 = var16 * 2.0F - 1.0F;
            var17 = var17 * 2.0F - 1.0F;
            var15 = var15 * -1.0F;
            var16 = var16 * -1.0F;
            var17 = var17 * -1.0F;
            float var21 = Mth.sqrt(var15 * var15 + var17 * var17);
            var10.yRot = (float)(Math.atan2((double)var15, (double)var17) * 57.2957763671875D);
            var10.xRot = (float)(Math.atan2((double)var16, (double)var21) * 57.2957763671875D);
            var10.yRotO = var10.yRot;
            var10.xRotO = var10.xRot;
            double var22 = 0.0D;
            double var24 = 0.0D;
            double var26 = 0.0D;
            this.dispatcher.render(var10, 0.0D, 0.0D, 0.0D, 0.0F, var4, false);
            GlStateManager.popMatrix();
         }

         Lighting.turnOn();
      }
   }

   public boolean colorsOnDamage() {
      return false;
   }
}
