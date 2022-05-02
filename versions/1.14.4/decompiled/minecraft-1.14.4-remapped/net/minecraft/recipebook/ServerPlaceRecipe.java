package net.minecraft.recipebook;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlaceRecipe implements PlaceRecipe {
   protected static final Logger LOGGER = LogManager.getLogger();
   protected final StackedContents stackedContents = new StackedContents();
   protected Inventory inventory;
   protected RecipeBookMenu menu;

   public ServerPlaceRecipe(RecipeBookMenu menu) {
      this.menu = menu;
   }

   public void recipeClicked(ServerPlayer serverPlayer, @Nullable Recipe recipe, boolean var3) {
      if(recipe != null && serverPlayer.getRecipeBook().contains(recipe)) {
         this.inventory = serverPlayer.inventory;
         if(this.testClearGrid() || serverPlayer.isCreative()) {
            this.stackedContents.clear();
            serverPlayer.inventory.fillStackedContents(this.stackedContents);
            this.menu.fillCraftSlotsStackedContents(this.stackedContents);
            if(this.stackedContents.canCraft(recipe, (IntList)null)) {
               this.handleRecipeClicked(recipe, var3);
            } else {
               this.clearGrid();
               serverPlayer.connection.send(new ClientboundPlaceGhostRecipePacket(serverPlayer.containerMenu.containerId, recipe));
            }

            serverPlayer.inventory.setChanged();
         }
      }
   }

   protected void clearGrid() {
      for(int var1 = 0; var1 < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++var1) {
         if(var1 != this.menu.getResultSlotIndex() || !(this.menu instanceof CraftingMenu) && !(this.menu instanceof InventoryMenu)) {
            this.moveItemToInventory(var1);
         }
      }

      this.menu.clearCraftingContent();
   }

   protected void moveItemToInventory(int i) {
      ItemStack var2 = this.menu.getSlot(i).getItem();
      if(!var2.isEmpty()) {
         for(; var2.getCount() > 0; this.menu.getSlot(i).remove(1)) {
            int var3 = this.inventory.getSlotWithRemainingSpace(var2);
            if(var3 == -1) {
               var3 = this.inventory.getFreeSlot();
            }

            ItemStack var4 = var2.copy();
            var4.setCount(1);
            if(!this.inventory.add(var3, var4)) {
               LOGGER.error("Can\'t find any space for item in the inventory");
            }
         }

      }
   }

   protected void handleRecipeClicked(Recipe recipe, boolean var2) {
      boolean var3 = this.menu.recipeMatches(recipe);
      int var4 = this.stackedContents.getBiggestCraftableStack(recipe, (IntList)null);
      if(var3) {
         for(int var5 = 0; var5 < this.menu.getGridHeight() * this.menu.getGridWidth() + 1; ++var5) {
            if(var5 != this.menu.getResultSlotIndex()) {
               ItemStack var6 = this.menu.getSlot(var5).getItem();
               if(!var6.isEmpty() && Math.min(var4, var6.getMaxStackSize()) < var6.getCount() + 1) {
                  return;
               }
            }
         }
      }

      int var5 = this.getStackSize(var2, var4, var3);
      IntList var6 = new IntArrayList();
      if(this.stackedContents.canCraft(recipe, var6, var5)) {
         int var7 = var5;
         IntListIterator var8 = var6.iterator();

         while(var8.hasNext()) {
            int var9 = ((Integer)var8.next()).intValue();
            int var10 = StackedContents.fromStackingIndex(var9).getMaxStackSize();
            if(var10 < var7) {
               var7 = var10;
            }
         }

         if(this.stackedContents.canCraft(recipe, var6, var7)) {
            this.clearGrid();
            this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), recipe, var6.iterator(), var7);
         }
      }

   }

   public void addItemToSlot(Iterator iterator, int var2, int var3, int var4, int var5) {
      Slot var6 = this.menu.getSlot(var2);
      ItemStack var7 = StackedContents.fromStackingIndex(((Integer)iterator.next()).intValue());
      if(!var7.isEmpty()) {
         for(int var8 = 0; var8 < var3; ++var8) {
            this.moveItemToGrid(var6, var7);
         }
      }

   }

   protected int getStackSize(boolean var1, int var2, boolean var3) {
      int var4 = 1;
      if(var1) {
         var4 = var2;
      } else if(var3) {
         var4 = 64;

         for(int var5 = 0; var5 < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++var5) {
            if(var5 != this.menu.getResultSlotIndex()) {
               ItemStack var6 = this.menu.getSlot(var5).getItem();
               if(!var6.isEmpty() && var4 > var6.getCount()) {
                  var4 = var6.getCount();
               }
            }
         }

         if(var4 < 64) {
            ++var4;
         }
      }

      return var4;
   }

   protected void moveItemToGrid(Slot slot, ItemStack itemStack) {
      int var3 = this.inventory.findSlotMatchingUnusedItem(itemStack);
      if(var3 != -1) {
         ItemStack var4 = this.inventory.getItem(var3).copy();
         if(!var4.isEmpty()) {
            if(var4.getCount() > 1) {
               this.inventory.removeItem(var3, 1);
            } else {
               this.inventory.removeItemNoUpdate(var3);
            }

            var4.setCount(1);
            if(slot.getItem().isEmpty()) {
               slot.set(var4);
            } else {
               slot.getItem().grow(1);
            }

         }
      }
   }

   private boolean testClearGrid() {
      List<ItemStack> var1 = Lists.newArrayList();
      int var2 = this.getAmountOfFreeSlotsInInventory();

      for(int var3 = 0; var3 < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++var3) {
         if(var3 != this.menu.getResultSlotIndex()) {
            ItemStack var4 = this.menu.getSlot(var3).getItem().copy();
            if(!var4.isEmpty()) {
               int var5 = this.inventory.getSlotWithRemainingSpace(var4);
               if(var5 == -1 && var1.size() <= var2) {
                  for(ItemStack var7 : var1) {
                     if(var7.sameItem(var4) && var7.getCount() != var7.getMaxStackSize() && var7.getCount() + var4.getCount() <= var7.getMaxStackSize()) {
                        var7.grow(var4.getCount());
                        var4.setCount(0);
                        break;
                     }
                  }

                  if(!var4.isEmpty()) {
                     if(var1.size() >= var2) {
                        return false;
                     }

                     var1.add(var4);
                  }
               } else if(var5 == -1) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private int getAmountOfFreeSlotsInInventory() {
      int var1 = 0;

      for(ItemStack var3 : this.inventory.items) {
         if(var3.isEmpty()) {
            ++var1;
         }
      }

      return var1;
   }
}
