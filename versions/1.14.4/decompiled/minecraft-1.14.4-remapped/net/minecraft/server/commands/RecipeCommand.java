package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class RecipeCommand {
   private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.recipe.give.failed", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_TAKE_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.recipe.take.failed", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("recipe").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.literal("give").then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.argument("recipe", ResourceLocationArgument.id()).suggests(SuggestionProviders.ALL_RECIPES).executes((commandContext) -> {
         return giveRecipes((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), Collections.singleton(ResourceLocationArgument.getRecipe(commandContext, "recipe")));
      }))).then(Commands.literal("*").executes((commandContext) -> {
         return giveRecipes((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), ((CommandSourceStack)commandContext.getSource()).getServer().getRecipeManager().getRecipes());
      }))))).then(Commands.literal("take").then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.argument("recipe", ResourceLocationArgument.id()).suggests(SuggestionProviders.ALL_RECIPES).executes((commandContext) -> {
         return takeRecipes((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), Collections.singleton(ResourceLocationArgument.getRecipe(commandContext, "recipe")));
      }))).then(Commands.literal("*").executes((commandContext) -> {
         return takeRecipes((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), ((CommandSourceStack)commandContext.getSource()).getServer().getRecipeManager().getRecipes());
      })))));
   }

   private static int giveRecipes(CommandSourceStack commandSourceStack, Collection var1, Collection var2) throws CommandSyntaxException {
      int var3 = 0;

      for(ServerPlayer var5 : var1) {
         var3 += var5.awardRecipes(var2);
      }

      if(var3 == 0) {
         throw ERROR_GIVE_FAILED.create();
      } else {
         if(var1.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.recipe.give.success.single", new Object[]{Integer.valueOf(var2.size()), ((ServerPlayer)var1.iterator().next()).getDisplayName()}), true);
         } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.recipe.give.success.multiple", new Object[]{Integer.valueOf(var2.size()), Integer.valueOf(var1.size())}), true);
         }

         return var3;
      }
   }

   private static int takeRecipes(CommandSourceStack commandSourceStack, Collection var1, Collection var2) throws CommandSyntaxException {
      int var3 = 0;

      for(ServerPlayer var5 : var1) {
         var3 += var5.resetRecipes(var2);
      }

      if(var3 == 0) {
         throw ERROR_TAKE_FAILED.create();
      } else {
         if(var1.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.recipe.take.success.single", new Object[]{Integer.valueOf(var2.size()), ((ServerPlayer)var1.iterator().next()).getDisplayName()}), true);
         } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.recipe.take.success.multiple", new Object[]{Integer.valueOf(var2.size()), Integer.valueOf(var1.size())}), true);
         }

         return var3;
      }
   }
}
