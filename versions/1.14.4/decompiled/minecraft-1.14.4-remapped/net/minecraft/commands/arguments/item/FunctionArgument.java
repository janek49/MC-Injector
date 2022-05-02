package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;

public class FunctionArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"foo", "foo:bar", "#foo"});
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("arguments.function.tag.unknown", new Object[]{object});
   });
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_FUNCTION = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("arguments.function.unknown", new Object[]{object});
   });

   public static FunctionArgument functions() {
      return new FunctionArgument();
   }

   public FunctionArgument.Result parse(StringReader stringReader) throws CommandSyntaxException {
      if(stringReader.canRead() && stringReader.peek() == 35) {
         stringReader.skip();
         final ResourceLocation var2 = ResourceLocation.read(stringReader);
         return new FunctionArgument.Result() {
            public Collection create(CommandContext commandContext) throws CommandSyntaxException {
               Tag<CommandFunction> var2 = FunctionArgument.getFunctionTag(commandContext, var2);
               return var2.getValues();
            }

            public Either unwrap(CommandContext commandContext) throws CommandSyntaxException {
               return Either.right(FunctionArgument.getFunctionTag(commandContext, var2));
            }
         };
      } else {
         final ResourceLocation var2 = ResourceLocation.read(stringReader);
         return new FunctionArgument.Result() {
            public Collection create(CommandContext commandContext) throws CommandSyntaxException {
               return Collections.singleton(FunctionArgument.getFunction(commandContext, var2));
            }

            public Either unwrap(CommandContext commandContext) throws CommandSyntaxException {
               return Either.left(FunctionArgument.getFunction(commandContext, var2));
            }
         };
      }
   }

   private static CommandFunction getFunction(CommandContext commandContext, ResourceLocation resourceLocation) throws CommandSyntaxException {
      return (CommandFunction)((CommandSourceStack)commandContext.getSource()).getServer().getFunctions().get(resourceLocation).orElseThrow(() -> {
         return ERROR_UNKNOWN_FUNCTION.create(resourceLocation.toString());
      });
   }

   private static Tag getFunctionTag(CommandContext commandContext, ResourceLocation resourceLocation) throws CommandSyntaxException {
      Tag<CommandFunction> tag = ((CommandSourceStack)commandContext.getSource()).getServer().getFunctions().getTags().getTag(resourceLocation);
      if(tag == null) {
         throw ERROR_UNKNOWN_TAG.create(resourceLocation.toString());
      } else {
         return tag;
      }
   }

   public static Collection getFunctions(CommandContext commandContext, String string) throws CommandSyntaxException {
      return ((FunctionArgument.Result)commandContext.getArgument(string, FunctionArgument.Result.class)).create(commandContext);
   }

   public static Either getFunctionOrTag(CommandContext commandContext, String string) throws CommandSyntaxException {
      return ((FunctionArgument.Result)commandContext.getArgument(string, FunctionArgument.Result.class)).unwrap(commandContext);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public interface Result {
      Collection create(CommandContext var1) throws CommandSyntaxException;

      Either unwrap(CommandContext var1) throws CommandSyntaxException;
   }
}
