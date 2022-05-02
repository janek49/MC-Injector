package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.server.players.StoredUserEntry;

public class ServerOpListEntry extends StoredUserEntry {
   private final int level;
   private final boolean bypassesPlayerLimit;

   public ServerOpListEntry(GameProfile gameProfile, int level, boolean bypassesPlayerLimit) {
      super(gameProfile);
      this.level = level;
      this.bypassesPlayerLimit = bypassesPlayerLimit;
   }

   public ServerOpListEntry(JsonObject jsonObject) {
      super(createGameProfile(jsonObject), jsonObject);
      this.level = jsonObject.has("level")?jsonObject.get("level").getAsInt():0;
      this.bypassesPlayerLimit = jsonObject.has("bypassesPlayerLimit") && jsonObject.get("bypassesPlayerLimit").getAsBoolean();
   }

   public int getLevel() {
      return this.level;
   }

   public boolean getBypassesPlayerLimit() {
      return this.bypassesPlayerLimit;
   }

   protected void serialize(JsonObject jsonObject) {
      if(this.getUser() != null) {
         jsonObject.addProperty("uuid", ((GameProfile)this.getUser()).getId() == null?"":((GameProfile)this.getUser()).getId().toString());
         jsonObject.addProperty("name", ((GameProfile)this.getUser()).getName());
         super.serialize(jsonObject);
         jsonObject.addProperty("level", Integer.valueOf(this.level));
         jsonObject.addProperty("bypassesPlayerLimit", Boolean.valueOf(this.bypassesPlayerLimit));
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
