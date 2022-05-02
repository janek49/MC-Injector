package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.InteractionHand;

public class ClientboundOpenBookPacket implements Packet {
   private InteractionHand hand;

   public ClientboundOpenBookPacket() {
   }

   public ClientboundOpenBookPacket(InteractionHand hand) {
      this.hand = hand;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.hand = (InteractionHand)friendlyByteBuf.readEnum(InteractionHand.class);
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeEnum(this.hand);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleOpenBook(this);
   }

   public InteractionHand getHand() {
      return this.hand;
   }
}
