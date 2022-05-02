package net.minecraft.world.level.block;

import com.google.common.base.MoreObjects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TripWireHookBlock extends Block {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
   protected static final VoxelShape NORTH_AABB = Block.box(5.0D, 0.0D, 10.0D, 11.0D, 10.0D, 16.0D);
   protected static final VoxelShape SOUTH_AABB = Block.box(5.0D, 0.0D, 0.0D, 11.0D, 10.0D, 6.0D);
   protected static final VoxelShape WEST_AABB = Block.box(10.0D, 0.0D, 5.0D, 16.0D, 10.0D, 11.0D);
   protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 5.0D, 6.0D, 10.0D, 11.0D);

   public TripWireHookBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, Boolean.valueOf(false))).setValue(ATTACHED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      switch((Direction)blockState.getValue(FACING)) {
      case EAST:
      default:
         return EAST_AABB;
      case WEST:
         return WEST_AABB;
      case SOUTH:
         return SOUTH_AABB;
      case NORTH:
         return NORTH_AABB;
      }
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      Direction var4 = (Direction)blockState.getValue(FACING);
      BlockPos var5 = blockPos.relative(var4.getOpposite());
      BlockState var6 = levelReader.getBlockState(var5);
      return var4.getAxis().isHorizontal() && var6.isFaceSturdy(levelReader, var5, var4) && !var6.isSignalSource();
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return direction.getOpposite() == var1.getValue(FACING) && !var1.canSurvive(levelAccessor, var5)?Blocks.AIR.defaultBlockState():super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      BlockState blockState = (BlockState)((BlockState)this.defaultBlockState().setValue(POWERED, Boolean.valueOf(false))).setValue(ATTACHED, Boolean.valueOf(false));
      LevelReader var3 = blockPlaceContext.getLevel();
      BlockPos var4 = blockPlaceContext.getClickedPos();
      Direction[] vars5 = blockPlaceContext.getNearestLookingDirections();

      for(Direction var9 : vars5) {
         if(var9.getAxis().isHorizontal()) {
            Direction var10 = var9.getOpposite();
            blockState = (BlockState)blockState.setValue(FACING, var10);
            if(blockState.canSurvive(var3, var4)) {
               return blockState;
            }
         }
      }

      return null;
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
      this.calculateState(level, blockPos, blockState, false, false, -1, (BlockState)null);
   }

   public void calculateState(Level level, BlockPos blockPos, BlockState var3, boolean var4, boolean var5, int var6, @Nullable BlockState var7) {
      Direction var8 = (Direction)var3.getValue(FACING);
      boolean var9 = ((Boolean)var3.getValue(ATTACHED)).booleanValue();
      boolean var10 = ((Boolean)var3.getValue(POWERED)).booleanValue();
      boolean var11 = !var4;
      boolean var12 = false;
      int var13 = 0;
      BlockState[] vars14 = new BlockState[42];

      for(int var15 = 1; var15 < 42; ++var15) {
         BlockPos var16 = blockPos.relative(var8, var15);
         BlockState var17 = level.getBlockState(var16);
         if(var17.getBlock() == Blocks.TRIPWIRE_HOOK) {
            if(var17.getValue(FACING) == var8.getOpposite()) {
               var13 = var15;
            }
            break;
         }

         if(var17.getBlock() != Blocks.TRIPWIRE && var15 != var6) {
            vars14[var15] = null;
            var11 = false;
         } else {
            if(var15 == var6) {
               var17 = (BlockState)MoreObjects.firstNonNull(var7, var17);
            }

            boolean var18 = !((Boolean)var17.getValue(TripWireBlock.DISARMED)).booleanValue();
            boolean var19 = ((Boolean)var17.getValue(TripWireBlock.POWERED)).booleanValue();
            var12 |= var18 && var19;
            vars14[var15] = var17;
            if(var15 == var6) {
               level.getBlockTicks().scheduleTick(blockPos, this, this.getTickDelay(level));
               var11 &= var18;
            }
         }
      }

      var11 = var11 & var13 > 1;
      var12 = var12 & var11;
      BlockState var15 = (BlockState)((BlockState)this.defaultBlockState().setValue(ATTACHED, Boolean.valueOf(var11))).setValue(POWERED, Boolean.valueOf(var12));
      if(var13 > 0) {
         BlockPos var16 = blockPos.relative(var8, var13);
         Direction var17 = var8.getOpposite();
         level.setBlock(var16, (BlockState)var15.setValue(FACING, var17), 3);
         this.notifyNeighbors(level, var16, var17);
         this.playSound(level, var16, var11, var12, var9, var10);
      }

      this.playSound(level, blockPos, var11, var12, var9, var10);
      if(!var4) {
         level.setBlock(blockPos, (BlockState)var15.setValue(FACING, var8), 3);
         if(var5) {
            this.notifyNeighbors(level, blockPos, var8);
         }
      }

      if(var9 != var11) {
         for(int var16 = 1; var16 < var13; ++var16) {
            BlockPos var17 = blockPos.relative(var8, var16);
            BlockState var18 = vars14[var16];
            if(var18 != null) {
               level.setBlock(var17, (BlockState)var18.setValue(ATTACHED, Boolean.valueOf(var11)), 3);
               if(!level.getBlockState(var17).isAir()) {
                  ;
               }
            }
         }
      }

   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      this.calculateState(level, blockPos, blockState, false, true, -1, (BlockState)null);
   }

   private void playSound(Level level, BlockPos blockPos, boolean var3, boolean var4, boolean var5, boolean var6) {
      if(var4 && !var6) {
         level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.BLOCKS, 0.4F, 0.6F);
      } else if(!var4 && var6) {
         level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.TRIPWIRE_CLICK_OFF, SoundSource.BLOCKS, 0.4F, 0.5F);
      } else if(var3 && !var5) {
         level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.TRIPWIRE_ATTACH, SoundSource.BLOCKS, 0.4F, 0.7F);
      } else if(!var3 && var5) {
         level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.TRIPWIRE_DETACH, SoundSource.BLOCKS, 0.4F, 1.2F / (level.random.nextFloat() * 0.2F + 0.9F));
      }

   }

   private void notifyNeighbors(Level level, BlockPos blockPos, Direction direction) {
      level.updateNeighborsAt(blockPos, this);
      level.updateNeighborsAt(blockPos.relative(direction.getOpposite()), this);
   }

   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(!var5 && var1.getBlock() != var4.getBlock()) {
         boolean var6 = ((Boolean)var1.getValue(ATTACHED)).booleanValue();
         boolean var7 = ((Boolean)var1.getValue(POWERED)).booleanValue();
         if(var6 || var7) {
            this.calculateState(level, blockPos, var1, true, false, -1, (BlockState)null);
         }

         if(var7) {
            level.updateNeighborsAt(blockPos, this);
            level.updateNeighborsAt(blockPos.relative(((Direction)var1.getValue(FACING)).getOpposite()), this);
         }

         super.onRemove(var1, level, blockPos, var4, var5);
      }
   }

   public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return ((Boolean)blockState.getValue(POWERED)).booleanValue()?15:0;
   }

   public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return !((Boolean)blockState.getValue(POWERED)).booleanValue()?0:(blockState.getValue(FACING) == direction?15:0);
   }

   public boolean isSignalSource(BlockState blockState) {
      return true;
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT_MIPPED;
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(FACING, rotation.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return var1.rotate(mirror.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, POWERED, ATTACHED});
   }
}
