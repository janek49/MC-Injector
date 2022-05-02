package net.minecraft.core.dispenser;

import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class DefaultDispenseItemBehavior implements DispenseItemBehavior {
   public final ItemStack dispense(BlockSource blockSource, ItemStack var2) {
      ItemStack var3 = this.execute(blockSource, var2);
      this.playSound(blockSource);
      this.playAnimation(blockSource, (Direction)blockSource.getBlockState().getValue(DispenserBlock.FACING));
      return var3;
   }

   protected ItemStack execute(BlockSource blockSource, ItemStack var2) {
      Direction var3 = (Direction)blockSource.getBlockState().getValue(DispenserBlock.FACING);
      Position var4 = DispenserBlock.getDispensePosition(blockSource);
      ItemStack var5 = var2.split(1);
      spawnItem(blockSource.getLevel(), var5, 6, var3, var4);
      return var2;
   }

   public static void spawnItem(Level level, ItemStack itemStack, int var2, Direction direction, Position position) {
      double var5 = position.x();
      double var7 = position.y();
      double var9 = position.z();
      if(direction.getAxis() == Direction.Axis.Y) {
         var7 = var7 - 0.125D;
      } else {
         var7 = var7 - 0.15625D;
      }

      ItemEntity var11 = new ItemEntity(level, var5, var7, var9, itemStack);
      double var12 = level.random.nextDouble() * 0.1D + 0.2D;
      var11.setDeltaMovement(level.random.nextGaussian() * 0.007499999832361937D * (double)var2 + (double)direction.getStepX() * var12, level.random.nextGaussian() * 0.007499999832361937D * (double)var2 + 0.20000000298023224D, level.random.nextGaussian() * 0.007499999832361937D * (double)var2 + (double)direction.getStepZ() * var12);
      level.addFreshEntity(var11);
   }

   protected void playSound(BlockSource blockSource) {
      blockSource.getLevel().levelEvent(1000, blockSource.getPos(), 0);
   }

   protected void playAnimation(BlockSource blockSource, Direction direction) {
      blockSource.getLevel().levelEvent(2000, blockSource.getPos(), direction.get3DDataValue());
   }
}
