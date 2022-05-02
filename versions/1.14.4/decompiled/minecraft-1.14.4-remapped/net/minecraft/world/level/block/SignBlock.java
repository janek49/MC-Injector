package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class SignBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

   protected SignBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(((Boolean)var1.getValue(WATERLOGGED)).booleanValue()) {
         levelAccessor.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
      }

      return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   public boolean hasCustomBreakingProgress(BlockState blockState) {
      return true;
   }

   public boolean isPossibleToRespawnInThis() {
      return true;
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new SignBlockEntity();
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(level.isClientSide) {
         return true;
      } else {
         BlockEntity var7 = level.getBlockEntity(blockPos);
         if(var7 instanceof SignBlockEntity) {
            SignBlockEntity var8 = (SignBlockEntity)var7;
            ItemStack var9 = player.getItemInHand(interactionHand);
            if(var9.getItem() instanceof DyeItem && player.abilities.mayBuild) {
               boolean var10 = var8.setColor(((DyeItem)var9.getItem()).getDyeColor());
               if(var10 && !player.isCreative()) {
                  var9.shrink(1);
               }
            }

            return var8.executeClickCommands(player);
         } else {
            return false;
         }
      }
   }

   public FluidState getFluidState(BlockState blockState) {
      return ((Boolean)blockState.getValue(WATERLOGGED)).booleanValue()?Fluids.WATER.getSource(false):super.getFluidState(blockState);
   }
}
