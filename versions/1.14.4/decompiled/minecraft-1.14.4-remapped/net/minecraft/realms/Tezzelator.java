package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.realms.RealmsBufferBuilder;
import net.minecraft.realms.RealmsVertexFormat;

@ClientJarOnly
public class Tezzelator {
   public static final Tesselator t = Tesselator.getInstance();
   public static final Tezzelator instance = new Tezzelator();

   public void end() {
      t.end();
   }

   public Tezzelator vertex(double var1, double var3, double var5) {
      t.getBuilder().vertex(var1, var3, var5);
      return this;
   }

   public void color(float var1, float var2, float var3, float var4) {
      t.getBuilder().color(var1, var2, var3, var4);
   }

   public void tex2(short var1, short var2) {
      t.getBuilder().uv2(var1, var2);
   }

   public void normal(float var1, float var2, float var3) {
      t.getBuilder().normal(var1, var2, var3);
   }

   public void begin(int var1, RealmsVertexFormat realmsVertexFormat) {
      t.getBuilder().begin(var1, realmsVertexFormat.getVertexFormat());
   }

   public void endVertex() {
      t.getBuilder().endVertex();
   }

   public void offset(double var1, double var3, double var5) {
      t.getBuilder().offset(var1, var3, var5);
   }

   public RealmsBufferBuilder color(int var1, int var2, int var3, int var4) {
      return new RealmsBufferBuilder(t.getBuilder().color(var1, var2, var3, var4));
   }

   public Tezzelator tex(double var1, double var3) {
      t.getBuilder().uv(var1, var3);
      return this;
   }
}
