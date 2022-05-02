package net.minecraft.world.level.levelgen.carver;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.ProbabilityFeatureConfiguration;

public class CaveWorldCarver extends WorldCarver {
   public CaveWorldCarver(Function function, int var2) {
      super(function, var2);
   }

   public boolean isStartChunk(Random random, int var2, int var3, ProbabilityFeatureConfiguration probabilityFeatureConfiguration) {
      return random.nextFloat() <= probabilityFeatureConfiguration.probability;
   }

   public boolean carve(ChunkAccess chunkAccess, Random random, int var3, int var4, int var5, int var6, int var7, BitSet bitSet, ProbabilityFeatureConfiguration probabilityFeatureConfiguration) {
      int var10 = (this.getRange() * 2 - 1) * 16;
      int var11 = random.nextInt(random.nextInt(random.nextInt(this.getCaveBound()) + 1) + 1);

      for(int var12 = 0; var12 < var11; ++var12) {
         double var13 = (double)(var4 * 16 + random.nextInt(16));
         double var15 = (double)this.getCaveY(random);
         double var17 = (double)(var5 * 16 + random.nextInt(16));
         int var19 = 1;
         if(random.nextInt(4) == 0) {
            double var20 = 0.5D;
            float var22 = 1.0F + random.nextFloat() * 6.0F;
            this.genRoom(chunkAccess, random.nextLong(), var3, var6, var7, var13, var15, var17, var22, 0.5D, bitSet);
            var19 += random.nextInt(4);
         }

         for(int var20 = 0; var20 < var19; ++var20) {
            float var21 = random.nextFloat() * 6.2831855F;
            float var22 = (random.nextFloat() - 0.5F) / 4.0F;
            float var23 = this.getThickness(random);
            int var24 = var10 - random.nextInt(var10 / 4);
            int var25 = 0;
            this.genTunnel(chunkAccess, random.nextLong(), var3, var6, var7, var13, var15, var17, var23, var21, var22, 0, var24, this.getYScale(), bitSet);
         }
      }

      return true;
   }

   protected int getCaveBound() {
      return 15;
   }

   protected float getThickness(Random random) {
      float var2 = random.nextFloat() * 2.0F + random.nextFloat();
      if(random.nextInt(10) == 0) {
         var2 *= random.nextFloat() * random.nextFloat() * 3.0F + 1.0F;
      }

      return var2;
   }

   protected double getYScale() {
      return 1.0D;
   }

   protected int getCaveY(Random random) {
      return random.nextInt(random.nextInt(120) + 8);
   }

   protected void genRoom(ChunkAccess chunkAccess, long var2, int var4, int var5, int var6, double var7, double var9, double var11, float var13, double var14, BitSet bitSet) {
      double var17 = 1.5D + (double)(Mth.sin(1.5707964F) * var13);
      double var19 = var17 * var14;
      this.carveSphere(chunkAccess, var2, var4, var5, var6, var7 + 1.0D, var9, var11, var17, var19, bitSet);
   }

   protected void genTunnel(ChunkAccess chunkAccess, long var2, int var4, int var5, int var6, double var7, double var9, double var11, float var13, float var14, float var15, int var16, int var17, double var18, BitSet bitSet) {
      Random var21 = new Random(var2);
      int var22 = var21.nextInt(var17 / 2) + var17 / 4;
      boolean var23 = var21.nextInt(6) == 0;
      float var24 = 0.0F;
      float var25 = 0.0F;

      for(int var26 = var16; var26 < var17; ++var26) {
         double var27 = 1.5D + (double)(Mth.sin(3.1415927F * (float)var26 / (float)var17) * var13);
         double var29 = var27 * var18;
         float var31 = Mth.cos(var15);
         var7 += (double)(Mth.cos(var14) * var31);
         var9 += (double)Mth.sin(var15);
         var11 += (double)(Mth.sin(var14) * var31);
         var15 = var15 * (var23?0.92F:0.7F);
         var15 = var15 + var25 * 0.1F;
         var14 += var24 * 0.1F;
         var25 = var25 * 0.9F;
         var24 = var24 * 0.75F;
         var25 = var25 + (var21.nextFloat() - var21.nextFloat()) * var21.nextFloat() * 2.0F;
         var24 = var24 + (var21.nextFloat() - var21.nextFloat()) * var21.nextFloat() * 4.0F;
         if(var26 == var22 && var13 > 1.0F) {
            this.genTunnel(chunkAccess, var21.nextLong(), var4, var5, var6, var7, var9, var11, var21.nextFloat() * 0.5F + 0.5F, var14 - 1.5707964F, var15 / 3.0F, var26, var17, 1.0D, bitSet);
            this.genTunnel(chunkAccess, var21.nextLong(), var4, var5, var6, var7, var9, var11, var21.nextFloat() * 0.5F + 0.5F, var14 + 1.5707964F, var15 / 3.0F, var26, var17, 1.0D, bitSet);
            return;
         }

         if(var21.nextInt(4) != 0) {
            if(!this.canReach(var5, var6, var7, var11, var26, var17, var13)) {
               return;
            }

            this.carveSphere(chunkAccess, var2, var4, var5, var6, var7, var9, var11, var27, var29, bitSet);
         }
      }

   }

   protected boolean skip(double var1, double var3, double var5, int var7) {
      return var3 <= -0.7D || var1 * var1 + var3 * var3 + var5 * var5 >= 1.0D;
   }
}
