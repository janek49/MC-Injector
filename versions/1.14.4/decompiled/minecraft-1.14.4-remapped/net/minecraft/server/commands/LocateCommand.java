package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;

public class LocateCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.locate.failed", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("locate").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.literal("Pillager_Outpost").executes((commandContext) -> {
         return locate((CommandSourceStack)commandContext.getSource(), "Pillager_Outpost");
      }))).then(Commands.literal("Mineshaft").executes((commandContext) -> {
         return locate((CommandSourceStack)commandContext.getSource(), "Mineshaft");
      }))).then(Commands.literal("Mansion").executes((commandContext) -> {
         return locate((CommandSourceStack)commandContext.getSource(), "Mansion");
      }))).then(Commands.literal("Igloo").executes((commandContext) -> {
         return locate((CommandSourceStack)commandContext.getSource(), "Igloo");
      }))).then(Commands.literal("Desert_Pyramid").executes((commandContext) -> {
         return locate((CommandSourceStack)commandContext.getSource(), "Desert_Pyramid");
      }))).then(Commands.literal("Jungle_Pyramid").executes((commandContext) -> {
         return locate((CommandSourceStack)commandContext.getSource(), "Jungle_Pyramid");
      }))).then(Commands.literal("Swamp_Hut").executes((commandContext) -> {
         return locate((CommandSourceStack)commandContext.getSource(), "Swamp_Hut");
      }))).then(Commands.literal("Stronghold").executes((commandContext) -> {
         return locate((CommandSourceStack)commandContext.getSource(), "Stronghold");
      }))).then(Commands.literal("Monument").executes((commandContext) -> {
         return locate((CommandSourceStack)commandContext.getSource(), "Monument");
      }))).then(Commands.literal("Fortress").executes((commandContext) -> {
         return locate((CommandSourceStack)commandContext.getSource(), "Fortress");
      }))).then(Commands.literal("EndCity").executes((commandContext) -> {
         return locate((CommandSourceStack)commandContext.getSource(), "EndCity");
      }))).then(Commands.literal("Ocean_Ruin").executes((commandContext) -> {
         return locate((CommandSourceStack)commandContext.getSource(), "Ocean_Ruin");
      }))).then(Commands.literal("Buried_Treasure").executes((commandContext) -> {
         return locate((CommandSourceStack)commandContext.getSource(), "Buried_Treasure");
      }))).then(Commands.literal("Shipwreck").executes((commandContext) -> {
         return locate((CommandSourceStack)commandContext.getSource(), "Shipwreck");
      }))).then(Commands.literal("Village").executes((commandContext) -> {
         return locate((CommandSourceStack)commandContext.getSource(), "Village");
      })));
   }

   private static int locate(CommandSourceStack commandSourceStack, String string) throws CommandSyntaxException {
      BlockPos var2 = new BlockPos(commandSourceStack.getPosition());
      BlockPos var3 = commandSourceStack.getLevel().findNearestMapFeature(string, var2, 100, false);
      if(var3 == null) {
         throw ERROR_FAILED.create();
      } else {
         int var4 = Mth.floor(dist(var2.getX(), var2.getZ(), var3.getX(), var3.getZ()));
         Component var5 = ComponentUtils.wrapInSquareBrackets(new TranslatableComponent("chat.coordinates", new Object[]{Integer.valueOf(var3.getX()), "~", Integer.valueOf(var3.getZ())})).withStyle((style) -> {
            style.setColor(ChatFormatting.GREEN).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + var3.getX() + " ~ " + var3.getZ())).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableComponent("chat.coordinates.tooltip", new Object[0])));
         });
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.locate.success", new Object[]{string, var5, Integer.valueOf(var4)}), false);
         return var4;
      }
   }

   private static float dist(int var0, int var1, int var2, int var3) {
      int var4 = var2 - var0;
      int var5 = var3 - var1;
      return Mth.sqrt((float)(var4 * var4 + var5 * var5));
   }
}
