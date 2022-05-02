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

public class SaveOnCommand {
   private static final SimpleCommandExceptionType ERROR_ALREADY_ON = new SimpleCommandExceptionType(new TranslatableComponent("commands.save.alreadyOn", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("save-on").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(4);
      })).executes((commandContext) -> {
         CommandSourceStack var1 = (CommandSourceStack)commandContext.getSource();
         boolean var2 = false;

         for(ServerLevel var4 : var1.getServer().getAllLevels()) {
            if(var4 != null && var4.noSave) {
               var4.noSave = false;
               var2 = true;
            }
         }

         if(!var2) {
            throw ERROR_ALREADY_ON.create();
         } else {
            var1.sendSuccess(new TranslatableComponent("commands.save.enabled", new Object[0]), true);
            return 1;
         }
      }));
   }
}
