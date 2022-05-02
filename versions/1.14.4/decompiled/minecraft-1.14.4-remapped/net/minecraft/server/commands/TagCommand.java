package net.minecraft.server.commands;

import com.google.common.collect.Sets;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;

public class TagCommand {
   private static final SimpleCommandExceptionType ERROR_ADD_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.tag.add.failed", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_REMOVE_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.tag.remove.failed", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tag").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities()).then(Commands.literal("add").then(Commands.argument("name", StringArgumentType.word()).executes((commandContext) -> {
         return addTag((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities(commandContext, "targets"), StringArgumentType.getString(commandContext, "name"));
      })))).then(Commands.literal("remove").then(Commands.argument("name", StringArgumentType.word()).suggests((commandContext, suggestionsBuilder) -> {
         return SharedSuggestionProvider.suggest((Iterable)getTags(EntityArgument.getEntities(commandContext, "targets")), suggestionsBuilder);
      }).executes((commandContext) -> {
         return removeTag((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities(commandContext, "targets"), StringArgumentType.getString(commandContext, "name"));
      })))).then(Commands.literal("list").executes((commandContext) -> {
         return listTags((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities(commandContext, "targets"));
      }))));
   }

   private static Collection getTags(Collection collection) {
      Set<String> var1 = Sets.newHashSet();

      for(Entity var3 : collection) {
         var1.addAll(var3.getTags());
      }

      return var1;
   }

   private static int addTag(CommandSourceStack commandSourceStack, Collection collection, String string) throws CommandSyntaxException {
      int var3 = 0;

      for(Entity var5 : collection) {
         if(var5.addTag(string)) {
            ++var3;
         }
      }

      if(var3 == 0) {
         throw ERROR_ADD_FAILED.create();
      } else {
         if(collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.tag.add.success.single", new Object[]{string, ((Entity)collection.iterator().next()).getDisplayName()}), true);
         } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.tag.add.success.multiple", new Object[]{string, Integer.valueOf(collection.size())}), true);
         }

         return var3;
      }
   }

   private static int removeTag(CommandSourceStack commandSourceStack, Collection collection, String string) throws CommandSyntaxException {
      int var3 = 0;

      for(Entity var5 : collection) {
         if(var5.removeTag(string)) {
            ++var3;
         }
      }

      if(var3 == 0) {
         throw ERROR_REMOVE_FAILED.create();
      } else {
         if(collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.tag.remove.success.single", new Object[]{string, ((Entity)collection.iterator().next()).getDisplayName()}), true);
         } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.tag.remove.success.multiple", new Object[]{string, Integer.valueOf(collection.size())}), true);
         }

         return var3;
      }
   }

   private static int listTags(CommandSourceStack commandSourceStack, Collection collection) {
      Set<String> var2 = Sets.newHashSet();

      for(Entity var4 : collection) {
         var2.addAll(var4.getTags());
      }

      if(collection.size() == 1) {
         Entity var3 = (Entity)collection.iterator().next();
         if(var2.isEmpty()) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.tag.list.single.empty", new Object[]{var3.getDisplayName()}), false);
         } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.tag.list.single.success", new Object[]{var3.getDisplayName(), Integer.valueOf(var2.size()), ComponentUtils.formatList(var2)}), false);
         }
      } else if(var2.isEmpty()) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.tag.list.multiple.empty", new Object[]{Integer.valueOf(collection.size())}), false);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.tag.list.multiple.success", new Object[]{Integer.valueOf(collection.size()), Integer.valueOf(var2.size()), ComponentUtils.formatList(var2)}), false);
      }

      return var2.size();
   }
}
