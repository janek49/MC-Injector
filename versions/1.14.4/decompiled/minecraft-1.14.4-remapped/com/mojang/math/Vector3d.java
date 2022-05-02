package com.mojang.math;

import com.fox2code.repacker.ClientJarOnly;

@ClientJarOnly
public class Vector3d {
   public double x;
   public double y;
   public double z;

   public Vector3d(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }
}
