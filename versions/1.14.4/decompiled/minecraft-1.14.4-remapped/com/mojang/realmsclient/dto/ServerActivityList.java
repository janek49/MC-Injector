package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.ServerActivity;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.ArrayList;
import java.util.List;

@ClientJarOnly
public class ServerActivityList extends ValueObject {
   public long periodInMillis;
   public List serverActivities = new ArrayList();

   public static ServerActivityList parse(String string) {
      ServerActivityList serverActivityList = new ServerActivityList();
      JsonParser var2 = new JsonParser();

      try {
         JsonElement var3 = var2.parse(string);
         JsonObject var4 = var3.getAsJsonObject();
         serverActivityList.periodInMillis = JsonUtils.getLongOr("periodInMillis", var4, -1L);
         JsonElement var5 = var4.get("playerActivityDto");
         if(var5 != null && var5.isJsonArray()) {
            for(JsonElement var8 : var5.getAsJsonArray()) {
               ServerActivity var9 = ServerActivity.parse(var8.getAsJsonObject());
               serverActivityList.serverActivities.add(var9);
            }
         }
      } catch (Exception var10) {
         ;
      }

      return serverActivityList;
   }
}
