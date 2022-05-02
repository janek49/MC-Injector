package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class SeedCommand {
   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("seed").requires((commandSourceStack) -> {
         return commandSourceStack.getServer().isSingleplayer() || commandSourceStack.hasPermission(2);
      })).executes((commandContext) -> {
         long var1 = ((CommandSourceStack)commandContext.getSource()).getLevel().getSeed();
         Component var3 = ComponentUtils.wrapInSquareBrackets((new TextComponent(String.valueOf(var1))).withStyle((style) -> {
            style.setColor(ChatFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.valueOf(var1))).setInsertion(String.valueOf(var1));
         }));
         ((CommandSourceStack)commandContext.getSource()).sendSuccess(new TranslatableComponent("commands.seed.success", new Object[]{var3}), false);
         return (int)var1;
      }));
   }
}
