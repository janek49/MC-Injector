package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.StoredUserList;

public class ServerOpList extends StoredUserList {
   public ServerOpList(File file) {
      super(file);
   }

   protected StoredUserEntry createEntry(JsonObject jsonObject) {
      return new ServerOpListEntry(jsonObject);
   }

   public String[] getUserList() {
      String[] strings = new String[this.getEntries().size()];
      int var2 = 0;

      for(StoredUserEntry<GameProfile> var4 : this.getEntries()) {
         strings[var2++] = ((GameProfile)var4.getUser()).getName();
      }

      return strings;
   }

   public boolean canBypassPlayerLimit(GameProfile gameProfile) {
      ServerOpListEntry var2 = (ServerOpListEntry)this.get(gameProfile);
      return var2 != null?var2.getBypassesPlayerLimit():false;
   }

   protected String getKeyForUser(GameProfile gameProfile) {
      return gameProfile.getId().toString();
   }

   // $FF: synthetic method
   protected String getKeyForUser(Object var1) {
      return this.getKeyForUser((GameProfile)var1);
   }
}
