package net.minecraft.client.sounds;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

@ClientJarOnly
public class MusicManager {
   private final Random random = new Random();
   private final Minecraft minecraft;
   private SoundInstance currentMusic;
   private int nextSongDelay = 100;

   public MusicManager(Minecraft minecraft) {
      this.minecraft = minecraft;
   }

   public void tick() {
      MusicManager.Music var1 = this.minecraft.getSituationalMusic();
      if(this.currentMusic != null) {
         if(!var1.getEvent().getLocation().equals(this.currentMusic.getLocation())) {
            this.minecraft.getSoundManager().stop(this.currentMusic);
            this.nextSongDelay = Mth.nextInt(this.random, 0, var1.getMinDelay() / 2);
         }

         if(!this.minecraft.getSoundManager().isActive(this.currentMusic)) {
            this.currentMusic = null;
            this.nextSongDelay = Math.min(Mth.nextInt(this.random, var1.getMinDelay(), var1.getMaxDelay()), this.nextSongDelay);
         }
      }

      this.nextSongDelay = Math.min(this.nextSongDelay, var1.getMaxDelay());
      if(this.currentMusic == null && this.nextSongDelay-- <= 0) {
         this.startPlaying(var1);
      }

   }

   public void startPlaying(MusicManager.Music musicManager$Music) {
      this.currentMusic = SimpleSoundInstance.forMusic(musicManager$Music.getEvent());
      this.minecraft.getSoundManager().play(this.currentMusic);
      this.nextSongDelay = Integer.MAX_VALUE;
   }

   public void stopPlaying() {
      if(this.currentMusic != null) {
         this.minecraft.getSoundManager().stop(this.currentMusic);
         this.currentMusic = null;
         this.nextSongDelay = 0;
      }

   }

   public boolean isPlayingMusic(MusicManager.Music musicManager$Music) {
      return this.currentMusic == null?false:musicManager$Music.getEvent().getLocation().equals(this.currentMusic.getLocation());
   }

   @ClientJarOnly
   public static enum Music {
      MENU(SoundEvents.MUSIC_MENU, 20, 600),
      GAME(SoundEvents.MUSIC_GAME, 12000, 24000),
      CREATIVE(SoundEvents.MUSIC_CREATIVE, 1200, 3600),
      CREDITS(SoundEvents.MUSIC_CREDITS, 0, 0),
      NETHER(SoundEvents.MUSIC_NETHER, 1200, 3600),
      END_BOSS(SoundEvents.MUSIC_DRAGON, 0, 0),
      END(SoundEvents.MUSIC_END, 6000, 24000),
      UNDER_WATER(SoundEvents.MUSIC_UNDER_WATER, 12000, 24000);

      private final SoundEvent event;
      private final int minDelay;
      private final int maxDelay;

      private Music(SoundEvent event, int minDelay, int maxDelay) {
         this.event = event;
         this.minDelay = minDelay;
         this.maxDelay = maxDelay;
      }

      public SoundEvent getEvent() {
         return this.event;
      }

      public int getMinDelay() {
         return this.minDelay;
      }

      public int getMaxDelay() {
         return this.maxDelay;
      }
   }
}
