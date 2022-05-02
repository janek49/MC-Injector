package net.minecraft.client.resources.metadata.animation;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.Validate;

@ClientJarOnly
public class AnimationMetadataSectionSerializer implements MetadataSectionSerializer {
   public AnimationMetadataSection fromJson(JsonObject json) {
      List<AnimationFrame> var2 = Lists.newArrayList();
      int var3 = GsonHelper.getAsInt(json, "frametime", 1);
      if(var3 != 1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)var3, "Invalid default frame time");
      }

      if(json.has("frames")) {
         try {
            JsonArray var4 = GsonHelper.getAsJsonArray(json, "frames");

            for(int var5 = 0; var5 < var4.size(); ++var5) {
               JsonElement var6 = var4.get(var5);
               AnimationFrame var7 = this.getFrame(var5, var6);
               if(var7 != null) {
                  var2.add(var7);
               }
            }
         } catch (ClassCastException var8) {
            throw new JsonParseException("Invalid animation->frames: expected array, was " + json.get("frames"), var8);
         }
      }

      int var4 = GsonHelper.getAsInt(json, "width", -1);
      int var5 = GsonHelper.getAsInt(json, "height", -1);
      if(var4 != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)var4, "Invalid width");
      }

      if(var5 != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)var5, "Invalid height");
      }

      boolean var6 = GsonHelper.getAsBoolean(json, "interpolate", false);
      return new AnimationMetadataSection(var2, var4, var5, var3, var6);
   }

   private AnimationFrame getFrame(int var1, JsonElement jsonElement) {
      if(jsonElement.isJsonPrimitive()) {
         return new AnimationFrame(GsonHelper.convertToInt(jsonElement, "frames[" + var1 + "]"));
      } else if(jsonElement.isJsonObject()) {
         JsonObject var3 = GsonHelper.convertToJsonObject(jsonElement, "frames[" + var1 + "]");
         int var4 = GsonHelper.getAsInt(var3, "time", -1);
         if(var3.has("time")) {
            Validate.inclusiveBetween(1L, 2147483647L, (long)var4, "Invalid frame time");
         }

         int var5 = GsonHelper.getAsInt(var3, "index");
         Validate.inclusiveBetween(0L, 2147483647L, (long)var5, "Invalid frame index");
         return new AnimationFrame(var5, var4);
      } else {
         return null;
      }
   }

   public String getMetadataSectionName() {
      return "animation";
   }

   // $FF: synthetic method
   public Object fromJson(JsonObject var1) {
      return this.fromJson(var1);
   }
}
