package net.minecraft.data.recipes;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
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
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShapelessRecipeBuilder {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Item result;
   private final int count;
   private final List ingredients = Lists.newArrayList();
   private final Advancement.Builder advancement = Advancement.Builder.advancement();
   private String group;

   public ShapelessRecipeBuilder(ItemLike itemLike, int count) {
      this.result = itemLike.asItem();
      this.count = count;
   }

   public static ShapelessRecipeBuilder shapeless(ItemLike itemLike) {
      return new ShapelessRecipeBuilder(itemLike, 1);
   }

   public static ShapelessRecipeBuilder shapeless(ItemLike itemLike, int var1) {
      return new ShapelessRecipeBuilder(itemLike, var1);
   }

   public ShapelessRecipeBuilder requires(Tag tag) {
      return this.requires(Ingredient.of(tag));
   }

   public ShapelessRecipeBuilder requires(ItemLike itemLike) {
      return this.requires((ItemLike)itemLike, 1);
   }

   public ShapelessRecipeBuilder requires(ItemLike itemLike, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         this.requires(Ingredient.of(new ItemLike[]{itemLike}));
      }

      return this;
   }

   public ShapelessRecipeBuilder requires(Ingredient ingredient) {
      return this.requires((Ingredient)ingredient, 1);
   }

   public ShapelessRecipeBuilder requires(Ingredient ingredient, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         this.ingredients.add(ingredient);
      }

      return this;
   }

   public ShapelessRecipeBuilder unlocks(String string, CriterionTriggerInstance criterionTriggerInstance) {
      this.advancement.addCriterion(string, criterionTriggerInstance);
      return this;
   }

   public ShapelessRecipeBuilder group(String group) {
      this.group = group;
      return this;
   }

   public void save(Consumer consumer) {
      this.save(consumer, Registry.ITEM.getKey(this.result));
   }

   public void save(Consumer consumer, String string) {
      ResourceLocation var3 = Registry.ITEM.getKey(this.result);
      if((new ResourceLocation(string)).equals(var3)) {
         throw new IllegalStateException("Shapeless Recipe " + string + " should remove its \'save\' argument");
      } else {
         this.save(consumer, new ResourceLocation(string));
      }
   }

   public void save(Consumer consumer, ResourceLocation resourceLocation) {
      this.ensureValid(resourceLocation);
      this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", (CriterionTriggerInstance)(new RecipeUnlockedTrigger.TriggerInstance(resourceLocation))).rewards(AdvancementRewards.Builder.recipe(resourceLocation)).requirements(RequirementsStrategy.OR);
      consumer.accept(new ShapelessRecipeBuilder.Result(resourceLocation, this.result, this.count, this.group == null?"":this.group, this.ingredients, this.advancement, new ResourceLocation(resourceLocation.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + resourceLocation.getPath())));
   }

   private void ensureValid(ResourceLocation resourceLocation) {
      if(this.advancement.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + resourceLocation);
      }
   }

   public static class Result implements FinishedRecipe {
      private final ResourceLocation id;
      private final Item result;
      private final int count;
      private final String group;
      private final List ingredients;
      private final Advancement.Builder advancement;
      private final ResourceLocation advancementId;

      public Result(ResourceLocation id, Item result, int count, String group, List ingredients, Advancement.Builder advancement, ResourceLocation advancementId) {
         this.id = id;
         this.result = result;
         this.count = count;
         this.group = group;
         this.ingredients = ingredients;
         this.advancement = advancement;
         this.advancementId = advancementId;
      }

      public void serializeRecipeData(JsonObject jsonObject) {
         if(!this.group.isEmpty()) {
            jsonObject.addProperty("group", this.group);
         }

         JsonArray var2 = new JsonArray();

         for(Ingredient var4 : this.ingredients) {
            var2.add(var4.toJson());
         }

         jsonObject.add("ingredients", var2);
         JsonObject var3 = new JsonObject();
         var3.addProperty("item", Registry.ITEM.getKey(this.result).toString());
         if(this.count > 1) {
            var3.addProperty("count", Integer.valueOf(this.count));
         }

         jsonObject.add("result", var3);
      }

      public RecipeSerializer getType() {
         return RecipeSerializer.SHAPELESS_RECIPE;
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
