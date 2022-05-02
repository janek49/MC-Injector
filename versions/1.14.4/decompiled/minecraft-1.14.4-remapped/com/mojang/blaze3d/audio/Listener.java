package com.mojang.blaze3d.audio;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.AL10;

@ClientJarOnly
public class Listener {
   public static final Vec3 UP = new Vec3(0.0D, 1.0D, 0.0D);
   private float gain = 1.0F;

   public void setListenerPosition(Vec3 listenerPosition) {
      AL10.alListener3f(4100, (float)listenerPosition.x, (float)listenerPosition.y, (float)listenerPosition.z);
   }

   public void setListenerOrientation(Vec3 var1, Vec3 var2) {
      AL10.alListenerfv(4111, new float[]{(float)var1.x, (float)var1.y, (float)var1.z, (float)var2.x, (float)var2.y, (float)var2.z});
   }

   public void setGain(float gain) {
      AL10.alListenerf(4106, gain);
      this.gain = gain;
   }

   public float getGain() {
      return this.gain;
   }

   public void reset() {
      this.setListenerPosition(Vec3.ZERO);
      this.setListenerOrientation(new Vec3(0.0D, 0.0D, -1.0D), UP);
   }
}
