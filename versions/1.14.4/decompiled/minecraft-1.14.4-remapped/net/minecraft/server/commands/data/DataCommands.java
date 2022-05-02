package net.minecraft.server.commands.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.commands.data.BlockDataAccessor;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.EntityDataAccessor;
import net.minecraft.util.Mth;

public class DataCommands {
   private static final SimpleCommandExceptionType ERROR_MERGE_UNCHANGED = new SimpleCommandExceptionType(new TranslatableComponent("commands.data.merge.failed", new Object[0]));
   private static final DynamicCommandExceptionType ERROR_GET_NOT_NUMBER = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.data.get.invalid", new Object[]{object});
   });
   private static final DynamicCommandExceptionType ERROR_GET_NON_EXISTENT = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.data.get.unknown", new Object[]{object});
   });
   private static final SimpleCommandExceptionType ERROR_MULTIPLE_TAGS = new SimpleCommandExceptionType(new TranslatableComponent("commands.data.get.multiple", new Object[0]));
   private static final DynamicCommandExceptionType ERROR_EXPECTED_LIST = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.data.modify.expected_list", new Object[]{object});
   });
   private static final DynamicCommandExceptionType ERROR_EXPECTED_OBJECT = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.data.modify.expected_object", new Object[]{object});
   });
   private static final DynamicCommandExceptionType ERROR_INVALID_INDEX = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.data.modify.invalid_index", new Object[]{object});
   });
   public static final List ALL_PROVIDERS = ImmutableList.of(EntityDataAccessor.PROVIDER, BlockDataAccessor.PROVIDER);
   public static final List TARGET_PROVIDERS = (List)ALL_PROVIDERS.stream().map((function) -> {
      return (DataCommands.DataProvider)function.apply("target");
   }).collect(ImmutableList.toImmutableList());
   public static final List SOURCE_PROVIDERS = (List)ALL_PROVIDERS.stream().map((function) -> {
      return (DataCommands.DataProvider)function.apply("source");
   }).collect(ImmutableList.toImmutableList());

   public static void register(CommandDispatcher commandDispatcher) {
      LiteralArgumentBuilder<CommandSourceStack> var1 = (LiteralArgumentBuilder)Commands.literal("data").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      });

      for(DataCommands.DataProvider var3 : TARGET_PROVIDERS) {
         ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)var1.then(var3.wrap(Commands.literal("merge"), (var1) -> {
            return var1.then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes((commandContext) -> {
               return mergeData((CommandSourceStack)commandContext.getSource(), var3.access(commandContext), CompoundTagArgument.getCompoundTag(commandContext, "nbt"));
            }));
         }))).then(var3.wrap(Commands.literal("get"), (var1) -> {
            return var1.executes((commandContext) -> {
               return getData((CommandSourceStack)commandContext.getSource(), var3.access(commandContext));
            }).then(((RequiredArgumentBuilder)Commands.argument("path", NbtPathArgument.nbtPath()).executes((commandContext) -> {
               return getData((CommandSourceStack)commandContext.getSource(), var3.access(commandContext), NbtPathArgument.getPath(commandContext, "path"));
            })).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes((commandContext) -> {
               return getNumeric((CommandSourceStack)commandContext.getSource(), var3.access(commandContext), NbtPathArgument.getPath(commandContext, "path"), DoubleArgumentType.getDouble(commandContext, "scale"));
            })));
         }))).then(var3.wrap(Commands.literal("remove"), (var1) -> {
            return var1.then(Commands.argument("path", NbtPathArgument.nbtPath()).executes((commandContext) -> {
               return removeData((CommandSourceStack)commandContext.getSource(), var3.access(commandContext), NbtPathArgument.getPath(commandContext, "path"));
            }));
         }))).then(decorateModification((argumentBuilder, dataCommands$DataManipulatorDecorator) -> {
            argumentBuilder.then(Commands.literal("insert").then(Commands.argument("index", IntegerArgumentType.integer()).then(dataCommands$DataManipulatorDecorator.create((commandContext, compoundTag, nbtPathArgument$NbtPath, list) -> {
               int var4 = IntegerArgumentType.getInteger(commandContext, "index");
               return insertAtIndex(var4, compoundTag, nbtPathArgument$NbtPath, list);
            })))).then(Commands.literal("prepend").then(dataCommands$DataManipulatorDecorator.create((commandContext, compoundTag, nbtPathArgument$NbtPath, list) -> {
               return insertAtIndex(0, compoundTag, nbtPathArgument$NbtPath, list);
            }))).then(Commands.literal("append").then(dataCommands$DataManipulatorDecorator.create((commandContext, compoundTag, nbtPathArgument$NbtPath, list) -> {
               return insertAtIndex(-1, compoundTag, nbtPathArgument$NbtPath, list);
            }))).then(Commands.literal("set").then(dataCommands$DataManipulatorDecorator.create((commandContext, compoundTag, nbtPathArgument$NbtPath, list) -> {
               Tag var10002 = (Tag)Iterables.getLast(list);
               var10002.getClass();
               return nbtPathArgument$NbtPath.set(compoundTag, var10002::copy);
            }))).then(Commands.literal("merge").then(dataCommands$DataManipulatorDecorator.create((commandContext, compoundTag, nbtPathArgument$NbtPath, list) -> {
               Collection<Tag> var4 = nbtPathArgument$NbtPath.getOrCreate(compoundTag, CompoundTag::<init>);
               int var5 = 0;

               for(Tag var7 : var4) {
                  if(!(var7 instanceof CompoundTag)) {
                     throw ERROR_EXPECTED_OBJECT.create(var7);
                  }

                  CompoundTag var8 = (CompoundTag)var7;
                  CompoundTag var9 = var8.copy();

                  for(Tag var11 : list) {
                     if(!(var11 instanceof CompoundTag)) {
                        throw ERROR_EXPECTED_OBJECT.create(var11);
                     }

                     var8.merge((CompoundTag)var11);
                  }

                  var5 += var9.equals(var8)?0:1;
               }

               return var5;
            })));
         }));
      }

      commandDispatcher.register(var1);
   }

   private static int insertAtIndex(int var0, CompoundTag compoundTag, NbtPathArgument.NbtPath nbtPathArgument$NbtPath, List list) throws CommandSyntaxException {
      Collection<Tag> var4 = nbtPathArgument$NbtPath.getOrCreate(compoundTag, ListTag::<init>);
      int var5 = 0;

      for(Tag var7 : var4) {
         if(!(var7 instanceof CollectionTag)) {
            throw ERROR_EXPECTED_LIST.create(var7);
         }

         boolean var8 = false;
         CollectionTag<?> var9 = (CollectionTag)var7;
         int var10 = var0 < 0?var9.size() + var0 + 1:var0;

         for(Tag var12 : list) {
            try {
               if(var9.addTag(var10, var12.copy())) {
                  ++var10;
                  var8 = true;
               }
            } catch (IndexOutOfBoundsException var14) {
               throw ERROR_INVALID_INDEX.create(Integer.valueOf(var10));
            }
         }

         var5 += var8?1:0;
      }

      return var5;
   }

   private static ArgumentBuilder decorateModification(BiConsumer biConsumer) {
      LiteralArgumentBuilder<CommandSourceStack> var1 = Commands.literal("modify");

      for(DataCommands.DataProvider var3 : TARGET_PROVIDERS) {
         var3.wrap(var1, (var2) -> {
            ArgumentBuilder<CommandSourceStack, ?> var3 = Commands.argument("targetPath", NbtPathArgument.nbtPath());

            for(DataCommands.DataProvider var5 : SOURCE_PROVIDERS) {
               biConsumer.accept(var3, (dataCommands$DataManipulator) -> {
                  return var5.wrap(Commands.literal("from"), (var3x) -> {
                     return var3x.executes((commandContext) -> {
                        List<Tag> var4 = Collections.singletonList(var5.access(commandContext).getData());
                        return manipulateData(commandContext, var3, dataCommands$DataManipulator, var4);
                     }).then(Commands.argument("sourcePath", NbtPathArgument.nbtPath()).executes((commandContext) -> {
                        DataAccessor var4 = var5.access(commandContext);
                        NbtPathArgument.NbtPath var5 = NbtPathArgument.getPath(commandContext, "sourcePath");
                        List<Tag> var6 = var5.get(var4.getData());
                        return manipulateData(commandContext, var3, dataCommands$DataManipulator, var6);
                     }));
                  });
               });
            }

            biConsumer.accept(var3, (dataCommands$DataManipulator) -> {
               return (LiteralArgumentBuilder)Commands.literal("value").then(Commands.argument("value", NbtTagArgument.nbtTag()).executes((commandContext) -> {
                  List<Tag> var3 = Collections.singletonList(NbtTagArgument.getNbtTag(commandContext, "value"));
                  return manipulateData(commandContext, var3, dataCommands$DataManipulator, var3);
               }));
            });
            return var2.then(var3);
         });
      }

      return var1;
   }

   private static int manipulateData(CommandContext commandContext, DataCommands.DataProvider dataCommands$DataProvider, DataCommands.DataManipulator dataCommands$DataManipulator, List list) throws CommandSyntaxException {
      DataAccessor var4 = dataCommands$DataProvider.access(commandContext);
      NbtPathArgument.NbtPath var5 = NbtPathArgument.getPath(commandContext, "targetPath");
      CompoundTag var6 = var4.getData();
      int var7 = dataCommands$DataManipulator.modify(commandContext, var6, var5, list);
      if(var7 == 0) {
         throw ERROR_MERGE_UNCHANGED.create();
      } else {
         var4.setData(var6);
         ((CommandSourceStack)commandContext.getSource()).sendSuccess(var4.getModifiedSuccess(), true);
         return var7;
      }
   }

   private static int removeData(CommandSourceStack commandSourceStack, DataAccessor dataAccessor, NbtPathArgument.NbtPath nbtPathArgument$NbtPath) throws CommandSyntaxException {
      CompoundTag var3 = dataAccessor.getData();
      int var4 = nbtPathArgument$NbtPath.remove(var3);
      if(var4 == 0) {
         throw ERROR_MERGE_UNCHANGED.create();
      } else {
         dataAccessor.setData(var3);
         commandSourceStack.sendSuccess(dataAccessor.getModifiedSuccess(), true);
         return var4;
      }
   }

   private static Tag getSingleTag(NbtPathArgument.NbtPath nbtPathArgument$NbtPath, DataAccessor dataAccessor) throws CommandSyntaxException {
      Collection<Tag> var2 = nbtPathArgument$NbtPath.get(dataAccessor.getData());
      Iterator<Tag> var3 = var2.iterator();
      Tag var4 = (Tag)var3.next();
      if(var3.hasNext()) {
         throw ERROR_MULTIPLE_TAGS.create();
      } else {
         return var4;
      }
   }

   private static int getData(CommandSourceStack commandSourceStack, DataAccessor dataAccessor, NbtPathArgument.NbtPath nbtPathArgument$NbtPath) throws CommandSyntaxException {
      Tag var3 = getSingleTag(nbtPathArgument$NbtPath, dataAccessor);
      int var4;
      if(var3 instanceof NumericTag) {
         var4 = Mth.floor(((NumericTag)var3).getAsDouble());
      } else if(var3 instanceof CollectionTag) {
         var4 = ((CollectionTag)var3).size();
      } else if(var3 instanceof CompoundTag) {
         var4 = ((CompoundTag)var3).size();
      } else {
         if(!(var3 instanceof StringTag)) {
            throw ERROR_GET_NON_EXISTENT.create(nbtPathArgument$NbtPath.toString());
         }

         var4 = var3.getAsString().length();
      }

      commandSourceStack.sendSuccess(dataAccessor.getPrintSuccess(var3), false);
      return var4;
   }

   private static int getNumeric(CommandSourceStack commandSourceStack, DataAccessor dataAccessor, NbtPathArgument.NbtPath nbtPathArgument$NbtPath, double var3) throws CommandSyntaxException {
      Tag var5 = getSingleTag(nbtPathArgument$NbtPath, dataAccessor);
      if(!(var5 instanceof NumericTag)) {
         throw ERROR_GET_NOT_NUMBER.create(nbtPathArgument$NbtPath.toString());
      } else {
         int var6 = Mth.floor(((NumericTag)var5).getAsDouble() * var3);
         commandSourceStack.sendSuccess(dataAccessor.getPrintSuccess(nbtPathArgument$NbtPath, var3, var6), false);
         return var6;
      }
   }

   private static int getData(CommandSourceStack commandSourceStack, DataAccessor dataAccessor) throws CommandSyntaxException {
      commandSourceStack.sendSuccess(dataAccessor.getPrintSuccess(dataAccessor.getData()), false);
      return 1;
   }

   private static int mergeData(CommandSourceStack commandSourceStack, DataAccessor dataAccessor, CompoundTag compoundTag) throws CommandSyntaxException {
      CompoundTag compoundTag = dataAccessor.getData();
      CompoundTag var4 = compoundTag.copy().merge(compoundTag);
      if(compoundTag.equals(var4)) {
         throw ERROR_MERGE_UNCHANGED.create();
      } else {
         dataAccessor.setData(var4);
         commandSourceStack.sendSuccess(dataAccessor.getModifiedSuccess(), true);
         return 1;
      }
   }

   interface DataManipulator {
      int modify(CommandContext var1, CompoundTag var2, NbtPathArgument.NbtPath var3, List var4) throws CommandSyntaxException;
   }

   interface DataManipulatorDecorator {
      ArgumentBuilder create(DataCommands.DataManipulator var1);
   }

   public interface DataProvider {
      DataAccessor access(CommandContext var1) throws CommandSyntaxException;

      ArgumentBuilder wrap(ArgumentBuilder var1, Function var2);
   }
}
