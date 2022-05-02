package net.minecraft.world.item;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BottleItem extends Item {
   public BottleItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      List<AreaEffectCloud> var4 = level.getEntitiesOfClass(AreaEffectCloud.class, player.getBoundingBox().inflate(2.0D), (areaEffectCloud) -> {
         return areaEffectCloud != null && areaEffectCloud.isAlive() && areaEffectCloud.getOwner() instanceof EnderDragon;
      });
      ItemStack var5 = player.getItemInHand(interactionHand);
      if(!var4.isEmpty()) {
         AreaEffectCloud var6 = (AreaEffectCloud)var4.get(0);
         var6.setRadius(var6.getRadius() - 0.5F);
         level.playSound((Player)null, player.x, player.y, player.z, SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.NEUTRAL, 1.0F, 1.0F);
         return new InteractionResultHolder(InteractionResult.SUCCESS, this.turnBottleIntoItem(var5, player, new ItemStack(Items.DRAGON_BREATH)));
      } else {
         HitResult var6 = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
         if(var6.getType() == HitResult.Type.MISS) {
            return new InteractionResultHolder(InteractionResult.PASS, var5);
         } else {
            if(var6.getType() == HitResult.Type.BLOCK) {
               BlockPos var7 = ((BlockHitResult)var6).getBlockPos();
               if(!level.mayInteract(player, var7)) {
                  return new InteractionResultHolder(InteractionResult.PASS, var5);
               }

               if(level.getFluidState(var7).is(FluidTags.WATER)) {
                  level.playSound(player, player.x, player.y, player.z, SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                  return new InteractionResultHolder(InteractionResult.SUCCESS, this.turnBottleIntoItem(var5, player, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
               }
            }

            return new InteractionResultHolder(InteractionResult.PASS, var5);
         }
      }
   }

   protected ItemStack turnBottleIntoItem(ItemStack var1, Player player, ItemStack var3) {
      var1.shrink(1);
      player.awardStat(Stats.ITEM_USED.get(this));
      if(var1.isEmpty()) {
         return var3;
      } else {
         if(!player.inventory.add(var3)) {
            player.drop(var3, false);
         }

         return var1;
      }
   }
}
