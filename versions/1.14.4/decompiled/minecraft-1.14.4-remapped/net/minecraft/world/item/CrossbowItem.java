package net.minecraft.world.item;

import com.google.common.collect.Lists;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemPropertyFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class CrossbowItem extends ProjectileWeaponItem {
   private boolean startSoundPlayed = false;
   private boolean midLoadSoundPlayed = false;

   public CrossbowItem(Item.Properties item$Properties) {
      super(item$Properties);
      this.addProperty(new ResourceLocation("pull"), (itemStack, level, livingEntity) -> {
         return livingEntity != null && itemStack.getItem() == this?(isCharged(itemStack)?0.0F:(float)(itemStack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / (float)getChargeDuration(itemStack)):0.0F;
      });
      this.addProperty(new ResourceLocation("pulling"), (itemStack, level, livingEntity) -> {
         return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack && !isCharged(itemStack)?1.0F:0.0F;
      });
      this.addProperty(new ResourceLocation("charged"), (itemStack, level, livingEntity) -> {
         return livingEntity != null && isCharged(itemStack)?1.0F:0.0F;
      });
      this.addProperty(new ResourceLocation("firework"), (itemStack, level, livingEntity) -> {
         return livingEntity != null && isCharged(itemStack) && containsChargedProjectile(itemStack, Items.FIREWORK_ROCKET)?1.0F:0.0F;
      });
   }

   public Predicate getSupportedHeldProjectiles() {
      return ARROW_OR_FIREWORK;
   }

   public Predicate getAllSupportedProjectiles() {
      return ARROW_ONLY;
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack var4 = player.getItemInHand(interactionHand);
      if(isCharged(var4)) {
         performShooting(level, player, interactionHand, var4, getShootingPower(var4), 1.0F);
         setCharged(var4, false);
         return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
      } else if(!player.getProjectile(var4).isEmpty()) {
         if(!isCharged(var4)) {
            this.startSoundPlayed = false;
            this.midLoadSoundPlayed = false;
            player.startUsingItem(interactionHand);
         }

         return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
      } else {
         return new InteractionResultHolder(InteractionResult.FAIL, var4);
      }
   }

   public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int var4) {
      int var5 = this.getUseDuration(itemStack) - var4;
      float var6 = getPowerForTime(var5, itemStack);
      if(var6 >= 1.0F && !isCharged(itemStack) && tryLoadProjectiles(livingEntity, itemStack)) {
         setCharged(itemStack, true);
         SoundSource var7 = livingEntity instanceof Player?SoundSource.PLAYERS:SoundSource.HOSTILE;
         level.playSound((Player)null, livingEntity.x, livingEntity.y, livingEntity.z, SoundEvents.CROSSBOW_LOADING_END, var7, 1.0F, 1.0F / (random.nextFloat() * 0.5F + 1.0F) + 0.2F);
      }

   }

   private static boolean tryLoadProjectiles(LivingEntity livingEntity, ItemStack itemStack) {
      int var2 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MULTISHOT, itemStack);
      int var3 = var2 == 0?1:3;
      boolean var4 = livingEntity instanceof Player && ((Player)livingEntity).abilities.instabuild;
      ItemStack var5 = livingEntity.getProjectile(itemStack);
      ItemStack var6 = var5.copy();

      for(int var7 = 0; var7 < var3; ++var7) {
         if(var7 > 0) {
            var5 = var6.copy();
         }

         if(var5.isEmpty() && var4) {
            var5 = new ItemStack(Items.ARROW);
            var6 = var5.copy();
         }

         if(!loadProjectile(livingEntity, itemStack, var5, var7 > 0, var4)) {
            return false;
         }
      }

      return true;
   }

   private static boolean loadProjectile(LivingEntity livingEntity, ItemStack var1, ItemStack var2, boolean var3, boolean var4) {
      if(var2.isEmpty()) {
         return false;
      } else {
         boolean var5 = var4 && var2.getItem() instanceof ArrowItem;
         ItemStack var6;
         if(!var5 && !var4 && !var3) {
            var6 = var2.split(1);
            if(var2.isEmpty() && livingEntity instanceof Player) {
               ((Player)livingEntity).inventory.removeItem(var2);
            }
         } else {
            var6 = var2.copy();
         }

         addChargedProjectile(var1, var6);
         return true;
      }
   }

   public static boolean isCharged(ItemStack itemStack) {
      CompoundTag var1 = itemStack.getTag();
      return var1 != null && var1.getBoolean("Charged");
   }

   public static void setCharged(ItemStack itemStack, boolean var1) {
      CompoundTag var2 = itemStack.getOrCreateTag();
      var2.putBoolean("Charged", var1);
   }

   private static void addChargedProjectile(ItemStack var0, ItemStack var1) {
      CompoundTag var2 = var0.getOrCreateTag();
      ListTag var3;
      if(var2.contains("ChargedProjectiles", 9)) {
         var3 = var2.getList("ChargedProjectiles", 10);
      } else {
         var3 = new ListTag();
      }

      CompoundTag var4 = new CompoundTag();
      var1.save(var4);
      var3.add(var4);
      var2.put("ChargedProjectiles", var3);
   }

   private static List getChargedProjectiles(ItemStack itemStack) {
      List<ItemStack> list = Lists.newArrayList();
      CompoundTag var2 = itemStack.getTag();
      if(var2 != null && var2.contains("ChargedProjectiles", 9)) {
         ListTag var3 = var2.getList("ChargedProjectiles", 10);
         if(var3 != null) {
            for(int var4 = 0; var4 < var3.size(); ++var4) {
               CompoundTag var5 = var3.getCompound(var4);
               list.add(ItemStack.of(var5));
            }
         }
      }

      return list;
   }

   private static void clearChargedProjectiles(ItemStack itemStack) {
      CompoundTag var1 = itemStack.getTag();
      if(var1 != null) {
         ListTag var2 = var1.getList("ChargedProjectiles", 9);
         var2.clear();
         var1.put("ChargedProjectiles", var2);
      }

   }

   private static boolean containsChargedProjectile(ItemStack itemStack, Item item) {
      return getChargedProjectiles(itemStack).stream().anyMatch((itemStack) -> {
         return itemStack.getItem() == item;
      });
   }

   private static void shootProjectile(Level level, LivingEntity livingEntity, InteractionHand interactionHand, ItemStack var3, ItemStack var4, float var5, boolean var6, float var7, float var8, float var9) {
      if(!level.isClientSide) {
         boolean var10 = var4.getItem() == Items.FIREWORK_ROCKET;
         Projectile var11;
         if(var10) {
            var11 = new FireworkRocketEntity(level, var4, livingEntity.x, livingEntity.y + (double)livingEntity.getEyeHeight() - 0.15000000596046448D, livingEntity.z, true);
         } else {
            var11 = getArrow(level, livingEntity, var3, var4);
            if(var6 || var9 != 0.0F) {
               ((AbstractArrow)var11).pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
         }

         if(livingEntity instanceof CrossbowAttackMob) {
            CrossbowAttackMob var12 = (CrossbowAttackMob)livingEntity;
            var12.shootProjectile(var12.getTarget(), var3, var11, var9);
         } else {
            Vec3 var12 = livingEntity.getUpVector(1.0F);
            Quaternion var13 = new Quaternion(new Vector3f(var12), var9, true);
            Vec3 var14 = livingEntity.getViewVector(1.0F);
            Vector3f var15 = new Vector3f(var14);
            var15.transform(var13);
            var11.shoot((double)var15.x(), (double)var15.y(), (double)var15.z(), var7, var8);
         }

         var3.hurtAndBreak(var10?3:1, livingEntity, (livingEntity) -> {
            livingEntity.broadcastBreakEvent(interactionHand);
         });
         level.addFreshEntity((Entity)var11);
         level.playSound((Player)null, livingEntity.x, livingEntity.y, livingEntity.z, SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, var5);
      }
   }

   private static AbstractArrow getArrow(Level level, LivingEntity livingEntity, ItemStack var2, ItemStack var3) {
      ArrowItem var4 = (ArrowItem)((ArrowItem)(var3.getItem() instanceof ArrowItem?var3.getItem():Items.ARROW));
      AbstractArrow var5 = var4.createArrow(level, var3, livingEntity);
      if(livingEntity instanceof Player) {
         var5.setCritArrow(true);
      }

      var5.setSoundEvent(SoundEvents.CROSSBOW_HIT);
      var5.setShotFromCrossbow(true);
      int var6 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, var2);
      if(var6 > 0) {
         var5.setPierceLevel((byte)var6);
      }

      return var5;
   }

   public static void performShooting(Level level, LivingEntity livingEntity, InteractionHand interactionHand, ItemStack itemStack, float var4, float var5) {
      List<ItemStack> var6 = getChargedProjectiles(itemStack);
      float[] vars7 = getShotPitches(livingEntity.getRandom());

      for(int var8 = 0; var8 < var6.size(); ++var8) {
         ItemStack var9 = (ItemStack)var6.get(var8);
         boolean var10 = livingEntity instanceof Player && ((Player)livingEntity).abilities.instabuild;
         if(!var9.isEmpty()) {
            if(var8 == 0) {
               shootProjectile(level, livingEntity, interactionHand, itemStack, var9, vars7[var8], var10, var4, var5, 0.0F);
            } else if(var8 == 1) {
               shootProjectile(level, livingEntity, interactionHand, itemStack, var9, vars7[var8], var10, var4, var5, -10.0F);
            } else if(var8 == 2) {
               shootProjectile(level, livingEntity, interactionHand, itemStack, var9, vars7[var8], var10, var4, var5, 10.0F);
            }
         }
      }

      onCrossbowShot(level, livingEntity, itemStack);
   }

   private static float[] getShotPitches(Random random) {
      boolean var1 = random.nextBoolean();
      return new float[]{1.0F, getRandomShotPitch(var1), getRandomShotPitch(!var1)};
   }

   private static float getRandomShotPitch(boolean b) {
      float var1 = b?0.63F:0.43F;
      return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + var1;
   }

   private static void onCrossbowShot(Level level, LivingEntity livingEntity, ItemStack itemStack) {
      if(livingEntity instanceof ServerPlayer) {
         ServerPlayer var3 = (ServerPlayer)livingEntity;
         if(!level.isClientSide) {
            CriteriaTriggers.SHOT_CROSSBOW.trigger(var3, itemStack);
         }

         var3.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
      }

      clearChargedProjectiles(itemStack);
   }

   public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int var4) {
      if(!level.isClientSide) {
         int var5 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, itemStack);
         SoundEvent var6 = this.getStartSound(var5);
         SoundEvent var7 = var5 == 0?SoundEvents.CROSSBOW_LOADING_MIDDLE:null;
         float var8 = (float)(itemStack.getUseDuration() - var4) / (float)getChargeDuration(itemStack);
         if(var8 < 0.2F) {
            this.startSoundPlayed = false;
            this.midLoadSoundPlayed = false;
         }

         if(var8 >= 0.2F && !this.startSoundPlayed) {
            this.startSoundPlayed = true;
            level.playSound((Player)null, livingEntity.x, livingEntity.y, livingEntity.z, var6, SoundSource.PLAYERS, 0.5F, 1.0F);
         }

         if(var8 >= 0.5F && var7 != null && !this.midLoadSoundPlayed) {
            this.midLoadSoundPlayed = true;
            level.playSound((Player)null, livingEntity.x, livingEntity.y, livingEntity.z, var7, SoundSource.PLAYERS, 0.5F, 1.0F);
         }
      }

   }

   public int getUseDuration(ItemStack itemStack) {
      return getChargeDuration(itemStack) + 3;
   }

   public static int getChargeDuration(ItemStack itemStack) {
      int var1 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, itemStack);
      return var1 == 0?25:25 - 5 * var1;
   }

   public UseAnim getUseAnimation(ItemStack itemStack) {
      return UseAnim.CROSSBOW;
   }

   private SoundEvent getStartSound(int i) {
      switch(i) {
      case 1:
         return SoundEvents.CROSSBOW_QUICK_CHARGE_1;
      case 2:
         return SoundEvents.CROSSBOW_QUICK_CHARGE_2;
      case 3:
         return SoundEvents.CROSSBOW_QUICK_CHARGE_3;
      default:
         return SoundEvents.CROSSBOW_LOADING_START;
      }
   }

   private static float getPowerForTime(int var0, ItemStack itemStack) {
      float var2 = (float)var0 / (float)getChargeDuration(itemStack);
      if(var2 > 1.0F) {
         var2 = 1.0F;
      }

      return var2;
   }

   public void appendHoverText(ItemStack itemStack, @Nullable Level level, List list, TooltipFlag tooltipFlag) {
      List<ItemStack> list = getChargedProjectiles(itemStack);
      if(isCharged(itemStack) && !list.isEmpty()) {
         ItemStack var6 = (ItemStack)list.get(0);
         list.add((new TranslatableComponent("item.minecraft.crossbow.projectile", new Object[0])).append(" ").append(var6.getDisplayName()));
         if(tooltipFlag.isAdvanced() && var6.getItem() == Items.FIREWORK_ROCKET) {
            List<Component> var7 = Lists.newArrayList();
            Items.FIREWORK_ROCKET.appendHoverText(var6, level, var7, tooltipFlag);
            if(!var7.isEmpty()) {
               for(int var8 = 0; var8 < ((List)var7).size(); ++var8) {
                  var7.set(var8, (new TextComponent("  ")).append((Component)var7.get(var8)).withStyle(ChatFormatting.GRAY));
               }

               list.addAll(var7);
            }
         }

      }
   }

   private static float getShootingPower(ItemStack itemStack) {
      return itemStack.getItem() == Items.CROSSBOW && containsChargedProjectile(itemStack, Items.FIREWORK_ROCKET)?1.6F:3.15F;
   }
}
