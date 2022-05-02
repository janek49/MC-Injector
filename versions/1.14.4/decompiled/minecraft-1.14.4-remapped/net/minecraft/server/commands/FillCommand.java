package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType.Function;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.commands.SetBlockCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class FillCommand {
   private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("commands.fill.toobig", new Object[]{var0, var1});
   });
   private static final BlockInput HOLLOW_CORE = new BlockInput(Blocks.AIR.defaultBlockState(), Collections.emptySet(), (CompoundTag)null);
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.fill.failed", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("fill").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("block", BlockStateArgument.block()).executes((commandContext) -> {
         return fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"), BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), FillCommand.Mode.REPLACE, (Predicate)null);
      })).then(((LiteralArgumentBuilder)Commands.literal("replace").executes((commandContext) -> {
         return fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"), BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), FillCommand.Mode.REPLACE, (Predicate)null);
      })).then(Commands.argument("filter", BlockPredicateArgument.blockPredicate()).executes((commandContext) -> {
         return fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"), BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), FillCommand.Mode.REPLACE, BlockPredicateArgument.getBlockPredicate(commandContext, "filter"));
      })))).then(Commands.literal("keep").executes((commandContext) -> {
         return fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"), BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), FillCommand.Mode.REPLACE, (blockInWorld) -> {
            return blockInWorld.getLevel().isEmptyBlock(blockInWorld.getPos());
         });
      }))).then(Commands.literal("outline").executes((commandContext) -> {
         return fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"), BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), FillCommand.Mode.OUTLINE, (Predicate)null);
      }))).then(Commands.literal("hollow").executes((commandContext) -> {
         return fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"), BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), FillCommand.Mode.HOLLOW, (Predicate)null);
      }))).then(Commands.literal("destroy").executes((commandContext) -> {
         return fillBlocks((CommandSourceStack)commandContext.getSource(), new BoundingBox(BlockPosArgument.getLoadedBlockPos(commandContext, "from"), BlockPosArgument.getLoadedBlockPos(commandContext, "to")), BlockStateArgument.getBlock(commandContext, "block"), FillCommand.Mode.DESTROY, (Predicate)null);
      }))))));
   }

   private static int fillBlocks(CommandSourceStack commandSourceStack, BoundingBox boundingBox, BlockInput blockInput, FillCommand.Mode fillCommand$Mode, @Nullable Predicate predicate) throws CommandSyntaxException {
      int var5 = boundingBox.getXSpan() * boundingBox.getYSpan() * boundingBox.getZSpan();
      if(var5 > '耀') {
         throw ERROR_AREA_TOO_LARGE.create(Integer.valueOf('耀'), Integer.valueOf(var5));
      } else {
         List<BlockPos> var6 = Lists.newArrayList();
         ServerLevel var7 = commandSourceStack.getLevel();
         int var8 = 0;

         for(BlockPos var10 : BlockPos.betweenClosed(boundingBox.x0, boundingBox.y0, boundingBox.z0, boundingBox.x1, boundingBox.y1, boundingBox.z1)) {
            if(predicate == null || predicate.test(new BlockInWorld(var7, var10, true))) {
               BlockInput var11 = fillCommand$Mode.filter.filter(boundingBox, var10, blockInput, var7);
               if(var11 != null) {
                  BlockEntity var12 = var7.getBlockEntity(var10);
                  Clearable.tryClear(var12);
                  if(var11.place(var7, var10, 2)) {
                     var6.add(var10.immutable());
                     ++var8;
                  }
               }
            }
         }

         for(BlockPos var10 : var6) {
            Block var11 = var7.getBlockState(var10).getBlock();
            var7.blockUpdated(var10, var11);
         }

         if(var8 == 0) {
            throw ERROR_FAILED.create();
         } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.fill.success", new Object[]{Integer.valueOf(var8)}), true);
            return var8;
         }
      }
   }

   static enum Mode {
      REPLACE((boundingBox, blockPos, var2, serverLevel) -> {
         return var2;
      }),
      OUTLINE((boundingBox, blockPos, var2, serverLevel) -> {
         return blockPos.getX() != boundingBox.x0 && blockPos.getX() != boundingBox.x1 && blockPos.getY() != boundingBox.y0 && blockPos.getY() != boundingBox.y1 && blockPos.getZ() != boundingBox.z0 && blockPos.getZ() != boundingBox.z1?null:var2;
      }),
      HOLLOW((boundingBox, blockPos, var2, serverLevel) -> {
         return blockPos.getX() != boundingBox.x0 && blockPos.getX() != boundingBox.x1 && blockPos.getY() != boundingBox.y0 && blockPos.getY() != boundingBox.y1 && blockPos.getZ() != boundingBox.z0 && blockPos.getZ() != boundingBox.z1?FillCommand.HOLLOW_CORE:var2;
      }),
      DESTROY((boundingBox, blockPos, var2, serverLevel) -> {
         serverLevel.destroyBlock(blockPos, true);
         return var2;
      });

      public final SetBlockCommand.Filter filter;

      private Mode(SetBlockCommand.Filter filter) {
         this.filter = filter;
      }
   }
}
