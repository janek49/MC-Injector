package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

public class WeightedList {
   private final List entries;
   private final Random random;

   public WeightedList() {
      this(new Random());
   }

   public WeightedList(Random random) {
      this.entries = Lists.newArrayList();
      this.random = random;
   }

   public void add(Object object, int var2) {
      this.entries.add(new WeightedList.WeightedEntry(object, var2));
   }

   public void shuffle() {
      this.entries.forEach((weightedList$WeightedEntry) -> {
         weightedList$WeightedEntry.setRandom(this.random.nextFloat());
      });
      this.entries.sort(Comparator.comparingDouble(WeightedList.WeightedEntry::getRandWeight));
   }

   public Stream stream() {
      return this.entries.stream().map(WeightedList.WeightedEntry::getData);
   }

   public String toString() {
      return "WeightedList[" + this.entries + "]";
   }

   class WeightedEntry {
      private final Object data;
      private final int weight;
      private double randWeight;

      private WeightedEntry(Object data, int weight) {
         this.weight = weight;
         this.data = data;
      }

      public double getRandWeight() {
         return this.randWeight;
      }

      public void setRandom(float random) {
         this.randWeight = -Math.pow((double)random, (double)(1.0F / (float)this.weight));
      }

      public Object getData() {
         return this.data;
      }

      public String toString() {
         return "" + this.weight + ":" + this.data;
      }
   }
}
