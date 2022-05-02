package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Locale;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundSetTitlesPacket;
import net.minecraft.server.level.ServerPlayer;

public class TitleCommand {
   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("title").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.literal("clear").executes((commandContext) -> {
         return clearTitle((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"));
      }))).then(Commands.literal("reset").executes((commandContext) -> {
         return resetTitle((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"));
      }))).then(Commands.literal("title").then(Commands.argument("title", ComponentArgument.textComponent()).executes((commandContext) -> {
         return showTitle((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), ComponentArgument.getComponent(commandContext, "title"), ClientboundSetTitlesPacket.Type.TITLE);
      })))).then(Commands.literal("subtitle").then(Commands.argument("title", ComponentArgument.textComponent()).executes((commandContext) -> {
         return showTitle((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), ComponentArgument.getComponent(commandContext, "title"), ClientboundSetTitlesPacket.Type.SUBTITLE);
      })))).then(Commands.literal("actionbar").then(Commands.argument("title", ComponentArgument.textComponent()).executes((commandContext) -> {
         return showTitle((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), ComponentArgument.getComponent(commandContext, "title"), ClientboundSetTitlesPacket.Type.ACTIONBAR);
      })))).then(Commands.literal("times").then(Commands.argument("fadeIn", IntegerArgumentType.integer(0)).then(Commands.argument("stay", IntegerArgumentType.integer(0)).then(Commands.argument("fadeOut", IntegerArgumentType.integer(0)).executes((commandContext) -> {
         return setTimes((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "fadeIn"), IntegerArgumentType.getInteger(commandContext, "stay"), IntegerArgumentType.getInteger(commandContext, "fadeOut"));
      })))))));
   }

   private static int clearTitle(CommandSourceStack commandSourceStack, Collection collection) {
      ClientboundSetTitlesPacket var2 = new ClientboundSetTitlesPacket(ClientboundSetTitlesPacket.Type.CLEAR, (Component)null);

      for(ServerPlayer var4 : collection) {
         var4.connection.send(var2);
      }

      if(collection.size() == 1) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.title.cleared.single", new Object[]{((ServerPlayer)collection.iterator().next()).getDisplayName()}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.title.cleared.multiple", new Object[]{Integer.valueOf(collection.size())}), true);
      }

      return collection.size();
   }

   private static int resetTitle(CommandSourceStack commandSourceStack, Collection collection) {
      ClientboundSetTitlesPacket var2 = new ClientboundSetTitlesPacket(ClientboundSetTitlesPacket.Type.RESET, (Component)null);

      for(ServerPlayer var4 : collection) {
         var4.connection.send(var2);
      }

      if(collection.size() == 1) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.title.reset.single", new Object[]{((ServerPlayer)collection.iterator().next()).getDisplayName()}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.title.reset.multiple", new Object[]{Integer.valueOf(collection.size())}), true);
      }

      return collection.size();
   }

   private static int showTitle(CommandSourceStack commandSourceStack, Collection collection, Component component, ClientboundSetTitlesPacket.Type clientboundSetTitlesPacket$Type) throws CommandSyntaxException {
      for(ServerPlayer var5 : collection) {
         var5.connection.send(new ClientboundSetTitlesPacket(clientboundSetTitlesPacket$Type, ComponentUtils.updateForEntity(commandSourceStack, component, var5, 0)));
      }

      if(collection.size() == 1) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.title.show." + clientboundSetTitlesPacket$Type.name().toLowerCase(Locale.ROOT) + ".single", new Object[]{((ServerPlayer)collection.iterator().next()).getDisplayName()}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.title.show." + clientboundSetTitlesPacket$Type.name().toLowerCase(Locale.ROOT) + ".multiple", new Object[]{Integer.valueOf(collection.size())}), true);
      }

      return collection.size();
   }

   private static int setTimes(CommandSourceStack commandSourceStack, Collection collection, int var2, int var3, int var4) {
      ClientboundSetTitlesPacket var5 = new ClientboundSetTitlesPacket(var2, var3, var4);

      for(ServerPlayer var7 : collection) {
         var7.connection.send(var5);
      }

      if(collection.size() == 1) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.title.times.single", new Object[]{((ServerPlayer)collection.iterator().next()).getDisplayName()}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.title.times.multiple", new Object[]{Integer.valueOf(collection.size())}), true);
      }

      return collection.size();
   }
}
