package net.minecraft.util;

import java.util.List;
import java.util.Random;

public class WeighedRandom {
   public static int getTotalWeight(List list) {
      int var1 = 0;
      int var2 = 0;

      for(int var3 = list.size(); var2 < var3; ++var2) {
         WeighedRandom.WeighedRandomItem var4 = (WeighedRandom.WeighedRandomItem)list.get(var2);
         var1 += var4.weight;
      }

      return var1;
   }

   public static WeighedRandom.WeighedRandomItem getRandomItem(Random random, List list, int var2) {
      if(var2 <= 0) {
         throw new IllegalArgumentException();
      } else {
         int var3 = random.nextInt(var2);
         return getWeightedItem(list, var3);
      }
   }

   public static WeighedRandom.WeighedRandomItem getWeightedItem(List list, int var1) {
      int var2 = 0;

      for(int var3 = list.size(); var2 < var3; ++var2) {
         T var4 = (WeighedRandom.WeighedRandomItem)list.get(var2);
         var1 -= var4.weight;
         if(var1 < 0) {
            return var4;
         }
      }

      return null;
   }

   public static WeighedRandom.WeighedRandomItem getRandomItem(Random random, List list) {
      return getRandomItem(random, list, getTotalWeight(list));
   }

   public static class WeighedRandomItem {
      protected final int weight;

      public WeighedRandomItem(int weight) {
         this.weight = weight;
      }
   }
}
