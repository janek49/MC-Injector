package net.minecraft.client.color.block;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Registry;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.ShearableDoublePlantBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MaterialColor;

@ClientJarOnly
public class BlockColors {
   private final IdMapper blockColors = new IdMapper(32);
   private final Map coloringStates = Maps.newHashMap();

   public static BlockColors createDefault() {
      BlockColors blockColors = new BlockColors();
      blockColors.register((blockState, blockAndBiomeGetter, blockPos, var3) -> {
         return blockAndBiomeGetter != null && blockPos != null?BiomeColors.getAverageGrassColor(blockAndBiomeGetter, blockState.getValue(ShearableDoublePlantBlock.HALF) == DoubleBlockHalf.UPPER?blockPos.below():blockPos):-1;
      }, new Block[]{Blocks.LARGE_FERN, Blocks.TALL_GRASS});
      blockColors.addColoringState(ShearableDoublePlantBlock.HALF, new Block[]{Blocks.LARGE_FERN, Blocks.TALL_GRASS});
      blockColors.register((blockState, blockAndBiomeGetter, blockPos, var3) -> {
         return blockAndBiomeGetter != null && blockPos != null?BiomeColors.getAverageGrassColor(blockAndBiomeGetter, blockPos):GrassColor.get(0.5D, 1.0D);
      }, new Block[]{Blocks.GRASS_BLOCK, Blocks.FERN, Blocks.GRASS, Blocks.POTTED_FERN});
      blockColors.register((blockState, blockAndBiomeGetter, blockPos, var3) -> {
         return FoliageColor.getEvergreenColor();
      }, new Block[]{Blocks.SPRUCE_LEAVES});
      blockColors.register((blockState, blockAndBiomeGetter, blockPos, var3) -> {
         return FoliageColor.getBirchColor();
      }, new Block[]{Blocks.BIRCH_LEAVES});
      blockColors.register((blockState, blockAndBiomeGetter, blockPos, var3) -> {
         return blockAndBiomeGetter != null && blockPos != null?BiomeColors.getAverageFoliageColor(blockAndBiomeGetter, blockPos):FoliageColor.getDefaultColor();
      }, new Block[]{Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.VINE});
      blockColors.register((blockState, blockAndBiomeGetter, blockPos, var3) -> {
         return blockAndBiomeGetter != null && blockPos != null?BiomeColors.getAverageWaterColor(blockAndBiomeGetter, blockPos):-1;
      }, new Block[]{Blocks.WATER, Blocks.BUBBLE_COLUMN, Blocks.CAULDRON});
      blockColors.register((blockState, blockAndBiomeGetter, blockPos, var3) -> {
         return RedStoneWireBlock.getColorForData(((Integer)blockState.getValue(RedStoneWireBlock.POWER)).intValue());
      }, new Block[]{Blocks.REDSTONE_WIRE});
      blockColors.addColoringState(RedStoneWireBlock.POWER, new Block[]{Blocks.REDSTONE_WIRE});
      blockColors.register((blockState, blockAndBiomeGetter, blockPos, var3) -> {
         return blockAndBiomeGetter != null && blockPos != null?BiomeColors.getAverageGrassColor(blockAndBiomeGetter, blockPos):-1;
      }, new Block[]{Blocks.SUGAR_CANE});
      blockColors.register((blockState, blockAndBiomeGetter, blockPos, var3) -> {
         return 14731036;
      }, new Block[]{Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM});
      blockColors.register((blockState, blockAndBiomeGetter, blockPos, var3) -> {
         int var4 = ((Integer)blockState.getValue(StemBlock.AGE)).intValue();
         int var5 = var4 * 32;
         int var6 = 255 - var4 * 8;
         int var7 = var4 * 4;
         return var5 << 16 | var6 << 8 | var7;
      }, new Block[]{Blocks.MELON_STEM, Blocks.PUMPKIN_STEM});
      blockColors.addColoringState(StemBlock.AGE, new Block[]{Blocks.MELON_STEM, Blocks.PUMPKIN_STEM});
      blockColors.register((blockState, blockAndBiomeGetter, blockPos, var3) -> {
         return blockAndBiomeGetter != null && blockPos != null?2129968:7455580;
      }, new Block[]{Blocks.LILY_PAD});
      return blockColors;
   }

   public int getColor(BlockState blockState, Level level, BlockPos blockPos) {
      BlockColor var4 = (BlockColor)this.blockColors.byId(Registry.BLOCK.getId(blockState.getBlock()));
      if(var4 != null) {
         return var4.getColor(blockState, (BlockAndBiomeGetter)null, (BlockPos)null, 0);
      } else {
         MaterialColor var5 = blockState.getMapColor(level, blockPos);
         return var5 != null?var5.col:-1;
      }
   }

   public int getColor(BlockState blockState, @Nullable BlockAndBiomeGetter blockAndBiomeGetter, @Nullable BlockPos blockPos, int var4) {
      BlockColor var5 = (BlockColor)this.blockColors.byId(Registry.BLOCK.getId(blockState.getBlock()));
      return var5 == null?-1:var5.getColor(blockState, blockAndBiomeGetter, blockPos, var4);
   }

   public void register(BlockColor blockColor, Block... blocks) {
      for(Block var6 : blocks) {
         this.blockColors.addMapping(blockColor, Registry.BLOCK.getId(var6));
      }

   }

   private void addColoringStates(Set set, Block... blocks) {
      for(Block var6 : blocks) {
         this.coloringStates.put(var6, set);
      }

   }

   private void addColoringState(Property property, Block... blocks) {
      this.addColoringStates(ImmutableSet.of(property), blocks);
   }

   public Set getColoringProperties(Block block) {
      return (Set)this.coloringStates.getOrDefault(block, ImmutableSet.of());
   }
}
