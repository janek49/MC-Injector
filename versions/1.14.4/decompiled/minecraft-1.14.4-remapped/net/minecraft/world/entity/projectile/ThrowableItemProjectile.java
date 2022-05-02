package net.minecraft.world.entity.projectile;

import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class ThrowableItemProjectile extends ThrowableProjectile implements ItemSupplier {
   private static final EntityDataAccessor DATA_ITEM_STACK = SynchedEntityData.defineId(ThrowableItemProjectile.class, EntityDataSerializers.ITEM_STACK);

   public ThrowableItemProjectile(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public ThrowableItemProjectile(EntityType entityType, double var2, double var4, double var6, Level level) {
      super(entityType, var2, var4, var6, level);
   }

   public ThrowableItemProjectile(EntityType entityType, LivingEntity livingEntity, Level level) {
      super(entityType, livingEntity, level);
   }

   public void setItem(ItemStack item) {
      if(item.getItem() != this.getDefaultItem() || item.hasTag()) {
         this.getEntityData().set(DATA_ITEM_STACK, Util.make(item.copy(), (itemStack) -> {
            itemStack.setCount(1);
         }));
      }

   }

   protected abstract Item getDefaultItem();

   protected ItemStack getItemRaw() {
      return (ItemStack)this.getEntityData().get(DATA_ITEM_STACK);
   }

   public ItemStack getItem() {
      ItemStack itemStack = this.getItemRaw();
      return itemStack.isEmpty()?new ItemStack(this.getDefaultItem()):itemStack;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      ItemStack var2 = this.getItemRaw();
      if(!var2.isEmpty()) {
         compoundTag.put("Item", var2.save(new CompoundTag()));
      }

   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      ItemStack var2 = ItemStack.of(compoundTag.getCompound("Item"));
      this.setItem(var2);
   }
}
