package net.minecraft.world.inventory;

import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class CraftingMenu extends RecipeBookMenu {
   private final CraftingContainer craftSlots;
   private final ResultContainer resultSlots;
   private final ContainerLevelAccess access;
   private final Player player;

   public CraftingMenu(int var1, Inventory inventory) {
      this(var1, inventory, ContainerLevelAccess.NULL);
   }

   public CraftingMenu(int var1, Inventory inventory, ContainerLevelAccess access) {
      super(MenuType.CRAFTING, var1);
      this.craftSlots = new CraftingContainer(this, 3, 3);
      this.resultSlots = new ResultContainer();
      this.access = access;
      this.player = inventory.player;
      this.addSlot(new ResultSlot(inventory.player, this.craftSlots, this.resultSlots, 0, 124, 35));

      for(int var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 3; ++var5) {
            this.addSlot(new Slot(this.craftSlots, var5 + var4 * 3, 30 + var5 * 18, 17 + var4 * 18));
         }
      }

      for(int var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 9; ++var5) {
            this.addSlot(new Slot(inventory, var5 + var4 * 9 + 9, 8 + var5 * 18, 84 + var4 * 18));
         }
      }

      for(int var4 = 0; var4 < 9; ++var4) {
         this.addSlot(new Slot(inventory, var4, 8 + var4 * 18, 142));
      }

   }

   protected static void slotChangedCraftingGrid(int var0, Level level, Player player, CraftingContainer craftingContainer, ResultContainer resultContainer) {
      if(!level.isClientSide) {
         ServerPlayer var5 = (ServerPlayer)player;
         ItemStack var6 = ItemStack.EMPTY;
         Optional<CraftingRecipe> var7 = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingContainer, level);
         if(var7.isPresent()) {
            CraftingRecipe var8 = (CraftingRecipe)var7.get();
            if(resultContainer.setRecipeUsed(level, var5, var8)) {
               var6 = var8.assemble(craftingContainer);
            }
         }

         resultContainer.setItem(0, var6);
         var5.connection.send(new ClientboundContainerSetSlotPacket(var0, 0, var6));
      }
   }

   public void slotsChanged(Container container) {
      this.access.execute((level, blockPos) -> {
         slotChangedCraftingGrid(this.containerId, level, this.player, this.craftSlots, this.resultSlots);
      });
   }

   public void fillCraftSlotsStackedContents(StackedContents stackedContents) {
      this.craftSlots.fillStackedContents(stackedContents);
   }

   public void clearCraftingContent() {
      this.craftSlots.clearContent();
      this.resultSlots.clearContent();
   }

   public boolean recipeMatches(Recipe recipe) {
      return recipe.matches(this.craftSlots, this.player.level);
   }

   public void removed(Player player) {
      super.removed(player);
      this.access.execute((level, blockPos) -> {
         this.clearContainer(player, level, this.craftSlots);
      });
   }

   public boolean stillValid(Player player) {
      return stillValid(this.access, player, Blocks.CRAFTING_TABLE);
   }

   public ItemStack quickMoveStack(Player player, int var2) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if(var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         itemStack = var5.copy();
         if(var2 == 0) {
            this.access.execute((level, blockPos) -> {
               var5.getItem().onCraftedBy(var5, level, player);
            });
            if(!this.moveItemStackTo(var5, 10, 46, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, itemStack);
         } else if(var2 >= 10 && var2 < 37) {
            if(!this.moveItemStackTo(var5, 37, 46, false)) {
               return ItemStack.EMPTY;
            }
         } else if(var2 >= 37 && var2 < 46) {
            if(!this.moveItemStackTo(var5, 10, 37, false)) {
               return ItemStack.EMPTY;
            }
         } else if(!this.moveItemStackTo(var5, 10, 46, false)) {
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

         ItemStack var6 = var4.onTake(player, var5);
         if(var2 == 0) {
            player.drop(var6, false);
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
      return 10;
   }
}
