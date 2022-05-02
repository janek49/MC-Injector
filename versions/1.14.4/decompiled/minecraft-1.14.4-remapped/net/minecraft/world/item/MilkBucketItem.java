package net.minecraft.world.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class MilkBucketItem extends Item {
   public MilkBucketItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public ItemStack finishUsingItem(ItemStack var1, Level level, LivingEntity livingEntity) {
      if(livingEntity instanceof ServerPlayer) {
         ServerPlayer var4 = (ServerPlayer)livingEntity;
         CriteriaTriggers.CONSUME_ITEM.trigger(var4, var1);
         var4.awardStat(Stats.ITEM_USED.get(this));
      }

      if(livingEntity instanceof Player && !((Player)livingEntity).abilities.instabuild) {
         var1.shrink(1);
      }

      if(!level.isClientSide) {
         livingEntity.removeAllEffects();
      }

      return var1.isEmpty()?new ItemStack(Items.BUCKET):var1;
   }

   public int getUseDuration(ItemStack itemStack) {
      return 32;
   }

   public UseAnim getUseAnimation(ItemStack itemStack) {
      return UseAnim.DRINK;
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      player.startUsingItem(interactionHand);
      return new InteractionResultHolder(InteractionResult.SUCCESS, player.getItemInHand(interactionHand));
   }
}
