package net.minecraft.data.recipes;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.level.ItemLike;

public class SimpleCookingRecipeBuilder {
   private final Item result;
   private final Ingredient ingredient;
   private final float experience;
   private final int cookingTime;
   private final Advancement.Builder advancement = Advancement.Builder.advancement();
   private String group;
   private final SimpleCookingSerializer serializer;

   private SimpleCookingRecipeBuilder(ItemLike itemLike, Ingredient ingredient, float experience, int cookingTime, SimpleCookingSerializer serializer) {
      this.result = itemLike.asItem();
      this.ingredient = ingredient;
      this.experience = experience;
      this.cookingTime = cookingTime;
      this.serializer = serializer;
   }

   public static SimpleCookingRecipeBuilder cooking(Ingredient ingredient, ItemLike itemLike, float var2, int var3, SimpleCookingSerializer simpleCookingSerializer) {
      return new SimpleCookingRecipeBuilder(itemLike, ingredient, var2, var3, simpleCookingSerializer);
   }

   public static SimpleCookingRecipeBuilder blasting(Ingredient ingredient, ItemLike itemLike, float var2, int var3) {
      return cooking(ingredient, itemLike, var2, var3, RecipeSerializer.BLASTING_RECIPE);
   }

   public static SimpleCookingRecipeBuilder smelting(Ingredient ingredient, ItemLike itemLike, float var2, int var3) {
      return cooking(ingredient, itemLike, var2, var3, RecipeSerializer.SMELTING_RECIPE);
   }

   public SimpleCookingRecipeBuilder unlocks(String string, CriterionTriggerInstance criterionTriggerInstance) {
      this.advancement.addCriterion(string, criterionTriggerInstance);
      return this;
   }

   public void save(Consumer consumer) {
      this.save(consumer, Registry.ITEM.getKey(this.result));
   }

   public void save(Consumer consumer, String string) {
      ResourceLocation var3 = Registry.ITEM.getKey(this.result);
      ResourceLocation var4 = new ResourceLocation(string);
      if(var4.equals(var3)) {
         throw new IllegalStateException("Recipe " + var4 + " should remove its \'save\' argument");
      } else {
         this.save(consumer, var4);
      }
   }

   public void save(Consumer consumer, ResourceLocation resourceLocation) {
      this.ensureValid(resourceLocation);
      this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", (CriterionTriggerInstance)(new RecipeUnlockedTrigger.TriggerInstance(resourceLocation))).rewards(AdvancementRewards.Builder.recipe(resourceLocation)).requirements(RequirementsStrategy.OR);
      consumer.accept(new SimpleCookingRecipeBuilder.Result(resourceLocation, this.group == null?"":this.group, this.ingredient, this.result, this.experience, this.cookingTime, this.advancement, new ResourceLocation(resourceLocation.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + resourceLocation.getPath()), this.serializer));
   }

   private void ensureValid(ResourceLocation resourceLocation) {
      if(this.advancement.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + resourceLocation);
      }
   }

   public static class Result implements FinishedRecipe {
      private final ResourceLocation id;
      private final String group;
      private final Ingredient ingredient;
      private final Item result;
      private final float experience;
      private final int cookingTime;
      private final Advancement.Builder advancement;
      private final ResourceLocation advancementId;
      private final RecipeSerializer serializer;

      public Result(ResourceLocation id, String group, Ingredient ingredient, Item result, float experience, int cookingTime, Advancement.Builder advancement, ResourceLocation advancementId, RecipeSerializer serializer) {
         this.id = id;
         this.group = group;
         this.ingredient = ingredient;
         this.result = result;
         this.experience = experience;
         this.cookingTime = cookingTime;
         this.advancement = advancement;
         this.advancementId = advancementId;
         this.serializer = serializer;
      }

      public void serializeRecipeData(JsonObject jsonObject) {
         if(!this.group.isEmpty()) {
            jsonObject.addProperty("group", this.group);
         }

         jsonObject.add("ingredient", this.ingredient.toJson());
         jsonObject.addProperty("result", Registry.ITEM.getKey(this.result).toString());
         jsonObject.addProperty("experience", Float.valueOf(this.experience));
         jsonObject.addProperty("cookingtime", Integer.valueOf(this.cookingTime));
      }

      public RecipeSerializer getType() {
         return this.serializer;
      }

      public ResourceLocation getId() {
         return this.id;
      }

      @Nullable
      public JsonObject serializeAdvancement() {
         return this.advancement.serializeToJson();
      }

      @Nullable
      public ResourceLocation getAdvancementId() {
         return this.advancementId;
      }
   }
}
