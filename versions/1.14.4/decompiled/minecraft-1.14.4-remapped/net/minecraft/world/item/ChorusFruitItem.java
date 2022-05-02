package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ChorusFruitItem extends Item {
   public ChorusFruitItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public ItemStack finishUsingItem(ItemStack var1, Level level, LivingEntity livingEntity) {
      ItemStack var4 = super.finishUsingItem(var1, level, livingEntity);
      if(!level.isClientSide) {
         double var5 = livingEntity.x;
         double var7 = livingEntity.y;
         double var9 = livingEntity.z;

         for(int var11 = 0; var11 < 16; ++var11) {
            double var12 = livingEntity.x + (livingEntity.getRandom().nextDouble() - 0.5D) * 16.0D;
            double var14 = Mth.clamp(livingEntity.y + (double)(livingEntity.getRandom().nextInt(16) - 8), 0.0D, (double)(level.getHeight() - 1));
            double var16 = livingEntity.z + (livingEntity.getRandom().nextDouble() - 0.5D) * 16.0D;
            if(livingEntity.isPassenger()) {
               livingEntity.stopRiding();
            }

            if(livingEntity.randomTeleport(var12, var14, var16, true)) {
               level.playSound((Player)null, var5, var7, var9, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
               livingEntity.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
               break;
            }
         }

         if(livingEntity instanceof Player) {
            ((Player)livingEntity).getCooldowns().addCooldown(this, 20);
         }
      }

      return var4;
   }
}
