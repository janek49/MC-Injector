package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MobEffectArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EffectCommands {
   private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.effect.give.failed", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_CLEAR_EVERYTHING_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.effect.clear.everything.failed", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_CLEAR_SPECIFIC_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.effect.clear.specific.failed", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("effect").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.literal("clear").then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities()).executes((commandContext) -> {
         return clearEffects((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities(commandContext, "targets"));
      })).then(Commands.argument("effect", MobEffectArgument.effect()).executes((commandContext) -> {
         return clearEffect((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities(commandContext, "targets"), MobEffectArgument.getEffect(commandContext, "effect"));
      }))))).then(Commands.literal("give").then(Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)Commands.argument("effect", MobEffectArgument.effect()).executes((commandContext) -> {
         return giveEffect((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities(commandContext, "targets"), MobEffectArgument.getEffect(commandContext, "effect"), (Integer)null, 0, true);
      })).then(((RequiredArgumentBuilder)Commands.argument("seconds", IntegerArgumentType.integer(1, 1000000)).executes((commandContext) -> {
         return giveEffect((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities(commandContext, "targets"), MobEffectArgument.getEffect(commandContext, "effect"), Integer.valueOf(IntegerArgumentType.getInteger(commandContext, "seconds")), 0, true);
      })).then(((RequiredArgumentBuilder)Commands.argument("amplifier", IntegerArgumentType.integer(0, 255)).executes((commandContext) -> {
         return giveEffect((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities(commandContext, "targets"), MobEffectArgument.getEffect(commandContext, "effect"), Integer.valueOf(IntegerArgumentType.getInteger(commandContext, "seconds")), IntegerArgumentType.getInteger(commandContext, "amplifier"), true);
      })).then(Commands.argument("hideParticles", BoolArgumentType.bool()).executes((commandContext) -> {
         return giveEffect((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities(commandContext, "targets"), MobEffectArgument.getEffect(commandContext, "effect"), Integer.valueOf(IntegerArgumentType.getInteger(commandContext, "seconds")), IntegerArgumentType.getInteger(commandContext, "amplifier"), !BoolArgumentType.getBool(commandContext, "hideParticles"));
      }))))))));
   }

   private static int giveEffect(CommandSourceStack commandSourceStack, Collection collection, MobEffect mobEffect, @Nullable Integer integer, int var4, boolean var5) throws CommandSyntaxException {
      int var6 = 0;
      int var7;
      if(integer != null) {
         if(mobEffect.isInstantenous()) {
            var7 = integer.intValue();
         } else {
            var7 = integer.intValue() * 20;
         }
      } else if(mobEffect.isInstantenous()) {
         var7 = 1;
      } else {
         var7 = 600;
      }

      for(Entity var9 : collection) {
         if(var9 instanceof LivingEntity) {
            MobEffectInstance var10 = new MobEffectInstance(mobEffect, var7, var4, false, var5);
            if(((LivingEntity)var9).addEffect(var10)) {
               ++var6;
            }
         }
      }

      if(var6 == 0) {
         throw ERROR_GIVE_FAILED.create();
      } else {
         if(collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.effect.give.success.single", new Object[]{mobEffect.getDisplayName(), ((Entity)collection.iterator().next()).getDisplayName(), Integer.valueOf(var7 / 20)}), true);
         } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.effect.give.success.multiple", new Object[]{mobEffect.getDisplayName(), Integer.valueOf(collection.size()), Integer.valueOf(var7 / 20)}), true);
         }

         return var6;
      }
   }

   private static int clearEffects(CommandSourceStack commandSourceStack, Collection collection) throws CommandSyntaxException {
      int var2 = 0;

      for(Entity var4 : collection) {
         if(var4 instanceof LivingEntity && ((LivingEntity)var4).removeAllEffects()) {
            ++var2;
         }
      }

      if(var2 == 0) {
         throw ERROR_CLEAR_EVERYTHING_FAILED.create();
      } else {
         if(collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.effect.clear.everything.success.single", new Object[]{((Entity)collection.iterator().next()).getDisplayName()}), true);
         } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.effect.clear.everything.success.multiple", new Object[]{Integer.valueOf(collection.size())}), true);
         }

         return var2;
      }
   }

   private static int clearEffect(CommandSourceStack commandSourceStack, Collection collection, MobEffect mobEffect) throws CommandSyntaxException {
      int var3 = 0;

      for(Entity var5 : collection) {
         if(var5 instanceof LivingEntity && ((LivingEntity)var5).removeEffect(mobEffect)) {
            ++var3;
         }
      }

      if(var3 == 0) {
         throw ERROR_CLEAR_SPECIFIC_FAILED.create();
      } else {
         if(collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.effect.clear.specific.success.single", new Object[]{mobEffect.getDisplayName(), ((Entity)collection.iterator().next()).getDisplayName()}), true);
         } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.effect.clear.specific.success.multiple", new Object[]{mobEffect.getDisplayName(), Integer.valueOf(collection.size())}), true);
         }

         return var3;
      }
   }
}
