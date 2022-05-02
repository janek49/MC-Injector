package net.minecraft.world.inventory;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class CartographyMenu extends AbstractContainerMenu {
   private final ContainerLevelAccess access;
   private boolean quickMoved;
   public final Container container;
   private final ResultContainer resultContainer;

   public CartographyMenu(int var1, Inventory inventory) {
      this(var1, inventory, ContainerLevelAccess.NULL);
   }

   public CartographyMenu(int var1, Inventory inventory, final ContainerLevelAccess access) {
      super(MenuType.CARTOGRAPHY, var1);
      this.container = new SimpleContainer(2) {
         public void setChanged() {
            CartographyMenu.this.slotsChanged(this);
            super.setChanged();
         }
      };
      this.resultContainer = new ResultContainer() {
         public void setChanged() {
            CartographyMenu.this.slotsChanged(this);
            super.setChanged();
         }
      };
      this.access = access;
      this.addSlot(new Slot(this.container, 0, 15, 15) {
         public boolean mayPlace(ItemStack itemStack) {
            return itemStack.getItem() == Items.FILLED_MAP;
         }
      });
      this.addSlot(new Slot(this.container, 1, 15, 52) {
         public boolean mayPlace(ItemStack itemStack) {
            Item var2 = itemStack.getItem();
            return var2 == Items.PAPER || var2 == Items.MAP || var2 == Items.GLASS_PANE;
         }
      });
      this.addSlot(new Slot(this.resultContainer, 2, 145, 39) {
         public boolean mayPlace(ItemStack itemStack) {
            return false;
         }

         public ItemStack remove(int i) {
            ItemStack itemStack = super.remove(i);
            ItemStack var3 = (ItemStack)access.evaluate((level, blockPos) -> {
               if(!CartographyMenu.this.quickMoved && CartographyMenu.this.container.getItem(1).getItem() == Items.GLASS_PANE) {
                  ItemStack var4 = MapItem.lockMap(level, CartographyMenu.this.container.getItem(0));
                  if(var4 != null) {
                     var4.setCount(1);
                     return var4;
                  }
               }

               return itemStack;
            }).orElse(itemStack);
            CartographyMenu.this.container.removeItem(0, 1);
            CartographyMenu.this.container.removeItem(1, 1);
            return var3;
         }

         protected void onQuickCraft(ItemStack itemStack, int var2) {
            this.remove(var2);
            super.onQuickCraft(itemStack, var2);
         }

         public ItemStack onTake(Player player, ItemStack var2) {
            var2.getItem().onCraftedBy(var2, player.level, player);
            access.execute((level, blockPos) -> {
               level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
            });
            return super.onTake(player, var2);
         }
      });

      for(int var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 9; ++var5) {
            this.addSlot(new Slot(inventory, var5 + var4 * 9 + 9, 8 + var5 * 18, 84 + var4 * 18));
         }
      }

      for(int var4 = 0; var4 < 9; ++var4) {
         this.addSlot(new Slot(inventory, var4, 8 + var4 * 18, 142));
      }

   }

   public boolean stillValid(Player player) {
      return stillValid(this.access, player, Blocks.CARTOGRAPHY_TABLE);
   }

   public void slotsChanged(Container container) {
      ItemStack var2 = this.container.getItem(0);
      ItemStack var3 = this.container.getItem(1);
      ItemStack var4 = this.resultContainer.getItem(2);
      if(var4.isEmpty() || !var2.isEmpty() && !var3.isEmpty()) {
         if(!var2.isEmpty() && !var3.isEmpty()) {
            this.setupResultSlot(var2, var3, var4);
         }
      } else {
         this.resultContainer.removeItemNoUpdate(2);
      }

   }

   private void setupResultSlot(ItemStack var1, ItemStack var2, ItemStack var3) {
      this.access.execute((level, blockPos) -> {
         Item var6 = var2.getItem();
         MapItemSavedData var7 = MapItem.getSavedData(var1, level);
         if(var7 != null) {
            ItemStack var8;
            if(var6 == Items.PAPER && !var7.locked && var7.scale < 4) {
               var8 = var1.copy();
               var8.setCount(1);
               var8.getOrCreateTag().putInt("map_scale_direction", 1);
               this.broadcastChanges();
            } else if(var6 == Items.GLASS_PANE && !var7.locked) {
               var8 = var1.copy();
               var8.setCount(1);
               this.broadcastChanges();
            } else {
               if(var6 != Items.MAP) {
                  this.resultContainer.removeItemNoUpdate(2);
                  this.broadcastChanges();
                  return;
               }

               var8 = var1.copy();
               var8.setCount(2);
               this.broadcastChanges();
            }

            if(!ItemStack.matches(var8, var3)) {
               this.resultContainer.setItem(2, var8);
               this.broadcastChanges();
            }

         }
      });
   }

   public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
      return false;
   }

   public ItemStack quickMoveStack(Player player, int var2) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if(var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         ItemStack var6 = var5;
         Item var7 = var5.getItem();
         itemStack = var5.copy();
         if(var2 == 2) {
            if(this.container.getItem(1).getItem() == Items.GLASS_PANE) {
               var6 = (ItemStack)this.access.evaluate((level, blockPos) -> {
                  ItemStack var4 = MapItem.lockMap(level, this.container.getItem(0));
                  if(var4 != null) {
                     var4.setCount(1);
                     return var4;
                  } else {
                     return var5;
                  }
               }).orElse(var5);
            }

            var7.onCraftedBy(var6, player.level, player);
            if(!this.moveItemStackTo(var6, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var6, itemStack);
         } else if(var2 != 1 && var2 != 0) {
            if(var7 == Items.FILLED_MAP) {
               if(!this.moveItemStackTo(var5, 0, 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if(var7 != Items.PAPER && var7 != Items.MAP && var7 != Items.GLASS_PANE) {
               if(var2 >= 3 && var2 < 30) {
                  if(!this.moveItemStackTo(var5, 30, 39, false)) {
                     return ItemStack.EMPTY;
                  }
               } else if(var2 >= 30 && var2 < 39 && !this.moveItemStackTo(var5, 3, 30, false)) {
                  return ItemStack.EMPTY;
               }
            } else if(!this.moveItemStackTo(var5, 1, 2, false)) {
               return ItemStack.EMPTY;
            }
         } else if(!this.moveItemStackTo(var5, 3, 39, false)) {
            return ItemStack.EMPTY;
         }

         if(var6.isEmpty()) {
            var4.set(ItemStack.EMPTY);
         }

         var4.setChanged();
         if(var6.getCount() == itemStack.getCount()) {
            return ItemStack.EMPTY;
         }

         this.quickMoved = true;
         var4.onTake(player, var6);
         this.quickMoved = false;
         this.broadcastChanges();
      }

      return itemStack;
   }

   public void removed(Player player) {
      super.removed(player);
      this.resultContainer.removeItemNoUpdate(2);
      this.access.execute((level, blockPos) -> {
         this.clearContainer(player, player.level, this.container);
      });
   }
}
