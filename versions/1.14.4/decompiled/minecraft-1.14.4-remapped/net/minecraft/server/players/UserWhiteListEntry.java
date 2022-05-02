package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.server.players.StoredUserEntry;

public class UserWhiteListEntry extends StoredUserEntry {
   public UserWhiteListEntry(GameProfile gameProfile) {
      super(gameProfile);
   }

   public UserWhiteListEntry(JsonObject jsonObject) {
      super(createGameProfile(jsonObject), jsonObject);
   }

   protected void serialize(JsonObject jsonObject) {
      if(this.getUser() != null) {
         jsonObject.addProperty("uuid", ((GameProfile)this.getUser()).getId() == null?"":((GameProfile)this.getUser()).getId().toString());
         jsonObject.addProperty("name", ((GameProfile)this.getUser()).getName());
         super.serialize(jsonObject);
      }
   }

   private static GameProfile createGameProfile(JsonObject jsonObject) {
      if(jsonObject.has("uuid") && jsonObject.has("name")) {
         String var1 = jsonObject.get("uuid").getAsString();

         UUID var2;
         try {
            var2 = UUID.fromString(var1);
         } catch (Throwable var4) {
            return null;
         }

         return new GameProfile(var2, jsonObject.get("name").getAsString());
      } else {
         return null;
      }
   }
}
