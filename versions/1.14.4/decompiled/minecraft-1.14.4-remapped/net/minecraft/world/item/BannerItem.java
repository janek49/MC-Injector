package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.apache.commons.lang3.Validate;

public class BannerItem extends StandingAndWallBlockItem {
   public BannerItem(Block var1, Block var2, Item.Properties item$Properties) {
      super(var1, var2, item$Properties);
      Validate.isInstanceOf(AbstractBannerBlock.class, var1);
      Validate.isInstanceOf(AbstractBannerBlock.class, var2);
   }

   public static void appendHoverTextFromBannerBlockEntityTag(ItemStack itemStack, List list) {
      CompoundTag var2 = itemStack.getTagElement("BlockEntityTag");
      if(var2 != null && var2.contains("Patterns")) {
         ListTag var3 = var2.getList("Patterns", 10);

         for(int var4 = 0; var4 < var3.size() && var4 < 6; ++var4) {
            CompoundTag var5 = var3.getCompound(var4);
            DyeColor var6 = DyeColor.byId(var5.getInt("Color"));
            BannerPattern var7 = BannerPattern.byHash(var5.getString("Pattern"));
            if(var7 != null) {
               list.add((new TranslatableComponent("block.minecraft.banner." + var7.getFilename() + '.' + var6.getName(), new Object[0])).withStyle(ChatFormatting.GRAY));
            }
         }

      }
   }

   public DyeColor getColor() {
      return ((AbstractBannerBlock)this.getBlock()).getColor();
   }

   public void appendHoverText(ItemStack itemStack, @Nullable Level level, List list, TooltipFlag tooltipFlag) {
      appendHoverTextFromBannerBlockEntityTag(itemStack, list);
   }
}
