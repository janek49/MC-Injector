package net.minecraft.client.resources.sounds;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.Validate;

@ClientJarOnly
public class SoundEventRegistrationSerializer implements JsonDeserializer {
   public SoundEventRegistration deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      JsonObject var4 = GsonHelper.convertToJsonObject(jsonElement, "entry");
      boolean var5 = GsonHelper.getAsBoolean(var4, "replace", false);
      String var6 = GsonHelper.getAsString(var4, "subtitle", (String)null);
      List<Sound> var7 = this.getSounds(var4);
      return new SoundEventRegistration(var7, var5, var6);
   }

   private List getSounds(JsonObject jsonObject) {
      List<Sound> list = Lists.newArrayList();
      if(jsonObject.has("sounds")) {
         JsonArray var3 = GsonHelper.getAsJsonArray(jsonObject, "sounds");

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            JsonElement var5 = var3.get(var4);
            if(GsonHelper.isStringValue(var5)) {
               String var6 = GsonHelper.convertToString(var5, "sound");
               list.add(new Sound(var6, 1.0F, 1.0F, 1, Sound.Type.FILE, false, false, 16));
            } else {
               list.add(this.getSound(GsonHelper.convertToJsonObject(var5, "sound")));
            }
         }
      }

      return list;
   }

   private Sound getSound(JsonObject jsonObject) {
      String var2 = GsonHelper.getAsString(jsonObject, "name");
      Sound.Type var3 = this.getType(jsonObject, Sound.Type.FILE);
      float var4 = GsonHelper.getAsFloat(jsonObject, "volume", 1.0F);
      Validate.isTrue(var4 > 0.0F, "Invalid volume", new Object[0]);
      float var5 = GsonHelper.getAsFloat(jsonObject, "pitch", 1.0F);
      Validate.isTrue(var5 > 0.0F, "Invalid pitch", new Object[0]);
      int var6 = GsonHelper.getAsInt(jsonObject, "weight", 1);
      Validate.isTrue(var6 > 0, "Invalid weight", new Object[0]);
      boolean var7 = GsonHelper.getAsBoolean(jsonObject, "preload", false);
      boolean var8 = GsonHelper.getAsBoolean(jsonObject, "stream", false);
      int var9 = GsonHelper.getAsInt(jsonObject, "attenuation_distance", 16);
      return new Sound(var2, var4, var5, var6, var3, var8, var7, var9);
   }

   private Sound.Type getType(JsonObject jsonObject, Sound.Type var2) {
      Sound.Type var3 = var2;
      if(jsonObject.has("type")) {
         var3 = Sound.Type.getByName(GsonHelper.getAsString(jsonObject, "type"));
         Validate.notNull(var3, "Invalid type", new Object[0]);
      }

      return var3;
   }

   // $FF: synthetic method
   public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      return this.deserialize(var1, var2, var3);
   }
}
