package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;

public class SaveOffCommand {
   private static final SimpleCommandExceptionType ERROR_ALREADY_OFF = new SimpleCommandExceptionType(new TranslatableComponent("commands.save.alreadyOff", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("save-off").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(4);
      })).executes((commandContext) -> {
         CommandSourceStack var1 = (CommandSourceStack)commandContext.getSource();
         boolean var2 = false;

         for(ServerLevel var4 : var1.getServer().getAllLevels()) {
            if(var4 != null && !var4.noSave) {
               var4.noSave = true;
               var2 = true;
            }
         }

         if(!var2) {
            throw ERROR_ALREADY_OFF.create();
         } else {
            var1.sendSuccess(new TranslatableComponent("commands.save.disabled", new Object[0]), true);
            return 1;
         }
      }));
   }
}
