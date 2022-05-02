package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

public class LingeringPotionItem extends PotionItem {
   public LingeringPotionItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public void appendHoverText(ItemStack itemStack, @Nullable Level level, List list, TooltipFlag tooltipFlag) {
      PotionUtils.addPotionTooltip(itemStack, list, 0.25F);
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack var4 = player.getItemInHand(interactionHand);
      ItemStack var5 = player.abilities.instabuild?var4.copy():var4.split(1);
      level.playSound((Player)null, player.x, player.y, player.z, SoundEvents.LINGERING_POTION_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      if(!level.isClientSide) {
         ThrownPotion var6 = new ThrownPotion(level, player);
         var6.setItem(var5);
         var6.shootFromRotation(player, player.xRot, player.yRot, -20.0F, 0.5F, 1.0F);
         level.addFreshEntity(var6);
      }

      player.awardStat(Stats.ITEM_USED.get(this));
      return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
   }
}
