package net.minecraft.world.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ShovelItem extends DiggerItem {
   private static final Set DIGGABLES = Sets.newHashSet(new Block[]{Blocks.CLAY, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.FARMLAND, Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.RED_SAND, Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER});
   protected static final Map FLATTENABLES = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.GRASS_PATH.defaultBlockState()));

   public ShovelItem(Tier tier, float var2, float var3, Item.Properties item$Properties) {
      super(var2, var3, tier, DIGGABLES, item$Properties);
   }

   public boolean canDestroySpecial(BlockState blockState) {
      Block var2 = blockState.getBlock();
      return var2 == Blocks.SNOW || var2 == Blocks.SNOW_BLOCK;
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      Level var2 = useOnContext.getLevel();
      BlockPos var3 = useOnContext.getClickedPos();
      if(useOnContext.getClickedFace() != Direction.DOWN && var2.getBlockState(var3.above()).isAir()) {
         BlockState var4 = (BlockState)FLATTENABLES.get(var2.getBlockState(var3).getBlock());
         if(var4 != null) {
            Player var5 = useOnContext.getPlayer();
            var2.playSound(var5, var3, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            if(!var2.isClientSide) {
               var2.setBlock(var3, var4, 11);
               if(var5 != null) {
                  useOnContext.getItemInHand().hurtAndBreak(1, var5, (player) -> {
                     player.broadcastBreakEvent(useOnContext.getHand());
                  });
               }
            }

            return InteractionResult.SUCCESS;
         }
      }

      return InteractionResult.PASS;
   }
}
