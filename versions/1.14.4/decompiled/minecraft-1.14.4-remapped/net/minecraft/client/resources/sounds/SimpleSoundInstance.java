package net.minecraft.client.resources.sounds;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

@ClientJarOnly
public class SimpleSoundInstance extends AbstractSoundInstance {
   public SimpleSoundInstance(SoundEvent soundEvent, SoundSource soundSource, float var3, float var4, BlockPos blockPos) {
      this(soundEvent, soundSource, var3, var4, (float)blockPos.getX() + 0.5F, (float)blockPos.getY() + 0.5F, (float)blockPos.getZ() + 0.5F);
   }

   public static SimpleSoundInstance forUI(SoundEvent soundEvent, float var1) {
      return forUI(soundEvent, var1, 0.25F);
   }

   public static SimpleSoundInstance forUI(SoundEvent soundEvent, float var1, float var2) {
      return new SimpleSoundInstance(soundEvent.getLocation(), SoundSource.MASTER, var2, var1, false, 0, SoundInstance.Attenuation.NONE, 0.0F, 0.0F, 0.0F, true);
   }

   public static SimpleSoundInstance forMusic(SoundEvent soundEvent) {
      return new SimpleSoundInstance(soundEvent.getLocation(), SoundSource.MUSIC, 1.0F, 1.0F, false, 0, SoundInstance.Attenuation.NONE, 0.0F, 0.0F, 0.0F, true);
   }

   public static SimpleSoundInstance forRecord(SoundEvent soundEvent, float var1, float var2, float var3) {
      return new SimpleSoundInstance(soundEvent, SoundSource.RECORDS, 4.0F, 1.0F, false, 0, SoundInstance.Attenuation.LINEAR, var1, var2, var3);
   }

   public SimpleSoundInstance(SoundEvent soundEvent, SoundSource soundSource, float var3, float var4, float var5, float var6, float var7) {
      this(soundEvent, soundSource, var3, var4, false, 0, SoundInstance.Attenuation.LINEAR, var5, var6, var7);
   }

   private SimpleSoundInstance(SoundEvent soundEvent, SoundSource soundSource, float var3, float var4, boolean var5, int var6, SoundInstance.Attenuation soundInstance$Attenuation, float var8, float var9, float var10) {
      this(soundEvent.getLocation(), soundSource, var3, var4, var5, var6, soundInstance$Attenuation, var8, var9, var10, false);
   }

   public SimpleSoundInstance(ResourceLocation resourceLocation, SoundSource soundSource, float volume, float pitch, boolean looping, int delay, SoundInstance.Attenuation attenuation, float x, float y, float z, boolean relative) {
      super(resourceLocation, soundSource);
      this.volume = volume;
      this.pitch = pitch;
      this.x = x;
      this.y = y;
      this.z = z;
      this.looping = looping;
      this.delay = delay;
      this.attenuation = attenuation;
      this.relative = relative;
   }
}
