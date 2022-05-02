package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class BoatDispenseItemBehavior extends DefaultDispenseItemBehavior {
   private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
   private final Boat.Type type;

   public BoatDispenseItemBehavior(Boat.Type type) {
      this.type = type;
   }

   public ItemStack execute(BlockSource blockSource, ItemStack var2) {
      Direction var3 = (Direction)blockSource.getBlockState().getValue(DispenserBlock.FACING);
      Level var4 = blockSource.getLevel();
      double var5 = blockSource.x() + (double)((float)var3.getStepX() * 1.125F);
      double var7 = blockSource.y() + (double)((float)var3.getStepY() * 1.125F);
      double var9 = blockSource.z() + (double)((float)var3.getStepZ() * 1.125F);
      BlockPos var11 = blockSource.getPos().relative(var3);
      double var12;
      if(var4.getFluidState(var11).is(FluidTags.WATER)) {
         var12 = 1.0D;
      } else {
         if(!var4.getBlockState(var11).isAir() || !var4.getFluidState(var11.below()).is(FluidTags.WATER)) {
            return this.defaultDispenseItemBehavior.dispense(blockSource, var2);
         }

         var12 = 0.0D;
      }

      Boat var14 = new Boat(var4, var5, var7 + var12, var9);
      var14.setType(this.type);
      var14.yRot = var3.toYRot();
      var4.addFreshEntity(var14);
      var2.shrink(1);
      return var2;
   }

   protected void playSound(BlockSource blockSource) {
      blockSource.getLevel().levelEvent(1000, blockSource.getPos(), 0);
   }
}
