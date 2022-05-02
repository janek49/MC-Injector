package com.mojang.realmsclient.gui;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;

@ClientJarOnly
public abstract class LongRunningTask implements Runnable {
   protected RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen;

   public void setScreen(RealmsLongRunningMcoTaskScreen screen) {
      this.longRunningMcoTaskScreen = screen;
   }

   public void error(String string) {
      this.longRunningMcoTaskScreen.error(string);
   }

   public void setTitle(String title) {
      this.longRunningMcoTaskScreen.setTitle(title);
   }

   public boolean aborted() {
      return this.longRunningMcoTaskScreen.aborted();
   }

   public void tick() {
   }

   public void init() {
   }

   public void abortTask() {
   }
}
