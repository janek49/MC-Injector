package net.minecraft.realms.pluginapi;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.realms.RealmsScreen;

@ClientJarOnly
public interface LoadedRealmsPlugin {
   RealmsScreen getMainScreen(RealmsScreen var1);

   RealmsScreen getNotificationsScreen(RealmsScreen var1);
}
