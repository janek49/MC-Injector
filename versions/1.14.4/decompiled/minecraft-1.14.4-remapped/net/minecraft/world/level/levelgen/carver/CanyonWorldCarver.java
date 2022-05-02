package net.minecraft.world.level.levelgen.carver;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.ProbabilityFeatureConfiguration;

public class CanyonWorldCarver extends WorldCarver {
   private final float[] rs = new float[1024];

   public CanyonWorldCarver(Function function) {
      super(function, 256);
   }

   public boolean isStartChunk(Random random, int var2, int var3, ProbabilityFeatureConfiguration probabilityFeatureConfiguration) {
      return random.nextFloat() <= probabilityFeatureConfiguration.probability;
   }

   public boolean carve(ChunkAccess chunkAccess, Random random, int var3, int var4, int var5, int var6, int var7, BitSet bitSet, ProbabilityFeatureConfiguration probabilityFeatureConfiguration) {
      int var10 = (this.getRange() * 2 - 1) * 16;
      double var11 = (double)(var4 * 16 + random.nextInt(16));
      double var13 = (double)(random.nextInt(random.nextInt(40) + 8) + 20);
      double var15 = (double)(var5 * 16 + random.nextInt(16));
      float var17 = random.nextFloat() * 6.2831855F;
      float var18 = (random.nextFloat() - 0.5F) * 2.0F / 8.0F;
      double var19 = 3.0D;
      float var21 = (random.nextFloat() * 2.0F + random.nextFloat()) * 2.0F;
      int var22 = var10 - random.nextInt(var10 / 4);
      int var23 = 0;
      this.genCanyon(chunkAccess, random.nextLong(), var3, var6, var7, var11, var13, var15, var21, var17, var18, 0, var22, 3.0D, bitSet);
      return true;
   }

   private void genCanyon(ChunkAccess chunkAccess, long var2, int var4, int var5, int var6, double var7, double var9, double var11, float var13, float var14, float var15, int var16, int var17, double var18, BitSet bitSet) {
      Random var21 = new Random(var2);
      float var22 = 1.0F;

      for(int var23 = 0; var23 < 256; ++var23) {
         if(var23 == 0 || var21.nextInt(3) == 0) {
            var22 = 1.0F + var21.nextFloat() * var21.nextFloat();
         }

         this.rs[var23] = var22 * var22;
      }

      float var23 = 0.0F;
      float var24 = 0.0F;

      for(int var25 = var16; var25 < var17; ++var25) {
         double var26 = 1.5D + (double)(Mth.sin((float)var25 * 3.1415927F / (float)var17) * var13);
         double var28 = var26 * var18;
         var26 = var26 * ((double)var21.nextFloat() * 0.25D + 0.75D);
         var28 = var28 * ((double)var21.nextFloat() * 0.25D + 0.75D);
         float var30 = Mth.cos(var15);
         float var31 = Mth.sin(var15);
         var7 += (double)(Mth.cos(var14) * var30);
         var9 += (double)var31;
         var11 += (double)(Mth.sin(var14) * var30);
         var15 = var15 * 0.7F;
         var15 = var15 + var24 * 0.05F;
         var14 += var23 * 0.05F;
         var24 = var24 * 0.8F;
         var23 = var23 * 0.5F;
         var24 = var24 + (var21.nextFloat() - var21.nextFloat()) * var21.nextFloat() * 2.0F;
         var23 = var23 + (var21.nextFloat() - var21.nextFloat()) * var21.nextFloat() * 4.0F;
         if(var21.nextInt(4) != 0) {
            if(!this.canReach(var5, var6, var7, var11, var25, var17, var13)) {
               return;
            }

            this.carveSphere(chunkAccess, var2, var4, var5, var6, var7, var9, var11, var26, var28, bitSet);
         }
      }

   }

   protected boolean skip(double var1, double var3, double var5, int var7) {
      return (var1 * var1 + var5 * var5) * (double)this.rs[var7 - 1] + var3 * var3 / 6.0D >= 1.0D;
   }
}
