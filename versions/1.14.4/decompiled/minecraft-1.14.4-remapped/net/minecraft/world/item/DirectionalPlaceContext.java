package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class DirectionalPlaceContext extends BlockPlaceContext {
   private final Direction direction;

   public DirectionalPlaceContext(Level level, BlockPos blockPos, Direction direction, ItemStack itemStack, Direction var5) {
      super(level, (Player)null, InteractionHand.MAIN_HAND, itemStack, new BlockHitResult(new Vec3((double)blockPos.getX() + 0.5D, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5D), var5, blockPos, false));
      this.direction = direction;
   }

   public BlockPos getClickedPos() {
      return this.hitResult.getBlockPos();
   }

   public boolean canPlace() {
      return this.level.getBlockState(this.hitResult.getBlockPos()).canBeReplaced(this);
   }

   public boolean replacingClickedOnBlock() {
      return this.canPlace();
   }

   public Direction getNearestLookingDirection() {
      return Direction.DOWN;
   }

   public Direction[] getNearestLookingDirections() {
      switch(this.direction) {
      case DOWN:
      default:
         return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP};
      case UP:
         return new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
      case NORTH:
         return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.SOUTH};
      case SOUTH:
         return new Direction[]{Direction.DOWN, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.NORTH};
      case WEST:
         return new Direction[]{Direction.DOWN, Direction.WEST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.EAST};
      case EAST:
         return new Direction[]{Direction.DOWN, Direction.EAST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.WEST};
      }
   }

   public Direction getHorizontalDirection() {
      return this.direction.getAxis() == Direction.Axis.Y?Direction.NORTH:this.direction;
   }

   public boolean isSneaking() {
      return false;
   }

   public float getRotation() {
      return (float)(this.direction.get2DDataValue() * 90);
   }
}
