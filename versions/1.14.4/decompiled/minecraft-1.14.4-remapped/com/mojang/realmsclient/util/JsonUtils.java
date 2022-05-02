package com.mojang.realmsclient.util;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Date;

@ClientJarOnly
public class JsonUtils {
   public static String getStringOr(String var0, JsonObject jsonObject, String var2) {
      JsonElement var3 = jsonObject.get(var0);
      return var3 != null?(var3.isJsonNull()?var2:var3.getAsString()):var2;
   }

   public static int getIntOr(String string, JsonObject jsonObject, int var2) {
      JsonElement var3 = jsonObject.get(string);
      return var3 != null?(var3.isJsonNull()?var2:var3.getAsInt()):var2;
   }

   public static long getLongOr(String string, JsonObject jsonObject, long var2) {
      JsonElement var4 = jsonObject.get(string);
      return var4 != null?(var4.isJsonNull()?var2:var4.getAsLong()):var2;
   }

   public static boolean getBooleanOr(String string, JsonObject jsonObject, boolean var2) {
      JsonElement var3 = jsonObject.get(string);
      return var3 != null?(var3.isJsonNull()?var2:var3.getAsBoolean()):var2;
   }

   public static Date getDateOr(String string, JsonObject jsonObject) {
      JsonElement var2 = jsonObject.get(string);
      return var2 != null?new Date(Long.parseLong(var2.getAsString())):new Date();
   }
}
