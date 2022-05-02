package net.minecraft.client.renderer.debug;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

@ClientJarOnly
public class NeighborsUpdateRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private final Map lastUpdate = Maps.newTreeMap(Ordering.natural().reverse());

   NeighborsUpdateRenderer(Minecraft minecraft) {
      this.minecraft = minecraft;
   }

   public void addUpdate(long var1, BlockPos blockPos) {
      Map<BlockPos, Integer> var4 = (Map)this.lastUpdate.get(Long.valueOf(var1));
      if(var4 == null) {
         var4 = Maps.newHashMap();
         this.lastUpdate.put(Long.valueOf(var1), var4);
      }

      Integer var5 = (Integer)var4.get(blockPos);
      if(var5 == null) {
         var5 = Integer.valueOf(0);
      }

      var4.put(blockPos, Integer.valueOf(var5.intValue() + 1));
   }

   public void render(long l) {
      long var3 = this.minecraft.level.getGameTime();
      Camera var5 = this.minecraft.gameRenderer.getMainCamera();
      double var6 = var5.getPosition().x;
      double var8 = var5.getPosition().y;
      double var10 = var5.getPosition().z;
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.lineWidth(2.0F);
      GlStateManager.disableTexture();
      GlStateManager.depthMask(false);
      int var12 = 200;
      double var13 = 0.0025D;
      Set<BlockPos> var15 = Sets.newHashSet();
      Map<BlockPos, Integer> var16 = Maps.newHashMap();
      Iterator<Entry<Long, Map<BlockPos, Integer>>> var17 = this.lastUpdate.entrySet().iterator();

      while(var17.hasNext()) {
         Entry<Long, Map<BlockPos, Integer>> var18 = (Entry)var17.next();
         Long var19 = (Long)var18.getKey();
         Map<BlockPos, Integer> var20 = (Map)var18.getValue();
         long var21 = var3 - var19.longValue();
         if(var21 > 200L) {
            var17.remove();
         } else {
            for(Entry<BlockPos, Integer> var24 : var20.entrySet()) {
               BlockPos var25 = (BlockPos)var24.getKey();
               Integer var26 = (Integer)var24.getValue();
               if(var15.add(var25)) {
                  LevelRenderer.renderLineBox((new AABB(BlockPos.ZERO)).inflate(0.002D).deflate(0.0025D * (double)var21).move((double)var25.getX(), (double)var25.getY(), (double)var25.getZ()).move(-var6, -var8, -var10), 1.0F, 1.0F, 1.0F, 1.0F);
                  var16.put(var25, var26);
               }
            }
         }
      }

      for(Entry<BlockPos, Integer> var18 : var16.entrySet()) {
         BlockPos var19 = (BlockPos)var18.getKey();
         Integer var20 = (Integer)var18.getValue();
         DebugRenderer.renderFloatingText(String.valueOf(var20), var19.getX(), var19.getY(), var19.getZ(), -1);
      }

      GlStateManager.depthMask(true);
      GlStateManager.enableTexture();
      GlStateManager.disableBlend();
   }
}
