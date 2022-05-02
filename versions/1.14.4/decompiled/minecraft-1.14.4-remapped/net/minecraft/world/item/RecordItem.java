package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;

public class RecordItem extends Item {
   private static final Map BY_NAME = Maps.newHashMap();
   private final int analogOutput;
   private final SoundEvent sound;

   protected RecordItem(int analogOutput, SoundEvent sound, Item.Properties item$Properties) {
      super(item$Properties);
      this.analogOutput = analogOutput;
      this.sound = sound;
      BY_NAME.put(this.sound, this);
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      Level var2 = useOnContext.getLevel();
      BlockPos var3 = useOnContext.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      if(var4.getBlock() == Blocks.JUKEBOX && !((Boolean)var4.getValue(JukeboxBlock.HAS_RECORD)).booleanValue()) {
         ItemStack var5 = useOnContext.getItemInHand();
         if(!var2.isClientSide) {
            ((JukeboxBlock)Blocks.JUKEBOX).setRecord(var2, var3, var4, var5);
            var2.levelEvent((Player)null, 1010, var3, Item.getId(this));
            var5.shrink(1);
            Player var6 = useOnContext.getPlayer();
            if(var6 != null) {
               var6.awardStat(Stats.PLAY_RECORD);
            }
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public int getAnalogOutput() {
      return this.analogOutput;
   }

   public void appendHoverText(ItemStack itemStack, @Nullable Level level, List list, TooltipFlag tooltipFlag) {
      list.add(this.getDisplayName().withStyle(ChatFormatting.GRAY));
   }

   public Component getDisplayName() {
      return new TranslatableComponent(this.getDescriptionId() + ".desc", new Object[0]);
   }

   @Nullable
   public static RecordItem getBySound(SoundEvent sound) {
      return (RecordItem)BY_NAME.get(sound);
   }

   public SoundEvent getSound() {
      return this.sound;
   }
}
