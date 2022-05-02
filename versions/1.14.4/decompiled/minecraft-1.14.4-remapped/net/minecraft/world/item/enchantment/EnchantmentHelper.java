package net.minecraft.world.item.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.WeighedRandom;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.SweepingEdgeEnchantment;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class EnchantmentHelper {
   public static int getItemEnchantmentLevel(Enchantment enchantment, ItemStack itemStack) {
      if(itemStack.isEmpty()) {
         return 0;
      } else {
         ResourceLocation var2 = Registry.ENCHANTMENT.getKey(enchantment);
         ListTag var3 = itemStack.getEnchantmentTags();

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            CompoundTag var5 = var3.getCompound(var4);
            ResourceLocation var6 = ResourceLocation.tryParse(var5.getString("id"));
            if(var6 != null && var6.equals(var2)) {
               return var5.getInt("lvl");
            }
         }

         return 0;
      }
   }

   public static Map getEnchantments(ItemStack itemStack) {
      Map<Enchantment, Integer> map = Maps.newLinkedHashMap();
      ListTag var2 = itemStack.getItem() == Items.ENCHANTED_BOOK?EnchantedBookItem.getEnchantments(itemStack):itemStack.getEnchantmentTags();

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         CompoundTag var4 = var2.getCompound(var3);
         Registry.ENCHANTMENT.getOptional(ResourceLocation.tryParse(var4.getString("id"))).ifPresent((enchantment) -> {
            Integer var10000 = (Integer)map.put(enchantment, Integer.valueOf(var4.getInt("lvl")));
         });
      }

      return map;
   }

   public static void setEnchantments(Map map, ItemStack itemStack) {
      ListTag var2 = new ListTag();

      for(Entry<Enchantment, Integer> var4 : map.entrySet()) {
         Enchantment var5 = (Enchantment)var4.getKey();
         if(var5 != null) {
            int var6 = ((Integer)var4.getValue()).intValue();
            CompoundTag var7 = new CompoundTag();
            var7.putString("id", String.valueOf(Registry.ENCHANTMENT.getKey(var5)));
            var7.putShort("lvl", (short)var6);
            var2.add(var7);
            if(itemStack.getItem() == Items.ENCHANTED_BOOK) {
               EnchantedBookItem.addEnchantment(itemStack, new EnchantmentInstance(var5, var6));
            }
         }
      }

      if(var2.isEmpty()) {
         itemStack.removeTagKey("Enchantments");
      } else if(itemStack.getItem() != Items.ENCHANTED_BOOK) {
         itemStack.addTagElement("Enchantments", var2);
      }

   }

   private static void runIterationOnItem(EnchantmentHelper.EnchantmentVisitor enchantmentHelper$EnchantmentVisitor, ItemStack itemStack) {
      if(!itemStack.isEmpty()) {
         ListTag var2 = itemStack.getEnchantmentTags();

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            String var4 = var2.getCompound(var3).getString("id");
            int var5 = var2.getCompound(var3).getInt("lvl");
            Registry.ENCHANTMENT.getOptional(ResourceLocation.tryParse(var4)).ifPresent((enchantment) -> {
               enchantmentHelper$EnchantmentVisitor.accept(enchantment, var5);
            });
         }

      }
   }

   private static void runIterationOnInventory(EnchantmentHelper.EnchantmentVisitor enchantmentHelper$EnchantmentVisitor, Iterable iterable) {
      for(ItemStack var3 : iterable) {
         runIterationOnItem(enchantmentHelper$EnchantmentVisitor, var3);
      }

   }

   public static int getDamageProtection(Iterable iterable, DamageSource damageSource) {
      MutableInt var2 = new MutableInt();
      runIterationOnInventory((enchantment, var3) -> {
         var2.add(enchantment.getDamageProtection(var3, damageSource));
      }, iterable);
      return var2.intValue();
   }

   public static float getDamageBonus(ItemStack itemStack, MobType mobType) {
      MutableFloat var2 = new MutableFloat();
      runIterationOnItem((enchantment, var3) -> {
         var2.add(enchantment.getDamageBonus(var3, mobType));
      }, itemStack);
      return var2.floatValue();
   }

   public static float getSweepingDamageRatio(LivingEntity livingEntity) {
      int var1 = getEnchantmentLevel(Enchantments.SWEEPING_EDGE, livingEntity);
      return var1 > 0?SweepingEdgeEnchantment.getSweepingDamageRatio(var1):0.0F;
   }

   public static void doPostHurtEffects(LivingEntity livingEntity, Entity entity) {
      EnchantmentHelper.EnchantmentVisitor var2 = (enchantment, var3) -> {
         enchantment.doPostHurt(livingEntity, entity, var3);
      };
      if(livingEntity != null) {
         runIterationOnInventory(var2, livingEntity.getAllSlots());
      }

      if(entity instanceof Player) {
         runIterationOnItem(var2, livingEntity.getMainHandItem());
      }

   }

   public static void doPostDamageEffects(LivingEntity livingEntity, Entity entity) {
      EnchantmentHelper.EnchantmentVisitor var2 = (enchantment, var3) -> {
         enchantment.doPostAttack(livingEntity, entity, var3);
      };
      if(livingEntity != null) {
         runIterationOnInventory(var2, livingEntity.getAllSlots());
      }

      if(livingEntity instanceof Player) {
         runIterationOnItem(var2, livingEntity.getMainHandItem());
      }

   }

   public static int getEnchantmentLevel(Enchantment enchantment, LivingEntity livingEntity) {
      Iterable<ItemStack> var2 = enchantment.getSlotItems(livingEntity).values();
      if(var2 == null) {
         return 0;
      } else {
         int var3 = 0;

         for(ItemStack var5 : var2) {
            int var6 = getItemEnchantmentLevel(enchantment, var5);
            if(var6 > var3) {
               var3 = var6;
            }
         }

         return var3;
      }
   }

   public static int getKnockbackBonus(LivingEntity livingEntity) {
      return getEnchantmentLevel(Enchantments.KNOCKBACK, livingEntity);
   }

   public static int getFireAspect(LivingEntity livingEntity) {
      return getEnchantmentLevel(Enchantments.FIRE_ASPECT, livingEntity);
   }

   public static int getRespiration(LivingEntity livingEntity) {
      return getEnchantmentLevel(Enchantments.RESPIRATION, livingEntity);
   }

   public static int getDepthStrider(LivingEntity livingEntity) {
      return getEnchantmentLevel(Enchantments.DEPTH_STRIDER, livingEntity);
   }

   public static int getBlockEfficiency(LivingEntity livingEntity) {
      return getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, livingEntity);
   }

   public static int getFishingLuckBonus(ItemStack itemStack) {
      return getItemEnchantmentLevel(Enchantments.FISHING_LUCK, itemStack);
   }

   public static int getFishingSpeedBonus(ItemStack itemStack) {
      return getItemEnchantmentLevel(Enchantments.FISHING_SPEED, itemStack);
   }

   public static int getMobLooting(LivingEntity livingEntity) {
      return getEnchantmentLevel(Enchantments.MOB_LOOTING, livingEntity);
   }

   public static boolean hasAquaAffinity(LivingEntity livingEntity) {
      return getEnchantmentLevel(Enchantments.AQUA_AFFINITY, livingEntity) > 0;
   }

   public static boolean hasFrostWalker(LivingEntity livingEntity) {
      return getEnchantmentLevel(Enchantments.FROST_WALKER, livingEntity) > 0;
   }

   public static boolean hasBindingCurse(ItemStack itemStack) {
      return getItemEnchantmentLevel(Enchantments.BINDING_CURSE, itemStack) > 0;
   }

   public static boolean hasVanishingCurse(ItemStack itemStack) {
      return getItemEnchantmentLevel(Enchantments.VANISHING_CURSE, itemStack) > 0;
   }

   public static int getLoyalty(ItemStack itemStack) {
      return getItemEnchantmentLevel(Enchantments.LOYALTY, itemStack);
   }

   public static int getRiptide(ItemStack itemStack) {
      return getItemEnchantmentLevel(Enchantments.RIPTIDE, itemStack);
   }

   public static boolean hasChanneling(ItemStack itemStack) {
      return getItemEnchantmentLevel(Enchantments.CHANNELING, itemStack) > 0;
   }

   @Nullable
   public static Entry getRandomItemWith(Enchantment enchantment, LivingEntity livingEntity) {
      Map<EquipmentSlot, ItemStack> var2 = enchantment.getSlotItems(livingEntity);
      if(var2.isEmpty()) {
         return null;
      } else {
         List<Entry<EquipmentSlot, ItemStack>> var3 = Lists.newArrayList();

         for(Entry<EquipmentSlot, ItemStack> var5 : var2.entrySet()) {
            ItemStack var6 = (ItemStack)var5.getValue();
            if(!var6.isEmpty() && getItemEnchantmentLevel(enchantment, var6) > 0) {
               var3.add(var5);
            }
         }

         return var3.isEmpty()?null:(Entry)var3.get(livingEntity.getRandom().nextInt(var3.size()));
      }
   }

   public static int getEnchantmentCost(Random random, int var1, int var2, ItemStack itemStack) {
      Item var4 = itemStack.getItem();
      int var5 = var4.getEnchantmentValue();
      if(var5 <= 0) {
         return 0;
      } else {
         if(var2 > 15) {
            var2 = 15;
         }

         int var6 = random.nextInt(8) + 1 + (var2 >> 1) + random.nextInt(var2 + 1);
         return var1 == 0?Math.max(var6 / 3, 1):(var1 == 1?var6 * 2 / 3 + 1:Math.max(var6, var2 * 2));
      }
   }

   public static ItemStack enchantItem(Random random, ItemStack var1, int var2, boolean var3) {
      List<EnchantmentInstance> var4 = selectEnchantment(random, var1, var2, var3);
      boolean var5 = var1.getItem() == Items.BOOK;
      if(var5) {
         var1 = new ItemStack(Items.ENCHANTED_BOOK);
      }

      for(EnchantmentInstance var7 : var4) {
         if(var5) {
            EnchantedBookItem.addEnchantment(var1, var7);
         } else {
            var1.enchant(var7.enchantment, var7.level);
         }
      }

      return var1;
   }

   public static List selectEnchantment(Random random, ItemStack itemStack, int var2, boolean var3) {
      List<EnchantmentInstance> list = Lists.newArrayList();
      Item var5 = itemStack.getItem();
      int var6 = var5.getEnchantmentValue();
      if(var6 <= 0) {
         return list;
      } else {
         var2 = var2 + 1 + random.nextInt(var6 / 4 + 1) + random.nextInt(var6 / 4 + 1);
         float var7 = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
         var2 = Mth.clamp(Math.round((float)var2 + (float)var2 * var7), 1, Integer.MAX_VALUE);
         List<EnchantmentInstance> var8 = getAvailableEnchantmentResults(var2, itemStack, var3);
         if(!var8.isEmpty()) {
            list.add(WeighedRandom.getRandomItem(random, var8));

            while(random.nextInt(50) <= var2) {
               filterCompatibleEnchantments(var8, (EnchantmentInstance)Util.lastOf(list));
               if(var8.isEmpty()) {
                  break;
               }

               list.add(WeighedRandom.getRandomItem(random, var8));
               var2 /= 2;
            }
         }

         return list;
      }
   }

   public static void filterCompatibleEnchantments(List list, EnchantmentInstance enchantmentInstance) {
      Iterator<EnchantmentInstance> var2 = list.iterator();

      while(var2.hasNext()) {
         if(!enchantmentInstance.enchantment.isCompatibleWith(((EnchantmentInstance)var2.next()).enchantment)) {
            var2.remove();
         }
      }

   }

   public static boolean isEnchantmentCompatible(Collection collection, Enchantment enchantment) {
      for(Enchantment var3 : collection) {
         if(!var3.isCompatibleWith(enchantment)) {
            return false;
         }
      }

      return true;
   }

   public static List getAvailableEnchantmentResults(int var0, ItemStack itemStack, boolean var2) {
      List<EnchantmentInstance> list = Lists.newArrayList();
      Item var4 = itemStack.getItem();
      boolean var5 = itemStack.getItem() == Items.BOOK;

      for(Enchantment var7 : Registry.ENCHANTMENT) {
         if((!var7.isTreasureOnly() || var2) && (var7.category.canEnchant(var4) || var5)) {
            for(int var8 = var7.getMaxLevel(); var8 > var7.getMinLevel() - 1; --var8) {
               if(var0 >= var7.getMinCost(var8) && var0 <= var7.getMaxCost(var8)) {
                  list.add(new EnchantmentInstance(var7, var8));
                  break;
               }
            }
         }
      }

      return list;
   }

   @FunctionalInterface
   interface EnchantmentVisitor {
      void accept(Enchantment var1, int var2);
   }
}
