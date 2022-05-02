package net.minecraft.advancements;

import java.util.Collection;

public interface RequirementsStrategy {
   RequirementsStrategy AND = (collection) -> {
      String[][] strings = new String[collection.size()][];
      int var2 = 0;

      for(String var4 : collection) {
         strings[var2++] = new String[]{var4};
      }

      return strings;
   };
   RequirementsStrategy OR = (collection) -> {
      return new String[][]{(String[])collection.toArray(new String[0])};
   };

   String[][] createRequirements(Collection var1);
}
