package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnerBlockEntity extends BlockEntity implements TickableBlockEntity {
   private final BaseSpawner spawner = new BaseSpawner() {
      public void broadcastEvent(int i) {
         SpawnerBlockEntity.this.level.blockEvent(SpawnerBlockEntity.this.worldPosition, Blocks.SPAWNER, i, 0);
      }

      public Level getLevel() {
         return SpawnerBlockEntity.this.level;
      }

      public BlockPos getPos() {
         return SpawnerBlockEntity.this.worldPosition;
      }

      public void setNextSpawnData(SpawnData nextSpawnData) {
         super.setNextSpawnData(nextSpawnData);
         if(this.getLevel() != null) {
            BlockState var2 = this.getLevel().getBlockState(this.getPos());
            this.getLevel().sendBlockUpdated(SpawnerBlockEntity.this.worldPosition, var2, var2, 4);
         }

      }
   };

   public SpawnerBlockEntity() {
      super(BlockEntityType.MOB_SPAWNER);
   }

   public void load(CompoundTag compoundTag) {
      super.load(compoundTag);
      this.spawner.load(compoundTag);
   }

   public CompoundTag save(CompoundTag compoundTag) {
      super.save(compoundTag);
      this.spawner.save(compoundTag);
      return compoundTag;
   }

   public void tick() {
      this.spawner.tick();
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 1, this.getUpdateTag());
   }

   public CompoundTag getUpdateTag() {
      CompoundTag compoundTag = this.save(new CompoundTag());
      compoundTag.remove("SpawnPotentials");
      return compoundTag;
   }

   public boolean triggerEvent(int var1, int var2) {
      return this.spawner.onEventTriggered(var1)?true:super.triggerEvent(var1, var2);
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public BaseSpawner getSpawner() {
      return this.spawner;
   }
}
