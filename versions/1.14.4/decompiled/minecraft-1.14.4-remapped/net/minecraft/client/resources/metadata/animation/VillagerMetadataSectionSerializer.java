package net.minecraft.client.resources.metadata.animation;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.metadata.animation.VillagerMetaDataSection;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;

@ClientJarOnly
public class VillagerMetadataSectionSerializer implements MetadataSectionSerializer {
   public VillagerMetaDataSection fromJson(JsonObject json) {
      return new VillagerMetaDataSection(VillagerMetaDataSection.Hat.getByName(GsonHelper.getAsString(json, "hat", "none")));
   }

   public String getMetadataSectionName() {
      return "villager";
   }

   // $FF: synthetic method
   public Object fromJson(JsonObject var1) {
      return this.fromJson(var1);
   }
}
