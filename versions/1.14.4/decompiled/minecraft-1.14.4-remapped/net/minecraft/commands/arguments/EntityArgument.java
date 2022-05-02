package net.minecraft.commands.arguments;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class EntityArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498"});
   public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_ENTITY = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.toomany", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_PLAYER = new SimpleCommandExceptionType(new TranslatableComponent("argument.player.toomany", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_ONLY_PLAYERS_ALLOWED = new SimpleCommandExceptionType(new TranslatableComponent("argument.player.entities", new Object[0]));
   public static final SimpleCommandExceptionType NO_ENTITIES_FOUND = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.notfound.entity", new Object[0]));
   public static final SimpleCommandExceptionType NO_PLAYERS_FOUND = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.notfound.player", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType(new TranslatableComponent("argument.entity.selector.not_allowed", new Object[0]));
   private final boolean single;
   private final boolean playersOnly;

   protected EntityArgument(boolean single, boolean playersOnly) {
      this.single = single;
      this.playersOnly = playersOnly;
   }

   public static EntityArgument entity() {
      return new EntityArgument(true, false);
   }

   public static Entity getEntity(CommandContext commandContext, String string) throws CommandSyntaxException {
      return ((EntitySelector)commandContext.getArgument(string, EntitySelector.class)).findSingleEntity((CommandSourceStack)commandContext.getSource());
   }

   public static EntityArgument entities() {
      return new EntityArgument(false, false);
   }

   public static Collection getEntities(CommandContext commandContext, String string) throws CommandSyntaxException {
      Collection<? extends Entity> collection = getOptionalEntities(commandContext, string);
      if(collection.isEmpty()) {
         throw NO_ENTITIES_FOUND.create();
      } else {
         return collection;
      }
   }

   public static Collection getOptionalEntities(CommandContext commandContext, String string) throws CommandSyntaxException {
      return ((EntitySelector)commandContext.getArgument(string, EntitySelector.class)).findEntities((CommandSourceStack)commandContext.getSource());
   }

   public static Collection getOptionalPlayers(CommandContext commandContext, String string) throws CommandSyntaxException {
      return ((EntitySelector)commandContext.getArgument(string, EntitySelector.class)).findPlayers((CommandSourceStack)commandContext.getSource());
   }

   public static EntityArgument player() {
      return new EntityArgument(true, true);
   }

   public static ServerPlayer getPlayer(CommandContext commandContext, String string) throws CommandSyntaxException {
      return ((EntitySelector)commandContext.getArgument(string, EntitySelector.class)).findSinglePlayer((CommandSourceStack)commandContext.getSource());
   }

   public static EntityArgument players() {
      return new EntityArgument(false, true);
   }

   public static Collection getPlayers(CommandContext commandContext, String string) throws CommandSyntaxException {
      List<ServerPlayer> var2 = ((EntitySelector)commandContext.getArgument(string, EntitySelector.class)).findPlayers((CommandSourceStack)commandContext.getSource());
      if(var2.isEmpty()) {
         throw NO_PLAYERS_FOUND.create();
      } else {
         return var2;
      }
   }

   public EntitySelector parse(StringReader stringReader) throws CommandSyntaxException {
      int var2 = 0;
      EntitySelectorParser var3 = new EntitySelectorParser(stringReader);
      EntitySelector var4 = var3.parse();
      if(var4.getMaxResults() > 1 && this.single) {
         if(this.playersOnly) {
            stringReader.setCursor(0);
            throw ERROR_NOT_SINGLE_PLAYER.createWithContext(stringReader);
         } else {
            stringReader.setCursor(0);
            throw ERROR_NOT_SINGLE_ENTITY.createWithContext(stringReader);
         }
      } else if(var4.includesEntities() && this.playersOnly && !var4.isSelfSelector()) {
         stringReader.setCursor(0);
         throw ERROR_ONLY_PLAYERS_ALLOWED.createWithContext(stringReader);
      } else {
         return var4;
      }
   }

   public CompletableFuture listSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      if(commandContext.getSource() instanceof SharedSuggestionProvider) {
         StringReader var3 = new StringReader(suggestionsBuilder.getInput());
         var3.setCursor(suggestionsBuilder.getStart());
         SharedSuggestionProvider var4 = (SharedSuggestionProvider)commandContext.getSource();
         EntitySelectorParser var5 = new EntitySelectorParser(var3, var4.hasPermission(2));

         try {
            var5.parse();
         } catch (CommandSyntaxException var7) {
            ;
         }

         return var5.fillSuggestions(suggestionsBuilder, (suggestionsBuilder) -> {
            Collection<String> var3 = var4.getOnlinePlayerNames();
            Iterable<String> var4 = (Iterable)(this.playersOnly?var3:Iterables.concat(var3, var4.getSelectedEntities()));
            SharedSuggestionProvider.suggest(var4, suggestionsBuilder);
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

   public static class Serializer implements ArgumentSerializer {
      public void serializeToNetwork(EntityArgument entityArgument, FriendlyByteBuf friendlyByteBuf) {
         byte var3 = 0;
         if(entityArgument.single) {
            var3 = (byte)(var3 | 1);
         }

         if(entityArgument.playersOnly) {
            var3 = (byte)(var3 | 2);
         }

         friendlyByteBuf.writeByte(var3);
      }

      public EntityArgument deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
         byte var2 = friendlyByteBuf.readByte();
         return new EntityArgument((var2 & 1) != 0, (var2 & 2) != 0);
      }

      public void serializeToJson(EntityArgument entityArgument, JsonObject jsonObject) {
         jsonObject.addProperty("amount", entityArgument.single?"single":"multiple");
         jsonObject.addProperty("type", entityArgument.playersOnly?"players":"entities");
      }

      // $FF: synthetic method
      public ArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
         return this.deserializeFromNetwork(var1);
      }
   }
}
