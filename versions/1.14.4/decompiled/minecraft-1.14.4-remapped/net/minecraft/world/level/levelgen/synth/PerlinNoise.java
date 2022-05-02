package net.minecraft.world.level.levelgen.synth;

import java.util.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.levelgen.synth.SurfaceNoise;

public class PerlinNoise implements SurfaceNoise {
   private final ImprovedNoise[] noiseLevels;

   public PerlinNoise(Random random, int noiseLevels) {
      this.noiseLevels = new ImprovedNoise[noiseLevels];

      for(int var3 = 0; var3 < noiseLevels; ++var3) {
         this.noiseLevels[var3] = new ImprovedNoise(random);
      }

   }

   public double getValue(double var1, double var3, double var5) {
      return this.getValue(var1, var3, var5, 0.0D, 0.0D, false);
   }

   public double getValue(double var1, double var3, double var5, double var7, double var9, boolean var11) {
      double var12 = 0.0D;
      double var14 = 1.0D;

      for(ImprovedNoise var19 : this.noiseLevels) {
         var12 += var19.noise(wrap(var1 * var14), var11?-var19.yo:wrap(var3 * var14), wrap(var5 * var14), var7 * var14, var9 * var14) / var14;
         var14 /= 2.0D;
      }

      return var12;
   }

   public ImprovedNoise getOctaveNoise(int i) {
      return this.noiseLevels[i];
   }

   public static double wrap(double d) {
      return d - (double)Mth.lfloor(d / 3.3554432E7D + 0.5D) * 3.3554432E7D;
   }

   public double getSurfaceNoiseValue(double var1, double var3, double var5, double var7) {
      return this.getValue(var1, var3, 0.0D, var5, var7, false);
   }
}
