package net.minecraft.client.resources.sounds;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

@ClientJarOnly
public abstract class AbstractTickableSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {
   protected boolean stopped;

   protected AbstractTickableSoundInstance(SoundEvent soundEvent, SoundSource soundSource) {
      super(soundEvent, soundSource);
   }

   public boolean isStopped() {
      return this.stopped;
   }
}
