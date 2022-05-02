package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TurtleEggBlock extends Block {
   private static final VoxelShape ONE_EGG_AABB = Block.box(3.0D, 0.0D, 3.0D, 12.0D, 7.0D, 12.0D);
   private static final VoxelShape MULTIPLE_EGGS_AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 7.0D, 15.0D);
   public static final IntegerProperty HATCH = BlockStateProperties.HATCH;
   public static final IntegerProperty EGGS = BlockStateProperties.EGGS;

   public TurtleEggBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HATCH, Integer.valueOf(0))).setValue(EGGS, Integer.valueOf(1)));
   }

   public void stepOn(Level level, BlockPos blockPos, Entity entity) {
      this.destroyEgg(level, blockPos, entity, 100);
      super.stepOn(level, blockPos, entity);
   }

   public void fallOn(Level level, BlockPos blockPos, Entity entity, float var4) {
      if(!(entity instanceof Zombie)) {
         this.destroyEgg(level, blockPos, entity, 3);
      }

      super.fallOn(level, blockPos, entity, var4);
   }

   private void destroyEgg(Level level, BlockPos blockPos, Entity entity, int var4) {
      if(!this.canDestroyEgg(level, entity)) {
         super.stepOn(level, blockPos, entity);
      } else {
         if(!level.isClientSide && level.random.nextInt(var4) == 0) {
            this.decreaseEggs(level, blockPos, level.getBlockState(blockPos));
         }

      }
   }

   private void decreaseEggs(Level level, BlockPos blockPos, BlockState blockState) {
      level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + level.random.nextFloat() * 0.2F);
      int var4 = ((Integer)blockState.getValue(EGGS)).intValue();
      if(var4 <= 1) {
         level.destroyBlock(blockPos, false);
      } else {
         level.setBlock(blockPos, (BlockState)blockState.setValue(EGGS, Integer.valueOf(var4 - 1)), 2);
         level.levelEvent(2001, blockPos, Block.getId(blockState));
      }

   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(this.shouldUpdateHatchLevel(level) && this.onSand(level, blockPos)) {
         int var5 = ((Integer)blockState.getValue(HATCH)).intValue();
         if(var5 < 2) {
            level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
            level.setBlock(blockPos, (BlockState)blockState.setValue(HATCH, Integer.valueOf(var5 + 1)), 2);
         } else {
            level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
            level.removeBlock(blockPos, false);
            if(!level.isClientSide) {
               for(int var6 = 0; var6 < ((Integer)blockState.getValue(EGGS)).intValue(); ++var6) {
                  level.levelEvent(2001, blockPos, Block.getId(blockState));
                  Turtle var7 = (Turtle)EntityType.TURTLE.create(level);
                  var7.setAge(-24000);
                  var7.setHomePos(blockPos);
                  var7.moveTo((double)blockPos.getX() + 0.3D + (double)var6 * 0.2D, (double)blockPos.getY(), (double)blockPos.getZ() + 0.3D, 0.0F, 0.0F);
                  level.addFreshEntity(var7);
               }
            }
         }
      }

   }

   private boolean onSand(BlockGetter blockGetter, BlockPos blockPos) {
      return blockGetter.getBlockState(blockPos.below()).getBlock() == Blocks.SAND;
   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(this.onSand(level, blockPos) && !level.isClientSide) {
         level.levelEvent(2005, blockPos, 0);
      }

   }

   private boolean shouldUpdateHatchLevel(Level level) {
      float var2 = level.getTimeOfDay(1.0F);
      return (double)var2 < 0.69D && (double)var2 > 0.65D?true:level.random.nextInt(500) == 0;
   }

   public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
      super.playerDestroy(level, player, blockPos, blockState, blockEntity, itemStack);
      this.decreaseEggs(level, blockPos, blockState);
   }

   public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
      return blockPlaceContext.getItemInHand().getItem() == this.asItem() && ((Integer)blockState.getValue(EGGS)).intValue() < 4?true:super.canBeReplaced(blockState, blockPlaceContext);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      BlockState blockState = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos());
      return blockState.getBlock() == this?(BlockState)blockState.setValue(EGGS, Integer.valueOf(Math.min(4, ((Integer)blockState.getValue(EGGS)).intValue() + 1))):super.getStateForPlacement(blockPlaceContext);
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return ((Integer)blockState.getValue(EGGS)).intValue() > 1?MULTIPLE_EGGS_AABB:ONE_EGG_AABB;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{HATCH, EGGS});
   }

   private boolean canDestroyEgg(Level level, Entity entity) {
      return entity instanceof Turtle?false:(entity instanceof LivingEntity && !(entity instanceof Player)?level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING):true);
   }
}
