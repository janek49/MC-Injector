package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class SetBlockCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.setblock.failed", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("setblock").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.argument("pos", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("block", BlockStateArgument.block()).executes((commandContext) -> {
         return setBlock((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "pos"), BlockStateArgument.getBlock(commandContext, "block"), SetBlockCommand.Mode.REPLACE, (Predicate)null);
      })).then(Commands.literal("destroy").executes((commandContext) -> {
         return setBlock((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "pos"), BlockStateArgument.getBlock(commandContext, "block"), SetBlockCommand.Mode.DESTROY, (Predicate)null);
      }))).then(Commands.literal("keep").executes((commandContext) -> {
         return setBlock((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "pos"), BlockStateArgument.getBlock(commandContext, "block"), SetBlockCommand.Mode.REPLACE, (blockInWorld) -> {
            return blockInWorld.getLevel().isEmptyBlock(blockInWorld.getPos());
         });
      }))).then(Commands.literal("replace").executes((commandContext) -> {
         return setBlock((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "pos"), BlockStateArgument.getBlock(commandContext, "block"), SetBlockCommand.Mode.REPLACE, (Predicate)null);
      })))));
   }

   private static int setBlock(CommandSourceStack commandSourceStack, BlockPos blockPos, BlockInput blockInput, SetBlockCommand.Mode setBlockCommand$Mode, @Nullable Predicate predicate) throws CommandSyntaxException {
      ServerLevel var5 = commandSourceStack.getLevel();
      if(predicate != null && !predicate.test(new BlockInWorld(var5, blockPos, true))) {
         throw ERROR_FAILED.create();
      } else {
         boolean var6;
         if(setBlockCommand$Mode == SetBlockCommand.Mode.DESTROY) {
            var5.destroyBlock(blockPos, true);
            var6 = !blockInput.getState().isAir();
         } else {
            BlockEntity var7 = var5.getBlockEntity(blockPos);
            Clearable.tryClear(var7);
            var6 = true;
         }

         if(var6 && !blockInput.place(var5, blockPos, 2)) {
            throw ERROR_FAILED.create();
         } else {
            var5.blockUpdated(blockPos, blockInput.getState().getBlock());
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.setblock.success", new Object[]{Integer.valueOf(blockPos.getX()), Integer.valueOf(blockPos.getY()), Integer.valueOf(blockPos.getZ())}), true);
            return 1;
         }
      }
   }

   public interface Filter {
      @Nullable
      BlockInput filter(BoundingBox var1, BlockPos var2, BlockInput var3, ServerLevel var4);
   }

   public static enum Mode {
      REPLACE,
      DESTROY;
   }
}
