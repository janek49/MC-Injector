package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.HttpUtil;

public class PublishCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.publish.failed", new Object[0]));
   private static final DynamicCommandExceptionType ERROR_ALREADY_PUBLISHED = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.publish.alreadyPublished", new Object[]{object});
   });

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("publish").requires((commandSourceStack) -> {
         return commandSourceStack.getServer().isSingleplayer() && commandSourceStack.hasPermission(4);
      })).executes((commandContext) -> {
         return publish((CommandSourceStack)commandContext.getSource(), HttpUtil.getAvailablePort());
      })).then(Commands.argument("port", IntegerArgumentType.integer(0, '\uffff')).executes((commandContext) -> {
         return publish((CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "port"));
      })));
   }

   private static int publish(CommandSourceStack commandSourceStack, int var1) throws CommandSyntaxException {
      if(commandSourceStack.getServer().isPublished()) {
         throw ERROR_ALREADY_PUBLISHED.create(Integer.valueOf(commandSourceStack.getServer().getPort()));
      } else if(!commandSourceStack.getServer().publishServer(commandSourceStack.getServer().getDefaultGameType(), false, var1)) {
         throw ERROR_FAILED.create();
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.publish.success", new Object[]{Integer.valueOf(var1)}), true);
         return var1;
      }
   }
}
