package net.minecraft.client.renderer;

import com.fox2code.repacker.ClientJarOnly;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunkFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

@ClientJarOnly
public class ViewArea {
   protected final LevelRenderer levelRenderer;
   protected final Level level;
   protected int chunkGridSizeY;
   protected int chunkGridSizeX;
   protected int chunkGridSizeZ;
   public RenderChunk[] chunks;

   public ViewArea(Level level, int viewDistance, LevelRenderer levelRenderer, RenderChunkFactory renderChunkFactory) {
      this.levelRenderer = levelRenderer;
      this.level = level;
      this.setViewDistance(viewDistance);
      this.createChunks(renderChunkFactory);
   }

   protected void createChunks(RenderChunkFactory renderChunkFactory) {
      int var2 = this.chunkGridSizeX * this.chunkGridSizeY * this.chunkGridSizeZ;
      this.chunks = new RenderChunk[var2];

      for(int var3 = 0; var3 < this.chunkGridSizeX; ++var3) {
         for(int var4 = 0; var4 < this.chunkGridSizeY; ++var4) {
            for(int var5 = 0; var5 < this.chunkGridSizeZ; ++var5) {
               int var6 = this.getChunkIndex(var3, var4, var5);
               this.chunks[var6] = renderChunkFactory.create(this.level, this.levelRenderer);
               this.chunks[var6].setOrigin(var3 * 16, var4 * 16, var5 * 16);
            }
         }
      }

   }

   public void releaseAllBuffers() {
      for(RenderChunk var4 : this.chunks) {
         var4.releaseBuffers();
      }

   }

   private int getChunkIndex(int var1, int var2, int var3) {
      return (var3 * this.chunkGridSizeY + var2) * this.chunkGridSizeX + var1;
   }

   protected void setViewDistance(int viewDistance) {
      int var2 = viewDistance * 2 + 1;
      this.chunkGridSizeX = var2;
      this.chunkGridSizeY = 16;
      this.chunkGridSizeZ = var2;
   }

   public void repositionCamera(double var1, double var3) {
      int var5 = Mth.floor(var1) - 8;
      int var6 = Mth.floor(var3) - 8;
      int var7 = this.chunkGridSizeX * 16;

      for(int var8 = 0; var8 < this.chunkGridSizeX; ++var8) {
         int var9 = this.getCoordinate(var5, var7, var8);

         for(int var10 = 0; var10 < this.chunkGridSizeZ; ++var10) {
            int var11 = this.getCoordinate(var6, var7, var10);

            for(int var12 = 0; var12 < this.chunkGridSizeY; ++var12) {
               int var13 = var12 * 16;
               RenderChunk var14 = this.chunks[this.getChunkIndex(var8, var12, var10)];
               var14.setOrigin(var9, var13, var11);
            }
         }
      }

   }

   private int getCoordinate(int var1, int var2, int var3) {
      int var4 = var3 * 16;
      int var5 = var4 - var1 + var2 / 2;
      if(var5 < 0) {
         var5 -= var2 - 1;
      }

      return var4 - var5 / var2 * var2;
   }

   public void setDirty(int var1, int var2, int var3, boolean var4) {
      int var5 = Math.floorMod(var1, this.chunkGridSizeX);
      int var6 = Math.floorMod(var2, this.chunkGridSizeY);
      int var7 = Math.floorMod(var3, this.chunkGridSizeZ);
      RenderChunk var8 = this.chunks[this.getChunkIndex(var5, var6, var7)];
      var8.setDirty(var4);
   }

   @Nullable
   protected RenderChunk getRenderChunkAt(BlockPos blockPos) {
      int var2 = Mth.intFloorDiv(blockPos.getX(), 16);
      int var3 = Mth.intFloorDiv(blockPos.getY(), 16);
      int var4 = Mth.intFloorDiv(blockPos.getZ(), 16);
      if(var3 >= 0 && var3 < this.chunkGridSizeY) {
         var2 = Mth.positiveModulo(var2, this.chunkGridSizeX);
         var4 = Mth.positiveModulo(var4, this.chunkGridSizeZ);
         return this.chunks[this.getChunkIndex(var2, var3, var4)];
      } else {
         return null;
      }
   }
}
