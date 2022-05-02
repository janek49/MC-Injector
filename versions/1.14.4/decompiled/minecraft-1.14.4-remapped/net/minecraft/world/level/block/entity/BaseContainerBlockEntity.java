package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public abstract class BaseContainerBlockEntity extends BlockEntity implements Container, MenuProvider, Nameable {
   private LockCode lockKey = LockCode.NO_LOCK;
   private Component name;

   protected BaseContainerBlockEntity(BlockEntityType blockEntityType) {
      super(blockEntityType);
   }

   public void load(CompoundTag compoundTag) {
      super.load(compoundTag);
      this.lockKey = LockCode.fromTag(compoundTag);
      if(compoundTag.contains("CustomName", 8)) {
         this.name = Component.Serializer.fromJson(compoundTag.getString("CustomName"));
      }

   }

   public CompoundTag save(CompoundTag compoundTag) {
      super.save(compoundTag);
      this.lockKey.addToTag(compoundTag);
      if(this.name != null) {
         compoundTag.putString("CustomName", Component.Serializer.toJson(this.name));
      }

      return compoundTag;
   }

   public void setCustomName(Component customName) {
      this.name = customName;
   }

   public Component getName() {
      return this.name != null?this.name:this.getDefaultName();
   }

   public Component getDisplayName() {
      return this.getName();
   }

   @Nullable
   public Component getCustomName() {
      return this.name;
   }

   protected abstract Component getDefaultName();

   public boolean canOpen(Player player) {
      return canUnlock(player, this.lockKey, this.getDisplayName());
   }

   public static boolean canUnlock(Player player, LockCode lockCode, Component component) {
      if(!player.isSpectator() && !lockCode.unlocksWith(player.getMainHandItem())) {
         player.displayClientMessage(new TranslatableComponent("container.isLocked", new Object[]{component}), true);
         player.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
         return false;
      } else {
         return true;
      }
   }

   @Nullable
   public AbstractContainerMenu createMenu(int var1, Inventory inventory, Player player) {
      return this.canOpen(player)?this.createMenu(var1, inventory):null;
   }

   protected abstract AbstractContainerMenu createMenu(int var1, Inventory var2);
}
