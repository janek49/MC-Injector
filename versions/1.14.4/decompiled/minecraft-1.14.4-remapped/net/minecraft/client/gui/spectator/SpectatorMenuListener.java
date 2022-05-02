package net.minecraft.client.gui.spectator;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.gui.spectator.SpectatorMenu;

@ClientJarOnly
public interface SpectatorMenuListener {
   void onSpectatorMenuClosed(SpectatorMenu var1);
}
