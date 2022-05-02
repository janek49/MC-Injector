package net.minecraft.client.resources.sounds;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

@ClientJarOnly
public class ElytraOnPlayerSoundInstance extends AbstractTickableSoundInstance {
   private final LocalPlayer player;
   private int time;

   public ElytraOnPlayerSoundInstance(LocalPlayer player) {
      super(SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS);
      this.player = player;
      this.looping = true;
      this.delay = 0;
      this.volume = 0.1F;
   }

   public void tick() {
      ++this.time;
      if(!this.player.removed && (this.time <= 20 || this.player.isFallFlying())) {
         this.x = (float)this.player.x;
         this.y = (float)this.player.y;
         this.z = (float)this.player.z;
         float var1 = (float)this.player.getDeltaMovement().lengthSqr();
         if((double)var1 >= 1.0E-7D) {
            this.volume = Mth.clamp(var1 / 4.0F, 0.0F, 1.0F);
         } else {
            this.volume = 0.0F;
         }

         if(this.time < 20) {
            this.volume = 0.0F;
         } else if(this.time < 40) {
            this.volume = (float)((double)this.volume * ((double)(this.time - 20) / 20.0D));
         }

         float var2 = 0.8F;
         if(this.volume > 0.8F) {
            this.pitch = 1.0F + (this.volume - 0.8F);
         } else {
            this.pitch = 1.0F;
         }

      } else {
         this.stopped = true;
      }
   }
}
