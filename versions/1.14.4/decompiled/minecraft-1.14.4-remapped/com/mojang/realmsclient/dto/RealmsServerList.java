package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class RealmsServerList extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public List servers;

   public static RealmsServerList parse(String string) {
      RealmsServerList realmsServerList = new RealmsServerList();
      realmsServerList.servers = new ArrayList();

      try {
         JsonParser var2 = new JsonParser();
         JsonObject var3 = var2.parse(string).getAsJsonObject();
         if(var3.get("servers").isJsonArray()) {
            JsonArray var4 = var3.get("servers").getAsJsonArray();
            Iterator<JsonElement> var5 = var4.iterator();

            while(var5.hasNext()) {
               realmsServerList.servers.add(RealmsServer.parse(((JsonElement)var5.next()).getAsJsonObject()));
            }
         }
      } catch (Exception var6) {
         LOGGER.error("Could not parse McoServerList: " + var6.getMessage());
      }

      return realmsServerList;
   }
}
