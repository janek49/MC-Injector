package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CropBlock extends BushBlock implements BonemealableBlock {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
   private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

   protected CropBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(this.getAgeProperty(), Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE_BY_AGE[((Integer)blockState.getValue(this.getAgeProperty())).intValue()];
   }

   protected boolean mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return blockState.getBlock() == Blocks.FARMLAND;
   }

   public IntegerProperty getAgeProperty() {
      return AGE;
   }

   public int getMaxAge() {
      return 7;
   }

   protected int getAge(BlockState blockState) {
      return ((Integer)blockState.getValue(this.getAgeProperty())).intValue();
   }

   public BlockState getStateForAge(int i) {
      return (BlockState)this.defaultBlockState().setValue(this.getAgeProperty(), Integer.valueOf(i));
   }

   public boolean isMaxAge(BlockState blockState) {
      return ((Integer)blockState.getValue(this.getAgeProperty())).intValue() >= this.getMaxAge();
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      super.tick(blockState, level, blockPos, random);
      if(level.getRawBrightness(blockPos, 0) >= 9) {
         int var5 = this.getAge(blockState);
         if(var5 < this.getMaxAge()) {
            float var6 = getGrowthSpeed(this, level, blockPos);
            if(random.nextInt((int)(25.0F / var6) + 1) == 0) {
               level.setBlock(blockPos, this.getStateForAge(var5 + 1), 2);
            }
         }
      }

   }

   public void growCrops(Level level, BlockPos blockPos, BlockState blockState) {
      int var4 = this.getAge(blockState) + this.getBonemealAgeIncrease(level);
      int var5 = this.getMaxAge();
      if(var4 > var5) {
         var4 = var5;
      }

      level.setBlock(blockPos, this.getStateForAge(var4), 2);
   }

   protected int getBonemealAgeIncrease(Level level) {
      return Mth.nextInt(level.random, 2, 5);
   }

   protected static float getGrowthSpeed(Block block, BlockGetter blockGetter, BlockPos blockPos) {
      float var3 = 1.0F;
      BlockPos var4 = blockPos.below();

      for(int var5 = -1; var5 <= 1; ++var5) {
         for(int var6 = -1; var6 <= 1; ++var6) {
            float var7 = 0.0F;
            BlockState var8 = blockGetter.getBlockState(var4.offset(var5, 0, var6));
            if(var8.getBlock() == Blocks.FARMLAND) {
               var7 = 1.0F;
               if(((Integer)var8.getValue(FarmBlock.MOISTURE)).intValue() > 0) {
                  var7 = 3.0F;
               }
            }

            if(var5 != 0 || var6 != 0) {
               var7 /= 4.0F;
            }

            var3 += var7;
         }
      }

      BlockPos var5 = blockPos.north();
      BlockPos var6 = blockPos.south();
      BlockPos var7 = blockPos.west();
      BlockPos var8 = blockPos.east();
      boolean var9 = block == blockGetter.getBlockState(var7).getBlock() || block == blockGetter.getBlockState(var8).getBlock();
      boolean var10 = block == blockGetter.getBlockState(var5).getBlock() || block == blockGetter.getBlockState(var6).getBlock();
      if(var9 && var10) {
         var3 /= 2.0F;
      } else {
         boolean var11 = block == blockGetter.getBlockState(var7.north()).getBlock() || block == blockGetter.getBlockState(var8.north()).getBlock() || block == blockGetter.getBlockState(var8.south()).getBlock() || block == blockGetter.getBlockState(var7.south()).getBlock();
         if(var11) {
            var3 /= 2.0F;
         }
      }

      return var3;
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      return (levelReader.getRawBrightness(blockPos, 0) >= 8 || levelReader.canSeeSky(blockPos)) && super.canSurvive(blockState, levelReader, blockPos);
   }

   public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      if(entity instanceof Ravager && level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
         level.destroyBlock(blockPos, true);
      }

      super.entityInside(blockState, level, blockPos, entity);
   }

   protected ItemLike getBaseSeedId() {
      return Items.WHEAT_SEEDS;
   }

   public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
      return new ItemStack(this.getBaseSeedId());
   }

   public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, boolean var4) {
      return !this.isMaxAge(blockState);
   }

   public boolean isBonemealSuccess(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      return true;
   }

   public void performBonemeal(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      this.growCrops(level, blockPos, blockState);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{AGE});
   }
}
