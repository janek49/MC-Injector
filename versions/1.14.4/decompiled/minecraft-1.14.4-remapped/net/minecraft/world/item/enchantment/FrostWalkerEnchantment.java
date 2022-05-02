package net.minecraft.world.item.enchantment;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;

public class FrostWalkerEnchantment extends Enchantment {
   public FrostWalkerEnchantment(Enchantment.Rarity enchantment$Rarity, EquipmentSlot... equipmentSlots) {
      super(enchantment$Rarity, EnchantmentCategory.ARMOR_FEET, equipmentSlots);
   }

   public int getMinCost(int i) {
      return i * 10;
   }

   public int getMaxCost(int i) {
      return this.getMinCost(i) + 15;
   }

   public boolean isTreasureOnly() {
      return true;
   }

   public int getMaxLevel() {
      return 2;
   }

   public static void onEntityMoved(LivingEntity livingEntity, Level level, BlockPos blockPos, int var3) {
      if(livingEntity.onGround) {
         BlockState var4 = Blocks.FROSTED_ICE.defaultBlockState();
         float var5 = (float)Math.min(16, 2 + var3);
         BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

         for(BlockPos var8 : BlockPos.betweenClosed(blockPos.offset((double)(-var5), -1.0D, (double)(-var5)), blockPos.offset((double)var5, -1.0D, (double)var5))) {
            if(var8.closerThan(livingEntity.position(), (double)var5)) {
               var6.set(var8.getX(), var8.getY() + 1, var8.getZ());
               BlockState var9 = level.getBlockState(var6);
               if(var9.isAir()) {
                  BlockState var10 = level.getBlockState(var8);
                  if(var10.getMaterial() == Material.WATER && ((Integer)var10.getValue(LiquidBlock.LEVEL)).intValue() == 0 && var4.canSurvive(level, var8) && level.isUnobstructed(var4, var8, CollisionContext.empty())) {
                     level.setBlockAndUpdate(var8, var4);
                     level.getBlockTicks().scheduleTick(var8, Blocks.FROSTED_ICE, Mth.nextInt(livingEntity.getRandom(), 60, 120));
                  }
               }
            }
         }

      }
   }

   public boolean checkCompatibility(Enchantment enchantment) {
      return super.checkCompatibility(enchantment) && enchantment != Enchantments.DEPTH_STRIDER;
   }
}
