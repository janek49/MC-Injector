package net.minecraft.resources;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.lang.reflect.Type;
import java.util.function.IntPredicate;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.StringUtils;

public class ResourceLocation implements Comparable {
   private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(new TranslatableComponent("argument.id.invalid", new Object[0]));
   protected final String namespace;
   protected final String path;

   protected ResourceLocation(String[] strings) {
      this.namespace = StringUtils.isEmpty(strings[0])?"minecraft":strings[0];
      this.path = strings[1];
      if(!isValidNamespace(this.namespace)) {
         throw new ResourceLocationException("Non [a-z0-9_.-] character in namespace of location: " + this.namespace + ':' + this.path);
      } else if(!isValidPath(this.path)) {
         throw new ResourceLocationException("Non [a-z0-9/._-] character in path of location: " + this.namespace + ':' + this.path);
      }
   }

   public ResourceLocation(String string) {
      this(decompose(string, ':'));
   }

   public ResourceLocation(String var1, String var2) {
      this(new String[]{var1, var2});
   }

   public static ResourceLocation of(String string, char var1) {
      return new ResourceLocation(decompose(string, var1));
   }

   @Nullable
   public static ResourceLocation tryParse(String string) {
      try {
         return new ResourceLocation(string);
      } catch (ResourceLocationException var2) {
         return null;
      }
   }

   protected static String[] decompose(String var0, char var1) {
      String[] strings = new String[]{"minecraft", var0};
      int var3 = var0.indexOf(var1);
      if(var3 >= 0) {
         strings[1] = var0.substring(var3 + 1, var0.length());
         if(var3 >= 1) {
            strings[0] = var0.substring(0, var3);
         }
      }

      return strings;
   }

   public String getPath() {
      return this.path;
   }

   public String getNamespace() {
      return this.namespace;
   }

   public String toString() {
      return this.namespace + ':' + this.path;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof ResourceLocation)) {
         return false;
      } else {
         ResourceLocation var2 = (ResourceLocation)object;
         return this.namespace.equals(var2.namespace) && this.path.equals(var2.path);
      }
   }

   public int hashCode() {
      return 31 * this.namespace.hashCode() + this.path.hashCode();
   }

   public int compareTo(ResourceLocation resourceLocation) {
      int var2 = this.path.compareTo(resourceLocation.path);
      if(var2 == 0) {
         var2 = this.namespace.compareTo(resourceLocation.namespace);
      }

      return var2;
   }

   public static ResourceLocation read(StringReader stringReader) throws CommandSyntaxException {
      int var1 = stringReader.getCursor();

      while(stringReader.canRead() && isAllowedInResourceLocation(stringReader.peek())) {
         stringReader.skip();
      }

      String var2 = stringReader.getString().substring(var1, stringReader.getCursor());

      try {
         return new ResourceLocation(var2);
      } catch (ResourceLocationException var4) {
         stringReader.setCursor(var1);
         throw ERROR_INVALID.createWithContext(stringReader);
      }
   }

   public static boolean isAllowedInResourceLocation(char c) {
      return c >= 48 && c <= 57 || c >= 97 && c <= 122 || c == 95 || c == 58 || c == 47 || c == 46 || c == 45;
   }

   private static boolean isValidPath(String string) {
      return string.chars().allMatch((i) -> {
         return i == 95 || i == 45 || i >= 97 && i <= 122 || i >= 48 && i <= 57 || i == 47 || i == 46;
      });
   }

   private static boolean isValidNamespace(String string) {
      return string.chars().allMatch((i) -> {
         return i == 95 || i == 45 || i >= 97 && i <= 122 || i >= 48 && i <= 57 || i == 46;
      });
   }

   public static boolean isValidResourceLocation(String string) {
      String[] vars1 = decompose(string, ':');
      return isValidNamespace(StringUtils.isEmpty(vars1[0])?"minecraft":vars1[0]) && isValidPath(vars1[1]);
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((ResourceLocation)var1);
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public ResourceLocation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         return new ResourceLocation(GsonHelper.convertToString(jsonElement, "location"));
      }

      public JsonElement serialize(ResourceLocation resourceLocation, Type type, JsonSerializationContext jsonSerializationContext) {
         return new JsonPrimitive(resourceLocation.toString());
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((ResourceLocation)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
