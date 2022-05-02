package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class RealmsServerAddress extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String address;
   public String resourcePackUrl;
   public String resourcePackHash;

   public static RealmsServerAddress parse(String string) {
      JsonParser var1 = new JsonParser();
      RealmsServerAddress var2 = new RealmsServerAddress();

      try {
         JsonObject var3 = var1.parse(string).getAsJsonObject();
         var2.address = JsonUtils.getStringOr("address", var3, (String)null);
         var2.resourcePackUrl = JsonUtils.getStringOr("resourcePackUrl", var3, (String)null);
         var2.resourcePackHash = JsonUtils.getStringOr("resourcePackHash", var3, (String)null);
      } catch (Exception var4) {
         LOGGER.error("Could not parse RealmsServerAddress: " + var4.getMessage());
      }

      return var2;
   }
}
