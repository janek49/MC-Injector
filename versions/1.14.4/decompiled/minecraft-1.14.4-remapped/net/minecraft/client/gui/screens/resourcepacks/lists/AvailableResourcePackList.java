package net.minecraft.client.gui.screens.resourcepacks.lists;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.resourcepacks.lists.ResourcePackList;
import net.minecraft.network.chat.TranslatableComponent;

@ClientJarOnly
public class AvailableResourcePackList extends ResourcePackList {
   public AvailableResourcePackList(Minecraft minecraft, int var2, int var3) {
      super(minecraft, var2, var3, new TranslatableComponent("resourcePack.available.title", new Object[0]));
   }
}
