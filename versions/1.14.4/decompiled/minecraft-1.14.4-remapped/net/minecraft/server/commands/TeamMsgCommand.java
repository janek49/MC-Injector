package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;

public class TeamMsgCommand {
   private static final SimpleCommandExceptionType ERROR_NOT_ON_TEAM = new SimpleCommandExceptionType(new TranslatableComponent("commands.teammsg.failed.noteam", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      LiteralCommandNode<CommandSourceStack> var1 = commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("teammsg").then(Commands.argument("message", MessageArgument.message()).executes((commandContext) -> {
         return sendMessage((CommandSourceStack)commandContext.getSource(), MessageArgument.getMessage(commandContext, "message"));
      })));
      commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("tm").redirect(var1));
   }

   private static int sendMessage(CommandSourceStack commandSourceStack, Component component) throws CommandSyntaxException {
      Entity var2 = commandSourceStack.getEntityOrException();
      PlayerTeam var3 = (PlayerTeam)var2.getTeam();
      if(var3 == null) {
         throw ERROR_NOT_ON_TEAM.create();
      } else {
         Consumer<Style> var4 = (style) -> {
            style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.type.team.hover", new Object[0]))).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/teammsg "));
         };
         Component var5 = var3.getFormattedDisplayName().withStyle(var4);

         for(Component var7 : var5.getSiblings()) {
            var7.withStyle(var4);
         }

         List<ServerPlayer> var6 = commandSourceStack.getServer().getPlayerList().getPlayers();

         for(ServerPlayer var8 : var6) {
            if(var8 == var2) {
               var8.sendMessage(new TranslatableComponent("chat.type.team.sent", new Object[]{var5, commandSourceStack.getDisplayName(), component.deepCopy()}));
            } else if(var8.getTeam() == var3) {
               var8.sendMessage(new TranslatableComponent("chat.type.team.text", new Object[]{var5, commandSourceStack.getDisplayName(), component.deepCopy()}));
            }
         }

         return var6.size();
      }
   }
}
