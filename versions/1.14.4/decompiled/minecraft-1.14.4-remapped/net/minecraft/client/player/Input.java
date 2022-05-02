package net.minecraft.client.player;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.world.phys.Vec2;

@ClientJarOnly
public class Input {
   public float leftImpulse;
   public float forwardImpulse;
   public boolean up;
   public boolean down;
   public boolean left;
   public boolean right;
   public boolean jumping;
   public boolean sneakKeyDown;

   public void tick(boolean var1, boolean var2) {
   }

   public Vec2 getMoveVector() {
      return new Vec2(this.leftImpulse, this.forwardImpulse);
   }

   public boolean hasForwardImpulse() {
      return this.forwardImpulse > 1.0E-5F;
   }
}
