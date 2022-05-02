package net.minecraft.server.commands;

import com.google.common.base.Joiner;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType.Function;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.function.LongConsumer;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.dimension.DimensionType;

public class ForceLoadCommand {
   private static final Dynamic2CommandExceptionType ERROR_TOO_MANY_CHUNKS = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("commands.forceload.toobig", new Object[]{var0, var1});
   });
   private static final Dynamic2CommandExceptionType ERROR_NOT_TICKING = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("commands.forceload.query.failure", new Object[]{var0, var1});
   });
   private static final SimpleCommandExceptionType ERROR_ALL_ADDED = new SimpleCommandExceptionType(new TranslatableComponent("commands.forceload.added.failure", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_NONE_REMOVED = new SimpleCommandExceptionType(new TranslatableComponent("commands.forceload.removed.failure", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("forceload").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.literal("add").then(((RequiredArgumentBuilder)Commands.argument("from", ColumnPosArgument.columnPos()).executes((commandContext) -> {
         return changeForceLoad((CommandSourceStack)commandContext.getSource(), ColumnPosArgument.getColumnPos(commandContext, "from"), ColumnPosArgument.getColumnPos(commandContext, "from"), true);
      })).then(Commands.argument("to", ColumnPosArgument.columnPos()).executes((commandContext) -> {
         return changeForceLoad((CommandSourceStack)commandContext.getSource(), ColumnPosArgument.getColumnPos(commandContext, "from"), ColumnPosArgument.getColumnPos(commandContext, "to"), true);
      }))))).then(((LiteralArgumentBuilder)Commands.literal("remove").then(((RequiredArgumentBuilder)Commands.argument("from", ColumnPosArgument.columnPos()).executes((commandContext) -> {
         return changeForceLoad((CommandSourceStack)commandContext.getSource(), ColumnPosArgument.getColumnPos(commandContext, "from"), ColumnPosArgument.getColumnPos(commandContext, "from"), false);
      })).then(Commands.argument("to", ColumnPosArgument.columnPos()).executes((commandContext) -> {
         return changeForceLoad((CommandSourceStack)commandContext.getSource(), ColumnPosArgument.getColumnPos(commandContext, "from"), ColumnPosArgument.getColumnPos(commandContext, "to"), false);
      })))).then(Commands.literal("all").executes((commandContext) -> {
         return removeAll((CommandSourceStack)commandContext.getSource());
      })))).then(((LiteralArgumentBuilder)Commands.literal("query").executes((commandContext) -> {
         return listForceLoad((CommandSourceStack)commandContext.getSource());
      })).then(Commands.argument("pos", ColumnPosArgument.columnPos()).executes((commandContext) -> {
         return queryForceLoad((CommandSourceStack)commandContext.getSource(), ColumnPosArgument.getColumnPos(commandContext, "pos"));
      }))));
   }

   private static int queryForceLoad(CommandSourceStack commandSourceStack, ColumnPos columnPos) throws CommandSyntaxException {
      ChunkPos var2 = new ChunkPos(columnPos.x >> 4, columnPos.z >> 4);
      DimensionType var3 = commandSourceStack.getLevel().getDimension().getType();
      boolean var4 = commandSourceStack.getServer().getLevel(var3).getForcedChunks().contains(var2.toLong());
      if(var4) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.forceload.query.success", new Object[]{var2, var3}), false);
         return 1;
      } else {
         throw ERROR_NOT_TICKING.create(var2, var3);
      }
   }

   private static int listForceLoad(CommandSourceStack commandSourceStack) {
      DimensionType var1 = commandSourceStack.getLevel().getDimension().getType();
      LongSet var2 = commandSourceStack.getServer().getLevel(var1).getForcedChunks();
      int var3 = var2.size();
      if(var3 > 0) {
         String var4 = Joiner.on(", ").join(var2.stream().sorted().map(ChunkPos::<init>).map(ChunkPos::toString).iterator());
         if(var3 == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.forceload.list.single", new Object[]{var1, var4}), false);
         } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.forceload.list.multiple", new Object[]{Integer.valueOf(var3), var1, var4}), false);
         }
      } else {
         commandSourceStack.sendFailure(new TranslatableComponent("commands.forceload.added.none", new Object[]{var1}));
      }

      return var3;
   }

   private static int removeAll(CommandSourceStack commandSourceStack) {
      DimensionType var1 = commandSourceStack.getLevel().getDimension().getType();
      ServerLevel var2 = commandSourceStack.getServer().getLevel(var1);
      LongSet var3 = var2.getForcedChunks();
      var3.forEach((var1) -> {
         var2.setChunkForced(ChunkPos.getX(var1), ChunkPos.getZ(var1), false);
      });
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.forceload.removed.all", new Object[]{var1}), true);
      return 0;
   }

   private static int changeForceLoad(CommandSourceStack commandSourceStack, ColumnPos var1, ColumnPos var2, boolean var3) throws CommandSyntaxException {
      int var4 = Math.min(var1.x, var2.x);
      int var5 = Math.min(var1.z, var2.z);
      int var6 = Math.max(var1.x, var2.x);
      int var7 = Math.max(var1.z, var2.z);
      if(var4 >= -30000000 && var5 >= -30000000 && var6 < 30000000 && var7 < 30000000) {
         int var8 = var4 >> 4;
         int var9 = var5 >> 4;
         int var10 = var6 >> 4;
         int var11 = var7 >> 4;
         long var12 = ((long)(var10 - var8) + 1L) * ((long)(var11 - var9) + 1L);
         if(var12 > 256L) {
            throw ERROR_TOO_MANY_CHUNKS.create(Integer.valueOf(256), Long.valueOf(var12));
         } else {
            DimensionType var14 = commandSourceStack.getLevel().getDimension().getType();
            ServerLevel var15 = commandSourceStack.getServer().getLevel(var14);
            ChunkPos var16 = null;
            int var17 = 0;

            for(int var18 = var8; var18 <= var10; ++var18) {
               for(int var19 = var9; var19 <= var11; ++var19) {
                  boolean var20 = var15.setChunkForced(var18, var19, var3);
                  if(var20) {
                     ++var17;
                     if(var16 == null) {
                        var16 = new ChunkPos(var18, var19);
                     }
                  }
               }
            }

            if(var17 == 0) {
               throw (var3?ERROR_ALL_ADDED:ERROR_NONE_REMOVED).create();
            } else {
               if(var17 == 1) {
                  commandSourceStack.sendSuccess(new TranslatableComponent("commands.forceload." + (var3?"added":"removed") + ".single", new Object[]{var16, var14}), true);
               } else {
                  ChunkPos var18 = new ChunkPos(var8, var9);
                  ChunkPos var19 = new ChunkPos(var10, var11);
                  commandSourceStack.sendSuccess(new TranslatableComponent("commands.forceload." + (var3?"added":"removed") + ".multiple", new Object[]{Integer.valueOf(var17), var14, var18, var19}), true);
               }

               return var17;
            }
         }
      } else {
         throw BlockPosArgument.ERROR_OUT_OF_WORLD.create();
      }
   }
}
