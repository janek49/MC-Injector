package net.minecraft.world.level.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShulkerBoxBlock extends BaseEntityBlock {
   public static final EnumProperty FACING = DirectionalBlock.FACING;
   public static final ResourceLocation CONTENTS = new ResourceLocation("contents");
   @Nullable
   private final DyeColor color;

   public ShulkerBoxBlock(@Nullable DyeColor color, Block.Properties block$Properties) {
      super(block$Properties);
      this.color = color;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP));
   }

   public BlockEntity newBlockEntity(BlockGetter blockGetter) {
      return new ShulkerBoxBlockEntity(this.color);
   }

   public boolean isViewBlocking(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return true;
   }

   public boolean hasCustomBreakingProgress(BlockState blockState) {
      return true;
   }

   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      if(level.isClientSide) {
         return true;
      } else if(player.isSpectator()) {
         return true;
      } else {
         BlockEntity var7 = level.getBlockEntity(blockPos);
         if(var7 instanceof ShulkerBoxBlockEntity) {
            Direction var8 = (Direction)blockState.getValue(FACING);
            ShulkerBoxBlockEntity var10 = (ShulkerBoxBlockEntity)var7;
            boolean var9;
            if(var10.getAnimationStatus() == ShulkerBoxBlockEntity.AnimationStatus.CLOSED) {
               AABB var11 = Shapes.block().bounds().expandTowards((double)(0.5F * (float)var8.getStepX()), (double)(0.5F * (float)var8.getStepY()), (double)(0.5F * (float)var8.getStepZ())).contract((double)var8.getStepX(), (double)var8.getStepY(), (double)var8.getStepZ());
               var9 = level.noCollision(var11.move(blockPos.relative(var8)));
            } else {
               var9 = true;
            }

            if(var9) {
               player.openMenu(var10);
               player.awardStat(Stats.OPEN_SHULKER_BOX);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return (BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getClickedFace());
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING});
   }

   public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
      BlockEntity var5 = level.getBlockEntity(blockPos);
      if(var5 instanceof ShulkerBoxBlockEntity) {
         ShulkerBoxBlockEntity var6 = (ShulkerBoxBlockEntity)var5;
         if(!level.isClientSide && player.isCreative() && !var6.isEmpty()) {
            ItemStack var7 = getColoredItemStack(this.getColor());
            CompoundTag var8 = var6.saveToTag(new CompoundTag());
            if(!var8.isEmpty()) {
               var7.addTagElement("BlockEntityTag", var8);
            }

            if(var6.hasCustomName()) {
               var7.setHoverName(var6.getCustomName());
            }

            ItemEntity var9 = new ItemEntity(level, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), var7);
            var9.setDefaultPickUpDelay();
            level.addFreshEntity(var9);
         } else {
            var6.unpackLootTable(player);
         }
      }

      super.playerWillDestroy(level, blockPos, blockState, player);
   }

   public List getDrops(BlockState blockState, LootContext.Builder lootContext$Builder) {
      BlockEntity var3 = (BlockEntity)lootContext$Builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
      if(var3 instanceof ShulkerBoxBlockEntity) {
         ShulkerBoxBlockEntity var4 = (ShulkerBoxBlockEntity)var3;
         lootContext$Builder = lootContext$Builder.withDynamicDrop(CONTENTS, (lootContext, consumer) -> {
            for(int var3 = 0; var3 < var4.getContainerSize(); ++var3) {
               consumer.accept(var4.getItem(var3));
            }

         });
      }

      return super.getDrops(blockState, lootContext$Builder);
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
      if(itemStack.hasCustomHoverName()) {
         BlockEntity var6 = level.getBlockEntity(blockPos);
         if(var6 instanceof ShulkerBoxBlockEntity) {
            ((ShulkerBoxBlockEntity)var6).setCustomName(itemStack.getHoverName());
         }
      }

   }

   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var1.getBlock() != var4.getBlock()) {
         BlockEntity var6 = level.getBlockEntity(blockPos);
         if(var6 instanceof ShulkerBoxBlockEntity) {
            level.updateNeighbourForOutputSignal(blockPos, var1.getBlock());
         }

         super.onRemove(var1, level, blockPos, var4, var5);
      }
   }

   public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List list, TooltipFlag tooltipFlag) {
      super.appendHoverText(itemStack, blockGetter, list, tooltipFlag);
      CompoundTag var5 = itemStack.getTagElement("BlockEntityTag");
      if(var5 != null) {
         if(var5.contains("LootTable", 8)) {
            list.add(new TextComponent("???????"));
         }

         if(var5.contains("Items", 9)) {
            NonNullList<ItemStack> var6 = NonNullList.withSize(27, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(var5, var6);
            int var7 = 0;
            int var8 = 0;

            for(ItemStack var10 : var6) {
               if(!var10.isEmpty()) {
                  ++var8;
                  if(var7 <= 4) {
                     ++var7;
                     Component var11 = var10.getHoverName().deepCopy();
                     var11.append(" x").append(String.valueOf(var10.getCount()));
                     list.add(var11);
                  }
               }
            }

            if(var8 - var7 > 0) {
               list.add((new TranslatableComponent("container.shulkerBox.more", new Object[]{Integer.valueOf(var8 - var7)})).withStyle(ChatFormatting.ITALIC));
            }
         }
      }

   }

   public PushReaction getPistonPushReaction(BlockState blockState) {
      return PushReaction.DESTROY;
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      BlockEntity var5 = blockGetter.getBlockEntity(blockPos);
      return var5 instanceof ShulkerBoxBlockEntity?Shapes.create(((ShulkerBoxBlockEntity)var5).getBoundingBox(blockState)):Shapes.block();
   }

   public boolean canOcclude(BlockState blockState) {
      return false;
   }

   public boolean hasAnalogOutputSignal(BlockState blockState) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
      return AbstractContainerMenu.getRedstoneSignalFromContainer((Container)level.getBlockEntity(blockPos));
   }

   public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
      ItemStack itemStack = super.getCloneItemStack(blockGetter, blockPos, blockState);
      ShulkerBoxBlockEntity var5 = (ShulkerBoxBlockEntity)blockGetter.getBlockEntity(blockPos);
      CompoundTag var6 = var5.saveToTag(new CompoundTag());
      if(!var6.isEmpty()) {
         itemStack.addTagElement("BlockEntityTag", var6);
      }

      return itemStack;
   }

   @Nullable
   public static DyeColor getColorFromItem(Item item) {
      return getColorFromBlock(Block.byItem(item));
   }

   @Nullable
   public static DyeColor getColorFromBlock(Block block) {
      return block instanceof ShulkerBoxBlock?((ShulkerBoxBlock)block).getColor():null;
   }

   public static Block getBlockByColor(@Nullable DyeColor dyeColor) {
      if(dyeColor == null) {
         return Blocks.SHULKER_BOX;
      } else {
         switch(dyeColor) {
         case WHITE:
            return Blocks.WHITE_SHULKER_BOX;
         case ORANGE:
            return Blocks.ORANGE_SHULKER_BOX;
         case MAGENTA:
            return Blocks.MAGENTA_SHULKER_BOX;
         case LIGHT_BLUE:
            return Blocks.LIGHT_BLUE_SHULKER_BOX;
         case YELLOW:
            return Blocks.YELLOW_SHULKER_BOX;
         case LIME:
            return Blocks.LIME_SHULKER_BOX;
         case PINK:
            return Blocks.PINK_SHULKER_BOX;
         case GRAY:
            return Blocks.GRAY_SHULKER_BOX;
         case LIGHT_GRAY:
            return Blocks.LIGHT_GRAY_SHULKER_BOX;
         case CYAN:
            return Blocks.CYAN_SHULKER_BOX;
         case PURPLE:
         default:
            return Blocks.PURPLE_SHULKER_BOX;
         case BLUE:
            return Blocks.BLUE_SHULKER_BOX;
         case BROWN:
            return Blocks.BROWN_SHULKER_BOX;
         case GREEN:
            return Blocks.GREEN_SHULKER_BOX;
         case RED:
            return Blocks.RED_SHULKER_BOX;
         case BLACK:
            return Blocks.BLACK_SHULKER_BOX;
         }
      }
   }

   @Nullable
   public DyeColor getColor() {
      return this.color;
   }

   public static ItemStack getColoredItemStack(@Nullable DyeColor dyeColor) {
      return new ItemStack(getBlockByColor(dyeColor));
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      return (BlockState)var1.setValue(FACING, rotation.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror mirror) {
      return var1.rotate(mirror.getRotation((Direction)var1.getValue(FACING)));
   }
}
