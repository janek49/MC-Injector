package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.UnopenedPack;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelData;

public class DataPackCommand {
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_PACK = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.datapack.unknown", new Object[]{object});
   });
   private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_ENABLED = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.datapack.enable.failed", new Object[]{object});
   });
   private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_DISABLED = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.datapack.disable.failed", new Object[]{object});
   });
   private static final SuggestionProvider SELECTED_PACKS = (commandContext, suggestionsBuilder) -> {
      return SharedSuggestionProvider.suggest(((CommandSourceStack)commandContext.getSource()).getServer().getPackRepository().getSelected().stream().map(UnopenedPack::getId).map(StringArgumentType::escapeIfRequired), suggestionsBuilder);
   };
   private static final SuggestionProvider AVAILABLE_PACKS = (commandContext, suggestionsBuilder) -> {
      return SharedSuggestionProvider.suggest(((CommandSourceStack)commandContext.getSource()).getServer().getPackRepository().getUnselected().stream().map(UnopenedPack::getId).map(StringArgumentType::escapeIfRequired), suggestionsBuilder);
   };

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("datapack").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.literal("enable").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("name", StringArgumentType.string()).suggests(AVAILABLE_PACKS).executes((commandContext) -> {
         return enablePack((CommandSourceStack)commandContext.getSource(), getPack(commandContext, "name", true), (list, unopenedPack) -> {
            unopenedPack.getDefaultPosition().insert(list, unopenedPack, (unopenedPack) -> {
               return unopenedPack;
            }, false);
         });
      })).then(Commands.literal("after").then(Commands.argument("existing", StringArgumentType.string()).suggests(SELECTED_PACKS).executes((commandContext) -> {
         return enablePack((CommandSourceStack)commandContext.getSource(), getPack(commandContext, "name", true), (list, unopenedPack) -> {
            list.add(list.indexOf(getPack(commandContext, "existing", false)) + 1, unopenedPack);
         });
      })))).then(Commands.literal("before").then(Commands.argument("existing", StringArgumentType.string()).suggests(SELECTED_PACKS).executes((commandContext) -> {
         return enablePack((CommandSourceStack)commandContext.getSource(), getPack(commandContext, "name", true), (list, unopenedPack) -> {
            list.add(list.indexOf(getPack(commandContext, "existing", false)), unopenedPack);
         });
      })))).then(Commands.literal("last").executes((commandContext) -> {
         return enablePack((CommandSourceStack)commandContext.getSource(), getPack(commandContext, "name", true), List::add);
      }))).then(Commands.literal("first").executes((commandContext) -> {
         return enablePack((CommandSourceStack)commandContext.getSource(), getPack(commandContext, "name", true), (list, unopenedPack) -> {
            list.add(0, unopenedPack);
         });
      }))))).then(Commands.literal("disable").then(Commands.argument("name", StringArgumentType.string()).suggests(SELECTED_PACKS).executes((commandContext) -> {
         return disablePack((CommandSourceStack)commandContext.getSource(), getPack(commandContext, "name", false));
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("list").executes((commandContext) -> {
         return listPacks((CommandSourceStack)commandContext.getSource());
      })).then(Commands.literal("available").executes((commandContext) -> {
         return listAvailablePacks((CommandSourceStack)commandContext.getSource());
      }))).then(Commands.literal("enabled").executes((commandContext) -> {
         return listEnabledPacks((CommandSourceStack)commandContext.getSource());
      }))));
   }

   private static int enablePack(CommandSourceStack commandSourceStack, UnopenedPack unopenedPack, DataPackCommand.Inserter dataPackCommand$Inserter) throws CommandSyntaxException {
      PackRepository<UnopenedPack> var3 = commandSourceStack.getServer().getPackRepository();
      List<UnopenedPack> var4 = Lists.newArrayList(var3.getSelected());
      dataPackCommand$Inserter.apply(var4, unopenedPack);
      var3.setSelected(var4);
      LevelData var5 = commandSourceStack.getServer().getLevel(DimensionType.OVERWORLD).getLevelData();
      var5.getEnabledDataPacks().clear();
      var3.getSelected().forEach((unopenedPack) -> {
         var5.getEnabledDataPacks().add(unopenedPack.getId());
      });
      var5.getDisabledDataPacks().remove(unopenedPack.getId());
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.datapack.enable.success", new Object[]{unopenedPack.getChatLink(true)}), true);
      commandSourceStack.getServer().reloadResources();
      return var3.getSelected().size();
   }

   private static int disablePack(CommandSourceStack commandSourceStack, UnopenedPack unopenedPack) {
      PackRepository<UnopenedPack> var2 = commandSourceStack.getServer().getPackRepository();
      List<UnopenedPack> var3 = Lists.newArrayList(var2.getSelected());
      var3.remove(unopenedPack);
      var2.setSelected(var3);
      LevelData var4 = commandSourceStack.getServer().getLevel(DimensionType.OVERWORLD).getLevelData();
      var4.getEnabledDataPacks().clear();
      var2.getSelected().forEach((unopenedPack) -> {
         var4.getEnabledDataPacks().add(unopenedPack.getId());
      });
      var4.getDisabledDataPacks().add(unopenedPack.getId());
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.datapack.disable.success", new Object[]{unopenedPack.getChatLink(true)}), true);
      commandSourceStack.getServer().reloadResources();
      return var2.getSelected().size();
   }

   private static int listPacks(CommandSourceStack commandSourceStack) {
      return listEnabledPacks(commandSourceStack) + listAvailablePacks(commandSourceStack);
   }

   private static int listAvailablePacks(CommandSourceStack commandSourceStack) {
      PackRepository<UnopenedPack> var1 = commandSourceStack.getServer().getPackRepository();
      if(var1.getUnselected().isEmpty()) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.datapack.list.available.none", new Object[0]), false);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.datapack.list.available.success", new Object[]{Integer.valueOf(var1.getUnselected().size()), ComponentUtils.formatList(var1.getUnselected(), (unopenedPack) -> {
            return unopenedPack.getChatLink(false);
         })}), false);
      }

      return var1.getUnselected().size();
   }

   private static int listEnabledPacks(CommandSourceStack commandSourceStack) {
      PackRepository<UnopenedPack> var1 = commandSourceStack.getServer().getPackRepository();
      if(var1.getSelected().isEmpty()) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.datapack.list.enabled.none", new Object[0]), false);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.datapack.list.enabled.success", new Object[]{Integer.valueOf(var1.getSelected().size()), ComponentUtils.formatList(var1.getSelected(), (unopenedPack) -> {
            return unopenedPack.getChatLink(true);
         })}), false);
      }

      return var1.getSelected().size();
   }

   private static UnopenedPack getPack(CommandContext commandContext, String string, boolean var2) throws CommandSyntaxException {
      String string = StringArgumentType.getString(commandContext, string);
      PackRepository<UnopenedPack> var4 = ((CommandSourceStack)commandContext.getSource()).getServer().getPackRepository();
      UnopenedPack var5 = var4.getPack(string);
      if(var5 == null) {
         throw ERROR_UNKNOWN_PACK.create(string);
      } else {
         boolean var6 = var4.getSelected().contains(var5);
         if(var2 && var6) {
            throw ERROR_PACK_ALREADY_ENABLED.create(string);
         } else if(!var2 && !var6) {
            throw ERROR_PACK_ALREADY_DISABLED.create(string);
         } else {
            return var5;
         }
      }
   }

   interface Inserter {
      void apply(List var1, UnopenedPack var2) throws CommandSyntaxException;
   }
}
