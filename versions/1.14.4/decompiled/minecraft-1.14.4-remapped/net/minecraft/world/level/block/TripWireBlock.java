package net.minecraft.world.level.block;

import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.TripWireHookBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TripWireBlock extends Block {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
   public static final BooleanProperty DISARMED = BlockStateProperties.DISARMED;
   public static final BooleanProperty NORTH = PipeBlock.NORTH;
   public static final BooleanProperty EAST = PipeBlock.EAST;
   public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
   public static final BooleanProperty WEST = PipeBlock.WEST;
   private static final Map PROPERTY_BY_DIRECTION = CrossCollisionBlock.PROPERTY_BY_DIRECTION;
   protected static final VoxelShape AABB = Block.box(0.0D, 1.0D, 0.0D, 16.0D, 2.5D, 16.0D);
   protected static final VoxelShape NOT_ATTACHED_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   private final TripWireHookBlock hook;

   public TripWireBlock(TripWireHookBlock hook, Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWERED, Boolean.valueOf(false))).setValue(ATTACHED, Boolean.valueOf(false))).setValue(DISARMED, Boolean.valueOf(false))).setValue(NORTH, Boolean.valueOf(false))).setValue(EAST, Boolean.valueOf(false))).setValue(SOUTH, Boolean.valueOf(false))).setValue(WEST, Boolean.valueOf(false)));
      this.hook = hook;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return ((Boolean)blockState.getValue(ATTACHED)).booleanValue()?AABB:NOT_ATTACHED_AABB;
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      BlockGetter var2 = blockPlaceContext.getLevel();
      BlockPos var3 = blockPlaceContext.getClickedPos();
      return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(NORTH, Boolean.valueOf(this.shouldConnectTo(var2.getBlockState(var3.north()), Direction.NORTH)))).setValue(EAST, Boolean.valueOf(this.shouldConnectTo(var2.getBlockState(var3.east()), Direction.EAST)))).setValue(SOUTH, Boolean.valueOf(this.shouldConnectTo(var2.getBlockState(var3.south()), Direction.SOUTH)))).setValue(WEST, Boolean.valueOf(this.shouldConnectTo(var2.getBlockState(var3.west()), Direction.WEST)));
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return direction.getAxis().isHorizontal()?(BlockState)var1.setValue((Property)PROPERTY_BY_DIRECTION.get(direction), Boolean.valueOf(this.shouldConnectTo(var3, direction))):super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.TRANSLUCENT;
   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var4.getBlock() != var1.getBlock()) {
         this.updateSource(level, blockPos, var1);
      }
   }

   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(!var5 && var1.getBlock() != var4.getBlock()) {
         this.updateSource(level, blockPos, (BlockState)var1.setValue(POWERED, Boolean.valueOf(true)));
      }
   }

   public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
      if(!level.isClientSide && !player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem() == Items.SHEARS) {
         level.setBlock(blockPos, (BlockState)blockState.setValue(DISARMED, Boolean.valueOf(true)), 4);
      }

      super.playerWillDestroy(level, blockPos, blockState, player);
   }

   private void updateSource(Level level, BlockPos blockPos, BlockState blockState) {
      for(Direction var7 : new Direction[]{Direction.SOUTH, Direction.WEST}) {
         for(int var8 = 1; var8 < 42; ++var8) {
            BlockPos var9 = blockPos.relative(var7, var8);
            BlockState var10 = level.getBlockState(var9);
            if(var10.getBlock() == this.hook) {
               if(var10.getValue(TripWireHookBlock.FACING) == var7.getOpposite()) {
                  this.hook.calculateState(level, var9, var10, false, true, var8, blockState);
               }
               break;
            }

            if(var10.getBlock() != this) {
               break;
            }
         }
      }

   }

   public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      if(!level.isClientSide) {
         if(!((Boolean)blockState.getValue(POWERED)).booleanValue()) {
            this.checkPressed(level, blockPos);
         }
      }
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!level.isClientSide) {
         if(((Boolean)level.getBlockState(blockPos).getValue(POWERED)).booleanValue()) {
            this.checkPressed(level, blockPos);
         }
      }
   }

   private void checkPressed(Level level, BlockPos blockPos) {
      BlockState var3 = level.getBlockState(blockPos);
      boolean var4 = ((Boolean)var3.getValue(POWERED)).booleanValue();
      boolean var5 = false;
      List<? extends Entity> var6 = level.getEntities((Entity)null, var3.getShape(level, blockPos).bounds().move(blockPos));
      if(!var6.isEmpty()) {
         for(Entity var8 : var6) {
            if(!var8.isIgnoringBlockTriggers()) {
               var5 = true;
               break;
            }
         }
      }

      if(var5 != var4) {
         var3 = (BlockState)var3.setValue(POWERED, Boolean.valueOf(var5));
         level.setBlock(blockPos, var3, 3);
         this.updateSource(level, blockPos, var3);
      }

      if(var5) {
         level.getBlockTicks().scheduleTick(new BlockPos(blockPos), this, this.getTickDelay(level));
      }

   }

   public boolean shouldConnectTo(BlockState blockState, Direction direction) {
      Block var3 = blockState.getBlock();
      return var3 == this.hook?blockState.getValue(TripWireHookBlock.FACING) == direction.getOpposite():var3 == this;
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      switch(rotation) {
      case CLOCKWISE_180:
         return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, var1.getValue(SOUTH))).setValue(EAST, var1.getValue(WEST))).setValue(SOUTH, var1.getValue(NORTH))).setValue(WEST, var1.getValue(EAST));
      case COUNTERCLOCKWISE_90:
         return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, var1.getValue(EAST))).setValue(EAST, var1.getValue(SOUTH))).setValue(SOUTH, var1.getValue(WEST))).setValue(WEST, var1.getValue(NORTH));
      case CLOCKWISE_90:
         return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, var1.getValue(WEST))).setValue(EAST, var1.getValue(NORTH))).setValue(SOUTH, var1.getValue(EAST))).setValue(WEST, var1.getValue(SOUTH));
      default:
         return var1;
      }
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      switch(mirror) {
      case LEFT_RIGHT:
         return (BlockState)((BlockState)var1.setValue(NORTH, var1.getValue(SOUTH))).setValue(SOUTH, var1.getValue(NORTH));
      case FRONT_BACK:
         return (BlockState)((BlockState)var1.setValue(EAST, var1.getValue(WEST))).setValue(WEST, var1.getValue(EAST));
      default:
         return super.mirror(var1, mirror);
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{POWERED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH});
   }
}
