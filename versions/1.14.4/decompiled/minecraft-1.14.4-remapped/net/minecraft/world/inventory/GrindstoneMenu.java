package net.minecraft.world.inventory;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class GrindstoneMenu extends AbstractContainerMenu {
   private final Container resultSlots;
   private final Container repairSlots;
   private final ContainerLevelAccess access;

   public GrindstoneMenu(int var1, Inventory inventory) {
      this(var1, inventory, ContainerLevelAccess.NULL);
   }

   public GrindstoneMenu(int var1, Inventory inventory, final ContainerLevelAccess access) {
      super(MenuType.GRINDSTONE, var1);
      this.resultSlots = new ResultContainer();
      this.repairSlots = new SimpleContainer(2) {
         public void setChanged() {
            super.setChanged();
            GrindstoneMenu.this.slotsChanged(this);
         }
      };
      this.access = access;
      this.addSlot(new Slot(this.repairSlots, 0, 49, 19) {
         public boolean mayPlace(ItemStack itemStack) {
            return itemStack.isDamageableItem() || itemStack.getItem() == Items.ENCHANTED_BOOK || itemStack.isEnchanted();
         }
      });
      this.addSlot(new Slot(this.repairSlots, 1, 49, 40) {
         public boolean mayPlace(ItemStack itemStack) {
            return itemStack.isDamageableItem() || itemStack.getItem() == Items.ENCHANTED_BOOK || itemStack.isEnchanted();
         }
      });
      this.addSlot(new Slot(this.resultSlots, 2, 129, 34) {
         public boolean mayPlace(ItemStack itemStack) {
            return false;
         }

         public ItemStack onTake(Player player, ItemStack var2) {
            access.execute((level, blockPos) -> {
               int var3 = this.getExperienceAmount(level);

               while(var3 > 0) {
                  int var4 = ExperienceOrb.getExperienceValue(var3);
                  var3 -= var4;
                  level.addFreshEntity(new ExperienceOrb(level, (double)blockPos.getX(), (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D, var4));
               }

               level.levelEvent(1042, blockPos, 0);
            });
            GrindstoneMenu.this.repairSlots.setItem(0, ItemStack.EMPTY);
            GrindstoneMenu.this.repairSlots.setItem(1, ItemStack.EMPTY);
            return var2;
         }

         private int getExperienceAmount(Level level) {
            int var2 = 0;
            var2 = var2 + this.getExperienceFromItem(GrindstoneMenu.this.repairSlots.getItem(0));
            var2 = var2 + this.getExperienceFromItem(GrindstoneMenu.this.repairSlots.getItem(1));
            if(var2 > 0) {
               int var3 = (int)Math.ceil((double)var2 / 2.0D);
               return var3 + level.random.nextInt(var3);
            } else {
               return 0;
            }
         }

         private int getExperienceFromItem(ItemStack itemStack) {
            int var2 = 0;
            Map<Enchantment, Integer> var3 = EnchantmentHelper.getEnchantments(itemStack);

            for(Entry<Enchantment, Integer> var5 : var3.entrySet()) {
               Enchantment var6 = (Enchantment)var5.getKey();
               Integer var7 = (Integer)var5.getValue();
               if(!var6.isCurse()) {
                  var2 += var6.getMinCost(var7.intValue());
               }
            }

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

   private void createResult() {
      ItemStack var1 = this.repairSlots.getItem(0);
      ItemStack var2 = this.repairSlots.getItem(1);
      boolean var3 = !var1.isEmpty() || !var2.isEmpty();
      boolean var4 = !var1.isEmpty() && !var2.isEmpty();
      if(!var3) {
         this.resultSlots.setItem(0, ItemStack.EMPTY);
      } else {
         boolean var5 = !var1.isEmpty() && var1.getItem() != Items.ENCHANTED_BOOK && !var1.isEnchanted() || !var2.isEmpty() && var2.getItem() != Items.ENCHANTED_BOOK && !var2.isEnchanted();
         if(var1.getCount() > 1 || var2.getCount() > 1 || !var4 && var5) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.broadcastChanges();
            return;
         }

         int var7 = 1;
         int var6;
         ItemStack var8;
         if(var4) {
            if(var1.getItem() != var2.getItem()) {
               this.resultSlots.setItem(0, ItemStack.EMPTY);
               this.broadcastChanges();
               return;
            }

            Item var9 = var1.getItem();
            int var10 = var9.getMaxDamage() - var1.getDamageValue();
            int var11 = var9.getMaxDamage() - var2.getDamageValue();
            int var12 = var10 + var11 + var9.getMaxDamage() * 5 / 100;
            var6 = Math.max(var9.getMaxDamage() - var12, 0);
            var8 = this.mergeEnchants(var1, var2);
            if(!var8.isDamageableItem()) {
               if(!ItemStack.matches(var1, var2)) {
                  this.resultSlots.setItem(0, ItemStack.EMPTY);
                  this.broadcastChanges();
                  return;
               }

               var7 = 2;
            }
         } else {
            boolean var9 = !var1.isEmpty();
            var6 = var9?var1.getDamageValue():var2.getDamageValue();
            var8 = var9?var1:var2;
         }

         this.resultSlots.setItem(0, this.removeNonCurses(var8, var6, var7));
      }

      this.broadcastChanges();
   }

   private ItemStack mergeEnchants(ItemStack var1, ItemStack var2) {
      ItemStack var3 = var1.copy();
      Map<Enchantment, Integer> var4 = EnchantmentHelper.getEnchantments(var2);

      for(Entry<Enchantment, Integer> var6 : var4.entrySet()) {
         Enchantment var7 = (Enchantment)var6.getKey();
         if(!var7.isCurse() || EnchantmentHelper.getItemEnchantmentLevel(var7, var3) == 0) {
            var3.enchant(var7, ((Integer)var6.getValue()).intValue());
         }
      }

      return var3;
   }

   private ItemStack removeNonCurses(ItemStack var1, int var2, int var3) {
      ItemStack var4 = var1.copy();
      var4.removeTagKey("Enchantments");
      var4.removeTagKey("StoredEnchantments");
      if(var2 > 0) {
         var4.setDamageValue(var2);
      } else {
         var4.removeTagKey("Damage");
      }

      var4.setCount(var3);
      Map<Enchantment, Integer> var5 = (Map)EnchantmentHelper.getEnchantments(var1).entrySet().stream().filter((map$Entry) -> {
         return ((Enchantment)map$Entry.getKey()).isCurse();
      }).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
      EnchantmentHelper.setEnchantments(var5, var4);
      var4.setRepairCost(0);
      if(var4.getItem() == Items.ENCHANTED_BOOK && var5.size() == 0) {
         var4 = new ItemStack(Items.BOOK);
         if(var1.hasCustomHoverName()) {
            var4.setHoverName(var1.getHoverName());
         }
      }

      for(int var6 = 0; var6 < var5.size(); ++var6) {
         var4.setRepairCost(AnvilMenu.calculateIncreasedRepairCost(var4.getBaseRepairCost()));
      }

      return var4;
   }

   public void removed(Player player) {
      super.removed(player);
      this.access.execute((level, blockPos) -> {
         this.clearContainer(player, level, this.repairSlots);
      });
   }

   public boolean stillValid(Player player) {
      return stillValid(this.access, player, Blocks.GRINDSTONE);
   }

   public ItemStack quickMoveStack(Player player, int var2) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if(var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         itemStack = var5.copy();
         ItemStack var6 = this.repairSlots.getItem(0);
         ItemStack var7 = this.repairSlots.getItem(1);
         if(var2 == 2) {
            if(!this.moveItemStackTo(var5, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, itemStack);
         } else if(var2 != 0 && var2 != 1) {
            if(!var6.isEmpty() && !var7.isEmpty()) {
               if(var2 >= 3 && var2 < 30) {
                  if(!this.moveItemStackTo(var5, 30, 39, false)) {
                     return ItemStack.EMPTY;
                  }
               } else if(var2 >= 30 && var2 < 39 && !this.moveItemStackTo(var5, 3, 30, false)) {
                  return ItemStack.EMPTY;
               }
            } else if(!this.moveItemStackTo(var5, 0, 2, false)) {
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
}
