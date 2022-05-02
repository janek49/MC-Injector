package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.players.BanListEntry;

public class IpBanListEntry extends BanListEntry {
   public IpBanListEntry(String string) {
      this(string, (Date)null, (String)null, (Date)null, (String)null);
   }

   public IpBanListEntry(String var1, @Nullable Date var2, @Nullable String var3, @Nullable Date var4, @Nullable String var5) {
      super(var1, var2, var3, var4, var5);
   }

   public Component getDisplayName() {
      return new TextComponent((String)this.getUser());
   }

   public IpBanListEntry(JsonObject jsonObject) {
      super(createIpInfo(jsonObject), jsonObject);
   }

   private static String createIpInfo(JsonObject jsonObject) {
      return jsonObject.has("ip")?jsonObject.get("ip").getAsString():null;
   }

   protected void serialize(JsonObject jsonObject) {
      if(this.getUser() != null) {
         jsonObject.addProperty("ip", (String)this.getUser());
         super.serialize(jsonObject);
      }
   }
}
