package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.realms.RealmsAbstractButtonProxy;

@ClientJarOnly
public abstract class AbstractRealmsButton {
   public abstract AbstractWidget getProxy();

   public boolean active() {
      return ((RealmsAbstractButtonProxy)this.getProxy()).active();
   }

   public void active(boolean b) {
      ((RealmsAbstractButtonProxy)this.getProxy()).active(b);
   }

   public boolean isVisible() {
      return ((RealmsAbstractButtonProxy)this.getProxy()).isVisible();
   }

   public void setVisible(boolean visible) {
      ((RealmsAbstractButtonProxy)this.getProxy()).setVisible(visible);
   }

   public void render(int var1, int var2, float var3) {
      this.getProxy().render(var1, var2, var3);
   }

   public void blit(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.getProxy().blit(var1, var2, var3, var4, var5, var6);
   }

   public void tick() {
   }
}
