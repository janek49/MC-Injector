package net.minecraft.client.renderer.blockentity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

@ClientJarOnly
public abstract class BlockEntityRenderer {
   public static final ResourceLocation[] BREAKING_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/" + ModelBakery.DESTROY_STAGE_0.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.DESTROY_STAGE_1.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.DESTROY_STAGE_2.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.DESTROY_STAGE_3.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.DESTROY_STAGE_4.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.DESTROY_STAGE_5.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.DESTROY_STAGE_6.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.DESTROY_STAGE_7.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.DESTROY_STAGE_8.getPath() + ".png"), new ResourceLocation("textures/" + ModelBakery.DESTROY_STAGE_9.getPath() + ".png")};
   protected BlockEntityRenderDispatcher blockEntityRenderDispatcher;

   public void render(BlockEntity blockEntity, double var2, double var4, double var6, float var8, int var9) {
      HitResult var10 = this.blockEntityRenderDispatcher.cameraHitResult;
      if(blockEntity instanceof Nameable && var10 != null && var10.getType() == HitResult.Type.BLOCK && blockEntity.getBlockPos().equals(((BlockHitResult)var10).getBlockPos())) {
         this.setOverlayRenderState(true);
         this.renderNameTag(blockEntity, ((Nameable)blockEntity).getDisplayName().getColoredString(), var2, var4, var6, 12);
         this.setOverlayRenderState(false);
      }

   }

   protected void setOverlayRenderState(boolean overlayRenderState) {
      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      if(overlayRenderState) {
         GlStateManager.disableTexture();
      } else {
         GlStateManager.enableTexture();
      }

      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
   }

   protected void bindTexture(ResourceLocation resourceLocation) {
      TextureManager var2 = this.blockEntityRenderDispatcher.textureManager;
      if(var2 != null) {
         var2.bind(resourceLocation);
      }

   }

   protected Level getLevel() {
      return this.blockEntityRenderDispatcher.level;
   }

   public void init(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
      this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
   }

   public Font getFont() {
      return this.blockEntityRenderDispatcher.getFont();
   }

   public boolean shouldRenderOffScreen(BlockEntity blockEntity) {
      return false;
   }

   protected void renderNameTag(BlockEntity blockEntity, String string, double var3, double var5, double var7, int var9) {
      Camera var10 = this.blockEntityRenderDispatcher.camera;
      double var11 = blockEntity.distanceToSqr(var10.getPosition().x, var10.getPosition().y, var10.getPosition().z);
      if(var11 <= (double)(var9 * var9)) {
         float var13 = var10.getYRot();
         float var14 = var10.getXRot();
         GameRenderer.renderNameTagInWorld(this.getFont(), string, (float)var3 + 0.5F, (float)var5 + 1.5F, (float)var7 + 0.5F, 0, var13, var14, false);
      }
   }
}
