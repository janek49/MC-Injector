package net.minecraft.world.item;

import com.google.common.collect.Multimap;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemPropertyFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TridentItem extends Item {
   public TridentItem(Item.Properties item$Properties) {
      super(item$Properties);
      this.addProperty(new ResourceLocation("throwing"), (itemStack, level, livingEntity) -> {
         return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack?1.0F:0.0F;
      });
   }

   public boolean canAttackBlock(BlockState blockState, Level level, BlockPos blockPos, Player player) {
      return !player.isCreative();
   }

   public UseAnim getUseAnimation(ItemStack itemStack) {
      return UseAnim.SPEAR;
   }

   public int getUseDuration(ItemStack itemStack) {
      return 72000;
   }

   public boolean isFoil(ItemStack itemStack) {
      return false;
   }

   public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int var4) {
      if(livingEntity instanceof Player) {
         Player var5 = (Player)livingEntity;
         int var6 = this.getUseDuration(itemStack) - var4;
         if(var6 >= 10) {
            int var7 = EnchantmentHelper.getRiptide(itemStack);
            if(var7 <= 0 || var5.isInWaterOrRain()) {
               if(!level.isClientSide) {
                  itemStack.hurtAndBreak(1, var5, (player) -> {
                     player.broadcastBreakEvent(livingEntity.getUsedItemHand());
                  });
                  if(var7 == 0) {
                     ThrownTrident var8 = new ThrownTrident(level, var5, itemStack);
                     var8.shootFromRotation(var5, var5.xRot, var5.yRot, 0.0F, 2.5F + (float)var7 * 0.5F, 1.0F);
                     if(var5.abilities.instabuild) {
                        var8.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                     }

                     level.addFreshEntity(var8);
                     level.playSound((Player)null, (Entity)var8, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                     if(!var5.abilities.instabuild) {
                        var5.inventory.removeItem(itemStack);
                     }
                  }
               }

               var5.awardStat(Stats.ITEM_USED.get(this));
               if(var7 > 0) {
                  float var8 = var5.yRot;
                  float var9 = var5.xRot;
                  float var10 = -Mth.sin(var8 * 0.017453292F) * Mth.cos(var9 * 0.017453292F);
                  float var11 = -Mth.sin(var9 * 0.017453292F);
                  float var12 = Mth.cos(var8 * 0.017453292F) * Mth.cos(var9 * 0.017453292F);
                  float var13 = Mth.sqrt(var10 * var10 + var11 * var11 + var12 * var12);
                  float var14 = 3.0F * ((1.0F + (float)var7) / 4.0F);
                  var10 = var10 * (var14 / var13);
                  var11 = var11 * (var14 / var13);
                  var12 = var12 * (var14 / var13);
                  var5.push((double)var10, (double)var11, (double)var12);
                  var5.startAutoSpinAttack(20);
                  if(var5.onGround) {
                     float var15 = 1.1999999F;
                     var5.move(MoverType.SELF, new Vec3(0.0D, 1.1999999284744263D, 0.0D));
                  }

                  SoundEvent var15;
                  if(var7 >= 3) {
                     var15 = SoundEvents.TRIDENT_RIPTIDE_3;
                  } else if(var7 == 2) {
                     var15 = SoundEvents.TRIDENT_RIPTIDE_2;
                  } else {
                     var15 = SoundEvents.TRIDENT_RIPTIDE_1;
                  }

                  level.playSound((Player)null, (Entity)var5, var15, SoundSource.PLAYERS, 1.0F, 1.0F);
               }

            }
         }
      }
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack var4 = player.getItemInHand(interactionHand);
      if(var4.getDamageValue() >= var4.getMaxDamage()) {
         return new InteractionResultHolder(InteractionResult.FAIL, var4);
      } else if(EnchantmentHelper.getRiptide(var4) > 0 && !player.isInWaterOrRain()) {
         return new InteractionResultHolder(InteractionResult.FAIL, var4);
      } else {
         player.startUsingItem(interactionHand);
         return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
      }
   }

   public boolean hurtEnemy(ItemStack itemStack, LivingEntity var2, LivingEntity var3) {
      itemStack.hurtAndBreak(1, var3, (livingEntity) -> {
         livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
      });
      return true;
   }

   public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
      if((double)blockState.getDestroySpeed(level, blockPos) != 0.0D) {
         itemStack.hurtAndBreak(2, livingEntity, (livingEntity) -> {
            livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
         });
      }

      return true;
   }

   public Multimap getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
      Multimap<String, AttributeModifier> multimap = super.getDefaultAttributeModifiers(equipmentSlot);
      if(equipmentSlot == EquipmentSlot.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 8.0D, AttributeModifier.Operation.ADDITION));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -2.9000000953674316D, AttributeModifier.Operation.ADDITION));
      }

      return multimap;
   }

   public int getEnchantmentValue() {
      return 1;
   }
}
