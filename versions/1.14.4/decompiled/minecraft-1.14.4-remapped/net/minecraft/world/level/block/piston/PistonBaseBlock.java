package net.minecraft.world.level.block.piston;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PistonBaseBlock extends DirectionalBlock {
   public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
   protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_AABB = Block.box(4.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 12.0D);
   protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape UP_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
   protected static final VoxelShape DOWN_AABB = Block.box(0.0D, 4.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   private final boolean isSticky;

   public PistonBaseBlock(boolean isSticky, Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(EXTENDED, Boolean.valueOf(false)));
      this.isSticky = isSticky;
   }

   public boolean isViewBlocking(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return !((Boolean)blockState.getValue(EXTENDED)).booleanValue();
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      if(((Boolean)blockState.getValue(EXTENDED)).booleanValue()) {
         switch((Direction)blockState.getValue(FACING)) {
         case DOWN:
            return DOWN_AABB;
         case UP:
         default:
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
      } else {
         return Shapes.block();
      }
   }

   public boolean isRedstoneConductor(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return false;
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
      if(!level.isClientSide) {
         this.checkIfExtend(level, blockPos, blockState);
      }

   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      if(!level.isClientSide) {
         this.checkIfExtend(level, var3, blockState);
      }

   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var4.getBlock() != var1.getBlock()) {
         if(!level.isClientSide && level.getBlockEntity(blockPos) == null) {
            this.checkIfExtend(level, blockPos, var1);
         }

      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getNearestLookingDirection().getOpposite())).setValue(EXTENDED, Boolean.valueOf(false));
   }

   private void checkIfExtend(Level level, BlockPos blockPos, BlockState blockState) {
      Direction var4 = (Direction)blockState.getValue(FACING);
      boolean var5 = this.getNeighborSignal(level, blockPos, var4);
      if(var5 && !((Boolean)blockState.getValue(EXTENDED)).booleanValue()) {
         if((new PistonStructureResolver(level, blockPos, var4, true)).resolve()) {
            level.blockEvent(blockPos, this, 0, var4.get3DDataValue());
         }
      } else if(!var5 && ((Boolean)blockState.getValue(EXTENDED)).booleanValue()) {
         BlockPos var6 = blockPos.relative(var4, 2);
         BlockState var7 = level.getBlockState(var6);
         int var8 = 1;
         if(var7.getBlock() == Blocks.MOVING_PISTON && var7.getValue(FACING) == var4) {
            BlockEntity var9 = level.getBlockEntity(var6);
            if(var9 instanceof PistonMovingBlockEntity) {
               PistonMovingBlockEntity var10 = (PistonMovingBlockEntity)var9;
               if(var10.isExtending() && (var10.getProgress(0.0F) < 0.5F || level.getGameTime() == var10.getLastTicked() || ((ServerLevel)level).isHandlingTick())) {
                  var8 = 2;
               }
            }
         }

         level.blockEvent(blockPos, this, var8, var4.get3DDataValue());
      }

   }

   private boolean getNeighborSignal(Level level, BlockPos blockPos, Direction direction) {
      for(Direction var7 : Direction.values()) {
         if(var7 != direction && level.hasSignal(blockPos.relative(var7), var7)) {
            return true;
         }
      }

      if(level.hasSignal(blockPos, Direction.DOWN)) {
         return true;
      } else {
         BlockPos blockPos = blockPos.above();

         for(Direction var8 : Direction.values()) {
            if(var8 != Direction.DOWN && level.hasSignal(blockPos.relative(var8), var8)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean triggerEvent(BlockState blockState, Level level, BlockPos blockPos, int var4, int var5) {
      Direction var6 = (Direction)blockState.getValue(FACING);
      if(!level.isClientSide) {
         boolean var7 = this.getNeighborSignal(level, blockPos, var6);
         if(var7 && (var4 == 1 || var4 == 2)) {
            level.setBlock(blockPos, (BlockState)blockState.setValue(EXTENDED, Boolean.valueOf(true)), 2);
            return false;
         }

         if(!var7 && var4 == 0) {
            return false;
         }
      }

      if(var4 == 0) {
         if(!this.moveBlocks(level, blockPos, var6, true)) {
            return false;
         }

         level.setBlock(blockPos, (BlockState)blockState.setValue(EXTENDED, Boolean.valueOf(true)), 67);
         level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.25F + 0.6F);
      } else if(var4 == 1 || var4 == 2) {
         BlockEntity var7 = level.getBlockEntity(blockPos.relative(var6));
         if(var7 instanceof PistonMovingBlockEntity) {
            ((PistonMovingBlockEntity)var7).finalTick();
         }

         level.setBlock(blockPos, (BlockState)((BlockState)Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, var6)).setValue(MovingPistonBlock.TYPE, this.isSticky?PistonType.STICKY:PistonType.DEFAULT), 3);
         level.setBlockEntity(blockPos, MovingPistonBlock.newMovingBlockEntity((BlockState)this.defaultBlockState().setValue(FACING, Direction.from3DDataValue(var5 & 7)), var6, false, true));
         if(this.isSticky) {
            BlockPos var8 = blockPos.offset(var6.getStepX() * 2, var6.getStepY() * 2, var6.getStepZ() * 2);
            BlockState var9 = level.getBlockState(var8);
            Block var10 = var9.getBlock();
            boolean var11 = false;
            if(var10 == Blocks.MOVING_PISTON) {
               BlockEntity var12 = level.getBlockEntity(var8);
               if(var12 instanceof PistonMovingBlockEntity) {
                  PistonMovingBlockEntity var13 = (PistonMovingBlockEntity)var12;
                  if(var13.getDirection() == var6 && var13.isExtending()) {
                     var13.finalTick();
                     var11 = true;
                  }
               }
            }

            if(!var11) {
               if(var4 != 1 || var9.isAir() || !isPushable(var9, level, var8, var6.getOpposite(), false, var6) || var9.getPistonPushReaction() != PushReaction.NORMAL && var10 != Blocks.PISTON && var10 != Blocks.STICKY_PISTON) {
                  level.removeBlock(blockPos.relative(var6), false);
               } else {
                  this.moveBlocks(level, blockPos, var6, false);
               }
            }
         } else {
            level.removeBlock(blockPos.relative(var6), false);
         }

         level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.15F + 0.6F);
      }

      return true;
   }

   public static boolean isPushable(BlockState blockState, Level level, BlockPos blockPos, Direction var3, boolean var4, Direction var5) {
      Block var6 = blockState.getBlock();
      if(var6 == Blocks.OBSIDIAN) {
         return false;
      } else if(!level.getWorldBorder().isWithinBounds(blockPos)) {
         return false;
      } else if(blockPos.getY() >= 0 && (var3 != Direction.DOWN || blockPos.getY() != 0)) {
         if(blockPos.getY() <= level.getMaxBuildHeight() - 1 && (var3 != Direction.UP || blockPos.getY() != level.getMaxBuildHeight() - 1)) {
            if(var6 != Blocks.PISTON && var6 != Blocks.STICKY_PISTON) {
               if(blockState.getDestroySpeed(level, blockPos) == -1.0F) {
                  return false;
               }

               switch(blockState.getPistonPushReaction()) {
               case BLOCK:
                  return false;
               case DESTROY:
                  return var4;
               case PUSH_ONLY:
                  return var3 == var5;
               }
            } else if(((Boolean)blockState.getValue(EXTENDED)).booleanValue()) {
               return false;
            }

            return !var6.isEntityBlock();
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean moveBlocks(Level level, BlockPos blockPos, Direction direction, boolean var4) {
      BlockPos blockPos = blockPos.relative(direction);
      if(!var4 && level.getBlockState(blockPos).getBlock() == Blocks.PISTON_HEAD) {
         level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 20);
      }

      PistonStructureResolver var6 = new PistonStructureResolver(level, blockPos, direction, var4);
      if(!var6.resolve()) {
         return false;
      } else {
         List<BlockPos> var7 = var6.getToPush();
         List<BlockState> var8 = Lists.newArrayList();

         for(int var9 = 0; var9 < var7.size(); ++var9) {
            BlockPos var10 = (BlockPos)var7.get(var9);
            var8.add(level.getBlockState(var10));
         }

         List<BlockPos> var9 = var6.getToDestroy();
         int var10 = var7.size() + var9.size();
         BlockState[] vars11 = new BlockState[var10];
         Direction var12 = var4?direction:direction.getOpposite();
         Set<BlockPos> var13 = Sets.newHashSet(var7);

         for(int var14 = var9.size() - 1; var14 >= 0; --var14) {
            BlockPos var15 = (BlockPos)var9.get(var14);
            BlockState var16 = level.getBlockState(var15);
            BlockEntity var17 = var16.getBlock().isEntityBlock()?level.getBlockEntity(var15):null;
            dropResources(var16, level, var15, var17);
            level.setBlock(var15, Blocks.AIR.defaultBlockState(), 18);
            --var10;
            vars11[var10] = var16;
         }

         for(int var14 = var7.size() - 1; var14 >= 0; --var14) {
            BlockPos var15 = (BlockPos)var7.get(var14);
            BlockState var16 = level.getBlockState(var15);
            var15 = var15.relative(var12);
            var13.remove(var15);
            level.setBlock(var15, (BlockState)Blocks.MOVING_PISTON.defaultBlockState().setValue(FACING, direction), 68);
            level.setBlockEntity(var15, MovingPistonBlock.newMovingBlockEntity((BlockState)var8.get(var14), direction, var4, false));
            --var10;
            vars11[var10] = var16;
         }

         if(var4) {
            PistonType var14 = this.isSticky?PistonType.STICKY:PistonType.DEFAULT;
            BlockState var15 = (BlockState)((BlockState)Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.FACING, direction)).setValue(PistonHeadBlock.TYPE, var14);
            BlockState var16 = (BlockState)((BlockState)Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, direction)).setValue(MovingPistonBlock.TYPE, this.isSticky?PistonType.STICKY:PistonType.DEFAULT);
            var13.remove(blockPos);
            level.setBlock(blockPos, var16, 68);
            level.setBlockEntity(blockPos, MovingPistonBlock.newMovingBlockEntity(var15, direction, true, true));
         }

         for(BlockPos var15 : var13) {
            level.setBlock(var15, Blocks.AIR.defaultBlockState(), 66);
         }

         for(int var14 = var9.size() - 1; var14 >= 0; --var14) {
            BlockState var15 = vars11[var10++];
            BlockPos var16 = (BlockPos)var9.get(var14);
            var15.updateIndirectNeighbourShapes(level, var16, 2);
            level.updateNeighborsAt(var16, var15.getBlock());
         }

         for(int var14 = var7.size() - 1; var14 >= 0; --var14) {
            level.updateNeighborsAt((BlockPos)var7.get(var14), vars11[var10++].getBlock());
         }

         if(var4) {
            level.updateNeighborsAt(blockPos, Blocks.PISTON_HEAD);
         }

         return true;
      }
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(FACING, rotation.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return var1.rotate(mirror.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, EXTENDED});
   }

   public boolean useShapeForLightOcclusion(BlockState blockState) {
      return ((Boolean)blockState.getValue(EXTENDED)).booleanValue();
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
