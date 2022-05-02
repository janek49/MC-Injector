package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class BlockPlaceContext extends UseOnContext {
   private final BlockPos relativePos;
   protected boolean replaceClicked;

   public BlockPlaceContext(UseOnContext useOnContext) {
      this(useOnContext.getLevel(), useOnContext.getPlayer(), useOnContext.getHand(), useOnContext.getItemInHand(), useOnContext.hitResult);
   }

   protected BlockPlaceContext(Level level, @Nullable Player player, InteractionHand interactionHand, ItemStack itemStack, BlockHitResult blockHitResult) {
      super(level, player, interactionHand, itemStack, blockHitResult);
      this.replaceClicked = true;
      this.relativePos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
      this.replaceClicked = level.getBlockState(blockHitResult.getBlockPos()).canBeReplaced(this);
   }

   public static BlockPlaceContext at(BlockPlaceContext var0, BlockPos blockPos, Direction direction) {
      return new BlockPlaceContext(var0.getLevel(), var0.getPlayer(), var0.getHand(), var0.getItemInHand(), new BlockHitResult(new Vec3((double)blockPos.getX() + 0.5D + (double)direction.getStepX() * 0.5D, (double)blockPos.getY() + 0.5D + (double)direction.getStepY() * 0.5D, (double)blockPos.getZ() + 0.5D + (double)direction.getStepZ() * 0.5D), direction, blockPos, false));
   }

   public BlockPos getClickedPos() {
      return this.replaceClicked?super.getClickedPos():this.relativePos;
   }

   public boolean canPlace() {
      return this.replaceClicked || this.getLevel().getBlockState(this.getClickedPos()).canBeReplaced(this);
   }

   public boolean replacingClickedOnBlock() {
      return this.replaceClicked;
   }

   public Direction getNearestLookingDirection() {
      return Direction.orderedByNearest(this.player)[0];
   }

   public Direction[] getNearestLookingDirections() {
      Direction[] directions = Direction.orderedByNearest(this.player);
      if(this.replaceClicked) {
         return directions;
      } else {
         Direction var2 = this.getClickedFace();

         int var3;
         for(var3 = 0; var3 < directions.length && directions[var3] != var2.getOpposite(); ++var3) {
            ;
         }

         if(var3 > 0) {
            System.arraycopy(directions, 0, directions, 1, var3);
            directions[0] = var2.getOpposite();
         }

         return directions;
      }
   }
}
