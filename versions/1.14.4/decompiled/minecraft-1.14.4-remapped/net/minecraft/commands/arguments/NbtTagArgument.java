package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;

public class NbtTagArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"0", "0b", "0l", "0.0", "\"foo\"", "{foo=bar}", "[0]"});

   public static NbtTagArgument nbtTag() {
      return new NbtTagArgument();
   }

   public static Tag getNbtTag(CommandContext commandContext, String string) {
      return (Tag)commandContext.getArgument(string, Tag.class);
   }

   public Tag parse(StringReader stringReader) throws CommandSyntaxException {
      return (new TagParser(stringReader)).readValue();
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
