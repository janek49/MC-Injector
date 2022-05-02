package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WrittenBookItem extends Item {
   public WrittenBookItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public static boolean makeSureTagIsValid(@Nullable CompoundTag compoundTag) {
      if(!WritableBookItem.makeSureTagIsValid(compoundTag)) {
         return false;
      } else if(!compoundTag.contains("title", 8)) {
         return false;
      } else {
         String var1 = compoundTag.getString("title");
         return var1.length() > 32?false:compoundTag.contains("author", 8);
      }
   }

   public static int getGeneration(ItemStack itemStack) {
      return itemStack.getTag().getInt("generation");
   }

   public static int getPageCount(ItemStack itemStack) {
      CompoundTag var1 = itemStack.getTag();
      return var1 != null?var1.getList("pages", 8).size():0;
   }

   public Component getName(ItemStack itemStack) {
      if(itemStack.hasTag()) {
         CompoundTag var2 = itemStack.getTag();
         String var3 = var2.getString("title");
         if(!StringUtil.isNullOrEmpty(var3)) {
            return new TextComponent(var3);
         }
      }

      return super.getName(itemStack);
   }

   public void appendHoverText(ItemStack itemStack, @Nullable Level level, List list, TooltipFlag tooltipFlag) {
      if(itemStack.hasTag()) {
         CompoundTag var5 = itemStack.getTag();
         String var6 = var5.getString("author");
         if(!StringUtil.isNullOrEmpty(var6)) {
            list.add((new TranslatableComponent("book.byAuthor", new Object[]{var6})).withStyle(ChatFormatting.GRAY));
         }

         list.add((new TranslatableComponent("book.generation." + var5.getInt("generation"), new Object[0])).withStyle(ChatFormatting.GRAY));
      }

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

   public static boolean resolveBookComponents(ItemStack itemStack, @Nullable CommandSourceStack commandSourceStack, @Nullable Player player) {
      CompoundTag var3 = itemStack.getTag();
      if(var3 != null && !var3.getBoolean("resolved")) {
         var3.putBoolean("resolved", true);
         if(!makeSureTagIsValid(var3)) {
            return false;
         } else {
            ListTag var4 = var3.getList("pages", 8);

            for(int var5 = 0; var5 < var4.size(); ++var5) {
               String var6 = var4.getString(var5);

               Component var7;
               try {
                  var7 = Component.Serializer.fromJsonLenient(var6);
                  var7 = ComponentUtils.updateForEntity(commandSourceStack, var7, player, 0);
               } catch (Exception var9) {
                  var7 = new TextComponent(var6);
               }

               var4.set(var5, (Tag)(new StringTag(Component.Serializer.toJson(var7))));
            }

            var3.put("pages", var4);
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean isFoil(ItemStack itemStack) {
      return true;
   }
}
