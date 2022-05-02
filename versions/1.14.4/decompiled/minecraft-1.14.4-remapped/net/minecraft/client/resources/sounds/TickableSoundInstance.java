package net.minecraft.client.resources.sounds;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.resources.sounds.SoundInstance;

@ClientJarOnly
public interface TickableSoundInstance extends SoundInstance {
   boolean isStopped();

   void tick();
}
