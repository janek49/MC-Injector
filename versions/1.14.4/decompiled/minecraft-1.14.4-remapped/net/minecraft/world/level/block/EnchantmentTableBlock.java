package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EnchantmentTableBlock extends BaseEntityBlock {
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

   protected EnchantmentTableBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public boolean useShapeForLightOcclusion(BlockState blockState) {
      return true;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      super.animateTick(blockState, level, blockPos, random);

      for(int var5 = -2; var5 <= 2; ++var5) {
         for(int var6 = -2; var6 <= 2; ++var6) {
            if(var5 > -2 && var5 < 2 && var6 == -1) {
               var6 = 2;
            }

            if(random.nextInt(16) == 0) {
               for(int var7 = 0; var7 <= 1; ++var7) {
                  BlockPos var8 = blockPos.offset(var5, var7, var6);
                  if(level.getBlockState(var8).getBlock() == Blocks.BOOKSHELF) {
                     if(!level.isEmptyBlock(blockPos.offset(var5 / 2, 0, var6 / 2))) {
                        break;
                     }

                     level.addParticle(ParticleTypes.ENCHANT, (double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 2.0D, (double)blockPos.getZ() + 0.5D, (double)((float)var5 + random.nextFloat()) - 0.5D, (double)((float)var7 - random.nextFloat() - 1.0F), (double)((float)var6 + random.nextFloat()) - 0.5D);
                  }
               }
            }
         }
      }

   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.MODEL;
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new EnchantmentTableBlockEntity();
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(level.isClientSide) {
         return true;
      } else {
         player.openMenu(blockState.getMenuProvider(level, blockPos));
         return true;
      }
   }

   @Nullable
   public MenuProvider getMenuProvider(BlockState blockState, Level level, BlockPos blockPos) {
      BlockEntity var4 = level.getBlockEntity(blockPos);
      if(var4 instanceof EnchantmentTableBlockEntity) {
         Component var5 = ((Nameable)var4).getDisplayName();
         return new SimpleMenuProvider((var2, inventory, player) -> {
            return new EnchantmentMenu(var2, inventory, ContainerLevelAccess.create(level, blockPos));
         }, var5);
      } else {
         return null;
      }
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
      if(itemStack.hasCustomHoverName()) {
         BlockEntity var6 = level.getBlockEntity(blockPos);
         if(var6 instanceof EnchantmentTableBlockEntity) {
            ((EnchantmentTableBlockEntity)var6).setCustomName(itemStack.getHoverName());
         }
      }

   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
