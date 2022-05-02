package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class InventoryMenu extends RecipeBookMenu {
   private static final String[] TEXTURE_EMPTY_SLOTS = new String[]{"item/empty_armor_slot_boots", "item/empty_armor_slot_leggings", "item/empty_armor_slot_chestplate", "item/empty_armor_slot_helmet"};
   private static final EquipmentSlot[] SLOT_IDS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private final CraftingContainer craftSlots = new CraftingContainer(this, 2, 2);
   private final ResultContainer resultSlots = new ResultContainer();
   public final boolean active;
   private final Player owner;

   public InventoryMenu(final Inventory inventory, boolean active, Player owner) {
      super((MenuType)null, 0);
      this.active = active;
      this.owner = owner;
      this.addSlot(new ResultSlot(inventory.player, this.craftSlots, this.resultSlots, 0, 154, 28));

      for(int var4 = 0; var4 < 2; ++var4) {
         for(int var5 = 0; var5 < 2; ++var5) {
            this.addSlot(new Slot(this.craftSlots, var5 + var4 * 2, 98 + var5 * 18, 18 + var4 * 18));
         }
      }

      for(int var4 = 0; var4 < 4; ++var4) {
         final EquipmentSlot var5 = SLOT_IDS[var4];
         this.addSlot(new Slot(inventory, 39 - var4, 8, 8 + var4 * 18) {
            public int getMaxStackSize() {
               return 1;
            }

            public boolean mayPlace(ItemStack itemStack) {
               return var5 == Mob.getEquipmentSlotForItem(itemStack);
            }

            public boolean mayPickup(Player player) {
               ItemStack var2 = this.getItem();
               return !var2.isEmpty() && !player.isCreative() && EnchantmentHelper.hasBindingCurse(var2)?false:super.mayPickup(player);
            }

            @Nullable
            public String getNoItemIcon() {
               return InventoryMenu.TEXTURE_EMPTY_SLOTS[var5.getIndex()];
            }
         });
      }

      for(int var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 9; ++var5) {
            this.addSlot(new Slot(inventory, var5 + (var4 + 1) * 9, 8 + var5 * 18, 84 + var4 * 18));
         }
      }

      for(int var4 = 0; var4 < 9; ++var4) {
         this.addSlot(new Slot(inventory, var4, 8 + var4 * 18, 142));
      }

      this.addSlot(new Slot(inventory, 40, 77, 62) {
         @Nullable
         public String getNoItemIcon() {
            return "item/empty_armor_slot_shield";
         }
      });
   }

   public void fillCraftSlotsStackedContents(StackedContents stackedContents) {
      this.craftSlots.fillStackedContents(stackedContents);
   }

   public void clearCraftingContent() {
      this.resultSlots.clearContent();
      this.craftSlots.clearContent();
   }

   public boolean recipeMatches(Recipe recipe) {
      return recipe.matches(this.craftSlots, this.owner.level);
   }

   public void slotsChanged(Container container) {
      CraftingMenu.slotChangedCraftingGrid(this.containerId, this.owner.level, this.owner, this.craftSlots, this.resultSlots);
   }

   public void removed(Player player) {
      super.removed(player);
      this.resultSlots.clearContent();
      if(!player.level.isClientSide) {
         this.clearContainer(player, player.level, this.craftSlots);
      }
   }

   public boolean stillValid(Player player) {
      return true;
   }

   public ItemStack quickMoveStack(Player player, int var2) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if(var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         itemStack = var5.copy();
         EquipmentSlot var6 = Mob.getEquipmentSlotForItem(itemStack);
         if(var2 == 0) {
            if(!this.moveItemStackTo(var5, 9, 45, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, itemStack);
         } else if(var2 >= 1 && var2 < 5) {
            if(!this.moveItemStackTo(var5, 9, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if(var2 >= 5 && var2 < 9) {
            if(!this.moveItemStackTo(var5, 9, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if(var6.getType() == EquipmentSlot.Type.ARMOR && !((Slot)this.slots.get(8 - var6.getIndex())).hasItem()) {
            int var7 = 8 - var6.getIndex();
            if(!this.moveItemStackTo(var5, var7, var7 + 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if(var6 == EquipmentSlot.OFFHAND && !((Slot)this.slots.get(45)).hasItem()) {
            if(!this.moveItemStackTo(var5, 45, 46, false)) {
               return ItemStack.EMPTY;
            }
         } else if(var2 >= 9 && var2 < 36) {
            if(!this.moveItemStackTo(var5, 36, 45, false)) {
               return ItemStack.EMPTY;
            }
         } else if(var2 >= 36 && var2 < 45) {
            if(!this.moveItemStackTo(var5, 9, 36, false)) {
               return ItemStack.EMPTY;
            }
         } else if(!this.moveItemStackTo(var5, 9, 45, false)) {
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

         ItemStack var7 = var4.onTake(player, var5);
         if(var2 == 0) {
            player.drop(var7, false);
         }
      }

      return itemStack;
   }

   public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
      return slot.container != this.resultSlots && super.canTakeItemForPickAll(itemStack, slot);
   }

   public int getResultSlotIndex() {
      return 0;
   }

   public int getGridWidth() {
      return this.craftSlots.getWidth();
   }

   public int getGridHeight() {
      return this.craftSlots.getHeight();
   }

   public int getSize() {
      return 5;
   }
}
