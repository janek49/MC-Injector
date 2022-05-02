package net.minecraft.world.item;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;

public class BlockItem extends Item {
   @Deprecated
   private final Block block;

   public BlockItem(Block block, Item.Properties item$Properties) {
      super(item$Properties);
      this.block = block;
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      InteractionResult interactionResult = this.place(new BlockPlaceContext(useOnContext));
      return interactionResult != InteractionResult.SUCCESS && this.isEdible()?this.use(useOnContext.level, useOnContext.player, useOnContext.hand).getResult():interactionResult;
   }

   public InteractionResult place(BlockPlaceContext blockPlaceContext) {
      if(!blockPlaceContext.canPlace()) {
         return InteractionResult.FAIL;
      } else {
         BlockPlaceContext blockPlaceContext = this.updatePlacementContext(blockPlaceContext);
         if(blockPlaceContext == null) {
            return InteractionResult.FAIL;
         } else {
            BlockState var3 = this.getPlacementState(blockPlaceContext);
            if(var3 == null) {
               return InteractionResult.FAIL;
            } else if(!this.placeBlock(blockPlaceContext, var3)) {
               return InteractionResult.FAIL;
            } else {
               BlockPos var4 = blockPlaceContext.getClickedPos();
               Level var5 = blockPlaceContext.getLevel();
               Player var6 = blockPlaceContext.getPlayer();
               ItemStack var7 = blockPlaceContext.getItemInHand();
               BlockState var8 = var5.getBlockState(var4);
               Block var9 = var8.getBlock();
               if(var9 == var3.getBlock()) {
                  var8 = this.updateBlockStateFromTag(var4, var5, var7, var8);
                  this.updateCustomBlockEntityTag(var4, var5, var6, var7, var8);
                  var9.setPlacedBy(var5, var4, var8, var6, var7);
                  if(var6 instanceof ServerPlayer) {
                     CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)var6, var4, var7);
                  }
               }

               SoundType var10 = var8.getSoundType();
               var5.playSound(var6, var4, this.getPlaceSound(var8), SoundSource.BLOCKS, (var10.getVolume() + 1.0F) / 2.0F, var10.getPitch() * 0.8F);
               var7.shrink(1);
               return InteractionResult.SUCCESS;
            }
         }
      }
   }

   protected SoundEvent getPlaceSound(BlockState blockState) {
      return blockState.getSoundType().getPlaceSound();
   }

   @Nullable
   public BlockPlaceContext updatePlacementContext(BlockPlaceContext blockPlaceContext) {
      return blockPlaceContext;
   }

   protected boolean updateCustomBlockEntityTag(BlockPos blockPos, Level level, @Nullable Player player, ItemStack itemStack, BlockState blockState) {
      return updateCustomBlockEntityTag(level, player, blockPos, itemStack);
   }

   @Nullable
   protected BlockState getPlacementState(BlockPlaceContext blockPlaceContext) {
      BlockState blockState = this.getBlock().getStateForPlacement(blockPlaceContext);
      return blockState != null && this.canPlace(blockPlaceContext, blockState)?blockState:null;
   }

   private BlockState updateBlockStateFromTag(BlockPos blockPos, Level level, ItemStack itemStack, BlockState var4) {
      BlockState var5 = var4;
      CompoundTag var6 = itemStack.getTag();
      if(var6 != null) {
         CompoundTag var7 = var6.getCompound("BlockStateTag");
         StateDefinition<Block, BlockState> var8 = var4.getBlock().getStateDefinition();

         for(String var10 : var7.getAllKeys()) {
            Property<?> var11 = var8.getProperty(var10);
            if(var11 != null) {
               String var12 = var7.get(var10).getAsString();
               var5 = updateState(var5, var11, var12);
            }
         }
      }

      if(var5 != var4) {
         level.setBlock(blockPos, var5, 2);
      }

      return var5;
   }

   private static BlockState updateState(BlockState var0, Property property, String string) {
      return (BlockState)property.getValue(string).map((comparable) -> {
         return (BlockState)var0.setValue(property, comparable);
      }).orElse(var0);
   }

   protected boolean canPlace(BlockPlaceContext blockPlaceContext, BlockState blockState) {
      Player var3 = blockPlaceContext.getPlayer();
      CollisionContext var4 = var3 == null?CollisionContext.empty():CollisionContext.of(var3);
      return (!this.mustSurvive() || blockState.canSurvive(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos())) && blockPlaceContext.getLevel().isUnobstructed(blockState, blockPlaceContext.getClickedPos(), var4);
   }

   protected boolean mustSurvive() {
      return true;
   }

   protected boolean placeBlock(BlockPlaceContext blockPlaceContext, BlockState blockState) {
      return blockPlaceContext.getLevel().setBlock(blockPlaceContext.getClickedPos(), blockState, 11);
   }

   public static boolean updateCustomBlockEntityTag(Level level, @Nullable Player player, BlockPos blockPos, ItemStack itemStack) {
      MinecraftServer var4 = level.getServer();
      if(var4 == null) {
         return false;
      } else {
         CompoundTag var5 = itemStack.getTagElement("BlockEntityTag");
         if(var5 != null) {
            BlockEntity var6 = level.getBlockEntity(blockPos);
            if(var6 != null) {
               if(!level.isClientSide && var6.onlyOpCanSetNbt() && (player == null || !player.canUseGameMasterBlocks())) {
                  return false;
               }

               CompoundTag var7 = var6.save(new CompoundTag());
               CompoundTag var8 = var7.copy();
               var7.merge(var5);
               var7.putInt("x", blockPos.getX());
               var7.putInt("y", blockPos.getY());
               var7.putInt("z", blockPos.getZ());
               if(!var7.equals(var8)) {
                  var6.load(var7);
                  var6.setChanged();
                  return true;
               }
            }
         }

         return false;
      }
   }

   public String getDescriptionId() {
      return this.getBlock().getDescriptionId();
   }

   public void fillItemCategory(CreativeModeTab creativeModeTab, NonNullList nonNullList) {
      if(this.allowdedIn(creativeModeTab)) {
         this.getBlock().fillItemCategory(creativeModeTab, nonNullList);
      }

   }

   public void appendHoverText(ItemStack itemStack, @Nullable Level level, List list, TooltipFlag tooltipFlag) {
      super.appendHoverText(itemStack, level, list, tooltipFlag);
      this.getBlock().appendHoverText(itemStack, level, list, tooltipFlag);
   }

   public Block getBlock() {
      return this.block;
   }

   public void registerBlocks(Map map, Item item) {
      map.put(this.getBlock(), item);
   }
}
