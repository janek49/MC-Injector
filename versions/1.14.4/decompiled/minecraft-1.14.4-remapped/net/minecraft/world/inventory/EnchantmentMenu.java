package net.minecraft.world.inventory;

import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.Blocks;

public class EnchantmentMenu extends AbstractContainerMenu {
   private final Container enchantSlots;
   private final ContainerLevelAccess access;
   private final Random random;
   private final DataSlot enchantmentSeed;
   public final int[] costs;
   public final int[] enchantClue;
   public final int[] levelClue;

   public EnchantmentMenu(int var1, Inventory inventory) {
      this(var1, inventory, ContainerLevelAccess.NULL);
   }

   public EnchantmentMenu(int var1, Inventory inventory, ContainerLevelAccess access) {
      super(MenuType.ENCHANTMENT, var1);
      this.enchantSlots = new SimpleContainer(2) {
         public void setChanged() {
            super.setChanged();
            EnchantmentMenu.this.slotsChanged(this);
         }
      };
      this.random = new Random();
      this.enchantmentSeed = DataSlot.standalone();
      this.costs = new int[3];
      this.enchantClue = new int[]{-1, -1, -1};
      this.levelClue = new int[]{-1, -1, -1};
      this.access = access;
      this.addSlot(new Slot(this.enchantSlots, 0, 15, 47) {
         public boolean mayPlace(ItemStack itemStack) {
            return true;
         }

         public int getMaxStackSize() {
            return 1;
         }
      });
      this.addSlot(new Slot(this.enchantSlots, 1, 35, 47) {
         public boolean mayPlace(ItemStack itemStack) {
            return itemStack.getItem() == Items.LAPIS_LAZULI;
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

      this.addDataSlot(DataSlot.shared(this.costs, 0));
      this.addDataSlot(DataSlot.shared(this.costs, 1));
      this.addDataSlot(DataSlot.shared(this.costs, 2));
      this.addDataSlot(this.enchantmentSeed).set(inventory.player.getEnchantmentSeed());
      this.addDataSlot(DataSlot.shared(this.enchantClue, 0));
      this.addDataSlot(DataSlot.shared(this.enchantClue, 1));
      this.addDataSlot(DataSlot.shared(this.enchantClue, 2));
      this.addDataSlot(DataSlot.shared(this.levelClue, 0));
      this.addDataSlot(DataSlot.shared(this.levelClue, 1));
      this.addDataSlot(DataSlot.shared(this.levelClue, 2));
   }

   public void slotsChanged(Container container) {
      if(container == this.enchantSlots) {
         ItemStack var2 = container.getItem(0);
         if(!var2.isEmpty() && var2.isEnchantable()) {
            this.access.execute((level, blockPos) -> {
               int var4 = 0;

               for(int var5 = -1; var5 <= 1; ++var5) {
                  for(int var6 = -1; var6 <= 1; ++var6) {
                     if((var5 != 0 || var6 != 0) && level.isEmptyBlock(blockPos.offset(var6, 0, var5)) && level.isEmptyBlock(blockPos.offset(var6, 1, var5))) {
                        if(level.getBlockState(blockPos.offset(var6 * 2, 0, var5 * 2)).getBlock() == Blocks.BOOKSHELF) {
                           ++var4;
                        }

                        if(level.getBlockState(blockPos.offset(var6 * 2, 1, var5 * 2)).getBlock() == Blocks.BOOKSHELF) {
                           ++var4;
                        }

                        if(var6 != 0 && var5 != 0) {
                           if(level.getBlockState(blockPos.offset(var6 * 2, 0, var5)).getBlock() == Blocks.BOOKSHELF) {
                              ++var4;
                           }

                           if(level.getBlockState(blockPos.offset(var6 * 2, 1, var5)).getBlock() == Blocks.BOOKSHELF) {
                              ++var4;
                           }

                           if(level.getBlockState(blockPos.offset(var6, 0, var5 * 2)).getBlock() == Blocks.BOOKSHELF) {
                              ++var4;
                           }

                           if(level.getBlockState(blockPos.offset(var6, 1, var5 * 2)).getBlock() == Blocks.BOOKSHELF) {
                              ++var4;
                           }
                        }
                     }
                  }
               }

               this.random.setSeed((long)this.enchantmentSeed.get());

               for(int var5 = 0; var5 < 3; ++var5) {
                  this.costs[var5] = EnchantmentHelper.getEnchantmentCost(this.random, var5, var4, var2);
                  this.enchantClue[var5] = -1;
                  this.levelClue[var5] = -1;
                  if(this.costs[var5] < var5 + 1) {
                     this.costs[var5] = 0;
                  }
               }

               for(int var5 = 0; var5 < 3; ++var5) {
                  if(this.costs[var5] > 0) {
                     List<EnchantmentInstance> var6 = this.getEnchantmentList(var2, var5, this.costs[var5]);
                     if(var6 != null && !var6.isEmpty()) {
                        EnchantmentInstance var7 = (EnchantmentInstance)var6.get(this.random.nextInt(var6.size()));
                        this.enchantClue[var5] = Registry.ENCHANTMENT.getId(var7.enchantment);
                        this.levelClue[var5] = var7.level;
                     }
                  }
               }

               this.broadcastChanges();
            });
         } else {
            for(int var3 = 0; var3 < 3; ++var3) {
               this.costs[var3] = 0;
               this.enchantClue[var3] = -1;
               this.levelClue[var3] = -1;
            }
         }
      }

   }

   public boolean clickMenuButton(Player player, int var2) {
      ItemStack var3 = this.enchantSlots.getItem(0);
      ItemStack var4 = this.enchantSlots.getItem(1);
      int var5 = var2 + 1;
      if((var4.isEmpty() || var4.getCount() < var5) && !player.abilities.instabuild) {
         return false;
      } else if(this.costs[var2] <= 0 || var3.isEmpty() || (player.experienceLevel < var5 || player.experienceLevel < this.costs[var2]) && !player.abilities.instabuild) {
         return false;
      } else {
         this.access.execute((level, blockPos) -> {
            ItemStack var8 = var3;
            List<EnchantmentInstance> var9 = this.getEnchantmentList(var3, var2, this.costs[var2]);
            if(!var9.isEmpty()) {
               player.onEnchantmentPerformed(var3, var5);
               boolean var10 = var3.getItem() == Items.BOOK;
               if(var10) {
                  var8 = new ItemStack(Items.ENCHANTED_BOOK);
                  this.enchantSlots.setItem(0, var8);
               }

               for(int var11 = 0; var11 < var9.size(); ++var11) {
                  EnchantmentInstance var12 = (EnchantmentInstance)var9.get(var11);
                  if(var10) {
                     EnchantedBookItem.addEnchantment(var8, var12);
                  } else {
                     var8.enchant(var12.enchantment, var12.level);
                  }
               }

               if(!player.abilities.instabuild) {
                  var4.shrink(var5);
                  if(var4.isEmpty()) {
                     this.enchantSlots.setItem(1, ItemStack.EMPTY);
                  }
               }

               player.awardStat(Stats.ENCHANT_ITEM);
               if(player instanceof ServerPlayer) {
                  CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer)player, var8, var5);
               }

               this.enchantSlots.setChanged();
               this.enchantmentSeed.set(player.getEnchantmentSeed());
               this.slotsChanged(this.enchantSlots);
               level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.1F + 0.9F);
            }

         });
         return true;
      }
   }

   private List getEnchantmentList(ItemStack itemStack, int var2, int var3) {
      this.random.setSeed((long)(this.enchantmentSeed.get() + var2));
      List<EnchantmentInstance> list = EnchantmentHelper.selectEnchantment(this.random, itemStack, var3, false);
      if(itemStack.getItem() == Items.BOOK && list.size() > 1) {
         list.remove(this.random.nextInt(list.size()));
      }

      return list;
   }

   public int getGoldCount() {
      ItemStack var1 = this.enchantSlots.getItem(1);
      return var1.isEmpty()?0:var1.getCount();
   }

   public int getEnchantmentSeed() {
      return this.enchantmentSeed.get();
   }

   public void removed(Player player) {
      super.removed(player);
      this.access.execute((level, blockPos) -> {
         this.clearContainer(player, player.level, this.enchantSlots);
      });
   }

   public boolean stillValid(Player player) {
      return stillValid(this.access, player, Blocks.ENCHANTING_TABLE);
   }

   public ItemStack quickMoveStack(Player player, int var2) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if(var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         itemStack = var5.copy();
         if(var2 == 0) {
            if(!this.moveItemStackTo(var5, 2, 38, true)) {
               return ItemStack.EMPTY;
            }
         } else if(var2 == 1) {
            if(!this.moveItemStackTo(var5, 2, 38, true)) {
               return ItemStack.EMPTY;
            }
         } else if(var5.getItem() == Items.LAPIS_LAZULI) {
            if(!this.moveItemStackTo(var5, 1, 2, true)) {
               return ItemStack.EMPTY;
            }
         } else {
            if(((Slot)this.slots.get(0)).hasItem() || !((Slot)this.slots.get(0)).mayPlace(var5)) {
               return ItemStack.EMPTY;
            }

            if(var5.hasTag() && var5.getCount() == 1) {
               ((Slot)this.slots.get(0)).set(var5.copy());
               var5.setCount(0);
            } else if(!var5.isEmpty()) {
               ((Slot)this.slots.get(0)).set(new ItemStack(var5.getItem()));
               var5.shrink(1);
            }
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
