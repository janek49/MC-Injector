package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundChatPacket implements Packet {
   private Component message;
   private ChatType type;

   public ClientboundChatPacket() {
   }

   public ClientboundChatPacket(Component component) {
      this(component, ChatType.SYSTEM);
   }

   public ClientboundChatPacket(Component message, ChatType type) {
      this.message = message;
      this.type = type;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.message = friendlyByteBuf.readComponent();
      this.type = ChatType.getForIndex(friendlyByteBuf.readByte());
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeComponent(this.message);
      friendlyByteBuf.writeByte(this.type.getIndex());
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleChat(this);
   }

   public Component getMessage() {
      return this.message;
   }

   public boolean isSystem() {
      return this.type == ChatType.SYSTEM || this.type == ChatType.GAME_INFO;
   }

   public ChatType getType() {
      return this.type;
   }

   public boolean isSkippable() {
      return true;
   }
}
