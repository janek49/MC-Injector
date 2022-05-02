package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundCommandSuggestionPacket implements Packet {
   private int id;
   private String command;

   public ServerboundCommandSuggestionPacket() {
   }

   public ServerboundCommandSuggestionPacket(int id, String command) {
      this.id = id;
      this.command = command;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.id = friendlyByteBuf.readVarInt();
      this.command = friendlyByteBuf.readUtf(32500);
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.id);
      friendlyByteBuf.writeUtf(this.command, 32500);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleCustomCommandSuggestions(this);
   }

   public int getId() {
      return this.id;
   }

   public String getCommand() {
      return this.command;
   }
}
