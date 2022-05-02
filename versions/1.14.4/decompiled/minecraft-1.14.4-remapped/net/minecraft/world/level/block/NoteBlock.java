package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class NoteBlock extends Block {
   public static final EnumProperty INSTRUMENT = BlockStateProperties.NOTEBLOCK_INSTRUMENT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final IntegerProperty NOTE = BlockStateProperties.NOTE;

   public NoteBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(INSTRUMENT, NoteBlockInstrument.HARP)).setValue(NOTE, Integer.valueOf(0))).setValue(POWERED, Boolean.valueOf(false)));
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return (BlockState)this.defaultBlockState().setValue(INSTRUMENT, NoteBlockInstrument.byState(blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos().below())));
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return direction == Direction.DOWN?(BlockState)var1.setValue(INSTRUMENT, NoteBlockInstrument.byState(var3)):super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      boolean var7 = level.hasNeighborSignal(var3);
      if(var7 != ((Boolean)blockState.getValue(POWERED)).booleanValue()) {
         if(var7) {
            this.playNote(level, var3);
         }

         level.setBlock(var3, (BlockState)blockState.setValue(POWERED, Boolean.valueOf(var7)), 3);
      }

   }

   private void playNote(Level level, BlockPos blockPos) {
      if(level.getBlockState(blockPos.above()).isAir()) {
         level.blockEvent(blockPos, this, 0, 0);
      }

   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(level.isClientSide) {
         return true;
      } else {
         blockState = (BlockState)blockState.cycle(NOTE);
         level.setBlock(blockPos, blockState, 3);
         this.playNote(level, blockPos);
         player.awardStat(Stats.TUNE_NOTEBLOCK);
         return true;
      }
   }

   public void attack(BlockState blockState, Level level, BlockPos blockPos, Player player) {
      if(!level.isClientSide) {
         this.playNote(level, blockPos);
         player.awardStat(Stats.PLAY_NOTEBLOCK);
      }
   }

   public boolean triggerEvent(BlockState blockState, Level level, BlockPos blockPos, int var4, int var5) {
      int var6 = ((Integer)blockState.getValue(NOTE)).intValue();
      float var7 = (float)Math.pow(2.0D, (double)(var6 - 12) / 12.0D);
      level.playSound((Player)null, (BlockPos)blockPos, ((NoteBlockInstrument)blockState.getValue(INSTRUMENT)).getSoundEvent(), SoundSource.RECORDS, 3.0F, var7);
      level.addParticle(ParticleTypes.NOTE, (double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 1.2D, (double)blockPos.getZ() + 0.5D, (double)var6 / 24.0D, 0.0D, 0.0D);
      return true;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{INSTRUMENT, POWERED, NOTE});
   }
}
