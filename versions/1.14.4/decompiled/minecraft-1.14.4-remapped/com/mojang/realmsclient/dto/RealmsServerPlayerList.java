package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class RealmsServerPlayerList extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final JsonParser jsonParser = new JsonParser();
   public long serverId;
   public List players;

   public static RealmsServerPlayerList parse(JsonObject jsonObject) {
      RealmsServerPlayerList realmsServerPlayerList = new RealmsServerPlayerList();

      try {
         realmsServerPlayerList.serverId = JsonUtils.getLongOr("serverId", jsonObject, -1L);
         String var2 = JsonUtils.getStringOr("playerList", jsonObject, (String)null);
         if(var2 != null) {
            JsonElement var3 = jsonParser.parse(var2);
            if(var3.isJsonArray()) {
               realmsServerPlayerList.players = parsePlayers(var3.getAsJsonArray());
            } else {
               realmsServerPlayerList.players = new ArrayList();
            }
         } else {
            realmsServerPlayerList.players = new ArrayList();
         }
      } catch (Exception var4) {
         LOGGER.error("Could not parse RealmsServerPlayerList: " + var4.getMessage());
      }

      return realmsServerPlayerList;
   }

   private static List parsePlayers(JsonArray jsonArray) {
      ArrayList<String> var1 = new ArrayList();

      for(JsonElement var3 : jsonArray) {
         try {
            var1.add(var3.getAsString());
         } catch (Exception var5) {
            ;
         }
      }

      return var1;
   }
}
