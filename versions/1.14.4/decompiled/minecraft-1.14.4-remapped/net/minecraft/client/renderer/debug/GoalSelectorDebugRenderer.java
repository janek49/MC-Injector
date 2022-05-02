package net.minecraft.client.renderer.debug;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;

@ClientJarOnly
public class GoalSelectorDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private final Map goalSelectors = Maps.newHashMap();

   public void clear() {
      this.goalSelectors.clear();
   }

   public void addGoalSelector(int var1, List list) {
      this.goalSelectors.put(Integer.valueOf(var1), list);
   }

   public GoalSelectorDebugRenderer(Minecraft minecraft) {
      this.minecraft = minecraft;
   }

   public void render(long l) {
      Camera var3 = this.minecraft.gameRenderer.getMainCamera();
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.disableTexture();
      BlockPos var4 = new BlockPos(var3.getPosition().x, 0.0D, var3.getPosition().z);
      this.goalSelectors.forEach((integer, list) -> {
         for(int var3 = 0; var3 < list.size(); ++var3) {
            GoalSelectorDebugRenderer.DebugGoal var4 = (GoalSelectorDebugRenderer.DebugGoal)list.get(var3);
            if(var4.closerThan(var4.pos, 160.0D)) {
               double var5 = (double)var4.pos.getX() + 0.5D;
               double var7 = (double)var4.pos.getY() + 2.0D + (double)var3 * 0.25D;
               double var9 = (double)var4.pos.getZ() + 0.5D;
               int var11 = var4.isRunning?-16711936:-3355444;
               DebugRenderer.renderFloatingText(var4.name, var5, var7, var9, var11);
            }
         }

      });
      GlStateManager.enableDepthTest();
      GlStateManager.enableTexture();
      GlStateManager.popMatrix();
   }

   @ClientJarOnly
   public static class DebugGoal {
      public final BlockPos pos;
      public final int priority;
      public final String name;
      public final boolean isRunning;

      public DebugGoal(BlockPos pos, int priority, String name, boolean isRunning) {
         this.pos = pos;
         this.priority = priority;
         this.name = name;
         this.isRunning = isRunning;
      }
   }
}
