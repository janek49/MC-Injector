package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;

public class TimeCommand {
   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("time").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("set").then(Commands.literal("day").executes((commandContext) -> {
         return setTime((CommandSourceStack)commandContext.getSource(), 1000);
      }))).then(Commands.literal("noon").executes((commandContext) -> {
         return setTime((CommandSourceStack)commandContext.getSource(), 6000);
      }))).then(Commands.literal("night").executes((commandContext) -> {
         return setTime((CommandSourceStack)commandContext.getSource(), 13000);
      }))).then(Commands.literal("midnight").executes((commandContext) -> {
         return setTime((CommandSourceStack)commandContext.getSource(), 18000);
      }))).then(Commands.argument("time", TimeArgument.time()).executes((commandContext) -> {
         return setTime((CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time"));
      })))).then(Commands.literal("add").then(Commands.argument("time", TimeArgument.time()).executes((commandContext) -> {
         return addTime((CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time"));
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("query").then(Commands.literal("daytime").executes((commandContext) -> {
         return queryTime((CommandSourceStack)commandContext.getSource(), getDayTime(((CommandSourceStack)commandContext.getSource()).getLevel()));
      }))).then(Commands.literal("gametime").executes((commandContext) -> {
         return queryTime((CommandSourceStack)commandContext.getSource(), (int)(((CommandSourceStack)commandContext.getSource()).getLevel().getGameTime() % 2147483647L));
      }))).then(Commands.literal("day").executes((commandContext) -> {
         return queryTime((CommandSourceStack)commandContext.getSource(), (int)(((CommandSourceStack)commandContext.getSource()).getLevel().getDayTime() / 24000L % 2147483647L));
      }))));
   }

   private static int getDayTime(ServerLevel serverLevel) {
      return (int)(serverLevel.getDayTime() % 24000L);
   }

   private static int queryTime(CommandSourceStack commandSourceStack, int var1) {
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.time.query", new Object[]{Integer.valueOf(var1)}), false);
      return var1;
   }

   public static int setTime(CommandSourceStack commandSourceStack, int var1) {
      for(ServerLevel var3 : commandSourceStack.getServer().getAllLevels()) {
         var3.setDayTime((long)var1);
      }

      commandSourceStack.sendSuccess(new TranslatableComponent("commands.time.set", new Object[]{Integer.valueOf(var1)}), true);
      return getDayTime(commandSourceStack.getLevel());
   }

   public static int addTime(CommandSourceStack commandSourceStack, int var1) {
      for(ServerLevel var3 : commandSourceStack.getServer().getAllLevels()) {
         var3.setDayTime(var3.getDayTime() + (long)var1);
      }

      int var2 = getDayTime(commandSourceStack.getLevel());
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.time.set", new Object[]{Integer.valueOf(var2)}), true);
      return var2;
   }
}
