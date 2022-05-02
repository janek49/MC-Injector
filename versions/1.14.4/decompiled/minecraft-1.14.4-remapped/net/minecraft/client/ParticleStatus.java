package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import net.minecraft.util.Mth;

@ClientJarOnly
public enum ParticleStatus {
   ALL(0, "options.particles.all"),
   DECREASED(1, "options.particles.decreased"),
   MINIMAL(2, "options.particles.minimal");

   private static final ParticleStatus[] BY_ID = (ParticleStatus[])Arrays.stream(values()).sorted(Comparator.comparingInt(ParticleStatus::getId)).toArray((i) -> {
      return new ParticleStatus[i];
   });
   private final int id;
   private final String key;

   private ParticleStatus(int id, String key) {
      this.id = id;
      this.key = key;
   }

   public String getKey() {
      return this.key;
   }

   public int getId() {
      return this.id;
   }

   public static ParticleStatus byId(int id) {
      return BY_ID[Mth.positiveModulo(id, BY_ID.length)];
   }
}
