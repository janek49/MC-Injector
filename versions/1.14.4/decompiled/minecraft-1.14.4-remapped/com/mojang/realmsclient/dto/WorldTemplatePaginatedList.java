package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class WorldTemplatePaginatedList extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public List templates;
   public int page;
   public int size;
   public int total;

   public WorldTemplatePaginatedList() {
   }

   public WorldTemplatePaginatedList(int size) {
      this.templates = Collections.emptyList();
      this.page = 0;
      this.size = size;
      this.total = -1;
   }

   public boolean isLastPage() {
      return this.page * this.size >= this.total && this.page > 0 && this.total > 0 && this.size > 0;
   }

   public static WorldTemplatePaginatedList parse(String string) {
      WorldTemplatePaginatedList worldTemplatePaginatedList = new WorldTemplatePaginatedList();
      worldTemplatePaginatedList.templates = new ArrayList();

      try {
         JsonParser var2 = new JsonParser();
         JsonObject var3 = var2.parse(string).getAsJsonObject();
         if(var3.get("templates").isJsonArray()) {
            Iterator<JsonElement> var4 = var3.get("templates").getAsJsonArray().iterator();

            while(var4.hasNext()) {
               worldTemplatePaginatedList.templates.add(WorldTemplate.parse(((JsonElement)var4.next()).getAsJsonObject()));
            }
         }

         worldTemplatePaginatedList.page = JsonUtils.getIntOr("page", var3, 0);
         worldTemplatePaginatedList.size = JsonUtils.getIntOr("size", var3, 0);
         worldTemplatePaginatedList.total = JsonUtils.getIntOr("total", var3, 0);
      } catch (Exception var5) {
         LOGGER.error("Could not parse WorldTemplatePaginatedList: " + var5.getMessage());
      }

      return worldTemplatePaginatedList;
   }
}
