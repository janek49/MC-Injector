package net.minecraft.world.level.block;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;

public class PlayerHeadBlock extends SkullBlock {
   protected PlayerHeadBlock(Block.Properties block$Properties) {
      super(SkullBlock.Types.PLAYER, block$Properties);
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
      super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
      BlockEntity var6 = level.getBlockEntity(blockPos);
      if(var6 instanceof SkullBlockEntity) {
         SkullBlockEntity var7 = (SkullBlockEntity)var6;
         GameProfile var8 = null;
         if(itemStack.hasTag()) {
            CompoundTag var9 = itemStack.getTag();
            if(var9.contains("SkullOwner", 10)) {
               var8 = NbtUtils.readGameProfile(var9.getCompound("SkullOwner"));
            } else if(var9.contains("SkullOwner", 8) && !StringUtils.isBlank(var9.getString("SkullOwner"))) {
               var8 = new GameProfile((UUID)null, var9.getString("SkullOwner"));
            }
         }

         var7.setOwner(var8);
      }

   }
}
