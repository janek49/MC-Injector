package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.world.phys.Vec3;

@ClientJarOnly
public class Vertex {
   public final Vec3 pos;
   public final float u;
   public final float v;

   public Vertex(float var1, float var2, float var3, float var4, float var5) {
      this(new Vec3((double)var1, (double)var2, (double)var3), var4, var5);
   }

   public Vertex remap(float var1, float var2) {
      return new Vertex(this, var1, var2);
   }

   public Vertex(Vertex vertex, float u, float v) {
      this.pos = vertex.pos;
      this.u = u;
      this.v = v;
   }

   public Vertex(Vec3 pos, float u, float v) {
      this.pos = pos;
      this.u = u;
      this.v = v;
   }
}
