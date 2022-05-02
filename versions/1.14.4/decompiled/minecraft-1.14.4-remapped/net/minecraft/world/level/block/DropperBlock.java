package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

public class DropperBlock extends DispenserBlock {
   private static final DispenseItemBehavior DISPENSE_BEHAVIOUR = new DefaultDispenseItemBehavior();

   public DropperBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   protected DispenseItemBehavior getDispenseMethod(ItemStack itemStack) {
      return DISPENSE_BEHAVIOUR;
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new DropperBlockEntity();
   }

   protected void dispenseFrom(Level level, BlockPos blockPos) {
      BlockSourceImpl var3 = new BlockSourceImpl(level, blockPos);
      DispenserBlockEntity var4 = (DispenserBlockEntity)var3.getEntity();
      int var5 = var4.getRandomSlot();
      if(var5 < 0) {
         level.levelEvent(1001, blockPos, 0);
      } else {
         ItemStack var6 = var4.getItem(var5);
         if(!var6.isEmpty()) {
            Direction var7 = (Direction)level.getBlockState(blockPos).getValue(FACING);
            Container var8 = HopperBlockEntity.getContainerAt(level, blockPos.relative(var7));
            ItemStack var9;
            if(var8 == null) {
               var9 = DISPENSE_BEHAVIOUR.dispense(var3, var6);
            } else {
               var9 = HopperBlockEntity.addItem(var4, var8, var6.copy().split(1), var7.getOpposite());
               if(var9.isEmpty()) {
                  var9 = var6.copy();
                  var9.shrink(1);
               } else {
                  var9 = var6.copy();
               }
            }

            var4.setItem(var5, var9);
         }
      }
   }
}
