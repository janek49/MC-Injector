package net.minecraft.world.item.alchemy;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

public class PotionUtils {
   public static List getMobEffects(ItemStack itemStack) {
      return getAllEffects(itemStack.getTag());
   }

   public static List getAllEffects(Potion potion, Collection collection) {
      List<MobEffectInstance> list = Lists.newArrayList();
      list.addAll(potion.getEffects());
      list.addAll(collection);
      return list;
   }

   public static List getAllEffects(@Nullable CompoundTag compoundTag) {
      List<MobEffectInstance> list = Lists.newArrayList();
      list.addAll(getPotion(compoundTag).getEffects());
      getCustomEffects(compoundTag, list);
      return list;
   }

   public static List getCustomEffects(ItemStack itemStack) {
      return getCustomEffects(itemStack.getTag());
   }

   public static List getCustomEffects(@Nullable CompoundTag compoundTag) {
      List<MobEffectInstance> list = Lists.newArrayList();
      getCustomEffects(compoundTag, list);
      return list;
   }

   public static void getCustomEffects(@Nullable CompoundTag compoundTag, List list) {
      if(compoundTag != null && compoundTag.contains("CustomPotionEffects", 9)) {
         ListTag var2 = compoundTag.getList("CustomPotionEffects", 10);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            CompoundTag var4 = var2.getCompound(var3);
            MobEffectInstance var5 = MobEffectInstance.load(var4);
            if(var5 != null) {
               list.add(var5);
            }
         }
      }

   }

   public static int getColor(ItemStack itemStack) {
      CompoundTag var1 = itemStack.getTag();
      return var1 != null && var1.contains("CustomPotionColor", 99)?var1.getInt("CustomPotionColor"):(getPotion(itemStack) == Potions.EMPTY?16253176:getColor((Collection)getMobEffects(itemStack)));
   }

   public static int getColor(Potion potion) {
      return potion == Potions.EMPTY?16253176:getColor((Collection)potion.getEffects());
   }

   public static int getColor(Collection collection) {
      int var1 = 3694022;
      if(collection.isEmpty()) {
         return 3694022;
      } else {
         float var2 = 0.0F;
         float var3 = 0.0F;
         float var4 = 0.0F;
         int var5 = 0;

         for(MobEffectInstance var7 : collection) {
            if(var7.isVisible()) {
               int var8 = var7.getEffect().getColor();
               int var9 = var7.getAmplifier() + 1;
               var2 += (float)(var9 * (var8 >> 16 & 255)) / 255.0F;
               var3 += (float)(var9 * (var8 >> 8 & 255)) / 255.0F;
               var4 += (float)(var9 * (var8 >> 0 & 255)) / 255.0F;
               var5 += var9;
            }
         }

         if(var5 == 0) {
            return 0;
         } else {
            var2 = var2 / (float)var5 * 255.0F;
            var3 = var3 / (float)var5 * 255.0F;
            var4 = var4 / (float)var5 * 255.0F;
            return (int)var2 << 16 | (int)var3 << 8 | (int)var4;
         }
      }
   }

   public static Potion getPotion(ItemStack itemStack) {
      return getPotion(itemStack.getTag());
   }

   public static Potion getPotion(@Nullable CompoundTag compoundTag) {
      return compoundTag == null?Potions.EMPTY:Potion.byName(compoundTag.getString("Potion"));
   }

   public static ItemStack setPotion(ItemStack var0, Potion potion) {
      ResourceLocation var2 = Registry.POTION.getKey(potion);
      if(potion == Potions.EMPTY) {
         var0.removeTagKey("Potion");
      } else {
         var0.getOrCreateTag().putString("Potion", var2.toString());
      }

      return var0;
   }

   public static ItemStack setCustomEffects(ItemStack var0, Collection collection) {
      if(collection.isEmpty()) {
         return var0;
      } else {
         CompoundTag var2 = var0.getOrCreateTag();
         ListTag var3 = var2.getList("CustomPotionEffects", 9);

         for(MobEffectInstance var5 : collection) {
            var3.add(var5.save(new CompoundTag()));
         }

         var2.put("CustomPotionEffects", var3);
         return var0;
      }
   }

   public static void addPotionTooltip(ItemStack itemStack, List list, float var2) {
      List<MobEffectInstance> list = getMobEffects(itemStack);
      List<Tuple<String, AttributeModifier>> var4 = Lists.newArrayList();
      if(list.isEmpty()) {
         list.add((new TranslatableComponent("effect.none", new Object[0])).withStyle(ChatFormatting.GRAY));
      } else {
         for(MobEffectInstance var6 : list) {
            Component var7 = new TranslatableComponent(var6.getDescriptionId(), new Object[0]);
            MobEffect var8 = var6.getEffect();
            Map<Attribute, AttributeModifier> var9 = var8.getAttributeModifiers();
            if(!var9.isEmpty()) {
               for(Entry<Attribute, AttributeModifier> var11 : var9.entrySet()) {
                  AttributeModifier var12 = (AttributeModifier)var11.getValue();
                  AttributeModifier var13 = new AttributeModifier(var12.getName(), var8.getAttributeModifierValue(var6.getAmplifier(), var12), var12.getOperation());
                  var4.add(new Tuple(((Attribute)var11.getKey()).getName(), var13));
               }
            }

            if(var6.getAmplifier() > 0) {
               var7.append(" ").append((Component)(new TranslatableComponent("potion.potency." + var6.getAmplifier(), new Object[0])));
            }

            if(var6.getDuration() > 20) {
               var7.append(" (").append(MobEffectUtil.formatDuration(var6, var2)).append(")");
            }

            list.add(var7.withStyle(var8.getCategory().getTooltipFormatting()));
         }
      }

      if(!var4.isEmpty()) {
         list.add(new TextComponent(""));
         list.add((new TranslatableComponent("potion.whenDrank", new Object[0])).withStyle(ChatFormatting.DARK_PURPLE));

         for(Tuple<String, AttributeModifier> var6 : var4) {
            AttributeModifier var7 = (AttributeModifier)var6.getB();
            double var8 = var7.getAmount();
            double var10;
            if(var7.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && var7.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
               var10 = var7.getAmount();
            } else {
               var10 = var7.getAmount() * 100.0D;
            }

            if(var8 > 0.0D) {
               list.add((new TranslatableComponent("attribute.modifier.plus." + var7.getOperation().toValue(), new Object[]{ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(var10), new TranslatableComponent("attribute.name." + (String)var6.getA(), new Object[0])})).withStyle(ChatFormatting.BLUE));
            } else if(var8 < 0.0D) {
               var10 = var10 * -1.0D;
               list.add((new TranslatableComponent("attribute.modifier.take." + var7.getOperation().toValue(), new Object[]{ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(var10), new TranslatableComponent("attribute.name." + (String)var6.getA(), new Object[0])})).withStyle(ChatFormatting.RED));
            }
         }
      }

   }
}
