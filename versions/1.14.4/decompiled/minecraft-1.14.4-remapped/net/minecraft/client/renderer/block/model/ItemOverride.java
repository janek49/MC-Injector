package net.minecraft.client.renderer.block.model;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemPropertyFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@ClientJarOnly
public class ItemOverride {
   private final ResourceLocation model;
   private final Map predicates;

   public ItemOverride(ResourceLocation model, Map predicates) {
      this.model = model;
      this.predicates = predicates;
   }

   public ResourceLocation getModel() {
      return this.model;
   }

   boolean test(ItemStack itemStack, @Nullable Level level, @Nullable LivingEntity livingEntity) {
      Item var4 = itemStack.getItem();

      for(Entry<ResourceLocation, Float> var6 : this.predicates.entrySet()) {
         ItemPropertyFunction var7 = var4.getProperty((ResourceLocation)var6.getKey());
         if(var7 == null || var7.call(itemStack, level, livingEntity) < ((Float)var6.getValue()).floatValue()) {
            return false;
         }
      }

      return true;
   }

   @ClientJarOnly
   public static class Deserializer implements JsonDeserializer {
      public ItemOverride deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject var4 = jsonElement.getAsJsonObject();
         ResourceLocation var5 = new ResourceLocation(GsonHelper.getAsString(var4, "model"));
         Map<ResourceLocation, Float> var6 = this.getPredicates(var4);
         return new ItemOverride(var5, var6);
      }

      protected Map getPredicates(JsonObject jsonObject) {
         Map<ResourceLocation, Float> map = Maps.newLinkedHashMap();
         JsonObject var3 = GsonHelper.getAsJsonObject(jsonObject, "predicate");

         for(Entry<String, JsonElement> var5 : var3.entrySet()) {
            map.put(new ResourceLocation((String)var5.getKey()), Float.valueOf(GsonHelper.convertToFloat((JsonElement)var5.getValue(), (String)var5.getKey())));
         }

         return map;
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
