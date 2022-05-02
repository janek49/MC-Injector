package net.minecraft.client.resources.sounds;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.resources.ResourceLocation;

@ClientJarOnly
public class Sound implements Weighted {
   private final ResourceLocation location;
   private final float volume;
   private final float pitch;
   private final int weight;
   private final Sound.Type type;
   private final boolean stream;
   private final boolean preload;
   private final int attenuationDistance;

   public Sound(String string, float volume, float pitch, int weight, Sound.Type type, boolean stream, boolean preload, int attenuationDistance) {
      this.location = new ResourceLocation(string);
      this.volume = volume;
      this.pitch = pitch;
      this.weight = weight;
      this.type = type;
      this.stream = stream;
      this.preload = preload;
      this.attenuationDistance = attenuationDistance;
   }

   public ResourceLocation getLocation() {
      return this.location;
   }

   public ResourceLocation getPath() {
      return new ResourceLocation(this.location.getNamespace(), "sounds/" + this.location.getPath() + ".ogg");
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public int getWeight() {
      return this.weight;
   }

   public Sound getSound() {
      return this;
   }

   public void preloadIfRequired(SoundEngine soundEngine) {
      if(this.preload) {
         soundEngine.requestPreload(this);
      }

   }

   public Sound.Type getType() {
      return this.type;
   }

   public boolean shouldStream() {
      return this.stream;
   }

   public boolean shouldPreload() {
      return this.preload;
   }

   public int getAttenuationDistance() {
      return this.attenuationDistance;
   }

   // $FF: synthetic method
   public Object getSound() {
      return this.getSound();
   }

   @ClientJarOnly
   public static enum Type {
      FILE("file"),
      SOUND_EVENT("event");

      private final String name;

      private Type(String name) {
         this.name = name;
      }

      public static Sound.Type getByName(String name) {
         for(Sound.Type var4 : values()) {
            if(var4.name.equals(name)) {
               return var4;
            }
         }

         return null;
      }
   }
}
