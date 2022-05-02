package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.item.Item;

public class ClientboundCooldownPacket implements Packet {
   private Item item;
   private int duration;

   public ClientboundCooldownPacket() {
   }

   public ClientboundCooldownPacket(Item item, int duration) {
      this.item = item;
      this.duration = duration;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.item = Item.byId(friendlyByteBuf.readVarInt());
      this.duration = friendlyByteBuf.readVarInt();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(Item.getId(this.item));
      friendlyByteBuf.writeVarInt(this.duration);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleItemCooldown(this);
   }

   public Item getItem() {
      return this.item;
   }

   public int getDuration() {
      return this.duration;
   }
}
