package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.realms.AbstractRealmsButton;

@ClientJarOnly
public interface RealmsAbstractButtonProxy {
   AbstractRealmsButton getButton();

   boolean active();

   void active(boolean var1);

   boolean isVisible();

   void setVisible(boolean var1);
}
