package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.PandaHoldsItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Panda;

@ClientJarOnly
public class PandaRenderer extends MobRenderer {
   private static final Map TEXTURES = (Map)Util.make(Maps.newEnumMap(Panda.Gene.class), (enumMap) -> {
      enumMap.put(Panda.Gene.NORMAL, new ResourceLocation("textures/entity/panda/panda.png"));
      enumMap.put(Panda.Gene.LAZY, new ResourceLocation("textures/entity/panda/lazy_panda.png"));
      enumMap.put(Panda.Gene.WORRIED, new ResourceLocation("textures/entity/panda/worried_panda.png"));
      enumMap.put(Panda.Gene.PLAYFUL, new ResourceLocation("textures/entity/panda/playful_panda.png"));
      enumMap.put(Panda.Gene.BROWN, new ResourceLocation("textures/entity/panda/brown_panda.png"));
      enumMap.put(Panda.Gene.WEAK, new ResourceLocation("textures/entity/panda/weak_panda.png"));
      enumMap.put(Panda.Gene.AGGRESSIVE, new ResourceLocation("textures/entity/panda/aggressive_panda.png"));
   });

   public PandaRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new PandaModel(9, 0.0F), 0.9F);
      this.addLayer(new PandaHoldsItemLayer(this));
   }

   @Nullable
   protected ResourceLocation getTextureLocation(Panda panda) {
      return (ResourceLocation)TEXTURES.getOrDefault(panda.getVariant(), TEXTURES.get(Panda.Gene.NORMAL));
   }

   protected void setupRotations(Panda panda, float var2, float var3, float var4) {
      super.setupRotations(panda, var2, var3, var4);
      if(panda.rollCounter > 0) {
         int var5 = panda.rollCounter;
         int var6 = var5 + 1;
         float var7 = 7.0F;
         float var8 = panda.isBaby()?0.3F:0.8F;
         if(var5 < 8) {
            float var10 = (float)(90 * var5) / 7.0F;
            float var11 = (float)(90 * var6) / 7.0F;
            float var9 = this.getAngle(var10, var11, var6, var4, 8.0F);
            GlStateManager.translatef(0.0F, (var8 + 0.2F) * (var9 / 90.0F), 0.0F);
            GlStateManager.rotatef(-var9, 1.0F, 0.0F, 0.0F);
         } else if(var5 < 16) {
            float var10 = ((float)var5 - 8.0F) / 7.0F;
            float var11 = 90.0F + 90.0F * var10;
            float var12 = 90.0F + 90.0F * ((float)var6 - 8.0F) / 7.0F;
            float var9 = this.getAngle(var11, var12, var6, var4, 16.0F);
            GlStateManager.translatef(0.0F, var8 + 0.2F + (var8 - 0.2F) * (var9 - 90.0F) / 90.0F, 0.0F);
            GlStateManager.rotatef(-var9, 1.0F, 0.0F, 0.0F);
         } else if((float)var5 < 24.0F) {
            float var10 = ((float)var5 - 16.0F) / 7.0F;
            float var11 = 180.0F + 90.0F * var10;
            float var12 = 180.0F + 90.0F * ((float)var6 - 16.0F) / 7.0F;
            float var9 = this.getAngle(var11, var12, var6, var4, 24.0F);
            GlStateManager.translatef(0.0F, var8 + var8 * (270.0F - var9) / 90.0F, 0.0F);
            GlStateManager.rotatef(-var9, 1.0F, 0.0F, 0.0F);
         } else if(var5 < 32) {
            float var10 = ((float)var5 - 24.0F) / 7.0F;
            float var11 = 270.0F + 90.0F * var10;
            float var12 = 270.0F + 90.0F * ((float)var6 - 24.0F) / 7.0F;
            float var9 = this.getAngle(var11, var12, var6, var4, 32.0F);
            GlStateManager.translatef(0.0F, var8 * ((360.0F - var9) / 90.0F), 0.0F);
            GlStateManager.rotatef(-var9, 1.0F, 0.0F, 0.0F);
         }
      } else {
         GlStateManager.rotatef(0.0F, 1.0F, 0.0F, 0.0F);
      }

      float var5 = panda.getSitAmount(var4);
      if(var5 > 0.0F) {
         GlStateManager.translatef(0.0F, 0.8F * var5, 0.0F);
         GlStateManager.rotatef(Mth.lerp(var5, panda.xRot, panda.xRot + 90.0F), 1.0F, 0.0F, 0.0F);
         GlStateManager.translatef(0.0F, -1.0F * var5, 0.0F);
         if(panda.isScared()) {
            float var6 = (float)(Math.cos((double)panda.tickCount * 1.25D) * 3.141592653589793D * 0.05000000074505806D);
            GlStateManager.rotatef(var6, 0.0F, 1.0F, 0.0F);
            if(panda.isBaby()) {
               GlStateManager.translatef(0.0F, 0.8F, 0.55F);
            }
         }
      }

      float var6 = panda.getLieOnBackAmount(var4);
      if(var6 > 0.0F) {
         float var7 = panda.isBaby()?0.5F:1.3F;
         GlStateManager.translatef(0.0F, var7 * var6, 0.0F);
         GlStateManager.rotatef(Mth.lerp(var6, panda.xRot, panda.xRot + 180.0F), 1.0F, 0.0F, 0.0F);
      }

   }

   private float getAngle(float var1, float var2, int var3, float var4, float var5) {
      return (float)var3 < var5?Mth.lerp(var4, var1, var2):var1;
   }
}
