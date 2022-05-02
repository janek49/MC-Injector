package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;

public class SwizzleArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"xyz", "x"});
   private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(new TranslatableComponent("arguments.swizzle.invalid", new Object[0]));

   public static SwizzleArgument swizzle() {
      return new SwizzleArgument();
   }

   public static EnumSet getSwizzle(CommandContext commandContext, String string) {
      return (EnumSet)commandContext.getArgument(string, EnumSet.class);
   }

   public EnumSet parse(StringReader stringReader) throws CommandSyntaxException {
      EnumSet<Direction.Axis> enumSet = EnumSet.noneOf(Direction.Axis.class);

      while(stringReader.canRead() && stringReader.peek() != 32) {
         char var3 = stringReader.read();
         Direction.Axis var4;
         switch(var3) {
         case 'x':
            var4 = Direction.Axis.X;
            break;
         case 'y':
            var4 = Direction.Axis.Y;
            break;
         case 'z':
            var4 = Direction.Axis.Z;
            break;
         default:
            throw ERROR_INVALID.create();
         }

         if(enumSet.contains(var4)) {
            throw ERROR_INVALID.create();
         }

         enumSet.add(var4);
      }

      return enumSet;
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
