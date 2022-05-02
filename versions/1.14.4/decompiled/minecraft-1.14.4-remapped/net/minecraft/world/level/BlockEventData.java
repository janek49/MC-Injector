package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

public class BlockEventData {
   private final BlockPos pos;
   private final Block block;
   private final int paramA;
   private final int paramB;

   public BlockEventData(BlockPos pos, Block block, int paramA, int paramB) {
      this.pos = pos;
      this.block = block;
      this.paramA = paramA;
      this.paramB = paramB;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Block getBlock() {
      return this.block;
   }

   public int getParamA() {
      return this.paramA;
   }

   public int getParamB() {
      return this.paramB;
   }

   public boolean equals(Object object) {
      if(!(object instanceof BlockEventData)) {
         return false;
      } else {
         BlockEventData var2 = (BlockEventData)object;
         return this.pos.equals(var2.pos) && this.paramA == var2.paramA && this.paramB == var2.paramB && this.block == var2.block;
      }
   }

   public String toString() {
      return "TE(" + this.pos + ")," + this.paramA + "," + this.paramB + "," + this.block;
   }
}
