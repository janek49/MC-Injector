package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FarmBlock extends Block {
   public static final IntegerProperty MOISTURE = BlockStateProperties.MOISTURE;
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);

   protected FarmBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(MOISTURE, Integer.valueOf(0)));
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(direction == Direction.UP && !var1.canSurvive(levelAccessor, var5)) {
         levelAccessor.getBlockTicks().scheduleTick(var5, this, 1);
      }

      return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      BlockState blockState = levelReader.getBlockState(blockPos.above());
      return !blockState.getMaterial().isSolid() || blockState.getBlock() instanceof FenceGateBlock;
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return !this.defaultBlockState().canSurvive(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos())?Blocks.DIRT.defaultBlockState():super.getStateForPlacement(blockPlaceContext);
   }

   public boolean useShapeForLightOcclusion(BlockState blockState) {
      return true;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!blockState.canSurvive(level, blockPos)) {
         turnToDirt(blockState, level, blockPos);
      } else {
         int var5 = ((Integer)blockState.getValue(MOISTURE)).intValue();
         if(!isNearWater(level, blockPos) && !level.isRainingAt(blockPos.above())) {
            if(var5 > 0) {
               level.setBlock(blockPos, (BlockState)blockState.setValue(MOISTURE, Integer.valueOf(var5 - 1)), 2);
            } else if(!isUnderCrops(level, blockPos)) {
               turnToDirt(blockState, level, blockPos);
            }
         } else if(var5 < 7) {
            level.setBlock(blockPos, (BlockState)blockState.setValue(MOISTURE, Integer.valueOf(7)), 2);
         }

      }
   }

   public void fallOn(Level level, BlockPos blockPos, Entity entity, float var4) {
      if(!level.isClientSide && level.random.nextFloat() < var4 - 0.5F && entity instanceof LivingEntity && (entity instanceof Player || level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) && entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight() > 0.512F) {
         turnToDirt(level.getBlockState(blockPos), level, blockPos);
      }

      super.fallOn(level, blockPos, entity, var4);
   }

   public static void turnToDirt(BlockState blockState, Level level, BlockPos blockPos) {
      level.setBlockAndUpdate(blockPos, pushEntitiesUp(blockState, Blocks.DIRT.defaultBlockState(), level, blockPos));
   }

   private static boolean isUnderCrops(BlockGetter blockGetter, BlockPos blockPos) {
      Block var2 = blockGetter.getBlockState(blockPos.above()).getBlock();
      return var2 instanceof CropBlock || var2 instanceof StemBlock || var2 instanceof AttachedStemBlock;
   }

   private static boolean isNearWater(LevelReader levelReader, BlockPos blockPos) {
      for(BlockPos var3 : BlockPos.betweenClosed(blockPos.offset(-4, 0, -4), blockPos.offset(4, 1, 4))) {
         if(levelReader.getFluidState(var3).is(FluidTags.WATER)) {
            return true;
         }
      }

      return false;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{MOISTURE});
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
