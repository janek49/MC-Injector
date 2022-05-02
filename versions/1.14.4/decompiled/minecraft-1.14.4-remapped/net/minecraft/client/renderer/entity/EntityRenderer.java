package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.culling.Culler;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.PlayerTeam;

@ClientJarOnly
public abstract class EntityRenderer {
   private static final ResourceLocation SHADOW_LOCATION = new ResourceLocation("textures/misc/shadow.png");
   protected final EntityRenderDispatcher entityRenderDispatcher;
   protected float shadowRadius;
   protected float shadowStrength = 1.0F;
   protected boolean solidRender;

   protected EntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      this.entityRenderDispatcher = entityRenderDispatcher;
   }

   public void setSolidRender(boolean solidRender) {
      this.solidRender = solidRender;
   }

   public boolean shouldRender(Entity entity, Culler culler, double var3, double var5, double var7) {
      if(!entity.shouldRender(var3, var5, var7)) {
         return false;
      } else if(entity.noCulling) {
         return true;
      } else {
         AABB var9 = entity.getBoundingBoxForCulling().inflate(0.5D);
         if(var9.hasNaN() || var9.getSize() == 0.0D) {
            var9 = new AABB(entity.x - 2.0D, entity.y - 2.0D, entity.z - 2.0D, entity.x + 2.0D, entity.y + 2.0D, entity.z + 2.0D);
         }

         return culler.isVisible(var9);
      }
   }

   public void render(Entity entity, double var2, double var4, double var6, float var8, float var9) {
      if(!this.solidRender) {
         this.renderName(entity, var2, var4, var6);
      }

   }

   protected int getTeamColor(Entity entity) {
      PlayerTeam var2 = (PlayerTeam)entity.getTeam();
      return var2 != null && var2.getColor().getColor() != null?var2.getColor().getColor().intValue():16777215;
   }

   protected void renderName(Entity entity, double var2, double var4, double var6) {
      if(this.shouldShowName(entity)) {
         this.renderNameTag(entity, entity.getDisplayName().getColoredString(), var2, var4, var6, 64);
      }
   }

   protected boolean shouldShowName(Entity entity) {
      return entity.shouldShowName() && entity.hasCustomName();
   }

   protected void renderNameTags(Entity entity, double var2, double var4, double var6, String string, double var9) {
      this.renderNameTag(entity, string, var2, var4, var6, 64);
   }

   @Nullable
   protected abstract ResourceLocation getTextureLocation(Entity var1);

   protected boolean bindTexture(Entity entity) {
      ResourceLocation var2 = this.getTextureLocation(entity);
      if(var2 == null) {
         return false;
      } else {
         this.bindTexture(var2);
         return true;
      }
   }

   public void bindTexture(ResourceLocation resourceLocation) {
      this.entityRenderDispatcher.textureManager.bind(resourceLocation);
   }

   private void renderFlame(Entity entity, double var2, double var4, double var6, float var8) {
      GlStateManager.disableLighting();
      TextureAtlas var9 = Minecraft.getInstance().getTextureAtlas();
      TextureAtlasSprite var10 = var9.getSprite(ModelBakery.FIRE_0);
      TextureAtlasSprite var11 = var9.getSprite(ModelBakery.FIRE_1);
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)var2, (float)var4, (float)var6);
      float var12 = entity.getBbWidth() * 1.4F;
      GlStateManager.scalef(var12, var12, var12);
      Tesselator var13 = Tesselator.getInstance();
      BufferBuilder var14 = var13.getBuilder();
      float var15 = 0.5F;
      float var16 = 0.0F;
      float var17 = entity.getBbHeight() / var12;
      float var18 = (float)(entity.y - entity.getBoundingBox().minY);
      GlStateManager.rotatef(-this.entityRenderDispatcher.playerRotY, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(0.0F, 0.0F, -0.3F + (float)((int)var17) * 0.02F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float var19 = 0.0F;
      int var20 = 0;
      var14.begin(7, DefaultVertexFormat.POSITION_TEX);

      while(var17 > 0.0F) {
         TextureAtlasSprite var21 = var20 % 2 == 0?var10:var11;
         this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
         float var22 = var21.getU0();
         float var23 = var21.getV0();
         float var24 = var21.getU1();
         float var25 = var21.getV1();
         if(var20 / 2 % 2 == 0) {
            float var26 = var24;
            var24 = var22;
            var22 = var26;
         }

         var14.vertex((double)(var15 - 0.0F), (double)(0.0F - var18), (double)var19).uv((double)var24, (double)var25).endVertex();
         var14.vertex((double)(-var15 - 0.0F), (double)(0.0F - var18), (double)var19).uv((double)var22, (double)var25).endVertex();
         var14.vertex((double)(-var15 - 0.0F), (double)(1.4F - var18), (double)var19).uv((double)var22, (double)var23).endVertex();
         var14.vertex((double)(var15 - 0.0F), (double)(1.4F - var18), (double)var19).uv((double)var24, (double)var23).endVertex();
         var17 -= 0.45F;
         var18 -= 0.45F;
         var15 *= 0.9F;
         var19 += 0.03F;
         ++var20;
      }

      var13.end();
      GlStateManager.popMatrix();
      GlStateManager.enableLighting();
   }

   private void renderShadow(Entity entity, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      this.entityRenderDispatcher.textureManager.bind(SHADOW_LOCATION);
      LevelReader var10 = this.getLevel();
      GlStateManager.depthMask(false);
      float var11 = this.shadowRadius;
      if(entity instanceof Mob) {
         Mob var12 = (Mob)entity;
         if(var12.isBaby()) {
            var11 *= 0.5F;
         }
      }

      double var12 = Mth.lerp((double)var9, entity.xOld, entity.x);
      double var14 = Mth.lerp((double)var9, entity.yOld, entity.y);
      double var16 = Mth.lerp((double)var9, entity.zOld, entity.z);
      int var18 = Mth.floor(var12 - (double)var11);
      int var19 = Mth.floor(var12 + (double)var11);
      int var20 = Mth.floor(var14 - (double)var11);
      int var21 = Mth.floor(var14);
      int var22 = Mth.floor(var16 - (double)var11);
      int var23 = Mth.floor(var16 + (double)var11);
      double var24 = var2 - var12;
      double var26 = var4 - var14;
      double var28 = var6 - var16;
      Tesselator var30 = Tesselator.getInstance();
      BufferBuilder var31 = var30.getBuilder();
      var31.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);

      for(BlockPos var33 : BlockPos.betweenClosed(new BlockPos(var18, var20, var22), new BlockPos(var19, var21, var23))) {
         BlockPos var34 = var33.below();
         BlockState var35 = var10.getBlockState(var34);
         if(var35.getRenderShape() != RenderShape.INVISIBLE && var10.getMaxLocalRawBrightness(var33) > 3) {
            this.renderBlockShadow(var35, var10, var34, var2, var4, var6, var33, var8, var11, var24, var26, var28);
         }
      }

      var30.end();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
   }

   private LevelReader getLevel() {
      return this.entityRenderDispatcher.level;
   }

   private void renderBlockShadow(BlockState blockState, LevelReader levelReader, BlockPos var3, double var4, double var6, double var8, BlockPos var10, float var11, float var12, double var13, double var15, double var17) {
      if(blockState.isCollisionShapeFullBlock(levelReader, var3)) {
         VoxelShape var19 = blockState.getShape(this.getLevel(), var10.below());
         if(!var19.isEmpty()) {
            Tesselator var20 = Tesselator.getInstance();
            BufferBuilder var21 = var20.getBuilder();
            double var22 = ((double)var11 - (var6 - ((double)var10.getY() + var15)) / 2.0D) * 0.5D * (double)this.getLevel().getBrightness(var10);
            if(var22 >= 0.0D) {
               if(var22 > 1.0D) {
                  var22 = 1.0D;
               }

               AABB var24 = var19.bounds();
               double var25 = (double)var10.getX() + var24.minX + var13;
               double var27 = (double)var10.getX() + var24.maxX + var13;
               double var29 = (double)var10.getY() + var24.minY + var15 + 0.015625D;
               double var31 = (double)var10.getZ() + var24.minZ + var17;
               double var33 = (double)var10.getZ() + var24.maxZ + var17;
               float var35 = (float)((var4 - var25) / 2.0D / (double)var12 + 0.5D);
               float var36 = (float)((var4 - var27) / 2.0D / (double)var12 + 0.5D);
               float var37 = (float)((var8 - var31) / 2.0D / (double)var12 + 0.5D);
               float var38 = (float)((var8 - var33) / 2.0D / (double)var12 + 0.5D);
               var21.vertex(var25, var29, var31).uv((double)var35, (double)var37).color(1.0F, 1.0F, 1.0F, (float)var22).endVertex();
               var21.vertex(var25, var29, var33).uv((double)var35, (double)var38).color(1.0F, 1.0F, 1.0F, (float)var22).endVertex();
               var21.vertex(var27, var29, var33).uv((double)var36, (double)var38).color(1.0F, 1.0F, 1.0F, (float)var22).endVertex();
               var21.vertex(var27, var29, var31).uv((double)var36, (double)var37).color(1.0F, 1.0F, 1.0F, (float)var22).endVertex();
            }
         }
      }
   }

   public static void render(AABB aABB, double var1, double var3, double var5) {
      GlStateManager.disableTexture();
      Tesselator var7 = Tesselator.getInstance();
      BufferBuilder var8 = var7.getBuilder();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      var8.offset(var1, var3, var5);
      var8.begin(7, DefaultVertexFormat.POSITION_NORMAL);
      var8.vertex(aABB.minX, aABB.maxY, aABB.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      var8.vertex(aABB.maxX, aABB.maxY, aABB.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      var8.vertex(aABB.maxX, aABB.minY, aABB.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      var8.vertex(aABB.minX, aABB.minY, aABB.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      var8.vertex(aABB.minX, aABB.minY, aABB.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      var8.vertex(aABB.maxX, aABB.minY, aABB.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      var8.vertex(aABB.maxX, aABB.maxY, aABB.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      var8.vertex(aABB.minX, aABB.maxY, aABB.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      var8.vertex(aABB.minX, aABB.minY, aABB.minZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      var8.vertex(aABB.maxX, aABB.minY, aABB.minZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      var8.vertex(aABB.maxX, aABB.minY, aABB.maxZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      var8.vertex(aABB.minX, aABB.minY, aABB.maxZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      var8.vertex(aABB.minX, aABB.maxY, aABB.maxZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      var8.vertex(aABB.maxX, aABB.maxY, aABB.maxZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      var8.vertex(aABB.maxX, aABB.maxY, aABB.minZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      var8.vertex(aABB.minX, aABB.maxY, aABB.minZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      var8.vertex(aABB.minX, aABB.minY, aABB.maxZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      var8.vertex(aABB.minX, aABB.maxY, aABB.maxZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      var8.vertex(aABB.minX, aABB.maxY, aABB.minZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      var8.vertex(aABB.minX, aABB.minY, aABB.minZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      var8.vertex(aABB.maxX, aABB.minY, aABB.minZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      var8.vertex(aABB.maxX, aABB.maxY, aABB.minZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      var8.vertex(aABB.maxX, aABB.maxY, aABB.maxZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      var8.vertex(aABB.maxX, aABB.minY, aABB.maxZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      var7.end();
      var8.offset(0.0D, 0.0D, 0.0D);
      GlStateManager.enableTexture();
   }

   public void postRender(Entity entity, double var2, double var4, double var6, float var8, float var9) {
      if(this.entityRenderDispatcher.options != null) {
         if(this.entityRenderDispatcher.options.entityShadows && this.shadowRadius > 0.0F && !entity.isInvisible() && this.entityRenderDispatcher.shouldRenderShadow()) {
            double var10 = this.entityRenderDispatcher.distanceToSqr(entity.x, entity.y, entity.z);
            float var12 = (float)((1.0D - var10 / 256.0D) * (double)this.shadowStrength);
            if(var12 > 0.0F) {
               this.renderShadow(entity, var2, var4, var6, var12, var9);
            }
         }

         if(entity.displayFireAnimation() && !entity.isSpectator()) {
            this.renderFlame(entity, var2, var4, var6, var9);
         }

      }
   }

   public Font getFont() {
      return this.entityRenderDispatcher.getFont();
   }

   protected void renderNameTag(Entity entity, String string, double var3, double var5, double var7, int var9) {
      double var10 = entity.distanceToSqr(this.entityRenderDispatcher.camera.getPosition());
      if(var10 <= (double)(var9 * var9)) {
         boolean var12 = entity.isVisuallySneaking();
         float var13 = this.entityRenderDispatcher.playerRotY;
         float var14 = this.entityRenderDispatcher.playerRotX;
         float var15 = entity.getBbHeight() + 0.5F - (var12?0.25F:0.0F);
         int var16 = "deadmau5".equals(string)?-10:0;
         GameRenderer.renderNameTagInWorld(this.getFont(), string, (float)var3, (float)var5 + var15, (float)var7, var16, var13, var14, var12);
      }
   }

   public EntityRenderDispatcher getDispatcher() {
      return this.entityRenderDispatcher;
   }

   public boolean hasSecondPass() {
      return false;
   }

   public void renderSecondPass(Entity entity, double var2, double var4, double var6, float var8, float var9) {
   }

   public void setLightColor(Entity lightColor) {
      int var2 = lightColor.getLightColor();
      int var3 = var2 % 65536;
      int var4 = var2 / 65536;
      GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)var3, (float)var4);
   }
}
