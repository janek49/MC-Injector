package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.StringUtils;

public class GsonHelper {
   private static final Gson GSON = (new GsonBuilder()).create();

   public static boolean isStringValue(JsonObject jsonObject, String string) {
      return !isValidPrimitive(jsonObject, string)?false:jsonObject.getAsJsonPrimitive(string).isString();
   }

   public static boolean isStringValue(JsonElement jsonElement) {
      return !jsonElement.isJsonPrimitive()?false:jsonElement.getAsJsonPrimitive().isString();
   }

   public static boolean isNumberValue(JsonElement jsonElement) {
      return !jsonElement.isJsonPrimitive()?false:jsonElement.getAsJsonPrimitive().isNumber();
   }

   public static boolean isBooleanValue(JsonObject jsonObject, String string) {
      return !isValidPrimitive(jsonObject, string)?false:jsonObject.getAsJsonPrimitive(string).isBoolean();
   }

   public static boolean isArrayNode(JsonObject jsonObject, String string) {
      return !isValidNode(jsonObject, string)?false:jsonObject.get(string).isJsonArray();
   }

   public static boolean isValidPrimitive(JsonObject jsonObject, String string) {
      return !isValidNode(jsonObject, string)?false:jsonObject.get(string).isJsonPrimitive();
   }

   public static boolean isValidNode(JsonObject jsonObject, String string) {
      return jsonObject == null?false:jsonObject.get(string) != null;
   }

   public static String convertToString(JsonElement jsonElement, String var1) {
      if(jsonElement.isJsonPrimitive()) {
         return jsonElement.getAsString();
      } else {
         throw new JsonSyntaxException("Expected " + var1 + " to be a string, was " + getType(jsonElement));
      }
   }

   public static String getAsString(JsonObject jsonObject, String var1) {
      if(jsonObject.has(var1)) {
         return convertToString(jsonObject.get(var1), var1);
      } else {
         throw new JsonSyntaxException("Missing " + var1 + ", expected to find a string");
      }
   }

   public static String getAsString(JsonObject jsonObject, String var1, String var2) {
      return jsonObject.has(var1)?convertToString(jsonObject.get(var1), var1):var2;
   }

   public static Item convertToItem(JsonElement jsonElement, String string) {
      if(jsonElement.isJsonPrimitive()) {
         String string = jsonElement.getAsString();
         return (Item)Registry.ITEM.getOptional(new ResourceLocation(string)).orElseThrow(() -> {
            return new JsonSyntaxException("Expected " + string + " to be an item, was unknown string \'" + string + "\'");
         });
      } else {
         throw new JsonSyntaxException("Expected " + string + " to be an item, was " + getType(jsonElement));
      }
   }

   public static Item getAsItem(JsonObject jsonObject, String string) {
      if(jsonObject.has(string)) {
         return convertToItem(jsonObject.get(string), string);
      } else {
         throw new JsonSyntaxException("Missing " + string + ", expected to find an item");
      }
   }

   public static boolean convertToBoolean(JsonElement jsonElement, String string) {
      if(jsonElement.isJsonPrimitive()) {
         return jsonElement.getAsBoolean();
      } else {
         throw new JsonSyntaxException("Expected " + string + " to be a Boolean, was " + getType(jsonElement));
      }
   }

   public static boolean getAsBoolean(JsonObject jsonObject, String string) {
      if(jsonObject.has(string)) {
         return convertToBoolean(jsonObject.get(string), string);
      } else {
         throw new JsonSyntaxException("Missing " + string + ", expected to find a Boolean");
      }
   }

   public static boolean getAsBoolean(JsonObject jsonObject, String string, boolean var2) {
      return jsonObject.has(string)?convertToBoolean(jsonObject.get(string), string):var2;
   }

   public static float convertToFloat(JsonElement jsonElement, String string) {
      if(jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
         return jsonElement.getAsFloat();
      } else {
         throw new JsonSyntaxException("Expected " + string + " to be a Float, was " + getType(jsonElement));
      }
   }

   public static float getAsFloat(JsonObject jsonObject, String string) {
      if(jsonObject.has(string)) {
         return convertToFloat(jsonObject.get(string), string);
      } else {
         throw new JsonSyntaxException("Missing " + string + ", expected to find a Float");
      }
   }

   public static float getAsFloat(JsonObject jsonObject, String string, float var2) {
      return jsonObject.has(string)?convertToFloat(jsonObject.get(string), string):var2;
   }

   public static long convertToLong(JsonElement jsonElement, String string) {
      if(jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
         return jsonElement.getAsLong();
      } else {
         throw new JsonSyntaxException("Expected " + string + " to be a Long, was " + getType(jsonElement));
      }
   }

   public static long getAsLong(JsonObject jsonObject, String string, long var2) {
      return jsonObject.has(string)?convertToLong(jsonObject.get(string), string):var2;
   }

   public static int convertToInt(JsonElement jsonElement, String string) {
      if(jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
         return jsonElement.getAsInt();
      } else {
         throw new JsonSyntaxException("Expected " + string + " to be a Int, was " + getType(jsonElement));
      }
   }

   public static int getAsInt(JsonObject jsonObject, String string) {
      if(jsonObject.has(string)) {
         return convertToInt(jsonObject.get(string), string);
      } else {
         throw new JsonSyntaxException("Missing " + string + ", expected to find a Int");
      }
   }

   public static int getAsInt(JsonObject jsonObject, String string, int var2) {
      return jsonObject.has(string)?convertToInt(jsonObject.get(string), string):var2;
   }

   public static byte convertToByte(JsonElement jsonElement, String string) {
      if(jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
         return jsonElement.getAsByte();
      } else {
         throw new JsonSyntaxException("Expected " + string + " to be a Byte, was " + getType(jsonElement));
      }
   }

   public static byte getAsByte(JsonObject jsonObject, String string, byte var2) {
      return jsonObject.has(string)?convertToByte(jsonObject.get(string), string):var2;
   }

   public static JsonObject convertToJsonObject(JsonElement jsonElement, String string) {
      if(jsonElement.isJsonObject()) {
         return jsonElement.getAsJsonObject();
      } else {
         throw new JsonSyntaxException("Expected " + string + " to be a JsonObject, was " + getType(jsonElement));
      }
   }

   public static JsonObject getAsJsonObject(JsonObject var0, String string) {
      if(var0.has(string)) {
         return convertToJsonObject(var0.get(string), string);
      } else {
         throw new JsonSyntaxException("Missing " + string + ", expected to find a JsonObject");
      }
   }

   public static JsonObject getAsJsonObject(JsonObject var0, String string, JsonObject var2) {
      return var0.has(string)?convertToJsonObject(var0.get(string), string):var2;
   }

   public static JsonArray convertToJsonArray(JsonElement jsonElement, String string) {
      if(jsonElement.isJsonArray()) {
         return jsonElement.getAsJsonArray();
      } else {
         throw new JsonSyntaxException("Expected " + string + " to be a JsonArray, was " + getType(jsonElement));
      }
   }

   public static JsonArray getAsJsonArray(JsonObject jsonObject, String string) {
      if(jsonObject.has(string)) {
         return convertToJsonArray(jsonObject.get(string), string);
      } else {
         throw new JsonSyntaxException("Missing " + string + ", expected to find a JsonArray");
      }
   }

   public static JsonArray getAsJsonArray(JsonObject jsonObject, String string, @Nullable JsonArray var2) {
      return jsonObject.has(string)?convertToJsonArray(jsonObject.get(string), string):var2;
   }

   public static Object convertToObject(@Nullable JsonElement jsonElement, String string, JsonDeserializationContext jsonDeserializationContext, Class class) {
      if(jsonElement != null) {
         return jsonDeserializationContext.deserialize(jsonElement, class);
      } else {
         throw new JsonSyntaxException("Missing " + string);
      }
   }

   public static Object getAsObject(JsonObject jsonObject, String string, JsonDeserializationContext jsonDeserializationContext, Class class) {
      if(jsonObject.has(string)) {
         return convertToObject(jsonObject.get(string), string, jsonDeserializationContext, class);
      } else {
         throw new JsonSyntaxException("Missing " + string);
      }
   }

   public static Object getAsObject(JsonObject jsonObject, String string, Object var2, JsonDeserializationContext jsonDeserializationContext, Class class) {
      return jsonObject.has(string)?convertToObject(jsonObject.get(string), string, jsonDeserializationContext, class):var2;
   }

   public static String getType(JsonElement jsonElement) {
      String string = StringUtils.abbreviateMiddle(String.valueOf(jsonElement), "...", 10);
      if(jsonElement == null) {
         return "null (missing)";
      } else if(jsonElement.isJsonNull()) {
         return "null (json)";
      } else if(jsonElement.isJsonArray()) {
         return "an array (" + string + ")";
      } else if(jsonElement.isJsonObject()) {
         return "an object (" + string + ")";
      } else {
         if(jsonElement.isJsonPrimitive()) {
            JsonPrimitive var2 = jsonElement.getAsJsonPrimitive();
            if(var2.isNumber()) {
               return "a number (" + string + ")";
            }

            if(var2.isBoolean()) {
               return "a boolean (" + string + ")";
            }
         }

         return string;
      }
   }

   @Nullable
   public static Object fromJson(Gson gson, Reader reader, Class class, boolean var3) {
      try {
         JsonReader var4 = new JsonReader(reader);
         var4.setLenient(var3);
         return gson.getAdapter(class).read(var4);
      } catch (IOException var5) {
         throw new JsonParseException(var5);
      }
   }

   @Nullable
   public static Object fromJson(Gson gson, Reader reader, Type type, boolean var3) {
      try {
         JsonReader var4 = new JsonReader(reader);
         var4.setLenient(var3);
         return gson.getAdapter(TypeToken.get(type)).read(var4);
      } catch (IOException var5) {
         throw new JsonParseException(var5);
      }
   }

   @Nullable
   public static Object fromJson(Gson gson, String string, Type type, boolean var3) {
      return fromJson(gson, (Reader)(new StringReader(string)), (Type)type, var3);
   }

   @Nullable
   public static Object fromJson(Gson gson, String string, Class class, boolean var3) {
      return fromJson(gson, (Reader)(new StringReader(string)), (Class)class, var3);
   }

   @Nullable
   public static Object fromJson(Gson gson, Reader reader, Type type) {
      return fromJson(gson, reader, type, false);
   }

   @Nullable
   public static Object fromJson(Gson gson, String string, Type type) {
      return fromJson(gson, string, type, false);
   }

   @Nullable
   public static Object fromJson(Gson gson, Reader reader, Class class) {
      return fromJson(gson, reader, class, false);
   }

   @Nullable
   public static Object fromJson(Gson gson, String string, Class class) {
      return fromJson(gson, string, class, false);
   }

   public static JsonObject parse(String string, boolean var1) {
      return parse((Reader)(new StringReader(string)), var1);
   }

   public static JsonObject parse(Reader reader, boolean var1) {
      return (JsonObject)fromJson(GSON, reader, JsonObject.class, var1);
   }

   public static JsonObject parse(String string) {
      return parse(string, false);
   }

   public static JsonObject parse(Reader reader) {
      return parse(reader, false);
   }
}
