package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ExperienceBottleItem extends Item {
   public ExperienceBottleItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public boolean isFoil(ItemStack itemStack) {
      return true;
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack var4 = player.getItemInHand(interactionHand);
      if(!player.abilities.instabuild) {
         var4.shrink(1);
      }

      level.playSound((Player)null, player.x, player.y, player.z, SoundEvents.EXPERIENCE_BOTTLE_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      if(!level.isClientSide) {
         ThrownExperienceBottle var5 = new ThrownExperienceBottle(level, player);
         var5.setItem(var4);
         var5.shootFromRotation(player, player.xRot, player.yRot, -20.0F, 0.7F, 1.0F);
         level.addFreshEntity(var5);
      }

      player.awardStat(Stats.ITEM_USED.get(this));
      return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
   }
}
