package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class Criterion {
   private final CriterionTriggerInstance trigger;

   public Criterion(CriterionTriggerInstance trigger) {
      this.trigger = trigger;
   }

   public Criterion() {
      this.trigger = null;
   }

   public void serializeToNetwork(FriendlyByteBuf friendlyByteBuf) {
   }

   public static Criterion criterionFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      ResourceLocation var2 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "trigger"));
      CriterionTrigger<?> var3 = CriteriaTriggers.getCriterion(var2);
      if(var3 == null) {
         throw new JsonSyntaxException("Invalid criterion trigger: " + var2);
      } else {
         CriterionTriggerInstance var4 = var3.createInstance(GsonHelper.getAsJsonObject(jsonObject, "conditions", new JsonObject()), jsonDeserializationContext);
         return new Criterion(var4);
      }
   }

   public static Criterion criterionFromNetwork(FriendlyByteBuf friendlyByteBuf) {
      return new Criterion();
   }

   public static Map criteriaFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      Map<String, Criterion> map = Maps.newHashMap();

      for(Entry<String, JsonElement> var4 : jsonObject.entrySet()) {
         map.put(var4.getKey(), criterionFromJson(GsonHelper.convertToJsonObject((JsonElement)var4.getValue(), "criterion"), jsonDeserializationContext));
      }

      return map;
   }

   public static Map criteriaFromNetwork(FriendlyByteBuf friendlyByteBuf) {
      Map<String, Criterion> map = Maps.newHashMap();
      int var2 = friendlyByteBuf.readVarInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         map.put(friendlyByteBuf.readUtf(32767), criterionFromNetwork(friendlyByteBuf));
      }

      return map;
   }

   public static void serializeToNetwork(Map map, FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.writeVarInt(map.size());

      for(Entry<String, Criterion> var3 : map.entrySet()) {
         friendlyByteBuf.writeUtf((String)var3.getKey());
         ((Criterion)var3.getValue()).serializeToNetwork(friendlyByteBuf);
      }

   }

   @Nullable
   public CriterionTriggerInstance getTrigger() {
      return this.trigger;
   }

   public JsonElement serializeToJson() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("trigger", this.trigger.getCriterion().toString());
      var1.add("conditions", this.trigger.serializeToJson());
      return var1;
   }
}
