package net.minecraft.server.dedicated;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.Util;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerWatchdog implements Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final DedicatedServer server;
   private final long maxTickTime;

   public ServerWatchdog(DedicatedServer server) {
      this.server = server;
      this.maxTickTime = server.getMaxTickLength();
   }

   public void run() {
      while(this.server.isRunning()) {
         long var1 = this.server.getNextTickTime();
         long var3 = Util.getMillis();
         long var5 = var3 - var1;
         if(var5 > this.maxTickTime) {
            LOGGER.fatal("A single server tick took {} seconds (should be max {})", String.format(Locale.ROOT, "%.2f", new Object[]{Float.valueOf((float)var5 / 1000.0F)}), String.format(Locale.ROOT, "%.2f", new Object[]{Float.valueOf(0.05F)}));
            LOGGER.fatal("Considering it to be crashed, server will forcibly shutdown.");
            ThreadMXBean var7 = ManagementFactory.getThreadMXBean();
            ThreadInfo[] vars8 = var7.dumpAllThreads(true, true);
            StringBuilder var9 = new StringBuilder();
            Error var10 = new Error();

            for(ThreadInfo var14 : vars8) {
               if(var14.getThreadId() == this.server.getRunningThread().getId()) {
                  var10.setStackTrace(var14.getStackTrace());
               }

               var9.append(var14);
               var9.append("\n");
            }

            CrashReport var11 = new CrashReport("Watching Server", var10);
            this.server.fillReport(var11);
            CrashReportCategory var12 = var11.addCategory("Thread Dump");
            var12.setDetail("Threads", (Object)var9);
            File var13 = new File(new File(this.server.getServerDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");
            if(var11.saveToFile(var13)) {
               LOGGER.error("This crash report has been saved to: {}", var13.getAbsolutePath());
            } else {
               LOGGER.error("We were unable to save this crash report to disk.");
            }

            this.exit();
         }

         try {
            Thread.sleep(var1 + this.maxTickTime - var3);
         } catch (InterruptedException var15) {
            ;
         }
      }

   }

   private void exit() {
      try {
         Timer var1 = new Timer();
         var1.schedule(new TimerTask() {
            public void run() {
               Runtime.getRuntime().halt(1);
            }
         }, 10000L);
         System.exit(1);
      } catch (Throwable var2) {
         Runtime.getRuntime().halt(1);
      }

   }
}
