package net.minecraft.client.gui.spectator;

import com.fox2code.repacker.ClientJarOnly;
import java.util.List;
import net.minecraft.network.chat.Component;

@ClientJarOnly
public interface SpectatorMenuCategory {
   List getItems();

   Component getPrompt();
}
