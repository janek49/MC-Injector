package net.minecraft.client.renderer.block.model;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;

@ClientJarOnly
public class BlockFaceUV {
   public float[] uvs;
   public final int rotation;

   public BlockFaceUV(@Nullable float[] uvs, int rotation) {
      this.uvs = uvs;
      this.rotation = rotation;
   }

   public float getU(int i) {
      if(this.uvs == null) {
         throw new NullPointerException("uvs");
      } else {
         int var2 = this.getShiftedIndex(i);
         return this.uvs[var2 != 0 && var2 != 1?2:0];
      }
   }

   public float getV(int i) {
      if(this.uvs == null) {
         throw new NullPointerException("uvs");
      } else {
         int var2 = this.getShiftedIndex(i);
         return this.uvs[var2 != 0 && var2 != 3?3:1];
      }
   }

   private int getShiftedIndex(int i) {
      return (i + this.rotation / 90) % 4;
   }

   public int getReverseIndex(int i) {
      return (i + 4 - this.rotation / 90) % 4;
   }

   public void setMissingUv(float[] missingUv) {
      if(this.uvs == null) {
         this.uvs = missingUv;
      }

   }

   @ClientJarOnly
   public static class Deserializer implements JsonDeserializer {
      public BlockFaceUV deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject var4 = jsonElement.getAsJsonObject();
         float[] vars5 = this.getUVs(var4);
         int var6 = this.getRotation(var4);
         return new BlockFaceUV(vars5, var6);
      }

      protected int getRotation(JsonObject jsonObject) {
         int var2 = GsonHelper.getAsInt(jsonObject, "rotation", 0);
         if(var2 >= 0 && var2 % 90 == 0 && var2 / 90 <= 3) {
            return var2;
         } else {
            throw new JsonParseException("Invalid rotation " + var2 + " found, only 0/90/180/270 allowed");
         }
      }

      @Nullable
      private float[] getUVs(JsonObject jsonObject) {
         if(!jsonObject.has("uv")) {
            return null;
         } else {
            JsonArray var2 = GsonHelper.getAsJsonArray(jsonObject, "uv");
            if(var2.size() != 4) {
               throw new JsonParseException("Expected 4 uv values, found: " + var2.size());
            } else {
               float[] vars3 = new float[4];

               for(int var4 = 0; var4 < vars3.length; ++var4) {
                  vars3[var4] = GsonHelper.convertToFloat(var2.get(var4), "uv[" + var4 + "]");
               }

               return vars3;
            }
         }
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
