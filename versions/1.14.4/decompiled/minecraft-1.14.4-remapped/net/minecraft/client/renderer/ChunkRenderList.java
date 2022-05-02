package net.minecraft.client.renderer;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockLayer;

@ClientJarOnly
public abstract class ChunkRenderList {
   private double xOff;
   private double yOff;
   private double zOff;
   protected final List chunks = Lists.newArrayListWithCapacity(17424);
   protected boolean ready;

   public void setCameraLocation(double xOff, double yOff, double zOff) {
      this.ready = true;
      this.chunks.clear();
      this.xOff = xOff;
      this.yOff = yOff;
      this.zOff = zOff;
   }

   public void translateToRelativeChunkPosition(RenderChunk renderChunk) {
      BlockPos var2 = renderChunk.getOrigin();
      GlStateManager.translatef((float)((double)var2.getX() - this.xOff), (float)((double)var2.getY() - this.yOff), (float)((double)var2.getZ() - this.zOff));
   }

   public void add(RenderChunk renderChunk, BlockLayer blockLayer) {
      this.chunks.add(renderChunk);
   }

   public abstract void render(BlockLayer var1);
}
