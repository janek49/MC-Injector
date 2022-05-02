package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DirectionalPlaceContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;

public class ShulkerBoxDispenseBehavior extends OptionalDispenseItemBehavior {
   protected ItemStack execute(BlockSource blockSource, ItemStack var2) {
      this.success = false;
      Item var3 = var2.getItem();
      if(var3 instanceof BlockItem) {
         Direction var4 = (Direction)blockSource.getBlockState().getValue(DispenserBlock.FACING);
         BlockPos var5 = blockSource.getPos().relative(var4);
         Direction var6 = blockSource.getLevel().isEmptyBlock(var5.below())?var4:Direction.UP;
         this.success = ((BlockItem)var3).place(new DirectionalPlaceContext(blockSource.getLevel(), var5, var4, var2, var6)) == InteractionResult.SUCCESS;
      }

      return var2;
   }
}
