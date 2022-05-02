package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandBlock extends BaseEntityBlock {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final DirectionProperty FACING = DirectionalBlock.FACING;
   public static final BooleanProperty CONDITIONAL = BlockStateProperties.CONDITIONAL;

   public CommandBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(CONDITIONAL, Boolean.valueOf(false)));
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      CommandBlockEntity var2 = new CommandBlockEntity();
      var2.setAutomatic(this == Blocks.CHAIN_COMMAND_BLOCK);
      return var2;
   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      if(!level.isClientSide) {
         BlockEntity var7 = level.getBlockEntity(var3);
         if(var7 instanceof CommandBlockEntity) {
            CommandBlockEntity var8 = (CommandBlockEntity)var7;
            boolean var9 = level.hasNeighborSignal(var3);
            boolean var10 = var8.isPowered();
            var8.setPowered(var9);
            if(!var10 && !var8.isAutomatic() && var8.getMode() != CommandBlockEntity.Mode.SEQUENCE) {
               if(var9) {
                  var8.markConditionMet();
                  level.getBlockTicks().scheduleTick(var3, this, this.getTickDelay(level));
               }

            }
         }
      }
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(!level.isClientSide) {
         BlockEntity var5 = level.getBlockEntity(blockPos);
         if(var5 instanceof CommandBlockEntity) {
            CommandBlockEntity var6 = (CommandBlockEntity)var5;
            BaseCommandBlock var7 = var6.getCommandBlock();
            boolean var8 = !StringUtil.isNullOrEmpty(var7.getCommand());
            CommandBlockEntity.Mode var9 = var6.getMode();
            boolean var10 = var6.wasConditionMet();
            if(var9 == CommandBlockEntity.Mode.AUTO) {
               var6.markConditionMet();
               if(var10) {
                  this.execute(blockState, level, blockPos, var7, var8);
               } else if(var6.isConditional()) {
                  var7.setSuccessCount(0);
               }

               if(var6.isPowered() || var6.isAutomatic()) {
                  level.getBlockTicks().scheduleTick(blockPos, this, this.getTickDelay(level));
               }
            } else if(var9 == CommandBlockEntity.Mode.REDSTONE) {
               if(var10) {
                  this.execute(blockState, level, blockPos, var7, var8);
               } else if(var6.isConditional()) {
                  var7.setSuccessCount(0);
               }
            }

            level.updateNeighbourForOutputSignal(blockPos, this);
         }

      }
   }

   private void execute(BlockState blockState, Level level, BlockPos blockPos, BaseCommandBlock baseCommandBlock, boolean var5) {
      if(var5) {
         baseCommandBlock.performCommand(level);
      } else {
         baseCommandBlock.setSuccessCount(0);
      }

      executeChain(level, blockPos, (Direction)blockState.getValue(FACING));
   }

   public int getTickDelay(LevelReader levelReader) {
      return 1;
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      BlockEntity var7 = level.getBlockEntity(blockPos);
      if(var7 instanceof CommandBlockEntity && player.canUseGameMasterBlocks()) {
         player.openCommandBlock((CommandBlockEntity)var7);
         return true;
      } else {
         return false;
      }
   }

   public boolean hasAnalogOutputSignal(BlockState blockState) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
      BlockEntity var4 = level.getBlockEntity(blockPos);
      return var4 instanceof CommandBlockEntity?((CommandBlockEntity)var4).getCommandBlock().getSuccessCount():0;
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
      BlockEntity var6 = level.getBlockEntity(blockPos);
      if(var6 instanceof CommandBlockEntity) {
         CommandBlockEntity var7 = (CommandBlockEntity)var6;
         BaseCommandBlock var8 = var7.getCommandBlock();
         if(itemStack.hasCustomHoverName()) {
            var8.setName(itemStack.getHoverName());
         }

         if(!level.isClientSide) {
            if(itemStack.getTagElement("BlockEntityTag") == null) {
               var8.setTrackOutput(level.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK));
               var7.setAutomatic(this == Blocks.CHAIN_COMMAND_BLOCK);
            }

            if(var7.getMode() == CommandBlockEntity.Mode.SEQUENCE) {
               boolean var9 = level.hasNeighborSignal(blockPos);
               var7.setPowered(var9);
            }
         }

      }
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.MODEL;
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(FACING, rotation.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return var1.rotate(mirror.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING, CONDITIONAL});
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return (BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getNearestLookingDirection().getOpposite());
   }

   private static void executeChain(Level level, BlockPos blockPos, Direction direction) {
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos(blockPos);
      GameRules var4 = level.getGameRules();

      int var5;
      BlockState var6;
      for(var5 = var4.getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH); var5-- > 0; direction = (Direction)var6.getValue(FACING)) {
         var3.move(direction);
         var6 = level.getBlockState(var3);
         Block var7 = var6.getBlock();
         if(var7 != Blocks.CHAIN_COMMAND_BLOCK) {
            break;
         }

         BlockEntity var8 = level.getBlockEntity(var3);
         if(!(var8 instanceof CommandBlockEntity)) {
            break;
         }

         CommandBlockEntity var9 = (CommandBlockEntity)var8;
         if(var9.getMode() != CommandBlockEntity.Mode.SEQUENCE) {
            break;
         }

         if(var9.isPowered() || var9.isAutomatic()) {
            BaseCommandBlock var10 = var9.getCommandBlock();
            if(var9.markConditionMet()) {
               if(!var10.performCommand(level)) {
                  break;
               }

               level.updateNeighbourForOutputSignal(var3, var7);
            } else if(var9.isConditional()) {
               var10.setSuccessCount(0);
            }
         }
      }

      if(var5 <= 0) {
         int var6 = Math.max(var4.getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH), 0);
         LOGGER.warn("Command Block chain tried to execute more than {} steps!", Integer.valueOf(var6));
      }

   }
}
