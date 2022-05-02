package net.minecraft.server.packs.metadata.pack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.GsonHelper;

public class PackMetadataSectionSerializer implements MetadataSectionSerializer {
   public PackMetadataSection fromJson(JsonObject json) {
      Component var2 = Component.Serializer.fromJson(json.get("description"));
      if(var2 == null) {
         throw new JsonParseException("Invalid/missing description!");
      } else {
         int var3 = GsonHelper.getAsInt(json, "pack_format");
         return new PackMetadataSection(var2, var3);
      }
   }

   public String getMetadataSectionName() {
      return "pack";
   }

   // $FF: synthetic method
   public Object fromJson(JsonObject var1) {
      return this.fromJson(var1);
   }
}
