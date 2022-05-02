package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CoralPlantBlock extends BaseCoralPlantTypeBlock {
   private final Block deadBlock;
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);

   protected CoralPlantBlock(Block deadBlock, Block.Properties block$Properties) {
      super(block$Properties);
      this.deadBlock = deadBlock;
   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      this.tryScheduleDieTick(var1, level, blockPos);
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!scanForWater(blockState, level, blockPos)) {
         level.setBlock(blockPos, (BlockState)this.deadBlock.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)), 2);
      }

   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(direction == Direction.DOWN && !var1.canSurvive(levelAccessor, var5)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         this.tryScheduleDieTick(var1, levelAccessor, var5);
         if(((Boolean)var1.getValue(WATERLOGGED)).booleanValue()) {
            levelAccessor.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
         }

         return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
      }
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }
}
