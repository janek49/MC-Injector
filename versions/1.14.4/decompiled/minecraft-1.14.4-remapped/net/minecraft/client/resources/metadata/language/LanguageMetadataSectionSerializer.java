package net.minecraft.client.resources.metadata.language;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.resources.language.Language;
import net.minecraft.client.resources.metadata.language.LanguageMetadataSection;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;

@ClientJarOnly
public class LanguageMetadataSectionSerializer implements MetadataSectionSerializer {
   public LanguageMetadataSection fromJson(JsonObject json) {
      Set<Language> var2 = Sets.newHashSet();

      for(Entry<String, JsonElement> var4 : json.entrySet()) {
         String var5 = (String)var4.getKey();
         if(var5.length() > 16) {
            throw new JsonParseException("Invalid language->\'" + var5 + "\': language code must not be more than " + 16 + " characters long");
         }

         JsonObject var6 = GsonHelper.convertToJsonObject((JsonElement)var4.getValue(), "language");
         String var7 = GsonHelper.getAsString(var6, "region");
         String var8 = GsonHelper.getAsString(var6, "name");
         boolean var9 = GsonHelper.getAsBoolean(var6, "bidirectional", false);
         if(var7.isEmpty()) {
            throw new JsonParseException("Invalid language->\'" + var5 + "\'->region: empty value");
         }

         if(var8.isEmpty()) {
            throw new JsonParseException("Invalid language->\'" + var5 + "\'->name: empty value");
         }

         if(!var2.add(new Language(var5, var7, var8, var9))) {
            throw new JsonParseException("Duplicate language->\'" + var5 + "\' defined");
         }
      }

      return new LanguageMetadataSection(var2);
   }

   public String getMetadataSectionName() {
      return "language";
   }

   // $FF: synthetic method
   public Object fromJson(JsonObject var1) {
      return this.fromJson(var1);
   }
}
