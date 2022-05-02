package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluid;

public class FishBucketItem extends BucketItem {
   private final EntityType type;

   public FishBucketItem(EntityType type, Fluid fluid, Item.Properties item$Properties) {
      super(fluid, item$Properties);
      this.type = type;
   }

   public void checkExtraContent(Level level, ItemStack itemStack, BlockPos blockPos) {
      if(!level.isClientSide) {
         this.spawn(level, itemStack, blockPos);
      }

   }

   protected void playEmptySound(@Nullable Player player, LevelAccessor levelAccessor, BlockPos blockPos) {
      levelAccessor.playSound(player, blockPos, SoundEvents.BUCKET_EMPTY_FISH, SoundSource.NEUTRAL, 1.0F, 1.0F);
   }

   private void spawn(Level level, ItemStack itemStack, BlockPos blockPos) {
      Entity var4 = this.type.spawn(level, itemStack, (Player)null, blockPos, MobSpawnType.BUCKET, true, false);
      if(var4 != null) {
         ((AbstractFish)var4).setFromBucket(true);
      }

   }

   public void appendHoverText(ItemStack itemStack, @Nullable Level level, List list, TooltipFlag tooltipFlag) {
      if(this.type == EntityType.TROPICAL_FISH) {
         CompoundTag var5 = itemStack.getTag();
         if(var5 != null && var5.contains("BucketVariantTag", 3)) {
            int var6 = var5.getInt("BucketVariantTag");
            ChatFormatting[] vars7 = new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY};
            String var8 = "color.minecraft." + TropicalFish.getBaseColor(var6);
            String var9 = "color.minecraft." + TropicalFish.getPatternColor(var6);

            for(int var10 = 0; var10 < TropicalFish.COMMON_VARIANTS.length; ++var10) {
               if(var6 == TropicalFish.COMMON_VARIANTS[var10]) {
                  list.add((new TranslatableComponent(TropicalFish.getPredefinedName(var10), new Object[0])).withStyle(vars7));
                  return;
               }
            }

            list.add((new TranslatableComponent(TropicalFish.getFishTypeName(var6), new Object[0])).withStyle(vars7));
            Component var10 = new TranslatableComponent(var8, new Object[0]);
            if(!var8.equals(var9)) {
               var10.append(", ").append((Component)(new TranslatableComponent(var9, new Object[0])));
            }

            var10.withStyle(vars7);
            list.add(var10);
         }
      }

   }
}
