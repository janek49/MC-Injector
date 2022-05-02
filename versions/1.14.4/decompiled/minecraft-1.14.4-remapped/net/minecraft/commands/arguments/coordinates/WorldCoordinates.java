package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class WorldCoordinates implements Coordinates {
   private final WorldCoordinate x;
   private final WorldCoordinate y;
   private final WorldCoordinate z;

   public WorldCoordinates(WorldCoordinate x, WorldCoordinate y, WorldCoordinate z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vec3 getPosition(CommandSourceStack commandSourceStack) {
      Vec3 vec3 = commandSourceStack.getPosition();
      return new Vec3(this.x.get(vec3.x), this.y.get(vec3.y), this.z.get(vec3.z));
   }

   public Vec2 getRotation(CommandSourceStack commandSourceStack) {
      Vec2 vec2 = commandSourceStack.getRotation();
      return new Vec2((float)this.x.get((double)vec2.x), (float)this.y.get((double)vec2.y));
   }

   public boolean isXRelative() {
      return this.x.isRelative();
   }

   public boolean isYRelative() {
      return this.y.isRelative();
   }

   public boolean isZRelative() {
      return this.z.isRelative();
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof WorldCoordinates)) {
         return false;
      } else {
         WorldCoordinates var2 = (WorldCoordinates)object;
         return !this.x.equals(var2.x)?false:(!this.y.equals(var2.y)?false:this.z.equals(var2.z));
      }
   }

   public static WorldCoordinates parseInt(StringReader stringReader) throws CommandSyntaxException {
      int var1 = stringReader.getCursor();
      WorldCoordinate var2 = WorldCoordinate.parseInt(stringReader);
      if(stringReader.canRead() && stringReader.peek() == 32) {
         stringReader.skip();
         WorldCoordinate var3 = WorldCoordinate.parseInt(stringReader);
         if(stringReader.canRead() && stringReader.peek() == 32) {
            stringReader.skip();
            WorldCoordinate var4 = WorldCoordinate.parseInt(stringReader);
            return new WorldCoordinates(var2, var3, var4);
         } else {
            stringReader.setCursor(var1);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(stringReader);
         }
      } else {
         stringReader.setCursor(var1);
         throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(stringReader);
      }
   }

   public static WorldCoordinates parseDouble(StringReader stringReader, boolean var1) throws CommandSyntaxException {
      int var2 = stringReader.getCursor();
      WorldCoordinate var3 = WorldCoordinate.parseDouble(stringReader, var1);
      if(stringReader.canRead() && stringReader.peek() == 32) {
         stringReader.skip();
         WorldCoordinate var4 = WorldCoordinate.parseDouble(stringReader, false);
         if(stringReader.canRead() && stringReader.peek() == 32) {
            stringReader.skip();
            WorldCoordinate var5 = WorldCoordinate.parseDouble(stringReader, var1);
            return new WorldCoordinates(var3, var4, var5);
         } else {
            stringReader.setCursor(var2);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(stringReader);
         }
      } else {
         stringReader.setCursor(var2);
         throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(stringReader);
      }
   }

   public static WorldCoordinates current() {
      return new WorldCoordinates(new WorldCoordinate(true, 0.0D), new WorldCoordinate(true, 0.0D), new WorldCoordinate(true, 0.0D));
   }

   public int hashCode() {
      int var1 = this.x.hashCode();
      var1 = 31 * var1 + this.y.hashCode();
      var1 = 31 * var1 + this.z.hashCode();
      return var1;
   }
}
