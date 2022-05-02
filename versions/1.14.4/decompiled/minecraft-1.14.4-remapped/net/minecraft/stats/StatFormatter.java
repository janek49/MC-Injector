package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.Util;

public interface StatFormatter {
   DecimalFormat DECIMAL_FORMAT = (DecimalFormat)Util.make(new DecimalFormat("########0.00"), (decimalFormat) -> {
      decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
   });
   StatFormatter DEFAULT;
   StatFormatter DIVIDE_BY_TEN = (i) -> {
      return DECIMAL_FORMAT.format((double)i * 0.1D);
   };
   StatFormatter DISTANCE = (i) -> {
      double var1 = (double)i / 100.0D;
      double var3 = var1 / 1000.0D;
      return var3 > 0.5D?DECIMAL_FORMAT.format(var3) + " km":(var1 > 0.5D?DECIMAL_FORMAT.format(var1) + " m":i + " cm");
   };
   StatFormatter TIME = (i) -> {
      double var1 = (double)i / 20.0D;
      double var3 = var1 / 60.0D;
      double var5 = var3 / 60.0D;
      double var7 = var5 / 24.0D;
      double var9 = var7 / 365.0D;
      return var9 > 0.5D?DECIMAL_FORMAT.format(var9) + " y":(var7 > 0.5D?DECIMAL_FORMAT.format(var7) + " d":(var5 > 0.5D?DECIMAL_FORMAT.format(var5) + " h":(var3 > 0.5D?DECIMAL_FORMAT.format(var3) + " m":var1 + " s")));
   };

   String format(int var1);

   static default {
      NumberFormat var10000 = NumberFormat.getIntegerInstance(Locale.US);
      DEFAULT = var10000::format;
   }
}
