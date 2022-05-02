package net.minecraft.client.renderer.debug;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Locale;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;

@ClientJarOnly
public class PathfindingRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private final Map pathMap = Maps.newHashMap();
   private final Map pathMaxDist = Maps.newHashMap();
   private final Map creationMap = Maps.newHashMap();

   public PathfindingRenderer(Minecraft minecraft) {
      this.minecraft = minecraft;
   }

   public void addPath(int var1, Path path, float var3) {
      this.pathMap.put(Integer.valueOf(var1), path);
      this.creationMap.put(Integer.valueOf(var1), Long.valueOf(Util.getMillis()));
      this.pathMaxDist.put(Integer.valueOf(var1), Float.valueOf(var3));
   }

   public void render(long l) {
      if(!this.pathMap.isEmpty()) {
         long var3 = Util.getMillis();

         for(Integer var6 : this.pathMap.keySet()) {
            Path var7 = (Path)this.pathMap.get(var6);
            float var8 = ((Float)this.pathMaxDist.get(var6)).floatValue();
            renderPath(this.getCamera(), var7, var8, true, true);
         }

         for(Integer var8 : (Integer[])this.creationMap.keySet().toArray(new Integer[0])) {
            if(var3 - ((Long)this.creationMap.get(var8)).longValue() > 20000L) {
               this.pathMap.remove(var8);
               this.creationMap.remove(var8);
            }
         }

      }
   }

   public static void renderPath(Camera camera, Path path, float var2, boolean var3, boolean var4) {
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
      GlStateManager.disableTexture();
      GlStateManager.lineWidth(6.0F);
      doRenderPath(camera, path, var2, var3, var4);
      GlStateManager.enableTexture();
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
   }

   private static void doRenderPath(Camera camera, Path path, float var2, boolean var3, boolean var4) {
      renderPathLine(camera, path);
      double var5 = camera.getPosition().x;
      double var7 = camera.getPosition().y;
      double var9 = camera.getPosition().z;
      BlockPos var11 = path.getTarget();
      if(distanceToCamera(camera, var11) <= 40.0F) {
         DebugRenderer.renderFilledBox((new AABB((double)((float)var11.getX() + 0.25F), (double)((float)var11.getY() + 0.25F), (double)var11.getZ() + 0.25D, (double)((float)var11.getX() + 0.75F), (double)((float)var11.getY() + 0.75F), (double)((float)var11.getZ() + 0.75F))).move(-var5, -var7, -var9), 0.0F, 1.0F, 0.0F, 0.5F);

         for(int var12 = 0; var12 < path.getSize(); ++var12) {
            Node var13 = path.get(var12);
            if(distanceToCamera(camera, var13.asBlockPos()) <= 40.0F) {
               float var14 = var12 == path.getIndex()?1.0F:0.0F;
               float var15 = var12 == path.getIndex()?0.0F:1.0F;
               DebugRenderer.renderFilledBox((new AABB((double)((float)var13.x + 0.5F - var2), (double)((float)var13.y + 0.01F * (float)var12), (double)((float)var13.z + 0.5F - var2), (double)((float)var13.x + 0.5F + var2), (double)((float)var13.y + 0.25F + 0.01F * (float)var12), (double)((float)var13.z + 0.5F + var2))).move(-var5, -var7, -var9), var14, 0.0F, var15, 0.5F);
            }
         }
      }

      if(var3) {
         for(Node var15 : path.getClosedSet()) {
            if(distanceToCamera(camera, var15.asBlockPos()) <= 40.0F) {
               DebugRenderer.renderFloatingText(String.format("%s", new Object[]{var15.type}), (double)var15.x + 0.5D, (double)var15.y + 0.75D, (double)var15.z + 0.5D, -65536);
               DebugRenderer.renderFloatingText(String.format(Locale.ROOT, "%.2f", new Object[]{Float.valueOf(var15.costMalus)}), (double)var15.x + 0.5D, (double)var15.y + 0.25D, (double)var15.z + 0.5D, -65536);
            }
         }

         for(Node var15 : path.getOpenSet()) {
            if(distanceToCamera(camera, var15.asBlockPos()) <= 40.0F) {
               DebugRenderer.renderFloatingText(String.format("%s", new Object[]{var15.type}), (double)var15.x + 0.5D, (double)var15.y + 0.75D, (double)var15.z + 0.5D, -16776961);
               DebugRenderer.renderFloatingText(String.format(Locale.ROOT, "%.2f", new Object[]{Float.valueOf(var15.costMalus)}), (double)var15.x + 0.5D, (double)var15.y + 0.25D, (double)var15.z + 0.5D, -16776961);
            }
         }
      }

      if(var4) {
         for(int var12 = 0; var12 < path.getSize(); ++var12) {
            Node var13 = path.get(var12);
            if(distanceToCamera(camera, var13.asBlockPos()) <= 40.0F) {
               DebugRenderer.renderFloatingText(String.format("%s", new Object[]{var13.type}), (double)var13.x + 0.5D, (double)var13.y + 0.75D, (double)var13.z + 0.5D, -1);
               DebugRenderer.renderFloatingText(String.format(Locale.ROOT, "%.2f", new Object[]{Float.valueOf(var13.costMalus)}), (double)var13.x + 0.5D, (double)var13.y + 0.25D, (double)var13.z + 0.5D, -1);
            }
         }
      }

   }

   public static void renderPathLine(Camera camera, Path path) {
      Tesselator var2 = Tesselator.getInstance();
      BufferBuilder var3 = var2.getBuilder();
      double var4 = camera.getPosition().x;
      double var6 = camera.getPosition().y;
      double var8 = camera.getPosition().z;
      var3.begin(3, DefaultVertexFormat.POSITION_COLOR);

      for(int var10 = 0; var10 < path.getSize(); ++var10) {
         Node var11 = path.get(var10);
         if(distanceToCamera(camera, var11.asBlockPos()) <= 40.0F) {
            float var12 = (float)var10 / (float)path.getSize() * 0.33F;
            int var13 = var10 == 0?0:Mth.hsvToRgb(var12, 0.9F, 0.9F);
            int var14 = var13 >> 16 & 255;
            int var15 = var13 >> 8 & 255;
            int var16 = var13 & 255;
            var3.vertex((double)var11.x - var4 + 0.5D, (double)var11.y - var6 + 0.5D, (double)var11.z - var8 + 0.5D).color(var14, var15, var16, 255).endVertex();
         }
      }

      var2.end();
   }

   private static float distanceToCamera(Camera camera, BlockPos blockPos) {
      return (float)(Math.abs((double)blockPos.getX() - camera.getPosition().x) + Math.abs((double)blockPos.getY() - camera.getPosition().y) + Math.abs((double)blockPos.getZ() - camera.getPosition().z));
   }

   private Camera getCamera() {
      return this.minecraft.gameRenderer.getMainCamera();
   }
}
