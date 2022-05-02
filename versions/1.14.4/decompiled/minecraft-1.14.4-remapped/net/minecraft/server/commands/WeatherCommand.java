package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;

public class WeatherCommand {
   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("weather").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(((LiteralArgumentBuilder)Commands.literal("clear").executes((commandContext) -> {
         return setClear((CommandSourceStack)commandContext.getSource(), 6000);
      })).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((commandContext) -> {
         return setClear((CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20);
      })))).then(((LiteralArgumentBuilder)Commands.literal("rain").executes((commandContext) -> {
         return setRain((CommandSourceStack)commandContext.getSource(), 6000);
      })).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((commandContext) -> {
         return setRain((CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20);
      })))).then(((LiteralArgumentBuilder)Commands.literal("thunder").executes((commandContext) -> {
         return setThunder((CommandSourceStack)commandContext.getSource(), 6000);
      })).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes((commandContext) -> {
         return setThunder((CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "duration") * 20);
      }))));
   }

   private static int setClear(CommandSourceStack commandSourceStack, int var1) {
      commandSourceStack.getLevel().getLevelData().setClearWeatherTime(var1);
      commandSourceStack.getLevel().getLevelData().setRainTime(0);
      commandSourceStack.getLevel().getLevelData().setThunderTime(0);
      commandSourceStack.getLevel().getLevelData().setRaining(false);
      commandSourceStack.getLevel().getLevelData().setThundering(false);
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.weather.set.clear", new Object[0]), true);
      return var1;
   }

   private static int setRain(CommandSourceStack commandSourceStack, int var1) {
      commandSourceStack.getLevel().getLevelData().setClearWeatherTime(0);
      commandSourceStack.getLevel().getLevelData().setRainTime(var1);
      commandSourceStack.getLevel().getLevelData().setThunderTime(var1);
      commandSourceStack.getLevel().getLevelData().setRaining(true);
      commandSourceStack.getLevel().getLevelData().setThundering(false);
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.weather.set.rain", new Object[0]), true);
      return var1;
   }

   private static int setThunder(CommandSourceStack commandSourceStack, int var1) {
      commandSourceStack.getLevel().getLevelData().setClearWeatherTime(0);
      commandSourceStack.getLevel().getLevelData().setRainTime(var1);
      commandSourceStack.getLevel().getLevelData().setThunderTime(var1);
      commandSourceStack.getLevel().getLevelData().setRaining(true);
      commandSourceStack.getLevel().getLevelData().setThundering(true);
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.weather.set.thunder", new Object[0]), true);
      return var1;
   }
}
