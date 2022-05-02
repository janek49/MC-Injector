package net.minecraft;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String title;
   private final Throwable exception;
   private final CrashReportCategory systemDetails = new CrashReportCategory(this, "System Details");
   private final List details = Lists.newArrayList();
   private File saveFile;
   private boolean trackingStackTrace = true;
   private StackTraceElement[] uncategorizedStackTrace = new StackTraceElement[0];

   public CrashReport(String title, Throwable exception) {
      this.title = title;
      this.exception = exception;
      this.initDetails();
   }

   private void initDetails() {
      this.systemDetails.setDetail("Minecraft Version", () -> {
         return SharedConstants.getCurrentVersion().getName();
      });
      this.systemDetails.setDetail("Minecraft Version ID", () -> {
         return SharedConstants.getCurrentVersion().getId();
      });
      this.systemDetails.setDetail("Operating System", () -> {
         return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
      });
      this.systemDetails.setDetail("Java Version", () -> {
         return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
      });
      this.systemDetails.setDetail("Java VM Version", () -> {
         return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
      });
      this.systemDetails.setDetail("Memory", () -> {
         Runtime var0 = Runtime.getRuntime();
         long var1 = var0.maxMemory();
         long var3 = var0.totalMemory();
         long var5 = var0.freeMemory();
         long var7 = var1 / 1024L / 1024L;
         long var9 = var3 / 1024L / 1024L;
         long var11 = var5 / 1024L / 1024L;
         return var5 + " bytes (" + var11 + " MB) / " + var3 + " bytes (" + var9 + " MB) up to " + var1 + " bytes (" + var7 + " MB)";
      });
      this.systemDetails.setDetail("CPUs", (Object)Integer.valueOf(Runtime.getRuntime().availableProcessors()));
      this.systemDetails.setDetail("JVM Flags", () -> {
         List<String> var0 = (List)Util.getVmArguments().collect(Collectors.toList());
         return String.format("%d total; %s", new Object[]{Integer.valueOf(var0.size()), var0.stream().collect(Collectors.joining(" "))});
      });
   }

   public String getTitle() {
      return this.title;
   }

   public Throwable getException() {
      return this.exception;
   }

   public void getDetails(StringBuilder stringBuilder) {
      if((this.uncategorizedStackTrace == null || this.uncategorizedStackTrace.length <= 0) && !this.details.isEmpty()) {
         this.uncategorizedStackTrace = (StackTraceElement[])ArrayUtils.subarray(((CrashReportCategory)this.details.get(0)).getStacktrace(), 0, 1);
      }

      if(this.uncategorizedStackTrace != null && this.uncategorizedStackTrace.length > 0) {
         stringBuilder.append("-- Head --\n");
         stringBuilder.append("Thread: ").append(Thread.currentThread().getName()).append("\n");
         stringBuilder.append("Stacktrace:\n");

         for(StackTraceElement var5 : this.uncategorizedStackTrace) {
            stringBuilder.append("\t").append("at ").append(var5);
            stringBuilder.append("\n");
         }

         stringBuilder.append("\n");
      }

      for(CrashReportCategory var3 : this.details) {
         var3.getDetails(stringBuilder);
         stringBuilder.append("\n\n");
      }

      this.systemDetails.getDetails(stringBuilder);
   }

   public String getExceptionMessage() {
      StringWriter var1 = null;
      PrintWriter var2 = null;
      Throwable var3 = this.exception;
      if(var3.getMessage() == null) {
         if(var3 instanceof NullPointerException) {
            var3 = new NullPointerException(this.title);
         } else if(var3 instanceof StackOverflowError) {
            var3 = new StackOverflowError(this.title);
         } else if(var3 instanceof OutOfMemoryError) {
            var3 = new OutOfMemoryError(this.title);
         }

         var3.setStackTrace(this.exception.getStackTrace());
      }

      String var4;
      try {
         var1 = new StringWriter();
         var2 = new PrintWriter(var1);
         var3.printStackTrace(var2);
         var4 = var1.toString();
      } finally {
         IOUtils.closeQuietly(var1);
         IOUtils.closeQuietly(var2);
      }

      return var4;
   }

   public String getFriendlyReport() {
      StringBuilder var1 = new StringBuilder();
      var1.append("---- Minecraft Crash Report ----\n");
      var1.append("// ");
      var1.append(getErrorComment());
      var1.append("\n\n");
      var1.append("Time: ");
      var1.append((new SimpleDateFormat()).format(new Date()));
      var1.append("\n");
      var1.append("Description: ");
      var1.append(this.title);
      var1.append("\n\n");
      var1.append(this.getExceptionMessage());
      var1.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

      for(int var2 = 0; var2 < 87; ++var2) {
         var1.append("-");
      }

      var1.append("\n\n");
      this.getDetails(var1);
      return var1.toString();
   }

   public File getSaveFile() {
      return this.saveFile;
   }

   public boolean saveToFile(File saveFile) {
      if(this.saveFile != null) {
         return false;
      } else {
         if(saveFile.getParentFile() != null) {
            saveFile.getParentFile().mkdirs();
         }

         Writer var2 = null;

         boolean var4;
         try {
            var2 = new OutputStreamWriter(new FileOutputStream(saveFile), StandardCharsets.UTF_8);
            var2.write(this.getFriendlyReport());
            this.saveFile = saveFile;
            boolean var3 = true;
            return var3;
         } catch (Throwable var8) {
            LOGGER.error("Could not save crash report to {}", saveFile, var8);
            var4 = false;
         } finally {
            IOUtils.closeQuietly(var2);
         }

         return var4;
      }
   }

   public CrashReportCategory getSystemDetails() {
      return this.systemDetails;
   }

   public CrashReportCategory addCategory(String string) {
      return this.addCategory(string, 1);
   }

   public CrashReportCategory addCategory(String string, int var2) {
      CrashReportCategory crashReportCategory = new CrashReportCategory(this, string);
      if(this.trackingStackTrace) {
         int var4 = crashReportCategory.fillInStackTrace(var2);
         StackTraceElement[] vars5 = this.exception.getStackTrace();
         StackTraceElement var6 = null;
         StackTraceElement var7 = null;
         int var8 = vars5.length - var4;
         if(var8 < 0) {
            System.out.println("Negative index in crash report handler (" + vars5.length + "/" + var4 + ")");
         }

         if(vars5 != null && 0 <= var8 && var8 < vars5.length) {
            var6 = vars5[var8];
            if(vars5.length + 1 - var4 < vars5.length) {
               var7 = vars5[vars5.length + 1 - var4];
            }
         }

         this.trackingStackTrace = crashReportCategory.validateStackTrace(var6, var7);
         if(var4 > 0 && !this.details.isEmpty()) {
            CrashReportCategory var9 = (CrashReportCategory)this.details.get(this.details.size() - 1);
            var9.trimStacktrace(var4);
         } else if(vars5 != null && vars5.length >= var4 && 0 <= var8 && var8 < vars5.length) {
            this.uncategorizedStackTrace = new StackTraceElement[var8];
            System.arraycopy(vars5, 0, this.uncategorizedStackTrace, 0, this.uncategorizedStackTrace.length);
         } else {
            this.trackingStackTrace = false;
         }
      }

      this.details.add(crashReportCategory);
      return crashReportCategory;
   }

   private static String getErrorComment() {
      String[] vars0 = new String[]{"Who set us up the TNT?", "Everything\'s going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I\'m sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don\'t be sad. I\'ll do better next time, I promise!", "Don\'t be sad, have a hug! <3", "I just don\'t know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn\'t worry myself about that.", "I bet Cylons wouldn\'t have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I\'m Minecraft, and I\'m a crashaholic.", "Ooh. Shiny.", "This doesn\'t make any sense!", "Why is it breaking :(", "Don\'t do that.", "Ouch. That hurt :(", "You\'re mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!", "But it works on my machine."};

      try {
         return vars0[(int)(Util.getNanos() % (long)vars0.length)];
      } catch (Throwable var2) {
         return "Witty comment unavailable :(";
      }
   }

   public static CrashReport forThrowable(Throwable throwable, String string) {
      while(throwable instanceof CompletionException && throwable.getCause() != null) {
         throwable = throwable.getCause();
      }

      CrashReport crashReport;
      if(throwable instanceof ReportedException) {
         crashReport = ((ReportedException)throwable).getReport();
      } else {
         crashReport = new CrashReport(string, throwable);
      }

      return crashReport;
   }
}
