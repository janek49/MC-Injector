package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class LeavesBlock extends Block {
   public static final IntegerProperty DISTANCE = BlockStateProperties.DISTANCE;
   public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
   protected static boolean renderCutout;

   public LeavesBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(DISTANCE, Integer.valueOf(7))).setValue(PERSISTENT, Boolean.valueOf(false)));
   }

   public boolean isRandomlyTicking(BlockState blockState) {
      return ((Integer)blockState.getValue(DISTANCE)).intValue() == 7 && !((Boolean)blockState.getValue(PERSISTENT)).booleanValue();
   }

   public void randomTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!((Boolean)blockState.getValue(PERSISTENT)).booleanValue() && ((Integer)blockState.getValue(DISTANCE)).intValue() == 7) {
         dropResources(blockState, level, blockPos);
         level.removeBlock(blockPos, false);
      }

   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      level.setBlock(blockPos, updateDistance(blockState, level, blockPos), 3);
   }

   public int getLightBlock(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return 1;
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      int var7 = getDistanceAt(var3) + 1;
      if(var7 != 1 || ((Integer)var1.getValue(DISTANCE)).intValue() != var7) {
         levelAccessor.getBlockTicks().scheduleTick(var5, this, 1);
      }

      return var1;
   }

   private static BlockState updateDistance(BlockState var0, LevelAccessor levelAccessor, BlockPos blockPos) {
      int var3 = 7;
      BlockPos.PooledMutableBlockPos var4 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var5 = null;

      try {
         for(Direction var9 : Direction.values()) {
            var4.set((Vec3i)blockPos).move(var9);
            var3 = Math.min(var3, getDistanceAt(levelAccessor.getBlockState(var4)) + 1);
            if(var3 == 1) {
               break;
            }
         }
      } catch (Throwable var17) {
         var5 = var17;
         throw var17;
      } finally {
         if(var4 != null) {
            if(var5 != null) {
               try {
                  var4.close();
               } catch (Throwable var16) {
                  var5.addSuppressed(var16);
               }
            } else {
               var4.close();
            }
         }

      }

      return (BlockState)var0.setValue(DISTANCE, Integer.valueOf(var3));
   }

   private static int getDistanceAt(BlockState blockState) {
      return BlockTags.LOGS.contains(blockState.getBlock())?0:(blockState.getBlock() instanceof LeavesBlock?((Integer)blockState.getValue(DISTANCE)).intValue():7);
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(level.isRainingAt(blockPos.above())) {
         if(random.nextInt(15) == 1) {
            BlockPos blockPos = blockPos.below();
            BlockState var6 = level.getBlockState(blockPos);
            if(!var6.canOcclude() || !var6.isFaceSturdy(level, blockPos, Direction.UP)) {
               double var7 = (double)((float)blockPos.getX() + random.nextFloat());
               double var9 = (double)blockPos.getY() - 0.05D;
               double var11 = (double)((float)blockPos.getZ() + random.nextFloat());
               level.addParticle(ParticleTypes.DRIPPING_WATER, var7, var9, var11, 0.0D, 0.0D, 0.0D);
            }
         }
      }
   }

   public static void setFancy(boolean fancy) {
      renderCutout = fancy;
   }

   public boolean canOcclude(BlockState blockState) {
      return false;
   }

   public BlockLayer getRenderLayer() {
      return renderCutout?BlockLayer.CUTOUT_MIPPED:BlockLayer.SOLID;
   }

   public boolean isViewBlocking(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return false;
   }

   public boolean isValidSpawn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, EntityType entityType) {
      return entityType == EntityType.OCELOT || entityType == EntityType.PARROT;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{DISTANCE, PERSISTENT});
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return updateDistance((BlockState)this.defaultBlockState().setValue(PERSISTENT, Boolean.valueOf(true)), blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos());
   }
}
