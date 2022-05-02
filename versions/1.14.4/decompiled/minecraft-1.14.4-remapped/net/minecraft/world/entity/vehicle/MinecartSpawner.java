package net.minecraft.world.entity.vehicle;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MinecartSpawner extends AbstractMinecart {
   private final BaseSpawner spawner = new BaseSpawner() {
      public void broadcastEvent(int i) {
         MinecartSpawner.this.level.broadcastEntityEvent(MinecartSpawner.this, (byte)i);
      }

      public Level getLevel() {
         return MinecartSpawner.this.level;
      }

      public BlockPos getPos() {
         return new BlockPos(MinecartSpawner.this);
      }
   };

   public MinecartSpawner(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public MinecartSpawner(Level level, double var2, double var4, double var6) {
      super(EntityType.SPAWNER_MINECART, level, var2, var4, var6);
   }

   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.SPAWNER;
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.SPAWNER.defaultBlockState();
   }

   protected void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.spawner.load(compoundTag);
   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      this.spawner.save(compoundTag);
   }

   public void handleEntityEvent(byte b) {
      this.spawner.onEventTriggered(b);
   }

   public void tick() {
      super.tick();
      this.spawner.tick();
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }
}
