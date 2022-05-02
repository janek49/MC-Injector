package net.minecraft.world.entity.schedule;

public class Keyframe {
   private final int timeStamp;
   private final float value;

   public Keyframe(int timeStamp, float value) {
      this.timeStamp = timeStamp;
      this.value = value;
   }

   public int getTimeStamp() {
      return this.timeStamp;
   }

   public float getValue() {
      return this.value;
   }
}
