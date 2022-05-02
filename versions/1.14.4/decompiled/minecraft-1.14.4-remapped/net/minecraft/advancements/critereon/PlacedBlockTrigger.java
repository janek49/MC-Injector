package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class PlacedBlockTrigger implements CriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("placed_block");
   private final Map players = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addPlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      PlacedBlockTrigger.PlayerListeners var3 = (PlacedBlockTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 == null) {
         var3 = new PlacedBlockTrigger.PlayerListeners(playerAdvancements);
         this.players.put(playerAdvancements, var3);
      }

      var3.addListener(criterionTrigger$Listener);
   }

   public void removePlayerListener(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener criterionTrigger$Listener) {
      PlacedBlockTrigger.PlayerListeners var3 = (PlacedBlockTrigger.PlayerListeners)this.players.get(playerAdvancements);
      if(var3 != null) {
         var3.removeListener(criterionTrigger$Listener);
         if(var3.isEmpty()) {
            this.players.remove(playerAdvancements);
         }
      }

   }

   public void removePlayerListeners(PlayerAdvancements playerAdvancements) {
      this.players.remove(playerAdvancements);
   }

   public PlacedBlockTrigger.TriggerInstance createInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
      Block var3 = null;
      if(jsonObject.has("block")) {
         ResourceLocation var4 = new ResourceLocation(GsonHelper.getAsString(jsonObject, "block"));
         var3 = (Block)Registry.BLOCK.getOptional(var4).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type \'" + var4 + "\'");
         });
      }

      Map<Property<?>, Object> var4 = null;
      if(jsonObject.has("state")) {
         if(var3 == null) {
            throw new JsonSyntaxException("Can\'t define block state without a specific block type");
         }

         StateDefinition<Block, BlockState> var5 = var3.getStateDefinition();

         for(Entry<String, JsonElement> var7 : GsonHelper.getAsJsonObject(jsonObject, "state").entrySet()) {
            Property<?> var8 = var5.getProperty((String)var7.getKey());
            if(var8 == null) {
               throw new JsonSyntaxException("Unknown block state property \'" + (String)var7.getKey() + "\' for block \'" + Registry.BLOCK.getKey(var3) + "\'");
            }

            String var9 = GsonHelper.convertToString((JsonElement)var7.getValue(), (String)var7.getKey());
            Optional<?> var10 = var8.getValue(var9);
            if(!var10.isPresent()) {
               throw new JsonSyntaxException("Invalid block state value \'" + var9 + "\' for property \'" + (String)var7.getKey() + "\' on block \'" + Registry.BLOCK.getKey(var3) + "\'");
            }

            if(var4 == null) {
               var4 = Maps.newHashMap();
            }

            var4.put(var8, var10.get());
         }
      }

      LocationPredicate var5 = LocationPredicate.fromJson(jsonObject.get("location"));
      ItemPredicate var6 = ItemPredicate.fromJson(jsonObject.get("item"));
      return new PlacedBlockTrigger.TriggerInstance(var3, var4, var5, var6);
   }

   public void trigger(ServerPlayer serverPlayer, BlockPos blockPos, ItemStack itemStack) {
      BlockState var4 = serverPlayer.level.getBlockState(blockPos);
      PlacedBlockTrigger.PlayerListeners var5 = (PlacedBlockTrigger.PlayerListeners)this.players.get(serverPlayer.getAdvancements());
      if(var5 != null) {
         var5.trigger(var4, blockPos, serverPlayer.getLevel(), itemStack);
      }

   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   static class PlayerListeners {
      private final PlayerAdvancements player;
      private final Set listeners = Sets.newHashSet();

      public PlayerListeners(PlayerAdvancements player) {
         this.player = player;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void addListener(CriterionTrigger.Listener criterionTrigger$Listener) {
         this.listeners.add(criterionTrigger$Listener);
      }

      public void removeListener(CriterionTrigger.Listener criterionTrigger$Listener) {
         this.listeners.remove(criterionTrigger$Listener);
      }

      public void trigger(BlockState blockState, BlockPos blockPos, ServerLevel serverLevel, ItemStack itemStack) {
         List<CriterionTrigger.Listener<PlacedBlockTrigger.TriggerInstance>> var5 = null;

         for(CriterionTrigger.Listener<PlacedBlockTrigger.TriggerInstance> var7 : this.listeners) {
            if(((PlacedBlockTrigger.TriggerInstance)var7.getTriggerInstance()).matches(blockState, blockPos, serverLevel, itemStack)) {
               if(var5 == null) {
                  var5 = Lists.newArrayList();
               }

               var5.add(var7);
            }
         }

         if(var5 != null) {
            for(CriterionTrigger.Listener<PlacedBlockTrigger.TriggerInstance> var7 : var5) {
               var7.run(this.player);
            }
         }

      }
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Block block;
      private final Map state;
      private final LocationPredicate location;
      private final ItemPredicate item;

      public TriggerInstance(@Nullable Block block, @Nullable Map state, LocationPredicate location, ItemPredicate item) {
         super(PlacedBlockTrigger.ID);
         this.block = block;
         this.state = state;
         this.location = location;
         this.item = item;
      }

      public static PlacedBlockTrigger.TriggerInstance placedBlock(Block block) {
         return new PlacedBlockTrigger.TriggerInstance(block, (Map)null, LocationPredicate.ANY, ItemPredicate.ANY);
      }

      public boolean matches(BlockState blockState, BlockPos blockPos, ServerLevel serverLevel, ItemStack itemStack) {
         if(this.block != null && blockState.getBlock() != this.block) {
            return false;
         } else {
            if(this.state != null) {
               for(Entry<Property<?>, Object> var6 : this.state.entrySet()) {
                  if(blockState.getValue((Property)var6.getKey()) != var6.getValue()) {
                     return false;
                  }
               }
            }

            return !this.location.matches(serverLevel, (float)blockPos.getX(), (float)blockPos.getY(), (float)blockPos.getZ())?false:this.item.matches(itemStack);
         }
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         if(this.block != null) {
            var1.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
         }

         if(this.state != null) {
            JsonObject var2 = new JsonObject();

            for(Entry<Property<?>, Object> var4 : this.state.entrySet()) {
               var2.addProperty(((Property)var4.getKey()).getName(), Util.getPropertyName((Property)var4.getKey(), var4.getValue()));
            }

            var1.add("state", var2);
         }

         var1.add("location", this.location.serializeToJson());
         var1.add("item", this.item.serializeToJson());
         return var1;
      }
   }
}
