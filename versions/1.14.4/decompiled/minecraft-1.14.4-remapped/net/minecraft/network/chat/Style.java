package net.minecraft.network.chat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.util.GsonHelper;

public class Style {
   private Style parent;
   private ChatFormatting color;
   private Boolean bold;
   private Boolean italic;
   private Boolean underlined;
   private Boolean strikethrough;
   private Boolean obfuscated;
   private ClickEvent clickEvent;
   private HoverEvent hoverEvent;
   private String insertion;
   private static final Style ROOT = new Style() {
      @Nullable
      public ChatFormatting getColor() {
         return null;
      }

      public boolean isBold() {
         return false;
      }

      public boolean isItalic() {
         return false;
      }

      public boolean isStrikethrough() {
         return false;
      }

      public boolean isUnderlined() {
         return false;
      }

      public boolean isObfuscated() {
         return false;
      }

      @Nullable
      public ClickEvent getClickEvent() {
         return null;
      }

      @Nullable
      public HoverEvent getHoverEvent() {
         return null;
      }

      @Nullable
      public String getInsertion() {
         return null;
      }

      public Style setColor(ChatFormatting color) {
         throw new UnsupportedOperationException();
      }

      public Style setBold(Boolean bold) {
         throw new UnsupportedOperationException();
      }

      public Style setItalic(Boolean italic) {
         throw new UnsupportedOperationException();
      }

      public Style setStrikethrough(Boolean strikethrough) {
         throw new UnsupportedOperationException();
      }

      public Style setUnderlined(Boolean underlined) {
         throw new UnsupportedOperationException();
      }

      public Style setObfuscated(Boolean obfuscated) {
         throw new UnsupportedOperationException();
      }

      public Style setClickEvent(ClickEvent clickEvent) {
         throw new UnsupportedOperationException();
      }

      public Style setHoverEvent(HoverEvent hoverEvent) {
         throw new UnsupportedOperationException();
      }

      public Style inheritFrom(Style style) {
         throw new UnsupportedOperationException();
      }

      public String toString() {
         return "Style.ROOT";
      }

      public Style copy() {
         return this;
      }

      public Style flatCopy() {
         return this;
      }

      public String getLegacyFormatCodes() {
         return "";
      }
   };

   @Nullable
   public ChatFormatting getColor() {
      return this.color == null?this.getParent().getColor():this.color;
   }

   public boolean isBold() {
      return this.bold == null?this.getParent().isBold():this.bold.booleanValue();
   }

   public boolean isItalic() {
      return this.italic == null?this.getParent().isItalic():this.italic.booleanValue();
   }

   public boolean isStrikethrough() {
      return this.strikethrough == null?this.getParent().isStrikethrough():this.strikethrough.booleanValue();
   }

   public boolean isUnderlined() {
      return this.underlined == null?this.getParent().isUnderlined():this.underlined.booleanValue();
   }

   public boolean isObfuscated() {
      return this.obfuscated == null?this.getParent().isObfuscated():this.obfuscated.booleanValue();
   }

   public boolean isEmpty() {
      return this.bold == null && this.italic == null && this.strikethrough == null && this.underlined == null && this.obfuscated == null && this.color == null && this.clickEvent == null && this.hoverEvent == null && this.insertion == null;
   }

   @Nullable
   public ClickEvent getClickEvent() {
      return this.clickEvent == null?this.getParent().getClickEvent():this.clickEvent;
   }

   @Nullable
   public HoverEvent getHoverEvent() {
      return this.hoverEvent == null?this.getParent().getHoverEvent():this.hoverEvent;
   }

   @Nullable
   public String getInsertion() {
      return this.insertion == null?this.getParent().getInsertion():this.insertion;
   }

   public Style setColor(ChatFormatting color) {
      this.color = color;
      return this;
   }

   public Style setBold(Boolean bold) {
      this.bold = bold;
      return this;
   }

   public Style setItalic(Boolean italic) {
      this.italic = italic;
      return this;
   }

   public Style setStrikethrough(Boolean strikethrough) {
      this.strikethrough = strikethrough;
      return this;
   }

   public Style setUnderlined(Boolean underlined) {
      this.underlined = underlined;
      return this;
   }

   public Style setObfuscated(Boolean obfuscated) {
      this.obfuscated = obfuscated;
      return this;
   }

   public Style setClickEvent(ClickEvent clickEvent) {
      this.clickEvent = clickEvent;
      return this;
   }

   public Style setHoverEvent(HoverEvent hoverEvent) {
      this.hoverEvent = hoverEvent;
      return this;
   }

   public Style setInsertion(String insertion) {
      this.insertion = insertion;
      return this;
   }

   public Style inheritFrom(Style parent) {
      this.parent = parent;
      return this;
   }

   public String getLegacyFormatCodes() {
      if(this.isEmpty()) {
         return this.parent != null?this.parent.getLegacyFormatCodes():"";
      } else {
         StringBuilder var1 = new StringBuilder();
         if(this.getColor() != null) {
            var1.append(this.getColor());
         }

         if(this.isBold()) {
            var1.append(ChatFormatting.BOLD);
         }

         if(this.isItalic()) {
            var1.append(ChatFormatting.ITALIC);
         }

         if(this.isUnderlined()) {
            var1.append(ChatFormatting.UNDERLINE);
         }

         if(this.isObfuscated()) {
            var1.append(ChatFormatting.OBFUSCATED);
         }

         if(this.isStrikethrough()) {
            var1.append(ChatFormatting.STRIKETHROUGH);
         }

         return var1.toString();
      }
   }

   private Style getParent() {
      return this.parent == null?ROOT:this.parent;
   }

   public String toString() {
      return "Style{hasParent=" + (this.parent != null) + ", color=" + this.color + ", bold=" + this.bold + ", italic=" + this.italic + ", underlined=" + this.underlined + ", obfuscated=" + this.obfuscated + ", clickEvent=" + this.getClickEvent() + ", hoverEvent=" + this.getHoverEvent() + ", insertion=" + this.getInsertion() + '}';
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof Style)) {
         return false;
      } else {
         boolean var10000;
         label0: {
            Style var2 = (Style)object;
            if(this.isBold() == var2.isBold() && this.getColor() == var2.getColor() && this.isItalic() == var2.isItalic() && this.isObfuscated() == var2.isObfuscated() && this.isStrikethrough() == var2.isStrikethrough() && this.isUnderlined() == var2.isUnderlined()) {
               label85: {
                  if(this.getClickEvent() != null) {
                     if(!this.getClickEvent().equals(var2.getClickEvent())) {
                        break label85;
                     }
                  } else if(var2.getClickEvent() != null) {
                     break label85;
                  }

                  if(this.getHoverEvent() != null) {
                     if(!this.getHoverEvent().equals(var2.getHoverEvent())) {
                        break label85;
                     }
                  } else if(var2.getHoverEvent() != null) {
                     break label85;
                  }

                  if(this.getInsertion() != null) {
                     if(this.getInsertion().equals(var2.getInsertion())) {
                        break label0;
                     }
                  } else if(var2.getInsertion() == null) {
                     break label0;
                  }
               }
            }

            var10000 = false;
            return var10000;
         }

         var10000 = true;
         return var10000;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion});
   }

   public Style copy() {
      Style style = new Style();
      style.bold = this.bold;
      style.italic = this.italic;
      style.strikethrough = this.strikethrough;
      style.underlined = this.underlined;
      style.obfuscated = this.obfuscated;
      style.color = this.color;
      style.clickEvent = this.clickEvent;
      style.hoverEvent = this.hoverEvent;
      style.parent = this.parent;
      style.insertion = this.insertion;
      return style;
   }

   public Style flatCopy() {
      Style style = new Style();
      style.setBold(Boolean.valueOf(this.isBold()));
      style.setItalic(Boolean.valueOf(this.isItalic()));
      style.setStrikethrough(Boolean.valueOf(this.isStrikethrough()));
      style.setUnderlined(Boolean.valueOf(this.isUnderlined()));
      style.setObfuscated(Boolean.valueOf(this.isObfuscated()));
      style.setColor(this.getColor());
      style.setClickEvent(this.getClickEvent());
      style.setHoverEvent(this.getHoverEvent());
      style.setInsertion(this.getInsertion());
      return style;
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      @Nullable
      public Style deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         if(jsonElement.isJsonObject()) {
            Style style = new Style();
            JsonObject var5 = jsonElement.getAsJsonObject();
            if(var5 == null) {
               return null;
            } else {
               if(var5.has("bold")) {
                  style.bold = Boolean.valueOf(var5.get("bold").getAsBoolean());
               }

               if(var5.has("italic")) {
                  style.italic = Boolean.valueOf(var5.get("italic").getAsBoolean());
               }

               if(var5.has("underlined")) {
                  style.underlined = Boolean.valueOf(var5.get("underlined").getAsBoolean());
               }

               if(var5.has("strikethrough")) {
                  style.strikethrough = Boolean.valueOf(var5.get("strikethrough").getAsBoolean());
               }

               if(var5.has("obfuscated")) {
                  style.obfuscated = Boolean.valueOf(var5.get("obfuscated").getAsBoolean());
               }

               if(var5.has("color")) {
                  style.color = (ChatFormatting)jsonDeserializationContext.deserialize(var5.get("color"), ChatFormatting.class);
               }

               if(var5.has("insertion")) {
                  style.insertion = var5.get("insertion").getAsString();
               }

               if(var5.has("clickEvent")) {
                  JsonObject var6 = GsonHelper.getAsJsonObject(var5, "clickEvent");
                  String var7 = GsonHelper.getAsString(var6, "action", (String)null);
                  ClickEvent.Action var8 = var7 == null?null:ClickEvent.Action.getByName(var7);
                  String var9 = GsonHelper.getAsString(var6, "value", (String)null);
                  if(var8 != null && var9 != null && var8.isAllowedFromServer()) {
                     style.clickEvent = new ClickEvent(var8, var9);
                  }
               }

               if(var5.has("hoverEvent")) {
                  JsonObject var6 = GsonHelper.getAsJsonObject(var5, "hoverEvent");
                  String var7 = GsonHelper.getAsString(var6, "action", (String)null);
                  HoverEvent.Action var8 = var7 == null?null:HoverEvent.Action.getByName(var7);
                  Component var9 = (Component)jsonDeserializationContext.deserialize(var6.get("value"), Component.class);
                  if(var8 != null && var9 != null && var8.isAllowedFromServer()) {
                     style.hoverEvent = new HoverEvent(var8, var9);
                  }
               }

               return style;
            }
         } else {
            return null;
         }
      }

      @Nullable
      public JsonElement serialize(Style style, Type type, JsonSerializationContext jsonSerializationContext) {
         if(style.isEmpty()) {
            return null;
         } else {
            JsonObject var4 = new JsonObject();
            if(style.bold != null) {
               var4.addProperty("bold", style.bold);
            }

            if(style.italic != null) {
               var4.addProperty("italic", style.italic);
            }

            if(style.underlined != null) {
               var4.addProperty("underlined", style.underlined);
            }

            if(style.strikethrough != null) {
               var4.addProperty("strikethrough", style.strikethrough);
            }

            if(style.obfuscated != null) {
               var4.addProperty("obfuscated", style.obfuscated);
            }

            if(style.color != null) {
               var4.add("color", jsonSerializationContext.serialize(style.color));
            }

            if(style.insertion != null) {
               var4.add("insertion", jsonSerializationContext.serialize(style.insertion));
            }

            if(style.clickEvent != null) {
               JsonObject var5 = new JsonObject();
               var5.addProperty("action", style.clickEvent.getAction().getName());
               var5.addProperty("value", style.clickEvent.getValue());
               var4.add("clickEvent", var5);
            }

            if(style.hoverEvent != null) {
               JsonObject var5 = new JsonObject();
               var5.addProperty("action", style.hoverEvent.getAction().getName());
               var5.add("value", jsonSerializationContext.serialize(style.hoverEvent.getValue()));
               var4.add("hoverEvent", var5);
            }

            return var4;
         }
      }

      // $FF: synthetic method
      @Nullable
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((Style)var1, var2, var3);
      }

      // $FF: synthetic method
      @Nullable
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
