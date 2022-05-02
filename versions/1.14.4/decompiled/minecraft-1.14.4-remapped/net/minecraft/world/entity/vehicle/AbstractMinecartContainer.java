package net.minecraft.world.entity.vehicle;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public abstract class AbstractMinecartContainer extends AbstractMinecart implements Container, MenuProvider {
   private NonNullList itemStacks = NonNullList.withSize(36, ItemStack.EMPTY);
   private boolean dropEquipment = true;
   @Nullable
   private ResourceLocation lootTable;
   private long lootTableSeed;

   protected AbstractMinecartContainer(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected AbstractMinecartContainer(EntityType entityType, double var2, double var4, double var6, Level level) {
      super(entityType, level, var2, var4, var6);
   }

   public void destroy(DamageSource damageSource) {
      super.destroy(damageSource);
      if(this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         Containers.dropContents(this.level, (Entity)this, (Container)this);
      }

   }

   public boolean isEmpty() {
      for(ItemStack var2 : this.itemStacks) {
         if(!var2.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getItem(int i) {
      this.unpackLootTable((Player)null);
      return (ItemStack)this.itemStacks.get(i);
   }

   public ItemStack removeItem(int var1, int var2) {
      this.unpackLootTable((Player)null);
      return ContainerHelper.removeItem(this.itemStacks, var1, var2);
   }

   public ItemStack removeItemNoUpdate(int i) {
      this.unpackLootTable((Player)null);
      ItemStack itemStack = (ItemStack)this.itemStacks.get(i);
      if(itemStack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.itemStacks.set(i, ItemStack.EMPTY);
         return itemStack;
      }
   }

   public void setItem(int var1, ItemStack itemStack) {
      this.unpackLootTable((Player)null);
      this.itemStacks.set(var1, itemStack);
      if(!itemStack.isEmpty() && itemStack.getCount() > this.getMaxStackSize()) {
         itemStack.setCount(this.getMaxStackSize());
      }

   }

   public boolean setSlot(int var1, ItemStack itemStack) {
      if(var1 >= 0 && var1 < this.getContainerSize()) {
         this.setItem(var1, itemStack);
         return true;
      } else {
         return false;
      }
   }

   public void setChanged() {
   }

   public boolean stillValid(Player player) {
      return this.removed?false:player.distanceToSqr(this) <= 64.0D;
   }

   @Nullable
   public Entity changeDimension(DimensionType dimensionType) {
      this.dropEquipment = false;
      return super.changeDimension(dimensionType);
   }

   public void remove() {
      if(!this.level.isClientSide && this.dropEquipment) {
         Containers.dropContents(this.level, (Entity)this, (Container)this);
      }

      super.remove();
   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      if(this.lootTable != null) {
         compoundTag.putString("LootTable", this.lootTable.toString());
         if(this.lootTableSeed != 0L) {
            compoundTag.putLong("LootTableSeed", this.lootTableSeed);
         }
      } else {
         ContainerHelper.saveAllItems(compoundTag, this.itemStacks);
      }

   }

   protected void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if(compoundTag.contains("LootTable", 8)) {
         this.lootTable = new ResourceLocation(compoundTag.getString("LootTable"));
         this.lootTableSeed = compoundTag.getLong("LootTableSeed");
      } else {
         ContainerHelper.loadAllItems(compoundTag, this.itemStacks);
      }

   }

   public boolean interact(Player player, InteractionHand interactionHand) {
      player.openMenu(this);
      return true;
   }

   protected void applyNaturalSlowdown() {
      float var1 = 0.98F;
      if(this.lootTable == null) {
         int var2 = 15 - AbstractContainerMenu.getRedstoneSignalFromContainer(this);
         var1 += (float)var2 * 0.001F;
      }

      this.setDeltaMovement(this.getDeltaMovement().multiply((double)var1, 0.0D, (double)var1));
   }

   public void unpackLootTable(@Nullable Player player) {
      if(this.lootTable != null && this.level.getServer() != null) {
         LootTable var2 = this.level.getServer().getLootTables().get(this.lootTable);
         this.lootTable = null;
         LootContext.Builder var3 = (new LootContext.Builder((ServerLevel)this.level)).withParameter(LootContextParams.BLOCK_POS, new BlockPos(this)).withOptionalRandomSeed(this.lootTableSeed);
         if(player != null) {
            var3.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
         }

         var2.fill(this, var3.create(LootContextParamSets.CHEST));
      }

   }

   public void clearContent() {
      this.unpackLootTable((Player)null);
      this.itemStacks.clear();
   }

   public void setLootTable(ResourceLocation lootTable, long lootTableSeed) {
      this.lootTable = lootTable;
      this.lootTableSeed = lootTableSeed;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int var1, Inventory inventory, Player player) {
      if(this.lootTable != null && player.isSpectator()) {
         return null;
      } else {
         this.unpackLootTable(inventory.player);
         return this.createMenu(var1, inventory);
      }
   }

   protected abstract AbstractContainerMenu createMenu(int var1, Inventory var2);
}
