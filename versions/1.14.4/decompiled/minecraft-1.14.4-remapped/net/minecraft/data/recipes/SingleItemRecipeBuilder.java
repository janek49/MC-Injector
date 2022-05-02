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
import net.minecraft.world.level.ItemLike;

public class SingleItemRecipeBuilder {
   private final Item result;
   private final Ingredient ingredient;
   private final int count;
   private final Advancement.Builder advancement = Advancement.Builder.advancement();
   private String group;
   private final RecipeSerializer type;

   public SingleItemRecipeBuilder(RecipeSerializer type, Ingredient ingredient, ItemLike itemLike, int count) {
      this.type = type;
      this.result = itemLike.asItem();
      this.ingredient = ingredient;
      this.count = count;
   }

   public static SingleItemRecipeBuilder stonecutting(Ingredient ingredient, ItemLike itemLike) {
      return new SingleItemRecipeBuilder(RecipeSerializer.STONECUTTER, ingredient, itemLike, 1);
   }

   public static SingleItemRecipeBuilder stonecutting(Ingredient ingredient, ItemLike itemLike, int var2) {
      return new SingleItemRecipeBuilder(RecipeSerializer.STONECUTTER, ingredient, itemLike, var2);
   }

   public SingleItemRecipeBuilder unlocks(String string, CriterionTriggerInstance criterionTriggerInstance) {
      this.advancement.addCriterion(string, criterionTriggerInstance);
      return this;
   }

   public void save(Consumer consumer, String string) {
      ResourceLocation var3 = Registry.ITEM.getKey(this.result);
      if((new ResourceLocation(string)).equals(var3)) {
         throw new IllegalStateException("Single Item Recipe " + string + " should remove its \'save\' argument");
      } else {
         this.save(consumer, new ResourceLocation(string));
      }
   }

   public void save(Consumer consumer, ResourceLocation resourceLocation) {
      this.ensureValid(resourceLocation);
      this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", (CriterionTriggerInstance)(new RecipeUnlockedTrigger.TriggerInstance(resourceLocation))).rewards(AdvancementRewards.Builder.recipe(resourceLocation)).requirements(RequirementsStrategy.OR);
      consumer.accept(new SingleItemRecipeBuilder.Result(resourceLocation, this.type, this.group == null?"":this.group, this.ingredient, this.result, this.count, this.advancement, new ResourceLocation(resourceLocation.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + resourceLocation.getPath())));
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
      private final int count;
      private final Advancement.Builder advancement;
      private final ResourceLocation advancementId;
      private final RecipeSerializer type;

      public Result(ResourceLocation id, RecipeSerializer type, String group, Ingredient ingredient, Item result, int count, Advancement.Builder advancement, ResourceLocation advancementId) {
         this.id = id;
         this.type = type;
         this.group = group;
         this.ingredient = ingredient;
         this.result = result;
         this.count = count;
         this.advancement = advancement;
         this.advancementId = advancementId;
      }

      public void serializeRecipeData(JsonObject jsonObject) {
         if(!this.group.isEmpty()) {
            jsonObject.addProperty("group", this.group);
         }

         jsonObject.add("ingredient", this.ingredient.toJson());
         jsonObject.addProperty("result", Registry.ITEM.getKey(this.result).toString());
         jsonObject.addProperty("count", Integer.valueOf(this.count));
      }

      public ResourceLocation getId() {
         return this.id;
      }

      public RecipeSerializer getType() {
         return this.type;
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
