package net.minecraft.client.resources.metadata.texture;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;

@ClientJarOnly
public class TextureMetadataSectionSerializer implements MetadataSectionSerializer {
   public TextureMetadataSection fromJson(JsonObject json) {
      boolean var2 = GsonHelper.getAsBoolean(json, "blur", false);
      boolean var3 = GsonHelper.getAsBoolean(json, "clamp", false);
      return new TextureMetadataSection(var2, var3);
   }

   public String getMetadataSectionName() {
      return "texture";
   }

   // $FF: synthetic method
   public Object fromJson(JsonObject var1) {
      return this.fromJson(var1);
   }
}
