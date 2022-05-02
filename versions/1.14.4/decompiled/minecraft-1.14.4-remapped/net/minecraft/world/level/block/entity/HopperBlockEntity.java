package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

public class HopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper, TickableBlockEntity {
   private NonNullList items = NonNullList.withSize(5, ItemStack.EMPTY);
   private int cooldownTime = -1;
   private long tickedGameTime;

   public HopperBlockEntity() {
      super(BlockEntityType.HOPPER);
   }

   public void load(CompoundTag compoundTag) {
      super.load(compoundTag);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if(!this.tryLoadLootTable(compoundTag)) {
         ContainerHelper.loadAllItems(compoundTag, this.items);
      }

      this.cooldownTime = compoundTag.getInt("TransferCooldown");
   }

   public CompoundTag save(CompoundTag compoundTag) {
      super.save(compoundTag);
      if(!this.trySaveLootTable(compoundTag)) {
         ContainerHelper.saveAllItems(compoundTag, this.items);
      }

      compoundTag.putInt("TransferCooldown", this.cooldownTime);
      return compoundTag;
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public ItemStack removeItem(int var1, int var2) {
      this.unpackLootTable((Player)null);
      return ContainerHelper.removeItem(this.getItems(), var1, var2);
   }

   public void setItem(int var1, ItemStack itemStack) {
      this.unpackLootTable((Player)null);
      this.getItems().set(var1, itemStack);
      if(itemStack.getCount() > this.getMaxStackSize()) {
         itemStack.setCount(this.getMaxStackSize());
      }

   }

   protected Component getDefaultName() {
      return new TranslatableComponent("container.hopper", new Object[0]);
   }

   public void tick() {
      if(this.level != null && !this.level.isClientSide) {
         --this.cooldownTime;
         this.tickedGameTime = this.level.getGameTime();
         if(!this.isOnCooldown()) {
            this.setCooldown(0);
            this.tryMoveItems(() -> {
               return Boolean.valueOf(suckInItems(this));
            });
         }

      }
   }

   private boolean tryMoveItems(Supplier supplier) {
      if(this.level != null && !this.level.isClientSide) {
         if(!this.isOnCooldown() && ((Boolean)this.getBlockState().getValue(HopperBlock.ENABLED)).booleanValue()) {
            boolean var2 = false;
            if(!this.inventoryEmpty()) {
               var2 = this.ejectItems();
            }

            if(!this.inventoryFull()) {
               var2 |= ((Boolean)supplier.get()).booleanValue();
            }

            if(var2) {
               this.setCooldown(8);
               this.setChanged();
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean inventoryEmpty() {
      for(ItemStack var2 : this.items) {
         if(!var2.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public boolean isEmpty() {
      return this.inventoryEmpty();
   }

   private boolean inventoryFull() {
      for(ItemStack var2 : this.items) {
         if(var2.isEmpty() || var2.getCount() != var2.getMaxStackSize()) {
            return false;
         }
      }

      return true;
   }

   private boolean ejectItems() {
      Container var1 = this.getAttachedContainer();
      if(var1 == null) {
         return false;
      } else {
         Direction var2 = ((Direction)this.getBlockState().getValue(HopperBlock.FACING)).getOpposite();
         if(this.isFullContainer(var1, var2)) {
            return false;
         } else {
            for(int var3 = 0; var3 < this.getContainerSize(); ++var3) {
               if(!this.getItem(var3).isEmpty()) {
                  ItemStack var4 = this.getItem(var3).copy();
                  ItemStack var5 = addItem(this, var1, this.removeItem(var3, 1), var2);
                  if(var5.isEmpty()) {
                     var1.setChanged();
                     return true;
                  }

                  this.setItem(var3, var4);
               }
            }

            return false;
         }
      }
   }

   private static IntStream getSlots(Container container, Direction direction) {
      return container instanceof WorldlyContainer?IntStream.of(((WorldlyContainer)container).getSlotsForFace(direction)):IntStream.range(0, container.getContainerSize());
   }

   private boolean isFullContainer(Container container, Direction direction) {
      return getSlots(container, direction).allMatch((var1) -> {
         ItemStack var2 = container.getItem(var1);
         return var2.getCount() >= var2.getMaxStackSize();
      });
   }

   private static boolean isEmptyContainer(Container container, Direction direction) {
      return getSlots(container, direction).allMatch((var1) -> {
         return container.getItem(var1).isEmpty();
      });
   }

   public static boolean suckInItems(Hopper hopper) {
      Container var1 = getSourceContainer(hopper);
      if(var1 != null) {
         Direction var2 = Direction.DOWN;
         return isEmptyContainer(var1, var2)?false:getSlots(var1, var2).anyMatch((var3) -> {
            return tryTakeInItemFromSlot(hopper, var1, var3, var4);
         });
      } else {
         for(ItemEntity var3 : getItemsAtAndAbove(hopper)) {
            if(addItem(hopper, var3)) {
               return true;
            }
         }

         return false;
      }
   }

   private static boolean tryTakeInItemFromSlot(Hopper hopper, Container container, int var2, Direction direction) {
      ItemStack var4 = container.getItem(var2);
      if(!var4.isEmpty() && canTakeItemFromContainer(container, var4, var2, direction)) {
         ItemStack var5 = var4.copy();
         ItemStack var6 = addItem(container, hopper, container.removeItem(var2, 1), (Direction)null);
         if(var6.isEmpty()) {
            container.setChanged();
            return true;
         }

         container.setItem(var2, var5);
      }

      return false;
   }

   public static boolean addItem(Container container, ItemEntity itemEntity) {
      boolean var2 = false;
      ItemStack var3 = itemEntity.getItem().copy();
      ItemStack var4 = addItem((Container)null, container, var3, (Direction)null);
      if(var4.isEmpty()) {
         var2 = true;
         itemEntity.remove();
      } else {
         itemEntity.setItem(var4);
      }

      return var2;
   }

   public static ItemStack addItem(@Nullable Container var0, Container var1, ItemStack var2, @Nullable Direction direction) {
      if(var1 instanceof WorldlyContainer && direction != null) {
         WorldlyContainer var4 = (WorldlyContainer)var1;
         int[] vars5 = var4.getSlotsForFace(direction);

         for(int var6 = 0; var6 < vars5.length && !var2.isEmpty(); ++var6) {
            var2 = tryMoveInItem(var0, var1, var2, vars5[var6], direction);
         }
      } else {
         int var4 = var1.getContainerSize();

         for(int var5 = 0; var5 < var4 && !var2.isEmpty(); ++var5) {
            var2 = tryMoveInItem(var0, var1, var2, var5, direction);
         }
      }

      return var2;
   }

   private static boolean canPlaceItemInContainer(Container container, ItemStack itemStack, int var2, @Nullable Direction direction) {
      return !container.canPlaceItem(var2, itemStack)?false:!(container instanceof WorldlyContainer) || ((WorldlyContainer)container).canPlaceItemThroughFace(var2, itemStack, direction);
   }

   private static boolean canTakeItemFromContainer(Container container, ItemStack itemStack, int var2, Direction direction) {
      return !(container instanceof WorldlyContainer) || ((WorldlyContainer)container).canTakeItemThroughFace(var2, itemStack, direction);
   }

   private static ItemStack tryMoveInItem(@Nullable Container var0, Container var1, ItemStack var2, int var3, @Nullable Direction direction) {
      ItemStack var5 = var1.getItem(var3);
      if(canPlaceItemInContainer(var1, var2, var3, direction)) {
         boolean var6 = false;
         boolean var7 = var1.isEmpty();
         if(var5.isEmpty()) {
            var1.setItem(var3, var2);
            var2 = ItemStack.EMPTY;
            var6 = true;
         } else if(canMergeItems(var5, var2)) {
            int var8 = var2.getMaxStackSize() - var5.getCount();
            int var9 = Math.min(var2.getCount(), var8);
            var2.shrink(var9);
            var5.grow(var9);
            var6 = var9 > 0;
         }

         if(var6) {
            if(var7 && var1 instanceof HopperBlockEntity) {
               HopperBlockEntity var8 = (HopperBlockEntity)var1;
               if(!var8.isOnCustomCooldown()) {
                  int var9 = 0;
                  if(var0 instanceof HopperBlockEntity) {
                     HopperBlockEntity var10 = (HopperBlockEntity)var0;
                     if(var8.tickedGameTime >= var10.tickedGameTime) {
                        var9 = 1;
                     }
                  }

                  var8.setCooldown(8 - var9);
               }
            }

            var1.setChanged();
         }
      }

      return var2;
   }

   @Nullable
   private Container getAttachedContainer() {
      Direction var1 = (Direction)this.getBlockState().getValue(HopperBlock.FACING);
      return getContainerAt(this.getLevel(), this.worldPosition.relative(var1));
   }

   @Nullable
   public static Container getSourceContainer(Hopper hopper) {
      return getContainerAt(hopper.getLevel(), hopper.getLevelX(), hopper.getLevelY() + 1.0D, hopper.getLevelZ());
   }

   public static List getItemsAtAndAbove(Hopper hopper) {
      return (List)hopper.getSuckShape().toAabbs().stream().flatMap((aABB) -> {
         return hopper.getLevel().getEntitiesOfClass(ItemEntity.class, aABB.move(hopper.getLevelX() - 0.5D, hopper.getLevelY() - 0.5D, hopper.getLevelZ() - 0.5D), EntitySelector.ENTITY_STILL_ALIVE).stream();
      }).collect(Collectors.toList());
   }

   @Nullable
   public static Container getContainerAt(Level level, BlockPos blockPos) {
      return getContainerAt(level, (double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D);
   }

   @Nullable
   public static Container getContainerAt(Level level, double var1, double var3, double var5) {
      Container container = null;
      BlockPos var8 = new BlockPos(var1, var3, var5);
      BlockState var9 = level.getBlockState(var8);
      Block var10 = var9.getBlock();
      if(var10 instanceof WorldlyContainerHolder) {
         container = ((WorldlyContainerHolder)var10).getContainer(var9, level, var8);
      } else if(var10.isEntityBlock()) {
         BlockEntity var11 = level.getBlockEntity(var8);
         if(var11 instanceof Container) {
            container = (Container)var11;
            if(container instanceof ChestBlockEntity && var10 instanceof ChestBlock) {
               container = ChestBlock.getContainer(var9, level, var8, true);
            }
         }
      }

      if(container == null) {
         List<Entity> var11 = level.getEntities((Entity)null, new AABB(var1 - 0.5D, var3 - 0.5D, var5 - 0.5D, var1 + 0.5D, var3 + 0.5D, var5 + 0.5D), EntitySelector.CONTAINER_ENTITY_SELECTOR);
         if(!var11.isEmpty()) {
            container = (Container)var11.get(level.random.nextInt(var11.size()));
         }
      }

      return container;
   }

   private static boolean canMergeItems(ItemStack var0, ItemStack var1) {
      return var0.getItem() != var1.getItem()?false:(var0.getDamageValue() != var1.getDamageValue()?false:(var0.getCount() > var0.getMaxStackSize()?false:ItemStack.tagMatches(var0, var1)));
   }

   public double getLevelX() {
      return (double)this.worldPosition.getX() + 0.5D;
   }

   public double getLevelY() {
      return (double)this.worldPosition.getY() + 0.5D;
   }

   public double getLevelZ() {
      return (double)this.worldPosition.getZ() + 0.5D;
   }

   private void setCooldown(int cooldown) {
      this.cooldownTime = cooldown;
   }

   private boolean isOnCooldown() {
      return this.cooldownTime > 0;
   }

   private boolean isOnCustomCooldown() {
      return this.cooldownTime > 8;
   }

   protected NonNullList getItems() {
      return this.items;
   }

   protected void setItems(NonNullList items) {
      this.items = items;
   }

   public void entityInside(Entity entity) {
      if(entity instanceof ItemEntity) {
         BlockPos var2 = this.getBlockPos();
         if(Shapes.joinIsNotEmpty(Shapes.create(entity.getBoundingBox().move((double)(-var2.getX()), (double)(-var2.getY()), (double)(-var2.getZ()))), this.getSuckShape(), BooleanOp.AND)) {
            this.tryMoveItems(() -> {
               return Boolean.valueOf(addItem(this, (ItemEntity)entity));
            });
         }
      }

   }

   protected AbstractContainerMenu createMenu(int var1, Inventory inventory) {
      return new HopperMenu(var1, inventory, this);
   }
}
