package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

public class PotionItem extends Item {
   public PotionItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public ItemStack getDefaultInstance() {
      return PotionUtils.setPotion(super.getDefaultInstance(), Potions.WATER);
   }

   public ItemStack finishUsingItem(ItemStack var1, Level level, LivingEntity livingEntity) {
      Player var4 = livingEntity instanceof Player?(Player)livingEntity:null;
      if(var4 == null || !var4.abilities.instabuild) {
         var1.shrink(1);
      }

      if(var4 instanceof ServerPlayer) {
         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)var4, var1);
      }

      if(!level.isClientSide) {
         for(MobEffectInstance var7 : PotionUtils.getMobEffects(var1)) {
            if(var7.getEffect().isInstantenous()) {
               var7.getEffect().applyInstantenousEffect(var4, var4, livingEntity, var7.getAmplifier(), 1.0D);
            } else {
               livingEntity.addEffect(new MobEffectInstance(var7));
            }
         }
      }

      if(var4 != null) {
         var4.awardStat(Stats.ITEM_USED.get(this));
      }

      if(var4 == null || !var4.abilities.instabuild) {
         if(var1.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
         }

         if(var4 != null) {
            var4.inventory.add(new ItemStack(Items.GLASS_BOTTLE));
         }
      }

      return var1;
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

   public String getDescriptionId(ItemStack itemStack) {
      return PotionUtils.getPotion(itemStack).getName(this.getDescriptionId() + ".effect.");
   }

   public void appendHoverText(ItemStack itemStack, @Nullable Level level, List list, TooltipFlag tooltipFlag) {
      PotionUtils.addPotionTooltip(itemStack, list, 1.0F);
   }

   public boolean isFoil(ItemStack itemStack) {
      return super.isFoil(itemStack) || !PotionUtils.getMobEffects(itemStack).isEmpty();
   }

   public void fillItemCategory(CreativeModeTab creativeModeTab, NonNullList nonNullList) {
      if(this.allowdedIn(creativeModeTab)) {
         for(Potion var4 : Registry.POTION) {
            if(var4 != Potions.EMPTY) {
               nonNullList.add(PotionUtils.setPotion(new ItemStack(this), var4));
            }
         }
      }

   }
}
