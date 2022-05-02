package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;

public class DustParticleOptions implements ParticleOptions {
   public static final DustParticleOptions REDSTONE = new DustParticleOptions(1.0F, 0.0F, 0.0F, 1.0F);
   public static final ParticleOptions.Deserializer DESERIALIZER = new ParticleOptions.Deserializer() {
      public DustParticleOptions fromCommand(ParticleType particleType, StringReader stringReader) throws CommandSyntaxException {
         stringReader.expect(' ');
         float var3 = (float)stringReader.readDouble();
         stringReader.expect(' ');
         float var4 = (float)stringReader.readDouble();
         stringReader.expect(' ');
         float var5 = (float)stringReader.readDouble();
         stringReader.expect(' ');
         float var6 = (float)stringReader.readDouble();
         return new DustParticleOptions(var3, var4, var5, var6);
      }

      public DustParticleOptions fromNetwork(ParticleType particleType, FriendlyByteBuf friendlyByteBuf) {
         return new DustParticleOptions(friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat());
      }

      // $FF: synthetic method
      public ParticleOptions fromNetwork(ParticleType var1, FriendlyByteBuf var2) {
         return this.fromNetwork(var1, var2);
      }

      // $FF: synthetic method
      public ParticleOptions fromCommand(ParticleType var1, StringReader var2) throws CommandSyntaxException {
         return this.fromCommand(var1, var2);
      }
   };
   private final float r;
   private final float g;
   private final float b;
   private final float scale;

   public DustParticleOptions(float r, float g, float b, float var4) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.scale = Mth.clamp(var4, 0.01F, 4.0F);
   }

   public void writeToNetwork(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.writeFloat(this.r);
      friendlyByteBuf.writeFloat(this.g);
      friendlyByteBuf.writeFloat(this.b);
      friendlyByteBuf.writeFloat(this.scale);
   }

   public String writeToString() {
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", new Object[]{Registry.PARTICLE_TYPE.getKey(this.getType()), Float.valueOf(this.r), Float.valueOf(this.g), Float.valueOf(this.b), Float.valueOf(this.scale)});
   }

   public ParticleType getType() {
      return ParticleTypes.DUST;
   }

   public float getR() {
      return this.r;
   }

   public float getG() {
      return this.g;
   }

   public float getB() {
      return this.b;
   }

   public float getScale() {
      return this.scale;
   }
}
