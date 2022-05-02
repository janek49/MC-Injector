package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

@Immutable
public class LockCode {
   public static final LockCode NO_LOCK = new LockCode("");
   private final String key;

   public LockCode(String key) {
      this.key = key;
   }

   public boolean unlocksWith(ItemStack itemStack) {
      return this.key.isEmpty() || !itemStack.isEmpty() && itemStack.hasCustomHoverName() && this.key.equals(itemStack.getHoverName().getString());
   }

   public void addToTag(CompoundTag compoundTag) {
      if(!this.key.isEmpty()) {
         compoundTag.putString("Lock", this.key);
      }

   }

   public static LockCode fromTag(CompoundTag tag) {
      return tag.contains("Lock", 8)?new LockCode(tag.getString("Lock")):NO_LOCK;
   }
}
