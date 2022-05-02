package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;

@ClientJarOnly
public class Timer {
   public int ticks;
   public float partialTick;
   public float tickDelta;
   private long lastMs;
   private final float msPerTick;

   public Timer(float var1, long lastMs) {
      this.msPerTick = 1000.0F / var1;
      this.lastMs = lastMs;
   }

   public void advanceTime(long lastMs) {
      this.tickDelta = (float)(lastMs - this.lastMs) / this.msPerTick;
      this.lastMs = lastMs;
      this.partialTick += this.tickDelta;
      this.ticks = (int)this.partialTick;
      this.partialTick -= (float)this.ticks;
   }
}
