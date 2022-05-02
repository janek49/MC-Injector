package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItemBlockStatePropertyCondition implements LootItemCondition {
   private final Block block;
   private final Map properties;
   private final Predicate composedPredicate;

   private LootItemBlockStatePropertyCondition(Block block, Map map) {
      this.block = block;
      this.properties = ImmutableMap.copyOf(map);
      this.composedPredicate = bakePredicate(block, map);
   }

   private static Predicate bakePredicate(Block block, Map map) {
      int var2 = map.size();
      if(var2 == 0) {
         return (blockState) -> {
            return blockState.getBlock() == block;
         };
      } else if(var2 == 1) {
         Entry<Property<?>, Object> var3 = (Entry)map.entrySet().iterator().next();
         Property<?> var4 = (Property)var3.getKey();
         Object var5 = var3.getValue();
         return (blockState) -> {
            return blockState.getBlock() == block && var5.equals(blockState.getValue(var9));
         };
      } else {
         Predicate<BlockState> var3 = (blockState) -> {
            return blockState.getBlock() == block;
         };

         for(Entry<Property<?>, Object> var5 : map.entrySet()) {
            Property<?> var6 = (Property)var5.getKey();
            Object var7 = var5.getValue();
            var3 = var3.and((blockState) -> {
               return var7.equals(blockState.getValue(var6));
            });
         }

         return var3;
      }
   }

   public Set getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.BLOCK_STATE);
   }

   public boolean test(LootContext lootContext) {
      BlockState var2 = (BlockState)lootContext.getParamOrNull(LootContextParams.BLOCK_STATE);
      return var2 != null && this.composedPredicate.test(var2);
   }

   public static LootItemBlockStatePropertyCondition.Builder hasBlockStateProperties(Block block) {
      return new LootItemBlockStatePropertyCondition.Builder(block);
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((LootContext)var1);
   }

   public static class Builder implements LootItemCondition.Builder {
      private final Block block;
      private final Set allowedProperties;
      private final Map properties = Maps.newHashMap();

      public Builder(Block block) {
         this.block = block;
         this.allowedProperties = Sets.newIdentityHashSet();
         this.allowedProperties.addAll(block.getStateDefinition().getProperties());
      }

      public LootItemBlockStatePropertyCondition.Builder withProperty(Property property, Comparable comparable) {
         if(!this.allowedProperties.contains(property)) {
            throw new IllegalArgumentException("Block " + Registry.BLOCK.getKey(this.block) + " does not have property \'" + property + "\'");
         } else if(!property.getPossibleValues().contains(comparable)) {
            throw new IllegalArgumentException("Block " + Registry.BLOCK.getKey(this.block) + " property \'" + property + "\' does not have value \'" + comparable + "\'");
         } else {
            this.properties.put(property, comparable);
            return this;
         }
      }

      public LootItemCondition build() {
         return new LootItemBlockStatePropertyCondition(this.block, this.properties);
      }
   }

   public static class Serializer extends LootItemCondition.Serializer {
      private static String valueToString(Property property, Object object) {
         return property.getName((Comparable)object);
      }

      protected Serializer() {
         super(new ResourceLocation("block_state_property"), LootItemBlockStatePropertyCondition.class);
      }

      public void serialize(JsonObject jsonObject, LootItemBlockStatePropertyCondition lootItemBlockStatePropertyCondition, JsonSerializationContext jsonSerializationContext) {
         jsonObject.addProperty("block", Registry.BLOCK.getKey(lootItemBlockStatePropertyCondition.block).toString());
         JsonObject jsonObject = new JsonObject();
         lootItemBlockStatePropertyCondition.properties.forEach((property, object) -> {
            jsonObject.addProperty(property.getName(), valueToString(property, object));
         });
         jsonObject.add("properties", jsonObject);
      }

      public LootItemBlockStatePropertyCondition deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
         ResourceLocation var3 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "block"));
         Block var4 = (Block)Registry.BLOCK.getOptional(var3).orElseThrow(() -> {
            return new IllegalArgumentException("Can\'t find block " + var3);
         });
         StateDefinition<Block, BlockState> var5 = var4.getStateDefinition();
         Map<Property<?>, Object> var6 = Maps.newHashMap();
         if(jsonObject.has("properties")) {
            JsonObject var7 = GsonHelper.getAsJsonObject(jsonObject, "properties");
            var7.entrySet().forEach((map$Entry) -> {
               String var4 = (String)map$Entry.getKey();
               Property<?> var5 = var5.getProperty(var4);
               if(var5 == null) {
                  throw new IllegalArgumentException("Block " + Registry.BLOCK.getKey(var4) + " does not have property \'" + var4 + "\'");
               } else {
                  String var6 = GsonHelper.convertToString((JsonElement)map$Entry.getValue(), "value");
                  Object var7 = var5.getValue(var6).orElseThrow(() -> {
                     return new IllegalArgumentException("Block " + Registry.BLOCK.getKey(var4) + " property \'" + var4x + "\' does not have value \'" + var6x + "\'");
                  });
                  var6.put(var5, var7);
               }
            });
         }

         return new LootItemBlockStatePropertyCondition(var4, var6);
      }

      // $FF: synthetic method
      public LootItemCondition deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
