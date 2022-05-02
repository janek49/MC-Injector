package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Either;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.FunctionCommand;
import net.minecraft.world.level.timers.FunctionCallback;
import net.minecraft.world.level.timers.FunctionTagCallback;

public class ScheduleCommand {
   private static final SimpleCommandExceptionType ERROR_SAME_TICK = new SimpleCommandExceptionType(new TranslatableComponent("commands.schedule.same_tick", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("schedule").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.literal("function").then(Commands.argument("function", FunctionArgument.functions()).suggests(FunctionCommand.SUGGEST_FUNCTION).then(Commands.argument("time", TimeArgument.time()).executes((commandContext) -> {
         return schedule((CommandSourceStack)commandContext.getSource(), FunctionArgument.getFunctionOrTag(commandContext, "function"), IntegerArgumentType.getInteger(commandContext, "time"));
      })))));
   }

   private static int schedule(CommandSourceStack commandSourceStack, Either either, int var2) throws CommandSyntaxException {
      if(var2 == 0) {
         throw ERROR_SAME_TICK.create();
      } else {
         long var3 = commandSourceStack.getLevel().getGameTime() + (long)var2;
         either.ifLeft((commandFunction) -> {
            ResourceLocation var5 = commandFunction.getId();
            commandSourceStack.getLevel().getLevelData().getScheduledEvents().reschedule(var5.toString(), var3, new FunctionCallback(var5));
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.schedule.created.function", new Object[]{var5, Integer.valueOf(var2), Long.valueOf(var3)}), true);
         }).ifRight((tag) -> {
            ResourceLocation var5 = tag.getId();
            commandSourceStack.getLevel().getLevelData().getScheduledEvents().reschedule("#" + var5.toString(), var3, new FunctionTagCallback(var5));
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.schedule.created.tag", new Object[]{var5, Integer.valueOf(var2), Long.valueOf(var3)}), true);
         });
         return (int)Math.floorMod(var3, 2147483647L);
      }
   }
}
