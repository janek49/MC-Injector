package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.entity.Entity;

public class ScoreHolderArgument implements ArgumentType {
   public static final SuggestionProvider SUGGEST_SCORE_HOLDERS = (commandContext, suggestionsBuilder) -> {
      StringReader var2 = new StringReader(suggestionsBuilder.getInput());
      var2.setCursor(suggestionsBuilder.getStart());
      EntitySelectorParser var3 = new EntitySelectorParser(var2);

      try {
         var3.parse();
      } catch (CommandSyntaxException var5) {
         ;
      }

      return var3.fillSuggestions(suggestionsBuilder, (suggestionsBuilder) -> {
         SharedSuggestionProvider.suggest((Iterable)((CommandSourceStack)commandContext.getSource()).getOnlinePlayerNames(), suggestionsBuilder);
      });
   };
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"Player", "0123", "*", "@e"});
   private static final SimpleCommandExceptionType ERROR_NO_RESULTS = new SimpleCommandExceptionType(new TranslatableComponent("argument.scoreHolder.empty", new Object[0]));
   private final boolean multiple;

   public ScoreHolderArgument(boolean multiple) {
      this.multiple = multiple;
   }

   public static String getName(CommandContext commandContext, String var1) throws CommandSyntaxException {
      return (String)getNames(commandContext, var1).iterator().next();
   }

   public static Collection getNames(CommandContext commandContext, String string) throws CommandSyntaxException {
      return getNames(commandContext, string, Collections::emptyList);
   }

   public static Collection getNamesWithDefaultWildcard(CommandContext commandContext, String string) throws CommandSyntaxException {
      ServerScoreboard var10002 = ((CommandSourceStack)commandContext.getSource()).getServer().getScoreboard();
      var10002.getClass();
      return getNames(commandContext, string, var10002::getTrackedPlayers);
   }

   public static Collection getNames(CommandContext commandContext, String string, Supplier supplier) throws CommandSyntaxException {
      Collection<String> collection = ((ScoreHolderArgument.Result)commandContext.getArgument(string, ScoreHolderArgument.Result.class)).getNames((CommandSourceStack)commandContext.getSource(), supplier);
      if(collection.isEmpty()) {
         throw EntityArgument.NO_ENTITIES_FOUND.create();
      } else {
         return collection;
      }
   }

   public static ScoreHolderArgument scoreHolder() {
      return new ScoreHolderArgument(false);
   }

   public static ScoreHolderArgument scoreHolders() {
      return new ScoreHolderArgument(true);
   }

   public ScoreHolderArgument.Result parse(StringReader stringReader) throws CommandSyntaxException {
      if(stringReader.canRead() && stringReader.peek() == 64) {
         EntitySelectorParser var2 = new EntitySelectorParser(stringReader);
         EntitySelector var3 = var2.parse();
         if(!this.multiple && var3.getMaxResults() > 1) {
            throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
         } else {
            return new ScoreHolderArgument.SelectorResult(var3);
         }
      } else {
         int var2 = stringReader.getCursor();

         while(stringReader.canRead() && stringReader.peek() != 32) {
            stringReader.skip();
         }

         String var3 = stringReader.getString().substring(var2, stringReader.getCursor());
         if(var3.equals("*")) {
            return (commandSourceStack, supplier) -> {
               Collection<String> collection = (Collection)supplier.get();
               if(collection.isEmpty()) {
                  throw ERROR_NO_RESULTS.create();
               } else {
                  return collection;
               }
            };
         } else {
            Collection<String> var4 = Collections.singleton(var3);
            return (commandSourceStack, supplier) -> {
               return var4;
            };
         }
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
      Collection getNames(CommandSourceStack var1, Supplier var2) throws CommandSyntaxException;
   }

   public static class SelectorResult implements ScoreHolderArgument.Result {
      private final EntitySelector selector;

      public SelectorResult(EntitySelector selector) {
         this.selector = selector;
      }

      public Collection getNames(CommandSourceStack commandSourceStack, Supplier supplier) throws CommandSyntaxException {
         List<? extends Entity> var3 = this.selector.findEntities(commandSourceStack);
         if(var3.isEmpty()) {
            throw EntityArgument.NO_ENTITIES_FOUND.create();
         } else {
            List<String> var4 = Lists.newArrayList();

            for(Entity var6 : var3) {
               var4.add(var6.getScoreboardName());
            }

            return var4;
         }
      }
   }

   public static class Serializer implements ArgumentSerializer {
      public void serializeToNetwork(ScoreHolderArgument scoreHolderArgument, FriendlyByteBuf friendlyByteBuf) {
         byte var3 = 0;
         if(scoreHolderArgument.multiple) {
            var3 = (byte)(var3 | 1);
         }

         friendlyByteBuf.writeByte(var3);
      }

      public ScoreHolderArgument deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
         byte var2 = friendlyByteBuf.readByte();
         boolean var3 = (var2 & 1) != 0;
         return new ScoreHolderArgument(var3);
      }

      public void serializeToJson(ScoreHolderArgument scoreHolderArgument, JsonObject jsonObject) {
         jsonObject.addProperty("amount", scoreHolderArgument.multiple?"multiple":"single");
      }

      // $FF: synthetic method
      public ArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
         return this.deserializeFromNetwork(var1);
      }
   }
}
