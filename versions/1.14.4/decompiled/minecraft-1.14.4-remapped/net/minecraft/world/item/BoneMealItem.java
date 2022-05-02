package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BoneMealItem extends Item {
   public BoneMealItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      Level var2 = useOnContext.getLevel();
      BlockPos var3 = useOnContext.getClickedPos();
      BlockPos var4 = var3.relative(useOnContext.getClickedFace());
      if(growCrop(useOnContext.getItemInHand(), var2, var3)) {
         if(!var2.isClientSide) {
            var2.levelEvent(2005, var3, 0);
         }

         return InteractionResult.SUCCESS;
      } else {
         BlockState var5 = var2.getBlockState(var3);
         boolean var6 = var5.isFaceSturdy(var2, var3, useOnContext.getClickedFace());
         if(var6 && growWaterPlant(useOnContext.getItemInHand(), var2, var4, useOnContext.getClickedFace())) {
            if(!var2.isClientSide) {
               var2.levelEvent(2005, var4, 0);
            }

            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   public static boolean growCrop(ItemStack itemStack, Level level, BlockPos blockPos) {
      BlockState var3 = level.getBlockState(blockPos);
      if(var3.getBlock() instanceof BonemealableBlock) {
         BonemealableBlock var4 = (BonemealableBlock)var3.getBlock();
         if(var4.isValidBonemealTarget(level, blockPos, var3, level.isClientSide)) {
            if(!level.isClientSide) {
               if(var4.isBonemealSuccess(level, level.random, blockPos, var3)) {
                  var4.performBonemeal(level, level.random, blockPos, var3);
               }

               itemStack.shrink(1);
            }

            return true;
         }
      }

      return false;
   }

   public static boolean growWaterPlant(ItemStack itemStack, Level level, BlockPos blockPos, @Nullable Direction direction) {
      if(level.getBlockState(blockPos).getBlock() == Blocks.WATER && level.getFluidState(blockPos).getAmount() == 8) {
         if(!level.isClientSide) {
            label47:
            for(int var4 = 0; var4 < 128; ++var4) {
               BlockPos var5 = blockPos;
               Biome var6 = level.getBiome(blockPos);
               BlockState var7 = Blocks.SEAGRASS.defaultBlockState();

               for(int var8 = 0; var8 < var4 / 16; ++var8) {
                  var5 = var5.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                  var6 = level.getBiome(var5);
                  if(level.getBlockState(var5).isCollisionShapeFullBlock(level, var5)) {
                     continue label47;
                  }
               }

               if(var6 == Biomes.WARM_OCEAN || var6 == Biomes.DEEP_WARM_OCEAN) {
                  if(var4 == 0 && direction != null && direction.getAxis().isHorizontal()) {
                     var7 = (BlockState)((Block)BlockTags.WALL_CORALS.getRandomElement(level.random)).defaultBlockState().setValue(BaseCoralWallFanBlock.FACING, direction);
                  } else if(random.nextInt(4) == 0) {
                     var7 = ((Block)BlockTags.UNDERWATER_BONEMEALS.getRandomElement(random)).defaultBlockState();
                  }
               }

               if(var7.getBlock().is(BlockTags.WALL_CORALS)) {
                  for(int var8 = 0; !var7.canSurvive(level, var5) && var8 < 4; ++var8) {
                     var7 = (BlockState)var7.setValue(BaseCoralWallFanBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
                  }
               }

               if(var7.canSurvive(level, var5)) {
                  BlockState var8 = level.getBlockState(var5);
                  if(var8.getBlock() == Blocks.WATER && level.getFluidState(var5).getAmount() == 8) {
                     level.setBlock(var5, var7, 3);
                  } else if(var8.getBlock() == Blocks.SEAGRASS && random.nextInt(10) == 0) {
                     ((BonemealableBlock)Blocks.SEAGRASS).performBonemeal(level, random, var5, var8);
                  }
               }
            }

            itemStack.shrink(1);
         }

         return true;
      } else {
         return false;
      }
   }

   public static void addGrowthParticles(LevelAccessor levelAccessor, BlockPos blockPos, int var2) {
      if(var2 == 0) {
         var2 = 15;
      }

      BlockState var3 = levelAccessor.getBlockState(blockPos);
      if(!var3.isAir()) {
         for(int var4 = 0; var4 < var2; ++var4) {
            double var5 = random.nextGaussian() * 0.02D;
            double var7 = random.nextGaussian() * 0.02D;
            double var9 = random.nextGaussian() * 0.02D;
            levelAccessor.addParticle(ParticleTypes.HAPPY_VILLAGER, (double)((float)blockPos.getX() + random.nextFloat()), (double)blockPos.getY() + (double)random.nextFloat() * var3.getShape(levelAccessor, blockPos).max(Direction.Axis.Y), (double)((float)blockPos.getZ() + random.nextFloat()), var5, var7, var9);
         }

      }
   }
}
