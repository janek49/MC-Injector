package net.minecraft.world.level.block;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;

public class PressurePlateBlock extends BasePressurePlateBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private final PressurePlateBlock.Sensitivity sensitivity;

   protected PressurePlateBlock(PressurePlateBlock.Sensitivity sensitivity, Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWERED, Boolean.valueOf(false)));
      this.sensitivity = sensitivity;
   }

   protected int getSignalForState(BlockState blockState) {
      return ((Boolean)blockState.getValue(POWERED)).booleanValue()?15:0;
   }

   protected BlockState setSignalForState(BlockState var1, int var2) {
      return (BlockState)var1.setValue(POWERED, Boolean.valueOf(var2 > 0));
   }

   protected void playOnSound(LevelAccessor levelAccessor, BlockPos blockPos) {
      if(this.material == Material.WOOD) {
         levelAccessor.playSound((Player)null, blockPos, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.8F);
      } else {
         levelAccessor.playSound((Player)null, blockPos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.6F);
      }

   }

   protected void playOffSound(LevelAccessor levelAccessor, BlockPos blockPos) {
      if(this.material == Material.WOOD) {
         levelAccessor.playSound((Player)null, blockPos, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.7F);
      } else {
         levelAccessor.playSound((Player)null, blockPos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.5F);
      }

   }

   protected int getSignalStrength(Level level, BlockPos blockPos) {
      AABB var3 = TOUCH_AABB.move(blockPos);
      List<? extends Entity> var4;
      switch(this.sensitivity) {
      case EVERYTHING:
         var4 = level.getEntities((Entity)null, var3);
         break;
      case MOBS:
         var4 = level.getEntitiesOfClass(LivingEntity.class, var3);
         break;
      default:
         return 0;
      }

      if(!var4.isEmpty()) {
         for(Entity var6 : var4) {
            if(!var6.isIgnoringBlockTriggers()) {
               return 15;
            }
         }
      }

      return 0;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{POWERED});
   }

   public static enum Sensitivity {
      EVERYTHING,
      MOBS;
   }
}
