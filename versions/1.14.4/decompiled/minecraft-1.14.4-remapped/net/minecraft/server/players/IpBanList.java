package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;
import net.minecraft.server.players.IpBanListEntry;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.StoredUserList;

public class IpBanList extends StoredUserList {
   public IpBanList(File file) {
      super(file);
   }

   protected StoredUserEntry createEntry(JsonObject jsonObject) {
      return new IpBanListEntry(jsonObject);
   }

   public boolean isBanned(SocketAddress socketAddress) {
      String var2 = this.getIpFromAddress(socketAddress);
      return this.contains(var2);
   }

   public boolean isBanned(String string) {
      return this.contains(string);
   }

   public IpBanListEntry get(SocketAddress socketAddress) {
      String var2 = this.getIpFromAddress(socketAddress);
      return (IpBanListEntry)this.get(var2);
   }

   private String getIpFromAddress(SocketAddress socketAddress) {
      String string = socketAddress.toString();
      if(string.contains("/")) {
         string = string.substring(string.indexOf(47) + 1);
      }

      if(string.contains(":")) {
         string = string.substring(0, string.indexOf(58));
      }

      return string;
   }
}
