package net.minecraft.client.sounds;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.WeighedSoundEvents;

@ClientJarOnly
public interface SoundEventListener {
   void onPlaySound(SoundInstance var1, WeighedSoundEvents var2);
}
