package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.players.BanListEntry;

public class UserBanListEntry extends BanListEntry {
   public UserBanListEntry(GameProfile gameProfile) {
      this(gameProfile, (Date)null, (String)null, (Date)null, (String)null);
   }

   public UserBanListEntry(GameProfile gameProfile, @Nullable Date var2, @Nullable String var3, @Nullable Date var4, @Nullable String var5) {
      super(gameProfile, var2, var3, var4, var5);
   }

   public UserBanListEntry(JsonObject jsonObject) {
      super(createGameProfile(jsonObject), jsonObject);
   }

   protected void serialize(JsonObject jsonObject) {
      if(this.getUser() != null) {
         jsonObject.addProperty("uuid", ((GameProfile)this.getUser()).getId() == null?"":((GameProfile)this.getUser()).getId().toString());
         jsonObject.addProperty("name", ((GameProfile)this.getUser()).getName());
         super.serialize(jsonObject);
      }
   }

   public Component getDisplayName() {
      GameProfile var1 = (GameProfile)this.getUser();
      return new TextComponent(var1.getName() != null?var1.getName():Objects.toString(var1.getId(), "(Unknown)"));
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
