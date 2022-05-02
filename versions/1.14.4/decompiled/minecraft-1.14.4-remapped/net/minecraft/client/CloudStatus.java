package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import net.minecraft.util.Mth;

@ClientJarOnly
public enum CloudStatus {
   OFF(0, "options.off"),
   FAST(1, "options.clouds.fast"),
   FANCY(2, "options.clouds.fancy");

   private static final CloudStatus[] BY_ID = (CloudStatus[])Arrays.stream(values()).sorted(Comparator.comparingInt(CloudStatus::getId)).toArray((i) -> {
      return new CloudStatus[i];
   });
   private final int id;
   private final String key;

   private CloudStatus(int id, String key) {
      this.id = id;
      this.key = key;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public static CloudStatus byId(int id) {
      return BY_ID[Mth.positiveModulo(id, BY_ID.length)];
   }
}
