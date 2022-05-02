package net.minecraft.world.level.saveddata.maps;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class MapDecoration {
   private final MapDecoration.Type type;
   private byte x;
   private byte y;
   private byte rot;
   private final Component name;

   public MapDecoration(MapDecoration.Type type, byte x, byte y, byte rot, @Nullable Component name) {
      this.type = type;
      this.x = x;
      this.y = y;
      this.rot = rot;
      this.name = name;
   }

   public byte getImage() {
      return this.type.getIcon();
   }

   public MapDecoration.Type getType() {
      return this.type;
   }

   public byte getX() {
      return this.x;
   }

   public byte getY() {
      return this.y;
   }

   public byte getRot() {
      return this.rot;
   }

   public boolean renderOnFrame() {
      return this.type.isRenderedOnFrame();
   }

   @Nullable
   public Component getName() {
      return this.name;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof MapDecoration)) {
         return false;
      } else {
         MapDecoration var2 = (MapDecoration)object;
         return this.type != var2.type?false:(this.rot != var2.rot?false:(this.x != var2.x?false:(this.y != var2.y?false:Objects.equals(this.name, var2.name))));
      }
   }

   public int hashCode() {
      int var1 = this.type.getIcon();
      var1 = 31 * var1 + this.x;
      var1 = 31 * var1 + this.y;
      var1 = 31 * var1 + this.rot;
      var1 = 31 * var1 + Objects.hashCode(this.name);
      return var1;
   }

   public static enum Type {
      PLAYER(false),
      FRAME(true),
      RED_MARKER(false),
      BLUE_MARKER(false),
      TARGET_X(true),
      TARGET_POINT(true),
      PLAYER_OFF_MAP(false),
      PLAYER_OFF_LIMITS(false),
      MANSION(true, 5393476),
      MONUMENT(true, 3830373),
      BANNER_WHITE(true),
      BANNER_ORANGE(true),
      BANNER_MAGENTA(true),
      BANNER_LIGHT_BLUE(true),
      BANNER_YELLOW(true),
      BANNER_LIME(true),
      BANNER_PINK(true),
      BANNER_GRAY(true),
      BANNER_LIGHT_GRAY(true),
      BANNER_CYAN(true),
      BANNER_PURPLE(true),
      BANNER_BLUE(true),
      BANNER_BROWN(true),
      BANNER_GREEN(true),
      BANNER_RED(true),
      BANNER_BLACK(true),
      RED_X(true);

      private final byte icon;
      private final boolean renderedOnFrame;
      private final int mapColor;

      private Type(boolean var3) {
         this(var3, -1);
      }

      private Type(boolean renderedOnFrame, int mapColor) {
         this.icon = (byte)this.ordinal();
         this.renderedOnFrame = renderedOnFrame;
         this.mapColor = mapColor;
      }

      public byte getIcon() {
         return this.icon;
      }

      public boolean isRenderedOnFrame() {
         return this.renderedOnFrame;
      }

      public boolean hasMapColor() {
         return this.mapColor >= 0;
      }

      public int getMapColor() {
         return this.mapColor;
      }

      public static MapDecoration.Type byIcon(byte icon) {
         return values()[Mth.clamp(icon, 0, values().length - 1)];
      }
   }
}
