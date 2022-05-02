package net.minecraft.world.phys;

import net.minecraft.world.phys.Vec3;

public abstract class HitResult {
   protected final Vec3 location;

   protected HitResult(Vec3 location) {
      this.location = location;
   }

   public abstract HitResult.Type getType();

   public Vec3 getLocation() {
      return this.location;
   }

   public static enum Type {
      MISS,
      BLOCK,
      ENTITY;
   }
}
