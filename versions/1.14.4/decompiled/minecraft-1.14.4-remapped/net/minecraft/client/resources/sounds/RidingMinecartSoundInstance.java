package net.minecraft.client.resources.sounds;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

@ClientJarOnly
public class RidingMinecartSoundInstance extends AbstractTickableSoundInstance {
   private final Player player;
   private final AbstractMinecart minecart;

   public RidingMinecartSoundInstance(Player player, AbstractMinecart minecart) {
      super(SoundEvents.MINECART_INSIDE, SoundSource.NEUTRAL);
      this.player = player;
      this.minecart = minecart;
      this.attenuation = SoundInstance.Attenuation.NONE;
      this.looping = true;
      this.delay = 0;
      this.volume = 0.0F;
   }

   public boolean canStartSilent() {
      return true;
   }

   public void tick() {
      if(!this.minecart.removed && this.player.isPassenger() && this.player.getVehicle() == this.minecart) {
         float var1 = Mth.sqrt(Entity.getHorizontalDistanceSqr(this.minecart.getDeltaMovement()));
         if((double)var1 >= 0.01D) {
            this.volume = 0.0F + Mth.clamp(var1, 0.0F, 1.0F) * 0.75F;
         } else {
            this.volume = 0.0F;
         }

      } else {
         this.stopped = true;
      }
   }
}
