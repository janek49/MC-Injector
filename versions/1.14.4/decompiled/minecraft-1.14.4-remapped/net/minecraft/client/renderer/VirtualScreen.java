package net.minecraft.client.renderer;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.Monitor;
import com.mojang.blaze3d.platform.MonitorCreator;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;

@ClientJarOnly
public final class VirtualScreen implements AutoCloseable {
   private final Minecraft minecraft;
   private final ScreenManager screenManager;

   public VirtualScreen(Minecraft minecraft) {
      this.minecraft = minecraft;
      this.screenManager = new ScreenManager(Monitor::<init>);
   }

   public Window newWindow(DisplayData displayData, String var2, String var3) {
      return new Window(this.minecraft, this.screenManager, displayData, var2, var3);
   }

   public void close() {
      this.screenManager.shutdown();
   }
}
