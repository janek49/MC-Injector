package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.StoredUserEntry;

public abstract class BanListEntry extends StoredUserEntry {
   public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   protected final Date created;
   protected final String source;
   protected final Date expires;
   protected final String reason;

   public BanListEntry(Object object, @Nullable Date created, @Nullable String source, @Nullable Date expires, @Nullable String reason) {
      super(object);
      this.created = created == null?new Date():created;
      this.source = source == null?"(Unknown)":source;
      this.expires = expires;
      this.reason = reason == null?"Banned by an operator.":reason;
   }

   protected BanListEntry(Object object, JsonObject jsonObject) {
      super(object, jsonObject);

      Date var3;
      try {
         var3 = jsonObject.has("created")?DATE_FORMAT.parse(jsonObject.get("created").getAsString()):new Date();
      } catch (ParseException var7) {
         var3 = new Date();
      }

      this.created = var3;
      this.source = jsonObject.has("source")?jsonObject.get("source").getAsString():"(Unknown)";

      Date var4;
      try {
         var4 = jsonObject.has("expires")?DATE_FORMAT.parse(jsonObject.get("expires").getAsString()):null;
      } catch (ParseException var6) {
         var4 = null;
      }

      this.expires = var4;
      this.reason = jsonObject.has("reason")?jsonObject.get("reason").getAsString():"Banned by an operator.";
   }

   public String getSource() {
      return this.source;
   }

   public Date getExpires() {
      return this.expires;
   }

   public String getReason() {
      return this.reason;
   }

   public abstract Component getDisplayName();

   boolean hasExpired() {
      return this.expires == null?false:this.expires.before(new Date());
   }

   protected void serialize(JsonObject jsonObject) {
      jsonObject.addProperty("created", DATE_FORMAT.format(this.created));
      jsonObject.addProperty("source", this.source);
      jsonObject.addProperty("expires", this.expires == null?"forever":DATE_FORMAT.format(this.expires));
      jsonObject.addProperty("reason", this.reason);
   }
}
