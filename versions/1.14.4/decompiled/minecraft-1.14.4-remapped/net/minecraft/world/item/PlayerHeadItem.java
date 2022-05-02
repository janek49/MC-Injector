package net.minecraft.world.item;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.apache.commons.lang3.StringUtils;

public class PlayerHeadItem extends StandingAndWallBlockItem {
   public PlayerHeadItem(Block var1, Block var2, Item.Properties item$Properties) {
      super(var1, var2, item$Properties);
   }

   public Component getName(ItemStack itemStack) {
      if(itemStack.getItem() == Items.PLAYER_HEAD && itemStack.hasTag()) {
         String var2 = null;
         CompoundTag var3 = itemStack.getTag();
         if(var3.contains("SkullOwner", 8)) {
            var2 = var3.getString("SkullOwner");
         } else if(var3.contains("SkullOwner", 10)) {
            CompoundTag var4 = var3.getCompound("SkullOwner");
            if(var4.contains("Name", 8)) {
               var2 = var4.getString("Name");
            }
         }

         if(var2 != null) {
            return new TranslatableComponent(this.getDescriptionId() + ".named", new Object[]{var2});
         }
      }

      return super.getName(itemStack);
   }

   public boolean verifyTagAfterLoad(CompoundTag compoundTag) {
      super.verifyTagAfterLoad(compoundTag);
      if(compoundTag.contains("SkullOwner", 8) && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
         GameProfile var2 = new GameProfile((UUID)null, compoundTag.getString("SkullOwner"));
         var2 = SkullBlockEntity.updateGameprofile(var2);
         compoundTag.put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), var2));
         return true;
      } else {
         return false;
      }
   }
}
