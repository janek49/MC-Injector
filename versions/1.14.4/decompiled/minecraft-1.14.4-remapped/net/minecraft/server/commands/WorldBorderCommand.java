package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec2;

public class WorldBorderCommand {
   private static final SimpleCommandExceptionType ERROR_SAME_CENTER = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.center.failed", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_SAME_SIZE = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.set.failed.nochange", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_TOO_SMALL = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.set.failed.small.", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_TOO_BIG = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.set.failed.big.", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_SAME_WARNING_TIME = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.warning.time.failed", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_SAME_WARNING_DISTANCE = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.warning.distance.failed", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_SAME_DAMAGE_BUFFER = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.damage.buffer.failed", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_SAME_DAMAGE_AMOUNT = new SimpleCommandExceptionType(new TranslatableComponent("commands.worldborder.damage.amount.failed", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("worldborder").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.literal("add").then(((RequiredArgumentBuilder)Commands.argument("distance", FloatArgumentType.floatArg(-6.0E7F, 6.0E7F)).executes((commandContext) -> {
         return setSize((CommandSourceStack)commandContext.getSource(), ((CommandSourceStack)commandContext.getSource()).getLevel().getWorldBorder().getSize() + (double)FloatArgumentType.getFloat(commandContext, "distance"), 0L);
      })).then(Commands.argument("time", IntegerArgumentType.integer(0)).executes((commandContext) -> {
         return setSize((CommandSourceStack)commandContext.getSource(), ((CommandSourceStack)commandContext.getSource()).getLevel().getWorldBorder().getSize() + (double)FloatArgumentType.getFloat(commandContext, "distance"), ((CommandSourceStack)commandContext.getSource()).getLevel().getWorldBorder().getLerpRemainingTime() + (long)IntegerArgumentType.getInteger(commandContext, "time") * 1000L);
      }))))).then(Commands.literal("set").then(((RequiredArgumentBuilder)Commands.argument("distance", FloatArgumentType.floatArg(-6.0E7F, 6.0E7F)).executes((commandContext) -> {
         return setSize((CommandSourceStack)commandContext.getSource(), (double)FloatArgumentType.getFloat(commandContext, "distance"), 0L);
      })).then(Commands.argument("time", IntegerArgumentType.integer(0)).executes((commandContext) -> {
         return setSize((CommandSourceStack)commandContext.getSource(), (double)FloatArgumentType.getFloat(commandContext, "distance"), (long)IntegerArgumentType.getInteger(commandContext, "time") * 1000L);
      }))))).then(Commands.literal("center").then(Commands.argument("pos", Vec2Argument.vec2()).executes((commandContext) -> {
         return setCenter((CommandSourceStack)commandContext.getSource(), Vec2Argument.getVec2(commandContext, "pos"));
      })))).then(((LiteralArgumentBuilder)Commands.literal("damage").then(Commands.literal("amount").then(Commands.argument("damagePerBlock", FloatArgumentType.floatArg(0.0F)).executes((commandContext) -> {
         return setDamageAmount((CommandSourceStack)commandContext.getSource(), FloatArgumentType.getFloat(commandContext, "damagePerBlock"));
      })))).then(Commands.literal("buffer").then(Commands.argument("distance", FloatArgumentType.floatArg(0.0F)).executes((commandContext) -> {
         return setDamageBuffer((CommandSourceStack)commandContext.getSource(), FloatArgumentType.getFloat(commandContext, "distance"));
      }))))).then(Commands.literal("get").executes((commandContext) -> {
         return getSize((CommandSourceStack)commandContext.getSource());
      }))).then(((LiteralArgumentBuilder)Commands.literal("warning").then(Commands.literal("distance").then(Commands.argument("distance", IntegerArgumentType.integer(0)).executes((commandContext) -> {
         return setWarningDistance((CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "distance"));
      })))).then(Commands.literal("time").then(Commands.argument("time", IntegerArgumentType.integer(0)).executes((commandContext) -> {
         return setWarningTime((CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "time"));
      })))));
   }

   private static int setDamageBuffer(CommandSourceStack commandSourceStack, float var1) throws CommandSyntaxException {
      WorldBorder var2 = commandSourceStack.getLevel().getWorldBorder();
      if(var2.getDamageSafeZone() == (double)var1) {
         throw ERROR_SAME_DAMAGE_BUFFER.create();
      } else {
         var2.setDamageSafeZone((double)var1);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.worldborder.damage.buffer.success", new Object[]{String.format(Locale.ROOT, "%.2f", new Object[]{Float.valueOf(var1)})}), true);
         return (int)var1;
      }
   }

   private static int setDamageAmount(CommandSourceStack commandSourceStack, float var1) throws CommandSyntaxException {
      WorldBorder var2 = commandSourceStack.getLevel().getWorldBorder();
      if(var2.getDamagePerBlock() == (double)var1) {
         throw ERROR_SAME_DAMAGE_AMOUNT.create();
      } else {
         var2.setDamagePerBlock((double)var1);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.worldborder.damage.amount.success", new Object[]{String.format(Locale.ROOT, "%.2f", new Object[]{Float.valueOf(var1)})}), true);
         return (int)var1;
      }
   }

   private static int setWarningTime(CommandSourceStack commandSourceStack, int var1) throws CommandSyntaxException {
      WorldBorder var2 = commandSourceStack.getLevel().getWorldBorder();
      if(var2.getWarningTime() == var1) {
         throw ERROR_SAME_WARNING_TIME.create();
      } else {
         var2.setWarningTime(var1);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.worldborder.warning.time.success", new Object[]{Integer.valueOf(var1)}), true);
         return var1;
      }
   }

   private static int setWarningDistance(CommandSourceStack commandSourceStack, int var1) throws CommandSyntaxException {
      WorldBorder var2 = commandSourceStack.getLevel().getWorldBorder();
      if(var2.getWarningBlocks() == var1) {
         throw ERROR_SAME_WARNING_DISTANCE.create();
      } else {
         var2.setWarningBlocks(var1);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.worldborder.warning.distance.success", new Object[]{Integer.valueOf(var1)}), true);
         return var1;
      }
   }

   private static int getSize(CommandSourceStack commandSourceStack) {
      double var1 = commandSourceStack.getLevel().getWorldBorder().getSize();
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.worldborder.get", new Object[]{String.format(Locale.ROOT, "%.0f", new Object[]{Double.valueOf(var1)})}), false);
      return Mth.floor(var1 + 0.5D);
   }

   private static int setCenter(CommandSourceStack commandSourceStack, Vec2 vec2) throws CommandSyntaxException {
      WorldBorder var2 = commandSourceStack.getLevel().getWorldBorder();
      if(var2.getCenterX() == (double)vec2.x && var2.getCenterZ() == (double)vec2.y) {
         throw ERROR_SAME_CENTER.create();
      } else {
         var2.setCenter((double)vec2.x, (double)vec2.y);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.worldborder.center.success", new Object[]{String.format(Locale.ROOT, "%.2f", new Object[]{Float.valueOf(vec2.x)}), String.format("%.2f", new Object[]{Float.valueOf(vec2.y)})}), true);
         return 0;
      }
   }

   private static int setSize(CommandSourceStack commandSourceStack, double var1, long var3) throws CommandSyntaxException {
      WorldBorder var5 = commandSourceStack.getLevel().getWorldBorder();
      double var6 = var5.getSize();
      if(var6 == var1) {
         throw ERROR_SAME_SIZE.create();
      } else if(var1 < 1.0D) {
         throw ERROR_TOO_SMALL.create();
      } else if(var1 > 6.0E7D) {
         throw ERROR_TOO_BIG.create();
      } else {
         if(var3 > 0L) {
            var5.lerpSizeBetween(var6, var1, var3);
            if(var1 > var6) {
               commandSourceStack.sendSuccess(new TranslatableComponent("commands.worldborder.set.grow", new Object[]{String.format(Locale.ROOT, "%.1f", new Object[]{Double.valueOf(var1)}), Long.toString(var3 / 1000L)}), true);
            } else {
               commandSourceStack.sendSuccess(new TranslatableComponent("commands.worldborder.set.shrink", new Object[]{String.format(Locale.ROOT, "%.1f", new Object[]{Double.valueOf(var1)}), Long.toString(var3 / 1000L)}), true);
            }
         } else {
            var5.setSize(var1);
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.worldborder.set.immediate", new Object[]{String.format(Locale.ROOT, "%.1f", new Object[]{Double.valueOf(var1)})}), true);
         }

         return (int)(var1 - var6);
      }
   }
}
