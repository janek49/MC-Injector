package net.minecraft.client.sounds;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

@ClientJarOnly
public class WeighedSoundEvents implements Weighted {
   private final List list = Lists.newArrayList();
   private final Random random = new Random();
   private final ResourceLocation location;
   private final Component subtitle;

   public WeighedSoundEvents(ResourceLocation location, @Nullable String string) {
      this.location = location;
      this.subtitle = string == null?null:new TranslatableComponent(string, new Object[0]);
   }

   public int getWeight() {
      int var1 = 0;

      for(Weighted<Sound> var3 : this.list) {
         var1 += var3.getWeight();
      }

      return var1;
   }

   public Sound getSound() {
      int var1 = this.getWeight();
      if(!this.list.isEmpty() && var1 != 0) {
         int var2 = this.random.nextInt(var1);

         for(Weighted<Sound> var4 : this.list) {
            var2 -= var4.getWeight();
            if(var2 < 0) {
               return (Sound)var4.getSound();
            }
         }

         return SoundManager.EMPTY_SOUND;
      } else {
         return SoundManager.EMPTY_SOUND;
      }
   }

   public void addSound(Weighted weighted) {
      this.list.add(weighted);
   }

   @Nullable
   public Component getSubtitle() {
      return this.subtitle;
   }

   public void preloadIfRequired(SoundEngine soundEngine) {
      for(Weighted<Sound> var3 : this.list) {
         var3.preloadIfRequired(soundEngine);
      }

   }

   // $FF: synthetic method
   public Object getSound() {
      return this.getSound();
   }
}
