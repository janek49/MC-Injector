package net.minecraft.client.renderer.block.model;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;

@ClientJarOnly
public class BlockElementRotation {
   public final Vector3f origin;
   public final Direction.Axis axis;
   public final float angle;
   public final boolean rescale;

   public BlockElementRotation(Vector3f origin, Direction.Axis axis, float angle, boolean rescale) {
      this.origin = origin;
      this.axis = axis;
      this.angle = angle;
      this.rescale = rescale;
   }
}
