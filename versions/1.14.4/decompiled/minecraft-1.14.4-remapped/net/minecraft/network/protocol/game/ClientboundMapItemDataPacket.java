package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.Collection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class ClientboundMapItemDataPacket implements Packet {
   private int mapId;
   private byte scale;
   private boolean trackingPosition;
   private boolean locked;
   private MapDecoration[] decorations;
   private int startX;
   private int startY;
   private int width;
   private int height;
   private byte[] mapColors;

   public ClientboundMapItemDataPacket() {
   }

   public ClientboundMapItemDataPacket(int mapId, byte scale, boolean trackingPosition, boolean locked, Collection collection, byte[] bytes, int startX, int startY, int width, int height) {
      this.mapId = mapId;
      this.scale = scale;
      this.trackingPosition = trackingPosition;
      this.locked = locked;
      this.decorations = (MapDecoration[])collection.toArray(new MapDecoration[collection.size()]);
      this.startX = startX;
      this.startY = startY;
      this.width = width;
      this.height = height;
      this.mapColors = new byte[width * height];

      for(int var11 = 0; var11 < width; ++var11) {
         for(int var12 = 0; var12 < height; ++var12) {
            this.mapColors[var11 + var12 * width] = bytes[startX + var11 + (startY + var12) * 128];
         }
      }

   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.mapId = friendlyByteBuf.readVarInt();
      this.scale = friendlyByteBuf.readByte();
      this.trackingPosition = friendlyByteBuf.readBoolean();
      this.locked = friendlyByteBuf.readBoolean();
      this.decorations = new MapDecoration[friendlyByteBuf.readVarInt()];

      for(int var2 = 0; var2 < this.decorations.length; ++var2) {
         MapDecoration.Type var3 = (MapDecoration.Type)friendlyByteBuf.readEnum(MapDecoration.Type.class);
         this.decorations[var2] = new MapDecoration(var3, friendlyByteBuf.readByte(), friendlyByteBuf.readByte(), (byte)(friendlyByteBuf.readByte() & 15), friendlyByteBuf.readBoolean()?friendlyByteBuf.readComponent():null);
      }

      this.width = friendlyByteBuf.readUnsignedByte();
      if(this.width > 0) {
         this.height = friendlyByteBuf.readUnsignedByte();
         this.startX = friendlyByteBuf.readUnsignedByte();
         this.startY = friendlyByteBuf.readUnsignedByte();
         this.mapColors = friendlyByteBuf.readByteArray();
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.mapId);
      friendlyByteBuf.writeByte(this.scale);
      friendlyByteBuf.writeBoolean(this.trackingPosition);
      friendlyByteBuf.writeBoolean(this.locked);
      friendlyByteBuf.writeVarInt(this.decorations.length);

      for(MapDecoration var5 : this.decorations) {
         friendlyByteBuf.writeEnum(var5.getType());
         friendlyByteBuf.writeByte(var5.getX());
         friendlyByteBuf.writeByte(var5.getY());
         friendlyByteBuf.writeByte(var5.getRot() & 15);
         if(var5.getName() != null) {
            friendlyByteBuf.writeBoolean(true);
            friendlyByteBuf.writeComponent(var5.getName());
         } else {
            friendlyByteBuf.writeBoolean(false);
         }
      }

      friendlyByteBuf.writeByte(this.width);
      if(this.width > 0) {
         friendlyByteBuf.writeByte(this.height);
         friendlyByteBuf.writeByte(this.startX);
         friendlyByteBuf.writeByte(this.startY);
         friendlyByteBuf.writeByteArray(this.mapColors);
      }

   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleMapItemData(this);
   }

   public int getMapId() {
      return this.mapId;
   }

   public void applyToMap(MapItemSavedData mapItemSavedData) {
      mapItemSavedData.scale = this.scale;
      mapItemSavedData.trackingPosition = this.trackingPosition;
      mapItemSavedData.locked = this.locked;
      mapItemSavedData.decorations.clear();

      for(int var2 = 0; var2 < this.decorations.length; ++var2) {
         MapDecoration var3 = this.decorations[var2];
         mapItemSavedData.decorations.put("icon-" + var2, var3);
      }

      for(int var2 = 0; var2 < this.width; ++var2) {
         for(int var3 = 0; var3 < this.height; ++var3) {
            mapItemSavedData.colors[this.startX + var2 + (this.startY + var3) * 128] = this.mapColors[var2 + var3 * this.width];
         }
      }

   }
}
