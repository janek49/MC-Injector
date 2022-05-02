package net.minecraft.util.profiling;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ResultField;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FilledProfileResults implements ProfileResults {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map times;
   private final Map counts;
   private final long startTimeNano;
   private final int startTimeTicks;
   private final long endTimeNano;
   private final int endTimeTicks;
   private final int tickDuration;

   public FilledProfileResults(Map times, Map counts, long startTimeNano, int startTimeTicks, long endTimeNano, int endTimeTicks) {
      this.times = times;
      this.counts = counts;
      this.startTimeNano = startTimeNano;
      this.startTimeTicks = startTimeTicks;
      this.endTimeNano = endTimeNano;
      this.endTimeTicks = endTimeTicks;
      this.tickDuration = endTimeTicks - startTimeTicks;
   }

   public List getTimes(String string) {
      long var3 = this.times.containsKey("root")?((Long)this.times.get("root")).longValue():0L;
      long var5 = ((Long)this.times.getOrDefault(string, Long.valueOf(-1L))).longValue();
      long var7 = ((Long)this.counts.getOrDefault(string, Long.valueOf(0L))).longValue();
      List<ResultField> var9 = Lists.newArrayList();
      if(!string.isEmpty()) {
         string = string + '\u001e';
      }

      long var10 = 0L;

      for(String var13 : this.times.keySet()) {
         if(var13.length() > string.length() && var13.startsWith(string) && var13.indexOf(30, string.length() + 1) < 0) {
            var10 += ((Long)this.times.get(var13)).longValue();
         }
      }

      float var12 = (float)var10;
      if(var10 < var5) {
         var10 = var5;
      }

      if(var3 < var10) {
         var3 = var10;
      }

      Set<String> var13 = Sets.newHashSet(this.times.keySet());
      var13.addAll(this.counts.keySet());

      for(String var15 : var13) {
         if(var15.length() > string.length() && var15.startsWith(string) && var15.indexOf(30, string.length() + 1) < 0) {
            long var16 = ((Long)this.times.getOrDefault(var15, Long.valueOf(0L))).longValue();
            double var18 = (double)var16 * 100.0D / (double)var10;
            double var20 = (double)var16 * 100.0D / (double)var3;
            String var22 = var15.substring(string.length());
            long var23 = ((Long)this.counts.getOrDefault(var15, Long.valueOf(0L))).longValue();
            var9.add(new ResultField(var22, var18, var20, var23));
         }
      }

      for(String var15 : this.times.keySet()) {
         this.times.put(var15, Long.valueOf(((Long)this.times.get(var15)).longValue() * 999L / 1000L));
      }

      if((float)var10 > var12) {
         var9.add(new ResultField("unspecified", (double)((float)var10 - var12) * 100.0D / (double)var10, (double)((float)var10 - var12) * 100.0D / (double)var3, var7));
      }

      Collections.sort(var9);
      var9.add(0, new ResultField(string, 100.0D, (double)var10 * 100.0D / (double)var3, var7));
      return var9;
   }

   public long getStartTimeNano() {
      return this.startTimeNano;
   }

   public int getStartTimeTicks() {
      return this.startTimeTicks;
   }

   public long getEndTimeNano() {
      return this.endTimeNano;
   }

   public int getEndTimeTicks() {
      return this.endTimeTicks;
   }

   public boolean saveResults(File file) {
      file.getParentFile().mkdirs();
      Writer var2 = null;

      boolean var4;
      try {
         var2 = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
         var2.write(this.getProfilerResults(this.getNanoDuration(), this.getTickDuration()));
         boolean var3 = true;
         return var3;
      } catch (Throwable var8) {
         LOGGER.error("Could not save profiler results to {}", file, var8);
         var4 = false;
      } finally {
         IOUtils.closeQuietly(var2);
      }

      return var4;
   }

   protected String getProfilerResults(long var1, int var3) {
      StringBuilder var4 = new StringBuilder();
      var4.append("---- Minecraft Profiler Results ----\n");
      var4.append("// ");
      var4.append(getComment());
      var4.append("\n\n");
      var4.append("Version: ").append(SharedConstants.getCurrentVersion().getId()).append('\n');
      var4.append("Time span: ").append(var1 / 1000000L).append(" ms\n");
      var4.append("Tick span: ").append(var3).append(" ticks\n");
      var4.append("// This is approximately ").append(String.format(Locale.ROOT, "%.2f", new Object[]{Float.valueOf((float)var3 / ((float)var1 / 1.0E9F))})).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
      var4.append("--- BEGIN PROFILE DUMP ---\n\n");
      this.appendProfilerResults(0, "root", var4);
      var4.append("--- END PROFILE DUMP ---\n\n");
      return var4.toString();
   }

   public String getProfilerResults() {
      StringBuilder var1 = new StringBuilder();
      this.appendProfilerResults(0, "root", var1);
      return var1.toString();
   }

   private void appendProfilerResults(int var1, String string, StringBuilder stringBuilder) {
      List<ResultField> var4 = this.getTimes(string);
      if(var4.size() >= 3) {
         for(int var5 = 1; var5 < var4.size(); ++var5) {
            ResultField var6 = (ResultField)var4.get(var5);
            stringBuilder.append(String.format("[%02d] ", new Object[]{Integer.valueOf(var1)}));

            for(int var7 = 0; var7 < var1; ++var7) {
               stringBuilder.append("|   ");
            }

            stringBuilder.append(var6.name).append('(').append(var6.count).append('/').append(String.format(Locale.ROOT, "%.0f", new Object[]{Float.valueOf((float)var6.count / (float)this.tickDuration)})).append(')').append(" - ").append(String.format(Locale.ROOT, "%.2f", new Object[]{Double.valueOf(var6.percentage)})).append("%/").append(String.format(Locale.ROOT, "%.2f", new Object[]{Double.valueOf(var6.globalPercentage)})).append("%\n");
            if(!"unspecified".equals(var6.name)) {
               try {
                  this.appendProfilerResults(var1 + 1, string + '\u001e' + var6.name, stringBuilder);
               } catch (Exception var8) {
                  stringBuilder.append("[[ EXCEPTION ").append(var8).append(" ]]");
               }
            }
         }

      }
   }

   private static String getComment() {
      String[] vars0 = new String[]{"Shiny numbers!", "Am I not running fast enough? :(", "I\'m working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it\'ll have more motivation to work faster! Poor server."};

      try {
         return vars0[(int)(Util.getNanos() % (long)vars0.length)];
      } catch (Throwable var2) {
         return "Witty comment unavailable :(";
      }
   }

   public int getTickDuration() {
      return this.tickDuration;
   }
}
