package net.minecraft.world.entity.animal.horse;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SoundType;

public class Horse extends AbstractHorse {
   private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
   private static final EntityDataAccessor DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(Horse.class, EntityDataSerializers.INT);
   private static final String[] VARIANT_TEXTURES = new String[]{"textures/entity/horse/horse_white.png", "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_chestnut.png", "textures/entity/horse/horse_brown.png", "textures/entity/horse/horse_black.png", "textures/entity/horse/horse_gray.png", "textures/entity/horse/horse_darkbrown.png"};
   private static final String[] VARIANT_HASHES = new String[]{"hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
   private static final String[] MARKING_TEXTURES = new String[]{null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_whitedots.png", "textures/entity/horse/horse_markings_blackdots.png"};
   private static final String[] MARKING_HASHES = new String[]{"", "wo_", "wmo", "wdo", "bdo"};
   private String layerTextureHashName;
   private final String[] layerTextureLayers = new String[2];

   public Horse(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_TYPE_VARIANT, Integer.valueOf(0));
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("Variant", this.getVariant());
      if(!this.inventory.getItem(1).isEmpty()) {
         compoundTag.put("ArmorItem", this.inventory.getItem(1).save(new CompoundTag()));
      }

   }

   public ItemStack getArmor() {
      return this.getItemBySlot(EquipmentSlot.CHEST);
   }

   private void setArmor(ItemStack armor) {
      this.setItemSlot(EquipmentSlot.CHEST, armor);
      this.setDropChance(EquipmentSlot.CHEST, 0.0F);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.setVariant(compoundTag.getInt("Variant"));
      if(compoundTag.contains("ArmorItem", 10)) {
         ItemStack var2 = ItemStack.of(compoundTag.getCompound("ArmorItem"));
         if(!var2.isEmpty() && this.isArmor(var2)) {
            this.inventory.setItem(1, var2);
         }
      }

      this.updateEquipment();
   }

   public void setVariant(int variant) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, Integer.valueOf(variant));
      this.clearLayeredTextureInfo();
   }

   public int getVariant() {
      return ((Integer)this.entityData.get(DATA_ID_TYPE_VARIANT)).intValue();
   }

   private void clearLayeredTextureInfo() {
      this.layerTextureHashName = null;
   }

   private void rebuildLayeredTextureInfo() {
      int var1 = this.getVariant();
      int var2 = (var1 & 255) % 7;
      int var3 = ((var1 & '\uff00') >> 8) % 5;
      this.layerTextureLayers[0] = VARIANT_TEXTURES[var2];
      this.layerTextureLayers[1] = MARKING_TEXTURES[var3];
      this.layerTextureHashName = "horse/" + VARIANT_HASHES[var2] + MARKING_HASHES[var3];
   }

   public String getLayeredTextureHashName() {
      if(this.layerTextureHashName == null) {
         this.rebuildLayeredTextureInfo();
      }

      return this.layerTextureHashName;
   }

   public String[] getLayeredTextureLayers() {
      if(this.layerTextureHashName == null) {
         this.rebuildLayeredTextureInfo();
      }

      return this.layerTextureLayers;
   }

   protected void updateEquipment() {
      super.updateEquipment();
      this.setArmorEquipment(this.inventory.getItem(1));
   }

   private void setArmorEquipment(ItemStack armorEquipment) {
      this.setArmor(armorEquipment);
      if(!this.level.isClientSide) {
         this.getAttribute(SharedMonsterAttributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
         if(this.isArmor(armorEquipment)) {
            int var2 = ((HorseArmorItem)armorEquipment.getItem()).getProtection();
            if(var2 != 0) {
               this.getAttribute(SharedMonsterAttributes.ARMOR).addModifier((new AttributeModifier(ARMOR_MODIFIER_UUID, "Horse armor bonus", (double)var2, AttributeModifier.Operation.ADDITION)).setSerialize(false));
            }
         }
      }

   }

   public void containerChanged(Container container) {
      ItemStack var2 = this.getArmor();
      super.containerChanged(container);
      ItemStack var3 = this.getArmor();
      if(this.tickCount > 20 && this.isArmor(var3) && var2 != var3) {
         this.playSound(SoundEvents.HORSE_ARMOR, 0.5F, 1.0F);
      }

   }

   protected void playGallopSound(SoundType soundType) {
      super.playGallopSound(soundType);
      if(this.random.nextInt(10) == 0) {
         this.playSound(SoundEvents.HORSE_BREATHE, soundType.getVolume() * 0.6F, soundType.getPitch());
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)this.generateRandomMaxHealth());
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(this.generateRandomSpeed());
      this.getAttribute(JUMP_STRENGTH).setBaseValue(this.generateRandomJumpStrength());
   }

   public void tick() {
      super.tick();
      if(this.level.isClientSide && this.entityData.isDirty()) {
         this.entityData.clearDirty();
         this.clearLayeredTextureInfo();
      }

   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      super.getHurtSound(damageSource);
      return SoundEvents.HORSE_HURT;
   }

   protected SoundEvent getAngrySound() {
      super.getAngrySound();
      return SoundEvents.HORSE_ANGRY;
   }

   public boolean mobInteract(Player player, InteractionHand interactionHand) {
      ItemStack var3 = player.getItemInHand(interactionHand);
      boolean var4 = !var3.isEmpty();
      if(var4 && var3.getItem() instanceof SpawnEggItem) {
         return super.mobInteract(player, interactionHand);
      } else {
         if(!this.isBaby()) {
            if(this.isTamed() && player.isSneaking()) {
               this.openInventory(player);
               return true;
            }

            if(this.isVehicle()) {
               return super.mobInteract(player, interactionHand);
            }
         }

         if(var4) {
            if(this.handleEating(player, var3)) {
               if(!player.abilities.instabuild) {
                  var3.shrink(1);
               }

               return true;
            }

            if(var3.interactEnemy(player, this, interactionHand)) {
               return true;
            }

            if(!this.isTamed()) {
               this.makeMad();
               return true;
            }

            boolean var5 = !this.isBaby() && !this.isSaddled() && var3.getItem() == Items.SADDLE;
            if(this.isArmor(var3) || var5) {
               this.openInventory(player);
               return true;
            }
         }

         if(this.isBaby()) {
            return super.mobInteract(player, interactionHand);
         } else {
            this.doPlayerRide(player);
            return true;
         }
      }
   }

   public boolean canMate(Animal animal) {
      return animal == this?false:(!(animal instanceof Donkey) && !(animal instanceof Horse)?false:this.canParent() && ((AbstractHorse)animal).canParent());
   }

   public AgableMob getBreedOffspring(AgableMob agableMob) {
      AbstractHorse var2;
      if(agableMob instanceof Donkey) {
         var2 = (AbstractHorse)EntityType.MULE.create(this.level);
      } else {
         Horse var3 = (Horse)agableMob;
         var2 = (AbstractHorse)EntityType.HORSE.create(this.level);
         int var5 = this.random.nextInt(9);
         int var4;
         if(var5 < 4) {
            var4 = this.getVariant() & 255;
         } else if(var5 < 8) {
            var4 = var3.getVariant() & 255;
         } else {
            var4 = this.random.nextInt(7);
         }

         int var6 = this.random.nextInt(5);
         if(var6 < 2) {
            var4 = var4 | this.getVariant() & '\uff00';
         } else if(var6 < 4) {
            var4 = var4 | var3.getVariant() & '\uff00';
         } else {
            var4 = var4 | this.random.nextInt(5) << 8 & '\uff00';
         }

         ((Horse)var2).setVariant(var4);
      }

      this.setOffspringAttributes(agableMob, var2);
      return var2;
   }

   public boolean wearsArmor() {
      return true;
   }

   public boolean isArmor(ItemStack itemStack) {
      return itemStack.getItem() instanceof HorseArmorItem;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      var4 = super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, var4, compoundTag);
      int var6;
      if(var4 instanceof Horse.HorseGroupData) {
         var6 = ((Horse.HorseGroupData)var4).variant;
      } else {
         var6 = this.random.nextInt(7);
         var4 = new Horse.HorseGroupData(var6);
      }

      this.setVariant(var6 | this.random.nextInt(5) << 8);
      return var4;
   }

   public static class HorseGroupData implements SpawnGroupData {
      public final int variant;

      public HorseGroupData(int variant) {
         this.variant = variant;
      }
   }
}
