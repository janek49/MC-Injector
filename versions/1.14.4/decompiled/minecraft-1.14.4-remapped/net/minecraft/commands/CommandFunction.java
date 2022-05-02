package net.minecraft.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;

public class CommandFunction {
   private final CommandFunction.Entry[] entries;
   private final ResourceLocation id;

   public CommandFunction(ResourceLocation id, CommandFunction.Entry[] entries) {
      this.id = id;
      this.entries = entries;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public CommandFunction.Entry[] getEntries() {
      return this.entries;
   }

   public static CommandFunction fromLines(ResourceLocation resourceLocation, ServerFunctionManager serverFunctionManager, List list) {
      List<CommandFunction.Entry> list = Lists.newArrayListWithCapacity(list.size());

      for(int var4 = 0; var4 < list.size(); ++var4) {
         int var5 = var4 + 1;
         String var6 = ((String)list.get(var4)).trim();
         StringReader var7 = new StringReader(var6);
         if(var7.canRead() && var7.peek() != 35) {
            if(var7.peek() == 47) {
               var7.skip();
               if(var7.peek() == 47) {
                  throw new IllegalArgumentException("Unknown or invalid command \'" + var6 + "\' on line " + var5 + " (if you intended to make a comment, use \'#\' not \'//\')");
               }

               String var8 = var7.readUnquotedString();
               throw new IllegalArgumentException("Unknown or invalid command \'" + var6 + "\' on line " + var5 + " (did you mean \'" + var8 + "\'? Do not use a preceding forwards slash.)");
            }

            try {
               ParseResults<CommandSourceStack> var8 = serverFunctionManager.getServer().getCommands().getDispatcher().parse(var7, serverFunctionManager.getCompilationContext());
               if(var8.getReader().canRead()) {
                  if(var8.getExceptions().size() == 1) {
                     throw (CommandSyntaxException)var8.getExceptions().values().iterator().next();
                  }

                  if(var8.getContext().getRange().isEmpty()) {
                     throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(var8.getReader());
                  }

                  throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(var8.getReader());
               }

               list.add(new CommandFunction.CommandEntry(var8));
            } catch (CommandSyntaxException var9) {
               throw new IllegalArgumentException("Whilst parsing command on line " + var5 + ": " + var9.getMessage());
            }
         }
      }

      return new CommandFunction(resourceLocation, (CommandFunction.Entry[])list.toArray(new CommandFunction.Entry[0]));
   }

   public static class CacheableFunction {
      public static final CommandFunction.CacheableFunction NONE = new CommandFunction.CacheableFunction((ResourceLocation)null);
      @Nullable
      private final ResourceLocation id;
      private boolean resolved;
      private Optional function = Optional.empty();

      public CacheableFunction(@Nullable ResourceLocation id) {
         this.id = id;
      }

      public CacheableFunction(CommandFunction commandFunction) {
         this.resolved = true;
         this.id = null;
         this.function = Optional.of(commandFunction);
      }

      public Optional get(ServerFunctionManager serverFunctionManager) {
         if(!this.resolved) {
            if(this.id != null) {
               this.function = serverFunctionManager.get(this.id);
            }

            this.resolved = true;
         }

         return this.function;
      }

      @Nullable
      public ResourceLocation getId() {
         return (ResourceLocation)this.function.map((commandFunction) -> {
            return commandFunction.id;
         }).orElse(this.id);
      }
   }

   public static class CommandEntry implements CommandFunction.Entry {
      private final ParseResults parse;

      public CommandEntry(ParseResults parse) {
         this.parse = parse;
      }

      public void execute(ServerFunctionManager serverFunctionManager, CommandSourceStack commandSourceStack, ArrayDeque arrayDeque, int var4) throws CommandSyntaxException {
         serverFunctionManager.getDispatcher().execute(new ParseResults(this.parse.getContext().withSource(commandSourceStack), this.parse.getReader(), this.parse.getExceptions()));
      }

      public String toString() {
         return this.parse.getReader().getString();
      }
   }

   public interface Entry {
      void execute(ServerFunctionManager var1, CommandSourceStack var2, ArrayDeque var3, int var4) throws CommandSyntaxException;
   }

   public static class FunctionEntry implements CommandFunction.Entry {
      private final CommandFunction.CacheableFunction function;

      public FunctionEntry(CommandFunction commandFunction) {
         this.function = new CommandFunction.CacheableFunction(commandFunction);
      }

      public void execute(ServerFunctionManager serverFunctionManager, CommandSourceStack commandSourceStack, ArrayDeque arrayDeque, int var4) {
         this.function.get(serverFunctionManager).ifPresent((commandFunction) -> {
            CommandFunction.Entry[] vars5 = commandFunction.getEntries();
            int var6 = var4 - arrayDeque.size();
            int var7 = Math.min(vars5.length, var6);

            for(int var8 = var7 - 1; var8 >= 0; --var8) {
               arrayDeque.addFirst(new ServerFunctionManager.QueuedCommand(serverFunctionManager, commandSourceStack, vars5[var8]));
            }

         });
      }

      public String toString() {
         return "function " + this.function.getId();
      }
   }
}
