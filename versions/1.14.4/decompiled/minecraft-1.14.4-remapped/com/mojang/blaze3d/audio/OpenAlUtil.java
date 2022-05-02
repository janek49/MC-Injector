package com.mojang.blaze3d.audio;

import com.fox2code.repacker.ClientJarOnly;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;

@ClientJarOnly
public class OpenAlUtil {
   private static final Logger LOGGER = LogManager.getLogger();

   private static String alErrorToString(int i) {
      switch(i) {
      case 40961:
         return "Invalid name parameter.";
      case 40962:
         return "Invalid enumerated parameter value.";
      case 40963:
         return "Invalid parameter parameter value.";
      case 40964:
         return "Invalid operation.";
      case 40965:
         return "Unable to allocate memory.";
      default:
         return "An unrecognized error occurred.";
      }
   }

   static boolean checkALError(String string) {
      int var1 = AL10.alGetError();
      if(var1 != 0) {
         LOGGER.error("{}: {}", string, alErrorToString(var1));
         return true;
      } else {
         return false;
      }
   }

   private static String alcErrorToString(int i) {
      switch(i) {
      case 40961:
         return "Invalid device.";
      case 40962:
         return "Invalid context.";
      case 40963:
         return "Illegal enum.";
      case 40964:
         return "Invalid value.";
      case 40965:
         return "Unable to allocate memory.";
      default:
         return "An unrecognized error occurred.";
      }
   }

   static boolean checkALCError(long var0, String string) {
      int var3 = ALC10.alcGetError(var0);
      if(var3 != 0) {
         LOGGER.error("{}{}: {}", string, Long.valueOf(var0), alcErrorToString(var3));
         return true;
      } else {
         return false;
      }
   }

   static int audioFormatToOpenAl(AudioFormat audioFormat) {
      Encoding var1 = audioFormat.getEncoding();
      int var2 = audioFormat.getChannels();
      int var3 = audioFormat.getSampleSizeInBits();
      if(var1.equals(Encoding.PCM_UNSIGNED) || var1.equals(Encoding.PCM_SIGNED)) {
         if(var2 == 1) {
            if(var3 == 8) {
               return 4352;
            }

            if(var3 == 16) {
               return 4353;
            }
         } else if(var2 == 2) {
            if(var3 == 8) {
               return 4354;
            }

            if(var3 == 16) {
               return 4355;
            }
         }
      }

      throw new IllegalArgumentException("Invalid audio format: " + audioFormat);
   }
}
