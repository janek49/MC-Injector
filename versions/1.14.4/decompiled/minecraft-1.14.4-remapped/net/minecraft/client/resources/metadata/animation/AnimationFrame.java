package net.minecraft.client.resources.metadata.animation;

import com.fox2code.repacker.ClientJarOnly;

@ClientJarOnly
public class AnimationFrame {
   private final int index;
   private final int time;

   public AnimationFrame(int i) {
      this(i, -1);
   }

   public AnimationFrame(int index, int time) {
      this.index = index;
      this.time = time;
   }

   public boolean isTimeUnknown() {
      return this.time == -1;
   }

   public int getTime() {
      return this.time;
   }

   public int getIndex() {
      return this.index;
   }
}
