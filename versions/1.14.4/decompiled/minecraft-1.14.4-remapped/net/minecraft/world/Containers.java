package net.minecraft.world;

import java.util.Random;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Containers {
   private static final Random RANDOM = new Random();

   public static void dropContents(Level level, BlockPos blockPos, Container container) {
      dropContents(level, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), container);
   }

   public static void dropContents(Level level, Entity entity, Container container) {
      dropContents(level, entity.x, entity.y, entity.z, container);
   }

   private static void dropContents(Level level, double var1, double var3, double var5, Container container) {
      for(int var8 = 0; var8 < container.getContainerSize(); ++var8) {
         dropItemStack(level, var1, var3, var5, container.getItem(var8));
      }

   }

   public static void dropContents(Level level, BlockPos blockPos, NonNullList nonNullList) {
      nonNullList.forEach((itemStack) -> {
         dropItemStack(level, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), itemStack);
      });
   }

   public static void dropItemStack(Level level, double var1, double var3, double var5, ItemStack itemStack) {
      double var8 = (double)EntityType.ITEM.getWidth();
      double var10 = 1.0D - var8;
      double var12 = var8 / 2.0D;
      double var14 = Math.floor(var1) + RANDOM.nextDouble() * var10 + var12;
      double var16 = Math.floor(var3) + RANDOM.nextDouble() * var10;
      double var18 = Math.floor(var5) + RANDOM.nextDouble() * var10 + var12;

      while(!itemStack.isEmpty()) {
         ItemEntity var20 = new ItemEntity(level, var14, var16, var18, itemStack.split(RANDOM.nextInt(21) + 10));
         float var21 = 0.05F;
         var20.setDeltaMovement(RANDOM.nextGaussian() * 0.05000000074505806D, RANDOM.nextGaussian() * 0.05000000074505806D + 0.20000000298023224D, RANDOM.nextGaussian() * 0.05000000074505806D);
         level.addFreshEntity(var20);
      }

   }
}
