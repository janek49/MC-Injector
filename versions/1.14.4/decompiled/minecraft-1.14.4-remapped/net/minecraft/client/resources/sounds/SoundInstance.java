package net.minecraft.client.resources.sounds;

import com.fox2code.repacker.ClientJarOnly;
import javax.annotation.Nullable;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

@ClientJarOnly
public interface SoundInstance {
   ResourceLocation getLocation();

   @Nullable
   WeighedSoundEvents resolve(SoundManager var1);

   Sound getSound();

   SoundSource getSource();

   boolean isLooping();

   boolean isRelative();

   int getDelay();

   float getVolume();

   float getPitch();

   float getX();

   float getY();

   float getZ();

   SoundInstance.Attenuation getAttenuation();

   default boolean canStartSilent() {
      return false;
   }

   @ClientJarOnly
   public static enum Attenuation {
      NONE,
      LINEAR;
   }
}
