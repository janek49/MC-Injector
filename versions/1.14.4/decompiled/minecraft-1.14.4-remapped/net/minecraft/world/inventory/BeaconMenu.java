package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class BeaconMenu extends AbstractContainerMenu {
   private final Container beacon;
   private final BeaconMenu.PaymentSlot paymentSlot;
   private final ContainerLevelAccess access;
   private final ContainerData beaconData;

   public BeaconMenu(int var1, Container container) {
      this(var1, container, new SimpleContainerData(3), ContainerLevelAccess.NULL);
   }

   public BeaconMenu(int var1, Container container, ContainerData beaconData, ContainerLevelAccess access) {
      super(MenuType.BEACON, var1);
      this.beacon = new SimpleContainer(1) {
         public boolean canPlaceItem(int var1, ItemStack itemStack) {
            return itemStack.getItem() == Items.EMERALD || itemStack.getItem() == Items.DIAMOND || itemStack.getItem() == Items.GOLD_INGOT || itemStack.getItem() == Items.IRON_INGOT;
         }

         public int getMaxStackSize() {
            return 1;
         }
      };
      checkContainerDataCount(beaconData, 3);
      this.beaconData = beaconData;
      this.access = access;
      this.paymentSlot = new BeaconMenu.PaymentSlot(this.beacon, 0, 136, 110);
      this.addSlot(this.paymentSlot);
      this.addDataSlots(beaconData);
      int var5 = 36;
      int var6 = 137;

      for(int var7 = 0; var7 < 3; ++var7) {
         for(int var8 = 0; var8 < 9; ++var8) {
            this.addSlot(new Slot(container, var8 + var7 * 9 + 9, 36 + var8 * 18, 137 + var7 * 18));
         }
      }

      for(int var7 = 0; var7 < 9; ++var7) {
         this.addSlot(new Slot(container, var7, 36 + var7 * 18, 195));
      }

   }

   public void removed(Player player) {
      super.removed(player);
      if(!player.level.isClientSide) {
         ItemStack var2 = this.paymentSlot.remove(this.paymentSlot.getMaxStackSize());
         if(!var2.isEmpty()) {
            player.drop(var2, false);
         }

      }
   }

   public boolean stillValid(Player player) {
      return stillValid(this.access, player, Blocks.BEACON);
   }

   public void setData(int var1, int var2) {
      super.setData(var1, var2);
      this.broadcastChanges();
   }

   public ItemStack quickMoveStack(Player player, int var2) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if(var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         itemStack = var5.copy();
         if(var2 == 0) {
            if(!this.moveItemStackTo(var5, 1, 37, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, itemStack);
         } else if(!this.paymentSlot.hasItem() && this.paymentSlot.mayPlace(var5) && var5.getCount() == 1) {
            if(!this.moveItemStackTo(var5, 0, 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if(var2 >= 1 && var2 < 28) {
            if(!this.moveItemStackTo(var5, 28, 37, false)) {
               return ItemStack.EMPTY;
            }
         } else if(var2 >= 28 && var2 < 37) {
            if(!this.moveItemStackTo(var5, 1, 28, false)) {
               return ItemStack.EMPTY;
            }
         } else if(!this.moveItemStackTo(var5, 1, 37, false)) {
            return ItemStack.EMPTY;
         }

         if(var5.isEmpty()) {
            var4.set(ItemStack.EMPTY);
         } else {
            var4.setChanged();
         }

         if(var5.getCount() == itemStack.getCount()) {
            return ItemStack.EMPTY;
         }

         var4.onTake(player, var5);
      }

      return itemStack;
   }

   public int getLevels() {
      return this.beaconData.get(0);
   }

   @Nullable
   public MobEffect getPrimaryEffect() {
      return MobEffect.byId(this.beaconData.get(1));
   }

   @Nullable
   public MobEffect getSecondaryEffect() {
      return MobEffect.byId(this.beaconData.get(2));
   }

   public void updateEffects(int var1, int var2) {
      if(this.paymentSlot.hasItem()) {
         this.beaconData.set(1, var1);
         this.beaconData.set(2, var2);
         this.paymentSlot.remove(1);
      }

   }

   public boolean hasPayment() {
      return !this.beacon.getItem(0).isEmpty();
   }

   class PaymentSlot extends Slot {
      public PaymentSlot(Container container, int var3, int var4, int var5) {
         super(container, var3, var4, var5);
      }

      public boolean mayPlace(ItemStack itemStack) {
         Item var2 = itemStack.getItem();
         return var2 == Items.EMERALD || var2 == Items.DIAMOND || var2 == Items.GOLD_INGOT || var2 == Items.IRON_INGOT;
      }

      public int getMaxStackSize() {
         return 1;
      }
   }
}
