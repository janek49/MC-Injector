package net.minecraft.util;

import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import net.minecraft.Util;
import net.minecraft.core.Vec3i;
import org.apache.commons.lang3.math.NumberUtils;

public class Mth {
   public static final float SQRT_OF_TWO = sqrt(2.0F);
   private static final float[] SIN = (float[])Util.make(new float[65536], (floats) -> {
      for(int var1 = 0; var1 < floats.length; ++var1) {
         floats[var1] = (float)Math.sin((double)var1 * 3.141592653589793D * 2.0D / 65536.0D);
      }

   });
   private static final Random RANDOM = new Random();
   private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
   private static final double FRAC_BIAS = Double.longBitsToDouble(4805340802404319232L);
   private static final double[] ASIN_TAB = new double[257];
   private static final double[] COS_TAB = new double[257];

   public static float sin(float f) {
      return SIN[(int)(f * 10430.378F) & '\uffff'];
   }

   public static float cos(float f) {
      return SIN[(int)(f * 10430.378F + 16384.0F) & '\uffff'];
   }

   public static float sqrt(float f) {
      return (float)Math.sqrt((double)f);
   }

   public static float sqrt(double d) {
      return (float)Math.sqrt(d);
   }

   public static int floor(float f) {
      int var1 = (int)f;
      return f < (float)var1?var1 - 1:var1;
   }

   public static int fastFloor(double d) {
      return (int)(d + 1024.0D) - 1024;
   }

   public static int floor(double d) {
      int var2 = (int)d;
      return d < (double)var2?var2 - 1:var2;
   }

   public static long lfloor(double d) {
      long var2 = (long)d;
      return d < (double)var2?var2 - 1L:var2;
   }

   public static int absFloor(double d) {
      return (int)(d >= 0.0D?d:-d + 1.0D);
   }

   public static float abs(float f) {
      return Math.abs(f);
   }

   public static int abs(int i) {
      return Math.abs(i);
   }

   public static int ceil(float f) {
      int var1 = (int)f;
      return f > (float)var1?var1 + 1:var1;
   }

   public static int ceil(double d) {
      int var2 = (int)d;
      return d > (double)var2?var2 + 1:var2;
   }

   public static int clamp(int var0, int var1, int var2) {
      return var0 < var1?var1:(var0 > var2?var2:var0);
   }

   public static float clamp(float var0, float var1, float var2) {
      return var0 < var1?var1:(var0 > var2?var2:var0);
   }

   public static double clamp(double x, double x, double x) {
      return x < x?x:(x > x?x:x);
   }

   public static double clampedLerp(double x, double x, double x) {
      return x < 0.0D?x:(x > 1.0D?x:lerp(x, x, x));
   }

   public static double absMax(double var0, double var2) {
      if(var0 < 0.0D) {
         var0 = -var0;
      }

      if(var2 < 0.0D) {
         var2 = -var2;
      }

      return var0 > var2?var0:var2;
   }

   public static int intFloorDiv(int var0, int var1) {
      return Math.floorDiv(var0, var1);
   }

   public static int nextInt(Random random, int var1, int var2) {
      return var1 >= var2?var1:random.nextInt(var2 - var1 + 1) + var1;
   }

   public static float nextFloat(Random random, float var1, float var2) {
      return var1 >= var2?var1:random.nextFloat() * (var2 - var1) + var1;
   }

   public static double nextDouble(Random random, double var1, double var3) {
      return var1 >= var3?var1:random.nextDouble() * (var3 - var1) + var1;
   }

   public static double average(long[] longs) {
      long var1 = 0L;

      for(long var6 : longs) {
         var1 += var6;
      }

      return (double)var1 / (double)longs.length;
   }

   public static boolean equal(float var0, float var1) {
      return Math.abs(var1 - var0) < 1.0E-5F;
   }

   public static boolean equal(double var0, double var2) {
      return Math.abs(var2 - var0) < 9.999999747378752E-6D;
   }

   public static int positiveModulo(int var0, int var1) {
      return Math.floorMod(var0, var1);
   }

   public static float positiveModulo(float var0, float var1) {
      return (var0 % var1 + var1) % var1;
   }

   public static double positiveModulo(double var0, double var2) {
      return (var0 % var2 + var2) % var2;
   }

   public static int wrapDegrees(int i) {
      int var1 = i % 360;
      if(var1 >= 180) {
         var1 -= 360;
      }

      if(var1 < -180) {
         var1 += 360;
      }

      return var1;
   }

   public static float wrapDegrees(float f) {
      float var1 = f % 360.0F;
      if(var1 >= 180.0F) {
         var1 -= 360.0F;
      }

      if(var1 < -180.0F) {
         var1 += 360.0F;
      }

      return var1;
   }

   public static double wrapDegrees(double d) {
      double var2 = d % 360.0D;
      if(var2 >= 180.0D) {
         var2 -= 360.0D;
      }

      if(var2 < -180.0D) {
         var2 += 360.0D;
      }

      return var2;
   }

   public static float degreesDifference(float var0, float var1) {
      return wrapDegrees(var1 - var0);
   }

   public static float degreesDifferenceAbs(float var0, float var1) {
      return abs(degreesDifference(var0, var1));
   }

   public static float rotateIfNecessary(float var0, float var1, float var2) {
      float var3 = degreesDifference(var0, var1);
      float var4 = clamp(var3, -var2, var2);
      return var1 - var4;
   }

   public static float approach(float var0, float var1, float var2) {
      var2 = abs(var2);
      return var0 < var1?clamp(var0 + var2, var0, var1):clamp(var0 - var2, var1, var0);
   }

   public static float approachDegrees(float var0, float var1, float var2) {
      float var3 = degreesDifference(var0, var1);
      return approach(var0, var0 + var3, var2);
   }

   public static int getInt(String string, int var1) {
      return NumberUtils.toInt(string, var1);
   }

   public static int getInt(String string, int var1, int var2) {
      return Math.max(var2, getInt(string, var1));
   }

   public static double getDouble(String string, double var1) {
      try {
         return Double.parseDouble(string);
      } catch (Throwable var4) {
         return var1;
      }
   }

   public static double getDouble(String string, double var1, double var3) {
      return Math.max(var3, getDouble(string, var1));
   }

   public static int smallestEncompassingPowerOfTwo(int i) {
      int var1 = i - 1;
      var1 = var1 | var1 >> 1;
      var1 = var1 | var1 >> 2;
      var1 = var1 | var1 >> 4;
      var1 = var1 | var1 >> 8;
      var1 = var1 | var1 >> 16;
      return var1 + 1;
   }

   private static boolean isPowerOfTwo(int i) {
      return i != 0 && (i & i - 1) == 0;
   }

   public static int ceillog2(int i) {
      i = isPowerOfTwo(i)?i:smallestEncompassingPowerOfTwo(i);
      return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)i * 125613361L >> 27) & 31];
   }

   public static int log2(int i) {
      return ceillog2(i) - (isPowerOfTwo(i)?0:1);
   }

   public static int roundUp(int var0, int var1) {
      if(var1 == 0) {
         return 0;
      } else if(var0 == 0) {
         return var1;
      } else {
         if(var0 < 0) {
            var1 *= -1;
         }

         int var2 = var0 % var1;
         return var2 == 0?var0:var0 + var1 - var2;
      }
   }

   public static int color(float var0, float var1, float var2) {
      return color(floor(var0 * 255.0F), floor(var1 * 255.0F), floor(var2 * 255.0F));
   }

   public static int color(int var0, int var1, int var2) {
      int var3 = (var0 << 8) + var1;
      var3 = (var3 << 8) + var2;
      return var3;
   }

   public static int colorMultiply(int var0, int var1) {
      int var2 = (var0 & 16711680) >> 16;
      int var3 = (var1 & 16711680) >> 16;
      int var4 = (var0 & '\uff00') >> 8;
      int var5 = (var1 & '\uff00') >> 8;
      int var6 = (var0 & 255) >> 0;
      int var7 = (var1 & 255) >> 0;
      int var8 = (int)((float)var2 * (float)var3 / 255.0F);
      int var9 = (int)((float)var4 * (float)var5 / 255.0F);
      int var10 = (int)((float)var6 * (float)var7 / 255.0F);
      return var0 & -16777216 | var8 << 16 | var9 << 8 | var10;
   }

   public static double frac(double d) {
      return d - (double)lfloor(d);
   }

   public static long getSeed(Vec3i vec3i) {
      return getSeed(vec3i.getX(), vec3i.getY(), vec3i.getZ());
   }

   public static long getSeed(int var0, int var1, int var2) {
      long var3 = (long)(var0 * 3129871) ^ (long)var2 * 116129781L ^ (long)var1;
      var3 = var3 * var3 * 42317861L + var3 * 11L;
      return var3 >> 16;
   }

   public static UUID createInsecureUUID(Random random) {
      long var1 = random.nextLong() & -61441L | 16384L;
      long var3 = random.nextLong() & 4611686018427387903L | Long.MIN_VALUE;
      return new UUID(var1, var3);
   }

   public static UUID createInsecureUUID() {
      return createInsecureUUID(RANDOM);
   }

   public static double pct(double x, double x, double x) {
      return (x - x) / (x - x);
   }

   public static double atan2(double var0, double var2) {
      double var4 = var2 * var2 + var0 * var0;
      if(Double.isNaN(var4)) {
         return Double.NaN;
      } else {
         boolean var6 = var0 < 0.0D;
         if(var6) {
            var0 = -var0;
         }

         boolean var7 = var2 < 0.0D;
         if(var7) {
            var2 = -var2;
         }

         boolean var8 = var0 > var2;
         if(var8) {
            double var9 = var2;
            var2 = var0;
            var0 = var9;
         }

         double var9 = fastInvSqrt(var4);
         var2 = var2 * var9;
         var0 = var0 * var9;
         double var11 = FRAC_BIAS + var0;
         int var13 = (int)Double.doubleToRawLongBits(var11);
         double var14 = ASIN_TAB[var13];
         double var16 = COS_TAB[var13];
         double var18 = var11 - FRAC_BIAS;
         double var20 = var0 * var16 - var2 * var18;
         double var22 = (6.0D + var20 * var20) * var20 * 0.16666666666666666D;
         double var24 = var14 + var22;
         if(var8) {
            var24 = 1.5707963267948966D - var24;
         }

         if(var7) {
            var24 = 3.141592653589793D - var24;
         }

         if(var6) {
            var24 = -var24;
         }

         return var24;
      }
   }

   public static double fastInvSqrt(double d) {
      double var2 = 0.5D * d;
      long var4 = Double.doubleToRawLongBits(d);
      var4 = 6910469410427058090L - (var4 >> 1);
      d = Double.longBitsToDouble(var4);
      d = d * (1.5D - var2 * d * d);
      return d;
   }

   public static int hsvToRgb(float var0, float var1, float var2) {
      int var3 = (int)(var0 * 6.0F) % 6;
      float var4 = var0 * 6.0F - (float)var3;
      float var5 = var2 * (1.0F - var1);
      float var6 = var2 * (1.0F - var4 * var1);
      float var7 = var2 * (1.0F - (1.0F - var4) * var1);
      float var8;
      float var9;
      float var10;
      switch(var3) {
      case 0:
         var8 = var2;
         var9 = var7;
         var10 = var5;
         break;
      case 1:
         var8 = var6;
         var9 = var2;
         var10 = var5;
         break;
      case 2:
         var8 = var5;
         var9 = var2;
         var10 = var7;
         break;
      case 3:
         var8 = var5;
         var9 = var6;
         var10 = var2;
         break;
      case 4:
         var8 = var7;
         var9 = var5;
         var10 = var2;
         break;
      case 5:
         var8 = var2;
         var9 = var5;
         var10 = var6;
         break;
      default:
         throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + var0 + ", " + var1 + ", " + var2);
      }

      int var11 = clamp((int)(var8 * 255.0F), 0, 255);
      int var12 = clamp((int)(var9 * 255.0F), 0, 255);
      int var13 = clamp((int)(var10 * 255.0F), 0, 255);
      return var11 << 16 | var12 << 8 | var13;
   }

   public static int murmurHash3Mixer(int i) {
      i = i ^ i >>> 16;
      i = i * -2048144789;
      i = i ^ i >>> 13;
      i = i * -1028477387;
      i = i ^ i >>> 16;
      return i;
   }

   public static int binarySearch(int var0, int var1, IntPredicate intPredicate) {
      int var3 = var1 - var0;

      while(var3 > 0) {
         int var4 = var3 / 2;
         int var5 = var0 + var4;
         if(intPredicate.test(var5)) {
            var3 = var4;
         } else {
            var0 = var5 + 1;
            var3 -= var4 + 1;
         }
      }

      return var0;
   }

   public static float lerp(float var0, float var1, float var2) {
      return var1 + var0 * (var2 - var1);
   }

   public static double lerp(double x, double x, double x) {
      return x + x * (x - x);
   }

   public static double lerp2(double var0, double var2, double var4, double var6, double var8, double var10) {
      return lerp(var2, lerp(var0, var4, var6), lerp(var0, var8, var10));
   }

   public static double lerp3(double var0, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20) {
      return lerp(var4, lerp2(var0, var2, var6, var8, var10, var12), lerp2(var0, var2, var14, var16, var18, var20));
   }

   public static double smoothstep(double d) {
      return d * d * d * (d * (d * 6.0D - 15.0D) + 10.0D);
   }

   public static int sign(double d) {
      return d == 0.0D?0:(d > 0.0D?1:-1);
   }

   public static float rotLerp(float var0, float var1, float var2) {
      return var1 + var0 * wrapDegrees(var2 - var1);
   }

   static {
      for(int var0 = 0; var0 < 257; ++var0) {
         double var1 = (double)var0 / 256.0D;
         double var3 = Math.asin(var1);
         COS_TAB[var0] = Math.cos(var3);
         ASIN_TAB[var0] = var3;
      }

   }
}
