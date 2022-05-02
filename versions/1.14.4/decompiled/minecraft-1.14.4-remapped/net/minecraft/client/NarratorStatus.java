package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import net.minecraft.util.Mth;

@ClientJarOnly
public enum NarratorStatus {
   OFF(0, "options.narrator.off"),
   ALL(1, "options.narrator.all"),
   CHAT(2, "options.narrator.chat"),
   SYSTEM(3, "options.narrator.system");

   private static final NarratorStatus[] BY_ID = (NarratorStatus[])Arrays.stream(values()).sorted(Comparator.comparingInt(NarratorStatus::getId)).toArray((i) -> {
      return new NarratorStatus[i];
   });
   private final int id;
   private final String key;

   private NarratorStatus(int id, String key) {
      this.id = id;
      this.key = key;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public static NarratorStatus byId(int id) {
      return BY_ID[Mth.positiveModulo(id, BY_ID.length)];
   }
}
