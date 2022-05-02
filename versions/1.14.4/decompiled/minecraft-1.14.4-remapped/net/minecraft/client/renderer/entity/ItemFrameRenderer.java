package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

@ClientJarOnly
public class ItemFrameRenderer extends EntityRenderer {
   private static final ResourceLocation MAP_BACKGROUND_LOCATION = new ResourceLocation("textures/map/map_background.png");
   private static final ModelResourceLocation FRAME_LOCATION = new ModelResourceLocation("item_frame", "map=false");
   private static final ModelResourceLocation MAP_FRAME_LOCATION = new ModelResourceLocation("item_frame", "map=true");
   private final Minecraft minecraft = Minecraft.getInstance();
   private final ItemRenderer itemRenderer;

   public ItemFrameRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
      super(entityRenderDispatcher);
      this.itemRenderer = itemRenderer;
   }

   public void render(ItemFrame itemFrame, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      BlockPos var10 = itemFrame.getPos();
      double var11 = (double)var10.getX() - itemFrame.x + var2;
      double var13 = (double)var10.getY() - itemFrame.y + var4;
      double var15 = (double)var10.getZ() - itemFrame.z + var6;
      GlStateManager.translated(var11 + 0.5D, var13 + 0.5D, var15 + 0.5D);
      GlStateManager.rotatef(itemFrame.xRot, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(180.0F - itemFrame.yRot, 0.0F, 1.0F, 0.0F);
      this.entityRenderDispatcher.textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
      BlockRenderDispatcher var17 = this.minecraft.getBlockRenderer();
      ModelManager var18 = var17.getBlockModelShaper().getModelManager();
      ModelResourceLocation var19 = itemFrame.getItem().getItem() == Items.FILLED_MAP?MAP_FRAME_LOCATION:FRAME_LOCATION;
      GlStateManager.pushMatrix();
      GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
      if(this.solidRender) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(itemFrame));
      }

      var17.getModelRenderer().renderModel(var18.getModel(var19), 1.0F, 1.0F, 1.0F, 1.0F);
      if(this.solidRender) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      GlStateManager.enableLighting();
      if(itemFrame.getItem().getItem() == Items.FILLED_MAP) {
         GlStateManager.pushLightingAttributes();
         Lighting.turnOn();
      }

      GlStateManager.translatef(0.0F, 0.0F, 0.4375F);
      this.drawItem(itemFrame);
      if(itemFrame.getItem().getItem() == Items.FILLED_MAP) {
         Lighting.turnOff();
         GlStateManager.popAttributes();
      }

      GlStateManager.enableLighting();
      GlStateManager.popMatrix();
      this.renderName(itemFrame, var2 + (double)((float)itemFrame.getDirection().getStepX() * 0.3F), var4 - 0.25D, var6 + (double)((float)itemFrame.getDirection().getStepZ() * 0.3F));
   }

   @Nullable
   protected ResourceLocation getTextureLocation(ItemFrame itemFrame) {
      return null;
   }

   private void drawItem(ItemFrame itemFrame) {
      ItemStack var2 = itemFrame.getItem();
      if(!var2.isEmpty()) {
         GlStateManager.pushMatrix();
         boolean var3 = var2.getItem() == Items.FILLED_MAP;
         int var4 = var3?itemFrame.getRotation() % 4 * 2:itemFrame.getRotation();
         GlStateManager.rotatef((float)var4 * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);
         if(var3) {
            GlStateManager.disableLighting();
            this.entityRenderDispatcher.textureManager.bind(MAP_BACKGROUND_LOCATION);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            float var5 = 0.0078125F;
            GlStateManager.scalef(0.0078125F, 0.0078125F, 0.0078125F);
            GlStateManager.translatef(-64.0F, -64.0F, 0.0F);
            MapItemSavedData var6 = MapItem.getOrCreateSavedData(var2, itemFrame.level);
            GlStateManager.translatef(0.0F, 0.0F, -1.0F);
            if(var6 != null) {
               this.minecraft.gameRenderer.getMapRenderer().render(var6, true);
            }
         } else {
            GlStateManager.scalef(0.5F, 0.5F, 0.5F);
            this.itemRenderer.renderStatic(var2, ItemTransforms.TransformType.FIXED);
         }

         GlStateManager.popMatrix();
      }
   }

   protected void renderName(ItemFrame itemFrame, double var2, double var4, double var6) {
      if(Minecraft.renderNames() && !itemFrame.getItem().isEmpty() && itemFrame.getItem().hasCustomHoverName() && this.entityRenderDispatcher.crosshairPickEntity == itemFrame) {
         double var8 = itemFrame.distanceToSqr(this.entityRenderDispatcher.camera.getPosition());
         float var10 = itemFrame.isVisuallySneaking()?32.0F:64.0F;
         if(var8 < (double)(var10 * var10)) {
            String var11 = itemFrame.getItem().getHoverName().getColoredString();
            this.renderNameTag(itemFrame, var11, var2, var4, var6, 64);
         }
      }
   }
}
