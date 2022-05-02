package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.network.chat.TranslatableComponent;

public class RotationArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"0 0", "~ ~", "~-5 ~5"});
   public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(new TranslatableComponent("argument.rotation.incomplete", new Object[0]));

   public static RotationArgument rotation() {
      return new RotationArgument();
   }

   public static Coordinates getRotation(CommandContext commandContext, String string) {
      return (Coordinates)commandContext.getArgument(string, Coordinates.class);
   }

   public Coordinates parse(StringReader stringReader) throws CommandSyntaxException {
      int var2 = stringReader.getCursor();
      if(!stringReader.canRead()) {
         throw ERROR_NOT_COMPLETE.createWithContext(stringReader);
      } else {
         WorldCoordinate var3 = WorldCoordinate.parseDouble(stringReader, false);
         if(stringReader.canRead() && stringReader.peek() == 32) {
            stringReader.skip();
            WorldCoordinate var4 = WorldCoordinate.parseDouble(stringReader, false);
            return new WorldCoordinates(var4, var3, new WorldCoordinate(true, 0.0D));
         } else {
            stringReader.setCursor(var2);
            throw ERROR_NOT_COMPLETE.createWithContext(stringReader);
         }
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
