package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.realms.RealmsGuiEventListener;
import net.minecraft.realms.RealmsLabelProxy;
import net.minecraft.realms.RealmsScreen;

@ClientJarOnly
public class RealmsLabel extends RealmsGuiEventListener {
   private final RealmsLabelProxy proxy = new RealmsLabelProxy(this);
   private final String text;
   private final int x;
   private final int y;
   private final int color;

   public RealmsLabel(String text, int x, int y, int color) {
      this.text = text;
      this.x = x;
      this.y = y;
      this.color = color;
   }

   public void render(RealmsScreen realmsScreen) {
      realmsScreen.drawCenteredString(this.text, this.x, this.y, this.color);
   }

   public GuiEventListener getProxy() {
      return this.proxy;
   }

   public String getText() {
      return this.text;
   }
}
