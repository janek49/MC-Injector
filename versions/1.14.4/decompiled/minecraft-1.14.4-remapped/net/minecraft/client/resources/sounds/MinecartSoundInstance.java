package net.minecraft.client.resources.sounds;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

@ClientJarOnly
public class MinecartSoundInstance extends AbstractTickableSoundInstance {
   private final AbstractMinecart minecart;
   private float pitch = 0.0F;

   public MinecartSoundInstance(AbstractMinecart minecart) {
      super(SoundEvents.MINECART_RIDING, SoundSource.NEUTRAL);
      this.minecart = minecart;
      this.looping = true;
      this.delay = 0;
      this.volume = 0.0F;
      this.x = (float)minecart.x;
      this.y = (float)minecart.y;
      this.z = (float)minecart.z;
   }

   public boolean canStartSilent() {
      return true;
   }

   public void tick() {
      if(this.minecart.removed) {
         this.stopped = true;
      } else {
         this.x = (float)this.minecart.x;
         this.y = (float)this.minecart.y;
         this.z = (float)this.minecart.z;
         float var1 = Mth.sqrt(Entity.getHorizontalDistanceSqr(this.minecart.getDeltaMovement()));
         if((double)var1 >= 0.01D) {
            this.pitch = Mth.clamp(this.pitch + 0.0025F, 0.0F, 1.0F);
            this.volume = Mth.lerp(Mth.clamp(var1, 0.0F, 0.5F), 0.0F, 0.7F);
         } else {
            this.pitch = 0.0F;
            this.volume = 0.0F;
         }

      }
   }
}
