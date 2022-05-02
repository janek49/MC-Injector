package net.minecraft.world.entity.projectile;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

public class Arrow extends AbstractArrow {
   private static final EntityDataAccessor ID_EFFECT_COLOR = SynchedEntityData.defineId(Arrow.class, EntityDataSerializers.INT);
   private Potion potion = Potions.EMPTY;
   private final Set effects = Sets.newHashSet();
   private boolean fixedColor;

   public Arrow(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public Arrow(Level level, double var2, double var4, double var6) {
      super(EntityType.ARROW, var2, var4, var6, level);
   }

   public Arrow(Level level, LivingEntity livingEntity) {
      super(EntityType.ARROW, livingEntity, level);
   }

   public void setEffectsFromItem(ItemStack effectsFromItem) {
      if(effectsFromItem.getItem() == Items.TIPPED_ARROW) {
         this.potion = PotionUtils.getPotion(effectsFromItem);
         Collection<MobEffectInstance> var2 = PotionUtils.getCustomEffects(effectsFromItem);
         if(!var2.isEmpty()) {
            for(MobEffectInstance var4 : var2) {
               this.effects.add(new MobEffectInstance(var4));
            }
         }

         int var3 = getCustomColor(effectsFromItem);
         if(var3 == -1) {
            this.updateColor();
         } else {
            this.setFixedColor(var3);
         }
      } else if(effectsFromItem.getItem() == Items.ARROW) {
         this.potion = Potions.EMPTY;
         this.effects.clear();
         this.entityData.set(ID_EFFECT_COLOR, Integer.valueOf(-1));
      }

   }

   public static int getCustomColor(ItemStack itemStack) {
      CompoundTag var1 = itemStack.getTag();
      return var1 != null && var1.contains("CustomPotionColor", 99)?var1.getInt("CustomPotionColor"):-1;
   }

   private void updateColor() {
      this.fixedColor = false;
      this.entityData.set(ID_EFFECT_COLOR, Integer.valueOf(PotionUtils.getColor((Collection)PotionUtils.getAllEffects(this.potion, this.effects))));
   }

   public void addEffect(MobEffectInstance mobEffectInstance) {
      this.effects.add(mobEffectInstance);
      this.getEntityData().set(ID_EFFECT_COLOR, Integer.valueOf(PotionUtils.getColor((Collection)PotionUtils.getAllEffects(this.potion, this.effects))));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(ID_EFFECT_COLOR, Integer.valueOf(-1));
   }

   public void tick() {
      super.tick();
      if(this.level.isClientSide) {
         if(this.inGround) {
            if(this.inGroundTime % 5 == 0) {
               this.makeParticle(1);
            }
         } else {
            this.makeParticle(2);
         }
      } else if(this.inGround && this.inGroundTime != 0 && !this.effects.isEmpty() && this.inGroundTime >= 600) {
         this.level.broadcastEntityEvent(this, (byte)0);
         this.potion = Potions.EMPTY;
         this.effects.clear();
         this.entityData.set(ID_EFFECT_COLOR, Integer.valueOf(-1));
      }

   }

   private void makeParticle(int i) {
      int var2 = this.getColor();
      if(var2 != -1 && i > 0) {
         double var3 = (double)(var2 >> 16 & 255) / 255.0D;
         double var5 = (double)(var2 >> 8 & 255) / 255.0D;
         double var7 = (double)(var2 >> 0 & 255) / 255.0D;

         for(int var9 = 0; var9 < i; ++var9) {
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.x + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth(), this.y + this.random.nextDouble() * (double)this.getBbHeight(), this.z + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth(), var3, var5, var7);
         }

      }
   }

   public int getColor() {
      return ((Integer)this.entityData.get(ID_EFFECT_COLOR)).intValue();
   }

   private void setFixedColor(int fixedColor) {
      this.fixedColor = true;
      this.entityData.set(ID_EFFECT_COLOR, Integer.valueOf(fixedColor));
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      if(this.potion != Potions.EMPTY && this.potion != null) {
         compoundTag.putString("Potion", Registry.POTION.getKey(this.potion).toString());
      }

      if(this.fixedColor) {
         compoundTag.putInt("Color", this.getColor());
      }

      if(!this.effects.isEmpty()) {
         ListTag var2 = new ListTag();

         for(MobEffectInstance var4 : this.effects) {
            var2.add(var4.save(new CompoundTag()));
         }

         compoundTag.put("CustomPotionEffects", var2);
      }

   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      if(compoundTag.contains("Potion", 8)) {
         this.potion = PotionUtils.getPotion(compoundTag);
      }

      for(MobEffectInstance var3 : PotionUtils.getCustomEffects(compoundTag)) {
         this.addEffect(var3);
      }

      if(compoundTag.contains("Color", 99)) {
         this.setFixedColor(compoundTag.getInt("Color"));
      } else {
         this.updateColor();
      }

   }

   protected void doPostHurtEffects(LivingEntity livingEntity) {
      super.doPostHurtEffects(livingEntity);

      for(MobEffectInstance var3 : this.potion.getEffects()) {
         livingEntity.addEffect(new MobEffectInstance(var3.getEffect(), Math.max(var3.getDuration() / 8, 1), var3.getAmplifier(), var3.isAmbient(), var3.isVisible()));
      }

      if(!this.effects.isEmpty()) {
         for(MobEffectInstance var3 : this.effects) {
            livingEntity.addEffect(var3);
         }
      }

   }

   protected ItemStack getPickupItem() {
      if(this.effects.isEmpty() && this.potion == Potions.EMPTY) {
         return new ItemStack(Items.ARROW);
      } else {
         ItemStack itemStack = new ItemStack(Items.TIPPED_ARROW);
         PotionUtils.setPotion(itemStack, this.potion);
         PotionUtils.setCustomEffects(itemStack, this.effects);
         if(this.fixedColor) {
            itemStack.getOrCreateTag().putInt("CustomPotionColor", this.getColor());
         }

         return itemStack;
      }
   }

   public void handleEntityEvent(byte b) {
      if(b == 0) {
         int var2 = this.getColor();
         if(var2 != -1) {
            double var3 = (double)(var2 >> 16 & 255) / 255.0D;
            double var5 = (double)(var2 >> 8 & 255) / 255.0D;
            double var7 = (double)(var2 >> 0 & 255) / 255.0D;

            for(int var9 = 0; var9 < 20; ++var9) {
               this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.x + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth(), this.y + this.random.nextDouble() * (double)this.getBbHeight(), this.z + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth(), var3, var5, var7);
            }
         }
      } else {
         super.handleEntityEvent(b);
      }

   }
}
