package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.AABB;

public class ChestBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity, TickableBlockEntity {
   private NonNullList items;
   protected float openness;
   protected float oOpenness;
   protected int openCount;
   private int tickInterval;

   protected ChestBlockEntity(BlockEntityType blockEntityType) {
      super(blockEntityType);
      this.items = NonNullList.withSize(27, ItemStack.EMPTY);
   }

   public ChestBlockEntity() {
      this(BlockEntityType.CHEST);
   }

   public int getContainerSize() {
      return 27;
   }

   public boolean isEmpty() {
      for(ItemStack var2 : this.items) {
         if(!var2.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   protected Component getDefaultName() {
      return new TranslatableComponent("container.chest", new Object[0]);
   }

   public void load(CompoundTag compoundTag) {
      super.load(compoundTag);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if(!this.tryLoadLootTable(compoundTag)) {
         ContainerHelper.loadAllItems(compoundTag, this.items);
      }

   }

   public CompoundTag save(CompoundTag compoundTag) {
      super.save(compoundTag);
      if(!this.trySaveLootTable(compoundTag)) {
         ContainerHelper.saveAllItems(compoundTag, this.items);
      }

      return compoundTag;
   }

   public void tick() {
      int var1 = this.worldPosition.getX();
      int var2 = this.worldPosition.getY();
      int var3 = this.worldPosition.getZ();
      ++this.tickInterval;
      this.openCount = getOpenCount(this.level, this, this.tickInterval, var1, var2, var3, this.openCount);
      this.oOpenness = this.openness;
      float var4 = 0.1F;
      if(this.openCount > 0 && this.openness == 0.0F) {
         this.playSound(SoundEvents.CHEST_OPEN);
      }

      if(this.openCount == 0 && this.openness > 0.0F || this.openCount > 0 && this.openness < 1.0F) {
         float var5 = this.openness;
         if(this.openCount > 0) {
            this.openness += 0.1F;
         } else {
            this.openness -= 0.1F;
         }

         if(this.openness > 1.0F) {
            this.openness = 1.0F;
         }

         float var6 = 0.5F;
         if(this.openness < 0.5F && var5 >= 0.5F) {
            this.playSound(SoundEvents.CHEST_CLOSE);
         }

         if(this.openness < 0.0F) {
            this.openness = 0.0F;
         }
      }

   }

   public static int getOpenCount(Level level, BaseContainerBlockEntity baseContainerBlockEntity, int var2, int var3, int var4, int var5, int var6) {
      if(!level.isClientSide && var6 != 0 && (var2 + var3 + var4 + var5) % 200 == 0) {
         var6 = getOpenCount(level, baseContainerBlockEntity, var3, var4, var5);
      }

      return var6;
   }

   public static int getOpenCount(Level level, BaseContainerBlockEntity baseContainerBlockEntity, int var2, int var3, int var4) {
      int var5 = 0;
      float var6 = 5.0F;

      for(Player var9 : level.getEntitiesOfClass(Player.class, new AABB((double)((float)var2 - 5.0F), (double)((float)var3 - 5.0F), (double)((float)var4 - 5.0F), (double)((float)(var2 + 1) + 5.0F), (double)((float)(var3 + 1) + 5.0F), (double)((float)(var4 + 1) + 5.0F)))) {
         if(var9.containerMenu instanceof ChestMenu) {
            Container var10 = ((ChestMenu)var9.containerMenu).getContainer();
            if(var10 == baseContainerBlockEntity || var10 instanceof CompoundContainer && ((CompoundContainer)var10).contains(baseContainerBlockEntity)) {
               ++var5;
            }
         }
      }

      return var5;
   }

   private void playSound(SoundEvent soundEvent) {
      ChestType var2 = (ChestType)this.getBlockState().getValue(ChestBlock.TYPE);
      if(var2 != ChestType.LEFT) {
         double var3 = (double)this.worldPosition.getX() + 0.5D;
         double var5 = (double)this.worldPosition.getY() + 0.5D;
         double var7 = (double)this.worldPosition.getZ() + 0.5D;
         if(var2 == ChestType.RIGHT) {
            Direction var9 = ChestBlock.getConnectedDirection(this.getBlockState());
            var3 += (double)var9.getStepX() * 0.5D;
            var7 += (double)var9.getStepZ() * 0.5D;
         }

         this.level.playSound((Player)null, var3, var5, var7, soundEvent, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
      }
   }

   public boolean triggerEvent(int var1, int openCount) {
      if(var1 == 1) {
         this.openCount = openCount;
         return true;
      } else {
         return super.triggerEvent(var1, openCount);
      }
   }

   public void startOpen(Player player) {
      if(!player.isSpectator()) {
         if(this.openCount < 0) {
            this.openCount = 0;
         }

         ++this.openCount;
         this.signalOpenCount();
      }

   }

   public void stopOpen(Player player) {
      if(!player.isSpectator()) {
         --this.openCount;
         this.signalOpenCount();
      }

   }

   protected void signalOpenCount() {
      Block var1 = this.getBlockState().getBlock();
      if(var1 instanceof ChestBlock) {
         this.level.blockEvent(this.worldPosition, var1, 1, this.openCount);
         this.level.updateNeighborsAt(this.worldPosition, var1);
      }

   }

   protected NonNullList getItems() {
      return this.items;
   }

   protected void setItems(NonNullList items) {
      this.items = items;
   }

   public float getOpenNess(float f) {
      return Mth.lerp(f, this.oOpenness, this.openness);
   }

   public static int getOpenCount(BlockGetter blockGetter, BlockPos blockPos) {
      BlockState var2 = blockGetter.getBlockState(blockPos);
      if(var2.getBlock().isEntityBlock()) {
         BlockEntity var3 = blockGetter.getBlockEntity(blockPos);
         if(var3 instanceof ChestBlockEntity) {
            return ((ChestBlockEntity)var3).openCount;
         }
      }

      return 0;
   }

   public static void swapContents(ChestBlockEntity var0, ChestBlockEntity var1) {
      NonNullList<ItemStack> var2 = var0.getItems();
      var0.setItems(var1.getItems());
      var1.setItems(var2);
   }

   protected AbstractContainerMenu createMenu(int var1, Inventory inventory) {
      return ChestMenu.threeRows(var1, inventory, this);
   }
}
