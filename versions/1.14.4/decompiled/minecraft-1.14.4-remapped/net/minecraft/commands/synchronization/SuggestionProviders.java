package net.minecraft.commands.synchronization;

import com.google.common.collect.Maps;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class SuggestionProviders {
   private static final Map PROVIDERS_BY_NAME = Maps.newHashMap();
   private static final ResourceLocation DEFAULT_NAME = new ResourceLocation("ask_server");
   public static final SuggestionProvider ASK_SERVER = register(DEFAULT_NAME, (commandContext, suggestionsBuilder) -> {
      return ((SharedSuggestionProvider)commandContext.getSource()).customSuggestion(commandContext, suggestionsBuilder);
   });
   public static final SuggestionProvider ALL_RECIPES = register(new ResourceLocation("all_recipes"), (commandContext, suggestionsBuilder) -> {
      return SharedSuggestionProvider.suggestResource(((SharedSuggestionProvider)commandContext.getSource()).getRecipeNames(), suggestionsBuilder);
   });
   public static final SuggestionProvider AVAILABLE_SOUNDS = register(new ResourceLocation("available_sounds"), (commandContext, suggestionsBuilder) -> {
      return SharedSuggestionProvider.suggestResource((Iterable)((SharedSuggestionProvider)commandContext.getSource()).getAvailableSoundEvents(), suggestionsBuilder);
   });
   public static final SuggestionProvider SUMMONABLE_ENTITIES = register(new ResourceLocation("summonable_entities"), (commandContext, suggestionsBuilder) -> {
      return SharedSuggestionProvider.suggestResource(Registry.ENTITY_TYPE.stream().filter(EntityType::canSummon), suggestionsBuilder, EntityType::getKey, (entityType) -> {
         return new TranslatableComponent(Util.makeDescriptionId("entity", EntityType.getKey(entityType)), new Object[0]);
      });
   });

   public static SuggestionProvider register(ResourceLocation resourceLocation, SuggestionProvider var1) {
      if(PROVIDERS_BY_NAME.containsKey(resourceLocation)) {
         throw new IllegalArgumentException("A command suggestion provider is already registered with the name " + resourceLocation);
      } else {
         PROVIDERS_BY_NAME.put(resourceLocation, var1);
         return new SuggestionProviders.Wrapper(resourceLocation, var1);
      }
   }

   public static SuggestionProvider getProvider(ResourceLocation resourceLocation) {
      return (SuggestionProvider)PROVIDERS_BY_NAME.getOrDefault(resourceLocation, ASK_SERVER);
   }

   public static ResourceLocation getName(SuggestionProvider suggestionProvider) {
      return suggestionProvider instanceof SuggestionProviders.Wrapper?((SuggestionProviders.Wrapper)suggestionProvider).name:DEFAULT_NAME;
   }

   public static SuggestionProvider safelySwap(SuggestionProvider suggestionProvider) {
      return suggestionProvider instanceof SuggestionProviders.Wrapper?suggestionProvider:ASK_SERVER;
   }

   public static class Wrapper implements SuggestionProvider {
      private final SuggestionProvider delegate;
      private final ResourceLocation name;

      public Wrapper(ResourceLocation name, SuggestionProvider delegate) {
         this.delegate = delegate;
         this.name = name;
      }

      public CompletableFuture getSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
         return this.delegate.getSuggestions(commandContext, suggestionsBuilder);
      }
   }
}
