package net.minecraft.client.renderer.blockentity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.model.BannerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.banner.BannerTextures;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@ClientJarOnly
public class BannerRenderer extends BlockEntityRenderer {
   private final BannerModel bannerModel = new BannerModel();

   public void render(BannerBlockEntity bannerBlockEntity, double var2, double var4, double var6, float var8, int var9) {
      float var10 = 0.6666667F;
      boolean var11 = bannerBlockEntity.getLevel() == null;
      GlStateManager.pushMatrix();
      ModelPart var14 = this.bannerModel.getPole();
      long var12;
      if(var11) {
         var12 = 0L;
         GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 0.5F, (float)var6 + 0.5F);
         var14.visible = true;
      } else {
         var12 = bannerBlockEntity.getLevel().getGameTime();
         BlockState var15 = bannerBlockEntity.getBlockState();
         if(var15.getBlock() instanceof BannerBlock) {
            GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 0.5F, (float)var6 + 0.5F);
            GlStateManager.rotatef((float)(-((Integer)var15.getValue(BannerBlock.ROTATION)).intValue() * 360) / 16.0F, 0.0F, 1.0F, 0.0F);
            var14.visible = true;
         } else {
            GlStateManager.translatef((float)var2 + 0.5F, (float)var4 - 0.16666667F, (float)var6 + 0.5F);
            GlStateManager.rotatef(-((Direction)var15.getValue(WallBannerBlock.FACING)).toYRot(), 0.0F, 1.0F, 0.0F);
            GlStateManager.translatef(0.0F, -0.3125F, -0.4375F);
            var14.visible = false;
         }
      }

      BlockPos var15 = bannerBlockEntity.getBlockPos();
      float var16 = (float)((long)(var15.getX() * 7 + var15.getY() * 9 + var15.getZ() * 13) + var12) + var8;
      this.bannerModel.getFlag().xRot = (-0.0125F + 0.01F * Mth.cos(var16 * 3.1415927F * 0.02F)) * 3.1415927F;
      GlStateManager.enableRescaleNormal();
      ResourceLocation var17 = this.getTextureLocation(bannerBlockEntity);
      if(var17 != null) {
         this.bindTexture(var17);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.6666667F, -0.6666667F, -0.6666667F);
         this.bannerModel.render();
         GlStateManager.popMatrix();
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   @Nullable
   private ResourceLocation getTextureLocation(BannerBlockEntity bannerBlockEntity) {
      return BannerTextures.BANNER_CACHE.getTextureLocation(bannerBlockEntity.getTextureHashName(), bannerBlockEntity.getPatterns(), bannerBlockEntity.getColors());
   }
}
