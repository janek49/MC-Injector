package net.minecraft.client.renderer.culling;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.world.phys.AABB;

@ClientJarOnly
public interface Culler {
   boolean isVisible(AABB var1);

   void prepare(double var1, double var3, double var5);
}
