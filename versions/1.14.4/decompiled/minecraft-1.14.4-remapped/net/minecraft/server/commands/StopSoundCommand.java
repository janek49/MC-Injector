package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;

public class StopSoundCommand {
   public static void register(CommandDispatcher commandDispatcher) {
      RequiredArgumentBuilder<CommandSourceStack, EntitySelector> var1 = (RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((commandContext) -> {
         return stopSound((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), (SoundSource)null, (ResourceLocation)null);
      })).then(Commands.literal("*").then(Commands.argument("sound", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes((commandContext) -> {
         return stopSound((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), (SoundSource)null, ResourceLocationArgument.getId(commandContext, "sound"));
      })));

      for(SoundSource var5 : SoundSource.values()) {
         var1.then(((LiteralArgumentBuilder)Commands.literal(var5.getName()).executes((commandContext) -> {
            return stopSound((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), var5, (ResourceLocation)null);
         })).then(Commands.argument("sound", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes((commandContext) -> {
            return stopSound((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), var5, ResourceLocationArgument.getId(commandContext, "sound"));
         })));
      }

      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("stopsound").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(var1));
   }

   private static int stopSound(CommandSourceStack commandSourceStack, Collection collection, @Nullable SoundSource soundSource, @Nullable ResourceLocation resourceLocation) {
      ClientboundStopSoundPacket var4 = new ClientboundStopSoundPacket(resourceLocation, soundSource);

      for(ServerPlayer var6 : collection) {
         var6.connection.send(var4);
      }

      if(soundSource != null) {
         if(resourceLocation != null) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.stopsound.success.source.sound", new Object[]{resourceLocation, soundSource.getName()}), true);
         } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.stopsound.success.source.any", new Object[]{soundSource.getName()}), true);
         }
      } else if(resourceLocation != null) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.stopsound.success.sourceless.sound", new Object[]{resourceLocation}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.stopsound.success.sourceless.any", new Object[0]), true);
      }

      return collection.size();
   }
}
