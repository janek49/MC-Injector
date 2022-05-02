package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DaylightDetectorBlock extends BaseEntityBlock {
   public static final IntegerProperty POWER = BlockStateProperties.POWER;
   public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);

   public DaylightDetectorBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWER, Integer.valueOf(0))).setValue(INVERTED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   public boolean useShapeForLightOcclusion(BlockState blockState) {
      return true;
   }

   public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return ((Integer)blockState.getValue(POWER)).intValue();
   }

   public static void updateSignalStrength(BlockState blockState, Level level, BlockPos blockPos) {
      if(level.dimension.isHasSkyLight()) {
         int var3 = level.getBrightness(LightLayer.SKY, blockPos) - level.getSkyDarken();
         float var4 = level.getSunAngle(1.0F);
         boolean var5 = ((Boolean)blockState.getValue(INVERTED)).booleanValue();
         if(var5) {
            var3 = 15 - var3;
         } else if(var3 > 0) {
            float var6 = var4 < 3.1415927F?0.0F:6.2831855F;
            var4 = var4 + (var6 - var4) * 0.2F;
            var3 = Math.round((float)var3 * Mth.cos(var4));
         }

         var3 = Mth.clamp(var3, 0, 15);
         if(((Integer)blockState.getValue(POWER)).intValue() != var3) {
            level.setBlock(blockPos, (BlockState)blockState.setValue(POWER, Integer.valueOf(var3)), 3);
         }

      }
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(player.mayBuild()) {
         if(level.isClientSide) {
            return true;
         } else {
            BlockState blockState = (BlockState)blockState.cycle(INVERTED);
            level.setBlock(blockPos, blockState, 4);
            updateSignalStrength(blockState, level, blockPos);
            return true;
         }
      } else {
         return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
      }
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.MODEL;
   }

   public boolean isSignalSource(BlockState blockState) {
      return true;
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new DaylightDetectorBlockEntity();
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{POWER, INVERTED});
   }
}
