package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class WorldTemplate extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String id;
   public String name;
   public String version;
   public String author;
   public String link;
   public String image;
   public String trailer;
   public String recommendedPlayers;
   public WorldTemplate.WorldTemplateType type;

   public static WorldTemplate parse(JsonObject jsonObject) {
      WorldTemplate worldTemplate = new WorldTemplate();

      try {
         worldTemplate.id = JsonUtils.getStringOr("id", jsonObject, "");
         worldTemplate.name = JsonUtils.getStringOr("name", jsonObject, "");
         worldTemplate.version = JsonUtils.getStringOr("version", jsonObject, "");
         worldTemplate.author = JsonUtils.getStringOr("author", jsonObject, "");
         worldTemplate.link = JsonUtils.getStringOr("link", jsonObject, "");
         worldTemplate.image = JsonUtils.getStringOr("image", jsonObject, (String)null);
         worldTemplate.trailer = JsonUtils.getStringOr("trailer", jsonObject, "");
         worldTemplate.recommendedPlayers = JsonUtils.getStringOr("recommendedPlayers", jsonObject, "");
         worldTemplate.type = WorldTemplate.WorldTemplateType.valueOf(JsonUtils.getStringOr("type", jsonObject, WorldTemplate.WorldTemplateType.WORLD_TEMPLATE.name()));
      } catch (Exception var3) {
         LOGGER.error("Could not parse WorldTemplate: " + var3.getMessage());
      }

      return worldTemplate;
   }

   @ClientJarOnly
   public static enum WorldTemplateType {
      WORLD_TEMPLATE,
      MINIGAME,
      ADVENTUREMAP,
      EXPERIENCE,
      INSPIRATION;
   }
}
