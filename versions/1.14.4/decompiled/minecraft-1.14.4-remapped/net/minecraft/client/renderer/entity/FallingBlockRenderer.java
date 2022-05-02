package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

@ClientJarOnly
public class FallingBlockRenderer extends EntityRenderer {
   public FallingBlockRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher);
      this.shadowRadius = 0.5F;
   }

   public void render(FallingBlockEntity fallingBlockEntity, double var2, double var4, double var6, float var8, float var9) {
      BlockState var10 = fallingBlockEntity.getBlockState();
      if(var10.getRenderShape() == RenderShape.MODEL) {
         Level var11 = fallingBlockEntity.getLevel();
         if(var10 != var11.getBlockState(new BlockPos(fallingBlockEntity)) && var10.getRenderShape() != RenderShape.INVISIBLE) {
            this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            Tesselator var12 = Tesselator.getInstance();
            BufferBuilder var13 = var12.getBuilder();
            if(this.solidRender) {
               GlStateManager.enableColorMaterial();
               GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(fallingBlockEntity));
            }

            var13.begin(7, DefaultVertexFormat.BLOCK);
            BlockPos var14 = new BlockPos(fallingBlockEntity.x, fallingBlockEntity.getBoundingBox().maxY, fallingBlockEntity.z);
            GlStateManager.translatef((float)(var2 - (double)var14.getX() - 0.5D), (float)(var4 - (double)var14.getY()), (float)(var6 - (double)var14.getZ() - 0.5D));
            BlockRenderDispatcher var15 = Minecraft.getInstance().getBlockRenderer();
            var15.getModelRenderer().tesselateBlock(var11, var15.getBlockModel(var10), var10, var14, var13, false, new Random(), var10.getSeed(fallingBlockEntity.getStartPos()));
            var12.end();
            if(this.solidRender) {
               GlStateManager.tearDownSolidRenderingTextureCombine();
               GlStateManager.disableColorMaterial();
            }

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
            super.render(fallingBlockEntity, var2, var4, var6, var8, var9);
         }
      }
   }

   protected ResourceLocation getTextureLocation(FallingBlockEntity fallingBlockEntity) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
