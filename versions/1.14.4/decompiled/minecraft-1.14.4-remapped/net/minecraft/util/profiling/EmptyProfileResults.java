package net.minecraft.util.profiling;

import java.io.File;
import java.util.Collections;
import java.util.List;
import net.minecraft.util.profiling.ProfileResults;

public class EmptyProfileResults implements ProfileResults {
   public static final EmptyProfileResults EMPTY = new EmptyProfileResults();

   public List getTimes(String string) {
      return Collections.emptyList();
   }

   public boolean saveResults(File file) {
      return false;
   }

   public long getStartTimeNano() {
      return 0L;
   }

   public int getStartTimeTicks() {
      return 0;
   }

   public long getEndTimeNano() {
      return 0L;
   }

   public int getEndTimeTicks() {
      return 0;
   }

   public String getProfilerResults() {
      return "";
   }
}
