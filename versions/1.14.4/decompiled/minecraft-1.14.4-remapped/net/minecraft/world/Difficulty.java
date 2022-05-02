package net.minecraft.world;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum Difficulty {
   PEACEFUL(0, "peaceful"),
   EASY(1, "easy"),
   NORMAL(2, "normal"),
   HARD(3, "hard");

   private static final Difficulty[] BY_ID = (Difficulty[])Arrays.stream(values()).sorted(Comparator.comparingInt(Difficulty::getId)).toArray((i) -> {
      return new Difficulty[i];
   });
   private final int id;
   private final String key;

   private Difficulty(int id, String key) {
      this.id = id;
      this.key = key;
   }

   public int getId() {
      return this.id;
   }

   public Component getDisplayName() {
      return new TranslatableComponent("options.difficulty." + this.key, new Object[0]);
   }

   public static Difficulty byId(int id) {
      return BY_ID[id % BY_ID.length];
   }

   @Nullable
   public static Difficulty byName(String name) {
      for(Difficulty var4 : values()) {
         if(var4.key.equals(name)) {
            return var4;
         }
      }

      return null;
   }

   public String getKey() {
      return this.key;
   }
}
