package net.minecraft.world.phys.shapes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EntityCollisionContext implements CollisionContext {
   protected static final CollisionContext EMPTY = new EntityCollisionContext(false, -1.7976931348623157E308D, item) {
      public boolean isAbove(VoxelShape voxelShape, BlockPos blockPos, boolean var3) {
         return var3;
      }
   };
   private final boolean sneaking;
   private final double entityBottom;
   private final Item heldItem;

   protected EntityCollisionContext(boolean sneaking, double entityBottom, Item heldItem) {
      this.sneaking = sneaking;
      this.entityBottom = entityBottom;
      this.heldItem = heldItem;
   }

   @Deprecated
   protected EntityCollisionContext(Entity entity) {
      this(entity.isSneaking(), entity.getBoundingBox().minY, entity instanceof LivingEntity?((LivingEntity)entity).getMainHandItem().getItem():Items.AIR);
   }

   public boolean isHoldingItem(Item item) {
      return this.heldItem == item;
   }

   public boolean isSneaking() {
      return this.sneaking;
   }

   public boolean isAbove(VoxelShape voxelShape, BlockPos blockPos, boolean var3) {
      return this.entityBottom > (double)blockPos.getY() + voxelShape.max(Direction.Axis.Y) - 9.999999747378752E-6D;
   }
}
