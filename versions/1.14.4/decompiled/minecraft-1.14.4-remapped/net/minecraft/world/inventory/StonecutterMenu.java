package net.minecraft.world.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class StonecutterMenu extends AbstractContainerMenu {
   static final ImmutableList validItems = ImmutableList.of(Items.STONE, Items.SANDSTONE, Items.RED_SANDSTONE, Items.QUARTZ_BLOCK, Items.COBBLESTONE, Items.STONE_BRICKS, Items.BRICKS, Items.NETHER_BRICKS, Items.RED_NETHER_BRICKS, Items.PURPUR_BLOCK, Items.PRISMARINE, Items.PRISMARINE_BRICKS, new Item[]{Items.DARK_PRISMARINE, Items.ANDESITE, Items.POLISHED_ANDESITE, Items.GRANITE, Items.POLISHED_GRANITE, Items.DIORITE, Items.POLISHED_DIORITE, Items.MOSSY_STONE_BRICKS, Items.MOSSY_COBBLESTONE, Items.SMOOTH_SANDSTONE, Items.SMOOTH_RED_SANDSTONE, Items.SMOOTH_QUARTZ, Items.END_STONE, Items.END_STONE_BRICKS, Items.SMOOTH_STONE, Items.CUT_SANDSTONE, Items.CUT_RED_SANDSTONE});
   private final ContainerLevelAccess access;
   private final DataSlot selectedRecipeIndex;
   private final Level level;
   private List recipes;
   private ItemStack input;
   private long lastSoundTime;
   final Slot inputSlot;
   final Slot resultSlot;
   private Runnable slotUpdateListener;
   public final Container container;
   private final ResultContainer resultContainer;

   public StonecutterMenu(int var1, Inventory inventory) {
      this(var1, inventory, ContainerLevelAccess.NULL);
   }

   public StonecutterMenu(int var1, Inventory inventory, final ContainerLevelAccess access) {
      super(MenuType.STONECUTTER, var1);
      this.selectedRecipeIndex = DataSlot.standalone();
      this.recipes = Lists.newArrayList();
      this.input = ItemStack.EMPTY;
      this.slotUpdateListener = () -> {
      };
      this.container = new SimpleContainer(1) {
         public void setChanged() {
            super.setChanged();
            StonecutterMenu.this.slotsChanged(this);
            StonecutterMenu.this.slotUpdateListener.run();
         }
      };
      this.resultContainer = new ResultContainer();
      this.access = access;
      this.level = inventory.player.level;
      this.inputSlot = this.addSlot(new Slot(this.container, 0, 20, 33));
      this.resultSlot = this.addSlot(new Slot(this.resultContainer, 1, 143, 33) {
         public boolean mayPlace(ItemStack itemStack) {
            return false;
         }

         public ItemStack onTake(Player player, ItemStack var2) {
            ItemStack var3 = StonecutterMenu.this.inputSlot.remove(1);
            if(!var3.isEmpty()) {
               StonecutterMenu.this.setupResultSlot();
            }

            var2.getItem().onCraftedBy(var2, player.level, player);
            access.execute((level, blockPos) -> {
               long var3 = level.getGameTime();
               if(StonecutterMenu.this.lastSoundTime != var3) {
                  level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                  StonecutterMenu.this.lastSoundTime = var3;
               }

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

      this.addDataSlot(this.selectedRecipeIndex);
   }

   public int getSelectedRecipeIndex() {
      return this.selectedRecipeIndex.get();
   }

   public List getRecipes() {
      return this.recipes;
   }

   public int getNumRecipes() {
      return this.recipes.size();
   }

   public boolean hasInputItem() {
      return this.inputSlot.hasItem() && !this.recipes.isEmpty();
   }

   public boolean stillValid(Player player) {
      return stillValid(this.access, player, Blocks.STONECUTTER);
   }

   public boolean clickMenuButton(Player player, int var2) {
      if(var2 >= 0 && var2 < this.recipes.size()) {
         this.selectedRecipeIndex.set(var2);
         this.setupResultSlot();
      }

      return true;
   }

   public void slotsChanged(Container container) {
      ItemStack var2 = this.inputSlot.getItem();
      if(var2.getItem() != this.input.getItem()) {
         this.input = var2.copy();
         this.setupRecipeList(container, var2);
      }

   }

   private void setupRecipeList(Container container, ItemStack itemStack) {
      this.recipes.clear();
      this.selectedRecipeIndex.set(-1);
      this.resultSlot.set(ItemStack.EMPTY);
      if(!itemStack.isEmpty()) {
         this.recipes = this.level.getRecipeManager().getRecipesFor(RecipeType.STONECUTTING, container, this.level);
      }

   }

   private void setupResultSlot() {
      if(!this.recipes.isEmpty()) {
         StonecutterRecipe var1 = (StonecutterRecipe)this.recipes.get(this.selectedRecipeIndex.get());
         this.resultSlot.set(var1.assemble(this.container));
      } else {
         this.resultSlot.set(ItemStack.EMPTY);
      }

      this.broadcastChanges();
   }

   public MenuType getType() {
      return MenuType.STONECUTTER;
   }

   public void registerUpdateListener(Runnable slotUpdateListener) {
      this.slotUpdateListener = slotUpdateListener;
   }

   public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
      return false;
   }

   public ItemStack quickMoveStack(Player player, int var2) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if(var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         Item var6 = var5.getItem();
         itemStack = var5.copy();
         if(var2 == 1) {
            var6.onCraftedBy(var5, player.level, player);
            if(!this.moveItemStackTo(var5, 2, 38, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, itemStack);
         } else if(var2 == 0) {
            if(!this.moveItemStackTo(var5, 2, 38, false)) {
               return ItemStack.EMPTY;
            }
         } else if(validItems.contains(var6)) {
            if(!this.moveItemStackTo(var5, 0, 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if(var2 >= 2 && var2 < 29) {
            if(!this.moveItemStackTo(var5, 29, 38, false)) {
               return ItemStack.EMPTY;
            }
         } else if(var2 >= 29 && var2 < 38 && !this.moveItemStackTo(var5, 2, 29, false)) {
            return ItemStack.EMPTY;
         }

         if(var5.isEmpty()) {
            var4.set(ItemStack.EMPTY);
         }

         var4.setChanged();
         if(var5.getCount() == itemStack.getCount()) {
            return ItemStack.EMPTY;
         }

         var4.onTake(player, var5);
         this.broadcastChanges();
      }

      return itemStack;
   }

   public void removed(Player player) {
      super.removed(player);
      this.resultContainer.removeItemNoUpdate(1);
      this.access.execute((level, blockPos) -> {
         this.clearContainer(player, player.level, this.container);
      });
   }
}
