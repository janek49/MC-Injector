package net.minecraft.network.protocol.status;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;

public class ServerStatus {
   private Component description;
   private ServerStatus.Players players;
   private ServerStatus.Version version;
   private String favicon;

   public Component getDescription() {
      return this.description;
   }

   public void setDescription(Component description) {
      this.description = description;
   }

   public ServerStatus.Players getPlayers() {
      return this.players;
   }

   public void setPlayers(ServerStatus.Players players) {
      this.players = players;
   }

   public ServerStatus.Version getVersion() {
      return this.version;
   }

   public void setVersion(ServerStatus.Version version) {
      this.version = version;
   }

   public void setFavicon(String favicon) {
      this.favicon = favicon;
   }

   public String getFavicon() {
      return this.favicon;
   }

   public static class Players {
      private final int maxPlayers;
      private final int numPlayers;
      private GameProfile[] sample;

      public Players(int maxPlayers, int numPlayers) {
         this.maxPlayers = maxPlayers;
         this.numPlayers = numPlayers;
      }

      public int getMaxPlayers() {
         return this.maxPlayers;
      }

      public int getNumPlayers() {
         return this.numPlayers;
      }

      public GameProfile[] getSample() {
         return this.sample;
      }

      public void setSample(GameProfile[] sample) {
         this.sample = sample;
      }

      public static class Serializer implements JsonDeserializer, JsonSerializer {
         public ServerStatus.Players deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject var4 = GsonHelper.convertToJsonObject(jsonElement, "players");
            ServerStatus.Players var5 = new ServerStatus.Players(GsonHelper.getAsInt(var4, "max"), GsonHelper.getAsInt(var4, "online"));
            if(GsonHelper.isArrayNode(var4, "sample")) {
               JsonArray var6 = GsonHelper.getAsJsonArray(var4, "sample");
               if(var6.size() > 0) {
                  GameProfile[] vars7 = new GameProfile[var6.size()];

                  for(int var8 = 0; var8 < vars7.length; ++var8) {
                     JsonObject var9 = GsonHelper.convertToJsonObject(var6.get(var8), "player[" + var8 + "]");
                     String var10 = GsonHelper.getAsString(var9, "id");
                     vars7[var8] = new GameProfile(UUID.fromString(var10), GsonHelper.getAsString(var9, "name"));
                  }

                  var5.setSample(vars7);
               }
            }

            return var5;
         }

         public JsonElement serialize(ServerStatus.Players serverStatus$Players, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject var4 = new JsonObject();
            var4.addProperty("max", Integer.valueOf(serverStatus$Players.getMaxPlayers()));
            var4.addProperty("online", Integer.valueOf(serverStatus$Players.getNumPlayers()));
            if(serverStatus$Players.getSample() != null && serverStatus$Players.getSample().length > 0) {
               JsonArray var5 = new JsonArray();

               for(int var6 = 0; var6 < serverStatus$Players.getSample().length; ++var6) {
                  JsonObject var7 = new JsonObject();
                  UUID var8 = serverStatus$Players.getSample()[var6].getId();
                  var7.addProperty("id", var8 == null?"":var8.toString());
                  var7.addProperty("name", serverStatus$Players.getSample()[var6].getName());
                  var5.add(var7);
               }

               var4.add("sample", var5);
            }

            return var4;
         }

         // $FF: synthetic method
         public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
            return this.serialize((ServerStatus.Players)var1, var2, var3);
         }

         // $FF: synthetic method
         public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
            return this.deserialize(var1, var2, var3);
         }
      }
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public ServerStatus deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject var4 = GsonHelper.convertToJsonObject(jsonElement, "status");
         ServerStatus var5 = new ServerStatus();
         if(var4.has("description")) {
            var5.setDescription((Component)jsonDeserializationContext.deserialize(var4.get("description"), Component.class));
         }

         if(var4.has("players")) {
            var5.setPlayers((ServerStatus.Players)jsonDeserializationContext.deserialize(var4.get("players"), ServerStatus.Players.class));
         }

         if(var4.has("version")) {
            var5.setVersion((ServerStatus.Version)jsonDeserializationContext.deserialize(var4.get("version"), ServerStatus.Version.class));
         }

         if(var4.has("favicon")) {
            var5.setFavicon(GsonHelper.getAsString(var4, "favicon"));
         }

         return var5;
      }

      public JsonElement serialize(ServerStatus serverStatus, Type type, JsonSerializationContext jsonSerializationContext) {
         JsonObject var4 = new JsonObject();
         if(serverStatus.getDescription() != null) {
            var4.add("description", jsonSerializationContext.serialize(serverStatus.getDescription()));
         }

         if(serverStatus.getPlayers() != null) {
            var4.add("players", jsonSerializationContext.serialize(serverStatus.getPlayers()));
         }

         if(serverStatus.getVersion() != null) {
            var4.add("version", jsonSerializationContext.serialize(serverStatus.getVersion()));
         }

         if(serverStatus.getFavicon() != null) {
            var4.addProperty("favicon", serverStatus.getFavicon());
         }

         return var4;
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((ServerStatus)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }

   public static class Version {
      private final String name;
      private final int protocol;

      public Version(String name, int protocol) {
         this.name = name;
         this.protocol = protocol;
      }

      public String getName() {
         return this.name;
      }

      public int getProtocol() {
         return this.protocol;
      }

      public static class Serializer implements JsonDeserializer, JsonSerializer {
         public ServerStatus.Version deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject var4 = GsonHelper.convertToJsonObject(jsonElement, "version");
            return new ServerStatus.Version(GsonHelper.getAsString(var4, "name"), GsonHelper.getAsInt(var4, "protocol"));
         }

         public JsonElement serialize(ServerStatus.Version serverStatus$Version, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject var4 = new JsonObject();
            var4.addProperty("name", serverStatus$Version.getName());
            var4.addProperty("protocol", Integer.valueOf(serverStatus$Version.getProtocol()));
            return var4;
         }

         // $FF: synthetic method
         public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
            return this.serialize((ServerStatus.Version)var1, var2, var3);
         }

         // $FF: synthetic method
         public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
            return this.deserialize(var1, var2, var3);
         }
      }
   }
}
