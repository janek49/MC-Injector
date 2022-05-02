package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class MessageArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"Hello world!", "foo", "@e", "Hello @p :)"});

   public static MessageArgument message() {
      return new MessageArgument();
   }

   public static Component getMessage(CommandContext commandContext, String string) throws CommandSyntaxException {
      return ((MessageArgument.Message)commandContext.getArgument(string, MessageArgument.Message.class)).toComponent((CommandSourceStack)commandContext.getSource(), ((CommandSourceStack)commandContext.getSource()).hasPermission(2));
   }

   public MessageArgument.Message parse(StringReader stringReader) throws CommandSyntaxException {
      return MessageArgument.Message.parseText(stringReader, true);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public static class Message {
      private final String text;
      private final MessageArgument.Part[] parts;

      public Message(String text, MessageArgument.Part[] parts) {
         this.text = text;
         this.parts = parts;
      }

      public Component toComponent(CommandSourceStack commandSourceStack, boolean var2) throws CommandSyntaxException {
         if(this.parts.length != 0 && var2) {
            Component component = new TextComponent(this.text.substring(0, this.parts[0].getStart()));
            int var4 = this.parts[0].getStart();

            for(MessageArgument.Part var8 : this.parts) {
               Component var9 = var8.toComponent(commandSourceStack);
               if(var4 < var8.getStart()) {
                  component.append(this.text.substring(var4, var8.getStart()));
               }

               if(var9 != null) {
                  component.append(var9);
               }

               var4 = var8.getEnd();
            }

            if(var4 < this.text.length()) {
               component.append(this.text.substring(var4, this.text.length()));
            }

            return component;
         } else {
            return new TextComponent(this.text);
         }
      }

      public static MessageArgument.Message parseText(StringReader stringReader, boolean var1) throws CommandSyntaxException {
         String var2 = stringReader.getString().substring(stringReader.getCursor(), stringReader.getTotalLength());
         if(!var1) {
            stringReader.setCursor(stringReader.getTotalLength());
            return new MessageArgument.Message(var2, new MessageArgument.Part[0]);
         } else {
            List<MessageArgument.Part> var3 = Lists.newArrayList();
            int var4 = stringReader.getCursor();

            while(true) {
               int var5;
               EntitySelector var6;
               while(true) {
                  if(!stringReader.canRead()) {
                     return new MessageArgument.Message(var2, (MessageArgument.Part[])var3.toArray(new MessageArgument.Part[var3.size()]));
                  }

                  if(stringReader.peek() == 64) {
                     var5 = stringReader.getCursor();

                     try {
                        EntitySelectorParser var7 = new EntitySelectorParser(stringReader);
                        var6 = var7.parse();
                        break;
                     } catch (CommandSyntaxException var8) {
                        if(var8.getType() != EntitySelectorParser.ERROR_MISSING_SELECTOR_TYPE && var8.getType() != EntitySelectorParser.ERROR_UNKNOWN_SELECTOR_TYPE) {
                           throw var8;
                        }

                        stringReader.setCursor(var5 + 1);
                     }
                  } else {
                     stringReader.skip();
                  }
               }

               var3.add(new MessageArgument.Part(var5 - var4, stringReader.getCursor() - var4, var6));
            }
         }
      }
   }

   public static class Part {
      private final int start;
      private final int end;
      private final EntitySelector selector;

      public Part(int start, int end, EntitySelector selector) {
         this.start = start;
         this.end = end;
         this.selector = selector;
      }

      public int getStart() {
         return this.start;
      }

      public int getEnd() {
         return this.end;
      }

      @Nullable
      public Component toComponent(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
         return EntitySelector.joinNames(this.selector.findEntities(commandSourceStack));
      }
   }
}
