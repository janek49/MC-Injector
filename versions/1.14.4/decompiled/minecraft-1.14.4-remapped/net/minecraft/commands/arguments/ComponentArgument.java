package net.minecraft.commands.arguments;

import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class ComponentArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]"});
   public static final DynamicCommandExceptionType ERROR_INVALID_JSON = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("argument.component.invalid", new Object[]{object});
   });

   public static Component getComponent(CommandContext commandContext, String string) {
      return (Component)commandContext.getArgument(string, Component.class);
   }

   public static ComponentArgument textComponent() {
      return new ComponentArgument();
   }

   public Component parse(StringReader stringReader) throws CommandSyntaxException {
      try {
         Component component = Component.Serializer.fromJson(stringReader);
         if(component == null) {
            throw ERROR_INVALID_JSON.createWithContext(stringReader, "empty");
         } else {
            return component;
         }
      } catch (JsonParseException var4) {
         String var3 = var4.getCause() != null?var4.getCause().getMessage():var4.getMessage();
         throw ERROR_INVALID_JSON.createWithContext(stringReader, var3);
      }
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
