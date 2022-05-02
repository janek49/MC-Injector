package net.minecraft.sounds;

import net.minecraft.resources.ResourceLocation;

public class SoundEvent {
   private final ResourceLocation location;

   public SoundEvent(ResourceLocation location) {
      this.location = location;
   }

   public ResourceLocation getLocation() {
      return this.location;
   }
}
