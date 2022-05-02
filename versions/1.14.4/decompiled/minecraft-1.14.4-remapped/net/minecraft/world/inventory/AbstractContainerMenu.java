package net.minecraft.world.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractContainerMenu {
   private final NonNullList lastSlots = NonNullList.create();
   public final List slots = Lists.newArrayList();
   private final List dataSlots = Lists.newArrayList();
   @Nullable
   private final MenuType menuType;
   public final int containerId;
   private short changeUid;
   private int quickcraftType = -1;
   private int quickcraftStatus;
   private final Set quickcraftSlots = Sets.newHashSet();
   private final List containerListeners = Lists.newArrayList();
   private final Set unSynchedPlayers = Sets.newHashSet();

   protected AbstractContainerMenu(@Nullable MenuType menuType, int containerId) {
      this.menuType = menuType;
      this.containerId = containerId;
   }

   protected static boolean stillValid(ContainerLevelAccess containerLevelAccess, Player player, Block block) {
      return ((Boolean)containerLevelAccess.evaluate((level, blockPos) -> {
         return level.getBlockState(blockPos).getBlock() != block?Boolean.valueOf(false):Boolean.valueOf(player.distanceToSqr((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D) <= 64.0D);
      }, Boolean.valueOf(true))).booleanValue();
   }

   public MenuType getType() {
      if(this.menuType == null) {
         throw new UnsupportedOperationException("Unable to construct this menu by type");
      } else {
         return this.menuType;
      }
   }

   protected static void checkContainerSize(Container container, int var1) {
      int var2 = container.getContainerSize();
      if(var2 < var1) {
         throw new IllegalArgumentException("Container size " + var2 + " is smaller than expected " + var1);
      }
   }

   protected static void checkContainerDataCount(ContainerData containerData, int var1) {
      int var2 = containerData.getCount();
      if(var2 < var1) {
         throw new IllegalArgumentException("Container data count " + var2 + " is smaller than expected " + var1);
      }
   }

   protected Slot addSlot(Slot slot) {
      slot.index = this.slots.size();
      this.slots.add(slot);
      this.lastSlots.add(ItemStack.EMPTY);
      return slot;
   }

   protected DataSlot addDataSlot(DataSlot dataSlot) {
      this.dataSlots.add(dataSlot);
      return dataSlot;
   }

   protected void addDataSlots(ContainerData containerData) {
      for(int var2 = 0; var2 < containerData.getCount(); ++var2) {
         this.addDataSlot(DataSlot.forContainer(containerData, var2));
      }

   }

   public void addSlotListener(ContainerListener containerListener) {
      if(!this.containerListeners.contains(containerListener)) {
         this.containerListeners.add(containerListener);
         containerListener.refreshContainer(this, this.getItems());
         this.broadcastChanges();
      }
   }

   public void removeSlotListener(ContainerListener containerListener) {
      this.containerListeners.remove(containerListener);
   }

   public NonNullList getItems() {
      NonNullList<ItemStack> nonNullList = NonNullList.create();

      for(int var2 = 0; var2 < this.slots.size(); ++var2) {
         nonNullList.add(((Slot)this.slots.get(var2)).getItem());
      }

      return nonNullList;
   }

   public void broadcastChanges() {
      for(int var1 = 0; var1 < this.slots.size(); ++var1) {
         ItemStack var2 = ((Slot)this.slots.get(var1)).getItem();
         ItemStack var3 = (ItemStack)this.lastSlots.get(var1);
         if(!ItemStack.matches(var3, var2)) {
            var3 = var2.isEmpty()?ItemStack.EMPTY:var2.copy();
            this.lastSlots.set(var1, var3);

            for(ContainerListener var5 : this.containerListeners) {
               var5.slotChanged(this, var1, var3);
            }
         }
      }

      for(int var1 = 0; var1 < this.dataSlots.size(); ++var1) {
         DataSlot var2 = (DataSlot)this.dataSlots.get(var1);
         if(var2.checkAndClearUpdateFlag()) {
            for(ContainerListener var4 : this.containerListeners) {
               var4.setContainerData(this, var1, var2.get());
            }
         }
      }

   }

   public boolean clickMenuButton(Player player, int var2) {
      return false;
   }

   public Slot getSlot(int i) {
      return (Slot)this.slots.get(i);
   }

   public ItemStack quickMoveStack(Player player, int var2) {
      Slot var3 = (Slot)this.slots.get(var2);
      return var3 != null?var3.getItem():ItemStack.EMPTY;
   }

   public ItemStack clicked(int var1, int var2, ClickType clickType, Player player) {
      ItemStack itemStack = ItemStack.EMPTY;
      Inventory var6 = player.inventory;
      if(clickType == ClickType.QUICK_CRAFT) {
         int var7 = this.quickcraftStatus;
         this.quickcraftStatus = getQuickcraftHeader(var2);
         if((var7 != 1 || this.quickcraftStatus != 2) && var7 != this.quickcraftStatus) {
            this.resetQuickCraft();
         } else if(var6.getCarried().isEmpty()) {
            this.resetQuickCraft();
         } else if(this.quickcraftStatus == 0) {
            this.quickcraftType = getQuickcraftType(var2);
            if(isValidQuickcraftType(this.quickcraftType, player)) {
               this.quickcraftStatus = 1;
               this.quickcraftSlots.clear();
            } else {
               this.resetQuickCraft();
            }
         } else if(this.quickcraftStatus == 1) {
            Slot var8 = (Slot)this.slots.get(var1);
            ItemStack var9 = var6.getCarried();
            if(var8 != null && canItemQuickReplace(var8, var9, true) && var8.mayPlace(var9) && (this.quickcraftType == 2 || var9.getCount() > this.quickcraftSlots.size()) && this.canDragTo(var8)) {
               this.quickcraftSlots.add(var8);
            }
         } else if(this.quickcraftStatus == 2) {
            if(!this.quickcraftSlots.isEmpty()) {
               ItemStack var8 = var6.getCarried().copy();
               int var9 = var6.getCarried().getCount();

               for(Slot var11 : this.quickcraftSlots) {
                  ItemStack var12 = var6.getCarried();
                  if(var11 != null && canItemQuickReplace(var11, var12, true) && var11.mayPlace(var12) && (this.quickcraftType == 2 || var12.getCount() >= this.quickcraftSlots.size()) && this.canDragTo(var11)) {
                     ItemStack var13 = var8.copy();
                     int var14 = var11.hasItem()?var11.getItem().getCount():0;
                     getQuickCraftSlotCount(this.quickcraftSlots, this.quickcraftType, var13, var14);
                     int var15 = Math.min(var13.getMaxStackSize(), var11.getMaxStackSize(var13));
                     if(var13.getCount() > var15) {
                        var13.setCount(var15);
                     }

                     var9 -= var13.getCount() - var14;
                     var11.set(var13);
                  }
               }

               var8.setCount(var9);
               var6.setCarried(var8);
            }

            this.resetQuickCraft();
         } else {
            this.resetQuickCraft();
         }
      } else if(this.quickcraftStatus != 0) {
         this.resetQuickCraft();
      } else if((clickType == ClickType.PICKUP || clickType == ClickType.QUICK_MOVE) && (var2 == 0 || var2 == 1)) {
         if(var1 == -999) {
            if(!var6.getCarried().isEmpty()) {
               if(var2 == 0) {
                  player.drop(var6.getCarried(), true);
                  var6.setCarried(ItemStack.EMPTY);
               }

               if(var2 == 1) {
                  player.drop(var6.getCarried().split(1), true);
               }
            }
         } else if(clickType == ClickType.QUICK_MOVE) {
            if(var1 < 0) {
               return ItemStack.EMPTY;
            }

            Slot var7 = (Slot)this.slots.get(var1);
            if(var7 == null || !var7.mayPickup(player)) {
               return ItemStack.EMPTY;
            }

            for(ItemStack var8 = this.quickMoveStack(player, var1); !var8.isEmpty() && ItemStack.isSame(var7.getItem(), var8); var8 = this.quickMoveStack(player, var1)) {
               itemStack = var8.copy();
            }
         } else {
            if(var1 < 0) {
               return ItemStack.EMPTY;
            }

            Slot var7 = (Slot)this.slots.get(var1);
            if(var7 != null) {
               ItemStack var8 = var7.getItem();
               ItemStack var9 = var6.getCarried();
               if(!var8.isEmpty()) {
                  itemStack = var8.copy();
               }

               if(var8.isEmpty()) {
                  if(!var9.isEmpty() && var7.mayPlace(var9)) {
                     int var10 = var2 == 0?var9.getCount():1;
                     if(var10 > var7.getMaxStackSize(var9)) {
                        var10 = var7.getMaxStackSize(var9);
                     }

                     var7.set(var9.split(var10));
                  }
               } else if(var7.mayPickup(player)) {
                  if(var9.isEmpty()) {
                     if(var8.isEmpty()) {
                        var7.set(ItemStack.EMPTY);
                        var6.setCarried(ItemStack.EMPTY);
                     } else {
                        int var10 = var2 == 0?var8.getCount():(var8.getCount() + 1) / 2;
                        var6.setCarried(var7.remove(var10));
                        if(var8.isEmpty()) {
                           var7.set(ItemStack.EMPTY);
                        }

                        var7.onTake(player, var6.getCarried());
                     }
                  } else if(var7.mayPlace(var9)) {
                     if(consideredTheSameItem(var8, var9)) {
                        int var10 = var2 == 0?var9.getCount():1;
                        if(var10 > var7.getMaxStackSize(var9) - var8.getCount()) {
                           var10 = var7.getMaxStackSize(var9) - var8.getCount();
                        }

                        if(var10 > var9.getMaxStackSize() - var8.getCount()) {
                           var10 = var9.getMaxStackSize() - var8.getCount();
                        }

                        var9.shrink(var10);
                        var8.grow(var10);
                     } else if(var9.getCount() <= var7.getMaxStackSize(var9)) {
                        var7.set(var9);
                        var6.setCarried(var8);
                     }
                  } else if(var9.getMaxStackSize() > 1 && consideredTheSameItem(var8, var9) && !var8.isEmpty()) {
                     int var10 = var8.getCount();
                     if(var10 + var9.getCount() <= var9.getMaxStackSize()) {
                        var9.grow(var10);
                        var8 = var7.remove(var10);
                        if(var8.isEmpty()) {
                           var7.set(ItemStack.EMPTY);
                        }

                        var7.onTake(player, var6.getCarried());
                     }
                  }
               }

               var7.setChanged();
            }
         }
      } else if(clickType == ClickType.SWAP && var2 >= 0 && var2 < 9) {
         Slot var7 = (Slot)this.slots.get(var1);
         ItemStack var8 = var6.getItem(var2);
         ItemStack var9 = var7.getItem();
         if(!var8.isEmpty() || !var9.isEmpty()) {
            if(var8.isEmpty()) {
               if(var7.mayPickup(player)) {
                  var6.setItem(var2, var9);
                  var7.onSwapCraft(var9.getCount());
                  var7.set(ItemStack.EMPTY);
                  var7.onTake(player, var9);
               }
            } else if(var9.isEmpty()) {
               if(var7.mayPlace(var8)) {
                  int var10 = var7.getMaxStackSize(var8);
                  if(var8.getCount() > var10) {
                     var7.set(var8.split(var10));
                  } else {
                     var7.set(var8);
                     var6.setItem(var2, ItemStack.EMPTY);
                  }
               }
            } else if(var7.mayPickup(player) && var7.mayPlace(var8)) {
               int var10 = var7.getMaxStackSize(var8);
               if(var8.getCount() > var10) {
                  var7.set(var8.split(var10));
                  var7.onTake(player, var9);
                  if(!var6.add(var9)) {
                     player.drop(var9, true);
                  }
               } else {
                  var7.set(var8);
                  var6.setItem(var2, var9);
                  var7.onTake(player, var9);
               }
            }
         }
      } else if(clickType == ClickType.CLONE && player.abilities.instabuild && var6.getCarried().isEmpty() && var1 >= 0) {
         Slot var7 = (Slot)this.slots.get(var1);
         if(var7 != null && var7.hasItem()) {
            ItemStack var8 = var7.getItem().copy();
            var8.setCount(var8.getMaxStackSize());
            var6.setCarried(var8);
         }
      } else if(clickType == ClickType.THROW && var6.getCarried().isEmpty() && var1 >= 0) {
         Slot var7 = (Slot)this.slots.get(var1);
         if(var7 != null && var7.hasItem() && var7.mayPickup(player)) {
            ItemStack var8 = var7.remove(var2 == 0?1:var7.getItem().getCount());
            var7.onTake(player, var8);
            player.drop(var8, true);
         }
      } else if(clickType == ClickType.PICKUP_ALL && var1 >= 0) {
         Slot var7 = (Slot)this.slots.get(var1);
         ItemStack var8 = var6.getCarried();
         if(!var8.isEmpty() && (var7 == null || !var7.hasItem() || !var7.mayPickup(player))) {
            int var9 = var2 == 0?0:this.slots.size() - 1;
            int var10 = var2 == 0?1:-1;

            for(int var11 = 0; var11 < 2; ++var11) {
               for(int var12 = var9; var12 >= 0 && var12 < this.slots.size() && var8.getCount() < var8.getMaxStackSize(); var12 += var10) {
                  Slot var13 = (Slot)this.slots.get(var12);
                  if(var13.hasItem() && canItemQuickReplace(var13, var8, true) && var13.mayPickup(player) && this.canTakeItemForPickAll(var8, var13)) {
                     ItemStack var14 = var13.getItem();
                     if(var11 != 0 || var14.getCount() != var14.getMaxStackSize()) {
                        int var15 = Math.min(var8.getMaxStackSize() - var8.getCount(), var14.getCount());
                        ItemStack var16 = var13.remove(var15);
                        var8.grow(var15);
                        if(var16.isEmpty()) {
                           var13.set(ItemStack.EMPTY);
                        }

                        var13.onTake(player, var16);
                     }
                  }
               }
            }
         }

         this.broadcastChanges();
      }

      return itemStack;
   }

   public static boolean consideredTheSameItem(ItemStack var0, ItemStack var1) {
      return var0.getItem() == var1.getItem() && ItemStack.tagMatches(var0, var1);
   }

   public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
      return true;
   }

   public void removed(Player player) {
      Inventory var2 = player.inventory;
      if(!var2.getCarried().isEmpty()) {
         player.drop(var2.getCarried(), false);
         var2.setCarried(ItemStack.EMPTY);
      }

   }

   protected void clearContainer(Player player, Level level, Container container) {
      if(!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer)player).hasDisconnected()) {
         for(int var4 = 0; var4 < container.getContainerSize(); ++var4) {
            player.drop(container.removeItemNoUpdate(var4), false);
         }

      } else {
         for(int var4 = 0; var4 < container.getContainerSize(); ++var4) {
            player.inventory.placeItemBackInInventory(level, container.removeItemNoUpdate(var4));
         }

      }
   }

   public void slotsChanged(Container container) {
      this.broadcastChanges();
   }

   public void setItem(int var1, ItemStack itemStack) {
      this.getSlot(var1).set(itemStack);
   }

   public void setAll(List all) {
      for(int var2 = 0; var2 < all.size(); ++var2) {
         this.getSlot(var2).set((ItemStack)all.get(var2));
      }

   }

   public void setData(int var1, int var2) {
      ((DataSlot)this.dataSlots.get(var1)).set(var2);
   }

   public short backup(Inventory inventory) {
      ++this.changeUid;
      return this.changeUid;
   }

   public boolean isSynched(Player player) {
      return !this.unSynchedPlayers.contains(player);
   }

   public void setSynched(Player player, boolean var2) {
      if(var2) {
         this.unSynchedPlayers.remove(player);
      } else {
         this.unSynchedPlayers.add(player);
      }

   }

   public abstract boolean stillValid(Player var1);

   protected boolean moveItemStackTo(ItemStack itemStack, int var2, int var3, boolean var4) {
      boolean var5 = false;
      int var6 = var2;
      if(var4) {
         var6 = var3 - 1;
      }

      if(itemStack.isStackable()) {
         while(!itemStack.isEmpty()) {
            if(var4) {
               if(var6 < var2) {
                  break;
               }
            } else if(var6 >= var3) {
               break;
            }

            Slot var7 = (Slot)this.slots.get(var6);
            ItemStack var8 = var7.getItem();
            if(!var8.isEmpty() && consideredTheSameItem(itemStack, var8)) {
               int var9 = var8.getCount() + itemStack.getCount();
               if(var9 <= itemStack.getMaxStackSize()) {
                  itemStack.setCount(0);
                  var8.setCount(var9);
                  var7.setChanged();
                  var5 = true;
               } else if(var8.getCount() < itemStack.getMaxStackSize()) {
                  itemStack.shrink(itemStack.getMaxStackSize() - var8.getCount());
                  var8.setCount(itemStack.getMaxStackSize());
                  var7.setChanged();
                  var5 = true;
               }
            }

            if(var4) {
               --var6;
            } else {
               ++var6;
            }
         }
      }

      if(!itemStack.isEmpty()) {
         if(var4) {
            var6 = var3 - 1;
         } else {
            var6 = var2;
         }

         while(true) {
            if(var4) {
               if(var6 < var2) {
                  break;
               }
            } else if(var6 >= var3) {
               break;
            }

            Slot var7 = (Slot)this.slots.get(var6);
            ItemStack var8 = var7.getItem();
            if(var8.isEmpty() && var7.mayPlace(itemStack)) {
               if(itemStack.getCount() > var7.getMaxStackSize()) {
                  var7.set(itemStack.split(var7.getMaxStackSize()));
               } else {
                  var7.set(itemStack.split(itemStack.getCount()));
               }

               var7.setChanged();
               var5 = true;
               break;
            }

            if(var4) {
               --var6;
            } else {
               ++var6;
            }
         }
      }

      return var5;
   }

   public static int getQuickcraftType(int i) {
      return i >> 2 & 3;
   }

   public static int getQuickcraftHeader(int i) {
      return i & 3;
   }

   public static int getQuickcraftMask(int var0, int var1) {
      return var0 & 3 | (var1 & 3) << 2;
   }

   public static boolean isValidQuickcraftType(int var0, Player player) {
      return var0 == 0?true:(var0 == 1?true:var0 == 2 && player.abilities.instabuild);
   }

   protected void resetQuickCraft() {
      this.quickcraftStatus = 0;
      this.quickcraftSlots.clear();
   }

   public static boolean canItemQuickReplace(@Nullable Slot slot, ItemStack itemStack, boolean var2) {
      boolean var3 = slot == null || !slot.hasItem();
      return !var3 && itemStack.sameItem(slot.getItem()) && ItemStack.tagMatches(slot.getItem(), itemStack)?slot.getItem().getCount() + (var2?0:itemStack.getCount()) <= itemStack.getMaxStackSize():var3;
   }

   public static void getQuickCraftSlotCount(Set set, int var1, ItemStack itemStack, int var3) {
      switch(var1) {
      case 0:
         itemStack.setCount(Mth.floor((float)itemStack.getCount() / (float)set.size()));
         break;
      case 1:
         itemStack.setCount(1);
         break;
      case 2:
         itemStack.setCount(itemStack.getItem().getMaxStackSize());
      }

      itemStack.grow(var3);
   }

   public boolean canDragTo(Slot slot) {
      return true;
   }

   public static int getRedstoneSignalFromBlockEntity(@Nullable BlockEntity blockEntity) {
      return blockEntity instanceof Container?getRedstoneSignalFromContainer((Container)blockEntity):0;
   }

   public static int getRedstoneSignalFromContainer(@Nullable Container container) {
      if(container == null) {
         return 0;
      } else {
         int var1 = 0;
         float var2 = 0.0F;

         for(int var3 = 0; var3 < container.getContainerSize(); ++var3) {
            ItemStack var4 = container.getItem(var3);
            if(!var4.isEmpty()) {
               var2 += (float)var4.getCount() / (float)Math.min(container.getMaxStackSize(), var4.getMaxStackSize());
               ++var1;
            }
         }

         var2 = var2 / (float)container.getContainerSize();
         return Mth.floor(var2 * 14.0F) + (var1 > 0?1:0);
      }
   }
}
