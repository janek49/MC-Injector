package net.minecraft.world.level.block;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class TntBlock extends Block {
   public static final BooleanProperty UNSTABLE = BlockStateProperties.UNSTABLE;

   public TntBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(UNSTABLE, Boolean.valueOf(false)));
   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var4.getBlock() != var1.getBlock()) {
         if(level.hasNeighborSignal(blockPos)) {
            explode(level, blockPos);
            level.removeBlock(blockPos, false);
         }

      }
   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      if(level.hasNeighborSignal(var3)) {
         explode(level, var3);
         level.removeBlock(var3, false);
      }

   }

   public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
      if(!level.isClientSide() && !player.isCreative() && ((Boolean)blockState.getValue(UNSTABLE)).booleanValue()) {
         explode(level, blockPos);
      }

      super.playerWillDestroy(level, blockPos, blockState, player);
   }

   public void wasExploded(Level level, BlockPos blockPos, Explosion explosion) {
      if(!level.isClientSide) {
         PrimedTnt var4 = new PrimedTnt(level, (double)((float)blockPos.getX() + 0.5F), (double)blockPos.getY(), (double)((float)blockPos.getZ() + 0.5F), explosion.getSourceMob());
         var4.setFuse((short)(level.random.nextInt(var4.getLife() / 4) + var4.getLife() / 8));
         level.addFreshEntity(var4);
      }
   }

   public static void explode(Level level, BlockPos blockPos) {
      explode(level, blockPos, (LivingEntity)null);
   }

   private static void explode(Level level, BlockPos blockPos, @Nullable LivingEntity livingEntity) {
      if(!level.isClientSide) {
         PrimedTnt var3 = new PrimedTnt(level, (double)((float)blockPos.getX() + 0.5F), (double)blockPos.getY(), (double)((float)blockPos.getZ() + 0.5F), livingEntity);
         level.addFreshEntity(var3);
         level.playSound((Player)null, var3.x, var3.y, var3.z, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
      }
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      ItemStack var7 = player.getItemInHand(interactionHand);
      Item var8 = var7.getItem();
      if(var8 != Items.FLINT_AND_STEEL && var8 != Items.FIRE_CHARGE) {
         return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
      } else {
         explode(level, blockPos, player);
         level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 11);
         if(var8 == Items.FLINT_AND_STEEL) {
            var7.hurtAndBreak(1, player, (player) -> {
               player.broadcastBreakEvent(interactionHand);
            });
         } else {
            var7.shrink(1);
         }

         return true;
      }
   }

   public void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
      if(!level.isClientSide && entity instanceof AbstractArrow) {
         AbstractArrow var5 = (AbstractArrow)entity;
         Entity var6 = var5.getOwner();
         if(var5.isOnFire()) {
            BlockPos var7 = blockHitResult.getBlockPos();
            explode(level, var7, var6 instanceof LivingEntity?(LivingEntity)var6:null);
            level.removeBlock(var7, false);
         }
      }

   }

   public boolean dropFromExplosion(Explosion explosion) {
      return false;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{UNSTABLE});
   }
}
