package net.minecraft.world.effect;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.world.entity.player.Player;

public class MobEffect {
   private final Map attributeModifiers = Maps.newHashMap();
   private final MobEffectCategory category;
   private final int color;
   @Nullable
   private String descriptionId;

   @Nullable
   public static MobEffect byId(int id) {
      return (MobEffect)Registry.MOB_EFFECT.byId(id);
   }

   public static int getId(MobEffect mobEffect) {
      return Registry.MOB_EFFECT.getId(mobEffect);
   }

   protected MobEffect(MobEffectCategory category, int color) {
      this.category = category;
      this.color = color;
   }

   public void applyEffectTick(LivingEntity livingEntity, int var2) {
      if(this == MobEffects.REGENERATION) {
         if(livingEntity.getHealth() < livingEntity.getMaxHealth()) {
            livingEntity.heal(1.0F);
         }
      } else if(this == MobEffects.POISON) {
         if(livingEntity.getHealth() > 1.0F) {
            livingEntity.hurt(DamageSource.MAGIC, 1.0F);
         }
      } else if(this == MobEffects.WITHER) {
         livingEntity.hurt(DamageSource.WITHER, 1.0F);
      } else if(this == MobEffects.HUNGER && livingEntity instanceof Player) {
         ((Player)livingEntity).causeFoodExhaustion(0.005F * (float)(var2 + 1));
      } else if(this == MobEffects.SATURATION && livingEntity instanceof Player) {
         if(!livingEntity.level.isClientSide) {
            ((Player)livingEntity).getFoodData().eat(var2 + 1, 1.0F);
         }
      } else if((this != MobEffects.HEAL || livingEntity.isInvertedHealAndHarm()) && (this != MobEffects.HARM || !livingEntity.isInvertedHealAndHarm())) {
         if(this == MobEffects.HARM && !livingEntity.isInvertedHealAndHarm() || this == MobEffects.HEAL && livingEntity.isInvertedHealAndHarm()) {
            livingEntity.hurt(DamageSource.MAGIC, (float)(6 << var2));
         }
      } else {
         livingEntity.heal((float)Math.max(4 << var2, 0));
      }

   }

   public void applyInstantenousEffect(@Nullable Entity var1, @Nullable Entity var2, LivingEntity livingEntity, int var4, double var5) {
      if((this != MobEffects.HEAL || livingEntity.isInvertedHealAndHarm()) && (this != MobEffects.HARM || !livingEntity.isInvertedHealAndHarm())) {
         if(this == MobEffects.HARM && !livingEntity.isInvertedHealAndHarm() || this == MobEffects.HEAL && livingEntity.isInvertedHealAndHarm()) {
            int var7 = (int)(var5 * (double)(6 << var4) + 0.5D);
            if(var1 == null) {
               livingEntity.hurt(DamageSource.MAGIC, (float)var7);
            } else {
               livingEntity.hurt(DamageSource.indirectMagic(var1, var2), (float)var7);
            }
         } else {
            this.applyEffectTick(livingEntity, var4);
         }
      } else {
         int var7 = (int)(var5 * (double)(4 << var4) + 0.5D);
         livingEntity.heal((float)var7);
      }

   }

   public boolean isDurationEffectTick(int var1, int var2) {
      if(this == MobEffects.REGENERATION) {
         int var3 = 50 >> var2;
         return var3 > 0?var1 % var3 == 0:true;
      } else if(this == MobEffects.POISON) {
         int var3 = 25 >> var2;
         return var3 > 0?var1 % var3 == 0:true;
      } else if(this == MobEffects.WITHER) {
         int var3 = 40 >> var2;
         return var3 > 0?var1 % var3 == 0:true;
      } else {
         return this == MobEffects.HUNGER;
      }
   }

   public boolean isInstantenous() {
      return false;
   }

   protected String getOrCreateDescriptionId() {
      if(this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("effect", Registry.MOB_EFFECT.getKey(this));
      }

      return this.descriptionId;
   }

   public String getDescriptionId() {
      return this.getOrCreateDescriptionId();
   }

   public Component getDisplayName() {
      return new TranslatableComponent(this.getDescriptionId(), new Object[0]);
   }

   public MobEffectCategory getCategory() {
      return this.category;
   }

   public int getColor() {
      return this.color;
   }

   public MobEffect addAttributeModifier(Attribute attribute, String string, double var3, AttributeModifier.Operation attributeModifier$Operation) {
      AttributeModifier var6 = new AttributeModifier(UUID.fromString(string), this::getDescriptionId, var3, attributeModifier$Operation);
      this.attributeModifiers.put(attribute, var6);
      return this;
   }

   public Map getAttributeModifiers() {
      return this.attributeModifiers;
   }

   public void removeAttributeModifiers(LivingEntity livingEntity, BaseAttributeMap baseAttributeMap, int var3) {
      for(Entry<Attribute, AttributeModifier> var5 : this.attributeModifiers.entrySet()) {
         AttributeInstance var6 = baseAttributeMap.getInstance((Attribute)var5.getKey());
         if(var6 != null) {
            var6.removeModifier((AttributeModifier)var5.getValue());
         }
      }

   }

   public void addAttributeModifiers(LivingEntity livingEntity, BaseAttributeMap baseAttributeMap, int var3) {
      for(Entry<Attribute, AttributeModifier> var5 : this.attributeModifiers.entrySet()) {
         AttributeInstance var6 = baseAttributeMap.getInstance((Attribute)var5.getKey());
         if(var6 != null) {
            AttributeModifier var7 = (AttributeModifier)var5.getValue();
            var6.removeModifier(var7);
            var6.addModifier(new AttributeModifier(var7.getId(), this.getDescriptionId() + " " + var3, this.getAttributeModifierValue(var3, var7), var7.getOperation()));
         }
      }

   }

   public double getAttributeModifierValue(int var1, AttributeModifier attributeModifier) {
      return attributeModifier.getAmount() * (double)(var1 + 1);
   }

   public boolean isBeneficial() {
      return this.category == MobEffectCategory.BENEFICIAL;
   }
}
