package net.minecraft.world.level.block;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;

public class WoodButtonBlock extends ButtonBlock {
   protected WoodButtonBlock(Block.Properties block$Properties) {
      super(true, block$Properties);
   }

   protected SoundEvent getSound(boolean b) {
      return b?SoundEvents.WOODEN_BUTTON_CLICK_ON:SoundEvents.WOODEN_BUTTON_CLICK_OFF;
   }
}
