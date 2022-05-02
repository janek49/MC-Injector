package net.minecraft.world.phys.shapes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface CollisionContext {
   static default CollisionContext empty() {
      return EntityCollisionContext.EMPTY;
   }

   static default CollisionContext of(Entity entity) {
      return new EntityCollisionContext(entity);
   }

   boolean isSneaking();

   boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3);

   boolean isHoldingItem(Item var1);
}
