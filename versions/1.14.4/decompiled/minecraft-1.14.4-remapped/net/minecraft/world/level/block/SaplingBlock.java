package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SaplingBlock extends BushBlock implements BonemealableBlock {
   public static final IntegerProperty STAGE = BlockStateProperties.STAGE;
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
   private final AbstractTreeGrower treeGrower;

   protected SaplingBlock(AbstractTreeGrower treeGrower, Block.Properties block$Properties) {
      super(block$Properties);
      this.treeGrower = treeGrower;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(STAGE, Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      super.tick(blockState, level, blockPos, random);
      if(level.getMaxLocalRawBrightness(blockPos.above()) >= 9 && random.nextInt(7) == 0) {
         this.advanceTree(level, blockPos, blockState, random);
      }

   }

   public void advanceTree(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, Random random) {
      if(((Integer)blockState.getValue(STAGE)).intValue() == 0) {
         levelAccessor.setBlock(blockPos, (BlockState)blockState.cycle(STAGE), 4);
      } else {
         this.treeGrower.growTree(levelAccessor, blockPos, blockState, random);
      }

   }

   public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, boolean var4) {
      return true;
   }

   public boolean isBonemealSuccess(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      return (double)level.random.nextFloat() < 0.45D;
   }

   public void performBonemeal(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      this.advanceTree(level, blockPos, blockState, random);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{STAGE});
   }
}
