package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class GameProfileArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e"});
   public static final SimpleCommandExceptionType ERROR_UNKNOWN_PLAYER = new SimpleCommandExceptionType(new TranslatableComponent("argument.player.unknown", new Object[0]));

   public static Collection getGameProfiles(CommandContext commandContext, String string) throws CommandSyntaxException {
      return ((GameProfileArgument.Result)commandContext.getArgument(string, GameProfileArgument.Result.class)).getNames((CommandSourceStack)commandContext.getSource());
   }

   public static GameProfileArgument gameProfile() {
      return new GameProfileArgument();
   }

   public GameProfileArgument.Result parse(StringReader stringReader) throws CommandSyntaxException {
      if(stringReader.canRead() && stringReader.peek() == 64) {
         EntitySelectorParser var2 = new EntitySelectorParser(stringReader);
         EntitySelector var3 = var2.parse();
         if(var3.includesEntities()) {
            throw EntityArgument.ERROR_ONLY_PLAYERS_ALLOWED.create();
         } else {
            return new GameProfileArgument.SelectorResult(var3);
         }
      } else {
         int var2 = stringReader.getCursor();

         while(stringReader.canRead() && stringReader.peek() != 32) {
            stringReader.skip();
         }

         String var3 = stringReader.getString().substring(var2, stringReader.getCursor());
         return (commandSourceStack) -> {
            GameProfile var2 = commandSourceStack.getServer().getProfileCache().get(var3);
            if(var2 == null) {
               throw ERROR_UNKNOWN_PLAYER.create();
            } else {
               return Collections.singleton(var2);
            }
         };
      }
   }

   public CompletableFuture listSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      if(commandContext.getSource() instanceof SharedSuggestionProvider) {
         StringReader var3 = new StringReader(suggestionsBuilder.getInput());
         var3.setCursor(suggestionsBuilder.getStart());
         EntitySelectorParser var4 = new EntitySelectorParser(var3);

         try {
            var4.parse();
         } catch (CommandSyntaxException var6) {
            ;
         }

         return var4.fillSuggestions(suggestionsBuilder, (suggestionsBuilder) -> {
            SharedSuggestionProvider.suggest((Iterable)((SharedSuggestionProvider)commandContext.getSource()).getOnlinePlayerNames(), suggestionsBuilder);
         });
      } else {
         return Suggestions.empty();
      }
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   @FunctionalInterface
   public interface Result {
      Collection getNames(CommandSourceStack var1) throws CommandSyntaxException;
   }

   public static class SelectorResult implements GameProfileArgument.Result {
      private final EntitySelector selector;

      public SelectorResult(EntitySelector selector) {
         this.selector = selector;
      }

      public Collection getNames(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
         List<ServerPlayer> var2 = this.selector.findPlayers(commandSourceStack);
         if(var2.isEmpty()) {
            throw EntityArgument.NO_PLAYERS_FOUND.create();
         } else {
            List<GameProfile> var3 = Lists.newArrayList();

            for(ServerPlayer var5 : var2) {
               var3.add(var5.getGameProfile());
            }

            return var3;
         }
      }
   }
}
