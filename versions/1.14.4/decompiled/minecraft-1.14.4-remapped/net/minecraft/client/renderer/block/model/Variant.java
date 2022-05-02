package net.minecraft.client.renderer.block.model;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

@ClientJarOnly
public class Variant implements ModelState {
   private final ResourceLocation modelLocation;
   private final BlockModelRotation rotation;
   private final boolean uvLock;
   private final int weight;

   public Variant(ResourceLocation modelLocation, BlockModelRotation rotation, boolean uvLock, int weight) {
      this.modelLocation = modelLocation;
      this.rotation = rotation;
      this.uvLock = uvLock;
      this.weight = weight;
   }

   public ResourceLocation getModelLocation() {
      return this.modelLocation;
   }

   public BlockModelRotation getRotation() {
      return this.rotation;
   }

   public boolean isUvLocked() {
      return this.uvLock;
   }

   public int getWeight() {
      return this.weight;
   }

   public String toString() {
      return "Variant{modelLocation=" + this.modelLocation + ", rotation=" + this.rotation + ", uvLock=" + this.uvLock + ", weight=" + this.weight + '}';
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof Variant)) {
         return false;
      } else {
         Variant var2 = (Variant)object;
         return this.modelLocation.equals(var2.modelLocation) && this.rotation == var2.rotation && this.uvLock == var2.uvLock && this.weight == var2.weight;
      }
   }

   public int hashCode() {
      int var1 = this.modelLocation.hashCode();
      var1 = 31 * var1 + this.rotation.hashCode();
      var1 = 31 * var1 + Boolean.valueOf(this.uvLock).hashCode();
      var1 = 31 * var1 + this.weight;
      return var1;
   }

   @ClientJarOnly
   public static class Deserializer implements JsonDeserializer {
      public Variant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject var4 = jsonElement.getAsJsonObject();
         ResourceLocation var5 = this.getModel(var4);
         BlockModelRotation var6 = this.getBlockRotation(var4);
         boolean var7 = this.getUvLock(var4);
         int var8 = this.getWeight(var4);
         return new Variant(var5, var6, var7, var8);
      }

      private boolean getUvLock(JsonObject jsonObject) {
         return GsonHelper.getAsBoolean(jsonObject, "uvlock", false);
      }

      protected BlockModelRotation getBlockRotation(JsonObject jsonObject) {
         int var2 = GsonHelper.getAsInt(jsonObject, "x", 0);
         int var3 = GsonHelper.getAsInt(jsonObject, "y", 0);
         BlockModelRotation var4 = BlockModelRotation.by(var2, var3);
         if(var4 == null) {
            throw new JsonParseException("Invalid BlockModelRotation x: " + var2 + ", y: " + var3);
         } else {
            return var4;
         }
      }

      protected ResourceLocation getModel(JsonObject jsonObject) {
         return new ResourceLocation(GsonHelper.getAsString(jsonObject, "model"));
      }

      protected int getWeight(JsonObject jsonObject) {
         int var2 = GsonHelper.getAsInt(jsonObject, "weight", 1);
         if(var2 < 1) {
            throw new JsonParseException("Invalid weight " + var2 + " found, expected integer >= 1");
         } else {
            return var2;
         }
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
