package com.mojang.blaze3d.audio;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.audio.OpenAlUtil;
import com.mojang.blaze3d.audio.SoundBuffer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntConsumer;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL10;

@ClientJarOnly
public class Channel {
   private static final Logger LOGGER = LogManager.getLogger();
   private final int source;
   private AtomicBoolean initialized = new AtomicBoolean(true);
   private int streamingBufferSize = 16384;
   @Nullable
   private AudioStream stream;

   @Nullable
   static Channel create() {
      int[] vars0 = new int[1];
      AL10.alGenSources(vars0);
      return OpenAlUtil.checkALError("Allocate new source")?null:new Channel(vars0[0]);
   }

   private Channel(int source) {
      this.source = source;
   }

   public void destroy() {
      if(this.initialized.compareAndSet(true, false)) {
         AL10.alSourceStop(this.source);
         OpenAlUtil.checkALError("Stop");
         if(this.stream != null) {
            try {
               this.stream.close();
            } catch (IOException var2) {
               LOGGER.error("Failed to close audio stream", var2);
            }

            this.removeProcessedBuffers();
            this.stream = null;
         }

         AL10.alDeleteSources(new int[]{this.source});
         OpenAlUtil.checkALError("Cleanup");
      }

   }

   public void play() {
      AL10.alSourcePlay(this.source);
   }

   private int getState() {
      return !this.initialized.get()?4116:AL10.alGetSourcei(this.source, 4112);
   }

   public void pause() {
      if(this.getState() == 4114) {
         AL10.alSourcePause(this.source);
      }

   }

   public void unpause() {
      if(this.getState() == 4115) {
         AL10.alSourcePlay(this.source);
      }

   }

   public void stop() {
      if(this.initialized.get()) {
         AL10.alSourceStop(this.source);
         OpenAlUtil.checkALError("Stop");
      }

   }

   public boolean stopped() {
      return this.getState() == 4116;
   }

   public void setSelfPosition(Vec3 selfPosition) {
      AL10.alSourcefv(this.source, 4100, new float[]{(float)selfPosition.x, (float)selfPosition.y, (float)selfPosition.z});
   }

   public void setPitch(float pitch) {
      AL10.alSourcef(this.source, 4099, pitch);
   }

   public void setLooping(boolean looping) {
      AL10.alSourcei(this.source, 4103, looping?1:0);
   }

   public void setVolume(float volume) {
      AL10.alSourcef(this.source, 4106, volume);
   }

   public void disableAttenuation() {
      AL10.alSourcei(this.source, '퀀', 0);
   }

   public void linearAttenuation(float f) {
      AL10.alSourcei(this.source, '퀀', '퀃');
      AL10.alSourcef(this.source, 4131, f);
      AL10.alSourcef(this.source, 4129, 1.0F);
      AL10.alSourcef(this.source, 4128, 0.0F);
   }

   public void setRelative(boolean relative) {
      AL10.alSourcei(this.source, 514, relative?1:0);
   }

   public void attachStaticBuffer(SoundBuffer soundBuffer) {
      soundBuffer.getAlBuffer().ifPresent((i) -> {
         AL10.alSourcei(this.source, 4105, i);
      });
   }

   public void attachBufferStream(AudioStream stream) {
      this.stream = stream;
      AudioFormat var2 = stream.getFormat();
      this.streamingBufferSize = calculateBufferSize(var2, 1);
      this.pumpBuffers(4);
   }

   private static int calculateBufferSize(AudioFormat audioFormat, int var1) {
      return (int)((float)(var1 * audioFormat.getSampleSizeInBits()) / 8.0F * (float)audioFormat.getChannels() * audioFormat.getSampleRate());
   }

   private void pumpBuffers(int i) {
      if(this.stream != null) {
         try {
            for(int var2 = 0; var2 < i; ++var2) {
               ByteBuffer var3 = this.stream.read(this.streamingBufferSize);
               if(var3 != null) {
                  (new SoundBuffer(var3, this.stream.getFormat())).releaseAlBuffer().ifPresent((i) -> {
                     AL10.alSourceQueueBuffers(this.source, new int[]{i});
                  });
               }
            }
         } catch (IOException var4) {
            LOGGER.error("Failed to read from audio stream", var4);
         }
      }

   }

   public void updateStream() {
      if(this.stream != null) {
         int var1 = this.removeProcessedBuffers();
         this.pumpBuffers(var1);
      }

   }

   private int removeProcessedBuffers() {
      int var1 = AL10.alGetSourcei(this.source, 4118);
      if(var1 > 0) {
         int[] vars2 = new int[var1];
         AL10.alSourceUnqueueBuffers(this.source, vars2);
         OpenAlUtil.checkALError("Unqueue buffers");
         AL10.alDeleteBuffers(vars2);
         OpenAlUtil.checkALError("Remove processed buffers");
      }

      return var1;
   }
}
