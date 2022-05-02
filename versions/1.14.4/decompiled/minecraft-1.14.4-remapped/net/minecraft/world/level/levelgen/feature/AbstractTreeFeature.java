package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public abstract class AbstractTreeFeature extends Feature {
   public AbstractTreeFeature(Function function, boolean var2) {
      super(function, var2);
   }

   protected static boolean isFree(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
      return levelSimulatedReader.isStateAtPosition(blockPos, (blockState) -> {
         Block var1 = blockState.getBlock();
         return blockState.isAir() || blockState.is(BlockTags.LEAVES) || var1 == Blocks.GRASS_BLOCK || Block.equalsDirt(var1) || var1.is(BlockTags.LOGS) || var1.is(BlockTags.SAPLINGS) || var1 == Blocks.VINE;
      });
   }

   protected static boolean isAir(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
      return levelSimulatedReader.isStateAtPosition(blockPos, BlockState::isAir);
   }

   protected static boolean isDirt(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
      return levelSimulatedReader.isStateAtPosition(blockPos, (blockState) -> {
         return Block.equalsDirt(blockState.getBlock());
      });
   }

   protected static boolean isBlockWater(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
      return levelSimulatedReader.isStateAtPosition(blockPos, (blockState) -> {
         return blockState.getBlock() == Blocks.WATER;
      });
   }

   protected static boolean isLeaves(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
      return levelSimulatedReader.isStateAtPosition(blockPos, (blockState) -> {
         return blockState.is(BlockTags.LEAVES);
      });
   }

   protected static boolean isAirOrLeaves(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
      return levelSimulatedReader.isStateAtPosition(blockPos, (blockState) -> {
         return blockState.isAir() || blockState.is(BlockTags.LEAVES);
      });
   }

   protected static boolean isGrassOrDirt(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
      return levelSimulatedReader.isStateAtPosition(blockPos, (blockState) -> {
         Block var1 = blockState.getBlock();
         return Block.equalsDirt(var1) || var1 == Blocks.GRASS_BLOCK;
      });
   }

   protected static boolean isGrassOrDirtOrFarmland(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
      return levelSimulatedReader.isStateAtPosition(blockPos, (blockState) -> {
         Block var1 = blockState.getBlock();
         return Block.equalsDirt(var1) || var1 == Blocks.GRASS_BLOCK || var1 == Blocks.FARMLAND;
      });
   }

   protected static boolean isReplaceablePlant(LevelSimulatedReader levelSimulatedReader, BlockPos blockPos) {
      return levelSimulatedReader.isStateAtPosition(blockPos, (blockState) -> {
         Material var1 = blockState.getMaterial();
         return var1 == Material.REPLACEABLE_PLANT;
      });
   }

   protected void setDirtAt(LevelSimulatedRW levelSimulatedRW, BlockPos blockPos) {
      if(!isDirt(levelSimulatedRW, blockPos)) {
         this.setBlock(levelSimulatedRW, blockPos, Blocks.DIRT.defaultBlockState());
      }

   }

   protected void setBlock(LevelWriter levelWriter, BlockPos blockPos, BlockState blockState) {
      this.setBlockKnownShape(levelWriter, blockPos, blockState);
   }

   protected final void setBlock(Set set, LevelWriter levelWriter, BlockPos blockPos, BlockState blockState, BoundingBox boundingBox) {
      this.setBlockKnownShape(levelWriter, blockPos, blockState);
      boundingBox.expand(new BoundingBox(blockPos, blockPos));
      if(BlockTags.LOGS.contains(blockState.getBlock())) {
         set.add(blockPos.immutable());
      }

   }

   private void setBlockKnownShape(LevelWriter levelWriter, BlockPos blockPos, BlockState blockState) {
      if(this.doUpdate) {
         levelWriter.setBlock(blockPos, blockState, 19);
      } else {
         levelWriter.setBlock(blockPos, blockState, 18);
      }

   }

   public final boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, FeatureConfiguration featureConfiguration) {
      Set<BlockPos> var6 = Sets.newHashSet();
      BoundingBox var7 = BoundingBox.getUnknownBox();
      boolean var8 = this.doPlace(var6, levelAccessor, random, blockPos, var7);
      if(var7.x0 > var7.x1) {
         return false;
      } else {
         List<Set<BlockPos>> var9 = Lists.newArrayList();
         int var10 = 6;

         for(int var11 = 0; var11 < 6; ++var11) {
            var9.add(Sets.newHashSet());
         }

         DiscreteVoxelShape var11 = new BitSetDiscreteVoxelShape(var7.getXSpan(), var7.getYSpan(), var7.getZSpan());
         BlockPos.PooledMutableBlockPos var12 = BlockPos.PooledMutableBlockPos.acquire();
         Throwable var13 = null;

         try {
            if(var8 && !var6.isEmpty()) {
               for(BlockPos var15 : Lists.newArrayList(var6)) {
                  if(var7.isInside(var15)) {
                     var11.setFull(var15.getX() - var7.x0, var15.getY() - var7.y0, var15.getZ() - var7.z0, true, true);
                  }

                  for(Direction var19 : Direction.values()) {
                     var12.set((Vec3i)var15).move(var19);
                     if(!var6.contains(var12)) {
                        BlockState var20 = levelAccessor.getBlockState(var12);
                        if(var20.hasProperty(BlockStateProperties.DISTANCE)) {
                           ((Set)var9.get(0)).add(var12.immutable());
                           this.setBlockKnownShape(levelAccessor, var12, (BlockState)var20.setValue(BlockStateProperties.DISTANCE, Integer.valueOf(1)));
                           if(var7.isInside(var12)) {
                              var11.setFull(var12.getX() - var7.x0, var12.getY() - var7.y0, var12.getZ() - var7.z0, true, true);
                           }
                        }
                     }
                  }
               }
            }

            for(int var14 = 1; var14 < 6; ++var14) {
               Set<BlockPos> var15 = (Set)var9.get(var14 - 1);
               Set<BlockPos> var16 = (Set)var9.get(var14);

               for(BlockPos var18 : var15) {
                  if(var7.isInside(var18)) {
                     var11.setFull(var18.getX() - var7.x0, var18.getY() - var7.y0, var18.getZ() - var7.z0, true, true);
                  }

                  for(Direction var22 : Direction.values()) {
                     var12.set((Vec3i)var18).move(var22);
                     if(!var15.contains(var12) && !var16.contains(var12)) {
                        BlockState var23 = levelAccessor.getBlockState(var12);
                        if(var23.hasProperty(BlockStateProperties.DISTANCE)) {
                           int var24 = ((Integer)var23.getValue(BlockStateProperties.DISTANCE)).intValue();
                           if(var24 > var14 + 1) {
                              BlockState var25 = (BlockState)var23.setValue(BlockStateProperties.DISTANCE, Integer.valueOf(var14 + 1));
                              this.setBlockKnownShape(levelAccessor, var12, var25);
                              if(var7.isInside(var12)) {
                                 var11.setFull(var12.getX() - var7.x0, var12.getY() - var7.y0, var12.getZ() - var7.z0, true, true);
                              }

                              var16.add(var12.immutable());
                           }
                        }
                     }
                  }
               }
            }
         } catch (Throwable var33) {
            var13 = var33;
            throw var33;
         } finally {
            if(var12 != null) {
               if(var13 != null) {
                  try {
                     var12.close();
                  } catch (Throwable var32) {
                     var13.addSuppressed(var32);
                  }
               } else {
                  var12.close();
               }
            }

         }

         StructureTemplate.updateShapeAtEdge(levelAccessor, 3, var11, var7.x0, var7.y0, var7.z0);
         return var8;
      }
   }

   protected abstract boolean doPlace(Set var1, LevelSimulatedRW var2, Random var3, BlockPos var4, BoundingBox var5);
}
