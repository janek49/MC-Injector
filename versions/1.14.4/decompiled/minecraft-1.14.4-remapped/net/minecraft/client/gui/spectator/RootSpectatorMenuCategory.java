package net.minecraft.client.gui.spectator;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;
import net.minecraft.client.gui.spectator.categories.TeleportToPlayerMenuCategory;
import net.minecraft.client.gui.spectator.categories.TeleportToTeamMenuCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

@ClientJarOnly
public class RootSpectatorMenuCategory implements SpectatorMenuCategory {
   private final List items = Lists.newArrayList();

   public RootSpectatorMenuCategory() {
      this.items.add(new TeleportToPlayerMenuCategory());
      this.items.add(new TeleportToTeamMenuCategory());
   }

   public List getItems() {
      return this.items;
   }

   public Component getPrompt() {
      return new TranslatableComponent("spectatorMenu.root.prompt", new Object[0]);
   }
}
