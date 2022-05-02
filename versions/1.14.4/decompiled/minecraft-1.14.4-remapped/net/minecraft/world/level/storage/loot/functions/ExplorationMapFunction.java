package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Locale;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExplorationMapFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final MapDecoration.Type DEFAULT_DECORATION = MapDecoration.Type.MANSION;
   private final String destination;
   private final MapDecoration.Type mapDecoration;
   private final byte zoom;
   private final int searchRadius;
   private final boolean skipKnownStructures;

   private ExplorationMapFunction(LootItemCondition[] lootItemConditions, String destination, MapDecoration.Type mapDecoration, byte zoom, int searchRadius, boolean skipKnownStructures) {
      super(lootItemConditions);
      this.destination = destination;
      this.mapDecoration = mapDecoration;
      this.zoom = zoom;
      this.searchRadius = searchRadius;
      this.skipKnownStructures = skipKnownStructures;
   }

   public Set getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.BLOCK_POS);
   }

   public ItemStack run(ItemStack var1, LootContext lootContext) {
      if(var1.getItem() != Items.MAP) {
         return var1;
      } else {
         BlockPos var3 = (BlockPos)lootContext.getParamOrNull(LootContextParams.BLOCK_POS);
         if(var3 != null) {
            ServerLevel var4 = lootContext.getLevel();
            BlockPos var5 = var4.findNearestMapFeature(this.destination, var3, this.searchRadius, this.skipKnownStructures);
            if(var5 != null) {
               ItemStack var6 = MapItem.create(var4, var5.getX(), var5.getZ(), this.zoom, true, true);
               MapItem.renderBiomePreviewMap(var4, var6);
               MapItemSavedData.addTargetDecoration(var6, var5, "+", this.mapDecoration);
               var6.setHoverName(new TranslatableComponent("filled_map." + this.destination.toLowerCase(Locale.ROOT), new Object[0]));
               return var6;
            }
         }

         return var1;
      }
   }

   public static ExplorationMapFunction.Builder makeExplorationMap() {
      return new ExplorationMapFunction.Builder();
   }

   public static class Builder extends LootItemConditionalFunction.Builder {
      private String destination = "Buried_Treasure";
      private MapDecoration.Type mapDecoration = ExplorationMapFunction.DEFAULT_DECORATION;
      private byte zoom = 2;
      private int searchRadius = 50;
      private boolean skipKnownStructures = true;

      protected ExplorationMapFunction.Builder getThis() {
         return this;
      }

      public ExplorationMapFunction.Builder setDestination(String destination) {
         this.destination = destination;
         return this;
      }

      public ExplorationMapFunction.Builder setMapDecoration(MapDecoration.Type mapDecoration) {
         this.mapDecoration = mapDecoration;
         return this;
      }

      public ExplorationMapFunction.Builder setZoom(byte zoom) {
         this.zoom = zoom;
         return this;
      }

      public ExplorationMapFunction.Builder setSkipKnownStructures(boolean skipKnownStructures) {
         this.skipKnownStructures = skipKnownStructures;
         return this;
      }

      public LootItemFunction build() {
         return new ExplorationMapFunction(this.getConditions(), this.destination, this.mapDecoration, this.zoom, this.searchRadius, this.skipKnownStructures);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      protected Serializer() {
         super(new ResourceLocation("exploration_map"), ExplorationMapFunction.class);
      }

      public void serialize(JsonObject jsonObject, ExplorationMapFunction explorationMapFunction, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootItemConditionalFunction)explorationMapFunction, jsonSerializationContext);
         if(!explorationMapFunction.destination.equals("Buried_Treasure")) {
            jsonObject.add("destination", jsonSerializationContext.serialize(explorationMapFunction.destination));
         }

         if(explorationMapFunction.mapDecoration != ExplorationMapFunction.DEFAULT_DECORATION) {
            jsonObject.add("decoration", jsonSerializationContext.serialize(explorationMapFunction.mapDecoration.toString().toLowerCase(Locale.ROOT)));
         }

         if(explorationMapFunction.zoom != 2) {
            jsonObject.addProperty("zoom", Byte.valueOf(explorationMapFunction.zoom));
         }

         if(explorationMapFunction.searchRadius != 50) {
            jsonObject.addProperty("search_radius", Integer.valueOf(explorationMapFunction.searchRadius));
         }

         if(!explorationMapFunction.skipKnownStructures) {
            jsonObject.addProperty("skip_existing_chunks", Boolean.valueOf(explorationMapFunction.skipKnownStructures));
         }

      }

      public ExplorationMapFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         String var4 = jsonObject.has("destination")?GsonHelper.getAsString(jsonObject, "destination"):"Buried_Treasure";
         var4 = Feature.STRUCTURES_REGISTRY.containsKey(var4.toLowerCase(Locale.ROOT))?var4:"Buried_Treasure";
         String var5 = jsonObject.has("decoration")?GsonHelper.getAsString(jsonObject, "decoration"):"mansion";
         MapDecoration.Type var6 = ExplorationMapFunction.DEFAULT_DECORATION;

         try {
            var6 = MapDecoration.Type.valueOf(var5.toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var10) {
            ExplorationMapFunction.LOGGER.error("Error while parsing loot table decoration entry. Found {}. Defaulting to " + ExplorationMapFunction.DEFAULT_DECORATION, var5);
         }

         byte var7 = GsonHelper.getAsByte(jsonObject, "zoom", (byte)2);
         int var8 = GsonHelper.getAsInt(jsonObject, "search_radius", 50);
         boolean var9 = GsonHelper.getAsBoolean(jsonObject, "skip_existing_chunks", true);
         return new ExplorationMapFunction(lootItemConditions, var4, var6, var7, var8, var9);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
