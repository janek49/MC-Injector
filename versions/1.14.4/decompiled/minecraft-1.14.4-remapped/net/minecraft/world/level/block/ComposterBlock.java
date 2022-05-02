package net.minecraft.world.level.block;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.Random;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ComposterBlock extends Block implements WorldlyContainerHolder {
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_COMPOSTER;
   public static final Object2FloatMap COMPOSTABLES = new Object2FloatOpenHashMap();
   public static final VoxelShape OUTER_SHAPE = Shapes.block();
   private static final VoxelShape[] SHAPES = (VoxelShape[])Util.make(new VoxelShape[9], (voxelShapes) -> {
      for(int var1 = 0; var1 < 8; ++var1) {
         voxelShapes[var1] = Shapes.join(OUTER_SHAPE, Block.box(2.0D, (double)Math.max(2, 1 + var1 * 2), 2.0D, 14.0D, 16.0D, 14.0D), BooleanOp.ONLY_FIRST);
      }

      voxelShapes[8] = voxelShapes[7];
   });

   public static void bootStrap() {
      COMPOSTABLES.defaultReturnValue(-1.0F);
      float var0 = 0.3F;
      float var1 = 0.5F;
      float var2 = 0.65F;
      float var3 = 0.85F;
      float var4 = 1.0F;
      add(0.3F, Items.JUNGLE_LEAVES);
      add(0.3F, Items.OAK_LEAVES);
      add(0.3F, Items.SPRUCE_LEAVES);
      add(0.3F, Items.DARK_OAK_LEAVES);
      add(0.3F, Items.ACACIA_LEAVES);
      add(0.3F, Items.BIRCH_LEAVES);
      add(0.3F, Items.OAK_SAPLING);
      add(0.3F, Items.SPRUCE_SAPLING);
      add(0.3F, Items.BIRCH_SAPLING);
      add(0.3F, Items.JUNGLE_SAPLING);
      add(0.3F, Items.ACACIA_SAPLING);
      add(0.3F, Items.DARK_OAK_SAPLING);
      add(0.3F, Items.BEETROOT_SEEDS);
      add(0.3F, Items.DRIED_KELP);
      add(0.3F, Items.GRASS);
      add(0.3F, Items.KELP);
      add(0.3F, Items.MELON_SEEDS);
      add(0.3F, Items.PUMPKIN_SEEDS);
      add(0.3F, Items.SEAGRASS);
      add(0.3F, Items.SWEET_BERRIES);
      add(0.3F, Items.WHEAT_SEEDS);
      add(0.5F, Items.DRIED_KELP_BLOCK);
      add(0.5F, Items.TALL_GRASS);
      add(0.5F, Items.CACTUS);
      add(0.5F, Items.SUGAR_CANE);
      add(0.5F, Items.VINE);
      add(0.5F, Items.MELON_SLICE);
      add(0.65F, Items.SEA_PICKLE);
      add(0.65F, Items.LILY_PAD);
      add(0.65F, Items.PUMPKIN);
      add(0.65F, Items.CARVED_PUMPKIN);
      add(0.65F, Items.MELON);
      add(0.65F, Items.APPLE);
      add(0.65F, Items.BEETROOT);
      add(0.65F, Items.CARROT);
      add(0.65F, Items.COCOA_BEANS);
      add(0.65F, Items.POTATO);
      add(0.65F, Items.WHEAT);
      add(0.65F, Items.BROWN_MUSHROOM);
      add(0.65F, Items.RED_MUSHROOM);
      add(0.65F, Items.MUSHROOM_STEM);
      add(0.65F, Items.DANDELION);
      add(0.65F, Items.POPPY);
      add(0.65F, Items.BLUE_ORCHID);
      add(0.65F, Items.ALLIUM);
      add(0.65F, Items.AZURE_BLUET);
      add(0.65F, Items.RED_TULIP);
      add(0.65F, Items.ORANGE_TULIP);
      add(0.65F, Items.WHITE_TULIP);
      add(0.65F, Items.PINK_TULIP);
      add(0.65F, Items.OXEYE_DAISY);
      add(0.65F, Items.CORNFLOWER);
      add(0.65F, Items.LILY_OF_THE_VALLEY);
      add(0.65F, Items.WITHER_ROSE);
      add(0.65F, Items.FERN);
      add(0.65F, Items.SUNFLOWER);
      add(0.65F, Items.LILAC);
      add(0.65F, Items.ROSE_BUSH);
      add(0.65F, Items.PEONY);
      add(0.65F, Items.LARGE_FERN);
      add(0.85F, Items.HAY_BLOCK);
      add(0.85F, Items.BROWN_MUSHROOM_BLOCK);
      add(0.85F, Items.RED_MUSHROOM_BLOCK);
      add(0.85F, Items.BREAD);
      add(0.85F, Items.BAKED_POTATO);
      add(0.85F, Items.COOKIE);
      add(1.0F, Items.CAKE);
      add(1.0F, Items.PUMPKIN_PIE);
   }

   private static void add(float var0, ItemLike itemLike) {
      COMPOSTABLES.put(itemLike.asItem(), var0);
   }

   public ComposterBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LEVEL, Integer.valueOf(0)));
   }

   public static void handleFill(Level level, BlockPos blockPos, boolean var2) {
      BlockState var3 = level.getBlockState(blockPos);
      level.playLocalSound((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), var2?SoundEvents.COMPOSTER_FILL_SUCCESS:SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
      double var4 = var3.getShape(level, blockPos).max(Direction.Axis.Y, 0.5D, 0.5D) + 0.03125D;
      double var6 = 0.13124999403953552D;
      double var8 = 0.737500011920929D;
      Random var10 = level.getRandom();

      for(int var11 = 0; var11 < 10; ++var11) {
         double var12 = var10.nextGaussian() * 0.02D;
         double var14 = var10.nextGaussian() * 0.02D;
         double var16 = var10.nextGaussian() * 0.02D;
         level.addParticle(ParticleTypes.COMPOSTER, (double)blockPos.getX() + 0.13124999403953552D + 0.737500011920929D * (double)var10.nextFloat(), (double)blockPos.getY() + var4 + (double)var10.nextFloat() * (1.0D - var4), (double)blockPos.getZ() + 0.13124999403953552D + 0.737500011920929D * (double)var10.nextFloat(), var12, var14, var16);
      }

   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPES[((Integer)blockState.getValue(LEVEL)).intValue()];
   }

   public VoxelShape getInteractionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return OUTER_SHAPE;
   }

   public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPES[0];
   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(((Integer)var1.getValue(LEVEL)).intValue() == 7) {
         level.getBlockTicks().scheduleTick(blockPos, var1.getBlock(), 20);
      }

   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      int var7 = ((Integer)blockState.getValue(LEVEL)).intValue();
      ItemStack var8 = player.getItemInHand(interactionHand);
      if(var7 < 8 && COMPOSTABLES.containsKey(var8.getItem())) {
         if(var7 < 7 && !level.isClientSide) {
            boolean var9 = addItem(blockState, level, blockPos, var8);
            level.levelEvent(1500, blockPos, var9?1:0);
            if(!player.abilities.instabuild) {
               var8.shrink(1);
            }
         }

         return true;
      } else if(var7 == 8) {
         if(!level.isClientSide) {
            float var9 = 0.7F;
            double var10 = (double)(level.random.nextFloat() * 0.7F) + 0.15000000596046448D;
            double var12 = (double)(level.random.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
            double var14 = (double)(level.random.nextFloat() * 0.7F) + 0.15000000596046448D;
            ItemEntity var16 = new ItemEntity(level, (double)blockPos.getX() + var10, (double)blockPos.getY() + var12, (double)blockPos.getZ() + var14, new ItemStack(Items.BONE_MEAL));
            var16.setDefaultPickUpDelay();
            level.addFreshEntity(var16);
         }

         empty(blockState, level, blockPos);
         level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
         return true;
      } else {
         return false;
      }
   }

   private static void empty(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos) {
      levelAccessor.setBlock(blockPos, (BlockState)blockState.setValue(LEVEL, Integer.valueOf(0)), 3);
   }

   private static boolean addItem(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, ItemStack itemStack) {
      int var4 = ((Integer)blockState.getValue(LEVEL)).intValue();
      float var5 = COMPOSTABLES.getFloat(itemStack.getItem());
      if((var4 != 0 || var5 <= 0.0F) && levelAccessor.getRandom().nextDouble() >= (double)var5) {
         return false;
      } else {
         int var6 = var4 + 1;
         levelAccessor.setBlock(blockPos, (BlockState)blockState.setValue(LEVEL, Integer.valueOf(var6)), 3);
         if(var6 == 7) {
            levelAccessor.getBlockTicks().scheduleTick(blockPos, blockState.getBlock(), 20);
         }

         return true;
      }
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(((Integer)blockState.getValue(LEVEL)).intValue() == 7) {
         level.setBlock(blockPos, (BlockState)blockState.cycle(LEVEL), 3);
         level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

      super.tick(blockState, level, blockPos, random);
   }

   public boolean hasAnalogOutputSignal(BlockState blockState) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
      return ((Integer)blockState.getValue(LEVEL)).intValue();
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{LEVEL});
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }

   public WorldlyContainer getContainer(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos) {
      int var4 = ((Integer)blockState.getValue(LEVEL)).intValue();
      return (WorldlyContainer)(var4 == 8?new ComposterBlock.OutputContainer(blockState, levelAccessor, blockPos, new ItemStack(Items.BONE_MEAL)):(var4 < 7?new ComposterBlock.InputContainer(blockState, levelAccessor, blockPos):new ComposterBlock.EmptyContainer()));
   }

   static class EmptyContainer extends SimpleContainer implements WorldlyContainer {
      public EmptyContainer() {
         super(0);
      }

      public int[] getSlotsForFace(Direction direction) {
         return new int[0];
      }

      public boolean canPlaceItemThroughFace(int var1, ItemStack itemStack, @Nullable Direction direction) {
         return false;
      }

      public boolean canTakeItemThroughFace(int var1, ItemStack itemStack, Direction direction) {
         return false;
      }
   }

   static class InputContainer extends SimpleContainer implements WorldlyContainer {
      private final BlockState state;
      private final LevelAccessor level;
      private final BlockPos pos;
      private boolean changed;

      public InputContainer(BlockState state, LevelAccessor level, BlockPos pos) {
         super(1);
         this.state = state;
         this.level = level;
         this.pos = pos;
      }

      public int getMaxStackSize() {
         return 1;
      }

      public int[] getSlotsForFace(Direction direction) {
         return direction == Direction.UP?new int[]{0}:new int[0];
      }

      public boolean canPlaceItemThroughFace(int var1, ItemStack itemStack, @Nullable Direction direction) {
         return !this.changed && direction == Direction.UP && ComposterBlock.COMPOSTABLES.containsKey(itemStack.getItem());
      }

      public boolean canTakeItemThroughFace(int var1, ItemStack itemStack, Direction direction) {
         return false;
      }

      public void setChanged() {
         ItemStack var1 = this.getItem(0);
         if(!var1.isEmpty()) {
            this.changed = true;
            ComposterBlock.addItem(this.state, this.level, this.pos, var1);
            this.removeItemNoUpdate(0);
         }

      }
   }

   static class OutputContainer extends SimpleContainer implements WorldlyContainer {
      private final BlockState state;
      private final LevelAccessor level;
      private final BlockPos pos;
      private boolean changed;

      public OutputContainer(BlockState state, LevelAccessor level, BlockPos pos, ItemStack itemStack) {
         super(new ItemStack[]{itemStack});
         this.state = state;
         this.level = level;
         this.pos = pos;
      }

      public int getMaxStackSize() {
         return 1;
      }

      public int[] getSlotsForFace(Direction direction) {
         return direction == Direction.DOWN?new int[]{0}:new int[0];
      }

      public boolean canPlaceItemThroughFace(int var1, ItemStack itemStack, @Nullable Direction direction) {
         return false;
      }

      public boolean canTakeItemThroughFace(int var1, ItemStack itemStack, Direction direction) {
         return !this.changed && direction == Direction.DOWN && itemStack.getItem() == Items.BONE_MEAL;
      }

      public void setChanged() {
         ComposterBlock.empty(this.state, this.level, this.pos);
         this.changed = true;
      }
   }
}
