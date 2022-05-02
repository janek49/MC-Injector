package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.dimension.DimensionType;

public class DifficultyCommand {
   private static final DynamicCommandExceptionType ERROR_ALREADY_DIFFICULT = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.difficulty.failure", new Object[]{object});
   });

   public static void register(CommandDispatcher commandDispatcher) {
      LiteralArgumentBuilder<CommandSourceStack> var1 = Commands.literal("difficulty");

      for(Difficulty var5 : Difficulty.values()) {
         var1.then(Commands.literal(var5.getKey()).executes((commandContext) -> {
            return setDifficulty((CommandSourceStack)commandContext.getSource(), var5);
         }));
      }

      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)var1.requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).executes((commandContext) -> {
         Difficulty var1 = ((CommandSourceStack)commandContext.getSource()).getLevel().getDifficulty();
         ((CommandSourceStack)commandContext.getSource()).sendSuccess(new TranslatableComponent("commands.difficulty.query", new Object[]{var1.getDisplayName()}), false);
         return var1.getId();
      }));
   }

   public static int setDifficulty(CommandSourceStack commandSourceStack, Difficulty difficulty) throws CommandSyntaxException {
      MinecraftServer var2 = commandSourceStack.getServer();
      if(var2.getLevel(DimensionType.OVERWORLD).getDifficulty() == difficulty) {
         throw ERROR_ALREADY_DIFFICULT.create(difficulty.getKey());
      } else {
         var2.setDifficulty(difficulty, true);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.difficulty.success", new Object[]{difficulty.getDisplayName()}), true);
         return 0;
      }
   }
}
