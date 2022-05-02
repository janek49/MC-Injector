package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class WeightedPressurePlateBlock extends BasePressurePlateBlock {
   public static final IntegerProperty POWER = BlockStateProperties.POWER;
   private final int maxWeight;

   protected WeightedPressurePlateBlock(int maxWeight, Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWER, Integer.valueOf(0)));
      this.maxWeight = maxWeight;
   }

   protected int getSignalStrength(Level level, BlockPos blockPos) {
      int var3 = Math.min(level.getEntitiesOfClass(Entity.class, TOUCH_AABB.move(blockPos)).size(), this.maxWeight);
      if(var3 > 0) {
         float var4 = (float)Math.min(this.maxWeight, var3) / (float)this.maxWeight;
         return Mth.ceil(var4 * 15.0F);
      } else {
         return 0;
      }
   }

   protected void playOnSound(LevelAccessor levelAccessor, BlockPos blockPos) {
      levelAccessor.playSound((Player)null, blockPos, SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.90000004F);
   }

   protected void playOffSound(LevelAccessor levelAccessor, BlockPos blockPos) {
      levelAccessor.playSound((Player)null, blockPos, SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.75F);
   }

   protected int getSignalForState(BlockState blockState) {
      return ((Integer)blockState.getValue(POWER)).intValue();
   }

   protected BlockState setSignalForState(BlockState var1, int var2) {
      return (BlockState)var1.setValue(POWER, Integer.valueOf(var2));
   }

   public int getTickDelay(LevelReader levelReader) {
      return 10;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{POWER});
   }
}
