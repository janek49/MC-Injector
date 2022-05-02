package com.mojang.blaze3d.platform;

import com.fox2code.repacker.ClientJarOnly;

@ClientJarOnly
public interface WindowEventHandler {
   void setWindowActive(boolean var1);

   void updateDisplay(boolean var1);

   void resizeDisplay();
}
