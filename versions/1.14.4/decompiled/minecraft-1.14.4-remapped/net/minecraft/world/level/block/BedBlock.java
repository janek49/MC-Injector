package net.minecraft.world.level.block;

import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BedBlock extends HorizontalDirectionalBlock implements EntityBlock {
   public static final EnumProperty PART = BlockStateProperties.BED_PART;
   public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
   protected static final VoxelShape BASE = Block.box(0.0D, 3.0D, 0.0D, 16.0D, 9.0D, 16.0D);
   protected static final VoxelShape LEG_NORTH_WEST = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 3.0D, 3.0D);
   protected static final VoxelShape LEG_SOUTH_WEST = Block.box(0.0D, 0.0D, 13.0D, 3.0D, 3.0D, 16.0D);
   protected static final VoxelShape LEG_NORTH_EAST = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 3.0D, 3.0D);
   protected static final VoxelShape LEG_SOUTH_EAST = Block.box(13.0D, 0.0D, 13.0D, 16.0D, 3.0D, 16.0D);
   protected static final VoxelShape NORTH_SHAPE = Shapes.or(BASE, new VoxelShape[]{LEG_NORTH_WEST, LEG_NORTH_EAST});
   protected static final VoxelShape SOUTH_SHAPE = Shapes.or(BASE, new VoxelShape[]{LEG_SOUTH_WEST, LEG_SOUTH_EAST});
   protected static final VoxelShape WEST_SHAPE = Shapes.or(BASE, new VoxelShape[]{LEG_NORTH_WEST, LEG_SOUTH_WEST});
   protected static final VoxelShape EAST_SHAPE = Shapes.or(BASE, new VoxelShape[]{LEG_NORTH_EAST, LEG_SOUTH_EAST});
   private final DyeColor color;

   public BedBlock(DyeColor color, Block.Properties block$Properties) {
      super(block$Properties);
      this.color = color;
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(PART, BedPart.FOOT)).setValue(OCCUPIED, Boolean.valueOf(false)));
   }

   public MaterialColor getMapColor(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return blockState.getValue(PART) == BedPart.FOOT?this.color.getMaterialColor():MaterialColor.WOOL;
   }

   @Nullable
   public static Direction getBedOrientation(BlockGetter blockGetter, BlockPos blockPos) {
      BlockState var2 = blockGetter.getBlockState(blockPos);
      return var2.getBlock() instanceof BedBlock?(Direction)var2.getValue(FACING):null;
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(level.isClientSide) {
         return true;
      } else {
         if(blockState.getValue(PART) != BedPart.HEAD) {
            blockPos = blockPos.relative((Direction)blockState.getValue(FACING));
            blockState = level.getBlockState(blockPos);
            if(blockState.getBlock() != this) {
               return true;
            }
         }

         if(level.dimension.mayRespawn() && level.getBiome(blockPos) != Biomes.NETHER) {
            if(((Boolean)blockState.getValue(OCCUPIED)).booleanValue()) {
               player.displayClientMessage(new TranslatableComponent("block.minecraft.bed.occupied", new Object[0]), true);
               return true;
            } else {
               player.startSleepInBed(blockPos).ifLeft((player$BedSleepingProblem) -> {
                  if(player$BedSleepingProblem != null) {
                     player.displayClientMessage(player$BedSleepingProblem.getMessage(), true);
                  }

               });
               return true;
            }
         } else {
            level.removeBlock(blockPos, false);
            BlockPos blockPos = blockPos.relative(((Direction)blockState.getValue(FACING)).getOpposite());
            if(level.getBlockState(blockPos).getBlock() == this) {
               level.removeBlock(blockPos, false);
            }

            level.explode((Entity)null, DamageSource.netherBedExplosion(), (double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D, 5.0F, true, Explosion.BlockInteraction.DESTROY);
            return true;
         }
      }
   }

   public void fallOn(Level level, BlockPos blockPos, Entity entity, float var4) {
      super.fallOn(level, blockPos, entity, var4 * 0.5F);
   }

   public void updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
      if(entity.isSneaking()) {
         super.updateEntityAfterFallOn(blockGetter, entity);
      } else {
         Vec3 var3 = entity.getDeltaMovement();
         if(var3.y < 0.0D) {
            double var4 = entity instanceof LivingEntity?1.0D:0.8D;
            entity.setDeltaMovement(var3.x, -var3.y * 0.6600000262260437D * var4, var3.z);
         }
      }

   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return direction == getNeighbourDirection((BedPart)var1.getValue(PART), (Direction)var1.getValue(FACING))?(var3.getBlock() == this && var3.getValue(PART) != var1.getValue(PART)?(BlockState)var1.setValue(OCCUPIED, var3.getValue(OCCUPIED)):Blocks.AIR.defaultBlockState()):super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   private static Direction getNeighbourDirection(BedPart bedPart, Direction var1) {
      return bedPart == BedPart.FOOT?var1:var1.getOpposite();
   }

   public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
      super.playerDestroy(level, player, blockPos, Blocks.AIR.defaultBlockState(), blockEntity, itemStack);
   }

   public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
      BedPart var5 = (BedPart)blockState.getValue(PART);
      BlockPos var6 = blockPos.relative(getNeighbourDirection(var5, (Direction)blockState.getValue(FACING)));
      BlockState var7 = level.getBlockState(var6);
      if(var7.getBlock() == this && var7.getValue(PART) != var5) {
         level.setBlock(var6, Blocks.AIR.defaultBlockState(), 35);
         level.levelEvent(player, 2001, var6, Block.getId(var7));
         if(!level.isClientSide && !player.isCreative()) {
            ItemStack var8 = player.getMainHandItem();
            dropResources(blockState, level, blockPos, (BlockEntity)null, player, var8);
            dropResources(var7, level, var6, (BlockEntity)null, player, var8);
         }

         player.awardStat(Stats.BLOCK_MINED.get(this));
      }

      super.playerWillDestroy(level, blockPos, blockState, player);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      Direction var2 = blockPlaceContext.getHorizontalDirection();
      BlockPos var3 = blockPlaceContext.getClickedPos();
      BlockPos var4 = var3.relative(var2);
      return blockPlaceContext.getLevel().getBlockState(var4).canBeReplaced(blockPlaceContext)?(BlockState)this.defaultBlockState().setValue(FACING, var2):null;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      Direction var5 = (Direction)blockState.getValue(FACING);
      Direction var6 = blockState.getValue(PART) == BedPart.HEAD?var5:var5.getOpposite();
      switch(var6) {
      case NORTH:
         return NORTH_SHAPE;
      case SOUTH:
         return SOUTH_SHAPE;
      case WEST:
         return WEST_SHAPE;
      default:
         return EAST_SHAPE;
      }
   }

   public boolean hasCustomBreakingProgress(BlockState blockState) {
      return true;
   }

   public static Optional findStandUpPosition(EntityType entityType, LevelReader levelReader, BlockPos blockPos, int var3) {
      Direction var4 = (Direction)levelReader.getBlockState(blockPos).getValue(FACING);
      int var5 = blockPos.getX();
      int var6 = blockPos.getY();
      int var7 = blockPos.getZ();

      for(int var8 = 0; var8 <= 1; ++var8) {
         int var9 = var5 - var4.getStepX() * var8 - 1;
         int var10 = var7 - var4.getStepZ() * var8 - 1;
         int var11 = var9 + 2;
         int var12 = var10 + 2;

         for(int var13 = var9; var13 <= var11; ++var13) {
            for(int var14 = var10; var14 <= var12; ++var14) {
               BlockPos var15 = new BlockPos(var13, var6, var14);
               Optional<Vec3> var16 = getStandingLocationAtOrBelow(entityType, levelReader, var15);
               if(var16.isPresent()) {
                  if(var3 <= 0) {
                     return var16;
                  }

                  --var3;
               }
            }
         }
      }

      return Optional.empty();
   }

   protected static Optional getStandingLocationAtOrBelow(EntityType entityType, LevelReader levelReader, BlockPos blockPos) {
      VoxelShape var3 = levelReader.getBlockState(blockPos).getCollisionShape(levelReader, blockPos);
      if(var3.max(Direction.Axis.Y) > 0.4375D) {
         return Optional.empty();
      } else {
         BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos(blockPos);

         while(var4.getY() >= 0 && blockPos.getY() - var4.getY() <= 2 && levelReader.getBlockState(var4).getCollisionShape(levelReader, var4).isEmpty()) {
            var4.move(Direction.DOWN);
         }

         VoxelShape var5 = levelReader.getBlockState(var4).getCollisionShape(levelReader, var4);
         if(var5.isEmpty()) {
            return Optional.empty();
         } else {
            double var6 = (double)var4.getY() + var5.max(Direction.Axis.Y) + 2.0E-7D;
            if((double)blockPos.getY() - var6 > 2.0D) {
               return Optional.empty();
            } else {
               float var8 = entityType.getWidth() / 2.0F;
               Vec3 var9 = new Vec3((double)var4.getX() + 0.5D, var6, (double)var4.getZ() + 0.5D);
               return levelReader.noCollision(new AABB(var9.x - (double)var8, var9.y, var9.z - (double)var8, var9.x + (double)var8, var9.y + (double)entityType.getHeight(), var9.z + (double)var8))?Optional.of(var9):Optional.empty();
            }
         }
      }
   }

   public PushReaction getPistonPushReaction(BlockState blockState) {
      return PushReaction.DESTROY;
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, PART, OCCUPIED});
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new BedBlockEntity(this.color);
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
      super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
      if(!level.isClientSide) {
         BlockPos blockPos = blockPos.relative((Direction)blockState.getValue(FACING));
         level.setBlock(blockPos, (BlockState)blockState.setValue(PART, BedPart.HEAD), 3);
         level.blockUpdated(blockPos, Blocks.AIR);
         blockState.updateNeighbourShapes(level, blockPos, 3);
      }

   }

   public DyeColor getColor() {
      return this.color;
   }

   public long getSeed(BlockState blockState, BlockPos blockPos) {
      BlockPos blockPos = blockPos.relative((Direction)blockState.getValue(FACING), blockState.getValue(PART) == BedPart.HEAD?0:1);
      return Mth.getSeed(blockPos.getX(), blockPos.getY(), blockPos.getZ());
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
