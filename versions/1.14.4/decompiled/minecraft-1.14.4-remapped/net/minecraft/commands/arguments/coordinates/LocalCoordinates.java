package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class LocalCoordinates implements Coordinates {
   private final double left;
   private final double up;
   private final double forwards;

   public LocalCoordinates(double left, double up, double forwards) {
      this.left = left;
      this.up = up;
      this.forwards = forwards;
   }

   public Vec3 getPosition(CommandSourceStack commandSourceStack) {
      Vec2 var2 = commandSourceStack.getRotation();
      Vec3 var3 = commandSourceStack.getAnchor().apply(commandSourceStack);
      float var4 = Mth.cos((var2.y + 90.0F) * 0.017453292F);
      float var5 = Mth.sin((var2.y + 90.0F) * 0.017453292F);
      float var6 = Mth.cos(-var2.x * 0.017453292F);
      float var7 = Mth.sin(-var2.x * 0.017453292F);
      float var8 = Mth.cos((-var2.x + 90.0F) * 0.017453292F);
      float var9 = Mth.sin((-var2.x + 90.0F) * 0.017453292F);
      Vec3 var10 = new Vec3((double)(var4 * var6), (double)var7, (double)(var5 * var6));
      Vec3 var11 = new Vec3((double)(var4 * var8), (double)var9, (double)(var5 * var8));
      Vec3 var12 = var10.cross(var11).scale(-1.0D);
      double var13 = var10.x * this.forwards + var11.x * this.up + var12.x * this.left;
      double var15 = var10.y * this.forwards + var11.y * this.up + var12.y * this.left;
      double var17 = var10.z * this.forwards + var11.z * this.up + var12.z * this.left;
      return new Vec3(var3.x + var13, var3.y + var15, var3.z + var17);
   }

   public Vec2 getRotation(CommandSourceStack commandSourceStack) {
      return Vec2.ZERO;
   }

   public boolean isXRelative() {
      return true;
   }

   public boolean isYRelative() {
      return true;
   }

   public boolean isZRelative() {
      return true;
   }

   public static LocalCoordinates parse(StringReader stringReader) throws CommandSyntaxException {
      int var1 = stringReader.getCursor();
      double var2 = readDouble(stringReader, var1);
      if(stringReader.canRead() && stringReader.peek() == 32) {
         stringReader.skip();
         double var4 = readDouble(stringReader, var1);
         if(stringReader.canRead() && stringReader.peek() == 32) {
            stringReader.skip();
            double var6 = readDouble(stringReader, var1);
            return new LocalCoordinates(var2, var4, var6);
         } else {
            stringReader.setCursor(var1);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(stringReader);
         }
      } else {
         stringReader.setCursor(var1);
         throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(stringReader);
      }
   }

   private static double readDouble(StringReader stringReader, int var1) throws CommandSyntaxException {
      if(!stringReader.canRead()) {
         throw WorldCoordinate.ERROR_EXPECTED_DOUBLE.createWithContext(stringReader);
      } else if(stringReader.peek() != 94) {
         stringReader.setCursor(var1);
         throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext(stringReader);
      } else {
         stringReader.skip();
         return stringReader.canRead() && stringReader.peek() != 32?stringReader.readDouble():0.0D;
      }
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof LocalCoordinates)) {
         return false;
      } else {
         LocalCoordinates var2 = (LocalCoordinates)object;
         return this.left == var2.left && this.up == var2.up && this.forwards == var2.forwards;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{Double.valueOf(this.left), Double.valueOf(this.up), Double.valueOf(this.forwards)});
   }
}
