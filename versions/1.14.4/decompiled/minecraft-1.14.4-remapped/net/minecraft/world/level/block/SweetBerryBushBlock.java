package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SweetBerryBushBlock extends BushBlock implements BonemealableBlock {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
   private static final VoxelShape SAPLING_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
   private static final VoxelShape MID_GROWTH_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

   public SweetBerryBushBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, Integer.valueOf(0)));
   }

   public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
      return new ItemStack(Items.SWEET_BERRIES);
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return ((Integer)blockState.getValue(AGE)).intValue() == 0?SAPLING_SHAPE:(((Integer)blockState.getValue(AGE)).intValue() < 3?MID_GROWTH_SHAPE:super.getShape(blockState, blockGetter, blockPos, collisionContext));
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      super.tick(blockState, level, blockPos, random);
      int var5 = ((Integer)blockState.getValue(AGE)).intValue();
      if(var5 < 3 && random.nextInt(5) == 0 && level.getRawBrightness(blockPos.above(), 0) >= 9) {
         level.setBlock(blockPos, (BlockState)blockState.setValue(AGE, Integer.valueOf(var5 + 1)), 2);
      }

   }

   public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      if(entity instanceof LivingEntity && entity.getType() != EntityType.FOX) {
         entity.makeStuckInBlock(blockState, new Vec3(0.800000011920929D, 0.75D, 0.800000011920929D));
         if(!level.isClientSide && ((Integer)blockState.getValue(AGE)).intValue() > 0 && (entity.xOld != entity.x || entity.zOld != entity.z)) {
            double var5 = Math.abs(entity.x - entity.xOld);
            double var7 = Math.abs(entity.z - entity.zOld);
            if(var5 >= 0.003000000026077032D || var7 >= 0.003000000026077032D) {
               entity.hurt(DamageSource.SWEET_BERRY_BUSH, 1.0F);
            }
         }

      }
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      int var7 = ((Integer)blockState.getValue(AGE)).intValue();
      boolean var8 = var7 == 3;
      if(!var8 && player.getItemInHand(interactionHand).getItem() == Items.BONE_MEAL) {
         return false;
      } else if(var7 > 1) {
         int var9 = 1 + level.random.nextInt(2);
         popResource(level, blockPos, new ItemStack(Items.SWEET_BERRIES, var9 + (var8?1:0)));
         level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
         level.setBlock(blockPos, (BlockState)blockState.setValue(AGE, Integer.valueOf(1)), 2);
         return true;
      } else {
         return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{AGE});
   }

   public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, boolean var4) {
      return ((Integer)blockState.getValue(AGE)).intValue() < 3;
   }

   public boolean isBonemealSuccess(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      return true;
   }

   public void performBonemeal(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      int var5 = Math.min(3, ((Integer)blockState.getValue(AGE)).intValue() + 1);
      level.setBlock(blockPos, (BlockState)blockState.setValue(AGE, Integer.valueOf(var5)), 2);
   }
}
