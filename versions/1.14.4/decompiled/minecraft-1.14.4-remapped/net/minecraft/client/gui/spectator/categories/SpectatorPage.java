package net.minecraft.client.gui.spectator.categories;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.base.MoreObjects;
import java.util.List;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;

@ClientJarOnly
public class SpectatorPage {
   private final SpectatorMenuCategory category;
   private final List items;
   private final int selection;

   public SpectatorPage(SpectatorMenuCategory category, List items, int selection) {
      this.category = category;
      this.items = items;
      this.selection = selection;
   }

   public SpectatorMenuItem getItem(int i) {
      return i >= 0 && i < this.items.size()?(SpectatorMenuItem)MoreObjects.firstNonNull(this.items.get(i), SpectatorMenu.EMPTY_SLOT):SpectatorMenu.EMPTY_SLOT;
   }

   public int getSelectedSlot() {
      return this.selection;
   }
}
