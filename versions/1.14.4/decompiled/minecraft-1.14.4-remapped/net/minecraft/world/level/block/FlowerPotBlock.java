package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FlowerPotBlock extends Block {
   private static final Map POTTED_BY_CONTENT = Maps.newHashMap();
   protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);
   private final Block content;

   public FlowerPotBlock(Block content, Block.Properties block$Properties) {
      super(block$Properties);
      this.content = content;
      POTTED_BY_CONTENT.put(content, this);
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.MODEL;
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      ItemStack var7 = player.getItemInHand(interactionHand);
      Item var8 = var7.getItem();
      Block var9 = var8 instanceof BlockItem?(Block)POTTED_BY_CONTENT.getOrDefault(((BlockItem)var8).getBlock(), Blocks.AIR):Blocks.AIR;
      boolean var10 = var9 == Blocks.AIR;
      boolean var11 = this.content == Blocks.AIR;
      if(var10 != var11) {
         if(var11) {
            level.setBlock(blockPos, var9.defaultBlockState(), 3);
            player.awardStat(Stats.POT_FLOWER);
            if(!player.abilities.instabuild) {
               var7.shrink(1);
            }
         } else {
            ItemStack var12 = new ItemStack(this.content);
            if(var7.isEmpty()) {
               player.setItemInHand(interactionHand, var12);
            } else if(!player.addItem(var12)) {
               player.drop(var12, false);
            }

            level.setBlock(blockPos, Blocks.FLOWER_POT.defaultBlockState(), 3);
         }
      }

      return true;
   }

   public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
      return this.content == Blocks.AIR?super.getCloneItemStack(blockGetter, blockPos, blockState):new ItemStack(this.content);
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return direction == Direction.DOWN && !var1.canSurvive(levelAccessor, var5)?Blocks.AIR.defaultBlockState():super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.CUTOUT;
   }

   public Block getContent() {
      return this.content;
   }
}
