package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;

public class ClientboundSetPassengersPacket implements Packet {
   private int vehicle;
   private int[] passengers;

   public ClientboundSetPassengersPacket() {
   }

   public ClientboundSetPassengersPacket(Entity entity) {
      this.vehicle = entity.getId();
      List<Entity> var2 = entity.getPassengers();
      this.passengers = new int[var2.size()];

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         this.passengers[var3] = ((Entity)var2.get(var3)).getId();
      }

   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.vehicle = friendlyByteBuf.readVarInt();
      this.passengers = friendlyByteBuf.readVarIntArray();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.vehicle);
      friendlyByteBuf.writeVarIntArray(this.passengers);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleSetEntityPassengersPacket(this);
   }

   public int[] getPassengers() {
      return this.passengers;
   }

   public int getVehicle() {
      return this.vehicle;
   }
}
