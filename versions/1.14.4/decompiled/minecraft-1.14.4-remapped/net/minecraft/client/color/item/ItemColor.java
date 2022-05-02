package net.minecraft.client.color.item;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.world.item.ItemStack;

@ClientJarOnly
public interface ItemColor {
   int getColor(ItemStack var1, int var2);
}
