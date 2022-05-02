package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.EntityBlockRenderer;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@ClientJarOnly
public class ItemRenderer implements ResourceManagerReloadListener {
   public static final ResourceLocation ENCHANT_GLINT_LOCATION = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   private static final Set IGNORED = Sets.newHashSet(new Item[]{Items.AIR});
   public float blitOffset;
   private final ItemModelShaper itemModelShaper;
   private final TextureManager textureManager;
   private final ItemColors itemColors;

   public ItemRenderer(TextureManager textureManager, ModelManager modelManager, ItemColors itemColors) {
      this.textureManager = textureManager;
      this.itemModelShaper = new ItemModelShaper(modelManager);

      for(Item var5 : Registry.ITEM) {
         if(!IGNORED.contains(var5)) {
            this.itemModelShaper.register(var5, new ModelResourceLocation(Registry.ITEM.getKey(var5), "inventory"));
         }
      }

      this.itemColors = itemColors;
   }

   public ItemModelShaper getItemModelShaper() {
      return this.itemModelShaper;
   }

   private void renderModelLists(BakedModel bakedModel, ItemStack itemStack) {
      this.renderModelLists(bakedModel, -1, itemStack);
   }

   private void renderModelLists(BakedModel bakedModel, int var2) {
      this.renderModelLists(bakedModel, var2, ItemStack.EMPTY);
   }

   private void renderModelLists(BakedModel bakedModel, int var2, ItemStack itemStack) {
      Tesselator var4 = Tesselator.getInstance();
      BufferBuilder var5 = var4.getBuilder();
      var5.begin(7, DefaultVertexFormat.BLOCK_NORMALS);
      Random var6 = new Random();
      long var7 = 42L;

      for(Direction var12 : Direction.values()) {
         var6.setSeed(42L);
         this.renderQuadList(var5, bakedModel.getQuads((BlockState)null, var12, var6), var2, itemStack);
      }

      var6.setSeed(42L);
      this.renderQuadList(var5, bakedModel.getQuads((BlockState)null, (Direction)null, var6), var2, itemStack);
      var4.end();
   }

   public void render(ItemStack itemStack, BakedModel bakedModel) {
      if(!itemStack.isEmpty()) {
         GlStateManager.pushMatrix();
         GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
         if(bakedModel.isCustomRenderer()) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            EntityBlockRenderer.instance.renderByItem(itemStack);
         } else {
            this.renderModelLists(bakedModel, itemStack);
            if(itemStack.hasFoil()) {
               renderFoilLayer(this.textureManager, () -> {
                  this.renderModelLists(bakedModel, -8372020);
               }, 8);
            }
         }

         GlStateManager.popMatrix();
      }
   }

   public static void renderFoilLayer(TextureManager textureManager, Runnable runnable, int var2) {
      GlStateManager.depthMask(false);
      GlStateManager.depthFunc(514);
      GlStateManager.disableLighting();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
      textureManager.bind(ENCHANT_GLINT_LOCATION);
      GlStateManager.matrixMode(5890);
      GlStateManager.pushMatrix();
      GlStateManager.scalef((float)var2, (float)var2, (float)var2);
      float var3 = (float)(Util.getMillis() % 3000L) / 3000.0F / (float)var2;
      GlStateManager.translatef(var3, 0.0F, 0.0F);
      GlStateManager.rotatef(-50.0F, 0.0F, 0.0F, 1.0F);
      runnable.run();
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.scalef((float)var2, (float)var2, (float)var2);
      float var4 = (float)(Util.getMillis() % 4873L) / 4873.0F / (float)var2;
      GlStateManager.translatef(-var4, 0.0F, 0.0F);
      GlStateManager.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
      runnable.run();
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.enableLighting();
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(true);
      textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
   }

   private void applyNormal(BufferBuilder bufferBuilder, BakedQuad bakedQuad) {
      Vec3i var3 = bakedQuad.getDirection().getNormal();
      bufferBuilder.postNormal((float)var3.getX(), (float)var3.getY(), (float)var3.getZ());
   }

   private void putQuadData(BufferBuilder bufferBuilder, BakedQuad bakedQuad, int var3) {
      bufferBuilder.putBulkData(bakedQuad.getVertices());
      bufferBuilder.fixupQuadColor(var3);
      this.applyNormal(bufferBuilder, bakedQuad);
   }

   private void renderQuadList(BufferBuilder bufferBuilder, List list, int var3, ItemStack itemStack) {
      boolean var5 = var3 == -1 && !itemStack.isEmpty();
      int var6 = 0;

      for(int var7 = list.size(); var6 < var7; ++var6) {
         BakedQuad var8 = (BakedQuad)list.get(var6);
         int var9 = var3;
         if(var5 && var8.isTinted()) {
            var9 = this.itemColors.getColor(itemStack, var8.getTintIndex());
            var9 = var9 | -16777216;
         }

         this.putQuadData(bufferBuilder, var8, var9);
      }

   }

   public boolean isGui3d(ItemStack itemStack) {
      BakedModel var2 = this.itemModelShaper.getItemModel(itemStack);
      return var2 == null?false:var2.isGui3d();
   }

   public void renderStatic(ItemStack itemStack, ItemTransforms.TransformType itemTransforms$TransformType) {
      if(!itemStack.isEmpty()) {
         BakedModel var3 = this.getModel(itemStack);
         this.renderStatic(itemStack, var3, itemTransforms$TransformType, false);
      }
   }

   public BakedModel getModel(ItemStack itemStack, @Nullable Level level, @Nullable LivingEntity livingEntity) {
      BakedModel bakedModel = this.itemModelShaper.getItemModel(itemStack);
      Item var5 = itemStack.getItem();
      return !var5.hasProperties()?bakedModel:this.resolveOverrides(bakedModel, itemStack, level, livingEntity);
   }

   public BakedModel getInHandModel(ItemStack itemStack, Level level, LivingEntity livingEntity) {
      Item var5 = itemStack.getItem();
      BakedModel bakedModel;
      if(var5 == Items.TRIDENT) {
         bakedModel = this.itemModelShaper.getModelManager().getModel(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
      } else {
         bakedModel = this.itemModelShaper.getItemModel(itemStack);
      }

      return !var5.hasProperties()?bakedModel:this.resolveOverrides(bakedModel, itemStack, level, livingEntity);
   }

   public BakedModel getModel(ItemStack itemStack) {
      return this.getModel(itemStack, (Level)null, (LivingEntity)null);
   }

   private BakedModel resolveOverrides(BakedModel var1, ItemStack itemStack, @Nullable Level level, @Nullable LivingEntity livingEntity) {
      BakedModel var5 = var1.getOverrides().resolve(var1, itemStack, level, livingEntity);
      return var5 == null?this.itemModelShaper.getModelManager().getMissingModel():var5;
   }

   public void renderWithMobState(ItemStack itemStack, LivingEntity livingEntity, ItemTransforms.TransformType itemTransforms$TransformType, boolean var4) {
      if(!itemStack.isEmpty() && livingEntity != null) {
         BakedModel var5 = this.getInHandModel(itemStack, livingEntity.level, livingEntity);
         this.renderStatic(itemStack, var5, itemTransforms$TransformType, var4);
      }
   }

   protected void renderStatic(ItemStack itemStack, BakedModel bakedModel, ItemTransforms.TransformType itemTransforms$TransformType, boolean var4) {
      if(!itemStack.isEmpty()) {
         this.textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
         this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).pushFilter(false, false);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableRescaleNormal();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.pushMatrix();
         ItemTransforms var5 = bakedModel.getTransforms();
         ItemTransforms.apply(var5.getTransform(itemTransforms$TransformType), var4);
         if(this.needsFlip(var5.getTransform(itemTransforms$TransformType))) {
            GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
         }

         this.render(itemStack, bakedModel);
         GlStateManager.cullFace(GlStateManager.CullFace.BACK);
         GlStateManager.popMatrix();
         GlStateManager.disableRescaleNormal();
         GlStateManager.disableBlend();
         this.textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
         this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).popFilter();
      }
   }

   private boolean needsFlip(ItemTransform itemTransform) {
      return itemTransform.scale.x() < 0.0F ^ itemTransform.scale.y() < 0.0F ^ itemTransform.scale.z() < 0.0F;
   }

   public void renderGuiItem(ItemStack itemStack, int var2, int var3) {
      this.renderGuiItem(itemStack, var2, var3, this.getModel(itemStack));
   }

   protected void renderGuiItem(ItemStack itemStack, int var2, int var3, BakedModel bakedModel) {
      GlStateManager.pushMatrix();
      this.textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
      this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).pushFilter(false, false);
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableAlphaTest();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.setupGuiItem(var2, var3, bakedModel.isGui3d());
      bakedModel.getTransforms().apply(ItemTransforms.TransformType.GUI);
      this.render(itemStack, bakedModel);
      GlStateManager.disableAlphaTest();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableLighting();
      GlStateManager.popMatrix();
      this.textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
      this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).popFilter();
   }

   private void setupGuiItem(int var1, int var2, boolean var3) {
      GlStateManager.translatef((float)var1, (float)var2, 100.0F + this.blitOffset);
      GlStateManager.translatef(8.0F, 8.0F, 0.0F);
      GlStateManager.scalef(1.0F, -1.0F, 1.0F);
      GlStateManager.scalef(16.0F, 16.0F, 16.0F);
      if(var3) {
         GlStateManager.enableLighting();
      } else {
         GlStateManager.disableLighting();
      }

   }

   public void renderAndDecorateItem(ItemStack itemStack, int var2, int var3) {
      this.renderAndDecorateItem(Minecraft.getInstance().player, itemStack, var2, var3);
   }

   public void renderAndDecorateItem(@Nullable LivingEntity livingEntity, ItemStack itemStack, int var3, int var4) {
      if(!itemStack.isEmpty()) {
         this.blitOffset += 50.0F;

         try {
            this.renderGuiItem(itemStack, var3, var4, this.getModel(itemStack, (Level)null, livingEntity));
         } catch (Throwable var8) {
            CrashReport var6 = CrashReport.forThrowable(var8, "Rendering item");
            CrashReportCategory var7 = var6.addCategory("Item being rendered");
            var7.setDetail("Item Type", () -> {
               return String.valueOf(itemStack.getItem());
            });
            var7.setDetail("Item Damage", () -> {
               return String.valueOf(itemStack.getDamageValue());
            });
            var7.setDetail("Item NBT", () -> {
               return String.valueOf(itemStack.getTag());
            });
            var7.setDetail("Item Foil", () -> {
               return String.valueOf(itemStack.hasFoil());
            });
            throw new ReportedException(var6);
         }

         this.blitOffset -= 50.0F;
      }
   }

   public void renderGuiItemDecorations(Font font, ItemStack itemStack, int var3, int var4) {
      this.renderGuiItemDecorations(font, itemStack, var3, var4, (String)null);
   }

   public void renderGuiItemDecorations(Font font, ItemStack itemStack, int var3, int var4, @Nullable String string) {
      if(!itemStack.isEmpty()) {
         if(itemStack.getCount() != 1 || string != null) {
            String string = string == null?String.valueOf(itemStack.getCount()):string;
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableBlend();
            font.drawShadow(string, (float)(var3 + 19 - 2 - font.width(string)), (float)(var4 + 6 + 3), 16777215);
            GlStateManager.enableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
         }

         if(itemStack.isDamaged()) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableTexture();
            GlStateManager.disableAlphaTest();
            GlStateManager.disableBlend();
            Tesselator var6 = Tesselator.getInstance();
            BufferBuilder var7 = var6.getBuilder();
            float var8 = (float)itemStack.getDamageValue();
            float var9 = (float)itemStack.getMaxDamage();
            float var10 = Math.max(0.0F, (var9 - var8) / var9);
            int var11 = Math.round(13.0F - var8 * 13.0F / var9);
            int var12 = Mth.hsvToRgb(var10 / 3.0F, 1.0F, 1.0F);
            this.fillRect(var7, var3 + 2, var4 + 13, 13, 2, 0, 0, 0, 255);
            this.fillRect(var7, var3 + 2, var4 + 13, var11, 1, var12 >> 16 & 255, var12 >> 8 & 255, var12 & 255, 255);
            GlStateManager.enableBlend();
            GlStateManager.enableAlphaTest();
            GlStateManager.enableTexture();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
         }

         LocalPlayer var6 = Minecraft.getInstance().player;
         float var7 = var6 == null?0.0F:var6.getCooldowns().getCooldownPercent(itemStack.getItem(), Minecraft.getInstance().getFrameTime());
         if(var7 > 0.0F) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            GlStateManager.disableTexture();
            Tesselator var8 = Tesselator.getInstance();
            BufferBuilder var9 = var8.getBuilder();
            this.fillRect(var9, var3, var4 + Mth.floor(16.0F * (1.0F - var7)), 16, Mth.ceil(16.0F * var7), 255, 255, 255, 127);
            GlStateManager.enableTexture();
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
         }

      }
   }

   private void fillRect(BufferBuilder bufferBuilder, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      bufferBuilder.begin(7, DefaultVertexFormat.POSITION_COLOR);
      bufferBuilder.vertex((double)(var2 + 0), (double)(var3 + 0), 0.0D).color(var6, var7, var8, var9).endVertex();
      bufferBuilder.vertex((double)(var2 + 0), (double)(var3 + var5), 0.0D).color(var6, var7, var8, var9).endVertex();
      bufferBuilder.vertex((double)(var2 + var4), (double)(var3 + var5), 0.0D).color(var6, var7, var8, var9).endVertex();
      bufferBuilder.vertex((double)(var2 + var4), (double)(var3 + 0), 0.0D).color(var6, var7, var8, var9).endVertex();
      Tesselator.getInstance().end();
   }

   public void onResourceManagerReload(ResourceManager resourceManager) {
      this.itemModelShaper.rebuildCache();
   }
}
