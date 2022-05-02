package net.minecraft.world.entity.ai.behavior;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.PositionWrapper;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.phys.Vec3;

public class EntityPosWrapper implements PositionWrapper {
   private final Entity entity;

   public EntityPosWrapper(Entity entity) {
      this.entity = entity;
   }

   public BlockPos getPos() {
      return new BlockPos(this.entity);
   }

   public Vec3 getLookAtPos() {
      return new Vec3(this.entity.x, this.entity.y + (double)this.entity.getEyeHeight(), this.entity.z);
   }

   public boolean isVisible(LivingEntity livingEntity) {
      Optional<List<LivingEntity>> var2 = livingEntity.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES);
      return this.entity.isAlive() && var2.isPresent() && ((List)var2.get()).contains(this.entity);
   }

   public String toString() {
      return "EntityPosWrapper for " + this.entity;
   }
}
