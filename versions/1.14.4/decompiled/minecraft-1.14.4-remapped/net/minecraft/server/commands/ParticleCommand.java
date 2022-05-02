package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class ParticleCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.particle.failed", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("particle").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(((RequiredArgumentBuilder)Commands.argument("name", ParticleArgument.particle()).executes((commandContext) -> {
         return sendParticles((CommandSourceStack)commandContext.getSource(), ParticleArgument.getParticle(commandContext, "name"), ((CommandSourceStack)commandContext.getSource()).getPosition(), Vec3.ZERO, 0.0F, 0, false, ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getPlayers());
      })).then(((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3()).executes((commandContext) -> {
         return sendParticles((CommandSourceStack)commandContext.getSource(), ParticleArgument.getParticle(commandContext, "name"), Vec3Argument.getVec3(commandContext, "pos"), Vec3.ZERO, 0.0F, 0, false, ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getPlayers());
      })).then(Commands.argument("delta", Vec3Argument.vec3(false)).then(Commands.argument("speed", FloatArgumentType.floatArg(0.0F)).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("count", IntegerArgumentType.integer(0)).executes((commandContext) -> {
         return sendParticles((CommandSourceStack)commandContext.getSource(), ParticleArgument.getParticle(commandContext, "name"), Vec3Argument.getVec3(commandContext, "pos"), Vec3Argument.getVec3(commandContext, "delta"), FloatArgumentType.getFloat(commandContext, "speed"), IntegerArgumentType.getInteger(commandContext, "count"), false, ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getPlayers());
      })).then(((LiteralArgumentBuilder)Commands.literal("force").executes((commandContext) -> {
         return sendParticles((CommandSourceStack)commandContext.getSource(), ParticleArgument.getParticle(commandContext, "name"), Vec3Argument.getVec3(commandContext, "pos"), Vec3Argument.getVec3(commandContext, "delta"), FloatArgumentType.getFloat(commandContext, "speed"), IntegerArgumentType.getInteger(commandContext, "count"), true, ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getPlayers());
      })).then(Commands.argument("viewers", EntityArgument.players()).executes((commandContext) -> {
         return sendParticles((CommandSourceStack)commandContext.getSource(), ParticleArgument.getParticle(commandContext, "name"), Vec3Argument.getVec3(commandContext, "pos"), Vec3Argument.getVec3(commandContext, "delta"), FloatArgumentType.getFloat(commandContext, "speed"), IntegerArgumentType.getInteger(commandContext, "count"), true, EntityArgument.getPlayers(commandContext, "viewers"));
      })))).then(((LiteralArgumentBuilder)Commands.literal("normal").executes((commandContext) -> {
         return sendParticles((CommandSourceStack)commandContext.getSource(), ParticleArgument.getParticle(commandContext, "name"), Vec3Argument.getVec3(commandContext, "pos"), Vec3Argument.getVec3(commandContext, "delta"), FloatArgumentType.getFloat(commandContext, "speed"), IntegerArgumentType.getInteger(commandContext, "count"), false, ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getPlayers());
      })).then(Commands.argument("viewers", EntityArgument.players()).executes((commandContext) -> {
         return sendParticles((CommandSourceStack)commandContext.getSource(), ParticleArgument.getParticle(commandContext, "name"), Vec3Argument.getVec3(commandContext, "pos"), Vec3Argument.getVec3(commandContext, "delta"), FloatArgumentType.getFloat(commandContext, "speed"), IntegerArgumentType.getInteger(commandContext, "count"), false, EntityArgument.getPlayers(commandContext, "viewers"));
      })))))))));
   }

   private static int sendParticles(CommandSourceStack commandSourceStack, ParticleOptions particleOptions, Vec3 var2, Vec3 var3, float var4, int var5, boolean var6, Collection collection) throws CommandSyntaxException {
      int var8 = 0;

      for(ServerPlayer var10 : collection) {
         if(commandSourceStack.getLevel().sendParticles(var10, particleOptions, var6, var2.x, var2.y, var2.z, var5, var3.x, var3.y, var3.z, (double)var4)) {
            ++var8;
         }
      }

      if(var8 == 0) {
         throw ERROR_FAILED.create();
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.particle.success", new Object[]{Registry.PARTICLE_TYPE.getKey(particleOptions.getType()).toString()}), true);
         return var8;
      }
   }
}
