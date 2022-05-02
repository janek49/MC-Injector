package net.minecraft.world.level.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.end.TheEndDimension;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FireBlock extends Block {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
   public static final BooleanProperty NORTH = PipeBlock.NORTH;
   public static final BooleanProperty EAST = PipeBlock.EAST;
   public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
   public static final BooleanProperty WEST = PipeBlock.WEST;
   public static final BooleanProperty UP = PipeBlock.UP;
   private static final Map PROPERTY_BY_DIRECTION = (Map)PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((map$Entry) -> {
      return map$Entry.getKey() != Direction.DOWN;
   }).collect(Util.toMap());
   private final Object2IntMap flameOdds = new Object2IntOpenHashMap();
   private final Object2IntMap burnOdds = new Object2IntOpenHashMap();

   protected FireBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, Integer.valueOf(0))).setValue(NORTH, Boolean.valueOf(false))).setValue(EAST, Boolean.valueOf(false))).setValue(SOUTH, Boolean.valueOf(false))).setValue(WEST, Boolean.valueOf(false))).setValue(UP, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return Shapes.empty();
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return this.canSurvive(var1, levelAccessor, var5)?(BlockState)this.getStateForPlacement(levelAccessor, var5).setValue(AGE, var1.getValue(AGE)):Blocks.AIR.defaultBlockState();
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return this.getStateForPlacement(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos());
   }

   public BlockState getStateForPlacement(BlockGetter blockGetter, BlockPos blockPos) {
      BlockPos blockPos = blockPos.below();
      BlockState var4 = blockGetter.getBlockState(blockPos);
      if(!this.canBurn(var4) && !var4.isFaceSturdy(blockGetter, blockPos, Direction.UP)) {
         BlockState var5 = this.defaultBlockState();

         for(Direction var9 : Direction.values()) {
            BooleanProperty var10 = (BooleanProperty)PROPERTY_BY_DIRECTION.get(var9);
            if(var10 != null) {
               var5 = (BlockState)var5.setValue(var10, Boolean.valueOf(this.canBurn(blockGetter.getBlockState(blockPos.relative(var9)))));
            }
         }

         return var5;
      } else {
         return this.defaultBlockState();
      }
   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      BlockPos blockPos = blockPos.below();
      return levelReader.getBlockState(blockPos).isFaceSturdy(levelReader, blockPos, Direction.UP) || this.isValidFireLocation(levelReader, blockPos);
   }

   public int getTickDelay(LevelReader levelReader) {
      return 30;
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
         if(!blockState.canSurvive(level, blockPos)) {
            level.removeBlock(blockPos, false);
         }

         Block var5 = level.getBlockState(blockPos.below()).getBlock();
         boolean var6 = level.dimension instanceof TheEndDimension && var5 == Blocks.BEDROCK || var5 == Blocks.NETHERRACK || var5 == Blocks.MAGMA_BLOCK;
         int var7 = ((Integer)blockState.getValue(AGE)).intValue();
         if(!var6 && level.isRaining() && this.isNearRain(level, blockPos) && random.nextFloat() < 0.2F + (float)var7 * 0.03F) {
            level.removeBlock(blockPos, false);
         } else {
            int var8 = Math.min(15, var7 + random.nextInt(3) / 2);
            if(var7 != var8) {
               blockState = (BlockState)blockState.setValue(AGE, Integer.valueOf(var8));
               level.setBlock(blockPos, blockState, 4);
            }

            if(!var6) {
               level.getBlockTicks().scheduleTick(blockPos, this, this.getTickDelay(level) + random.nextInt(10));
               if(!this.isValidFireLocation(level, blockPos)) {
                  BlockPos var9 = blockPos.below();
                  if(!level.getBlockState(var9).isFaceSturdy(level, var9, Direction.UP) || var7 > 3) {
                     level.removeBlock(blockPos, false);
                  }

                  return;
               }

               if(var7 == 15 && random.nextInt(4) == 0 && !this.canBurn(level.getBlockState(blockPos.below()))) {
                  level.removeBlock(blockPos, false);
                  return;
               }
            }

            boolean var9 = level.isHumidAt(blockPos);
            int var10 = var9?-50:0;
            this.checkBurnOut(level, blockPos.east(), 300 + var10, random, var7);
            this.checkBurnOut(level, blockPos.west(), 300 + var10, random, var7);
            this.checkBurnOut(level, blockPos.below(), 250 + var10, random, var7);
            this.checkBurnOut(level, blockPos.above(), 250 + var10, random, var7);
            this.checkBurnOut(level, blockPos.north(), 300 + var10, random, var7);
            this.checkBurnOut(level, blockPos.south(), 300 + var10, random, var7);
            BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();

            for(int var12 = -1; var12 <= 1; ++var12) {
               for(int var13 = -1; var13 <= 1; ++var13) {
                  for(int var14 = -1; var14 <= 4; ++var14) {
                     if(var12 != 0 || var14 != 0 || var13 != 0) {
                        int var15 = 100;
                        if(var14 > 1) {
                           var15 += (var14 - 1) * 100;
                        }

                        var11.set((Vec3i)blockPos).move(var12, var14, var13);
                        int var16 = this.getFireOdds(level, var11);
                        if(var16 > 0) {
                           int var17 = (var16 + 40 + level.getDifficulty().getId() * 7) / (var7 + 30);
                           if(var9) {
                              var17 /= 2;
                           }

                           if(var17 > 0 && random.nextInt(var15) <= var17 && (!level.isRaining() || !this.isNearRain(level, var11))) {
                              int var18 = Math.min(15, var7 + random.nextInt(5) / 4);
                              level.setBlock(var11, (BlockState)this.getStateForPlacement(level, var11).setValue(AGE, Integer.valueOf(var18)), 3);
                           }
                        }
                     }
                  }
               }
            }

         }
      }
   }

   protected boolean isNearRain(Level level, BlockPos blockPos) {
      return level.isRainingAt(blockPos) || level.isRainingAt(blockPos.west()) || level.isRainingAt(blockPos.east()) || level.isRainingAt(blockPos.north()) || level.isRainingAt(blockPos.south());
   }

   private int getBurnOdd(BlockState blockState) {
      return blockState.hasProperty(BlockStateProperties.WATERLOGGED) && ((Boolean)blockState.getValue(BlockStateProperties.WATERLOGGED)).booleanValue()?0:this.burnOdds.getInt(blockState.getBlock());
   }

   private int getFlameOdds(BlockState blockState) {
      return blockState.hasProperty(BlockStateProperties.WATERLOGGED) && ((Boolean)blockState.getValue(BlockStateProperties.WATERLOGGED)).booleanValue()?0:this.flameOdds.getInt(blockState.getBlock());
   }

   private void checkBurnOut(Level level, BlockPos blockPos, int var3, Random random, int var5) {
      int var6 = this.getBurnOdd(level.getBlockState(blockPos));
      if(random.nextInt(var3) < var6) {
         BlockState var7 = level.getBlockState(blockPos);
         if(random.nextInt(var5 + 10) < 5 && !level.isRainingAt(blockPos)) {
            int var8 = Math.min(var5 + random.nextInt(5) / 4, 15);
            level.setBlock(blockPos, (BlockState)this.getStateForPlacement(level, blockPos).setValue(AGE, Integer.valueOf(var8)), 3);
         } else {
            level.removeBlock(blockPos, false);
         }

         Block var8 = var7.getBlock();
         if(var8 instanceof TntBlock) {
            TntBlock var10000 = (TntBlock)var8;
            TntBlock.explode(level, blockPos);
         }
      }

   }

   private boolean isValidFireLocation(BlockGetter blockGetter, BlockPos blockPos) {
      for(Direction var6 : Direction.values()) {
         if(this.canBurn(blockGetter.getBlockState(blockPos.relative(var6)))) {
            return true;
         }
      }

      return false;
   }

   private int getFireOdds(LevelReader levelReader, BlockPos blockPos) {
      if(!levelReader.isEmptyBlock(blockPos)) {
         return 0;
      } else {
         int var3 = 0;

         for(Direction var7 : Direction.values()) {
            BlockState var8 = levelReader.getBlockState(blockPos.relative(var7));
            var3 = Math.max(this.getFlameOdds(var8), var3);
         }

         return var3;
      }
   }

   public boolean canBurn(BlockState blockState) {
      return this.getFlameOdds(blockState) > 0;
   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var4.getBlock() != var1.getBlock()) {
         if(level.dimension.getType() != DimensionType.OVERWORLD && level.dimension.getType() != DimensionType.NETHER || !((NetherPortalBlock)Blocks.NETHER_PORTAL).trySpawnPortal(level, blockPos)) {
            if(!var1.canSurvive(level, blockPos)) {
               level.removeBlock(blockPos, false);
            } else {
               level.getBlockTicks().scheduleTick(blockPos, this, this.getTickDelay(level) + level.random.nextInt(10));
            }
         }
      }
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(random.nextInt(24) == 0) {
         level.playLocalSound((double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.5F), (double)((float)blockPos.getZ() + 0.5F), SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
      }

      BlockPos blockPos = blockPos.below();
      BlockState var6 = level.getBlockState(blockPos);
      if(!this.canBurn(var6) && !var6.isFaceSturdy(level, blockPos, Direction.UP)) {
         if(this.canBurn(level.getBlockState(blockPos.west()))) {
            for(int var7 = 0; var7 < 2; ++var7) {
               double var8 = (double)blockPos.getX() + random.nextDouble() * 0.10000000149011612D;
               double var10 = (double)blockPos.getY() + random.nextDouble();
               double var12 = (double)blockPos.getZ() + random.nextDouble();
               level.addParticle(ParticleTypes.LARGE_SMOKE, var8, var10, var12, 0.0D, 0.0D, 0.0D);
            }
         }

         if(this.canBurn(level.getBlockState(blockPos.east()))) {
            for(int var7 = 0; var7 < 2; ++var7) {
               double var8 = (double)(blockPos.getX() + 1) - random.nextDouble() * 0.10000000149011612D;
               double var10 = (double)blockPos.getY() + random.nextDouble();
               double var12 = (double)blockPos.getZ() + random.nextDouble();
               level.addParticle(ParticleTypes.LARGE_SMOKE, var8, var10, var12, 0.0D, 0.0D, 0.0D);
            }
         }

         if(this.canBurn(level.getBlockState(blockPos.north()))) {
            for(int var7 = 0; var7 < 2; ++var7) {
               double var8 = (double)blockPos.getX() + random.nextDouble();
               double var10 = (double)blockPos.getY() + random.nextDouble();
               double var12 = (double)blockPos.getZ() + random.nextDouble() * 0.10000000149011612D;
               level.addParticle(ParticleTypes.LARGE_SMOKE, var8, var10, var12, 0.0D, 0.0D, 0.0D);
            }
         }

         if(this.canBurn(level.getBlockState(blockPos.south()))) {
            for(int var7 = 0; var7 < 2; ++var7) {
               double var8 = (double)blockPos.getX() + random.nextDouble();
               double var10 = (double)blockPos.getY() + random.nextDouble();
               double var12 = (double)(blockPos.getZ() + 1) - random.nextDouble() * 0.10000000149011612D;
               level.addParticle(ParticleTypes.LARGE_SMOKE, var8, var10, var12, 0.0D, 0.0D, 0.0D);
            }
         }

         if(this.canBurn(level.getBlockState(blockPos.above()))) {
            for(int var7 = 0; var7 < 2; ++var7) {
               double var8 = (double)blockPos.getX() + random.nextDouble();
               double var10 = (double)(blockPos.getY() + 1) - random.nextDouble() * 0.10000000149011612D;
               double var12 = (double)blockPos.getZ() + random.nextDouble();
               level.addParticle(ParticleTypes.LARGE_SMOKE, var8, var10, var12, 0.0D, 0.0D, 0.0D);
            }
         }
      } else {
         for(int var7 = 0; var7 < 3; ++var7) {
            double var8 = (double)blockPos.getX() + random.nextDouble();
            double var10 = (double)blockPos.getY() + random.nextDouble() * 0.5D + 0.5D;
            double var12 = (double)blockPos.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.LARGE_SMOKE, var8, var10, var12, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{AGE, NORTH, EAST, SOUTH, WEST, UP});
   }

   public void setFlammable(Block block, int var2, int var3) {
      this.flameOdds.put(block, var2);
      this.burnOdds.put(block, var3);
   }

   public static void bootStrap() {
      FireBlock var0 = (FireBlock)Blocks.FIRE;
      var0.setFlammable(Blocks.OAK_PLANKS, 5, 20);
      var0.setFlammable(Blocks.SPRUCE_PLANKS, 5, 20);
      var0.setFlammable(Blocks.BIRCH_PLANKS, 5, 20);
      var0.setFlammable(Blocks.JUNGLE_PLANKS, 5, 20);
      var0.setFlammable(Blocks.ACACIA_PLANKS, 5, 20);
      var0.setFlammable(Blocks.DARK_OAK_PLANKS, 5, 20);
      var0.setFlammable(Blocks.OAK_SLAB, 5, 20);
      var0.setFlammable(Blocks.SPRUCE_SLAB, 5, 20);
      var0.setFlammable(Blocks.BIRCH_SLAB, 5, 20);
      var0.setFlammable(Blocks.JUNGLE_SLAB, 5, 20);
      var0.setFlammable(Blocks.ACACIA_SLAB, 5, 20);
      var0.setFlammable(Blocks.DARK_OAK_SLAB, 5, 20);
      var0.setFlammable(Blocks.OAK_FENCE_GATE, 5, 20);
      var0.setFlammable(Blocks.SPRUCE_FENCE_GATE, 5, 20);
      var0.setFlammable(Blocks.BIRCH_FENCE_GATE, 5, 20);
      var0.setFlammable(Blocks.JUNGLE_FENCE_GATE, 5, 20);
      var0.setFlammable(Blocks.DARK_OAK_FENCE_GATE, 5, 20);
      var0.setFlammable(Blocks.ACACIA_FENCE_GATE, 5, 20);
      var0.setFlammable(Blocks.OAK_FENCE, 5, 20);
      var0.setFlammable(Blocks.SPRUCE_FENCE, 5, 20);
      var0.setFlammable(Blocks.BIRCH_FENCE, 5, 20);
      var0.setFlammable(Blocks.JUNGLE_FENCE, 5, 20);
      var0.setFlammable(Blocks.DARK_OAK_FENCE, 5, 20);
      var0.setFlammable(Blocks.ACACIA_FENCE, 5, 20);
      var0.setFlammable(Blocks.OAK_STAIRS, 5, 20);
      var0.setFlammable(Blocks.BIRCH_STAIRS, 5, 20);
      var0.setFlammable(Blocks.SPRUCE_STAIRS, 5, 20);
      var0.setFlammable(Blocks.JUNGLE_STAIRS, 5, 20);
      var0.setFlammable(Blocks.ACACIA_STAIRS, 5, 20);
      var0.setFlammable(Blocks.DARK_OAK_STAIRS, 5, 20);
      var0.setFlammable(Blocks.OAK_LOG, 5, 5);
      var0.setFlammable(Blocks.SPRUCE_LOG, 5, 5);
      var0.setFlammable(Blocks.BIRCH_LOG, 5, 5);
      var0.setFlammable(Blocks.JUNGLE_LOG, 5, 5);
      var0.setFlammable(Blocks.ACACIA_LOG, 5, 5);
      var0.setFlammable(Blocks.DARK_OAK_LOG, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_OAK_LOG, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_SPRUCE_LOG, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_BIRCH_LOG, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_JUNGLE_LOG, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_ACACIA_LOG, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_DARK_OAK_LOG, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_OAK_WOOD, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_SPRUCE_WOOD, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_BIRCH_WOOD, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_JUNGLE_WOOD, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_ACACIA_WOOD, 5, 5);
      var0.setFlammable(Blocks.STRIPPED_DARK_OAK_WOOD, 5, 5);
      var0.setFlammable(Blocks.OAK_WOOD, 5, 5);
      var0.setFlammable(Blocks.SPRUCE_WOOD, 5, 5);
      var0.setFlammable(Blocks.BIRCH_WOOD, 5, 5);
      var0.setFlammable(Blocks.JUNGLE_WOOD, 5, 5);
      var0.setFlammable(Blocks.ACACIA_WOOD, 5, 5);
      var0.setFlammable(Blocks.DARK_OAK_WOOD, 5, 5);
      var0.setFlammable(Blocks.OAK_LEAVES, 30, 60);
      var0.setFlammable(Blocks.SPRUCE_LEAVES, 30, 60);
      var0.setFlammable(Blocks.BIRCH_LEAVES, 30, 60);
      var0.setFlammable(Blocks.JUNGLE_LEAVES, 30, 60);
      var0.setFlammable(Blocks.ACACIA_LEAVES, 30, 60);
      var0.setFlammable(Blocks.DARK_OAK_LEAVES, 30, 60);
      var0.setFlammable(Blocks.BOOKSHELF, 30, 20);
      var0.setFlammable(Blocks.TNT, 15, 100);
      var0.setFlammable(Blocks.GRASS, 60, 100);
      var0.setFlammable(Blocks.FERN, 60, 100);
      var0.setFlammable(Blocks.DEAD_BUSH, 60, 100);
      var0.setFlammable(Blocks.SUNFLOWER, 60, 100);
      var0.setFlammable(Blocks.LILAC, 60, 100);
      var0.setFlammable(Blocks.ROSE_BUSH, 60, 100);
      var0.setFlammable(Blocks.PEONY, 60, 100);
      var0.setFlammable(Blocks.TALL_GRASS, 60, 100);
      var0.setFlammable(Blocks.LARGE_FERN, 60, 100);
      var0.setFlammable(Blocks.DANDELION, 60, 100);
      var0.setFlammable(Blocks.POPPY, 60, 100);
      var0.setFlammable(Blocks.BLUE_ORCHID, 60, 100);
      var0.setFlammable(Blocks.ALLIUM, 60, 100);
      var0.setFlammable(Blocks.AZURE_BLUET, 60, 100);
      var0.setFlammable(Blocks.RED_TULIP, 60, 100);
      var0.setFlammable(Blocks.ORANGE_TULIP, 60, 100);
      var0.setFlammable(Blocks.WHITE_TULIP, 60, 100);
      var0.setFlammable(Blocks.PINK_TULIP, 60, 100);
      var0.setFlammable(Blocks.OXEYE_DAISY, 60, 100);
      var0.setFlammable(Blocks.CORNFLOWER, 60, 100);
      var0.setFlammable(Blocks.LILY_OF_THE_VALLEY, 60, 100);
      var0.setFlammable(Blocks.WITHER_ROSE, 60, 100);
      var0.setFlammable(Blocks.WHITE_WOOL, 30, 60);
      var0.setFlammable(Blocks.ORANGE_WOOL, 30, 60);
      var0.setFlammable(Blocks.MAGENTA_WOOL, 30, 60);
      var0.setFlammable(Blocks.LIGHT_BLUE_WOOL, 30, 60);
      var0.setFlammable(Blocks.YELLOW_WOOL, 30, 60);
      var0.setFlammable(Blocks.LIME_WOOL, 30, 60);
      var0.setFlammable(Blocks.PINK_WOOL, 30, 60);
      var0.setFlammable(Blocks.GRAY_WOOL, 30, 60);
      var0.setFlammable(Blocks.LIGHT_GRAY_WOOL, 30, 60);
      var0.setFlammable(Blocks.CYAN_WOOL, 30, 60);
      var0.setFlammable(Blocks.PURPLE_WOOL, 30, 60);
      var0.setFlammable(Blocks.BLUE_WOOL, 30, 60);
      var0.setFlammable(Blocks.BROWN_WOOL, 30, 60);
      var0.setFlammable(Blocks.GREEN_WOOL, 30, 60);
      var0.setFlammable(Blocks.RED_WOOL, 30, 60);
      var0.setFlammable(Blocks.BLACK_WOOL, 30, 60);
      var0.setFlammable(Blocks.VINE, 15, 100);
      var0.setFlammable(Blocks.COAL_BLOCK, 5, 5);
      var0.setFlammable(Blocks.HAY_BLOCK, 60, 20);
      var0.setFlammable(Blocks.WHITE_CARPET, 60, 20);
      var0.setFlammable(Blocks.ORANGE_CARPET, 60, 20);
      var0.setFlammable(Blocks.MAGENTA_CARPET, 60, 20);
      var0.setFlammable(Blocks.LIGHT_BLUE_CARPET, 60, 20);
      var0.setFlammable(Blocks.YELLOW_CARPET, 60, 20);
      var0.setFlammable(Blocks.LIME_CARPET, 60, 20);
      var0.setFlammable(Blocks.PINK_CARPET, 60, 20);
      var0.setFlammable(Blocks.GRAY_CARPET, 60, 20);
      var0.setFlammable(Blocks.LIGHT_GRAY_CARPET, 60, 20);
      var0.setFlammable(Blocks.CYAN_CARPET, 60, 20);
      var0.setFlammable(Blocks.PURPLE_CARPET, 60, 20);
      var0.setFlammable(Blocks.BLUE_CARPET, 60, 20);
      var0.setFlammable(Blocks.BROWN_CARPET, 60, 20);
      var0.setFlammable(Blocks.GREEN_CARPET, 60, 20);
      var0.setFlammable(Blocks.RED_CARPET, 60, 20);
      var0.setFlammable(Blocks.BLACK_CARPET, 60, 20);
      var0.setFlammable(Blocks.DRIED_KELP_BLOCK, 30, 60);
      var0.setFlammable(Blocks.BAMBOO, 60, 60);
      var0.setFlammable(Blocks.SCAFFOLDING, 60, 60);
      var0.setFlammable(Blocks.LECTERN, 30, 20);
      var0.setFlammable(Blocks.COMPOSTER, 5, 20);
      var0.setFlammable(Blocks.SWEET_BERRY_BUSH, 60, 100);
   }
}
