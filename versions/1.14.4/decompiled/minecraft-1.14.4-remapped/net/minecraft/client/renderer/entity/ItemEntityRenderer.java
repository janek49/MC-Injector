package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Random;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@ClientJarOnly
public class ItemEntityRenderer extends EntityRenderer {
   private final ItemRenderer itemRenderer;
   private final Random random = new Random();

   public ItemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
      super(entityRenderDispatcher);
      this.itemRenderer = itemRenderer;
      this.shadowRadius = 0.15F;
      this.shadowStrength = 0.75F;
   }

   private int setupBobbingItem(ItemEntity itemEntity, double var2, double var4, double var6, float var8, BakedModel bakedModel) {
      ItemStack var10 = itemEntity.getItem();
      Item var11 = var10.getItem();
      if(var11 == null) {
         return 0;
      } else {
         boolean var12 = bakedModel.isGui3d();
         int var13 = this.getRenderAmount(var10);
         float var14 = 0.25F;
         float var15 = Mth.sin(((float)itemEntity.getAge() + var8) / 10.0F + itemEntity.bobOffs) * 0.1F + 0.1F;
         float var16 = bakedModel.getTransforms().getTransform(ItemTransforms.TransformType.GROUND).scale.y();
         GlStateManager.translatef((float)var2, (float)var4 + var15 + 0.25F * var16, (float)var6);
         if(var12 || this.entityRenderDispatcher.options != null) {
            float var17 = (((float)itemEntity.getAge() + var8) / 20.0F + itemEntity.bobOffs) * 57.295776F;
            GlStateManager.rotatef(var17, 0.0F, 1.0F, 0.0F);
         }

         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         return var13;
      }
   }

   private int getRenderAmount(ItemStack itemStack) {
      int var2 = 1;
      if(itemStack.getCount() > 48) {
         var2 = 5;
      } else if(itemStack.getCount() > 32) {
         var2 = 4;
      } else if(itemStack.getCount() > 16) {
         var2 = 3;
      } else if(itemStack.getCount() > 1) {
         var2 = 2;
      }

      return var2;
   }

   public void render(ItemEntity itemEntity, double var2, double var4, double var6, float var8, float var9) {
      ItemStack var10 = itemEntity.getItem();
      int var11 = var10.isEmpty()?187:Item.getId(var10.getItem()) + var10.getDamageValue();
      this.random.setSeed((long)var11);
      boolean var12 = false;
      if(this.bindTexture(itemEntity)) {
         this.entityRenderDispatcher.textureManager.getTexture(this.getTextureLocation(itemEntity)).pushFilter(false, false);
         var12 = true;
      }

      GlStateManager.enableRescaleNormal();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      Lighting.turnOn();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.pushMatrix();
      BakedModel var13 = this.itemRenderer.getModel(var10, itemEntity.level, (LivingEntity)null);
      int var14 = this.setupBobbingItem(itemEntity, var2, var4, var6, var9, var13);
      float var15 = var13.getTransforms().ground.scale.x();
      float var16 = var13.getTransforms().ground.scale.y();
      float var17 = var13.getTransforms().ground.scale.z();
      boolean var18 = var13.isGui3d();
      if(!var18) {
         float var19 = -0.0F * (float)(var14 - 1) * 0.5F * var15;
         float var20 = -0.0F * (float)(var14 - 1) * 0.5F * var16;
         float var21 = -0.09375F * (float)(var14 - 1) * 0.5F * var17;
         GlStateManager.translatef(var19, var20, var21);
      }

      if(this.solidRender) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(itemEntity));
      }

      for(int var19 = 0; var19 < var14; ++var19) {
         if(var18) {
            GlStateManager.pushMatrix();
            if(var19 > 0) {
               float var20 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float var21 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float var22 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               GlStateManager.translatef(var20, var21, var22);
            }

            var13.getTransforms().apply(ItemTransforms.TransformType.GROUND);
            this.itemRenderer.render(var10, var13);
            GlStateManager.popMatrix();
         } else {
            GlStateManager.pushMatrix();
            if(var19 > 0) {
               float var20 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               float var21 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               GlStateManager.translatef(var20, var21, 0.0F);
            }

            var13.getTransforms().apply(ItemTransforms.TransformType.GROUND);
            this.itemRenderer.render(var10, var13);
            GlStateManager.popMatrix();
            GlStateManager.translatef(0.0F * var15, 0.0F * var16, 0.09375F * var17);
         }
      }

      if(this.solidRender) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
      this.bindTexture(itemEntity);
      if(var12) {
         this.entityRenderDispatcher.textureManager.getTexture(this.getTextureLocation(itemEntity)).popFilter();
      }

      super.render(itemEntity, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(ItemEntity itemEntity) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
