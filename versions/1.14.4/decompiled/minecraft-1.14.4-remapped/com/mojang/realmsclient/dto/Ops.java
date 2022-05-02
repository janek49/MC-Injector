package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.HashSet;
import java.util.Set;

@ClientJarOnly
public class Ops extends ValueObject {
   public Set ops = new HashSet();

   public static Ops parse(String string) {
      Ops ops = new Ops();
      JsonParser var2 = new JsonParser();

      try {
         JsonElement var3 = var2.parse(string);
         JsonObject var4 = var3.getAsJsonObject();
         JsonElement var5 = var4.get("ops");
         if(var5.isJsonArray()) {
            for(JsonElement var7 : var5.getAsJsonArray()) {
               ops.ops.add(var7.getAsString());
            }
         }
      } catch (Exception var8) {
         ;
      }

      return ops;
   }
}
