package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StemBlock extends BushBlock implements BonemealableBlock {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
   protected static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(7.0D, 0.0D, 7.0D, 9.0D, 2.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 4.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 6.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 8.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 12.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D)};
   private final StemGrownBlock fruit;

   protected StemBlock(StemGrownBlock fruit, Block.Properties block$Properties) {
      super(block$Properties);
      this.fruit = fruit;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE_BY_AGE[((Integer)blockState.getValue(AGE)).intValue()];
   }

   protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return blockState.getBlock() == Blocks.FARMLAND;
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      super.tick(blockState, level, blockPos, random);
      if(level.getRawBrightness(blockPos, 0) >= 9) {
         float var5 = CropBlock.getGrowthSpeed(this, level, blockPos);
         if(random.nextInt((int)(25.0F / var5) + 1) == 0) {
            int var6 = ((Integer)blockState.getValue(AGE)).intValue();
            if(var6 < 7) {
               blockState = (BlockState)blockState.setValue(AGE, Integer.valueOf(var6 + 1));
               level.setBlock(blockPos, blockState, 2);
            } else {
               Direction var7 = Direction.Plane.HORIZONTAL.getRandomDirection(random);
               BlockPos var8 = blockPos.relative(var7);
               Block var9 = level.getBlockState(var8.below()).getBlock();
               if(level.getBlockState(var8).isAir() && (var9 == Blocks.FARMLAND || var9 == Blocks.DIRT || var9 == Blocks.COARSE_DIRT || var9 == Blocks.PODZOL || var9 == Blocks.GRASS_BLOCK)) {
                  level.setBlockAndUpdate(var8, this.fruit.defaultBlockState());
                  level.setBlockAndUpdate(blockPos, (BlockState)this.fruit.getAttachedStem().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, var7));
               }
            }
         }

      }
   }

   @Nullable
   protected Item getSeedItem() {
      return this.fruit == Blocks.PUMPKIN?Items.PUMPKIN_SEEDS:(this.fruit == Blocks.MELON?Items.MELON_SEEDS:null);
   }

   public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
      Item var4 = this.getSeedItem();
      return var4 == null?ItemStack.EMPTY:new ItemStack(var4);
   }

   public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, boolean var4) {
      return ((Integer)blockState.getValue(AGE)).intValue() != 7;
   }

   public boolean isBonemealSuccess(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      return true;
   }

   public void performBonemeal(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      int var5 = Math.min(7, ((Integer)blockState.getValue(AGE)).intValue() + Mth.nextInt(level.random, 2, 5));
      BlockState var6 = (BlockState)blockState.setValue(AGE, Integer.valueOf(var5));
      level.setBlock(blockPos, var6, 2);
      if(var5 == 7) {
         var6.tick(level, blockPos, level.random);
      }

   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{AGE});
   }

   public StemGrownBlock getFruit() {
      return this.fruit;
   }
}
