package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WritableBookItem extends Item {
   public WritableBookItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      Level var2 = useOnContext.getLevel();
      BlockPos var3 = useOnContext.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      return var4.getBlock() == Blocks.LECTERN?(LecternBlock.tryPlaceBook(var2, var3, var4, useOnContext.getItemInHand())?InteractionResult.SUCCESS:InteractionResult.PASS):InteractionResult.PASS;
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack var4 = player.getItemInHand(interactionHand);
      player.openItemGui(var4, interactionHand);
      player.awardStat(Stats.ITEM_USED.get(this));
      return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
   }

   public static boolean makeSureTagIsValid(@Nullable CompoundTag compoundTag) {
      if(compoundTag == null) {
         return false;
      } else if(!compoundTag.contains("pages", 9)) {
         return false;
      } else {
         ListTag var1 = compoundTag.getList("pages", 8);

         for(int var2 = 0; var2 < var1.size(); ++var2) {
            String var3 = var1.getString(var2);
            if(var3.length() > 32767) {
               return false;
            }
         }

         return true;
      }
   }
}
