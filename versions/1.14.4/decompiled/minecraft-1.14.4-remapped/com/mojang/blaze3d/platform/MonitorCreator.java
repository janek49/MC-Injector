package com.mojang.blaze3d.platform;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.Monitor;

@ClientJarOnly
public interface MonitorCreator {
   Monitor createMonitor(long var1);
}
