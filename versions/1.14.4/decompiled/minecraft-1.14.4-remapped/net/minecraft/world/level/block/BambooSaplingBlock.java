package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BambooBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BambooSaplingBlock extends Block implements BonemealableBlock {
   protected static final VoxelShape SAPLING_SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D);

   public BambooSaplingBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public Block.OffsetType getOffsetType() {
      return Block.OffsetType.XZ;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      Vec3 var5 = blockState.getOffset(blockGetter, blockPos);
      return SAPLING_SHAPE.move(var5.x, var5.y, var5.z);
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(random.nextInt(3) == 0 && level.isEmptyBlock(blockPos.above()) && level.getRawBrightness(blockPos.above(), 0) >= 9) {
         this.growBamboo(level, blockPos);
      }

   }

   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      return levelReader.getBlockState(blockPos.below()).is(BlockTags.BAMBOO_PLANTABLE_ON);
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      if(!var1.canSurvive(levelAccessor, var5)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if(direction == Direction.UP && var3.getBlock() == Blocks.BAMBOO) {
            levelAccessor.setBlock(var5, Blocks.BAMBOO.defaultBlockState(), 2);
         }

         return super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
      }
   }

   public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
      return new ItemStack(Items.BAMBOO);
   }

   public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, boolean var4) {
      return blockGetter.getBlockState(blockPos.above()).isAir();
   }

   public boolean isBonemealSuccess(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      return true;
   }

   public void performBonemeal(Level level, Random random, BlockPos blockPos, BlockState blockState) {
      this.growBamboo(level, blockPos);
   }

   public float getDestroyProgress(BlockState blockState, Player player, BlockGetter blockGetter, BlockPos blockPos) {
      return player.getMainHandItem().getItem() instanceof SwordItem?1.0F:super.getDestroyProgress(blockState, player, blockGetter, blockPos);
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   protected void growBamboo(Level level, BlockPos blockPos) {
      level.setBlock(blockPos.above(), (BlockState)Blocks.BAMBOO.defaultBlockState().setValue(BambooBlock.LEAVES, BambooLeaves.SMALL), 3);
   }
}
