package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SimpleContainer implements Container, StackedContentsCompatible {
   private final int size;
   private final NonNullList items;
   private List listeners;

   public SimpleContainer(int size) {
      this.size = size;
      this.items = NonNullList.withSize(size, ItemStack.EMPTY);
   }

   public SimpleContainer(ItemStack... itemStacks) {
      this.size = itemStacks.length;
      this.items = NonNullList.of(ItemStack.EMPTY, itemStacks);
   }

   public void addListener(ContainerListener containerListener) {
      if(this.listeners == null) {
         this.listeners = Lists.newArrayList();
      }

      this.listeners.add(containerListener);
   }

   public void removeListener(ContainerListener containerListener) {
      this.listeners.remove(containerListener);
   }

   public ItemStack getItem(int i) {
      return i >= 0 && i < this.items.size()?(ItemStack)this.items.get(i):ItemStack.EMPTY;
   }

   public ItemStack removeItem(int var1, int var2) {
      ItemStack itemStack = ContainerHelper.removeItem(this.items, var1, var2);
      if(!itemStack.isEmpty()) {
         this.setChanged();
      }

      return itemStack;
   }

   public ItemStack removeItemType(Item item, int var2) {
      ItemStack itemStack = new ItemStack(item, 0);

      for(int var4 = this.size - 1; var4 >= 0; --var4) {
         ItemStack var5 = this.getItem(var4);
         if(var5.getItem().equals(item)) {
            int var6 = var2 - itemStack.getCount();
            ItemStack var7 = var5.split(var6);
            itemStack.grow(var7.getCount());
            if(itemStack.getCount() == var2) {
               break;
            }
         }
      }

      if(!itemStack.isEmpty()) {
         this.setChanged();
      }

      return itemStack;
   }

   public ItemStack addItem(ItemStack itemStack) {
      ItemStack var2 = itemStack.copy();
      this.moveItemToOccupiedSlotsWithSameType(var2);
      if(var2.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.moveItemToEmptySlots(var2);
         return var2.isEmpty()?ItemStack.EMPTY:var2;
      }
   }

   public ItemStack removeItemNoUpdate(int i) {
      ItemStack itemStack = (ItemStack)this.items.get(i);
      if(itemStack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.items.set(i, ItemStack.EMPTY);
         return itemStack;
      }
   }

   public void setItem(int var1, ItemStack itemStack) {
      this.items.set(var1, itemStack);
      if(!itemStack.isEmpty() && itemStack.getCount() > this.getMaxStackSize()) {
         itemStack.setCount(this.getMaxStackSize());
      }

      this.setChanged();
   }

   public int getContainerSize() {
      return this.size;
   }

   public boolean isEmpty() {
      for(ItemStack var2 : this.items) {
         if(!var2.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public void setChanged() {
      if(this.listeners != null) {
         for(ContainerListener var2 : this.listeners) {
            var2.containerChanged(this);
         }
      }

   }

   public boolean stillValid(Player player) {
      return true;
   }

   public void clearContent() {
      this.items.clear();
      this.setChanged();
   }

   public void fillStackedContents(StackedContents stackedContents) {
      for(ItemStack var3 : this.items) {
         stackedContents.accountStack(var3);
      }

   }

   public String toString() {
      return ((List)this.items.stream().filter((itemStack) -> {
         return !itemStack.isEmpty();
      }).collect(Collectors.toList())).toString();
   }

   private void moveItemToEmptySlots(ItemStack itemStack) {
      for(int var2 = 0; var2 < this.size; ++var2) {
         ItemStack var3 = this.getItem(var2);
         if(var3.isEmpty()) {
            this.setItem(var2, itemStack.copy());
            itemStack.setCount(0);
            return;
         }
      }

   }

   private void moveItemToOccupiedSlotsWithSameType(ItemStack itemStack) {
      for(int var2 = 0; var2 < this.size; ++var2) {
         ItemStack var3 = this.getItem(var2);
         if(ItemStack.isSame(var3, itemStack)) {
            this.moveItemsBetweenStacks(itemStack, var3);
            if(itemStack.isEmpty()) {
               return;
            }
         }
      }

   }

   private void moveItemsBetweenStacks(ItemStack var1, ItemStack var2) {
      int var3 = Math.min(this.getMaxStackSize(), var2.getMaxStackSize());
      int var4 = Math.min(var1.getCount(), var3 - var2.getCount());
      if(var4 > 0) {
         var2.grow(var4);
         var1.shrink(var4);
         this.setChanged();
      }

   }
}
