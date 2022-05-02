package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;

public class ClientboundSetEntityDataPacket implements Packet {
   private int id;
   private List packedItems;

   public ClientboundSetEntityDataPacket() {
   }

   public ClientboundSetEntityDataPacket(int id, SynchedEntityData synchedEntityData, boolean var3) {
      this.id = id;
      if(var3) {
         this.packedItems = synchedEntityData.getAll();
         synchedEntityData.clearDirty();
      } else {
         this.packedItems = synchedEntityData.packDirty();
      }

   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.id = friendlyByteBuf.readVarInt();
      this.packedItems = SynchedEntityData.unpack(friendlyByteBuf);
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.id);
      SynchedEntityData.pack(this.packedItems, friendlyByteBuf);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleSetEntityData(this);
   }

   public List getUnpackedData() {
      return this.packedItems;
   }

   public int getId() {
      return this.id;
   }
}
