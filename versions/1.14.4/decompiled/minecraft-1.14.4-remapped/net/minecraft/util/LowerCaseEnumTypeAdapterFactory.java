package net.minecraft.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;

public class LowerCaseEnumTypeAdapterFactory implements TypeAdapterFactory {
   @Nullable
   public TypeAdapter create(Gson gson, TypeToken typeToken) {
      Class<T> var3 = typeToken.getRawType();
      if(!var3.isEnum()) {
         return null;
      } else {
         final Map<String, T> var4 = Maps.newHashMap();

         for(T var8 : var3.getEnumConstants()) {
            var4.put(this.toLowercase(var8), var8);
         }

         return new TypeAdapter() {
            public void write(JsonWriter jsonWriter, Object object) throws IOException {
               if(object == null) {
                  jsonWriter.nullValue();
               } else {
                  jsonWriter.value(LowerCaseEnumTypeAdapterFactory.this.toLowercase(object));
               }

            }

            @Nullable
            public Object read(JsonReader jsonReader) throws IOException {
               if(jsonReader.peek() == JsonToken.NULL) {
                  jsonReader.nextNull();
                  return null;
               } else {
                  return var4.get(jsonReader.nextString());
               }
            }
         };
      }
   }

   private String toLowercase(Object object) {
      return object instanceof Enum?((Enum)object).name().toLowerCase(Locale.ROOT):object.toString().toLowerCase(Locale.ROOT);
   }
}
