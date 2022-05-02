package com.mojang.realmsclient.util;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import net.minecraft.realms.Realms;
import org.apache.commons.io.FileUtils;

@ClientJarOnly
public class RealmsPersistence {
   public static RealmsPersistence.RealmsPersistenceData readFile() {
      File var0 = new File(Realms.getGameDirectoryPath(), "realms_persistence.json");
      Gson var1 = new Gson();

      try {
         return (RealmsPersistence.RealmsPersistenceData)var1.fromJson(FileUtils.readFileToString(var0), RealmsPersistence.RealmsPersistenceData.class);
      } catch (IOException var3) {
         return new RealmsPersistence.RealmsPersistenceData();
      }
   }

   public static void writeFile(RealmsPersistence.RealmsPersistenceData realmsPersistence$RealmsPersistenceData) {
      File var1 = new File(Realms.getGameDirectoryPath(), "realms_persistence.json");
      Gson var2 = new Gson();
      String var3 = var2.toJson(realmsPersistence$RealmsPersistenceData);

      try {
         FileUtils.writeStringToFile(var1, var3);
      } catch (IOException var5) {
         ;
      }

   }

   @ClientJarOnly
   public static class RealmsPersistenceData {
      public String newsLink;
      public boolean hasUnreadNews;

      private RealmsPersistenceData() {
         this.hasUnreadNews = false;
      }
   }
}
