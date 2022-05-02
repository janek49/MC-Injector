package net.minecraft.commands;

import com.google.common.collect.Maps;
import com.mojang.brigadier.AmbiguityConsumer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.server.commands.AdvancementCommands;
import net.minecraft.server.commands.BanIpCommands;
import net.minecraft.server.commands.BanListCommands;
import net.minecraft.server.commands.BanPlayerCommands;
import net.minecraft.server.commands.BossBarCommands;
import net.minecraft.server.commands.ClearInventoryCommands;
import net.minecraft.server.commands.CloneCommands;
import net.minecraft.server.commands.DataPackCommand;
import net.minecraft.server.commands.DeOpCommands;
import net.minecraft.server.commands.DebugCommand;
import net.minecraft.server.commands.DefaultGameModeCommands;
import net.minecraft.server.commands.DifficultyCommand;
import net.minecraft.server.commands.EffectCommands;
import net.minecraft.server.commands.EmoteCommands;
import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.server.commands.ExecuteCommand;
import net.minecraft.server.commands.ExperienceCommand;
import net.minecraft.server.commands.FillCommand;
import net.minecraft.server.commands.ForceLoadCommand;
import net.minecraft.server.commands.FunctionCommand;
import net.minecraft.server.commands.GameModeCommand;
import net.minecraft.server.commands.GameRuleCommand;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.commands.HelpCommand;
import net.minecraft.server.commands.KickCommand;
import net.minecraft.server.commands.KillCommand;
import net.minecraft.server.commands.ListPlayersCommand;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.server.commands.LootCommand;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.commands.OpCommand;
import net.minecraft.server.commands.PardonCommand;
import net.minecraft.server.commands.PardonIpCommand;
import net.minecraft.server.commands.ParticleCommand;
import net.minecraft.server.commands.PlaySoundCommand;
import net.minecraft.server.commands.PublishCommand;
import net.minecraft.server.commands.RecipeCommand;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.commands.ReplaceItemCommand;
import net.minecraft.server.commands.SaveAllCommand;
import net.minecraft.server.commands.SaveOffCommand;
import net.minecraft.server.commands.SaveOnCommand;
import net.minecraft.server.commands.SayCommand;
import net.minecraft.server.commands.ScheduleCommand;
import net.minecraft.server.commands.ScoreboardCommand;
import net.minecraft.server.commands.SeedCommand;
import net.minecraft.server.commands.SetBlockCommand;
import net.minecraft.server.commands.SetPlayerIdleTimeoutCommand;
import net.minecraft.server.commands.SetSpawnCommand;
import net.minecraft.server.commands.SetWorldSpawnCommand;
import net.minecraft.server.commands.SpreadPlayersCommand;
import net.minecraft.server.commands.StopCommand;
import net.minecraft.server.commands.StopSoundCommand;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.commands.TagCommand;
import net.minecraft.server.commands.TeamCommand;
import net.minecraft.server.commands.TeamMsgCommand;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.commands.TellRawCommand;
import net.minecraft.server.commands.TimeCommand;
import net.minecraft.server.commands.TitleCommand;
import net.minecraft.server.commands.TriggerCommand;
import net.minecraft.server.commands.WeatherCommand;
import net.minecraft.server.commands.WhitelistCommand;
import net.minecraft.server.commands.WorldBorderCommand;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Commands {
   private static final Logger LOGGER = LogManager.getLogger();
   private final CommandDispatcher dispatcher = new CommandDispatcher();

   public Commands(boolean b) {
      AdvancementCommands.register(this.dispatcher);
      ExecuteCommand.register(this.dispatcher);
      BossBarCommands.register(this.dispatcher);
      ClearInventoryCommands.register(this.dispatcher);
      CloneCommands.register(this.dispatcher);
      DataCommands.register(this.dispatcher);
      DataPackCommand.register(this.dispatcher);
      DebugCommand.register(this.dispatcher);
      DefaultGameModeCommands.register(this.dispatcher);
      DifficultyCommand.register(this.dispatcher);
      EffectCommands.register(this.dispatcher);
      EmoteCommands.register(this.dispatcher);
      EnchantCommand.register(this.dispatcher);
      ExperienceCommand.register(this.dispatcher);
      FillCommand.register(this.dispatcher);
      ForceLoadCommand.register(this.dispatcher);
      FunctionCommand.register(this.dispatcher);
      GameModeCommand.register(this.dispatcher);
      GameRuleCommand.register(this.dispatcher);
      GiveCommand.register(this.dispatcher);
      HelpCommand.register(this.dispatcher);
      KickCommand.register(this.dispatcher);
      KillCommand.register(this.dispatcher);
      ListPlayersCommand.register(this.dispatcher);
      LocateCommand.register(this.dispatcher);
      LootCommand.register(this.dispatcher);
      MsgCommand.register(this.dispatcher);
      ParticleCommand.register(this.dispatcher);
      PlaySoundCommand.register(this.dispatcher);
      PublishCommand.register(this.dispatcher);
      ReloadCommand.register(this.dispatcher);
      RecipeCommand.register(this.dispatcher);
      ReplaceItemCommand.register(this.dispatcher);
      SayCommand.register(this.dispatcher);
      ScheduleCommand.register(this.dispatcher);
      ScoreboardCommand.register(this.dispatcher);
      SeedCommand.register(this.dispatcher);
      SetBlockCommand.register(this.dispatcher);
      SetSpawnCommand.register(this.dispatcher);
      SetWorldSpawnCommand.register(this.dispatcher);
      SpreadPlayersCommand.register(this.dispatcher);
      StopSoundCommand.register(this.dispatcher);
      SummonCommand.register(this.dispatcher);
      TagCommand.register(this.dispatcher);
      TeamCommand.register(this.dispatcher);
      TeamMsgCommand.register(this.dispatcher);
      TeleportCommand.register(this.dispatcher);
      TellRawCommand.register(this.dispatcher);
      TimeCommand.register(this.dispatcher);
      TitleCommand.register(this.dispatcher);
      TriggerCommand.register(this.dispatcher);
      WeatherCommand.register(this.dispatcher);
      WorldBorderCommand.register(this.dispatcher);
      if(b) {
         BanIpCommands.register(this.dispatcher);
         BanListCommands.register(this.dispatcher);
         BanPlayerCommands.register(this.dispatcher);
         DeOpCommands.register(this.dispatcher);
         OpCommand.register(this.dispatcher);
         PardonCommand.register(this.dispatcher);
         PardonIpCommand.register(this.dispatcher);
         SaveAllCommand.register(this.dispatcher);
         SaveOffCommand.register(this.dispatcher);
         SaveOnCommand.register(this.dispatcher);
         SetPlayerIdleTimeoutCommand.register(this.dispatcher);
         StopCommand.register(this.dispatcher);
         WhitelistCommand.register(this.dispatcher);
      }

      this.dispatcher.findAmbiguities((var1, var2, var3, collection) -> {
         LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", this.dispatcher.getPath(var2), this.dispatcher.getPath(var3), collection);
      });
      this.dispatcher.setConsumer((commandContext, var1, var2) -> {
         ((CommandSourceStack)commandContext.getSource()).onCommandComplete(commandContext, var1, var2);
      });
   }

   public int performCommand(CommandSourceStack commandSourceStack, String string) {
      // $FF: Couldn't be decompiled
   }

   public void sendCommands(ServerPlayer serverPlayer) {
      Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>> var2 = Maps.newHashMap();
      RootCommandNode<SharedSuggestionProvider> var3 = new RootCommandNode();
      var2.put(this.dispatcher.getRoot(), var3);
      this.fillUsableCommands(this.dispatcher.getRoot(), var3, serverPlayer.createCommandSourceStack(), var2);
      serverPlayer.connection.send(new ClientboundCommandsPacket(var3));
   }

   private void fillUsableCommands(CommandNode var1, CommandNode var2, CommandSourceStack commandSourceStack, Map map) {
      for(CommandNode<CommandSourceStack> var6 : var1.getChildren()) {
         if(var6.canUse(commandSourceStack)) {
            ArgumentBuilder<SharedSuggestionProvider, ?> var7 = var6.createBuilder();
            var7.requires((sharedSuggestionProvider) -> {
               return true;
            });
            if(var7.getCommand() != null) {
               var7.executes((commandContext) -> {
                  return 0;
               });
            }

            if(var7 instanceof RequiredArgumentBuilder) {
               RequiredArgumentBuilder<SharedSuggestionProvider, ?> var8 = (RequiredArgumentBuilder)var7;
               if(var8.getSuggestionsProvider() != null) {
                  var8.suggests(SuggestionProviders.safelySwap(var8.getSuggestionsProvider()));
               }
            }

            if(var7.getRedirect() != null) {
               var7.redirect((CommandNode)map.get(var7.getRedirect()));
            }

            CommandNode<SharedSuggestionProvider> var8 = var7.build();
            map.put(var6, var8);
            var2.addChild(var8);
            if(!var6.getChildren().isEmpty()) {
               this.fillUsableCommands(var6, var8, commandSourceStack, map);
            }
         }
      }

   }

   public static LiteralArgumentBuilder literal(String string) {
      return LiteralArgumentBuilder.literal(string);
   }

   public static RequiredArgumentBuilder argument(String string, ArgumentType argumentType) {
      return RequiredArgumentBuilder.argument(string, argumentType);
   }

   public static Predicate createValidator(Commands.ParseFunction commands$ParseFunction) {
      return (string) -> {
         try {
            commands$ParseFunction.parse(new StringReader(string));
            return true;
         } catch (CommandSyntaxException var3) {
            return false;
         }
      };
   }

   public CommandDispatcher getDispatcher() {
      return this.dispatcher;
   }

   @FunctionalInterface
   public interface ParseFunction {
      void parse(StringReader var1) throws CommandSyntaxException;
   }
}
