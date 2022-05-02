package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import net.minecraft.util.Mth;

@ClientJarOnly
public enum AmbientOcclusionStatus {
   OFF(0, "options.ao.off"),
   MIN(1, "options.ao.min"),
   MAX(2, "options.ao.max");

   private static final AmbientOcclusionStatus[] BY_ID = (AmbientOcclusionStatus[])Arrays.stream(values()).sorted(Comparator.comparingInt(AmbientOcclusionStatus::getId)).toArray((i) -> {
      return new AmbientOcclusionStatus[i];
   });
   private final int id;
   private final String key;

   private AmbientOcclusionStatus(int id, String key) {
      this.id = id;
      this.key = key;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public static AmbientOcclusionStatus byId(int id) {
      return BY_ID[Mth.positiveModulo(id, BY_ID.length)];
   }
}
