package net.minecraft.client.color.item;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@ClientJarOnly
public class ItemColors {
   private final IdMapper itemColors = new IdMapper(32);

   public static ItemColors createDefault(BlockColors blockColors) {
      ItemColors itemColors = new ItemColors();
      itemColors.register((itemStack, var1) -> {
         return var1 > 0?-1:((DyeableLeatherItem)itemStack.getItem()).getColor(itemStack);
      }, new ItemLike[]{Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.LEATHER_HORSE_ARMOR});
      itemColors.register((itemStack, var1) -> {
         return GrassColor.get(0.5D, 1.0D);
      }, new ItemLike[]{Blocks.TALL_GRASS, Blocks.LARGE_FERN});
      itemColors.register((itemStack, var1) -> {
         if(var1 != 1) {
            return -1;
         } else {
            CompoundTag var2 = itemStack.getTagElement("Explosion");
            int[] vars3 = var2 != null && var2.contains("Colors", 11)?var2.getIntArray("Colors"):null;
            if(vars3 == null) {
               return 9079434;
            } else if(vars3.length == 1) {
               return vars3[0];
            } else {
               int var4 = 0;
               int var5 = 0;
               int var6 = 0;

               for(int var10 : vars3) {
                  var4 += (var10 & 16711680) >> 16;
                  var5 += (var10 & '\uff00') >> 8;
                  var6 += (var10 & 255) >> 0;
               }

               var4 = var4 / vars3.length;
               var5 = var5 / vars3.length;
               var6 = var6 / vars3.length;
               return var4 << 16 | var5 << 8 | var6;
            }
         }
      }, new ItemLike[]{Items.FIREWORK_STAR});
      itemColors.register((itemStack, var1) -> {
         return var1 > 0?-1:PotionUtils.getColor(itemStack);
      }, new ItemLike[]{Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION});

      for(SpawnEggItem var3 : SpawnEggItem.eggs()) {
         itemColors.register((itemStack, var2) -> {
            return var3.getColor(var2);
         }, new ItemLike[]{var3});
      }

      itemColors.register((itemStack, var2) -> {
         BlockState var3 = ((BlockItem)itemStack.getItem()).getBlock().defaultBlockState();
         return blockColors.getColor(var3, (BlockAndBiomeGetter)null, (BlockPos)null, var2);
      }, new ItemLike[]{Blocks.GRASS_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.VINE, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.LILY_PAD});
      itemColors.register((itemStack, var1) -> {
         return var1 == 0?PotionUtils.getColor(itemStack):-1;
      }, new ItemLike[]{Items.TIPPED_ARROW});
      itemColors.register((itemStack, var1) -> {
         return var1 == 0?-1:MapItem.getColor(itemStack);
      }, new ItemLike[]{Items.FILLED_MAP});
      return itemColors;
   }

   public int getColor(ItemStack itemStack, int var2) {
      ItemColor var3 = (ItemColor)this.itemColors.byId(Registry.ITEM.getId(itemStack.getItem()));
      return var3 == null?-1:var3.getColor(itemStack, var2);
   }

   public void register(ItemColor itemColor, ItemLike... itemLikes) {
      for(ItemLike var6 : itemLikes) {
         this.itemColors.addMapping(itemColor, Item.getId(var6.asItem()));
      }

   }
}
