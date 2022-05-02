package net.minecraft.client.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;

@ClientJarOnly
public abstract class Overlay extends GuiComponent implements Widget {
   public boolean isPauseScreen() {
      return true;
   }
}
