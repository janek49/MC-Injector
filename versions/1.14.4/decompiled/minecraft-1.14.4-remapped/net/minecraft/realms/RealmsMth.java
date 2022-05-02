package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Random;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.StringUtils;

@ClientJarOnly
public class RealmsMth {
   public static float sin(float f) {
      return Mth.sin(f);
   }

   public static double nextDouble(Random random, double var1, double var3) {
      return Mth.nextDouble(random, var1, var3);
   }

   public static int ceil(float f) {
      return Mth.ceil(f);
   }

   public static int floor(double d) {
      return Mth.floor(d);
   }

   public static int intFloorDiv(int var0, int var1) {
      return Mth.intFloorDiv(var0, var1);
   }

   public static float abs(float f) {
      return Mth.abs(f);
   }

   public static int clamp(int var0, int var1, int var2) {
      return Mth.clamp(var0, var1, var2);
   }

   public static double clampedLerp(double x, double x, double x) {
      return Mth.clampedLerp(x, x, x);
   }

   public static int ceil(double d) {
      return Mth.ceil(d);
   }

   public static boolean isEmpty(String string) {
      return StringUtils.isEmpty(string);
   }

   public static long lfloor(double d) {
      return Mth.lfloor(d);
   }

   public static float sqrt(double d) {
      return Mth.sqrt(d);
   }

   public static double clamp(double x, double x, double x) {
      return Mth.clamp(x, x, x);
   }

   public static int getInt(String string, int var1) {
      return Mth.getInt(string, var1);
   }

   public static double getDouble(String string, double var1) {
      return Mth.getDouble(string, var1);
   }

   public static int log2(int i) {
      return Mth.log2(i);
   }

   public static int absFloor(double d) {
      return Mth.absFloor(d);
   }

   public static int smallestEncompassingPowerOfTwo(int i) {
      return Mth.smallestEncompassingPowerOfTwo(i);
   }

   public static float sqrt(float f) {
      return Mth.sqrt(f);
   }

   public static float cos(float f) {
      return Mth.cos(f);
   }

   public static int getInt(String string, int var1, int var2) {
      return Mth.getInt(string, var1, var2);
   }

   public static int fastFloor(double d) {
      return Mth.fastFloor(d);
   }

   public static double absMax(double var0, double var2) {
      return Mth.absMax(var0, var2);
   }

   public static float nextFloat(Random random, float var1, float var2) {
      return Mth.nextFloat(random, var1, var2);
   }

   public static double wrapDegrees(double d) {
      return Mth.wrapDegrees(d);
   }

   public static float wrapDegrees(float f) {
      return Mth.wrapDegrees(f);
   }

   public static float clamp(float var0, float var1, float var2) {
      return Mth.clamp(var0, var1, var2);
   }

   public static double getDouble(String string, double var1, double var3) {
      return Mth.getDouble(string, var1, var3);
   }

   public static int roundUp(int var0, int var1) {
      return Mth.roundUp(var0, var1);
   }

   public static double average(long[] longs) {
      return Mth.average(longs);
   }

   public static int floor(float f) {
      return Mth.floor(f);
   }

   public static int abs(int i) {
      return Mth.abs(i);
   }

   public static int nextInt(Random random, int var1, int var2) {
      return Mth.nextInt(random, var1, var2);
   }
}
