package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BannerBlock extends AbstractBannerBlock {
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
   private static final Map BY_COLOR = Maps.newHashMap();
   private static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

   public BannerBlock(DyeColor dyeColor, Block.Properties block$Properties) {
      super(dyeColor, block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(ROTATION, Integer.valueOf(0)));
      BY_COLOR.put(dyeColor, this);
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      return levelReader.getBlockState(blockPos.below()).getMaterial().isSolid();
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return (BlockState)this.defaultBlockState().setValue(ROTATION, Integer.valueOf(Mth.floor((double)((180.0F + blockPlaceContext.getRotation()) * 16.0F / 360.0F) + 0.5D) & 15));
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return direction == Direction.DOWN && !var1.canSurvive(levelAccessor, var5)?Blocks.AIR.defaultBlockState():super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(ROTATION, Integer.valueOf(rotation.rotate(((Integer)var1.getValue(ROTATION)).intValue(), 16)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return (BlockState)var1.setValue(ROTATION, Integer.valueOf(mirror.mirror(((Integer)var1.getValue(ROTATION)).intValue(), 16)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{ROTATION});
   }

   public static Block byColor(DyeColor color) {
      return (Block)BY_COLOR.getOrDefault(color, Blocks.WHITE_BANNER);
   }
}
