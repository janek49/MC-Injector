package net.minecraft.client.resources.sounds;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

@ClientJarOnly
public class UnderwaterAmbientSoundInstances {
   @ClientJarOnly
   public static class SubSound extends AbstractTickableSoundInstance {
      private final LocalPlayer player;

      protected SubSound(LocalPlayer player, SoundEvent soundEvent) {
         super(soundEvent, SoundSource.AMBIENT);
         this.player = player;
         this.looping = false;
         this.delay = 0;
         this.volume = 1.0F;
         this.priority = true;
         this.relative = true;
      }

      public void tick() {
         if(this.player.removed || !this.player.isUnderWater()) {
            this.stopped = true;
         }

      }
   }

   @ClientJarOnly
   public static class UnderwaterAmbientSoundInstance extends AbstractTickableSoundInstance {
      private final LocalPlayer player;
      private int fade;

      public UnderwaterAmbientSoundInstance(LocalPlayer player) {
         super(SoundEvents.AMBIENT_UNDERWATER_LOOP, SoundSource.AMBIENT);
         this.player = player;
         this.looping = true;
         this.delay = 0;
         this.volume = 1.0F;
         this.priority = true;
         this.relative = true;
      }

      public void tick() {
         if(!this.player.removed && this.fade >= 0) {
            if(this.player.isUnderWater()) {
               ++this.fade;
            } else {
               this.fade -= 2;
            }

            this.fade = Math.min(this.fade, 40);
            this.volume = Math.max(0.0F, Math.min((float)this.fade / 40.0F, 1.0F));
         } else {
            this.stopped = true;
         }
      }
   }
}
