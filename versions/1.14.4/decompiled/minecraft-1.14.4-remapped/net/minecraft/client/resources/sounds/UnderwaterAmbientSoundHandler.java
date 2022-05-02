package net.minecraft.client.resources.sounds;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundInstances;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvents;

@ClientJarOnly
public class UnderwaterAmbientSoundHandler implements AmbientSoundHandler {
   private final LocalPlayer player;
   private final SoundManager soundManager;
   private int tick_delay = 0;

   public UnderwaterAmbientSoundHandler(LocalPlayer player, SoundManager soundManager) {
      this.player = player;
      this.soundManager = soundManager;
   }

   public void tick() {
      --this.tick_delay;
      if(this.tick_delay <= 0 && this.player.isUnderWater()) {
         float var1 = this.player.level.random.nextFloat();
         if(var1 < 1.0E-4F) {
            this.tick_delay = 0;
            this.soundManager.play(new UnderwaterAmbientSoundInstances.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE));
         } else if(var1 < 0.001F) {
            this.tick_delay = 0;
            this.soundManager.play(new UnderwaterAmbientSoundInstances.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS_RARE));
         } else if(var1 < 0.01F) {
            this.tick_delay = 0;
            this.soundManager.play(new UnderwaterAmbientSoundInstances.SubSound(this.player, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS));
         }
      }

   }
}
