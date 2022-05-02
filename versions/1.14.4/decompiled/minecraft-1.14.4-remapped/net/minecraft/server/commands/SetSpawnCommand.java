package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class SetSpawnCommand {
   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spawnpoint").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).executes((commandContext) -> {
         return setSpawn((CommandSourceStack)commandContext.getSource(), Collections.singleton(((CommandSourceStack)commandContext.getSource()).getPlayerOrException()), new BlockPos(((CommandSourceStack)commandContext.getSource()).getPosition()));
      })).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((commandContext) -> {
         return setSpawn((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), new BlockPos(((CommandSourceStack)commandContext.getSource()).getPosition()));
      })).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((commandContext) -> {
         return setSpawn((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), BlockPosArgument.getOrLoadBlockPos(commandContext, "pos"));
      }))));
   }

   private static int setSpawn(CommandSourceStack commandSourceStack, Collection collection, BlockPos blockPos) {
      for(ServerPlayer var4 : collection) {
         var4.setRespawnPosition(blockPos, true);
      }

      if(collection.size() == 1) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.spawnpoint.success.single", new Object[]{Integer.valueOf(blockPos.getX()), Integer.valueOf(blockPos.getY()), Integer.valueOf(blockPos.getZ()), ((ServerPlayer)collection.iterator().next()).getDisplayName()}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.spawnpoint.success.multiple", new Object[]{Integer.valueOf(blockPos.getX()), Integer.valueOf(blockPos.getY()), Integer.valueOf(blockPos.getZ()), Integer.valueOf(collection.size())}), true);
      }

      return collection.size();
   }
}
