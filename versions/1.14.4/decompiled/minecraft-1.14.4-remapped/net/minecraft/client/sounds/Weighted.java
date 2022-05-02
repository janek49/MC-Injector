package net.minecraft.client.sounds;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.sounds.SoundEngine;

@ClientJarOnly
public interface Weighted {
   int getWeight();

   Object getSound();

   void preloadIfRequired(SoundEngine var1);
}
