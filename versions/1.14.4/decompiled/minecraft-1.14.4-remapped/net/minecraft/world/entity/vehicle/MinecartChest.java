package net.minecraft.world.entity.vehicle;

import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;

public class MinecartChest extends AbstractMinecartContainer {
   public MinecartChest(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public MinecartChest(Level level, double var2, double var4, double var6) {
      super(EntityType.CHEST_MINECART, var2, var4, var6, level);
   }

   public void destroy(DamageSource damageSource) {
      super.destroy(damageSource);
      if(this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         this.spawnAtLocation(Blocks.CHEST);
      }

   }

   public int getContainerSize() {
      return 27;
   }

   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.CHEST;
   }

   public BlockState getDefaultDisplayBlockState() {
      return (BlockState)Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH);
   }

   public int getDefaultDisplayOffset() {
      return 8;
   }

   public AbstractContainerMenu createMenu(int var1, Inventory inventory) {
      return ChestMenu.threeRows(var1, inventory, this);
   }
}
