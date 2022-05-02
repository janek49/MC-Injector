package com.mojang.realmsclient.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.realms.RealmsScreen;

@ClientJarOnly
public abstract class RealmsScreenWithCallback extends RealmsScreen {
   abstract void callback(Object var1);
}
