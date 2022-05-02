package net.minecraft.client.resources.sounds;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

@ClientJarOnly
public class EntityBoundSoundInstance extends AbstractTickableSoundInstance {
   private final Entity entity;

   public EntityBoundSoundInstance(SoundEvent soundEvent, SoundSource soundSource, Entity entity) {
      this(soundEvent, soundSource, 1.0F, 1.0F, entity);
   }

   public EntityBoundSoundInstance(SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch, Entity entity) {
      super(soundEvent, soundSource);
      this.volume = volume;
      this.pitch = pitch;
      this.entity = entity;
      this.x = (float)this.entity.x;
      this.y = (float)this.entity.y;
      this.z = (float)this.entity.z;
   }

   public void tick() {
      if(this.entity.removed) {
         this.stopped = true;
      } else {
         this.x = (float)this.entity.x;
         this.y = (float)this.entity.y;
         this.z = (float)this.entity.z;
      }
   }
}
