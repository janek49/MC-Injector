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
import java.util.Deque;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class CloneCommands {
   private static final SimpleCommandExceptionType ERROR_OVERLAP = new SimpleCommandExceptionType(new TranslatableComponent("commands.clone.overlap", new Object[0]));
   private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("commands.clone.toobig", new Object[]{var0, var1});
   });
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.clone.failed", new Object[0]));
   public static final Predicate FILTER_AIR = (blockInWorld) -> {
      return !blockInWorld.getState().isAir();
   };

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("clone").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.argument("begin", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("destination", BlockPosArgument.blockPos()).executes((commandContext) -> {
         return clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "begin"), BlockPosArgument.getLoadedBlockPos(commandContext, "end"), BlockPosArgument.getLoadedBlockPos(commandContext, "destination"), (blockInWorld) -> {
            return true;
         }, CloneCommands.Mode.NORMAL);
      })).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("replace").executes((commandContext) -> {
         return clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "begin"), BlockPosArgument.getLoadedBlockPos(commandContext, "end"), BlockPosArgument.getLoadedBlockPos(commandContext, "destination"), (blockInWorld) -> {
            return true;
         }, CloneCommands.Mode.NORMAL);
      })).then(Commands.literal("force").executes((commandContext) -> {
         return clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "begin"), BlockPosArgument.getLoadedBlockPos(commandContext, "end"), BlockPosArgument.getLoadedBlockPos(commandContext, "destination"), (blockInWorld) -> {
            return true;
         }, CloneCommands.Mode.FORCE);
      }))).then(Commands.literal("move").executes((commandContext) -> {
         return clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "begin"), BlockPosArgument.getLoadedBlockPos(commandContext, "end"), BlockPosArgument.getLoadedBlockPos(commandContext, "destination"), (blockInWorld) -> {
            return true;
         }, CloneCommands.Mode.MOVE);
      }))).then(Commands.literal("normal").executes((commandContext) -> {
         return clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "begin"), BlockPosArgument.getLoadedBlockPos(commandContext, "end"), BlockPosArgument.getLoadedBlockPos(commandContext, "destination"), (blockInWorld) -> {
            return true;
         }, CloneCommands.Mode.NORMAL);
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("masked").executes((commandContext) -> {
         return clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "begin"), BlockPosArgument.getLoadedBlockPos(commandContext, "end"), BlockPosArgument.getLoadedBlockPos(commandContext, "destination"), FILTER_AIR, CloneCommands.Mode.NORMAL);
      })).then(Commands.literal("force").executes((commandContext) -> {
         return clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "begin"), BlockPosArgument.getLoadedBlockPos(commandContext, "end"), BlockPosArgument.getLoadedBlockPos(commandContext, "destination"), FILTER_AIR, CloneCommands.Mode.FORCE);
      }))).then(Commands.literal("move").executes((commandContext) -> {
         return clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "begin"), BlockPosArgument.getLoadedBlockPos(commandContext, "end"), BlockPosArgument.getLoadedBlockPos(commandContext, "destination"), FILTER_AIR, CloneCommands.Mode.MOVE);
      }))).then(Commands.literal("normal").executes((commandContext) -> {
         return clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "begin"), BlockPosArgument.getLoadedBlockPos(commandContext, "end"), BlockPosArgument.getLoadedBlockPos(commandContext, "destination"), FILTER_AIR, CloneCommands.Mode.NORMAL);
      })))).then(Commands.literal("filtered").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("filter", BlockPredicateArgument.blockPredicate()).executes((commandContext) -> {
         return clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "begin"), BlockPosArgument.getLoadedBlockPos(commandContext, "end"), BlockPosArgument.getLoadedBlockPos(commandContext, "destination"), BlockPredicateArgument.getBlockPredicate(commandContext, "filter"), CloneCommands.Mode.NORMAL);
      })).then(Commands.literal("force").executes((commandContext) -> {
         return clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "begin"), BlockPosArgument.getLoadedBlockPos(commandContext, "end"), BlockPosArgument.getLoadedBlockPos(commandContext, "destination"), BlockPredicateArgument.getBlockPredicate(commandContext, "filter"), CloneCommands.Mode.FORCE);
      }))).then(Commands.literal("move").executes((commandContext) -> {
         return clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "begin"), BlockPosArgument.getLoadedBlockPos(commandContext, "end"), BlockPosArgument.getLoadedBlockPos(commandContext, "destination"), BlockPredicateArgument.getBlockPredicate(commandContext, "filter"), CloneCommands.Mode.MOVE);
      }))).then(Commands.literal("normal").executes((commandContext) -> {
         return clone((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "begin"), BlockPosArgument.getLoadedBlockPos(commandContext, "end"), BlockPosArgument.getLoadedBlockPos(commandContext, "destination"), BlockPredicateArgument.getBlockPredicate(commandContext, "filter"), CloneCommands.Mode.NORMAL);
      }))))))));
   }

   private static int clone(CommandSourceStack commandSourceStack, BlockPos var1, BlockPos var2, BlockPos var3, Predicate predicate, CloneCommands.Mode cloneCommands$Mode) throws CommandSyntaxException {
      BoundingBox var6 = new BoundingBox(var1, var2);
      BlockPos var7 = var3.offset(var6.getLength());
      BoundingBox var8 = new BoundingBox(var3, var7);
      if(!cloneCommands$Mode.canOverlap() && var8.intersects(var6)) {
         throw ERROR_OVERLAP.create();
      } else {
         int var9 = var6.getXSpan() * var6.getYSpan() * var6.getZSpan();
         if(var9 > '耀') {
            throw ERROR_AREA_TOO_LARGE.create(Integer.valueOf('耀'), Integer.valueOf(var9));
         } else {
            ServerLevel var10 = commandSourceStack.getLevel();
            if(var10.hasChunksAt(var1, var2) && var10.hasChunksAt(var3, var7)) {
               List<CloneCommands.CloneBlockInfo> var11 = Lists.newArrayList();
               List<CloneCommands.CloneBlockInfo> var12 = Lists.newArrayList();
               List<CloneCommands.CloneBlockInfo> var13 = Lists.newArrayList();
               Deque<BlockPos> var14 = Lists.newLinkedList();
               BlockPos var15 = new BlockPos(var8.x0 - var6.x0, var8.y0 - var6.y0, var8.z0 - var6.z0);

               for(int var16 = var6.z0; var16 <= var6.z1; ++var16) {
                  for(int var17 = var6.y0; var17 <= var6.y1; ++var17) {
                     for(int var18 = var6.x0; var18 <= var6.x1; ++var18) {
                        BlockPos var19 = new BlockPos(var18, var17, var16);
                        BlockPos var20 = var19.offset(var15);
                        BlockInWorld var21 = new BlockInWorld(var10, var19, false);
                        BlockState var22 = var21.getState();
                        if(predicate.test(var21)) {
                           BlockEntity var23 = var10.getBlockEntity(var19);
                           if(var23 != null) {
                              CompoundTag var24 = var23.save(new CompoundTag());
                              var12.add(new CloneCommands.CloneBlockInfo(var20, var22, var24));
                              var14.addLast(var19);
                           } else if(!var22.isSolidRender(var10, var19) && !var22.isCollisionShapeFullBlock(var10, var19)) {
                              var13.add(new CloneCommands.CloneBlockInfo(var20, var22, (CompoundTag)null));
                              var14.addFirst(var19);
                           } else {
                              var11.add(new CloneCommands.CloneBlockInfo(var20, var22, (CompoundTag)null));
                              var14.addLast(var19);
                           }
                        }
                     }
                  }
               }

               if(cloneCommands$Mode == CloneCommands.Mode.MOVE) {
                  for(BlockPos var17 : var14) {
                     BlockEntity var18 = var10.getBlockEntity(var17);
                     Clearable.tryClear(var18);
                     var10.setBlock(var17, Blocks.BARRIER.defaultBlockState(), 2);
                  }

                  for(BlockPos var17 : var14) {
                     var10.setBlock(var17, Blocks.AIR.defaultBlockState(), 3);
                  }
               }

               List<CloneCommands.CloneBlockInfo> var16 = Lists.newArrayList();
               var16.addAll(var11);
               var16.addAll(var12);
               var16.addAll(var13);
               List<CloneCommands.CloneBlockInfo> var17 = Lists.reverse(var16);

               for(CloneCommands.CloneBlockInfo var19 : var17) {
                  BlockEntity var20 = var10.getBlockEntity(var19.pos);
                  Clearable.tryClear(var20);
                  var10.setBlock(var19.pos, Blocks.BARRIER.defaultBlockState(), 2);
               }

               int var18 = 0;

               for(CloneCommands.CloneBlockInfo var20 : var16) {
                  if(var10.setBlock(var20.pos, var20.state, 2)) {
                     ++var18;
                  }
               }

               for(CloneCommands.CloneBlockInfo var20 : var12) {
                  BlockEntity var21 = var10.getBlockEntity(var20.pos);
                  if(var20.tag != null && var21 != null) {
                     var20.tag.putInt("x", var20.pos.getX());
                     var20.tag.putInt("y", var20.pos.getY());
                     var20.tag.putInt("z", var20.pos.getZ());
                     var21.load(var20.tag);
                     var21.setChanged();
                  }

                  var10.setBlock(var20.pos, var20.state, 2);
               }

               for(CloneCommands.CloneBlockInfo var20 : var17) {
                  var10.blockUpdated(var20.pos, var20.state.getBlock());
               }

               var10.getBlockTicks().copy(var6, var15);
               if(var18 == 0) {
                  throw ERROR_FAILED.create();
               } else {
                  commandSourceStack.sendSuccess(new TranslatableComponent("commands.clone.success", new Object[]{Integer.valueOf(var18)}), true);
                  return var18;
               }
            } else {
               throw BlockPosArgument.ERROR_NOT_LOADED.create();
            }
         }
      }
   }

   static class CloneBlockInfo {
      public final BlockPos pos;
      public final BlockState state;
      @Nullable
      public final CompoundTag tag;

      public CloneBlockInfo(BlockPos pos, BlockState state, @Nullable CompoundTag tag) {
         this.pos = pos;
         this.state = state;
         this.tag = tag;
      }
   }

   static enum Mode {
      FORCE(true),
      MOVE(true),
      NORMAL(false);

      private final boolean canOverlap;

      private Mode(boolean canOverlap) {
         this.canOverlap = canOverlap;
      }

      public boolean canOverlap() {
         return this.canOverlap;
      }
   }
}
