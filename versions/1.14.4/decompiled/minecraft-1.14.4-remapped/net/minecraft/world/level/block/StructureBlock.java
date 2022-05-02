package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.phys.BlockHitResult;

public class StructureBlock extends BaseEntityBlock {
   public static final EnumProperty MODE = BlockStateProperties.STRUCTUREBLOCK_MODE;

   protected StructureBlock(Block.Properties block$Properties) {
      super(block$Properties);
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new StructureBlockEntity();
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      BlockEntity var7 = level.getBlockEntity(blockPos);
      return var7 instanceof StructureBlockEntity?((StructureBlockEntity)var7).usedBy(player):false;
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
      if(!level.isClientSide) {
         if(livingEntity != null) {
            BlockEntity var6 = level.getBlockEntity(blockPos);
            if(var6 instanceof StructureBlockEntity) {
               ((StructureBlockEntity)var6).createdBy(livingEntity);
            }
         }

      }
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.MODEL;
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return (BlockState)this.defaultBlockState().setValue(MODE, StructureMode.DATA);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{MODE});
   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      if(!level.isClientSide) {
         BlockEntity var7 = level.getBlockEntity(var3);
         if(var7 instanceof StructureBlockEntity) {
            StructureBlockEntity var8 = (StructureBlockEntity)var7;
            boolean var9 = level.hasNeighborSignal(var3);
            boolean var10 = var8.isPowered();
            if(var9 && !var10) {
               var8.setPowered(true);
               this.trigger(var8);
            } else if(!var9 && var10) {
               var8.setPowered(false);
            }

         }
      }
   }

   private void trigger(StructureBlockEntity structureBlockEntity) {
      switch(structureBlockEntity.getMode()) {
      case SAVE:
         structureBlockEntity.saveStructure(false);
         break;
      case LOAD:
         structureBlockEntity.loadStructure(false);
         break;
      case CORNER:
         structureBlockEntity.unloadStructure();
      case DATA:
      }

   }
}
