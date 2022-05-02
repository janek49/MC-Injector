package net.minecraft.util.datafix.fixes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.lang.reflect.Type;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;
import org.apache.commons.lang3.StringUtils;

public class BlockEntitySignTextStrictJsonFix extends NamedEntityFix {
   public static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(Component.class, new JsonDeserializer() {
      public Component deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         if(jsonElement.isJsonPrimitive()) {
            return new TextComponent(jsonElement.getAsString());
         } else if(jsonElement.isJsonArray()) {
            JsonArray var4 = jsonElement.getAsJsonArray();
            Component var5 = null;

            for(JsonElement var7 : var4) {
               Component var8 = this.deserialize(var7, var7.getClass(), jsonDeserializationContext);
               if(var5 == null) {
                  var5 = var8;
               } else {
                  var5.append(var8);
               }
            }

            return var5;
         } else {
            throw new JsonParseException("Don\'t know how to turn " + jsonElement + " into a Component");
         }
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }).create();

   public BlockEntitySignTextStrictJsonFix(Schema schema, boolean var2) {
      super(schema, var2, "BlockEntitySignTextStrictJsonFix", References.BLOCK_ENTITY, "Sign");
   }

   private Dynamic updateLine(Dynamic var1, String string) {
      String string = var1.get(string).asString("");
      Component var4 = null;
      if(!"null".equals(string) && !StringUtils.isEmpty(string)) {
         if(string.charAt(0) == 34 && string.charAt(string.length() - 1) == 34 || string.charAt(0) == 123 && string.charAt(string.length() - 1) == 125) {
            try {
               var4 = (Component)GsonHelper.fromJson(GSON, string, Component.class, true);
               if(var4 == null) {
                  var4 = new TextComponent("");
               }
            } catch (JsonParseException var8) {
               ;
            }

            if(var4 == null) {
               try {
                  var4 = Component.Serializer.fromJson(string);
               } catch (JsonParseException var7) {
                  ;
               }
            }

            if(var4 == null) {
               try {
                  var4 = Component.Serializer.fromJsonLenient(string);
               } catch (JsonParseException var6) {
                  ;
               }
            }

            if(var4 == null) {
               var4 = new TextComponent(string);
            }
         } else {
            var4 = new TextComponent(string);
         }
      } else {
         var4 = new TextComponent("");
      }

      return var1.set(string, var1.createString(Component.Serializer.toJson(var4)));
   }

   protected Typed fix(Typed typed) {
      return typed.update(DSL.remainderFinder(), (dynamic) -> {
         dynamic = this.updateLine(dynamic, "Text1");
         dynamic = this.updateLine(dynamic, "Text2");
         dynamic = this.updateLine(dynamic, "Text3");
         dynamic = this.updateLine(dynamic, "Text4");
         return dynamic;
      });
   }
}
