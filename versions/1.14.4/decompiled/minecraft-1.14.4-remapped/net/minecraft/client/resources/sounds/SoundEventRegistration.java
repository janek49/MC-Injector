package net.minecraft.client.resources.sounds;

import com.fox2code.repacker.ClientJarOnly;
import java.util.List;
import javax.annotation.Nullable;

@ClientJarOnly
public class SoundEventRegistration {
   private final List sounds;
   private final boolean replace;
   private final String subtitle;

   public SoundEventRegistration(List sounds, boolean replace, String subtitle) {
      this.sounds = sounds;
      this.replace = replace;
      this.subtitle = subtitle;
   }

   public List getSounds() {
      return this.sounds;
   }

   public boolean isReplace() {
      return this.replace;
   }

   @Nullable
   public String getSubtitle() {
      return this.subtitle;
   }
}
