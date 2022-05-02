package net.minecraft.world.level.block;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CampfireBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D);
   public static final BooleanProperty LIT = BlockStateProperties.LIT;
   public static final BooleanProperty SIGNAL_FIRE = BlockStateProperties.SIGNAL_FIRE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

   public CampfireBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, Boolean.valueOf(true))).setValue(SIGNAL_FIRE, Boolean.valueOf(false))).setValue(WATERLOGGED, Boolean.valueOf(false))).setValue(FACING, Direction.NORTH));
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(((Boolean)blockState.getValue(LIT)).booleanValue()) {
         BlockEntity var7 = level.getBlockEntity(blockPos);
         if(var7 instanceof CampfireBlockEntity) {
            CampfireBlockEntity var8 = (CampfireBlockEntity)var7;
            ItemStack var9 = player.getItemInHand(interactionHand);
            Optional<CampfireCookingRecipe> var10 = var8.getCookableRecipe(var9);
            if(var10.isPresent()) {
               if(!level.isClientSide && var8.placeFood(player.abilities.instabuild?var9.copy():var9, ((CampfireCookingRecipe)var10.get()).getCookingTime())) {
                  player.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
               }

               return true;
            }
         }
      }

      return false;
   }

   public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      if(!entity.fireImmune() && ((Boolean)blockState.getValue(LIT)).booleanValue() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
         entity.hurt(DamageSource.IN_FIRE, 1.0F);
      }

      super.entityInside(blockState, level, blockPos, entity);
   }

   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var1.getBlock() != var4.getBlock()) {
         BlockEntity var6 = level.getBlockEntity(blockPos);
         if(var6 instanceof CampfireBlockEntity) {
            Containers.dropContents(level, blockPos, ((CampfireBlockEntity)var6).getItems());
         }

         super.onRemove(var1, level, blockPos, var4, var5);
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      LevelAccessor var2 = blockPlaceContext.getLevel();
      BlockPos var3 = blockPlaceContext.getClickedPos();
      boolean var4 = var2.getFluidState(var3).getType() == Fluids.WATER;
      return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(var4))).setValue(SIGNAL_FIRE, Boolean.valueOf(this.isSmokeSource(var2.getBlockState(var3.below()))))).setValue(LIT, Boolean.valueOf(!var4))).setValue(FACING, blockPlaceContext.getHorizontalDirection());
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(((Boolean)var1.getValue(WATERLOGGED)).booleanValue()) {
         levelAccessor.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
      }

      return direction == Direction.DOWN?(BlockState)var1.setValue(SIGNAL_FIRE, Boolean.valueOf(this.isSmokeSource(var3))):super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   private boolean isSmokeSource(BlockState blockState) {
      return blockState.getBlock() == Blocks.HAY_BLOCK;
   }

   public int getLightEmission(BlockState blockState) {
      return ((Boolean)blockState.getValue(LIT)).booleanValue()?super.getLightEmission(blockState):0;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.MODEL;
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(((Boolean)blockState.getValue(LIT)).booleanValue()) {
         if(random.nextInt(10) == 0) {
            level.playLocalSound((double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.5F), (double)((float)blockPos.getZ() + 0.5F), SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
         }

         if(random.nextInt(5) == 0) {
            for(int var5 = 0; var5 < random.nextInt(1) + 1; ++var5) {
               level.addParticle(ParticleTypes.LAVA, (double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.5F), (double)((float)blockPos.getZ() + 0.5F), (double)(random.nextFloat() / 2.0F), 5.0E-5D, (double)(random.nextFloat() / 2.0F));
            }
         }

      }
   }

   public boolean placeLiquid(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
      if(!((Boolean)blockState.getValue(BlockStateProperties.WATERLOGGED)).booleanValue() && fluidState.getType() == Fluids.WATER) {
         boolean var5 = ((Boolean)blockState.getValue(LIT)).booleanValue();
         if(var5) {
            if(levelAccessor.isClientSide()) {
               for(int var6 = 0; var6 < 20; ++var6) {
                  makeParticles(levelAccessor.getLevel(), blockPos, ((Boolean)blockState.getValue(SIGNAL_FIRE)).booleanValue(), true);
               }
            } else {
               levelAccessor.playSound((Player)null, blockPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            BlockEntity var6 = levelAccessor.getBlockEntity(blockPos);
            if(var6 instanceof CampfireBlockEntity) {
               ((CampfireBlockEntity)var6).dowse();
            }
         }

         levelAccessor.setBlock(blockPos, (BlockState)((BlockState)blockState.setValue(WATERLOGGED, Boolean.valueOf(true))).setValue(LIT, Boolean.valueOf(false)), 3);
         levelAccessor.getLiquidTicks().scheduleTick(blockPos, fluidState.getType(), fluidState.getType().getTickDelay(levelAccessor));
         return true;
      } else {
         return false;
      }
   }

   public void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
      if(!level.isClientSide && entity instanceof AbstractArrow) {
         AbstractArrow var5 = (AbstractArrow)entity;
         if(var5.isOnFire() && !((Boolean)blockState.getValue(LIT)).booleanValue() && !((Boolean)blockState.getValue(WATERLOGGED)).booleanValue()) {
            BlockPos var6 = blockHitResult.getBlockPos();
            level.setBlock(var6, (BlockState)blockState.setValue(BlockStateProperties.LIT, Boolean.valueOf(true)), 11);
         }
      }

   }

   public static void makeParticles(Level level, BlockPos blockPos, boolean var2, boolean var3) {
      Random var4 = level.getRandom();
      SimpleParticleType var5 = var2?ParticleTypes.CAMPFIRE_SIGNAL_SMOKE:ParticleTypes.CAMPFIRE_COSY_SMOKE;
      level.addAlwaysVisibleParticle(var5, true, (double)blockPos.getX() + 0.5D + var4.nextDouble() / 3.0D * (double)(var4.nextBoolean()?1:-1), (double)blockPos.getY() + var4.nextDouble() + var4.nextDouble(), (double)blockPos.getZ() + 0.5D + var4.nextDouble() / 3.0D * (double)(var4.nextBoolean()?1:-1), 0.0D, 0.07D, 0.0D);
      if(var3) {
         level.addParticle(ParticleTypes.SMOKE, (double)blockPos.getX() + 0.25D + var4.nextDouble() / 2.0D * (double)(var4.nextBoolean()?1:-1), (double)blockPos.getY() + 0.4D, (double)blockPos.getZ() + 0.25D + var4.nextDouble() / 2.0D * (double)(var4.nextBoolean()?1:-1), 0.0D, 0.005D, 0.0D);
      }

   }

   public FluidState getFluidState(BlockState blockState) {
      return ((Boolean)blockState.getValue(WATERLOGGED)).booleanValue()?Fluids.WATER.getSource(false):super.getFluidState(blockState);
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(FACING, rotation.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return var1.rotate(mirror.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{LIT, SIGNAL_FIRE, WATERLOGGED, FACING});
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new CampfireBlockEntity();
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
