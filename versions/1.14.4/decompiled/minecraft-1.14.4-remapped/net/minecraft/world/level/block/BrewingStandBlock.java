package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BrewingStandBlock extends BaseEntityBlock {
   public static final BooleanProperty[] HAS_BOTTLE = new BooleanProperty[]{BlockStateProperties.HAS_BOTTLE_0, BlockStateProperties.HAS_BOTTLE_1, BlockStateProperties.HAS_BOTTLE_2};
   protected static final VoxelShape SHAPE = Shapes.or(Block.box(1.0D, 0.0D, 1.0D, 15.0D, 2.0D, 15.0D), Block.box(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D));

   public BrewingStandBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HAS_BOTTLE[0], Boolean.valueOf(false))).setValue(HAS_BOTTLE[1], Boolean.valueOf(false))).setValue(HAS_BOTTLE[2], Boolean.valueOf(false)));
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.MODEL;
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new BrewingStandBlockEntity();
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(level.isClientSide) {
         return true;
      } else {
         BlockEntity var7 = level.getBlockEntity(blockPos);
         if(var7 instanceof BrewingStandBlockEntity) {
            player.openMenu((BrewingStandBlockEntity)var7);
            player.awardStat(Stats.INTERACT_WITH_BREWINGSTAND);
         }

         return true;
      }
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
      if(itemStack.hasCustomHoverName()) {
         BlockEntity var6 = level.getBlockEntity(blockPos);
         if(var6 instanceof BrewingStandBlockEntity) {
            ((BrewingStandBlockEntity)var6).setCustomName(itemStack.getHoverName());
         }
      }

   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      double var5 = (double)((float)blockPos.getX() + 0.4F + random.nextFloat() * 0.2F);
      double var7 = (double)((float)blockPos.getY() + 0.7F + random.nextFloat() * 0.3F);
      double var9 = (double)((float)blockPos.getZ() + 0.4F + random.nextFloat() * 0.2F);
      level.addParticle(ParticleTypes.SMOKE, var5, var7, var9, 0.0D, 0.0D, 0.0D);
   }

   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var1.getBlock() != var4.getBlock()) {
         BlockEntity var6 = level.getBlockEntity(blockPos);
         if(var6 instanceof BrewingStandBlockEntity) {
            Containers.dropContents(level, (BlockPos)blockPos, (Container)((BrewingStandBlockEntity)var6));
         }

         super.onRemove(var1, level, blockPos, var4, var5);
      }
   }

   public boolean hasAnalogOutputSignal(BlockState blockState) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(blockPos));
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{HAS_BOTTLE[0], HAS_BOTTLE[1], HAS_BOTTLE[2]});
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
