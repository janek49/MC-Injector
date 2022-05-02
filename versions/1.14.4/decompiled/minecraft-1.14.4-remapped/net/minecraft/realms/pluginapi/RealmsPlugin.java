package net.minecraft.realms.pluginapi;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.datafixers.util.Either;

@ClientJarOnly
public interface RealmsPlugin {
   Either tryLoad(String var1);
}
