package com.mojang.blaze3d.audio;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.audio.OpenAlUtil;
import java.nio.ByteBuffer;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import org.lwjgl.openal.AL10;

@ClientJarOnly
public class SoundBuffer {
   @Nullable
   private ByteBuffer data;
   private final AudioFormat format;
   private boolean hasAlBuffer;
   private int alBuffer;

   public SoundBuffer(ByteBuffer data, AudioFormat format) {
      this.data = data;
      this.format = format;
   }

   OptionalInt getAlBuffer() {
      if(!this.hasAlBuffer) {
         if(this.data == null) {
            return OptionalInt.empty();
         }

         int var1 = OpenAlUtil.audioFormatToOpenAl(this.format);
         int[] vars2 = new int[1];
         AL10.alGenBuffers(vars2);
         if(OpenAlUtil.checkALError("Creating buffer")) {
            return OptionalInt.empty();
         }

         AL10.alBufferData(vars2[0], var1, this.data, (int)this.format.getSampleRate());
         if(OpenAlUtil.checkALError("Assigning buffer data")) {
            return OptionalInt.empty();
         }

         this.alBuffer = vars2[0];
         this.hasAlBuffer = true;
         this.data = null;
      }

      return OptionalInt.of(this.alBuffer);
   }

   public void discardAlBuffer() {
      if(this.hasAlBuffer) {
         AL10.alDeleteBuffers(new int[]{this.alBuffer});
         if(OpenAlUtil.checkALError("Deleting stream buffers")) {
            return;
         }
      }

      this.hasAlBuffer = false;
   }

   public OptionalInt releaseAlBuffer() {
      OptionalInt optionalInt = this.getAlBuffer();
      this.hasAlBuffer = false;
      return optionalInt;
   }
}
