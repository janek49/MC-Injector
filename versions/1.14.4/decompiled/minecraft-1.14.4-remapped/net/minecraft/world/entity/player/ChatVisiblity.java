package net.minecraft.world.entity.player;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import net.minecraft.util.Mth;

public enum ChatVisiblity {
   FULL(0, "options.chat.visibility.full"),
   SYSTEM(1, "options.chat.visibility.system"),
   HIDDEN(2, "options.chat.visibility.hidden");

   private static final ChatVisiblity[] BY_ID = (ChatVisiblity[])Arrays.stream(values()).sorted(Comparator.comparingInt(ChatVisiblity::getId)).toArray((i) -> {
      return new ChatVisiblity[i];
   });
   private final int id;
   private final String key;

   private ChatVisiblity(int id, String key) {
      this.id = id;
      this.key = key;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public static ChatVisiblity byId(int id) {
      return BY_ID[Mth.positiveModulo(id, BY_ID.length)];
   }
}
