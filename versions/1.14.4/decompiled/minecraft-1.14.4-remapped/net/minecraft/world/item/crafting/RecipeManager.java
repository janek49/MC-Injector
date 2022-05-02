package net.minecraft.world.item.crafting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeManager extends SimpleJsonResourceReloadListener {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private static final Logger LOGGER = LogManager.getLogger();
   private Map recipes = ImmutableMap.of();
   private boolean hasErrors;

   public RecipeManager() {
      super(GSON, "recipes");
   }

   protected void apply(Map map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
      this.hasErrors = false;
      Map<RecipeType<?>, Builder<ResourceLocation, Recipe<?>>> map = Maps.newHashMap();

      for(Entry<ResourceLocation, JsonObject> var6 : map.entrySet()) {
         ResourceLocation var7 = (ResourceLocation)var6.getKey();

         try {
            Recipe<?> var8 = fromJson(var7, (JsonObject)var6.getValue());
            ((Builder)map.computeIfAbsent(var8.getType(), (recipeType) -> {
               return ImmutableMap.builder();
            })).put(var7, var8);
         } catch (IllegalArgumentException | JsonParseException var9) {
            LOGGER.error("Parsing error loading recipe {}", var7, var9);
         }
      }

      this.recipes = (Map)map.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (map$Entry) -> {
         return ((Builder)map$Entry.getValue()).build();
      }));
      LOGGER.info("Loaded {} recipes", Integer.valueOf(map.size()));
   }

   public Optional getRecipeFor(RecipeType recipeType, Container container, Level level) {
      return this.byType(recipeType).values().stream().flatMap((recipe) -> {
         return Util.toStream(recipeType.tryMatch(recipe, level, container));
      }).findFirst();
   }

   public List getRecipesFor(RecipeType recipeType, Container container, Level level) {
      return (List)this.byType(recipeType).values().stream().flatMap((recipe) -> {
         return Util.toStream(recipeType.tryMatch(recipe, level, container));
      }).sorted(Comparator.comparing((recipe) -> {
         return recipe.getResultItem().getDescriptionId();
      })).collect(Collectors.toList());
   }

   private Map byType(RecipeType type) {
      return (Map)this.recipes.getOrDefault(type, Collections.emptyMap());
   }

   public NonNullList getRemainingItemsFor(RecipeType recipeType, Container container, Level level) {
      Optional<T> var4 = this.getRecipeFor(recipeType, container, level);
      if(var4.isPresent()) {
         return ((Recipe)var4.get()).getRemainingItems(container);
      } else {
         NonNullList<ItemStack> var5 = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            var5.set(var6, container.getItem(var6));
         }

         return var5;
      }
   }

   public Optional byKey(ResourceLocation key) {
      return this.recipes.values().stream().map((map) -> {
         return (Recipe)map.get(key);
      }).filter(Objects::nonNull).findFirst();
   }

   public Collection getRecipes() {
      return (Collection)this.recipes.values().stream().flatMap((map) -> {
         return map.values().stream();
      }).collect(Collectors.toSet());
   }

   public Stream getRecipeIds() {
      return this.recipes.values().stream().flatMap((map) -> {
         return map.keySet().stream();
      });
   }

   public static Recipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
      String var2 = GsonHelper.getAsString(jsonObject, "type");
      return ((RecipeSerializer)Registry.RECIPE_SERIALIZER.getOptional(new ResourceLocation(var2)).orElseThrow(() -> {
         return new JsonSyntaxException("Invalid or unsupported recipe type \'" + var2 + "\'");
      })).fromJson(resourceLocation, jsonObject);
   }

   public void replaceRecipes(Iterable iterable) {
      this.hasErrors = false;
      Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> var2 = Maps.newHashMap();
      iterable.forEach((recipe) -> {
         Map<ResourceLocation, Recipe<?>> map = (Map)var2.computeIfAbsent(recipe.getType(), (recipeType) -> {
            return Maps.newHashMap();
         });
         Recipe<?> var3 = (Recipe)map.put(recipe.getId(), recipe);
         if(var3 != null) {
            throw new IllegalStateException("Duplicate recipe ignored with ID " + recipe.getId());
         }
      });
      this.recipes = ImmutableMap.copyOf(var2);
   }
}
