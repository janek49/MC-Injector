package net.minecraft.core.dispenser;

import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public abstract class AbstractProjectileDispenseBehavior extends DefaultDispenseItemBehavior {
   public ItemStack execute(BlockSource blockSource, ItemStack var2) {
      Level var3 = blockSource.getLevel();
      Position var4 = DispenserBlock.getDispensePosition(blockSource);
      Direction var5 = (Direction)blockSource.getBlockState().getValue(DispenserBlock.FACING);
      Projectile var6 = this.getProjectile(var3, var4, var2);
      var6.shoot((double)var5.getStepX(), (double)((float)var5.getStepY() + 0.1F), (double)var5.getStepZ(), this.getPower(), this.getUncertainty());
      var3.addFreshEntity((Entity)var6);
      var2.shrink(1);
      return var2;
   }

   protected void playSound(BlockSource blockSource) {
      blockSource.getLevel().levelEvent(1002, blockSource.getPos(), 0);
   }

   protected abstract Projectile getProjectile(Level var1, Position var2, ItemStack var3);

   protected float getUncertainty() {
      return 6.0F;
   }

   protected float getPower() {
      return 1.1F;
   }
}
