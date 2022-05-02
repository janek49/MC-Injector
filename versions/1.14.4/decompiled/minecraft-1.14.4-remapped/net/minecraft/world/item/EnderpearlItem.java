package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EnderpearlItem extends Item {
   public EnderpearlItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack var4 = player.getItemInHand(interactionHand);
      if(!player.abilities.instabuild) {
         var4.shrink(1);
      }

      level.playSound((Player)null, player.x, player.y, player.z, SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      player.getCooldowns().addCooldown(this, 20);
      if(!level.isClientSide) {
         ThrownEnderpearl var5 = new ThrownEnderpearl(level, player);
         var5.setItem(var4);
         var5.shootFromRotation(player, player.xRot, player.yRot, 0.0F, 1.5F, 1.0F);
         level.addFreshEntity(var5);
      }

      player.awardStat(Stats.ITEM_USED.get(this));
      return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
   }
}
