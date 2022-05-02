package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import net.minecraft.util.Mth;

@ClientJarOnly
public enum AttackIndicatorStatus {
   OFF(0, "options.off"),
   CROSSHAIR(1, "options.attack.crosshair"),
   HOTBAR(2, "options.attack.hotbar");

   private static final AttackIndicatorStatus[] BY_ID = (AttackIndicatorStatus[])Arrays.stream(values()).sorted(Comparator.comparingInt(AttackIndicatorStatus::getId)).toArray((i) -> {
      return new AttackIndicatorStatus[i];
   });
   private final int id;
   private final String key;

   private AttackIndicatorStatus(int id, String key) {
      this.id = id;
      this.key = key;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public static AttackIndicatorStatus byId(int id) {
      return BY_ID[Mth.positiveModulo(id, BY_ID.length)];
   }
}
