package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.RealmsServerPlayerList;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class RealmsServerPlayerLists extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public List servers;

   public static RealmsServerPlayerLists parse(String string) {
      RealmsServerPlayerLists realmsServerPlayerLists = new RealmsServerPlayerLists();
      realmsServerPlayerLists.servers = new ArrayList();

      try {
         JsonParser var2 = new JsonParser();
         JsonObject var3 = var2.parse(string).getAsJsonObject();
         if(var3.get("lists").isJsonArray()) {
            JsonArray var4 = var3.get("lists").getAsJsonArray();
            Iterator<JsonElement> var5 = var4.iterator();

            while(var5.hasNext()) {
               realmsServerPlayerLists.servers.add(RealmsServerPlayerList.parse(((JsonElement)var5.next()).getAsJsonObject()));
            }
         }
      } catch (Exception var6) {
         LOGGER.error("Could not parse RealmsServerPlayerLists: " + var6.getMessage());
      }

      return realmsServerPlayerLists;
   }
}
