package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemInput implements Predicate {
   private static final Dynamic2CommandExceptionType ERROR_STACK_TOO_BIG = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("arguments.item.overstacked", new Object[]{var0, var1});
   });
   private final Item item;
   @Nullable
   private final CompoundTag tag;

   public ItemInput(Item item, @Nullable CompoundTag tag) {
      this.item = item;
      this.tag = tag;
   }

   public Item getItem() {
      return this.item;
   }

   public boolean test(ItemStack itemStack) {
      return itemStack.getItem() == this.item && NbtUtils.compareNbt(this.tag, itemStack.getTag(), true);
   }

   public ItemStack createItemStack(int var1, boolean var2) throws CommandSyntaxException {
      ItemStack itemStack = new ItemStack(this.item, var1);
      if(this.tag != null) {
         itemStack.setTag(this.tag);
      }

      if(var2 && var1 > itemStack.getMaxStackSize()) {
         throw ERROR_STACK_TOO_BIG.create(Registry.ITEM.getKey(this.item), Integer.valueOf(itemStack.getMaxStackSize()));
      } else {
         return itemStack;
      }
   }

   public String serialize() {
      StringBuilder var1 = new StringBuilder(Registry.ITEM.getId(this.item));
      if(this.tag != null) {
         var1.append(this.tag);
      }

      return var1.toString();
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((ItemStack)var1);
   }
}
