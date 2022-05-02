package net.minecraft.world.item;

import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.fishing.FishingHook;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemPropertyFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class FishingRodItem extends Item {
   public FishingRodItem(Item.Properties item$Properties) {
      super(item$Properties);
      this.addProperty(new ResourceLocation("cast"), (itemStack, level, livingEntity) -> {
         if(livingEntity == null) {
            return 0.0F;
         } else {
            boolean var3 = livingEntity.getMainHandItem() == itemStack;
            boolean var4 = livingEntity.getOffhandItem() == itemStack;
            if(livingEntity.getMainHandItem().getItem() instanceof FishingRodItem) {
               var4 = false;
            }

            return (var3 || var4) && livingEntity instanceof Player && ((Player)livingEntity).fishing != null?1.0F:0.0F;
         }
      });
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack var4 = player.getItemInHand(interactionHand);
      if(player.fishing != null) {
         if(!level.isClientSide) {
            int var5 = player.fishing.retrieve(var4);
            var4.hurtAndBreak(var5, player, (player) -> {
               player.broadcastBreakEvent(interactionHand);
            });
         }

         player.swing(interactionHand);
         level.playSound((Player)null, player.x, player.y, player.z, SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      } else {
         level.playSound((Player)null, player.x, player.y, player.z, SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
         if(!level.isClientSide) {
            int var5 = EnchantmentHelper.getFishingSpeedBonus(var4);
            int var6 = EnchantmentHelper.getFishingLuckBonus(var4);
            level.addFreshEntity(new FishingHook(player, level, var6, var5));
         }

         player.swing(interactionHand);
         player.awardStat(Stats.ITEM_USED.get(this));
      }

      return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
   }

   public int getEnchantmentValue() {
      return 1;
   }
}
