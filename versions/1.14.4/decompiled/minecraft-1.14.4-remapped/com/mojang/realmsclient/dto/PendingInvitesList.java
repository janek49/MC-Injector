package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class PendingInvitesList extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public List pendingInvites = Lists.newArrayList();

   public static PendingInvitesList parse(String string) {
      PendingInvitesList pendingInvitesList = new PendingInvitesList();

      try {
         JsonParser var2 = new JsonParser();
         JsonObject var3 = var2.parse(string).getAsJsonObject();
         if(var3.get("invites").isJsonArray()) {
            Iterator<JsonElement> var4 = var3.get("invites").getAsJsonArray().iterator();

            while(var4.hasNext()) {
               pendingInvitesList.pendingInvites.add(PendingInvite.parse(((JsonElement)var4.next()).getAsJsonObject()));
            }
         }
      } catch (Exception var5) {
         LOGGER.error("Could not parse PendingInvitesList: " + var5.getMessage());
      }

      return pendingInvitesList;
   }
}
