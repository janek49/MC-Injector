package net.minecraft.client.renderer.block.model;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Vector3f;
import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

@ClientJarOnly
public class BlockElement {
   public final Vector3f from;
   public final Vector3f to;
   public final Map faces;
   public final BlockElementRotation rotation;
   public final boolean shade;

   public BlockElement(Vector3f from, Vector3f to, Map faces, @Nullable BlockElementRotation rotation, boolean shade) {
      this.from = from;
      this.to = to;
      this.faces = faces;
      this.rotation = rotation;
      this.shade = shade;
      this.fillUvs();
   }

   private void fillUvs() {
      for(Entry<Direction, BlockElementFace> var2 : this.faces.entrySet()) {
         float[] vars3 = this.uvsByFace((Direction)var2.getKey());
         ((BlockElementFace)var2.getValue()).uv.setMissingUv(vars3);
      }

   }

   private float[] uvsByFace(Direction direction) {
      switch(direction) {
      case DOWN:
         return new float[]{this.from.x(), 16.0F - this.to.z(), this.to.x(), 16.0F - this.from.z()};
      case UP:
         return new float[]{this.from.x(), this.from.z(), this.to.x(), this.to.z()};
      case NORTH:
      default:
         return new float[]{16.0F - this.to.x(), 16.0F - this.to.y(), 16.0F - this.from.x(), 16.0F - this.from.y()};
      case SOUTH:
         return new float[]{this.from.x(), 16.0F - this.to.y(), this.to.x(), 16.0F - this.from.y()};
      case WEST:
         return new float[]{this.from.z(), 16.0F - this.to.y(), this.to.z(), 16.0F - this.from.y()};
      case EAST:
         return new float[]{16.0F - this.to.z(), 16.0F - this.to.y(), 16.0F - this.from.z(), 16.0F - this.from.y()};
      }
   }

   @ClientJarOnly
   public static class Deserializer implements JsonDeserializer {
      public BlockElement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject var4 = jsonElement.getAsJsonObject();
         Vector3f var5 = this.getFrom(var4);
         Vector3f var6 = this.getTo(var4);
         BlockElementRotation var7 = this.getRotation(var4);
         Map<Direction, BlockElementFace> var8 = this.getFaces(jsonDeserializationContext, var4);
         if(var4.has("shade") && !GsonHelper.isBooleanValue(var4, "shade")) {
            throw new JsonParseException("Expected shade to be a Boolean");
         } else {
            boolean var9 = GsonHelper.getAsBoolean(var4, "shade", true);
            return new BlockElement(var5, var6, var8, var7, var9);
         }
      }

      @Nullable
      private BlockElementRotation getRotation(JsonObject jsonObject) {
         BlockElementRotation blockElementRotation = null;
         if(jsonObject.has("rotation")) {
            JsonObject var3 = GsonHelper.getAsJsonObject(jsonObject, "rotation");
            Vector3f var4 = this.getVector3f(var3, "origin");
            var4.mul(0.0625F);
            Direction.Axis var5 = this.getAxis(var3);
            float var6 = this.getAngle(var3);
            boolean var7 = GsonHelper.getAsBoolean(var3, "rescale", false);
            blockElementRotation = new BlockElementRotation(var4, var5, var6, var7);
         }

         return blockElementRotation;
      }

      private float getAngle(JsonObject jsonObject) {
         float var2 = GsonHelper.getAsFloat(jsonObject, "angle");
         if(var2 != 0.0F && Mth.abs(var2) != 22.5F && Mth.abs(var2) != 45.0F) {
            throw new JsonParseException("Invalid rotation " + var2 + " found, only -45/-22.5/0/22.5/45 allowed");
         } else {
            return var2;
         }
      }

      private Direction.Axis getAxis(JsonObject jsonObject) {
         String var2 = GsonHelper.getAsString(jsonObject, "axis");
         Direction.Axis var3 = Direction.Axis.byName(var2.toLowerCase(Locale.ROOT));
         if(var3 == null) {
            throw new JsonParseException("Invalid rotation axis: " + var2);
         } else {
            return var3;
         }
      }

      private Map getFaces(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
         Map<Direction, BlockElementFace> map = this.filterNullFromFaces(jsonDeserializationContext, jsonObject);
         if(map.isEmpty()) {
            throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
         } else {
            return map;
         }
      }

      private Map filterNullFromFaces(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject) {
         Map<Direction, BlockElementFace> map = Maps.newEnumMap(Direction.class);
         JsonObject var4 = GsonHelper.getAsJsonObject(jsonObject, "faces");

         for(Entry<String, JsonElement> var6 : var4.entrySet()) {
            Direction var7 = this.getFacing((String)var6.getKey());
            map.put(var7, jsonDeserializationContext.deserialize((JsonElement)var6.getValue(), BlockElementFace.class));
         }

         return map;
      }

      private Direction getFacing(String string) {
         Direction direction = Direction.byName(string);
         if(direction == null) {
            throw new JsonParseException("Unknown facing: " + string);
         } else {
            return direction;
         }
      }

      private Vector3f getTo(JsonObject jsonObject) {
         Vector3f vector3f = this.getVector3f(jsonObject, "to");
         if(vector3f.x() >= -16.0F && vector3f.y() >= -16.0F && vector3f.z() >= -16.0F && vector3f.x() <= 32.0F && vector3f.y() <= 32.0F && vector3f.z() <= 32.0F) {
            return vector3f;
         } else {
            throw new JsonParseException("\'to\' specifier exceeds the allowed boundaries: " + vector3f);
         }
      }

      private Vector3f getFrom(JsonObject jsonObject) {
         Vector3f vector3f = this.getVector3f(jsonObject, "from");
         if(vector3f.x() >= -16.0F && vector3f.y() >= -16.0F && vector3f.z() >= -16.0F && vector3f.x() <= 32.0F && vector3f.y() <= 32.0F && vector3f.z() <= 32.0F) {
            return vector3f;
         } else {
            throw new JsonParseException("\'from\' specifier exceeds the allowed boundaries: " + vector3f);
         }
      }

      private Vector3f getVector3f(JsonObject jsonObject, String string) {
         JsonArray var3 = GsonHelper.getAsJsonArray(jsonObject, string);
         if(var3.size() != 3) {
            throw new JsonParseException("Expected 3 " + string + " values, found: " + var3.size());
         } else {
            float[] vars4 = new float[3];

            for(int var5 = 0; var5 < vars4.length; ++var5) {
               vars4[var5] = GsonHelper.convertToFloat(var3.get(var5), string + "[" + var5 + "]");
            }

            return new Vector3f(vars4[0], vars4[1], vars4[2]);
         }
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
