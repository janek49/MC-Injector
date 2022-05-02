package net.minecraft.world.entity.projectile;

import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public abstract class Fireball extends AbstractHurtingProjectile implements ItemSupplier {
   private static final EntityDataAccessor DATA_ITEM_STACK = SynchedEntityData.defineId(Fireball.class, EntityDataSerializers.ITEM_STACK);

   public Fireball(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public Fireball(EntityType entityType, double var2, double var4, double var6, double var8, double var10, double var12, Level level) {
      super(entityType, var2, var4, var6, var8, var10, var12, level);
   }

   public Fireball(EntityType entityType, LivingEntity livingEntity, double var3, double var5, double var7, Level level) {
      super(entityType, livingEntity, var3, var5, var7, level);
   }

   public void setItem(ItemStack item) {
      if(item.getItem() != Items.FIRE_CHARGE || item.hasTag()) {
         this.getEntityData().set(DATA_ITEM_STACK, Util.make(item.copy(), (itemStack) -> {
            itemStack.setCount(1);
         }));
      }

   }

   protected ItemStack getItemRaw() {
      return (ItemStack)this.getEntityData().get(DATA_ITEM_STACK);
   }

   public ItemStack getItem() {
      ItemStack itemStack = this.getItemRaw();
      return itemStack.isEmpty()?new ItemStack(Items.FIRE_CHARGE):itemStack;
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
