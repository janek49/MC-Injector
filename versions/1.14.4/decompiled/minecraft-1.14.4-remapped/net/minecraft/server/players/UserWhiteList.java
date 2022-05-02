package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.StoredUserList;
import net.minecraft.server.players.UserWhiteListEntry;

public class UserWhiteList extends StoredUserList {
   public UserWhiteList(File file) {
      super(file);
   }

   protected StoredUserEntry createEntry(JsonObject jsonObject) {
      return new UserWhiteListEntry(jsonObject);
   }

   public boolean isWhiteListed(GameProfile gameProfile) {
      return this.contains(gameProfile);
   }

   public String[] getUserList() {
      String[] strings = new String[this.getEntries().size()];
      int var2 = 0;

      for(StoredUserEntry<GameProfile> var4 : this.getEntries()) {
         strings[var2++] = ((GameProfile)var4.getUser()).getName();
      }

      return strings;
   }

   protected String getKeyForUser(GameProfile gameProfile) {
      return gameProfile.getId().toString();
   }

   // $FF: synthetic method
   protected String getKeyForUser(Object var1) {
      return this.getKeyForUser((GameProfile)var1);
   }
}
