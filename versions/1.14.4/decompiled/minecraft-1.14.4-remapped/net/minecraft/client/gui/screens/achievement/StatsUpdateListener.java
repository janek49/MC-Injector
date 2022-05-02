package net.minecraft.client.gui.screens.achievement;

import com.fox2code.repacker.ClientJarOnly;

@ClientJarOnly
public interface StatsUpdateListener {
   String[] LOADING_SYMBOLS = new String[]{"oooooo", "Oooooo", "oOoooo", "ooOooo", "oooOoo", "ooooOo", "oooooO"};

   void onStatsUpdated();
}
