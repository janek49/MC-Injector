package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FenceBlock extends CrossCollisionBlock {
   private final VoxelShape[] occlusionByIndex;

   public FenceBlock(Block.Properties block$Properties) {
      super(2.0F, 2.0F, 16.0F, 16.0F, 24.0F, block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, Boolean.valueOf(false))).setValue(EAST, Boolean.valueOf(false))).setValue(SOUTH, Boolean.valueOf(false))).setValue(WEST, Boolean.valueOf(false))).setValue(WATERLOGGED, Boolean.valueOf(false)));
      this.occlusionByIndex = this.makeShapes(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
   }

   public VoxelShape getOcclusionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return this.occlusionByIndex[this.getAABBIndex(blockState)];
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }

   public boolean connectsTo(BlockState blockState, boolean var2, Direction direction) {
      Block var4 = blockState.getBlock();
      boolean var5 = var4.is(BlockTags.FENCES) && blockState.getMaterial() == this.material;
      boolean var6 = var4 instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(blockState, direction);
      return !isExceptionForConnection(var4) && var2 || var5 || var6;
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(!level.isClientSide) {
         return LeadItem.bindPlayerMobs(player, level, blockPos);
      } else {
         ItemStack var7 = player.getItemInHand(interactionHand);
         return var7.getItem() == Items.LEAD || var7.isEmpty();
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      BlockGetter var2 = blockPlaceContext.getLevel();
      BlockPos var3 = blockPlaceContext.getClickedPos();
      FluidState var4 = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
      BlockPos var5 = var3.north();
      BlockPos var6 = var3.east();
      BlockPos var7 = var3.south();
      BlockPos var8 = var3.west();
      BlockState var9 = var2.getBlockState(var5);
      BlockState var10 = var2.getBlockState(var6);
      BlockState var11 = var2.getBlockState(var7);
      BlockState var12 = var2.getBlockState(var8);
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)super.getStateForPlacement(blockPlaceContext).setValue(NORTH, Boolean.valueOf(this.connectsTo(var9, var9.isFaceSturdy(var2, var5, Direction.SOUTH), Direction.SOUTH)))).setValue(EAST, Boolean.valueOf(this.connectsTo(var10, var10.isFaceSturdy(var2, var6, Direction.WEST), Direction.WEST)))).setValue(SOUTH, Boolean.valueOf(this.connectsTo(var11, var11.isFaceSturdy(var2, var7, Direction.NORTH), Direction.NORTH)))).setValue(WEST, Boolean.valueOf(this.connectsTo(var12, var12.isFaceSturdy(var2, var8, Direction.EAST), Direction.EAST)))).setValue(WATERLOGGED, Boolean.valueOf(var4.getType() == Fluids.WATER));
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(((Boolean)var1.getValue(WATERLOGGED)).booleanValue()) {
         levelAccessor.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
      }

      return direction.getAxis().getPlane() == Direction.Plane.HORIZONTAL?(BlockState)var1.setValue((Property)PROPERTY_BY_DIRECTION.get(direction), Boolean.valueOf(this.connectsTo(var3, var3.isFaceSturdy(levelAccessor, var6, direction.getOpposite()), direction.getOpposite()))):super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{NORTH, EAST, WEST, SOUTH, WATERLOGGED});
   }
}
