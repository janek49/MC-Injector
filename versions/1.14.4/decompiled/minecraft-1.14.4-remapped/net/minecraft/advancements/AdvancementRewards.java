package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class AdvancementRewards {
   public static final AdvancementRewards EMPTY = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], CommandFunction.CacheableFunction.NONE);
   private final int experience;
   private final ResourceLocation[] loot;
   private final ResourceLocation[] recipes;
   private final CommandFunction.CacheableFunction function;

   public AdvancementRewards(int experience, ResourceLocation[] loot, ResourceLocation[] recipes, CommandFunction.CacheableFunction function) {
      this.experience = experience;
      this.loot = loot;
      this.recipes = recipes;
      this.function = function;
   }

   public void grant(ServerPlayer serverPlayer) {
      serverPlayer.giveExperiencePoints(this.experience);
      LootContext var2 = (new LootContext.Builder(serverPlayer.getLevel())).withParameter(LootContextParams.THIS_ENTITY, serverPlayer).withParameter(LootContextParams.BLOCK_POS, new BlockPos(serverPlayer)).withRandom(serverPlayer.getRandom()).create(LootContextParamSets.ADVANCEMENT_REWARD);
      boolean var3 = false;

      for(ResourceLocation var7 : this.loot) {
         for(ItemStack var9 : serverPlayer.server.getLootTables().get(var7).getRandomItems(var2)) {
            if(serverPlayer.addItem(var9)) {
               serverPlayer.level.playSound((Player)null, serverPlayer.x, serverPlayer.y, serverPlayer.z, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((serverPlayer.getRandom().nextFloat() - serverPlayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
               var3 = true;
            } else {
               ItemEntity var10 = serverPlayer.drop(var9, false);
               if(var10 != null) {
                  var10.setNoPickUpDelay();
                  var10.setOwner(serverPlayer.getUUID());
               }
            }
         }
      }

      if(var3) {
         serverPlayer.inventoryMenu.broadcastChanges();
      }

      if(this.recipes.length > 0) {
         serverPlayer.awardRecipesByKey(this.recipes);
      }

      MinecraftServer var4 = serverPlayer.server;
      this.function.get(var4.getFunctions()).ifPresent((commandFunction) -> {
         var11.getFunctions().execute(commandFunction, serverPlayer.createCommandSourceStack().withSuppressedOutput().withPermission(2));
      });
   }

   public String toString() {
      return "AdvancementRewards{experience=" + this.experience + ", loot=" + Arrays.toString(this.loot) + ", recipes=" + Arrays.toString(this.recipes) + ", function=" + this.function + '}';
   }

   public JsonElement serializeToJson() {
      if(this == EMPTY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         if(this.experience != 0) {
            var1.addProperty("experience", Integer.valueOf(this.experience));
         }

         if(this.loot.length > 0) {
            JsonArray var2 = new JsonArray();

            for(ResourceLocation var6 : this.loot) {
               var2.add(var6.toString());
            }

            var1.add("loot", var2);
         }

         if(this.recipes.length > 0) {
            JsonArray var2 = new JsonArray();

            for(ResourceLocation var6 : this.recipes) {
               var2.add(var6.toString());
            }

            var1.add("recipes", var2);
         }

         if(this.function.getId() != null) {
            var1.addProperty("function", this.function.getId().toString());
         }

         return var1;
      }
   }

   public static class Builder {
      private int experience;
      private final List loot = Lists.newArrayList();
      private final List recipes = Lists.newArrayList();
      @Nullable
      private ResourceLocation function;

      public static AdvancementRewards.Builder experience(int i) {
         return (new AdvancementRewards.Builder()).addExperience(i);
      }

      public AdvancementRewards.Builder addExperience(int i) {
         this.experience += i;
         return this;
      }

      public static AdvancementRewards.Builder recipe(ResourceLocation resourceLocation) {
         return (new AdvancementRewards.Builder()).addRecipe(resourceLocation);
      }

      public AdvancementRewards.Builder addRecipe(ResourceLocation resourceLocation) {
         this.recipes.add(resourceLocation);
         return this;
      }

      public AdvancementRewards build() {
         return new AdvancementRewards(this.experience, (ResourceLocation[])this.loot.toArray(new ResourceLocation[0]), (ResourceLocation[])this.recipes.toArray(new ResourceLocation[0]), this.function == null?CommandFunction.CacheableFunction.NONE:new CommandFunction.CacheableFunction(this.function));
      }
   }

   public static class Deserializer implements JsonDeserializer {
      public AdvancementRewards deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject var4 = GsonHelper.convertToJsonObject(jsonElement, "rewards");
         int var5 = GsonHelper.getAsInt(var4, "experience", 0);
         JsonArray var6 = GsonHelper.getAsJsonArray(var4, "loot", new JsonArray());
         ResourceLocation[] vars7 = new ResourceLocation[var6.size()];

         for(int var8 = 0; var8 < vars7.length; ++var8) {
            vars7[var8] = new ResourceLocation(GsonHelper.convertToString(var6.get(var8), "loot[" + var8 + "]"));
         }

         JsonArray var8 = GsonHelper.getAsJsonArray(var4, "recipes", new JsonArray());
         ResourceLocation[] vars9 = new ResourceLocation[var8.size()];

         for(int var10 = 0; var10 < vars9.length; ++var10) {
            vars9[var10] = new ResourceLocation(GsonHelper.convertToString(var8.get(var10), "recipes[" + var10 + "]"));
         }

         CommandFunction.CacheableFunction var10;
         if(var4.has("function")) {
            var10 = new CommandFunction.CacheableFunction(new ResourceLocation(GsonHelper.getAsString(var4, "function")));
         } else {
            var10 = CommandFunction.CacheableFunction.NONE;
         }

         return new AdvancementRewards(var5, vars7, vars9, var10);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
