package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.TranslatableComponent;

public class WorldCoordinate {
   public static final SimpleCommandExceptionType ERROR_EXPECTED_DOUBLE = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos.missing.double", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_EXPECTED_INT = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos.missing.int", new Object[0]));
   private final boolean relative;
   private final double value;

   public WorldCoordinate(boolean relative, double value) {
      this.relative = relative;
      this.value = value;
   }

   public double get(double d) {
      return this.relative?this.value + d:this.value;
   }

   public static WorldCoordinate parseDouble(StringReader stringReader, boolean var1) throws CommandSyntaxException {
      if(stringReader.canRead() && stringReader.peek() == 94) {
         throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext(stringReader);
      } else if(!stringReader.canRead()) {
         throw ERROR_EXPECTED_DOUBLE.createWithContext(stringReader);
      } else {
         boolean var2 = isRelative(stringReader);
         int var3 = stringReader.getCursor();
         double var4 = stringReader.canRead() && stringReader.peek() != 32?stringReader.readDouble():0.0D;
         String var6 = stringReader.getString().substring(var3, stringReader.getCursor());
         if(var2 && var6.isEmpty()) {
            return new WorldCoordinate(true, 0.0D);
         } else {
            if(!var6.contains(".") && !var2 && var1) {
               var4 += 0.5D;
            }

            return new WorldCoordinate(var2, var4);
         }
      }
   }

   public static WorldCoordinate parseInt(StringReader stringReader) throws CommandSyntaxException {
      if(stringReader.canRead() && stringReader.peek() == 94) {
         throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext(stringReader);
      } else if(!stringReader.canRead()) {
         throw ERROR_EXPECTED_INT.createWithContext(stringReader);
      } else {
         boolean var1 = isRelative(stringReader);
         double var2;
         if(stringReader.canRead() && stringReader.peek() != 32) {
            var2 = var1?stringReader.readDouble():(double)stringReader.readInt();
         } else {
            var2 = 0.0D;
         }

         return new WorldCoordinate(var1, var2);
      }
   }

   private static boolean isRelative(StringReader stringReader) {
      boolean var1;
      if(stringReader.peek() == 126) {
         var1 = true;
         stringReader.skip();
      } else {
         var1 = false;
      }

      return var1;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof WorldCoordinate)) {
         return false;
      } else {
         WorldCoordinate var2 = (WorldCoordinate)object;
         return this.relative != var2.relative?false:Double.compare(var2.value, this.value) == 0;
      }
   }

   public int hashCode() {
      int var1 = this.relative?1:0;
      long var2 = Double.doubleToLongBits(this.value);
      var1 = 31 * var1 + (int)(var2 ^ var2 >>> 32);
      return var1;
   }

   public boolean isRelative() {
      return this.relative;
   }
}
