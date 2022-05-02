package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportDetail;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CrashReportCategory {
   private final CrashReport report;
   private final String title;
   private final List entries = Lists.newArrayList();
   private StackTraceElement[] stackTrace = new StackTraceElement[0];

   public CrashReportCategory(CrashReport report, String title) {
      this.report = report;
      this.title = title;
   }

   public static String formatLocation(double x, double x, double x) {
      return String.format(Locale.ROOT, "%.2f,%.2f,%.2f - %s", new Object[]{Double.valueOf(x), Double.valueOf(x), Double.valueOf(x), formatLocation(new BlockPos(x, x, x))});
   }

   public static String formatLocation(BlockPos blockPos) {
      return formatLocation(blockPos.getX(), blockPos.getY(), blockPos.getZ());
   }

   public static String formatLocation(int var0, int var1, int var2) {
      StringBuilder var3 = new StringBuilder();

      try {
         var3.append(String.format("World: (%d,%d,%d)", new Object[]{Integer.valueOf(var0), Integer.valueOf(var1), Integer.valueOf(var2)}));
      } catch (Throwable var16) {
         var3.append("(Error finding world loc)");
      }

      var3.append(", ");

      try {
         int var4 = var0 >> 4;
         int var5 = var2 >> 4;
         int var6 = var0 & 15;
         int var7 = var1 >> 4;
         int var8 = var2 & 15;
         int var9 = var4 << 4;
         int var10 = var5 << 4;
         int var11 = (var4 + 1 << 4) - 1;
         int var12 = (var5 + 1 << 4) - 1;
         var3.append(String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", new Object[]{Integer.valueOf(var6), Integer.valueOf(var7), Integer.valueOf(var8), Integer.valueOf(var4), Integer.valueOf(var5), Integer.valueOf(var9), Integer.valueOf(var10), Integer.valueOf(var11), Integer.valueOf(var12)}));
      } catch (Throwable var15) {
         var3.append("(Error finding chunk loc)");
      }

      var3.append(", ");

      try {
         int var4 = var0 >> 9;
         int var5 = var2 >> 9;
         int var6 = var4 << 5;
         int var7 = var5 << 5;
         int var8 = (var4 + 1 << 5) - 1;
         int var9 = (var5 + 1 << 5) - 1;
         int var10 = var4 << 9;
         int var11 = var5 << 9;
         int var12 = (var4 + 1 << 9) - 1;
         int var13 = (var5 + 1 << 9) - 1;
         var3.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)", new Object[]{Integer.valueOf(var4), Integer.valueOf(var5), Integer.valueOf(var6), Integer.valueOf(var7), Integer.valueOf(var8), Integer.valueOf(var9), Integer.valueOf(var10), Integer.valueOf(var11), Integer.valueOf(var12), Integer.valueOf(var13)}));
      } catch (Throwable var14) {
         var3.append("(Error finding world loc)");
      }

      return var3.toString();
   }

   public CrashReportCategory setDetail(String string, CrashReportDetail crashReportDetail) {
      try {
         this.setDetail(string, crashReportDetail.call());
      } catch (Throwable var4) {
         this.setDetailError(string, var4);
      }

      return this;
   }

   public CrashReportCategory setDetail(String string, Object object) {
      this.entries.add(new CrashReportCategory.Entry(string, object));
      return this;
   }

   public void setDetailError(String string, Throwable throwable) {
      this.setDetail(string, (Object)throwable);
   }

   public int fillInStackTrace(int i) {
      StackTraceElement[] vars2 = Thread.currentThread().getStackTrace();
      if(vars2.length <= 0) {
         return 0;
      } else {
         this.stackTrace = new StackTraceElement[vars2.length - 3 - i];
         System.arraycopy(vars2, 3 + i, this.stackTrace, 0, this.stackTrace.length);
         return this.stackTrace.length;
      }
   }

   public boolean validateStackTrace(StackTraceElement var1, StackTraceElement var2) {
      if(this.stackTrace.length != 0 && var1 != null) {
         StackTraceElement var3 = this.stackTrace[0];
         if(var3.isNativeMethod() == var1.isNativeMethod() && var3.getClassName().equals(var1.getClassName()) && var3.getFileName().equals(var1.getFileName()) && var3.getMethodName().equals(var1.getMethodName())) {
            if(var2 != null != this.stackTrace.length > 1) {
               return false;
            } else if(var2 != null && !this.stackTrace[1].equals(var2)) {
               return false;
            } else {
               this.stackTrace[0] = var1;
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void trimStacktrace(int i) {
      StackTraceElement[] vars2 = new StackTraceElement[this.stackTrace.length - i];
      System.arraycopy(this.stackTrace, 0, vars2, 0, vars2.length);
      this.stackTrace = vars2;
   }

   public void getDetails(StringBuilder stringBuilder) {
      stringBuilder.append("-- ").append(this.title).append(" --\n");
      stringBuilder.append("Details:");

      for(CrashReportCategory.Entry var3 : this.entries) {
         stringBuilder.append("\n\t");
         stringBuilder.append(var3.getKey());
         stringBuilder.append(": ");
         stringBuilder.append(var3.getValue());
      }

      if(this.stackTrace != null && this.stackTrace.length > 0) {
         stringBuilder.append("\nStacktrace:");

         for(StackTraceElement var5 : this.stackTrace) {
            stringBuilder.append("\n\tat ");
            stringBuilder.append(var5);
         }
      }

   }

   public StackTraceElement[] getStacktrace() {
      return this.stackTrace;
   }

   public static void populateBlockDetails(CrashReportCategory crashReportCategory, BlockPos blockPos, @Nullable BlockState blockState) {
      if(blockState != null) {
         crashReportCategory.setDetail("Block", blockState::toString);
      }

      crashReportCategory.setDetail("Block location", () -> {
         return formatLocation(blockPos);
      });
   }

   static class Entry {
      private final String key;
      private final String value;

      public Entry(String key, Object object) {
         this.key = key;
         if(object == null) {
            this.value = "~~NULL~~";
         } else if(object instanceof Throwable) {
            Throwable var3 = (Throwable)object;
            this.value = "~~ERROR~~ " + var3.getClass().getSimpleName() + ": " + var3.getMessage();
         } else {
            this.value = object.toString();
         }

      }

      public String getKey() {
         return this.key;
      }

      public String getValue() {
         return this.value;
      }
   }
}
