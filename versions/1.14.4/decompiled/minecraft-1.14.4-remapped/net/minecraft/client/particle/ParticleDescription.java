package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

@ClientJarOnly
public class ParticleDescription {
   @Nullable
   private final List textures;

   private ParticleDescription(@Nullable List textures) {
      this.textures = textures;
   }

   @Nullable
   public List getTextures() {
      return this.textures;
   }

   public static ParticleDescription fromJson(JsonObject json) {
      JsonArray var1 = GsonHelper.getAsJsonArray(json, "textures", (JsonArray)null);
      List<ResourceLocation> var2;
      if(var1 != null) {
         var2 = (List)Streams.stream(var1).map((jsonElement) -> {
            return GsonHelper.convertToString(jsonElement, "texture");
         }).map(ResourceLocation::<init>).collect(ImmutableList.toImmutableList());
      } else {
         var2 = null;
      }

      return new ParticleDescription(var2);
   }
}
