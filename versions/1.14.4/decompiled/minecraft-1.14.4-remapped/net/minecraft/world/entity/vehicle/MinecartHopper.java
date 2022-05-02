package net.minecraft.world.entity.vehicle;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MinecartHopper extends AbstractMinecartContainer implements Hopper {
   private boolean enabled = true;
   private int cooldownTime = -1;
   private final BlockPos lastPosition = BlockPos.ZERO;

   public MinecartHopper(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public MinecartHopper(Level level, double var2, double var4, double var6) {
      super(EntityType.HOPPER_MINECART, var2, var4, var6, level);
   }

   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.HOPPER;
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.HOPPER.defaultBlockState();
   }

   public int getDefaultDisplayOffset() {
      return 1;
   }

   public int getContainerSize() {
      return 5;
   }

   public void activateMinecart(int var1, int var2, int var3, boolean var4) {
      boolean var5 = !var4;
      if(var5 != this.isEnabled()) {
         this.setEnabled(var5);
      }

   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public Level getLevel() {
      return this.level;
   }

   public double getLevelX() {
      return this.x;
   }

   public double getLevelY() {
      return this.y + 0.5D;
   }

   public double getLevelZ() {
      return this.z;
   }

   public void tick() {
      super.tick();
      if(!this.level.isClientSide && this.isAlive() && this.isEnabled()) {
         BlockPos var1 = new BlockPos(this);
         if(var1.equals(this.lastPosition)) {
            --this.cooldownTime;
         } else {
            this.setCooldown(0);
         }

         if(!this.isOnCooldown()) {
            this.setCooldown(0);
            if(this.suckInItems()) {
               this.setCooldown(4);
               this.setChanged();
            }
         }
      }

   }

   public boolean suckInItems() {
      if(HopperBlockEntity.suckInItems(this)) {
         return true;
      } else {
         List<ItemEntity> var1 = this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.25D, 0.0D, 0.25D), EntitySelector.ENTITY_STILL_ALIVE);
         if(!var1.isEmpty()) {
            HopperBlockEntity.addItem(this, (ItemEntity)var1.get(0));
         }

         return false;
      }
   }

   public void destroy(DamageSource damageSource) {
      super.destroy(damageSource);
      if(this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         this.spawnAtLocation(Blocks.HOPPER);
      }

   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("TransferCooldown", this.cooldownTime);
      compoundTag.putBoolean("Enabled", this.enabled);
   }

   protected void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.cooldownTime = compoundTag.getInt("TransferCooldown");
      this.enabled = compoundTag.contains("Enabled")?compoundTag.getBoolean("Enabled"):true;
   }

   public void setCooldown(int cooldown) {
      this.cooldownTime = cooldown;
   }

   public boolean isOnCooldown() {
      return this.cooldownTime > 0;
   }

   public AbstractContainerMenu createMenu(int var1, Inventory inventory) {
      return new HopperMenu(var1, inventory, this);
   }
}
