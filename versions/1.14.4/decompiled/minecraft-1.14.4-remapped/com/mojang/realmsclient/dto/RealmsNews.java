package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class RealmsNews extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String newsLink;

   public static RealmsNews parse(String string) {
      RealmsNews realmsNews = new RealmsNews();

      try {
         JsonParser var2 = new JsonParser();
         JsonObject var3 = var2.parse(string).getAsJsonObject();
         realmsNews.newsLink = JsonUtils.getStringOr("newsLink", var3, (String)null);
      } catch (Exception var4) {
         LOGGER.error("Could not parse RealmsNews: " + var4.getMessage());
      }

      return realmsNews;
   }
}
