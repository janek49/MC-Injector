package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.EntryGroup;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.entries.SequentialEntry;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.commons.lang3.ArrayUtils;

public class LootPoolEntries {
   private static final Map ID_TO_SERIALIZER = Maps.newHashMap();
   private static final Map CLASS_TO_SERIALIZER = Maps.newHashMap();

   private static void register(LootPoolEntryContainer.Serializer lootPoolEntryContainer$Serializer) {
      ID_TO_SERIALIZER.put(lootPoolEntryContainer$Serializer.getName(), lootPoolEntryContainer$Serializer);
      CLASS_TO_SERIALIZER.put(lootPoolEntryContainer$Serializer.getContainerClass(), lootPoolEntryContainer$Serializer);
   }

   static {
      register(CompositeEntryBase.createSerializer(new ResourceLocation("alternatives"), AlternativesEntry.class, AlternativesEntry::<init>));
      register(CompositeEntryBase.createSerializer(new ResourceLocation("sequence"), SequentialEntry.class, SequentialEntry::<init>));
      register(CompositeEntryBase.createSerializer(new ResourceLocation("group"), EntryGroup.class, EntryGroup::<init>));
      register(new EmptyLootItem.Serializer());
      register(new LootItem.Serializer());
      register(new LootTableReference.Serializer());
      register(new DynamicLoot.Serializer());
      register(new TagEntry.Serializer());
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public LootPoolEntryContainer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
         JsonObject var4 = GsonHelper.convertToJsonObject(jsonElement, "entry");
         ResourceLocation var5 = new ResourceLocation(GsonHelper.getAsString(var4, "type"));
         LootPoolEntryContainer.Serializer<?> var6 = (LootPoolEntryContainer.Serializer)LootPoolEntries.ID_TO_SERIALIZER.get(var5);
         if(var6 == null) {
            throw new JsonParseException("Unknown item type: " + var5);
         } else {
            LootItemCondition[] vars7 = (LootItemCondition[])GsonHelper.getAsObject(var4, "conditions", new LootItemCondition[0], jsonDeserializationContext, LootItemCondition[].class);
            return var6.deserialize(var4, jsonDeserializationContext, vars7);
         }
      }

      public JsonElement serialize(LootPoolEntryContainer lootPoolEntryContainer, Type type, JsonSerializationContext jsonSerializationContext) {
         JsonObject var4 = new JsonObject();
         LootPoolEntryContainer.Serializer<LootPoolEntryContainer> var5 = getSerializer(lootPoolEntryContainer.getClass());
         var4.addProperty("type", var5.getName().toString());
         if(!ArrayUtils.isEmpty(lootPoolEntryContainer.conditions)) {
            var4.add("conditions", jsonSerializationContext.serialize(lootPoolEntryContainer.conditions));
         }

         var5.serialize(var4, lootPoolEntryContainer, jsonSerializationContext);
         return var4;
      }

      private static LootPoolEntryContainer.Serializer getSerializer(Class class) {
         LootPoolEntryContainer.Serializer<?> lootPoolEntryContainer$Serializer = (LootPoolEntryContainer.Serializer)LootPoolEntries.CLASS_TO_SERIALIZER.get(class);
         if(lootPoolEntryContainer$Serializer == null) {
            throw new JsonParseException("Unknown item type: " + class);
         } else {
            return lootPoolEntryContainer$Serializer;
         }
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((LootPoolEntryContainer)var1, var2, var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
