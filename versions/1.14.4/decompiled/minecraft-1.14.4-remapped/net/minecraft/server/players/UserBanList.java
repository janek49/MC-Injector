package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.StoredUserList;
import net.minecraft.server.players.UserBanListEntry;

public class UserBanList extends StoredUserList {
   public UserBanList(File file) {
      super(file);
   }

   protected StoredUserEntry createEntry(JsonObject jsonObject) {
      return new UserBanListEntry(jsonObject);
   }

   public boolean isBanned(GameProfile gameProfile) {
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
