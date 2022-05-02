package net.minecraft.client.player.inventory;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.ForwardingList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

@ClientJarOnly
public class Hotbar extends ForwardingList {
   private final NonNullList items = NonNullList.withSize(Inventory.getSelectionSize(), ItemStack.EMPTY);

   protected List delegate() {
      return this.items;
   }

   public ListTag createTag() {
      ListTag listTag = new ListTag();

      for(ItemStack var3 : this.delegate()) {
         listTag.add(var3.save(new CompoundTag()));
      }

      return listTag;
   }

   public void fromTag(ListTag tag) {
      List<ItemStack> var2 = this.delegate();

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         var2.set(var3, ItemStack.of(tag.getCompound(var3)));
      }

   }

   public boolean isEmpty() {
      for(ItemStack var2 : this.delegate()) {
         if(!var2.isEmpty()) {
            return false;
         }
      }

      return true;
   }
}
