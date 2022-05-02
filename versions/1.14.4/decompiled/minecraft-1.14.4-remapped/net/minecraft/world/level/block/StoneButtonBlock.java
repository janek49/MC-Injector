package net.minecraft.world.level.block;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;

public class StoneButtonBlock extends ButtonBlock {
   protected StoneButtonBlock(Block.Properties block$Properties) {
      super(false, block$Properties);
   }

   protected SoundEvent getSound(boolean b) {
      return b?SoundEvents.STONE_BUTTON_CLICK_ON:SoundEvents.STONE_BUTTON_CLICK_OFF;
   }
}
