package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.Level;

public class HangingEntityItem extends Item {
   private final EntityType type;

   public HangingEntityItem(EntityType type, Item.Properties item$Properties) {
      super(item$Properties);
      this.type = type;
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      BlockPos var2 = useOnContext.getClickedPos();
      Direction var3 = useOnContext.getClickedFace();
      BlockPos var4 = var2.relative(var3);
      Player var5 = useOnContext.getPlayer();
      ItemStack var6 = useOnContext.getItemInHand();
      if(var5 != null && !this.mayPlace(var5, var3, var6, var4)) {
         return InteractionResult.FAIL;
      } else {
         Level var7 = useOnContext.getLevel();
         HangingEntity var8;
         if(this.type == EntityType.PAINTING) {
            var8 = new Painting(var7, var4, var3);
         } else {
            if(this.type != EntityType.ITEM_FRAME) {
               return InteractionResult.SUCCESS;
            }

            var8 = new ItemFrame(var7, var4, var3);
         }

         CompoundTag var9 = var6.getTag();
         if(var9 != null) {
            EntityType.updateCustomEntityTag(var7, var5, var8, var9);
         }

         if(var8.survives()) {
            if(!var7.isClientSide) {
               var8.playPlacementSound();
               var7.addFreshEntity(var8);
            }

            var6.shrink(1);
         }

         return InteractionResult.SUCCESS;
      }
   }

   protected boolean mayPlace(Player player, Direction direction, ItemStack itemStack, BlockPos blockPos) {
      return !direction.getAxis().isVertical() && player.mayUseItemAt(blockPos, direction, itemStack);
   }
}
