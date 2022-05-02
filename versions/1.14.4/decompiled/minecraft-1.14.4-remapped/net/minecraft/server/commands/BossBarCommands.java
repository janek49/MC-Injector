package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class BossBarCommands {
   private static final DynamicCommandExceptionType ERROR_ALREADY_EXISTS = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.bossbar.create.failed", new Object[]{object});
   });
   private static final DynamicCommandExceptionType ERROR_DOESNT_EXIST = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.bossbar.unknown", new Object[]{object});
   });
   private static final SimpleCommandExceptionType ERROR_NO_PLAYER_CHANGE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.players.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_NO_NAME_CHANGE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.name.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_NO_COLOR_CHANGE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.color.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_NO_STYLE_CHANGE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.style.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_NO_VALUE_CHANGE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.value.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_NO_MAX_CHANGE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.max.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_ALREADY_HIDDEN = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.visibility.unchanged.hidden", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_ALREADY_VISIBLE = new SimpleCommandExceptionType(new TranslatableComponent("commands.bossbar.set.visibility.unchanged.visible", new Object[0]));
   public static final SuggestionProvider SUGGEST_BOSS_BAR = (commandContext, suggestionsBuilder) -> {
      return SharedSuggestionProvider.suggestResource((Iterable)((CommandSourceStack)commandContext.getSource()).getServer().getCustomBossEvents().getIds(), suggestionsBuilder);
   };

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("bossbar").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.literal("add").then(Commands.argument("id", ResourceLocationArgument.id()).then(Commands.argument("name", ComponentArgument.textComponent()).executes((commandContext) -> {
         return createBar((CommandSourceStack)commandContext.getSource(), ResourceLocationArgument.getId(commandContext, "id"), ComponentArgument.getComponent(commandContext, "name"));
      }))))).then(Commands.literal("remove").then(Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_BOSS_BAR).executes((commandContext) -> {
         return removeBar((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext));
      })))).then(Commands.literal("list").executes((commandContext) -> {
         return listBars((CommandSourceStack)commandContext.getSource());
      }))).then(Commands.literal("set").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_BOSS_BAR).then(Commands.literal("name").then(Commands.argument("name", ComponentArgument.textComponent()).executes((commandContext) -> {
         return setName((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext), ComponentArgument.getComponent(commandContext, "name"));
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("color").then(Commands.literal("pink").executes((commandContext) -> {
         return setColor((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext), BossEvent.BossBarColor.PINK);
      }))).then(Commands.literal("blue").executes((commandContext) -> {
         return setColor((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext), BossEvent.BossBarColor.BLUE);
      }))).then(Commands.literal("red").executes((commandContext) -> {
         return setColor((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext), BossEvent.BossBarColor.RED);
      }))).then(Commands.literal("green").executes((commandContext) -> {
         return setColor((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext), BossEvent.BossBarColor.GREEN);
      }))).then(Commands.literal("yellow").executes((commandContext) -> {
         return setColor((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext), BossEvent.BossBarColor.YELLOW);
      }))).then(Commands.literal("purple").executes((commandContext) -> {
         return setColor((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext), BossEvent.BossBarColor.PURPLE);
      }))).then(Commands.literal("white").executes((commandContext) -> {
         return setColor((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext), BossEvent.BossBarColor.WHITE);
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("style").then(Commands.literal("progress").executes((commandContext) -> {
         return setStyle((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext), BossEvent.BossBarOverlay.PROGRESS);
      }))).then(Commands.literal("notched_6").executes((commandContext) -> {
         return setStyle((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext), BossEvent.BossBarOverlay.NOTCHED_6);
      }))).then(Commands.literal("notched_10").executes((commandContext) -> {
         return setStyle((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext), BossEvent.BossBarOverlay.NOTCHED_10);
      }))).then(Commands.literal("notched_12").executes((commandContext) -> {
         return setStyle((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext), BossEvent.BossBarOverlay.NOTCHED_12);
      }))).then(Commands.literal("notched_20").executes((commandContext) -> {
         return setStyle((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext), BossEvent.BossBarOverlay.NOTCHED_20);
      })))).then(Commands.literal("value").then(Commands.argument("value", IntegerArgumentType.integer(0)).executes((commandContext) -> {
         return setValue((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext), IntegerArgumentType.getInteger(commandContext, "value"));
      })))).then(Commands.literal("max").then(Commands.argument("max", IntegerArgumentType.integer(1)).executes((commandContext) -> {
         return setMax((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext), IntegerArgumentType.getInteger(commandContext, "max"));
      })))).then(Commands.literal("visible").then(Commands.argument("visible", BoolArgumentType.bool()).executes((commandContext) -> {
         return setVisible((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext), BoolArgumentType.getBool(commandContext, "visible"));
      })))).then(((LiteralArgumentBuilder)Commands.literal("players").executes((commandContext) -> {
         return setPlayers((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext), Collections.emptyList());
      })).then(Commands.argument("targets", EntityArgument.players()).executes((commandContext) -> {
         return setPlayers((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext), EntityArgument.getOptionalPlayers(commandContext, "targets"));
      })))))).then(Commands.literal("get").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("id", ResourceLocationArgument.id()).suggests(SUGGEST_BOSS_BAR).then(Commands.literal("value").executes((commandContext) -> {
         return getValue((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext));
      }))).then(Commands.literal("max").executes((commandContext) -> {
         return getMax((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext));
      }))).then(Commands.literal("visible").executes((commandContext) -> {
         return getVisible((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext));
      }))).then(Commands.literal("players").executes((commandContext) -> {
         return getPlayers((CommandSourceStack)commandContext.getSource(), getBossBar(commandContext));
      })))));
   }

   private static int getValue(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent) {
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.get.value", new Object[]{customBossEvent.getDisplayName(), Integer.valueOf(customBossEvent.getValue())}), true);
      return customBossEvent.getValue();
   }

   private static int getMax(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent) {
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.get.max", new Object[]{customBossEvent.getDisplayName(), Integer.valueOf(customBossEvent.getMax())}), true);
      return customBossEvent.getMax();
   }

   private static int getVisible(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent) {
      if(customBossEvent.isVisible()) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.get.visible.visible", new Object[]{customBossEvent.getDisplayName()}), true);
         return 1;
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.get.visible.hidden", new Object[]{customBossEvent.getDisplayName()}), true);
         return 0;
      }
   }

   private static int getPlayers(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent) {
      if(customBossEvent.getPlayers().isEmpty()) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.get.players.none", new Object[]{customBossEvent.getDisplayName()}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.get.players.some", new Object[]{customBossEvent.getDisplayName(), Integer.valueOf(customBossEvent.getPlayers().size()), ComponentUtils.formatList(customBossEvent.getPlayers(), Player::getDisplayName)}), true);
      }

      return customBossEvent.getPlayers().size();
   }

   private static int setVisible(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent, boolean var2) throws CommandSyntaxException {
      if(customBossEvent.isVisible() == var2) {
         if(var2) {
            throw ERROR_ALREADY_VISIBLE.create();
         } else {
            throw ERROR_ALREADY_HIDDEN.create();
         }
      } else {
         customBossEvent.setVisible(var2);
         if(var2) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.set.visible.success.visible", new Object[]{customBossEvent.getDisplayName()}), true);
         } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.set.visible.success.hidden", new Object[]{customBossEvent.getDisplayName()}), true);
         }

         return 0;
      }
   }

   private static int setValue(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent, int var2) throws CommandSyntaxException {
      if(customBossEvent.getValue() == var2) {
         throw ERROR_NO_VALUE_CHANGE.create();
      } else {
         customBossEvent.setValue(var2);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.set.value.success", new Object[]{customBossEvent.getDisplayName(), Integer.valueOf(var2)}), true);
         return var2;
      }
   }

   private static int setMax(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent, int var2) throws CommandSyntaxException {
      if(customBossEvent.getMax() == var2) {
         throw ERROR_NO_MAX_CHANGE.create();
      } else {
         customBossEvent.setMax(var2);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.set.max.success", new Object[]{customBossEvent.getDisplayName(), Integer.valueOf(var2)}), true);
         return var2;
      }
   }

   private static int setColor(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent, BossEvent.BossBarColor bossEvent$BossBarColor) throws CommandSyntaxException {
      if(customBossEvent.getColor().equals(bossEvent$BossBarColor)) {
         throw ERROR_NO_COLOR_CHANGE.create();
      } else {
         customBossEvent.setColor(bossEvent$BossBarColor);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.set.color.success", new Object[]{customBossEvent.getDisplayName()}), true);
         return 0;
      }
   }

   private static int setStyle(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent, BossEvent.BossBarOverlay bossEvent$BossBarOverlay) throws CommandSyntaxException {
      if(customBossEvent.getOverlay().equals(bossEvent$BossBarOverlay)) {
         throw ERROR_NO_STYLE_CHANGE.create();
      } else {
         customBossEvent.setOverlay(bossEvent$BossBarOverlay);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.set.style.success", new Object[]{customBossEvent.getDisplayName()}), true);
         return 0;
      }
   }

   private static int setName(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent, Component component) throws CommandSyntaxException {
      Component component = ComponentUtils.updateForEntity(commandSourceStack, component, (Entity)null, 0);
      if(customBossEvent.getName().equals(component)) {
         throw ERROR_NO_NAME_CHANGE.create();
      } else {
         customBossEvent.setName(component);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.set.name.success", new Object[]{customBossEvent.getDisplayName()}), true);
         return 0;
      }
   }

   private static int setPlayers(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent, Collection collection) throws CommandSyntaxException {
      boolean var3 = customBossEvent.setPlayers(collection);
      if(!var3) {
         throw ERROR_NO_PLAYER_CHANGE.create();
      } else {
         if(customBossEvent.getPlayers().isEmpty()) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.set.players.success.none", new Object[]{customBossEvent.getDisplayName()}), true);
         } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.set.players.success.some", new Object[]{customBossEvent.getDisplayName(), Integer.valueOf(collection.size()), ComponentUtils.formatList(collection, Player::getDisplayName)}), true);
         }

         return customBossEvent.getPlayers().size();
      }
   }

   private static int listBars(CommandSourceStack commandSourceStack) {
      Collection<CustomBossEvent> var1 = commandSourceStack.getServer().getCustomBossEvents().getEvents();
      if(var1.isEmpty()) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.list.bars.none", new Object[0]), false);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.list.bars.some", new Object[]{Integer.valueOf(var1.size()), ComponentUtils.formatList(var1, CustomBossEvent::getDisplayName)}), false);
      }

      return var1.size();
   }

   private static int createBar(CommandSourceStack commandSourceStack, ResourceLocation resourceLocation, Component component) throws CommandSyntaxException {
      CustomBossEvents var3 = commandSourceStack.getServer().getCustomBossEvents();
      if(var3.get(resourceLocation) != null) {
         throw ERROR_ALREADY_EXISTS.create(resourceLocation.toString());
      } else {
         CustomBossEvent var4 = var3.create(resourceLocation, ComponentUtils.updateForEntity(commandSourceStack, component, (Entity)null, 0));
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.create.success", new Object[]{var4.getDisplayName()}), true);
         return var3.getEvents().size();
      }
   }

   private static int removeBar(CommandSourceStack commandSourceStack, CustomBossEvent customBossEvent) {
      CustomBossEvents var2 = commandSourceStack.getServer().getCustomBossEvents();
      customBossEvent.removeAllPlayers();
      var2.remove(customBossEvent);
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.bossbar.remove.success", new Object[]{customBossEvent.getDisplayName()}), true);
      return var2.getEvents().size();
   }

   public static CustomBossEvent getBossBar(CommandContext commandContext) throws CommandSyntaxException {
      ResourceLocation var1 = ResourceLocationArgument.getId(commandContext, "id");
      CustomBossEvent var2 = ((CommandSourceStack)commandContext.getSource()).getServer().getCustomBossEvents().get(var1);
      if(var2 == null) {
         throw ERROR_DOESNT_EXIST.create(var1.toString());
      } else {
         return var2;
      }
   }
}
