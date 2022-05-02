package net.minecraft.world.entity.player;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class Inventory implements Container, Nameable {
   public final NonNullList items = NonNullList.withSize(36, ItemStack.EMPTY);
   public final NonNullList armor = NonNullList.withSize(4, ItemStack.EMPTY);
   public final NonNullList offhand = NonNullList.withSize(1, ItemStack.EMPTY);
   private final List compartments;
   public int selected;
   public final Player player;
   private ItemStack carried;
   private int timesChanged;

   public Inventory(Player player) {
      this.compartments = ImmutableList.of(this.items, this.armor, this.offhand);
      this.carried = ItemStack.EMPTY;
      this.player = player;
   }

   public ItemStack getSelected() {
      return isHotbarSlot(this.selected)?(ItemStack)this.items.get(this.selected):ItemStack.EMPTY;
   }

   public static int getSelectionSize() {
      return 9;
   }

   private boolean hasRemainingSpaceForItem(ItemStack var1, ItemStack var2) {
      return !var1.isEmpty() && this.isSameItem(var1, var2) && var1.isStackable() && var1.getCount() < var1.getMaxStackSize() && var1.getCount() < this.getMaxStackSize();
   }

   private boolean isSameItem(ItemStack var1, ItemStack var2) {
      return var1.getItem() == var2.getItem() && ItemStack.tagMatches(var1, var2);
   }

   public int getFreeSlot() {
      for(int var1 = 0; var1 < this.items.size(); ++var1) {
         if(((ItemStack)this.items.get(var1)).isEmpty()) {
            return var1;
         }
      }

      return -1;
   }

   public void setPickedItem(ItemStack pickedItem) {
      int var2 = this.findSlotMatchingItem(pickedItem);
      if(isHotbarSlot(var2)) {
         this.selected = var2;
      } else {
         if(var2 == -1) {
            this.selected = this.getSuitableHotbarSlot();
            if(!((ItemStack)this.items.get(this.selected)).isEmpty()) {
               int var3 = this.getFreeSlot();
               if(var3 != -1) {
                  this.items.set(var3, this.items.get(this.selected));
               }
            }

            this.items.set(this.selected, pickedItem);
         } else {
            this.pickSlot(var2);
         }

      }
   }

   public void pickSlot(int i) {
      this.selected = this.getSuitableHotbarSlot();
      ItemStack var2 = (ItemStack)this.items.get(this.selected);
      this.items.set(this.selected, this.items.get(i));
      this.items.set(i, var2);
   }

   public static boolean isHotbarSlot(int i) {
      return i >= 0 && i < 9;
   }

   public int findSlotMatchingItem(ItemStack itemStack) {
      for(int var2 = 0; var2 < this.items.size(); ++var2) {
         if(!((ItemStack)this.items.get(var2)).isEmpty() && this.isSameItem(itemStack, (ItemStack)this.items.get(var2))) {
            return var2;
         }
      }

      return -1;
   }

   public int findSlotMatchingUnusedItem(ItemStack itemStack) {
      for(int var2 = 0; var2 < this.items.size(); ++var2) {
         ItemStack var3 = (ItemStack)this.items.get(var2);
         if(!((ItemStack)this.items.get(var2)).isEmpty() && this.isSameItem(itemStack, (ItemStack)this.items.get(var2)) && !((ItemStack)this.items.get(var2)).isDamaged() && !var3.isEnchanted() && !var3.hasCustomHoverName()) {
            return var2;
         }
      }

      return -1;
   }

   public int getSuitableHotbarSlot() {
      for(int var1 = 0; var1 < 9; ++var1) {
         int var2 = (this.selected + var1) % 9;
         if(((ItemStack)this.items.get(var2)).isEmpty()) {
            return var2;
         }
      }

      for(int var1 = 0; var1 < 9; ++var1) {
         int var2 = (this.selected + var1) % 9;
         if(!((ItemStack)this.items.get(var2)).isEnchanted()) {
            return var2;
         }
      }

      return this.selected;
   }

   public void swapPaint(double d) {
      if(d > 0.0D) {
         d = 1.0D;
      }

      if(d < 0.0D) {
         d = -1.0D;
      }

      for(this.selected = (int)((double)this.selected - d); this.selected < 0; this.selected += 9) {
         ;
      }

      while(this.selected >= 9) {
         this.selected -= 9;
      }

   }

   public int clearInventory(Predicate predicate, int var2) {
      int var3 = 0;

      for(int var4 = 0; var4 < this.getContainerSize(); ++var4) {
         ItemStack var5 = this.getItem(var4);
         if(!var5.isEmpty() && predicate.test(var5)) {
            int var6 = var2 <= 0?var5.getCount():Math.min(var2 - var3, var5.getCount());
            var3 += var6;
            if(var2 != 0) {
               var5.shrink(var6);
               if(var5.isEmpty()) {
                  this.setItem(var4, ItemStack.EMPTY);
               }

               if(var2 > 0 && var3 >= var2) {
                  return var3;
               }
            }
         }
      }

      if(!this.carried.isEmpty() && predicate.test(this.carried)) {
         int var4 = var2 <= 0?this.carried.getCount():Math.min(var2 - var3, this.carried.getCount());
         var3 += var4;
         if(var2 != 0) {
            this.carried.shrink(var4);
            if(this.carried.isEmpty()) {
               this.carried = ItemStack.EMPTY;
            }

            if(var2 > 0 && var3 >= var2) {
               return var3;
            }
         }
      }

      return var3;
   }

   private int addResource(ItemStack itemStack) {
      int var2 = this.getSlotWithRemainingSpace(itemStack);
      if(var2 == -1) {
         var2 = this.getFreeSlot();
      }

      return var2 == -1?itemStack.getCount():this.addResource(var2, itemStack);
   }

   private int addResource(int var1, ItemStack itemStack) {
      Item var3 = itemStack.getItem();
      int var4 = itemStack.getCount();
      ItemStack var5 = this.getItem(var1);
      if(var5.isEmpty()) {
         var5 = new ItemStack(var3, 0);
         if(itemStack.hasTag()) {
            var5.setTag(itemStack.getTag().copy());
         }

         this.setItem(var1, var5);
      }

      int var6 = var4;
      if(var4 > var5.getMaxStackSize() - var5.getCount()) {
         var6 = var5.getMaxStackSize() - var5.getCount();
      }

      if(var6 > this.getMaxStackSize() - var5.getCount()) {
         var6 = this.getMaxStackSize() - var5.getCount();
      }

      if(var6 == 0) {
         return var4;
      } else {
         var4 = var4 - var6;
         var5.grow(var6);
         var5.setPopTime(5);
         return var4;
      }
   }

   public int getSlotWithRemainingSpace(ItemStack itemStack) {
      if(this.hasRemainingSpaceForItem(this.getItem(this.selected), itemStack)) {
         return this.selected;
      } else if(this.hasRemainingSpaceForItem(this.getItem(40), itemStack)) {
         return 40;
      } else {
         for(int var2 = 0; var2 < this.items.size(); ++var2) {
            if(this.hasRemainingSpaceForItem((ItemStack)this.items.get(var2), itemStack)) {
               return var2;
            }
         }

         return -1;
      }
   }

   public void tick() {
      for(NonNullList<ItemStack> var2 : this.compartments) {
         for(int var3 = 0; var3 < var2.size(); ++var3) {
            if(!((ItemStack)var2.get(var3)).isEmpty()) {
               ((ItemStack)var2.get(var3)).inventoryTick(this.player.level, this.player, var3, this.selected == var3);
            }
         }
      }

   }

   public boolean add(ItemStack itemStack) {
      return this.add(-1, itemStack);
   }

   public boolean add(int var1, ItemStack itemStack) {
      if(itemStack.isEmpty()) {
         return false;
      } else {
         try {
            if(itemStack.isDamaged()) {
               if(var1 == -1) {
                  var1 = this.getFreeSlot();
               }

               if(var1 >= 0) {
                  this.items.set(var1, itemStack.copy());
                  ((ItemStack)this.items.get(var1)).setPopTime(5);
                  itemStack.setCount(0);
                  return true;
               } else if(this.player.abilities.instabuild) {
                  itemStack.setCount(0);
                  return true;
               } else {
                  return false;
               }
            } else {
               int var3;
               while(true) {
                  var3 = itemStack.getCount();
                  if(var1 == -1) {
                     itemStack.setCount(this.addResource(itemStack));
                  } else {
                     itemStack.setCount(this.addResource(var1, itemStack));
                  }

                  if(itemStack.isEmpty() || itemStack.getCount() >= var3) {
                     break;
                  }
               }

               if(itemStack.getCount() == var3 && this.player.abilities.instabuild) {
                  itemStack.setCount(0);
                  return true;
               } else {
                  return itemStack.getCount() < var3;
               }
            }
         } catch (Throwable var6) {
            CrashReport var4 = CrashReport.forThrowable(var6, "Adding item to inventory");
            CrashReportCategory var5 = var4.addCategory("Item being added");
            var5.setDetail("Item ID", (Object)Integer.valueOf(Item.getId(itemStack.getItem())));
            var5.setDetail("Item data", (Object)Integer.valueOf(itemStack.getDamageValue()));
            var5.setDetail("Item name", () -> {
               return itemStack.getHoverName().getString();
            });
            throw new ReportedException(var4);
         }
      }
   }

   public void placeItemBackInInventory(Level level, ItemStack itemStack) {
      if(!level.isClientSide) {
         while(!itemStack.isEmpty()) {
            int var3 = this.getSlotWithRemainingSpace(itemStack);
            if(var3 == -1) {
               var3 = this.getFreeSlot();
            }

            if(var3 == -1) {
               this.player.drop(itemStack, false);
               break;
            }

            int var4 = itemStack.getMaxStackSize() - this.getItem(var3).getCount();
            if(this.add(var3, itemStack.split(var4))) {
               ((ServerPlayer)this.player).connection.send(new ClientboundContainerSetSlotPacket(-2, var3, this.getItem(var3)));
            }
         }

      }
   }

   public ItemStack removeItem(int var1, int var2) {
      List<ItemStack> var3 = null;

      for(NonNullList<ItemStack> var5 : this.compartments) {
         if(var1 < var5.size()) {
            var3 = var5;
            break;
         }

         var1 -= var5.size();
      }

      return var3 != null && !((ItemStack)var3.get(var1)).isEmpty()?ContainerHelper.removeItem(var3, var1, var2):ItemStack.EMPTY;
   }

   public void removeItem(ItemStack itemStack) {
      for(NonNullList<ItemStack> var3 : this.compartments) {
         for(int var4 = 0; var4 < var3.size(); ++var4) {
            if(var3.get(var4) == itemStack) {
               var3.set(var4, ItemStack.EMPTY);
               break;
            }
         }
      }

   }

   public ItemStack removeItemNoUpdate(int i) {
      NonNullList<ItemStack> var2 = null;

      for(NonNullList<ItemStack> var4 : this.compartments) {
         if(i < var4.size()) {
            var2 = var4;
            break;
         }

         i -= var4.size();
      }

      if(var2 != null && !((ItemStack)var2.get(i)).isEmpty()) {
         ItemStack var3 = (ItemStack)var2.get(i);
         var2.set(i, ItemStack.EMPTY);
         return var3;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void setItem(int var1, ItemStack itemStack) {
      NonNullList<ItemStack> var3 = null;

      for(NonNullList<ItemStack> var5 : this.compartments) {
         if(var1 < var5.size()) {
            var3 = var5;
            break;
         }

         var1 -= var5.size();
      }

      if(var3 != null) {
         var3.set(var1, itemStack);
      }

   }

   public float getDestroySpeed(BlockState blockState) {
      return ((ItemStack)this.items.get(this.selected)).getDestroySpeed(blockState);
   }

   public ListTag save(ListTag listTag) {
      for(int var2 = 0; var2 < this.items.size(); ++var2) {
         if(!((ItemStack)this.items.get(var2)).isEmpty()) {
            CompoundTag var3 = new CompoundTag();
            var3.putByte("Slot", (byte)var2);
            ((ItemStack)this.items.get(var2)).save(var3);
            listTag.add(var3);
         }
      }

      for(int var2 = 0; var2 < this.armor.size(); ++var2) {
         if(!((ItemStack)this.armor.get(var2)).isEmpty()) {
            CompoundTag var3 = new CompoundTag();
            var3.putByte("Slot", (byte)(var2 + 100));
            ((ItemStack)this.armor.get(var2)).save(var3);
            listTag.add(var3);
         }
      }

      for(int var2 = 0; var2 < this.offhand.size(); ++var2) {
         if(!((ItemStack)this.offhand.get(var2)).isEmpty()) {
            CompoundTag var3 = new CompoundTag();
            var3.putByte("Slot", (byte)(var2 + 150));
            ((ItemStack)this.offhand.get(var2)).save(var3);
            listTag.add(var3);
         }
      }

      return listTag;
   }

   public void load(ListTag listTag) {
      this.items.clear();
      this.armor.clear();
      this.offhand.clear();

      for(int var2 = 0; var2 < listTag.size(); ++var2) {
         CompoundTag var3 = listTag.getCompound(var2);
         int var4 = var3.getByte("Slot") & 255;
         ItemStack var5 = ItemStack.of(var3);
         if(!var5.isEmpty()) {
            if(var4 >= 0 && var4 < this.items.size()) {
               this.items.set(var4, var5);
            } else if(var4 >= 100 && var4 < this.armor.size() + 100) {
               this.armor.set(var4 - 100, var5);
            } else if(var4 >= 150 && var4 < this.offhand.size() + 150) {
               this.offhand.set(var4 - 150, var5);
            }
         }
      }

   }

   public int getContainerSize() {
      return this.items.size() + this.armor.size() + this.offhand.size();
   }

   public boolean isEmpty() {
      for(ItemStack var2 : this.items) {
         if(!var2.isEmpty()) {
            return false;
         }
      }

      for(ItemStack var2 : this.armor) {
         if(!var2.isEmpty()) {
            return false;
         }
      }

      for(ItemStack var2 : this.offhand) {
         if(!var2.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getItem(int i) {
      List<ItemStack> var2 = null;

      for(NonNullList<ItemStack> var4 : this.compartments) {
         if(i < var4.size()) {
            var2 = var4;
            break;
         }

         i -= var4.size();
      }

      return var2 == null?ItemStack.EMPTY:(ItemStack)var2.get(i);
   }

   public Component getName() {
      return new TranslatableComponent("container.inventory", new Object[0]);
   }

   public boolean canDestroy(BlockState blockState) {
      return this.getItem(this.selected).canDestroySpecial(blockState);
   }

   public ItemStack getArmor(int i) {
      return (ItemStack)this.armor.get(i);
   }

   public void hurtArmor(float f) {
      if(f > 0.0F) {
         f = f / 4.0F;
         if(f < 1.0F) {
            f = 1.0F;
         }

         for(int var2 = 0; var2 < this.armor.size(); ++var2) {
            ItemStack var3 = (ItemStack)this.armor.get(var2);
            if(var3.getItem() instanceof ArmorItem) {
               var3.hurtAndBreak((int)f, this.player, (player) -> {
                  player.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, var2));
               });
            }
         }

      }
   }

   public void dropAll() {
      for(List<ItemStack> var2 : this.compartments) {
         for(int var3 = 0; var3 < var2.size(); ++var3) {
            ItemStack var4 = (ItemStack)var2.get(var3);
            if(!var4.isEmpty()) {
               this.player.drop(var4, true, false);
               var2.set(var3, ItemStack.EMPTY);
            }
         }
      }

   }

   public void setChanged() {
      ++this.timesChanged;
   }

   public int getTimesChanged() {
      return this.timesChanged;
   }

   public void setCarried(ItemStack carried) {
      this.carried = carried;
   }

   public ItemStack getCarried() {
      return this.carried;
   }

   public boolean stillValid(Player player) {
      return this.player.removed?false:player.distanceToSqr(this.player) <= 64.0D;
   }

   public boolean contains(ItemStack itemStack) {
      label19:
      for(List<ItemStack> var3 : this.compartments) {
         Iterator var4 = var3.iterator();

         while(true) {
            if(!var4.hasNext()) {
               continue label19;
            }

            ItemStack var5 = (ItemStack)var4.next();
            if(!var5.isEmpty() && var5.sameItem(itemStack)) {
               break;
            }
         }

         return true;
      }

      return false;
   }

   public boolean contains(Tag tag) {
      label19:
      for(List<ItemStack> var3 : this.compartments) {
         Iterator var4 = var3.iterator();

         while(true) {
            if(!var4.hasNext()) {
               continue label19;
            }

            ItemStack var5 = (ItemStack)var4.next();
            if(!var5.isEmpty() && tag.contains(var5.getItem())) {
               break;
            }
         }

         return true;
      }

      return false;
   }

   public void replaceWith(Inventory inventory) {
      for(int var2 = 0; var2 < this.getContainerSize(); ++var2) {
         this.setItem(var2, inventory.getItem(var2));
      }

      this.selected = inventory.selected;
   }

   public void clearContent() {
      for(List<ItemStack> var2 : this.compartments) {
         var2.clear();
      }

   }

   public void fillStackedContents(StackedContents stackedContents) {
      for(ItemStack var3 : this.items) {
         stackedContents.accountSimpleStack(var3);
      }

   }
}
