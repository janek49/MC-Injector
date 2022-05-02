package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.realms.RealmsLabel;

@ClientJarOnly
public class RealmsLabelProxy implements GuiEventListener {
   private final RealmsLabel label;

   public RealmsLabelProxy(RealmsLabel label) {
      this.label = label;
   }

   public RealmsLabel getLabel() {
      return this.label;
   }
}
