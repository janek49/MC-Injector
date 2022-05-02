package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class ChorusFlowerBlock extends Block {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_5;
   private final ChorusPlantBlock plant;

   protected ChorusFlowerBlock(ChorusPlantBlock plant, Block.Properties block$Properties) {
      super(block$Properties);
      this.plant = plant;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, Integer.valueOf(0)));
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!blockState.canSurvive(level, blockPos)) {
         level.destroyBlock(blockPos, true);
      } else {
         BlockPos blockPos = blockPos.above();
         if(level.isEmptyBlock(blockPos) && blockPos.getY() < 256) {
            int var6 = ((Integer)blockState.getValue(AGE)).intValue();
            if(var6 < 5) {
               boolean var7 = false;
               boolean var8 = false;
               BlockState var9 = level.getBlockState(blockPos.below());
               Block var10 = var9.getBlock();
               if(var10 == Blocks.END_STONE) {
                  var7 = true;
               } else if(var10 == this.plant) {
                  int var11 = 1;

                  for(int var12 = 0; var12 < 4; ++var12) {
                     Block var13 = level.getBlockState(blockPos.below(var11 + 1)).getBlock();
                     if(var13 != this.plant) {
                        if(var13 == Blocks.END_STONE) {
                           var8 = true;
                        }
                        break;
                     }

                     ++var11;
                  }

                  if(var11 < 2 || var11 <= random.nextInt(var8?5:4)) {
                     var7 = true;
                  }
               } else if(var9.isAir()) {
                  var7 = true;
               }

               if(var7 && allNeighborsEmpty(level, blockPos, (Direction)null) && level.isEmptyBlock(blockPos.above(2))) {
                  level.setBlock(blockPos, this.plant.getStateForPlacement(level, blockPos), 2);
                  this.placeGrownFlower(level, blockPos, var6);
               } else if(var6 < 4) {
                  int var11 = random.nextInt(4);
                  if(var8) {
                     ++var11;
                  }

                  boolean var12 = false;

                  for(int var13 = 0; var13 < var11; ++var13) {
                     Direction var14 = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                     BlockPos var15 = blockPos.relative(var14);
                     if(level.isEmptyBlock(var15) && level.isEmptyBlock(var15.below()) && allNeighborsEmpty(level, var15, var14.getOpposite())) {
                        this.placeGrownFlower(level, var15, var6 + 1);
                        var12 = true;
                     }
                  }

                  if(var12) {
                     level.setBlock(blockPos, this.plant.getStateForPlacement(level, blockPos), 2);
                  } else {
                     this.placeDeadFlower(level, blockPos);
                  }
               } else {
                  this.placeDeadFlower(level, blockPos);
               }

            }
         }
      }
   }

   private void placeGrownFlower(Level level, BlockPos blockPos, int var3) {
      level.setBlock(blockPos, (BlockState)this.defaultBlockState().setValue(AGE, Integer.valueOf(var3)), 2);
      level.levelEvent(1033, blockPos, 0);
   }

   private void placeDeadFlower(Level level, BlockPos blockPos) {
      level.setBlock(blockPos, (BlockState)this.defaultBlockState().setValue(AGE, Integer.valueOf(5)), 2);
      level.levelEvent(1034, blockPos, 0);
   }

   private static boolean allNeighborsEmpty(LevelReader levelReader, BlockPos blockPos, @Nullable Direction direction) {
      for(Direction var4 : Direction.Plane.HORIZONTAL) {
         if(var4 != direction && !levelReader.isEmptyBlock(blockPos.relative(var4))) {
            return false;
         }
      }

      return true;
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(direction != Direction.UP && !var1.canSurvive(levelAccessor, var5)) {
         levelAccessor.getBlockTicks().scheduleTick(var5, this, 1);
      }

      return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      BlockState blockState = levelReader.getBlockState(blockPos.below());
      Block var5 = blockState.getBlock();
      if(var5 != this.plant && var5 != Blocks.END_STONE) {
         if(!blockState.isAir()) {
            return false;
         } else {
            boolean var6 = false;

            for(Direction var8 : Direction.Plane.HORIZONTAL) {
               BlockState var9 = levelReader.getBlockState(blockPos.relative(var8));
               if(var9.getBlock() == this.plant) {
                  if(var6) {
                     return false;
                  }

                  var6 = true;
               } else if(!var9.isAir()) {
                  return false;
               }
            }

            return var6;
         }
      } else {
         return true;
      }
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{AGE});
   }

   public static void generatePlant(LevelAccessor levelAccessor, BlockPos blockPos, Random random, int var3) {
      levelAccessor.setBlock(blockPos, ((ChorusPlantBlock)Blocks.CHORUS_PLANT).getStateForPlacement(levelAccessor, blockPos), 2);
      growTreeRecursive(levelAccessor, blockPos, random, blockPos, var3, 0);
   }

   private static void growTreeRecursive(LevelAccessor levelAccessor, BlockPos var1, Random random, BlockPos var3, int var4, int var5) {
      ChorusPlantBlock var6 = (ChorusPlantBlock)Blocks.CHORUS_PLANT;
      int var7 = random.nextInt(4) + 1;
      if(var5 == 0) {
         ++var7;
      }

      for(int var8 = 0; var8 < var7; ++var8) {
         BlockPos var9 = var1.above(var8 + 1);
         if(!allNeighborsEmpty(levelAccessor, var9, (Direction)null)) {
            return;
         }

         levelAccessor.setBlock(var9, var6.getStateForPlacement(levelAccessor, var9), 2);
         levelAccessor.setBlock(var9.below(), var6.getStateForPlacement(levelAccessor, var9.below()), 2);
      }

      boolean var8 = false;
      if(var5 < 4) {
         int var9 = random.nextInt(4);
         if(var5 == 0) {
            ++var9;
         }

         for(int var10 = 0; var10 < var9; ++var10) {
            Direction var11 = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            BlockPos var12 = var1.above(var7).relative(var11);
            if(Math.abs(var12.getX() - var3.getX()) < var4 && Math.abs(var12.getZ() - var3.getZ()) < var4 && levelAccessor.isEmptyBlock(var12) && levelAccessor.isEmptyBlock(var12.below()) && allNeighborsEmpty(levelAccessor, var12, var11.getOpposite())) {
               var8 = true;
               levelAccessor.setBlock(var12, var6.getStateForPlacement(levelAccessor, var12), 2);
               levelAccessor.setBlock(var12.relative(var11.getOpposite()), var6.getStateForPlacement(levelAccessor, var12.relative(var11.getOpposite())), 2);
               growTreeRecursive(levelAccessor, var12, random, var3, var4, var5 + 1);
            }
         }
      }

      if(!var8) {
         levelAccessor.setBlock(var1.above(var7), (BlockState)Blocks.CHORUS_FLOWER.defaultBlockState().setValue(AGE, Integer.valueOf(5)), 2);
      }

   }

   public void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
      BlockPos var5 = blockHitResult.getBlockPos();
      popResource(level, var5, new ItemStack(this));
      level.destroyBlock(var5, true);
   }
}
