package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

public class CompoundTagArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"{}", "{foo=bar}"});

   public static CompoundTagArgument compoundTag() {
      return new CompoundTagArgument();
   }

   public static CompoundTag getCompoundTag(CommandContext commandContext, String string) {
      return (CompoundTag)commandContext.getArgument(string, CompoundTag.class);
   }

   public CompoundTag parse(StringReader stringReader) throws CommandSyntaxException {
      return (new TagParser(stringReader)).readStruct();
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
