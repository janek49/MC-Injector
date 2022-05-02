package net.minecraft.world.inventory;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.tags.BlockTags;
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
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilMenu extends AbstractContainerMenu {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Container resultSlots;
   private final Container repairSlots;
   private final DataSlot cost;
   private final ContainerLevelAccess access;
   private int repairItemCountCost;
   private String itemName;
   private final Player player;

   public AnvilMenu(int var1, Inventory inventory) {
      this(var1, inventory, ContainerLevelAccess.NULL);
   }

   public AnvilMenu(int var1, Inventory inventory, final ContainerLevelAccess access) {
      super(MenuType.ANVIL, var1);
      this.resultSlots = new ResultContainer();
      this.repairSlots = new SimpleContainer(2) {
         public void setChanged() {
            super.setChanged();
            AnvilMenu.this.slotsChanged(this);
         }
      };
      this.cost = DataSlot.standalone();
      this.access = access;
      this.player = inventory.player;
      this.addDataSlot(this.cost);
      this.addSlot(new Slot(this.repairSlots, 0, 27, 47));
      this.addSlot(new Slot(this.repairSlots, 1, 76, 47));
      this.addSlot(new Slot(this.resultSlots, 2, 134, 47) {
         public boolean mayPlace(ItemStack itemStack) {
            return false;
         }

         public boolean mayPickup(Player player) {
            return (player.abilities.instabuild || player.experienceLevel >= AnvilMenu.this.cost.get()) && AnvilMenu.this.cost.get() > 0 && this.hasItem();
         }

         public ItemStack onTake(Player player, ItemStack var2) {
            if(!player.abilities.instabuild) {
               player.giveExperienceLevels(-AnvilMenu.this.cost.get());
            }

            AnvilMenu.this.repairSlots.setItem(0, ItemStack.EMPTY);
            if(AnvilMenu.this.repairItemCountCost > 0) {
               ItemStack var3 = AnvilMenu.this.repairSlots.getItem(1);
               if(!var3.isEmpty() && var3.getCount() > AnvilMenu.this.repairItemCountCost) {
                  var3.shrink(AnvilMenu.this.repairItemCountCost);
                  AnvilMenu.this.repairSlots.setItem(1, var3);
               } else {
                  AnvilMenu.this.repairSlots.setItem(1, ItemStack.EMPTY);
               }
            } else {
               AnvilMenu.this.repairSlots.setItem(1, ItemStack.EMPTY);
            }

            AnvilMenu.this.cost.set(0);
            access.execute((level, blockPos) -> {
               BlockState var3 = level.getBlockState(blockPos);
               if(!player.abilities.instabuild && var3.is(BlockTags.ANVIL) && player.getRandom().nextFloat() < 0.12F) {
                  BlockState var4 = AnvilBlock.damage(var3);
                  if(var4 == null) {
                     level.removeBlock(blockPos, false);
                     level.levelEvent(1029, blockPos, 0);
                  } else {
                     level.setBlock(blockPos, var4, 2);
                     level.levelEvent(1030, blockPos, 0);
                  }
               } else {
                  level.levelEvent(1030, blockPos, 0);
               }

            });
            return var2;
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

   public void slotsChanged(Container container) {
      super.slotsChanged(container);
      if(container == this.repairSlots) {
         this.createResult();
      }

   }

   public void createResult() {
      ItemStack var1 = this.repairSlots.getItem(0);
      this.cost.set(1);
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;
      if(var1.isEmpty()) {
         this.resultSlots.setItem(0, ItemStack.EMPTY);
         this.cost.set(0);
      } else {
         ItemStack var5 = var1.copy();
         ItemStack var6 = this.repairSlots.getItem(1);
         Map<Enchantment, Integer> var7 = EnchantmentHelper.getEnchantments(var5);
         var3 = var3 + var1.getBaseRepairCost() + (var6.isEmpty()?0:var6.getBaseRepairCost());
         this.repairItemCountCost = 0;
         if(!var6.isEmpty()) {
            boolean var8 = var6.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(var6).isEmpty();
            if(var5.isDamageableItem() && var5.getItem().isValidRepairItem(var1, var6)) {
               int var9 = Math.min(var5.getDamageValue(), var5.getMaxDamage() / 4);
               if(var9 <= 0) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
                  this.cost.set(0);
                  return;
               }

               int var10;
               for(var10 = 0; var9 > 0 && var10 < var6.getCount(); ++var10) {
                  int var11 = var5.getDamageValue() - var9;
                  var5.setDamageValue(var11);
                  ++var2;
                  var9 = Math.min(var5.getDamageValue(), var5.getMaxDamage() / 4);
               }

               this.repairItemCountCost = var10;
            } else {
               if(!var8 && (var5.getItem() != var6.getItem() || !var5.isDamageableItem())) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
                  this.cost.set(0);
                  return;
               }

               if(var5.isDamageableItem() && !var8) {
                  int var9 = var1.getMaxDamage() - var1.getDamageValue();
                  int var10 = var6.getMaxDamage() - var6.getDamageValue();
                  int var11 = var10 + var5.getMaxDamage() * 12 / 100;
                  int var12 = var9 + var11;
                  int var13 = var5.getMaxDamage() - var12;
                  if(var13 < 0) {
                     var13 = 0;
                  }

                  if(var13 < var5.getDamageValue()) {
                     var5.setDamageValue(var13);
                     var2 += 2;
                  }
               }

               Map<Enchantment, Integer> var9 = EnchantmentHelper.getEnchantments(var6);
               boolean var10 = false;
               boolean var11 = false;

               for(Enchantment var13 : var9.keySet()) {
                  if(var13 != null) {
                     int var14 = var7.containsKey(var13)?((Integer)var7.get(var13)).intValue():0;
                     int var15 = ((Integer)var9.get(var13)).intValue();
                     var15 = var14 == var15?var15 + 1:Math.max(var15, var14);
                     boolean var16 = var13.canEnchant(var1);
                     if(this.player.abilities.instabuild || var1.getItem() == Items.ENCHANTED_BOOK) {
                        var16 = true;
                     }

                     for(Enchantment var18 : var7.keySet()) {
                        if(var18 != var13 && !var13.isCompatibleWith(var18)) {
                           var16 = false;
                           ++var2;
                        }
                     }

                     if(!var16) {
                        var11 = true;
                     } else {
                        var10 = true;
                        if(var15 > var13.getMaxLevel()) {
                           var15 = var13.getMaxLevel();
                        }

                        var7.put(var13, Integer.valueOf(var15));
                        int var17 = 0;
                        switch(var13.getRarity()) {
                        case COMMON:
                           var17 = 1;
                           break;
                        case UNCOMMON:
                           var17 = 2;
                           break;
                        case RARE:
                           var17 = 4;
                           break;
                        case VERY_RARE:
                           var17 = 8;
                        }

                        if(var8) {
                           var17 = Math.max(1, var17 / 2);
                        }

                        var2 += var17 * var15;
                        if(var1.getCount() > 1) {
                           var2 = 40;
                        }
                     }
                  }
               }

               if(var11 && !var10) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
                  this.cost.set(0);
                  return;
               }
            }
         }

         if(StringUtils.isBlank(this.itemName)) {
            if(var1.hasCustomHoverName()) {
               var4 = 1;
               var2 += var4;
               var5.resetHoverName();
            }
         } else if(!this.itemName.equals(var1.getHoverName().getString())) {
            var4 = 1;
            var2 += var4;
            var5.setHoverName(new TextComponent(this.itemName));
         }

         this.cost.set(var3 + var2);
         if(var2 <= 0) {
            var5 = ItemStack.EMPTY;
         }

         if(var4 == var2 && var4 > 0 && this.cost.get() >= 40) {
            this.cost.set(39);
         }

         if(this.cost.get() >= 40 && !this.player.abilities.instabuild) {
            var5 = ItemStack.EMPTY;
         }

         if(!var5.isEmpty()) {
            int var8 = var5.getBaseRepairCost();
            if(!var6.isEmpty() && var8 < var6.getBaseRepairCost()) {
               var8 = var6.getBaseRepairCost();
            }

            if(var4 != var2 || var4 == 0) {
               var8 = calculateIncreasedRepairCost(var8);
            }

            var5.setRepairCost(var8);
            EnchantmentHelper.setEnchantments(var7, var5);
         }

         this.resultSlots.setItem(0, var5);
         this.broadcastChanges();
      }
   }

   public static int calculateIncreasedRepairCost(int i) {
      return i * 2 + 1;
   }

   public void removed(Player player) {
      super.removed(player);
      this.access.execute((level, blockPos) -> {
         this.clearContainer(player, level, this.repairSlots);
      });
   }

   public boolean stillValid(Player player) {
      return ((Boolean)this.access.evaluate((level, blockPos) -> {
         return !level.getBlockState(blockPos).is(BlockTags.ANVIL)?Boolean.valueOf(false):Boolean.valueOf(player.distanceToSqr((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D) <= 64.0D);
      }, Boolean.valueOf(true))).booleanValue();
   }

   public ItemStack quickMoveStack(Player player, int var2) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if(var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         itemStack = var5.copy();
         if(var2 == 2) {
            if(!this.moveItemStackTo(var5, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, itemStack);
         } else if(var2 != 0 && var2 != 1) {
            if(var2 >= 3 && var2 < 39 && !this.moveItemStackTo(var5, 0, 2, false)) {
               return ItemStack.EMPTY;
            }
         } else if(!this.moveItemStackTo(var5, 3, 39, false)) {
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

   public void setItemName(String itemName) {
      this.itemName = itemName;
      if(this.getSlot(2).hasItem()) {
         ItemStack var2 = this.getSlot(2).getItem();
         if(StringUtils.isBlank(itemName)) {
            var2.resetHoverName();
         } else {
            var2.setHoverName(new TextComponent(this.itemName));
         }
      }

      this.createResult();
   }

   public int getCost() {
      return this.cost.get();
   }
}
