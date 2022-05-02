package net.minecraft.network.chat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.Message;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.network.chat.NbtComponent;
import net.minecraft.network.chat.ScoreComponent;
import net.minecraft.network.chat.SelectorComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LowerCaseEnumTypeAdapterFactory;

public interface Component extends Message, Iterable {
   Component setStyle(Style var1);

   Style getStyle();

   default Component append(String string) {
      return this.append((Component)(new TextComponent(string)));
   }

   Component append(Component var1);

   String getContents();

   default String getString() {
      StringBuilder var1 = new StringBuilder();
      this.stream().forEach((component) -> {
         var1.append(component.getContents());
      });
      return var1.toString();
   }

   default String getString(int i) {
      StringBuilder var2 = new StringBuilder();
      Iterator<Component> var3 = this.stream().iterator();

      while(var3.hasNext()) {
         int var4 = i - var2.length();
         if(var4 <= 0) {
            break;
         }

         String var5 = ((Component)var3.next()).getContents();
         var2.append(var5.length() <= var4?var5:var5.substring(0, var4));
      }

      return var2.toString();
   }

   default String getColoredString() {
      StringBuilder var1 = new StringBuilder();
      String var2 = "";

      for(Component var4 : this.stream()) {
         String var5 = var4.getContents();
         if(!var5.isEmpty()) {
            String var6 = var4.getStyle().getLegacyFormatCodes();
            if(!var6.equals(var2)) {
               if(!var2.isEmpty()) {
                  var1.append(ChatFormatting.RESET);
               }

               var1.append(var6);
               var2 = var6;
            }

            var1.append(var5);
         }
      }

      if(!var2.isEmpty()) {
         var1.append(ChatFormatting.RESET);
      }

      return var1.toString();
   }

   List getSiblings();

   Stream stream();

   default Stream flatStream() {
      return this.stream().map(Component::flattenStyle);
   }

   default Iterator iterator() {
      return this.flatStream().iterator();
   }

   Component copy();

   default Component deepCopy() {
      Component component = this.copy();
      component.setStyle(this.getStyle().copy());

      for(Component var3 : this.getSiblings()) {
         component.append(var3.deepCopy());
      }

      return component;
   }

   default Component withStyle(Consumer consumer) {
      consumer.accept(this.getStyle());
      return this;
   }

   default Component withStyle(ChatFormatting... chatFormattings) {
      for(ChatFormatting var5 : chatFormattings) {
         this.withStyle(var5);
      }

      return this;
   }

   default Component withStyle(ChatFormatting chatFormatting) {
      Style var2 = this.getStyle();
      if(chatFormatting.isColor()) {
         var2.setColor(chatFormatting);
      }

      if(chatFormatting.isFormat()) {
         switch(chatFormatting) {
         case OBFUSCATED:
            var2.setObfuscated(Boolean.valueOf(true));
            break;
         case BOLD:
            var2.setBold(Boolean.valueOf(true));
            break;
         case STRIKETHROUGH:
            var2.setStrikethrough(Boolean.valueOf(true));
            break;
         case UNDERLINE:
            var2.setUnderlined(Boolean.valueOf(true));
            break;
         case ITALIC:
            var2.setItalic(Boolean.valueOf(true));
         }
      }

      return this;
   }

   static default Component flattenStyle(Component component) {
      Component var1 = component.copy();
      var1.setStyle(component.getStyle().flatCopy());
      return var1;
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      private static final Gson GSON = (Gson)Util.make(() -> {
         GsonBuilder var0 = new GsonBuilder();
         var0.disableHtmlEscaping();
         var0.registerTypeHierarchyAdapter(Component.class, new Component.Serializer());
         var0.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
         var0.registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory());
         return var0.create();
      });
      private static final Field JSON_READER_POS = (Field)Util.make(() -> {
         try {
            new JsonReader(new StringReader(""));
            Field field = JsonReader.class.getDeclaredField("pos");
            field.setAccessible(true);
            return field;
         } catch (NoSuchFieldException var1) {
            throw new IllegalStateException("Couldn\'t get field \'pos\' for JsonReader", var1);
         }
      });
      private static final Field JSON_READER_LINESTART = (Field)Util.make(() -> {
         try {
            new JsonReader(new StringReader(""));
            Field field = JsonReader.class.getDeclaredField("lineStart");
            field.setAccessible(true);
            return field;
         } catch (NoSuchFieldException var1) {
            throw new IllegalStateException("Couldn\'t get field \'lineStart\' for JsonReader", var1);
         }
      });

      public Component deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         if(jsonElement.isJsonPrimitive()) {
            return new TextComponent(jsonElement.getAsString());
         } else if(!jsonElement.isJsonObject()) {
            if(jsonElement.isJsonArray()) {
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
         } else {
            JsonObject var4 = jsonElement.getAsJsonObject();
            Component var5;
            if(var4.has("text")) {
               var5 = new TextComponent(GsonHelper.getAsString(var4, "text"));
            } else if(var4.has("translate")) {
               String var6 = GsonHelper.getAsString(var4, "translate");
               if(var4.has("with")) {
                  JsonArray var7 = GsonHelper.getAsJsonArray(var4, "with");
                  Object[] vars8 = new Object[var7.size()];

                  for(int var9 = 0; var9 < vars8.length; ++var9) {
                     vars8[var9] = this.deserialize(var7.get(var9), type, jsonDeserializationContext);
                     if(vars8[var9] instanceof TextComponent) {
                        TextComponent var10 = (TextComponent)vars8[var9];
                        if(var10.getStyle().isEmpty() && var10.getSiblings().isEmpty()) {
                           vars8[var9] = var10.getText();
                        }
                     }
                  }

                  var5 = new TranslatableComponent(var6, vars8);
               } else {
                  var5 = new TranslatableComponent(var6, new Object[0]);
               }
            } else if(var4.has("score")) {
               JsonObject var6 = GsonHelper.getAsJsonObject(var4, "score");
               if(!var6.has("name") || !var6.has("objective")) {
                  throw new JsonParseException("A score component needs a least a name and an objective");
               }

               var5 = new ScoreComponent(GsonHelper.getAsString(var6, "name"), GsonHelper.getAsString(var6, "objective"));
               if(var6.has("value")) {
                  ((ScoreComponent)var5).setValue(GsonHelper.getAsString(var6, "value"));
               }
            } else if(var4.has("selector")) {
               var5 = new SelectorComponent(GsonHelper.getAsString(var4, "selector"));
            } else if(var4.has("keybind")) {
               var5 = new KeybindComponent(GsonHelper.getAsString(var4, "keybind"));
            } else {
               if(!var4.has("nbt")) {
                  throw new JsonParseException("Don\'t know how to turn " + jsonElement + " into a Component");
               }

               String var6 = GsonHelper.getAsString(var4, "nbt");
               boolean var7 = GsonHelper.getAsBoolean(var4, "interpret", false);
               if(var4.has("block")) {
                  var5 = new NbtComponent.BlockNbtComponent(var6, var7, GsonHelper.getAsString(var4, "block"));
               } else {
                  if(!var4.has("entity")) {
                     throw new JsonParseException("Don\'t know how to turn " + jsonElement + " into a Component");
                  }

                  var5 = new NbtComponent.EntityNbtComponent(var6, var7, GsonHelper.getAsString(var4, "entity"));
               }
            }

            if(var4.has("extra")) {
               JsonArray var6 = GsonHelper.getAsJsonArray(var4, "extra");
               if(var6.size() <= 0) {
                  throw new JsonParseException("Unexpected empty array of components");
               }

               for(int var7 = 0; var7 < var6.size(); ++var7) {
                  var5.append(this.deserialize(var6.get(var7), type, jsonDeserializationContext));
               }
            }

            var5.setStyle((Style)jsonDeserializationContext.deserialize(jsonElement, Style.class));
            return var5;
         }
      }

      private void serializeStyle(Style style, JsonObject jsonObject, JsonSerializationContext jsonSerializationContext) {
         JsonElement var4 = jsonSerializationContext.serialize(style);
         if(var4.isJsonObject()) {
            JsonObject var5 = (JsonObject)var4;

            for(Entry<String, JsonElement> var7 : var5.entrySet()) {
               jsonObject.add((String)var7.getKey(), (JsonElement)var7.getValue());
            }
         }

      }

      public JsonElement serialize(Component component, Type type, JsonSerializationContext jsonSerializationContext) {
         JsonObject var4 = new JsonObject();
         if(!component.getStyle().isEmpty()) {
            this.serializeStyle(component.getStyle(), var4, jsonSerializationContext);
         }

         if(!component.getSiblings().isEmpty()) {
            JsonArray var5 = new JsonArray();

            for(Component var7 : component.getSiblings()) {
               var5.add(this.serialize((Component)var7, var7.getClass(), jsonSerializationContext));
            }

            var4.add("extra", var5);
         }

         if(component instanceof TextComponent) {
            var4.addProperty("text", ((TextComponent)component).getText());
         } else if(component instanceof TranslatableComponent) {
            TranslatableComponent var5 = (TranslatableComponent)component;
            var4.addProperty("translate", var5.getKey());
            if(var5.getArgs() != null && var5.getArgs().length > 0) {
               JsonArray var6 = new JsonArray();

               for(Object var10 : var5.getArgs()) {
                  if(var10 instanceof Component) {
                     var6.add(this.serialize((Component)((Component)var10), var10.getClass(), jsonSerializationContext));
                  } else {
                     var6.add(new JsonPrimitive(String.valueOf(var10)));
                  }
               }

               var4.add("with", var6);
            }
         } else if(component instanceof ScoreComponent) {
            ScoreComponent var5 = (ScoreComponent)component;
            JsonObject var6 = new JsonObject();
            var6.addProperty("name", var5.getName());
            var6.addProperty("objective", var5.getObjective());
            var6.addProperty("value", var5.getContents());
            var4.add("score", var6);
         } else if(component instanceof SelectorComponent) {
            SelectorComponent var5 = (SelectorComponent)component;
            var4.addProperty("selector", var5.getPattern());
         } else if(component instanceof KeybindComponent) {
            KeybindComponent var5 = (KeybindComponent)component;
            var4.addProperty("keybind", var5.getName());
         } else {
            if(!(component instanceof NbtComponent)) {
               throw new IllegalArgumentException("Don\'t know how to serialize " + component + " as a Component");
            }

            NbtComponent var5 = (NbtComponent)component;
            var4.addProperty("nbt", var5.getNbtPath());
            var4.addProperty("interpret", Boolean.valueOf(var5.isInterpreting()));
            if(component instanceof NbtComponent.BlockNbtComponent) {
               NbtComponent.BlockNbtComponent var6 = (NbtComponent.BlockNbtComponent)component;
               var4.addProperty("block", var6.getPos());
            } else {
               if(!(component instanceof NbtComponent.EntityNbtComponent)) {
                  throw new IllegalArgumentException("Don\'t know how to serialize " + component + " as a Component");
               }

               NbtComponent.EntityNbtComponent var6 = (NbtComponent.EntityNbtComponent)component;
               var4.addProperty("entity", var6.getSelector());
            }
         }

         return var4;
      }

      public static String toJson(Component component) {
         return GSON.toJson(component);
      }

      public static JsonElement toJsonTree(Component component) {
         return GSON.toJsonTree(component);
      }

      @Nullable
      public static Component fromJson(String json) {
         return (Component)GsonHelper.fromJson(GSON, json, Component.class, false);
      }

      @Nullable
      public static Component fromJson(JsonElement json) {
         return (Component)GSON.fromJson(json, Component.class);
      }

      @Nullable
      public static Component fromJsonLenient(String jsonLenient) {
         return (Component)GsonHelper.fromJson(GSON, jsonLenient, Component.class, true);
      }

      public static Component fromJson(com.mojang.brigadier.StringReader json) {
         try {
            JsonReader var1 = new JsonReader(new StringReader(json.getRemaining()));
            var1.setLenient(false);
            Component var2 = (Component)GSON.getAdapter(Component.class).read(var1);
            json.setCursor(json.getCursor() + getPos(var1));
            return var2;
         } catch (IOException var3) {
            throw new JsonParseException(var3);
         }
      }

      private static int getPos(JsonReader jsonReader) {
         try {
            return JSON_READER_POS.getInt(jsonReader) - JSON_READER_LINESTART.getInt(jsonReader) + 1;
         } catch (IllegalAccessException var2) {
            throw new IllegalStateException("Couldn\'t read position of JsonReader", var2);
         }
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((Component)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
