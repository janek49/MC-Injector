package net.minecraft.server.level;

import net.minecraft.core.BlockPos;

public class BlockDestructionProgress {
   private final int id;
   private final BlockPos pos;
   private int progress;
   private int updatedRenderTick;

   public BlockDestructionProgress(int id, BlockPos pos) {
      this.id = id;
      this.pos = pos;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public void setProgress(int progress) {
      if(progress > 10) {
         progress = 10;
      }

      this.progress = progress;
   }

   public int getProgress() {
      return this.progress;
   }

   public void updateTick(int updatedRenderTick) {
      this.updatedRenderTick = updatedRenderTick;
   }

   public int getUpdatedRenderTick() {
      return this.updatedRenderTick;
   }
}
