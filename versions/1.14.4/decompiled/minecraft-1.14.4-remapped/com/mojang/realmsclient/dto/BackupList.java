package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class BackupList extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public List backups;

   public static BackupList parse(String string) {
      JsonParser var1 = new JsonParser();
      BackupList var2 = new BackupList();
      var2.backups = new ArrayList();

      try {
         JsonElement var3 = var1.parse(string).getAsJsonObject().get("backups");
         if(var3.isJsonArray()) {
            Iterator<JsonElement> var4 = var3.getAsJsonArray().iterator();

            while(var4.hasNext()) {
               var2.backups.add(Backup.parse((JsonElement)var4.next()));
            }
         }
      } catch (Exception var5) {
         LOGGER.error("Could not parse BackupList: " + var5.getMessage());
      }

      return var2;
   }
}
