package net.minecraft.util.profiling;

public final class ResultField implements Comparable {
   public final double percentage;
   public final double globalPercentage;
   public final long count;
   public final String name;

   public ResultField(String name, double percentage, double globalPercentage, long count) {
      this.name = name;
      this.percentage = percentage;
      this.globalPercentage = globalPercentage;
      this.count = count;
   }

   public int compareTo(ResultField resultField) {
      return resultField.percentage < this.percentage?-1:(resultField.percentage > this.percentage?1:resultField.name.compareTo(this.name));
   }

   public int getColor() {
      return (this.name.hashCode() & 11184810) + 4473924;
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((ResultField)var1);
   }
}
