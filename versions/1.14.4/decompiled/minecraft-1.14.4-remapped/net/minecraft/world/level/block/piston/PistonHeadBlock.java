package net.minecraft.world.level.block.piston;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PistonHeadBlock extends DirectionalBlock {
   public static final EnumProperty TYPE = BlockStateProperties.PISTON_TYPE;
   public static final BooleanProperty SHORT = BlockStateProperties.SHORT;
   protected static final VoxelShape EAST_AABB = Block.box(12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D);
   protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D);
   protected static final VoxelShape UP_AABB = Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
   protected static final VoxelShape UP_ARM_AABB = Block.box(6.0D, -4.0D, 6.0D, 10.0D, 12.0D, 10.0D);
   protected static final VoxelShape DOWN_ARM_AABB = Block.box(6.0D, 4.0D, 6.0D, 10.0D, 20.0D, 10.0D);
   protected static final VoxelShape SOUTH_ARM_AABB = Block.box(6.0D, 6.0D, -4.0D, 10.0D, 10.0D, 12.0D);
   protected static final VoxelShape NORTH_ARM_AABB = Block.box(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 20.0D);
   protected static final VoxelShape EAST_ARM_AABB = Block.box(-4.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
   protected static final VoxelShape WEST_ARM_AABB = Block.box(4.0D, 6.0D, 6.0D, 20.0D, 10.0D, 10.0D);
   protected static final VoxelShape SHORT_UP_ARM_AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 12.0D, 10.0D);
   protected static final VoxelShape SHORT_DOWN_ARM_AABB = Block.box(6.0D, 4.0D, 6.0D, 10.0D, 16.0D, 10.0D);
   protected static final VoxelShape SHORT_SOUTH_ARM_AABB = Block.box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 12.0D);
   protected static final VoxelShape SHORT_NORTH_ARM_AABB = Block.box(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 16.0D);
   protected static final VoxelShape SHORT_EAST_ARM_AABB = Block.box(0.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
   protected static final VoxelShape SHORT_WEST_ARM_AABB = Block.box(4.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

   public PistonHeadBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TYPE, PistonType.DEFAULT)).setValue(SHORT, Boolean.valueOf(false)));
   }

   private VoxelShape getBaseShape(BlockState blockState) {
      switch((Direction)blockState.getValue(FACING)) {
      case DOWN:
      default:
         return DOWN_AABB;
      case UP:
         return UP_AABB;
      case NORTH:
         return NORTH_AABB;
      case SOUTH:
         return SOUTH_AABB;
      case WEST:
         return WEST_AABB;
      case EAST:
         return EAST_AABB;
      }
   }

   public boolean useShapeForLightOcclusion(BlockState blockState) {
      return true;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return Shapes.or(this.getBaseShape(blockState), this.getArmShape(blockState));
   }

   private VoxelShape getArmShape(BlockState blockState) {
      boolean var2 = ((Boolean)blockState.getValue(SHORT)).booleanValue();
      switch((Direction)blockState.getValue(FACING)) {
      case DOWN:
      default:
         return var2?SHORT_DOWN_ARM_AABB:DOWN_ARM_AABB;
      case UP:
         return var2?SHORT_UP_ARM_AABB:UP_ARM_AABB;
      case NORTH:
         return var2?SHORT_NORTH_ARM_AABB:NORTH_ARM_AABB;
      case SOUTH:
         return var2?SHORT_SOUTH_ARM_AABB:SOUTH_ARM_AABB;
      case WEST:
         return var2?SHORT_WEST_ARM_AABB:WEST_ARM_AABB;
      case EAST:
         return var2?SHORT_EAST_ARM_AABB:EAST_ARM_AABB;
      }
   }

   public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
      if(!level.isClientSide && player.abilities.instabuild) {
         BlockPos blockPos = blockPos.relative(((Direction)blockState.getValue(FACING)).getOpposite());
         Block var6 = level.getBlockState(blockPos).getBlock();
         if(var6 == Blocks.PISTON || var6 == Blocks.STICKY_PISTON) {
            level.removeBlock(blockPos, false);
         }
      }

      super.playerWillDestroy(level, blockPos, blockState, player);
   }

   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var1.getBlock() != var4.getBlock()) {
         super.onRemove(var1, level, blockPos, var4, var5);
         Direction var6 = ((Direction)var1.getValue(FACING)).getOpposite();
         blockPos = blockPos.relative(var6);
         BlockState var7 = level.getBlockState(blockPos);
         if((var7.getBlock() == Blocks.PISTON || var7.getBlock() == Blocks.STICKY_PISTON) && ((Boolean)var7.getValue(PistonBaseBlock.EXTENDED)).booleanValue()) {
            dropResources(var7, level, blockPos);
            level.removeBlock(blockPos, false);
         }

      }
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return direction.getOpposite() == var1.getValue(FACING) && !var1.canSurvive(levelAccessor, var5)?Blocks.AIR.defaultBlockState():super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      Block var4 = levelReader.getBlockState(blockPos.relative(((Direction)blockState.getValue(FACING)).getOpposite())).getBlock();
      return var4 == Blocks.PISTON || var4 == Blocks.STICKY_PISTON || var4 == Blocks.MOVING_PISTON;
   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      if(blockState.canSurvive(level, var3)) {
         BlockPos var7 = var3.relative(((Direction)blockState.getValue(FACING)).getOpposite());
         level.getBlockState(var7).neighborChanged(level, var7, block, var5, false);
      }

   }

   public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
      return new ItemStack(blockState.getValue(TYPE) == PistonType.STICKY?Blocks.STICKY_PISTON:Blocks.PISTON);
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(FACING, rotation.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return var1.rotate(mirror.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, TYPE, SHORT});
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
