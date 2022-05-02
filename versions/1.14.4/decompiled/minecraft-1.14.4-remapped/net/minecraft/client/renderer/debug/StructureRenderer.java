package net.minecraft.client.renderer.debug;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

@ClientJarOnly
public class StructureRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private final Map postMainBoxes = Maps.newIdentityHashMap();
   private final Map postPiecesBoxes = Maps.newIdentityHashMap();
   private final Map startPiecesMap = Maps.newIdentityHashMap();

   public StructureRenderer(Minecraft minecraft) {
      this.minecraft = minecraft;
   }

   public void render(long l) {
      Camera var3 = this.minecraft.gameRenderer.getMainCamera();
      LevelAccessor var4 = this.minecraft.level;
      DimensionType var5 = var4.getDimension().getType();
      double var6 = var3.getPosition().x;
      double var8 = var3.getPosition().y;
      double var10 = var3.getPosition().z;
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.disableTexture();
      GlStateManager.disableDepthTest();
      BlockPos var12 = new BlockPos(var3.getPosition().x, 0.0D, var3.getPosition().z);
      Tesselator var13 = Tesselator.getInstance();
      BufferBuilder var14 = var13.getBuilder();
      var14.begin(3, DefaultVertexFormat.POSITION_COLOR);
      GlStateManager.lineWidth(1.0F);
      if(this.postMainBoxes.containsKey(var5)) {
         for(BoundingBox var16 : ((Map)this.postMainBoxes.get(var5)).values()) {
            if(var12.closerThan(var16.getCenter(), 500.0D)) {
               LevelRenderer.addChainedLineBoxVertices(var14, (double)var16.x0 - var6, (double)var16.y0 - var8, (double)var16.z0 - var10, (double)(var16.x1 + 1) - var6, (double)(var16.y1 + 1) - var8, (double)(var16.z1 + 1) - var10, 1.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      }

      if(this.postPiecesBoxes.containsKey(var5)) {
         for(Entry<String, BoundingBox> var16 : ((Map)this.postPiecesBoxes.get(var5)).entrySet()) {
            String var17 = (String)var16.getKey();
            BoundingBox var18 = (BoundingBox)var16.getValue();
            Boolean var19 = (Boolean)((Map)this.startPiecesMap.get(var5)).get(var17);
            if(var12.closerThan(var18.getCenter(), 500.0D)) {
               if(var19.booleanValue()) {
                  LevelRenderer.addChainedLineBoxVertices(var14, (double)var18.x0 - var6, (double)var18.y0 - var8, (double)var18.z0 - var10, (double)(var18.x1 + 1) - var6, (double)(var18.y1 + 1) - var8, (double)(var18.z1 + 1) - var10, 0.0F, 1.0F, 0.0F, 1.0F);
               } else {
                  LevelRenderer.addChainedLineBoxVertices(var14, (double)var18.x0 - var6, (double)var18.y0 - var8, (double)var18.z0 - var10, (double)(var18.x1 + 1) - var6, (double)(var18.y1 + 1) - var8, (double)(var18.z1 + 1) - var10, 0.0F, 0.0F, 1.0F, 1.0F);
               }
            }
         }
      }

      var13.end();
      GlStateManager.enableDepthTest();
      GlStateManager.enableTexture();
      GlStateManager.popMatrix();
   }

   public void addBoundingBox(BoundingBox boundingBox, List var2, List var3, DimensionType dimensionType) {
      if(!this.postMainBoxes.containsKey(dimensionType)) {
         this.postMainBoxes.put(dimensionType, Maps.newHashMap());
      }

      if(!this.postPiecesBoxes.containsKey(dimensionType)) {
         this.postPiecesBoxes.put(dimensionType, Maps.newHashMap());
         this.startPiecesMap.put(dimensionType, Maps.newHashMap());
      }

      ((Map)this.postMainBoxes.get(dimensionType)).put(boundingBox.toString(), boundingBox);

      for(int var5 = 0; var5 < var2.size(); ++var5) {
         BoundingBox var6 = (BoundingBox)var2.get(var5);
         Boolean var7 = (Boolean)var3.get(var5);
         ((Map)this.postPiecesBoxes.get(dimensionType)).put(var6.toString(), var6);
         ((Map)this.startPiecesMap.get(dimensionType)).put(var6.toString(), var7);
      }

   }

   public void clear() {
      this.postMainBoxes.clear();
      this.postPiecesBoxes.clear();
      this.startPiecesMap.clear();
   }
}
