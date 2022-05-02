package net.minecraft.world.entity.item;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;

public class ItemEntity extends Entity {
   private static final EntityDataAccessor DATA_ITEM = SynchedEntityData.defineId(ItemEntity.class, EntityDataSerializers.ITEM_STACK);
   private int age;
   private int pickupDelay;
   private int health;
   private UUID thrower;
   private UUID owner;
   public final float bobOffs;

   public ItemEntity(EntityType entityType, Level level) {
      super(entityType, level);
      this.health = 5;
      this.bobOffs = (float)(Math.random() * 3.141592653589793D * 2.0D);
   }

   public ItemEntity(Level level, double var2, double var4, double var6) {
      this(EntityType.ITEM, level);
      this.setPos(var2, var4, var6);
      this.yRot = this.random.nextFloat() * 360.0F;
      this.setDeltaMovement(this.random.nextDouble() * 0.2D - 0.1D, 0.2D, this.random.nextDouble() * 0.2D - 0.1D);
   }

   public ItemEntity(Level level, double var2, double var4, double var6, ItemStack item) {
      this(level, var2, var4, var6);
      this.setItem(item);
   }

   protected boolean makeStepSound() {
      return false;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
   }

   public void tick() {
      if(this.getItem().isEmpty()) {
         this.remove();
      } else {
         super.tick();
         if(this.pickupDelay > 0 && this.pickupDelay != 32767) {
            --this.pickupDelay;
         }

         this.xo = this.x;
         this.yo = this.y;
         this.zo = this.z;
         Vec3 var1 = this.getDeltaMovement();
         if(this.isUnderLiquid(FluidTags.WATER)) {
            this.setUnderwaterMovement();
         } else if(!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
         }

         if(this.level.isClientSide) {
            this.noPhysics = false;
         } else {
            this.noPhysics = !this.level.noCollision(this);
            if(this.noPhysics) {
               this.checkInBlock(this.x, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.z);
            }
         }

         if(!this.onGround || getHorizontalDistanceSqr(this.getDeltaMovement()) > 9.999999747378752E-6D || (this.tickCount + this.getId()) % 4 == 0) {
            this.move(MoverType.SELF, this.getDeltaMovement());
            float var2 = 0.98F;
            if(this.onGround) {
               var2 = this.level.getBlockState(new BlockPos(this.x, this.getBoundingBox().minY - 1.0D, this.z)).getBlock().getFriction() * 0.98F;
            }

            this.setDeltaMovement(this.getDeltaMovement().multiply((double)var2, 0.98D, (double)var2));
            if(this.onGround) {
               this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, -0.5D, 1.0D));
            }
         }

         boolean var2 = Mth.floor(this.xo) != Mth.floor(this.x) || Mth.floor(this.yo) != Mth.floor(this.y) || Mth.floor(this.zo) != Mth.floor(this.z);
         int var3 = var2?2:40;
         if(this.tickCount % var3 == 0) {
            if(this.level.getFluidState(new BlockPos(this)).is(FluidTags.LAVA)) {
               this.setDeltaMovement((double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F), 0.20000000298023224D, (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F));
               this.playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
            }

            if(!this.level.isClientSide && this.isMergable()) {
               this.mergeWithNeighbours();
            }
         }

         if(this.age != -32768) {
            ++this.age;
         }

         this.hasImpulse |= this.updateInWaterState();
         if(!this.level.isClientSide) {
            double var4 = this.getDeltaMovement().subtract(var1).lengthSqr();
            if(var4 > 0.01D) {
               this.hasImpulse = true;
            }
         }

         if(!this.level.isClientSide && this.age >= 6000) {
            this.remove();
         }

      }
   }

   private void setUnderwaterMovement() {
      Vec3 var1 = this.getDeltaMovement();
      this.setDeltaMovement(var1.x * 0.9900000095367432D, var1.y + (double)(var1.y < 0.05999999865889549D?5.0E-4F:0.0F), var1.z * 0.9900000095367432D);
   }

   private void mergeWithNeighbours() {
      List<ItemEntity> var1 = this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.5D, 0.0D, 0.5D), (itemEntity) -> {
         return itemEntity != this && itemEntity.isMergable();
      });
      if(!var1.isEmpty()) {
         for(ItemEntity var3 : var1) {
            if(!this.isMergable()) {
               return;
            }

            this.merge(var3);
         }
      }

   }

   private boolean isMergable() {
      ItemStack var1 = this.getItem();
      return this.isAlive() && this.pickupDelay != 32767 && this.age != -32768 && this.age < 6000 && var1.getCount() < var1.getMaxStackSize();
   }

   private void merge(ItemEntity itemEntity) {
      ItemStack var2 = this.getItem();
      ItemStack var3 = itemEntity.getItem();
      if(var3.getItem() == var2.getItem()) {
         if(var3.getCount() + var2.getCount() <= var3.getMaxStackSize()) {
            if(!(var3.hasTag() ^ var2.hasTag())) {
               if(!var3.hasTag() || var3.getTag().equals(var2.getTag())) {
                  if(var3.getCount() < var2.getCount()) {
                     merge(this, var2, itemEntity, var3);
                  } else {
                     merge(itemEntity, var3, this, var2);
                  }

               }
            }
         }
      }
   }

   private static void merge(ItemEntity var0, ItemStack var1, ItemEntity var2, ItemStack item) {
      int var4 = Math.min(var1.getMaxStackSize() - var1.getCount(), item.getCount());
      ItemStack var5 = var1.copy();
      var5.grow(var4);
      var0.setItem(var5);
      item.shrink(var4);
      var2.setItem(item);
      var0.pickupDelay = Math.max(var0.pickupDelay, var2.pickupDelay);
      var0.age = Math.min(var0.age, var2.age);
      if(item.isEmpty()) {
         var2.remove();
      }

   }

   public void setShortLifeTime() {
      this.age = 4800;
   }

   protected void burn(int i) {
      this.hurt(DamageSource.IN_FIRE, (float)i);
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      if(this.isInvulnerableTo(damageSource)) {
         return false;
      } else if(!this.getItem().isEmpty() && this.getItem().getItem() == Items.NETHER_STAR && damageSource.isExplosion()) {
         return false;
      } else {
         this.markHurt();
         this.health = (int)((float)this.health - var2);
         if(this.health <= 0) {
            this.remove();
         }

         return false;
      }
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      compoundTag.putShort("Health", (short)this.health);
      compoundTag.putShort("Age", (short)this.age);
      compoundTag.putShort("PickupDelay", (short)this.pickupDelay);
      if(this.getThrower() != null) {
         compoundTag.put("Thrower", NbtUtils.createUUIDTag(this.getThrower()));
      }

      if(this.getOwner() != null) {
         compoundTag.put("Owner", NbtUtils.createUUIDTag(this.getOwner()));
      }

      if(!this.getItem().isEmpty()) {
         compoundTag.put("Item", this.getItem().save(new CompoundTag()));
      }

   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      this.health = compoundTag.getShort("Health");
      this.age = compoundTag.getShort("Age");
      if(compoundTag.contains("PickupDelay")) {
         this.pickupDelay = compoundTag.getShort("PickupDelay");
      }

      if(compoundTag.contains("Owner", 10)) {
         this.owner = NbtUtils.loadUUIDTag(compoundTag.getCompound("Owner"));
      }

      if(compoundTag.contains("Thrower", 10)) {
         this.thrower = NbtUtils.loadUUIDTag(compoundTag.getCompound("Thrower"));
      }

      CompoundTag compoundTag = compoundTag.getCompound("Item");
      this.setItem(ItemStack.of(compoundTag));
      if(this.getItem().isEmpty()) {
         this.remove();
      }

   }

   public void playerTouch(Player player) {
      if(!this.level.isClientSide) {
         ItemStack var2 = this.getItem();
         Item var3 = var2.getItem();
         int var4 = var2.getCount();
         if(this.pickupDelay == 0 && (this.owner == null || 6000 - this.age <= 200 || this.owner.equals(player.getUUID())) && player.inventory.add(var2)) {
            player.take(this, var4);
            if(var2.isEmpty()) {
               this.remove();
               var2.setCount(var4);
            }

            player.awardStat(Stats.ITEM_PICKED_UP.get(var3), var4);
         }

      }
   }

   public Component getName() {
      Component component = this.getCustomName();
      return (Component)(component != null?component:new TranslatableComponent(this.getItem().getDescriptionId(), new Object[0]));
   }

   public boolean isAttackable() {
      return false;
   }

   @Nullable
   public Entity changeDimension(DimensionType dimensionType) {
      Entity entity = super.changeDimension(dimensionType);
      if(!this.level.isClientSide && entity instanceof ItemEntity) {
         ((ItemEntity)entity).mergeWithNeighbours();
      }

      return entity;
   }

   public ItemStack getItem() {
      return (ItemStack)this.getEntityData().get(DATA_ITEM);
   }

   public void setItem(ItemStack item) {
      this.getEntityData().set(DATA_ITEM, item);
   }

   @Nullable
   public UUID getOwner() {
      return this.owner;
   }

   public void setOwner(@Nullable UUID owner) {
      this.owner = owner;
   }

   @Nullable
   public UUID getThrower() {
      return this.thrower;
   }

   public void setThrower(@Nullable UUID thrower) {
      this.thrower = thrower;
   }

   public int getAge() {
      return this.age;
   }

   public void setDefaultPickUpDelay() {
      this.pickupDelay = 10;
   }

   public void setNoPickUpDelay() {
      this.pickupDelay = 0;
   }

   public void setNeverPickUp() {
      this.pickupDelay = 32767;
   }

   public void setPickUpDelay(int pickUpDelay) {
      this.pickupDelay = pickUpDelay;
   }

   public boolean hasPickUpDelay() {
      return this.pickupDelay > 0;
   }

   public void setExtendedLifetime() {
      this.age = -6000;
   }

   public void makeFakeItem() {
      this.setNeverPickUp();
      this.age = 5999;
   }

   public Packet getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }
}
