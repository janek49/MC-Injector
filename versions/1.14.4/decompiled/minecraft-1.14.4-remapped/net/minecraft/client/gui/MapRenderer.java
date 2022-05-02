package net.minecraft.client.gui;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

@ClientJarOnly
public class MapRenderer implements AutoCloseable {
   private static final ResourceLocation MAP_ICONS_LOCATION = new ResourceLocation("textures/map/map_icons.png");
   private final TextureManager textureManager;
   private final Map maps = Maps.newHashMap();

   public MapRenderer(TextureManager textureManager) {
      this.textureManager = textureManager;
   }

   public void update(MapItemSavedData mapItemSavedData) {
      this.getMapInstance(mapItemSavedData).updateTexture();
   }

   public void render(MapItemSavedData mapItemSavedData, boolean var2) {
      this.getMapInstance(mapItemSavedData).draw(var2);
   }

   private MapRenderer.MapInstance getMapInstance(MapItemSavedData mapItemSavedData) {
      MapRenderer.MapInstance mapRenderer$MapInstance = (MapRenderer.MapInstance)this.maps.get(mapItemSavedData.getId());
      if(mapRenderer$MapInstance == null) {
         mapRenderer$MapInstance = new MapRenderer.MapInstance(mapItemSavedData);
         this.maps.put(mapItemSavedData.getId(), mapRenderer$MapInstance);
      }

      return mapRenderer$MapInstance;
   }

   @Nullable
   public MapRenderer.MapInstance getMapInstanceIfExists(String string) {
      return (MapRenderer.MapInstance)this.maps.get(string);
   }

   public void resetData() {
      for(MapRenderer.MapInstance var2 : this.maps.values()) {
         var2.close();
      }

      this.maps.clear();
   }

   @Nullable
   public MapItemSavedData getData(@Nullable MapRenderer.MapInstance mapRenderer$MapInstance) {
      return mapRenderer$MapInstance != null?mapRenderer$MapInstance.data:null;
   }

   public void close() {
      this.resetData();
   }

   @ClientJarOnly
   class MapInstance implements AutoCloseable {
      private final MapItemSavedData data;
      private final DynamicTexture texture;
      private final ResourceLocation location;

      private MapInstance(MapItemSavedData data) {
         this.data = data;
         this.texture = new DynamicTexture(128, 128, true);
         this.location = MapRenderer.this.textureManager.register("map/" + data.getId(), this.texture);
      }

      private void updateTexture() {
         for(int var1 = 0; var1 < 128; ++var1) {
            for(int var2 = 0; var2 < 128; ++var2) {
               int var3 = var2 + var1 * 128;
               int var4 = this.data.colors[var3] & 255;
               if(var4 / 4 == 0) {
                  this.texture.getPixels().setPixelRGBA(var2, var1, (var3 + var3 / 128 & 1) * 8 + 16 << 24);
               } else {
                  this.texture.getPixels().setPixelRGBA(var2, var1, MaterialColor.MATERIAL_COLORS[var4 / 4].calculateRGBColor(var4 & 3));
               }
            }
         }

         this.texture.upload();
      }

      private void draw(boolean b) {
         int var2 = 0;
         int var3 = 0;
         Tesselator var4 = Tesselator.getInstance();
         BufferBuilder var5 = var4.getBuilder();
         float var6 = 0.0F;
         MapRenderer.this.textureManager.bind(this.location);
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         GlStateManager.disableAlphaTest();
         var5.begin(7, DefaultVertexFormat.POSITION_TEX);
         var5.vertex(0.0D, 128.0D, -0.009999999776482582D).uv(0.0D, 1.0D).endVertex();
         var5.vertex(128.0D, 128.0D, -0.009999999776482582D).uv(1.0D, 1.0D).endVertex();
         var5.vertex(128.0D, 0.0D, -0.009999999776482582D).uv(1.0D, 0.0D).endVertex();
         var5.vertex(0.0D, 0.0D, -0.009999999776482582D).uv(0.0D, 0.0D).endVertex();
         var4.end();
         GlStateManager.enableAlphaTest();
         GlStateManager.disableBlend();
         int var7 = 0;

         for(MapDecoration var9 : this.data.decorations.values()) {
            if(!b || var9.renderOnFrame()) {
               MapRenderer.this.textureManager.bind(MapRenderer.MAP_ICONS_LOCATION);
               GlStateManager.pushMatrix();
               GlStateManager.translatef(0.0F + (float)var9.getX() / 2.0F + 64.0F, 0.0F + (float)var9.getY() / 2.0F + 64.0F, -0.02F);
               GlStateManager.rotatef((float)(var9.getRot() * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.scalef(4.0F, 4.0F, 3.0F);
               GlStateManager.translatef(-0.125F, 0.125F, 0.0F);
               byte var10 = var9.getImage();
               float var11 = (float)(var10 % 16 + 0) / 16.0F;
               float var12 = (float)(var10 / 16 + 0) / 16.0F;
               float var13 = (float)(var10 % 16 + 1) / 16.0F;
               float var14 = (float)(var10 / 16 + 1) / 16.0F;
               var5.begin(7, DefaultVertexFormat.POSITION_TEX);
               GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               float var15 = -0.001F;
               var5.vertex(-1.0D, 1.0D, (double)((float)var7 * -0.001F)).uv((double)var11, (double)var12).endVertex();
               var5.vertex(1.0D, 1.0D, (double)((float)var7 * -0.001F)).uv((double)var13, (double)var12).endVertex();
               var5.vertex(1.0D, -1.0D, (double)((float)var7 * -0.001F)).uv((double)var13, (double)var14).endVertex();
               var5.vertex(-1.0D, -1.0D, (double)((float)var7 * -0.001F)).uv((double)var11, (double)var14).endVertex();
               var4.end();
               GlStateManager.popMatrix();
               if(var9.getName() != null) {
                  Font var16 = Minecraft.getInstance().font;
                  String var17 = var9.getName().getColoredString();
                  float var18 = (float)var16.width(var17);
                  float var10000 = 25.0F / var18;
                  var16.getClass();
                  float var19 = Mth.clamp(var10000, 0.0F, 6.0F / 9.0F);
                  GlStateManager.pushMatrix();
                  GlStateManager.translatef(0.0F + (float)var9.getX() / 2.0F + 64.0F - var18 * var19 / 2.0F, 0.0F + (float)var9.getY() / 2.0F + 64.0F + 4.0F, -0.025F);
                  GlStateManager.scalef(var19, var19, 1.0F);
                  int var10002 = (int)var18;
                  var16.getClass();
                  GuiComponent.fill(-1, -1, var10002, 9 - 1, Integer.MIN_VALUE);
                  GlStateManager.translatef(0.0F, 0.0F, -0.1F);
                  var16.draw(var17, 0.0F, 0.0F, -1);
                  GlStateManager.popMatrix();
               }

               ++var7;
            }
         }

         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, 0.0F, -0.04F);
         GlStateManager.scalef(1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
      }

      public void close() {
         this.texture.close();
      }
   }
}
