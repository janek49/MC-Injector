package net.minecraft.world.level.block;

import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StemBlock;

public abstract class StemGrownBlock extends Block {
   public StemGrownBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public abstract StemBlock getStem();

   public abstract AttachedStemBlock getAttachedStem();
}
