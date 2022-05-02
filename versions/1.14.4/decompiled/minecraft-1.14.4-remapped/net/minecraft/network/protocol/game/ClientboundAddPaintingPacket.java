package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.entity.decoration.Painting;

public class ClientboundAddPaintingPacket implements Packet {
   private int id;
   private UUID uuid;
   private BlockPos pos;
   private Direction direction;
   private int motive;

   public ClientboundAddPaintingPacket() {
   }

   public ClientboundAddPaintingPacket(Painting painting) {
      this.id = painting.getId();
      this.uuid = painting.getUUID();
      this.pos = painting.getPos();
      this.direction = painting.getDirection();
      this.motive = Registry.MOTIVE.getId(painting.motive);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.id = friendlyByteBuf.readVarInt();
      this.uuid = friendlyByteBuf.readUUID();
      this.motive = friendlyByteBuf.readVarInt();
      this.pos = friendlyByteBuf.readBlockPos();
      this.direction = Direction.from2DDataValue(friendlyByteBuf.readUnsignedByte());
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.id);
      friendlyByteBuf.writeUUID(this.uuid);
      friendlyByteBuf.writeVarInt(this.motive);
      friendlyByteBuf.writeBlockPos(this.pos);
      friendlyByteBuf.writeByte(this.direction.get2DDataValue());
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleAddPainting(this);
   }

   public int getId() {
      return this.id;
   }

   public UUID getUUID() {
      return this.uuid;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Direction getDirection() {
      return this.direction;
   }

   public Motive getMotive() {
      return (Motive)Registry.MOTIVE.byId(this.motive);
   }
}
