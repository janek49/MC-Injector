package net.minecraft.data.recipes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
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

public class ShapedRecipeBuilder {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Item result;
   private final int count;
   private final List rows = Lists.newArrayList();
   private final Map key = Maps.newLinkedHashMap();
   private final Advancement.Builder advancement = Advancement.Builder.advancement();
   private String group;

   public ShapedRecipeBuilder(ItemLike itemLike, int count) {
      this.result = itemLike.asItem();
      this.count = count;
   }

   public static ShapedRecipeBuilder shaped(ItemLike itemLike) {
      return shaped(itemLike, 1);
   }

   public static ShapedRecipeBuilder shaped(ItemLike itemLike, int var1) {
      return new ShapedRecipeBuilder(itemLike, var1);
   }

   public ShapedRecipeBuilder define(Character character, Tag tag) {
      return this.define(character, Ingredient.of(tag));
   }

   public ShapedRecipeBuilder define(Character character, ItemLike itemLike) {
      return this.define(character, Ingredient.of(new ItemLike[]{itemLike}));
   }

   public ShapedRecipeBuilder define(Character character, Ingredient ingredient) {
      if(this.key.containsKey(character)) {
         throw new IllegalArgumentException("Symbol \'" + character + "\' is already defined!");
      } else if(character.charValue() == 32) {
         throw new IllegalArgumentException("Symbol \' \' (whitespace) is reserved and cannot be defined");
      } else {
         this.key.put(character, ingredient);
         return this;
      }
   }

   public ShapedRecipeBuilder pattern(String string) {
      if(!this.rows.isEmpty() && string.length() != ((String)this.rows.get(0)).length()) {
         throw new IllegalArgumentException("Pattern must be the same width on every line!");
      } else {
         this.rows.add(string);
         return this;
      }
   }

   public ShapedRecipeBuilder unlocks(String string, CriterionTriggerInstance criterionTriggerInstance) {
      this.advancement.addCriterion(string, criterionTriggerInstance);
      return this;
   }

   public ShapedRecipeBuilder group(String group) {
      this.group = group;
      return this;
   }

   public void save(Consumer consumer) {
      this.save(consumer, Registry.ITEM.getKey(this.result));
   }

   public void save(Consumer consumer, String string) {
      ResourceLocation var3 = Registry.ITEM.getKey(this.result);
      if((new ResourceLocation(string)).equals(var3)) {
         throw new IllegalStateException("Shaped Recipe " + string + " should remove its \'save\' argument");
      } else {
         this.save(consumer, new ResourceLocation(string));
      }
   }

   public void save(Consumer consumer, ResourceLocation resourceLocation) {
      this.ensureValid(resourceLocation);
      this.advancement.parent(new ResourceLocation("recipes/root")).addCriterion("has_the_recipe", (CriterionTriggerInstance)(new RecipeUnlockedTrigger.TriggerInstance(resourceLocation))).rewards(AdvancementRewards.Builder.recipe(resourceLocation)).requirements(RequirementsStrategy.OR);
      consumer.accept(new ShapedRecipeBuilder.Result(resourceLocation, this.result, this.count, this.group == null?"":this.group, this.rows, this.key, this.advancement, new ResourceLocation(resourceLocation.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + resourceLocation.getPath())));
   }

   private void ensureValid(ResourceLocation resourceLocation) {
      if(this.rows.isEmpty()) {
         throw new IllegalStateException("No pattern is defined for shaped recipe " + resourceLocation + "!");
      } else {
         Set<Character> var2 = Sets.newHashSet(this.key.keySet());
         var2.remove(Character.valueOf(' '));

         for(String var4 : this.rows) {
            for(int var5 = 0; var5 < var4.length(); ++var5) {
               char var6 = var4.charAt(var5);
               if(!this.key.containsKey(Character.valueOf(var6)) && var6 != 32) {
                  throw new IllegalStateException("Pattern in recipe " + resourceLocation + " uses undefined symbol \'" + var6 + "\'");
               }

               var2.remove(Character.valueOf(var6));
            }
         }

         if(!var2.isEmpty()) {
            throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + resourceLocation);
         } else if(this.rows.size() == 1 && ((String)this.rows.get(0)).length() == 1) {
            throw new IllegalStateException("Shaped recipe " + resourceLocation + " only takes in a single item - should it be a shapeless recipe instead?");
         } else if(this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + resourceLocation);
         }
      }
   }

   class Result implements FinishedRecipe {
      private final ResourceLocation id;
      private final Item result;
      private final int count;
      private final String group;
      private final List pattern;
      private final Map key;
      private final Advancement.Builder advancement;
      private final ResourceLocation advancementId;

      public Result(ResourceLocation id, Item result, int count, String group, List pattern, Map key, Advancement.Builder advancement, ResourceLocation advancementId) {
         this.id = id;
         this.result = result;
         this.count = count;
         this.group = group;
         this.pattern = pattern;
         this.key = key;
         this.advancement = advancement;
         this.advancementId = advancementId;
      }

      public void serializeRecipeData(JsonObject jsonObject) {
         if(!this.group.isEmpty()) {
            jsonObject.addProperty("group", this.group);
         }

         JsonArray var2 = new JsonArray();

         for(String var4 : this.pattern) {
            var2.add(var4);
         }

         jsonObject.add("pattern", var2);
         JsonObject var3 = new JsonObject();

         for(Entry<Character, Ingredient> var5 : this.key.entrySet()) {
            var3.add(String.valueOf(var5.getKey()), ((Ingredient)var5.getValue()).toJson());
         }

         jsonObject.add("key", var3);
         JsonObject var4 = new JsonObject();
         var4.addProperty("item", Registry.ITEM.getKey(this.result).toString());
         if(this.count > 1) {
            var4.addProperty("count", Integer.valueOf(this.count));
         }

         jsonObject.add("result", var4);
      }

      public RecipeSerializer getType() {
         return RecipeSerializer.SHAPED_RECIPE;
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
