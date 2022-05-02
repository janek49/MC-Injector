package net.minecraft.world.item;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class StandingAndWallBlockItem extends BlockItem {
   protected final Block wallBlock;

   public StandingAndWallBlockItem(Block var1, Block wallBlock, Item.Properties item$Properties) {
      super(var1, item$Properties);
      this.wallBlock = wallBlock;
   }

   @Nullable
   protected BlockState getPlacementState(BlockPlaceContext blockPlaceContext) {
      BlockState blockState = this.wallBlock.getStateForPlacement(blockPlaceContext);
      BlockState var3 = null;
      LevelReader var4 = blockPlaceContext.getLevel();
      BlockPos var5 = blockPlaceContext.getClickedPos();

      for(Direction var9 : blockPlaceContext.getNearestLookingDirections()) {
         if(var9 != Direction.UP) {
            BlockState var10 = var9 == Direction.DOWN?this.getBlock().getStateForPlacement(blockPlaceContext):blockState;
            if(var10 != null && var10.canSurvive(var4, var5)) {
               var3 = var10;
               break;
            }
         }
      }

      return var3 != null && var4.isUnobstructed(var3, var5, CollisionContext.empty())?var3:null;
   }

   public void registerBlocks(Map map, Item item) {
      super.registerBlocks(map, item);
      map.put(this.wallBlock, item);
   }
}
