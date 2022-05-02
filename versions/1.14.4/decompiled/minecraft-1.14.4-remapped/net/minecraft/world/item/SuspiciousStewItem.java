package net.minecraft.world.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SuspiciousStewItem extends Item {
   public SuspiciousStewItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public static void saveMobEffect(ItemStack itemStack, MobEffect mobEffect, int var2) {
      CompoundTag var3 = itemStack.getOrCreateTag();
      ListTag var4 = var3.getList("Effects", 9);
      CompoundTag var5 = new CompoundTag();
      var5.putByte("EffectId", (byte)MobEffect.getId(mobEffect));
      var5.putInt("EffectDuration", var2);
      var4.add(var5);
      var3.put("Effects", var4);
   }

   public ItemStack finishUsingItem(ItemStack var1, Level level, LivingEntity livingEntity) {
      super.finishUsingItem(var1, level, livingEntity);
      CompoundTag var4 = var1.getTag();
      if(var4 != null && var4.contains("Effects", 9)) {
         ListTag var5 = var4.getList("Effects", 10);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            int var7 = 160;
            CompoundTag var8 = var5.getCompound(var6);
            if(var8.contains("EffectDuration", 3)) {
               var7 = var8.getInt("EffectDuration");
            }

            MobEffect var9 = MobEffect.byId(var8.getByte("EffectId"));
            if(var9 != null) {
               livingEntity.addEffect(new MobEffectInstance(var9, var7));
            }
         }
      }

      return new ItemStack(Items.BOWL);
   }
}
