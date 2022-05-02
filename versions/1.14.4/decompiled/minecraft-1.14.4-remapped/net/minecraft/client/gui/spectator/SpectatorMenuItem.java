package net.minecraft.client.gui.spectator;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.network.chat.Component;

@ClientJarOnly
public interface SpectatorMenuItem {
   void selectItem(SpectatorMenu var1);

   Component getName();

   void renderIcon(float var1, int var2);

   boolean isEnabled();
}
