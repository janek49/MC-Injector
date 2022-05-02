package net.minecraft.world.level.block.piston;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MovingPistonBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = PistonHeadBlock.FACING;
   public static final EnumProperty TYPE = PistonHeadBlock.TYPE;

   public MovingPistonBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TYPE, PistonType.DEFAULT));
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return null;
   }

   public static BlockEntity newMovingBlockEntity(BlockState blockState, Direction direction, boolean var2, boolean var3) {
      return new PistonMovingBlockEntity(blockState, direction, var2, var3);
   }

   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var1.getBlock() != var4.getBlock()) {
         BlockEntity var6 = level.getBlockEntity(blockPos);
         if(var6 instanceof PistonMovingBlockEntity) {
            ((PistonMovingBlockEntity)var6).finalTick();
         }

      }
   }

   public void destroy(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
      BlockPos blockPos = blockPos.relative(((Direction)blockState.getValue(FACING)).getOpposite());
      BlockState var5 = levelAccessor.getBlockState(blockPos);
      if(var5.getBlock() instanceof PistonBaseBlock && ((Boolean)var5.getValue(PistonBaseBlock.EXTENDED)).booleanValue()) {
         levelAccessor.removeBlock(blockPos, false);
      }

   }

   public boolean canOcclude(BlockState blockState) {
      return false;
   }

   public boolean isRedstoneConductor(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return false;
   }

   public boolean isViewBlocking(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return false;
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(!level.isClientSide && level.getBlockEntity(blockPos) == null) {
         level.removeBlock(blockPos, false);
         return true;
      } else {
         return false;
      }
   }

   public List getDrops(BlockState blockState, LootContext.Builder lootContext$Builder) {
      PistonMovingBlockEntity var3 = this.getBlockEntity(lootContext$Builder.getLevel(), (BlockPos)lootContext$Builder.getParameter(LootContextParams.BLOCK_POS));
      return var3 == null?Collections.emptyList():var3.getMovedState().getDrops(lootContext$Builder);
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return Shapes.empty();
   }

   public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      PistonMovingBlockEntity var5 = this.getBlockEntity(blockGetter, blockPos);
      return var5 != null?var5.getCollisionShape(blockGetter, blockPos):Shapes.empty();
   }

   @Nullable
   private PistonMovingBlockEntity getBlockEntity(BlockGetter blockGetter, BlockPos blockPos) {
      BlockEntity var3 = blockGetter.getBlockEntity(blockPos);
      return var3 instanceof PistonMovingBlockEntity?(PistonMovingBlockEntity)var3:null;
   }

   public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
      return ItemStack.EMPTY;
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(FACING, rotation.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return var1.rotate(mirror.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, TYPE});
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
