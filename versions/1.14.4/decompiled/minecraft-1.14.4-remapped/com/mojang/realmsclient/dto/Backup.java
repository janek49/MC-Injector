package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class Backup extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String backupId;
   public Date lastModifiedDate;
   public long size;
   private boolean uploadedVersion;
   public Map metadata = new HashMap();
   public Map changeList = new HashMap();

   public static Backup parse(JsonElement jsonElement) {
      JsonObject var1 = jsonElement.getAsJsonObject();
      Backup var2 = new Backup();

      try {
         var2.backupId = JsonUtils.getStringOr("backupId", var1, "");
         var2.lastModifiedDate = JsonUtils.getDateOr("lastModifiedDate", var1);
         var2.size = JsonUtils.getLongOr("size", var1, 0L);
         if(var1.has("metadata")) {
            JsonObject var3 = var1.getAsJsonObject("metadata");

            for(Entry<String, JsonElement> var6 : var3.entrySet()) {
               if(!((JsonElement)var6.getValue()).isJsonNull()) {
                  var2.metadata.put(format((String)var6.getKey()), ((JsonElement)var6.getValue()).getAsString());
               }
            }
         }
      } catch (Exception var7) {
         LOGGER.error("Could not parse Backup: " + var7.getMessage());
      }

      return var2;
   }

   private static String format(String string) {
      String[] vars1 = string.split("_");
      StringBuilder var2 = new StringBuilder();

      for(String var6 : vars1) {
         if(var6 != null && var6.length() >= 1) {
            if("of".equals(var6)) {
               var2.append(var6).append(" ");
            } else {
               char var7 = Character.toUpperCase(var6.charAt(0));
               var2.append(var7).append(var6.substring(1, var6.length())).append(" ");
            }
         }
      }

      return var2.toString();
   }

   public boolean isUploadedVersion() {
      return this.uploadedVersion;
   }

   public void setUploadedVersion(boolean uploadedVersion) {
      this.uploadedVersion = uploadedVersion;
   }
}
