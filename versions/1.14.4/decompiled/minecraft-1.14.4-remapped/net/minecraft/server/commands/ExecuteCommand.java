package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.SingleRedirectModifier;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType.Function;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionTypeArgument;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.RangeArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.SwizzleArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.commands.BossBarCommands;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class ExecuteCommand {
   private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("commands.execute.blocks.toobig", new Object[]{var0, var1});
   });
   private static final SimpleCommandExceptionType ERROR_CONDITIONAL_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.execute.conditional.fail", new Object[0]));
   private static final DynamicCommandExceptionType ERROR_CONDITIONAL_FAILED_COUNT = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.execute.conditional.fail_count", new Object[]{object});
   });
   private static final BinaryOperator CALLBACK_CHAINER = (var0, var1) -> {
      return (commandContext, var3, var4) -> {
         var0.onCommandComplete(commandContext, var3, var4);
         var1.onCommandComplete(commandContext, var3, var4);
      };
   };

   public static void register(CommandDispatcher commandDispatcher) {
      LiteralCommandNode<CommandSourceStack> var1 = commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("execute").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      }));
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("execute").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.literal("run").redirect(commandDispatcher.getRoot()))).then(addConditionals(var1, Commands.literal("if"), true))).then(addConditionals(var1, Commands.literal("unless"), false))).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(var1, (commandContext) -> {
         List<CommandSourceStack> var1 = Lists.newArrayList();

         for(Entity var3 : EntityArgument.getOptionalEntities(commandContext, "targets")) {
            var1.add(((CommandSourceStack)commandContext.getSource()).withEntity(var3));
         }

         return var1;
      })))).then(Commands.literal("at").then(Commands.argument("targets", EntityArgument.entities()).fork(var1, (commandContext) -> {
         List<CommandSourceStack> var1 = Lists.newArrayList();

         for(Entity var3 : EntityArgument.getOptionalEntities(commandContext, "targets")) {
            var1.add(((CommandSourceStack)commandContext.getSource()).withLevel((ServerLevel)var3.level).withPosition(var3.getCommandSenderWorldPosition()).withRotation(var3.getRotationVector()));
         }

         return var1;
      })))).then(((LiteralArgumentBuilder)Commands.literal("store").then(wrapStores(var1, Commands.literal("result"), true))).then(wrapStores(var1, Commands.literal("success"), false)))).then(((LiteralArgumentBuilder)Commands.literal("positioned").then(Commands.argument("pos", Vec3Argument.vec3()).redirect(var1, (commandContext) -> {
         return ((CommandSourceStack)commandContext.getSource()).withPosition(Vec3Argument.getVec3(commandContext, "pos"));
      }))).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(var1, (commandContext) -> {
         List<CommandSourceStack> var1 = Lists.newArrayList();

         for(Entity var3 : EntityArgument.getOptionalEntities(commandContext, "targets")) {
            var1.add(((CommandSourceStack)commandContext.getSource()).withPosition(var3.getCommandSenderWorldPosition()));
         }

         return var1;
      }))))).then(((LiteralArgumentBuilder)Commands.literal("rotated").then(Commands.argument("rot", RotationArgument.rotation()).redirect(var1, (commandContext) -> {
         return ((CommandSourceStack)commandContext.getSource()).withRotation(RotationArgument.getRotation(commandContext, "rot").getRotation((CommandSourceStack)commandContext.getSource()));
      }))).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(var1, (commandContext) -> {
         List<CommandSourceStack> var1 = Lists.newArrayList();

         for(Entity var3 : EntityArgument.getOptionalEntities(commandContext, "targets")) {
            var1.add(((CommandSourceStack)commandContext.getSource()).withRotation(var3.getRotationVector()));
         }

         return var1;
      }))))).then(((LiteralArgumentBuilder)Commands.literal("facing").then(Commands.literal("entity").then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("anchor", EntityAnchorArgument.anchor()).fork(var1, (commandContext) -> {
         List<CommandSourceStack> var1 = Lists.newArrayList();
         EntityAnchorArgument.Anchor var2 = EntityAnchorArgument.getAnchor(commandContext, "anchor");

         for(Entity var4 : EntityArgument.getOptionalEntities(commandContext, "targets")) {
            var1.add(((CommandSourceStack)commandContext.getSource()).facing(var4, var2));
         }

         return var1;
      }))))).then(Commands.argument("pos", Vec3Argument.vec3()).redirect(var1, (commandContext) -> {
         return ((CommandSourceStack)commandContext.getSource()).facing(Vec3Argument.getVec3(commandContext, "pos"));
      })))).then(Commands.literal("align").then(Commands.argument("axes", SwizzleArgument.swizzle()).redirect(var1, (commandContext) -> {
         return ((CommandSourceStack)commandContext.getSource()).withPosition(((CommandSourceStack)commandContext.getSource()).getPosition().align(SwizzleArgument.getSwizzle(commandContext, "axes")));
      })))).then(Commands.literal("anchored").then(Commands.argument("anchor", EntityAnchorArgument.anchor()).redirect(var1, (commandContext) -> {
         return ((CommandSourceStack)commandContext.getSource()).withAnchor(EntityAnchorArgument.getAnchor(commandContext, "anchor"));
      })))).then(Commands.literal("in").then(Commands.argument("dimension", DimensionTypeArgument.dimension()).redirect(var1, (commandContext) -> {
         return ((CommandSourceStack)commandContext.getSource()).withLevel(((CommandSourceStack)commandContext.getSource()).getServer().getLevel(DimensionTypeArgument.getDimension(commandContext, "dimension")));
      }))));
   }

   private static ArgumentBuilder wrapStores(LiteralCommandNode literalCommandNode, LiteralArgumentBuilder literalArgumentBuilder, boolean var2) {
      literalArgumentBuilder.then(Commands.literal("score").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).redirect(literalCommandNode, (commandContext) -> {
         return storeValue((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(commandContext, "targets"), ObjectiveArgument.getObjective(commandContext, "objective"), var2);
      }))));
      literalArgumentBuilder.then(Commands.literal("bossbar").then(((RequiredArgumentBuilder)Commands.argument("id", ResourceLocationArgument.id()).suggests(BossBarCommands.SUGGEST_BOSS_BAR).then(Commands.literal("value").redirect(literalCommandNode, (commandContext) -> {
         return storeValue((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar(commandContext), true, var2);
      }))).then(Commands.literal("max").redirect(literalCommandNode, (commandContext) -> {
         return storeValue((CommandSourceStack)commandContext.getSource(), BossBarCommands.getBossBar(commandContext), false, var2);
      }))));

      for(DataCommands.DataProvider var4 : DataCommands.TARGET_PROVIDERS) {
         var4.wrap(literalArgumentBuilder, (var3) -> {
            return var3.then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("path", NbtPathArgument.nbtPath()).then(Commands.literal("int").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(literalCommandNode, (commandContext) -> {
               return storeData((CommandSourceStack)commandContext.getSource(), var4.access(commandContext), NbtPathArgument.getPath(commandContext, "path"), (var1) -> {
                  return new IntTag((int)((double)var1 * DoubleArgumentType.getDouble(commandContext, "scale")));
               }, var2);
            })))).then(Commands.literal("float").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(literalCommandNode, (commandContext) -> {
               return storeData((CommandSourceStack)commandContext.getSource(), var4.access(commandContext), NbtPathArgument.getPath(commandContext, "path"), (var1) -> {
                  return new FloatTag((float)((double)var1 * DoubleArgumentType.getDouble(commandContext, "scale")));
               }, var2);
            })))).then(Commands.literal("short").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(literalCommandNode, (commandContext) -> {
               return storeData((CommandSourceStack)commandContext.getSource(), var4.access(commandContext), NbtPathArgument.getPath(commandContext, "path"), (var1) -> {
                  return new ShortTag((short)((int)((double)var1 * DoubleArgumentType.getDouble(commandContext, "scale"))));
               }, var2);
            })))).then(Commands.literal("long").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(literalCommandNode, (commandContext) -> {
               return storeData((CommandSourceStack)commandContext.getSource(), var4.access(commandContext), NbtPathArgument.getPath(commandContext, "path"), (var1) -> {
                  return new LongTag((long)((double)var1 * DoubleArgumentType.getDouble(commandContext, "scale")));
               }, var2);
            })))).then(Commands.literal("double").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(literalCommandNode, (commandContext) -> {
               return storeData((CommandSourceStack)commandContext.getSource(), var4.access(commandContext), NbtPathArgument.getPath(commandContext, "path"), (var1) -> {
                  return new DoubleTag((double)var1 * DoubleArgumentType.getDouble(commandContext, "scale"));
               }, var2);
            })))).then(Commands.literal("byte").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(literalCommandNode, (commandContext) -> {
               return storeData((CommandSourceStack)commandContext.getSource(), var4.access(commandContext), NbtPathArgument.getPath(commandContext, "path"), (var1) -> {
                  return new ByteTag((byte)((int)((double)var1 * DoubleArgumentType.getDouble(commandContext, "scale"))));
               }, var2);
            }))));
         });
      }

      return literalArgumentBuilder;
   }

   private static CommandSourceStack storeValue(CommandSourceStack var0, Collection collection, Objective objective, boolean var3) {
      Scoreboard var4 = var0.getServer().getScoreboard();
      return var0.withCallback((commandContext, var5, var6) -> {
         for(String var8 : collection) {
            Score var9 = var4.getOrCreatePlayerScore(var8, objective);
            int var10 = var3?var6:(var5?1:0);
            var9.setScore(var10);
         }

      }, CALLBACK_CHAINER);
   }

   private static CommandSourceStack storeValue(CommandSourceStack var0, CustomBossEvent customBossEvent, boolean var2, boolean var3) {
      return var0.withCallback((commandContext, var4, var5) -> {
         int var6 = var3?var5:(var4?1:0);
         if(var2) {
            customBossEvent.setValue(var6);
         } else {
            customBossEvent.setMax(var6);
         }

      }, CALLBACK_CHAINER);
   }

   private static CommandSourceStack storeData(CommandSourceStack var0, DataAccessor dataAccessor, NbtPathArgument.NbtPath nbtPathArgument$NbtPath, IntFunction intFunction, boolean var4) {
      return var0.withCallback((commandContext, var5, var6) -> {
         try {
            CompoundTag var7 = dataAccessor.getData();
            int var8 = var4?var6:(var5?1:0);
            nbtPathArgument$NbtPath.set(var7, () -> {
               return (Tag)intFunction.apply(var8);
            });
            dataAccessor.setData(var7);
         } catch (CommandSyntaxException var9) {
            ;
         }

      }, CALLBACK_CHAINER);
   }

   private static ArgumentBuilder addConditionals(CommandNode commandNode, LiteralArgumentBuilder literalArgumentBuilder, boolean var2) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder.then(Commands.literal("block").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(addConditional(commandNode, Commands.argument("block", BlockPredicateArgument.blockPredicate()), var2, (commandContext) -> {
         return BlockPredicateArgument.getBlockPredicate(commandContext, "block").test(new BlockInWorld(((CommandSourceStack)commandContext.getSource()).getLevel(), BlockPosArgument.getLoadedBlockPos(commandContext, "pos"), true));
      }))))).then(Commands.literal("score").then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targetObjective", ObjectiveArgument.objective()).then(Commands.literal("=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(commandNode, Commands.argument("sourceObjective", ObjectiveArgument.objective()), var2, (commandContext) -> {
         return checkScore(commandContext, Integer::equals);
      }))))).then(Commands.literal("<").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(commandNode, Commands.argument("sourceObjective", ObjectiveArgument.objective()), var2, (commandContext) -> {
         return checkScore(commandContext, (var0, var1) -> {
            return var0.intValue() < var1.intValue();
         });
      }))))).then(Commands.literal("<=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(commandNode, Commands.argument("sourceObjective", ObjectiveArgument.objective()), var2, (commandContext) -> {
         return checkScore(commandContext, (var0, var1) -> {
            return var0.intValue() <= var1.intValue();
         });
      }))))).then(Commands.literal(">").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(commandNode, Commands.argument("sourceObjective", ObjectiveArgument.objective()), var2, (commandContext) -> {
         return checkScore(commandContext, (var0, var1) -> {
            return var0.intValue() > var1.intValue();
         });
      }))))).then(Commands.literal(">=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(addConditional(commandNode, Commands.argument("sourceObjective", ObjectiveArgument.objective()), var2, (commandContext) -> {
         return checkScore(commandContext, (var0, var1) -> {
            return var0.intValue() >= var1.intValue();
         });
      }))))).then(Commands.literal("matches").then(addConditional(commandNode, Commands.argument("range", RangeArgument.intRange()), var2, (commandContext) -> {
         return checkScore(commandContext, RangeArgument.Ints.getRange(commandContext, "range"));
      }))))))).then(Commands.literal("blocks").then(Commands.argument("start", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)Commands.argument("destination", BlockPosArgument.blockPos()).then(addIfBlocksConditional(commandNode, Commands.literal("all"), var2, false))).then(addIfBlocksConditional(commandNode, Commands.literal("masked"), var2, true))))))).then(Commands.literal("entity").then(((RequiredArgumentBuilder)Commands.argument("entities", EntityArgument.entities()).fork(commandNode, (commandContext) -> {
         return expect(commandContext, var2, !EntityArgument.getOptionalEntities(commandContext, "entities").isEmpty());
      })).executes(createNumericConditionalHandler(var2, (commandContext) -> {
         return EntityArgument.getOptionalEntities(commandContext, "entities").size();
      }))));

      for(DataCommands.DataProvider var4 : DataCommands.SOURCE_PROVIDERS) {
         literalArgumentBuilder.then(var4.wrap(Commands.literal("data"), (var3) -> {
            return var3.then(((RequiredArgumentBuilder)Commands.argument("path", NbtPathArgument.nbtPath()).fork(commandNode, (commandContext) -> {
               return expect(commandContext, var2, checkMatchingData(var4.access(commandContext), NbtPathArgument.getPath(commandContext, "path")) > 0);
            })).executes(createNumericConditionalHandler(var2, (commandContext) -> {
               return checkMatchingData(var4.access(commandContext), NbtPathArgument.getPath(commandContext, "path"));
            })));
         }));
      }

      return literalArgumentBuilder;
   }

   private static Command createNumericConditionalHandler(boolean var0, ExecuteCommand.CommandNumericPredicate executeCommand$CommandNumericPredicate) {
      return var0?(commandContext) -> {
         int var2 = executeCommand$CommandNumericPredicate.test(commandContext);
         if(var2 > 0) {
            ((CommandSourceStack)commandContext.getSource()).sendSuccess(new TranslatableComponent("commands.execute.conditional.pass_count", new Object[]{Integer.valueOf(var2)}), false);
            return var2;
         } else {
            throw ERROR_CONDITIONAL_FAILED.create();
         }
      }:(commandContext) -> {
         int var2 = executeCommand$CommandNumericPredicate.test(commandContext);
         if(var2 == 0) {
            ((CommandSourceStack)commandContext.getSource()).sendSuccess(new TranslatableComponent("commands.execute.conditional.pass", new Object[0]), false);
            return 1;
         } else {
            throw ERROR_CONDITIONAL_FAILED_COUNT.create(Integer.valueOf(var2));
         }
      };
   }

   private static int checkMatchingData(DataAccessor dataAccessor, NbtPathArgument.NbtPath nbtPathArgument$NbtPath) throws CommandSyntaxException {
      return nbtPathArgument$NbtPath.countMatching(dataAccessor.getData());
   }

   private static boolean checkScore(CommandContext commandContext, BiPredicate biPredicate) throws CommandSyntaxException {
      String var2 = ScoreHolderArgument.getName(commandContext, "target");
      Objective var3 = ObjectiveArgument.getObjective(commandContext, "targetObjective");
      String var4 = ScoreHolderArgument.getName(commandContext, "source");
      Objective var5 = ObjectiveArgument.getObjective(commandContext, "sourceObjective");
      Scoreboard var6 = ((CommandSourceStack)commandContext.getSource()).getServer().getScoreboard();
      if(var6.hasPlayerScore(var2, var3) && var6.hasPlayerScore(var4, var5)) {
         Score var7 = var6.getOrCreatePlayerScore(var2, var3);
         Score var8 = var6.getOrCreatePlayerScore(var4, var5);
         return biPredicate.test(Integer.valueOf(var7.getScore()), Integer.valueOf(var8.getScore()));
      } else {
         return false;
      }
   }

   private static boolean checkScore(CommandContext commandContext, MinMaxBounds.Ints minMaxBounds$Ints) throws CommandSyntaxException {
      String var2 = ScoreHolderArgument.getName(commandContext, "target");
      Objective var3 = ObjectiveArgument.getObjective(commandContext, "targetObjective");
      Scoreboard var4 = ((CommandSourceStack)commandContext.getSource()).getServer().getScoreboard();
      return !var4.hasPlayerScore(var2, var3)?false:minMaxBounds$Ints.matches(var4.getOrCreatePlayerScore(var2, var3).getScore());
   }

   private static Collection expect(CommandContext commandContext, boolean var1, boolean var2) {
      return (Collection)(var2 == var1?Collections.singleton(commandContext.getSource()):Collections.emptyList());
   }

   private static ArgumentBuilder addConditional(CommandNode commandNode, ArgumentBuilder var1, boolean var2, ExecuteCommand.CommandPredicate executeCommand$CommandPredicate) {
      return var1.fork(commandNode, (commandContext) -> {
         return expect(commandContext, var2, executeCommand$CommandPredicate.test(commandContext));
      }).executes((commandContext) -> {
         if(var2 == executeCommand$CommandPredicate.test(commandContext)) {
            ((CommandSourceStack)commandContext.getSource()).sendSuccess(new TranslatableComponent("commands.execute.conditional.pass", new Object[0]), false);
            return 1;
         } else {
            throw ERROR_CONDITIONAL_FAILED.create();
         }
      });
   }

   private static ArgumentBuilder addIfBlocksConditional(CommandNode commandNode, ArgumentBuilder var1, boolean var2, boolean var3) {
      return var1.fork(commandNode, (commandContext) -> {
         return expect(commandContext, var2, checkRegions(commandContext, var3).isPresent());
      }).executes(var2?(commandContext) -> {
         return checkIfRegions(commandContext, var3);
      }:(commandContext) -> {
         return checkUnlessRegions(commandContext, var3);
      });
   }

   private static int checkIfRegions(CommandContext commandContext, boolean var1) throws CommandSyntaxException {
      OptionalInt var2 = checkRegions(commandContext, var1);
      if(var2.isPresent()) {
         ((CommandSourceStack)commandContext.getSource()).sendSuccess(new TranslatableComponent("commands.execute.conditional.pass_count", new Object[]{Integer.valueOf(var2.getAsInt())}), false);
         return var2.getAsInt();
      } else {
         throw ERROR_CONDITIONAL_FAILED.create();
      }
   }

   private static int checkUnlessRegions(CommandContext commandContext, boolean var1) throws CommandSyntaxException {
      OptionalInt var2 = checkRegions(commandContext, var1);
      if(var2.isPresent()) {
         throw ERROR_CONDITIONAL_FAILED_COUNT.create(Integer.valueOf(var2.getAsInt()));
      } else {
         ((CommandSourceStack)commandContext.getSource()).sendSuccess(new TranslatableComponent("commands.execute.conditional.pass", new Object[0]), false);
         return 1;
      }
   }

   private static OptionalInt checkRegions(CommandContext commandContext, boolean var1) throws CommandSyntaxException {
      return checkRegions(((CommandSourceStack)commandContext.getSource()).getLevel(), BlockPosArgument.getLoadedBlockPos(commandContext, "start"), BlockPosArgument.getLoadedBlockPos(commandContext, "end"), BlockPosArgument.getLoadedBlockPos(commandContext, "destination"), var1);
   }

   private static OptionalInt checkRegions(ServerLevel serverLevel, BlockPos var1, BlockPos var2, BlockPos var3, boolean var4) throws CommandSyntaxException {
      BoundingBox var5 = new BoundingBox(var1, var2);
      BoundingBox var6 = new BoundingBox(var3, var3.offset(var5.getLength()));
      BlockPos var7 = new BlockPos(var6.x0 - var5.x0, var6.y0 - var5.y0, var6.z0 - var5.z0);
      int var8 = var5.getXSpan() * var5.getYSpan() * var5.getZSpan();
      if(var8 > '耀') {
         throw ERROR_AREA_TOO_LARGE.create(Integer.valueOf('耀'), Integer.valueOf(var8));
      } else {
         int var9 = 0;

         for(int var10 = var5.z0; var10 <= var5.z1; ++var10) {
            for(int var11 = var5.y0; var11 <= var5.y1; ++var11) {
               for(int var12 = var5.x0; var12 <= var5.x1; ++var12) {
                  BlockPos var13 = new BlockPos(var12, var11, var10);
                  BlockPos var14 = var13.offset(var7);
                  BlockState var15 = serverLevel.getBlockState(var13);
                  if(!var4 || var15.getBlock() != Blocks.AIR) {
                     if(var15 != serverLevel.getBlockState(var14)) {
                        return OptionalInt.empty();
                     }

                     BlockEntity var16 = serverLevel.getBlockEntity(var13);
                     BlockEntity var17 = serverLevel.getBlockEntity(var14);
                     if(var16 != null) {
                        if(var17 == null) {
                           return OptionalInt.empty();
                        }

                        CompoundTag var18 = var16.save(new CompoundTag());
                        var18.remove("x");
                        var18.remove("y");
                        var18.remove("z");
                        CompoundTag var19 = var17.save(new CompoundTag());
                        var19.remove("x");
                        var19.remove("y");
                        var19.remove("z");
                        if(!var18.equals(var19)) {
                           return OptionalInt.empty();
                        }
                     }

                     ++var9;
                  }
               }
            }
         }

         return OptionalInt.of(var9);
      }
   }

   @FunctionalInterface
   interface CommandNumericPredicate {
      int test(CommandContext var1) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface CommandPredicate {
      boolean test(CommandContext var1) throws CommandSyntaxException;
   }
}
