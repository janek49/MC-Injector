package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EndPortalBlock extends BaseEntityBlock {
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

   protected EndPortalBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new TheEndPortalBlockEntity();
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      if(!level.isClientSide && !entity.isPassenger() && !entity.isVehicle() && entity.canChangeDimensions() && Shapes.joinIsNotEmpty(Shapes.create(entity.getBoundingBox().move((double)(-blockPos.getX()), (double)(-blockPos.getY()), (double)(-blockPos.getZ()))), blockState.getShape(level, blockPos), BooleanOp.AND)) {
         entity.changeDimension(level.dimension.getType() == DimensionType.THE_END?DimensionType.OVERWORLD:DimensionType.THE_END);
      }

   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      double var5 = (double)((float)blockPos.getX() + random.nextFloat());
      double var7 = (double)((float)blockPos.getY() + 0.8F);
      double var9 = (double)((float)blockPos.getZ() + random.nextFloat());
      double var11 = 0.0D;
      double var13 = 0.0D;
      double var15 = 0.0D;
      level.addParticle(ParticleTypes.SMOKE, var5, var7, var9, 0.0D, 0.0D, 0.0D);
   }

   public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
      return ItemStack.EMPTY;
   }
}
