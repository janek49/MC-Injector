package net.minecraft.world.item;

import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemPropertyFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class BowItem extends ProjectileWeaponItem {
   public BowItem(Item.Properties item$Properties) {
      super(item$Properties);
      this.addProperty(new ResourceLocation("pull"), (itemStack, level, livingEntity) -> {
         return livingEntity == null?0.0F:(livingEntity.getUseItem().getItem() != Items.BOW?0.0F:(float)(itemStack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / 20.0F);
      });
      this.addProperty(new ResourceLocation("pulling"), (itemStack, level, livingEntity) -> {
         return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack?1.0F:0.0F;
      });
   }

   public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int var4) {
      if(livingEntity instanceof Player) {
         Player var5 = (Player)livingEntity;
         boolean var6 = var5.abilities.instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, itemStack) > 0;
         ItemStack var7 = var5.getProjectile(itemStack);
         if(!var7.isEmpty() || var6) {
            if(var7.isEmpty()) {
               var7 = new ItemStack(Items.ARROW);
            }

            int var8 = this.getUseDuration(itemStack) - var4;
            float var9 = getPowerForTime(var8);
            if((double)var9 >= 0.1D) {
               boolean var10 = var6 && var7.getItem() == Items.ARROW;
               if(!level.isClientSide) {
                  ArrowItem var11 = (ArrowItem)((ArrowItem)(var7.getItem() instanceof ArrowItem?var7.getItem():Items.ARROW));
                  AbstractArrow var12 = var11.createArrow(level, var7, var5);
                  var12.shootFromRotation(var5, var5.xRot, var5.yRot, 0.0F, var9 * 3.0F, 1.0F);
                  if(var9 == 1.0F) {
                     var12.setCritArrow(true);
                  }

                  int var13 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, itemStack);
                  if(var13 > 0) {
                     var12.setBaseDamage(var12.getBaseDamage() + (double)var13 * 0.5D + 0.5D);
                  }

                  int var14 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, itemStack);
                  if(var14 > 0) {
                     var12.setKnockback(var14);
                  }

                  if(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, itemStack) > 0) {
                     var12.setSecondsOnFire(100);
                  }

                  itemStack.hurtAndBreak(1, var5, (var1) -> {
                     var1.broadcastBreakEvent(var5.getUsedItemHand());
                  });
                  if(var10 || var5.abilities.instabuild && (var7.getItem() == Items.SPECTRAL_ARROW || var7.getItem() == Items.TIPPED_ARROW)) {
                     var12.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                  }

                  level.addFreshEntity(var12);
               }

               level.playSound((Player)null, var5.x, var5.y, var5.z, SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + var9 * 0.5F);
               if(!var10 && !var5.abilities.instabuild) {
                  var7.shrink(1);
                  if(var7.isEmpty()) {
                     var5.inventory.removeItem(var7);
                  }
               }

               var5.awardStat(Stats.ITEM_USED.get(this));
            }
         }
      }
   }

   public static float getPowerForTime(int i) {
      float var1 = (float)i / 20.0F;
      var1 = (var1 * var1 + var1 * 2.0F) / 3.0F;
      if(var1 > 1.0F) {
         var1 = 1.0F;
      }

      return var1;
   }

   public int getUseDuration(ItemStack itemStack) {
      return 72000;
   }

   public UseAnim getUseAnimation(ItemStack itemStack) {
      return UseAnim.BOW;
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack var4 = player.getItemInHand(interactionHand);
      boolean var5 = !player.getProjectile(var4).isEmpty();
      if(!player.abilities.instabuild && !var5) {
         return var5?new InteractionResultHolder(InteractionResult.PASS, var4):new InteractionResultHolder(InteractionResult.FAIL, var4);
      } else {
         player.startUsingItem(interactionHand);
         return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
      }
   }

   public Predicate getAllSupportedProjectiles() {
      return ARROW_ONLY;
   }
}
