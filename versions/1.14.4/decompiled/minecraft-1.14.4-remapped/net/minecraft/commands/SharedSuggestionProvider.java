package net.minecraft.commands;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;

public interface SharedSuggestionProvider {
   Collection getOnlinePlayerNames();

   default Collection getSelectedEntities() {
      return Collections.emptyList();
   }

   Collection getAllTeams();

   Collection getAvailableSoundEvents();

   Stream getRecipeNames();

   CompletableFuture customSuggestion(CommandContext var1, SuggestionsBuilder var2);

   default Collection getRelevantCoordinates() {
      return Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_GLOBAL);
   }

   default Collection getAbsoluteCoordinates() {
      return Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_GLOBAL);
   }

   boolean hasPermission(int var1);

   static default void filterResources(Iterable iterable, String string, Function function, Consumer consumer) {
      boolean var4 = string.indexOf(58) > -1;

      for(T var6 : iterable) {
         ResourceLocation var7 = (ResourceLocation)function.apply(var6);
         if(var4) {
            String var8 = var7.toString();
            if(var8.startsWith(string)) {
               consumer.accept(var6);
            }
         } else if(var7.getNamespace().startsWith(string) || var7.getNamespace().equals("minecraft") && var7.getPath().startsWith(string)) {
            consumer.accept(var6);
         }
      }

   }

   static default void filterResources(Iterable iterable, String var1, String var2, Function function, Consumer consumer) {
      if(var1.isEmpty()) {
         iterable.forEach(consumer);
      } else {
         String var5 = Strings.commonPrefix(var1, var2);
         if(!var5.isEmpty()) {
            String var6 = var1.substring(var5.length());
            filterResources(iterable, var6, function, consumer);
         }
      }

   }

   static default CompletableFuture suggestResource(Iterable iterable, SuggestionsBuilder suggestionsBuilder, String string) {
      String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
      filterResources(iterable, string, string, (resourceLocation) -> {
         return resourceLocation;
      }, (resourceLocation) -> {
         suggestionsBuilder.suggest(string + resourceLocation);
      });
      return suggestionsBuilder.buildFuture();
   }

   static default CompletableFuture suggestResource(Iterable iterable, SuggestionsBuilder suggestionsBuilder) {
      String var2 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
      filterResources(iterable, var2, (resourceLocation) -> {
         return resourceLocation;
      }, (resourceLocation) -> {
         suggestionsBuilder.suggest(resourceLocation.toString());
      });
      return suggestionsBuilder.buildFuture();
   }

   static default CompletableFuture suggestResource(Iterable iterable, SuggestionsBuilder suggestionsBuilder, Function var2, Function var3) {
      String var4 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
      filterResources(iterable, var4, var2, (object) -> {
         suggestionsBuilder.suggest(((ResourceLocation)var2.apply(object)).toString(), (Message)var3.apply(object));
      });
      return suggestionsBuilder.buildFuture();
   }

   static default CompletableFuture suggestResource(Stream stream, SuggestionsBuilder suggestionsBuilder) {
      return suggestResource(stream::iterator, suggestionsBuilder);
   }

   static default CompletableFuture suggestResource(Stream stream, SuggestionsBuilder suggestionsBuilder, Function var2, Function var3) {
      return suggestResource(stream::iterator, suggestionsBuilder, var2, var3);
   }

   static default CompletableFuture suggestCoordinates(String string, Collection collection, SuggestionsBuilder suggestionsBuilder, Predicate predicate) {
      List<String> var4 = Lists.newArrayList();
      if(Strings.isNullOrEmpty(string)) {
         for(SharedSuggestionProvider.TextCoordinates var6 : collection) {
            String var7 = var6.x + " " + var6.y + " " + var6.z;
            if(predicate.test(var7)) {
               var4.add(var6.x);
               var4.add(var6.x + " " + var6.y);
               var4.add(var7);
            }
         }
      } else {
         String[] vars5 = string.split(" ");
         if(vars5.length == 1) {
            for(SharedSuggestionProvider.TextCoordinates var7 : collection) {
               String var8 = vars5[0] + " " + var7.y + " " + var7.z;
               if(predicate.test(var8)) {
                  var4.add(vars5[0] + " " + var7.y);
                  var4.add(var8);
               }
            }
         } else if(vars5.length == 2) {
            for(SharedSuggestionProvider.TextCoordinates var7 : collection) {
               String var8 = vars5[0] + " " + vars5[1] + " " + var7.z;
               if(predicate.test(var8)) {
                  var4.add(var8);
               }
            }
         }
      }

      return suggest((Iterable)var4, suggestionsBuilder);
   }

   static default CompletableFuture suggest2DCoordinates(String string, Collection collection, SuggestionsBuilder suggestionsBuilder, Predicate predicate) {
      List<String> var4 = Lists.newArrayList();
      if(Strings.isNullOrEmpty(string)) {
         for(SharedSuggestionProvider.TextCoordinates var6 : collection) {
            String var7 = var6.x + " " + var6.z;
            if(predicate.test(var7)) {
               var4.add(var6.x);
               var4.add(var7);
            }
         }
      } else {
         String[] vars5 = string.split(" ");
         if(vars5.length == 1) {
            for(SharedSuggestionProvider.TextCoordinates var7 : collection) {
               String var8 = vars5[0] + " " + var7.z;
               if(predicate.test(var8)) {
                  var4.add(var8);
               }
            }
         }
      }

      return suggest((Iterable)var4, suggestionsBuilder);
   }

   static default CompletableFuture suggest(Iterable iterable, SuggestionsBuilder suggestionsBuilder) {
      String var2 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

      for(String var4 : iterable) {
         if(var4.toLowerCase(Locale.ROOT).startsWith(var2)) {
            suggestionsBuilder.suggest(var4);
         }
      }

      return suggestionsBuilder.buildFuture();
   }

   static default CompletableFuture suggest(Stream stream, SuggestionsBuilder suggestionsBuilder) {
      String var2 = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
      stream.filter((var1) -> {
         return var1.toLowerCase(Locale.ROOT).startsWith(var2);
      }).forEach(suggestionsBuilder::suggest);
      return suggestionsBuilder.buildFuture();
   }

   static default CompletableFuture suggest(String[] strings, SuggestionsBuilder suggestionsBuilder) {
      String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

      for(String var6 : strings) {
         if(var6.toLowerCase(Locale.ROOT).startsWith(string)) {
            suggestionsBuilder.suggest(var6);
         }
      }

      return suggestionsBuilder.buildFuture();
   }

   public static class TextCoordinates {
      public static final SharedSuggestionProvider.TextCoordinates DEFAULT_LOCAL = new SharedSuggestionProvider.TextCoordinates("^", "^", "^");
      public static final SharedSuggestionProvider.TextCoordinates DEFAULT_GLOBAL = new SharedSuggestionProvider.TextCoordinates("~", "~", "~");
      public final String x;
      public final String y;
      public final String z;

      public TextCoordinates(String x, String y, String z) {
         this.x = x;
         this.y = y;
         this.z = z;
      }
   }
}
