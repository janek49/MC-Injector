package net.minecraft.server.players;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class StoredUserEntry {
   @Nullable
   private final Object user;

   public StoredUserEntry(Object user) {
      this.user = user;
   }

   protected StoredUserEntry(@Nullable Object user, JsonObject jsonObject) {
      this.user = user;
   }

   @Nullable
   Object getUser() {
      return this.user;
   }

   boolean hasExpired() {
      return false;
   }

   protected void serialize(JsonObject jsonObject) {
   }
}
