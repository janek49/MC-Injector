package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class RedStoneOreBlock extends Block {
   public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

   public RedStoneOreBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)));
   }

   public int getLightEmission(BlockState blockState) {
      return ((Boolean)blockState.getValue(LIT)).booleanValue()?super.getLightEmission(blockState):0;
   }

   public void attack(BlockState blockState, Level level, BlockPos blockPos, Player player) {
      interact(blockState, level, blockPos);
      super.attack(blockState, level, blockPos, player);
   }

   public void stepOn(Level level, BlockPos blockPos, Entity entity) {
      interact(level.getBlockState(blockPos), level, blockPos);
      super.stepOn(level, blockPos, entity);
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      interact(blockState, level, blockPos);
      return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
   }

   private static void interact(BlockState blockState, Level level, BlockPos blockPos) {
      spawnParticles(level, blockPos);
      if(!((Boolean)blockState.getValue(LIT)).booleanValue()) {
         level.setBlock(blockPos, (BlockState)blockState.setValue(LIT, Boolean.valueOf(true)), 3);
      }

   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(((Boolean)blockState.getValue(LIT)).booleanValue()) {
         level.setBlock(blockPos, (BlockState)blockState.setValue(LIT, Boolean.valueOf(false)), 3);
      }

   }

   public void spawnAfterBreak(BlockState blockState, Level level, BlockPos blockPos, ItemStack itemStack) {
      super.spawnAfterBreak(blockState, level, blockPos, itemStack);
      if(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, itemStack) == 0) {
         int var5 = 1 + level.random.nextInt(5);
         this.popExperience(level, blockPos, var5);
      }

   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(((Boolean)blockState.getValue(LIT)).booleanValue()) {
         spawnParticles(level, blockPos);
      }

   }

   private static void spawnParticles(Level level, BlockPos blockPos) {
      double var2 = 0.5625D;
      Random var4 = level.random;

      for(Direction var8 : Direction.values()) {
         BlockPos var9 = blockPos.relative(var8);
         if(!level.getBlockState(var9).isSolidRender(level, var9)) {
            Direction.Axis var10 = var8.getAxis();
            double var11 = var10 == Direction.Axis.X?0.5D + 0.5625D * (double)var8.getStepX():(double)var4.nextFloat();
            double var13 = var10 == Direction.Axis.Y?0.5D + 0.5625D * (double)var8.getStepY():(double)var4.nextFloat();
            double var15 = var10 == Direction.Axis.Z?0.5D + 0.5625D * (double)var8.getStepZ():(double)var4.nextFloat();
            level.addParticle(DustParticleOptions.REDSTONE, (double)blockPos.getX() + var11, (double)blockPos.getY() + var13, (double)blockPos.getZ() + var15, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{LIT});
   }
}
