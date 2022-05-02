package net.minecraft.network.protocol.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LowerCaseEnumTypeAdapterFactory;

public class ClientboundStatusResponsePacket implements Packet {
   private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(ServerStatus.Version.class, new ServerStatus.Version.Serializer()).registerTypeAdapter(ServerStatus.Players.class, new ServerStatus.Players.Serializer()).registerTypeAdapter(ServerStatus.class, new ServerStatus.Serializer()).registerTypeHierarchyAdapter(Component.class, new Component.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory()).create();
   private ServerStatus status;

   public ClientboundStatusResponsePacket() {
   }

   public ClientboundStatusResponsePacket(ServerStatus status) {
      this.status = status;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.status = (ServerStatus)GsonHelper.fromJson(GSON, friendlyByteBuf.readUtf(32767), ServerStatus.class);
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeUtf(GSON.toJson(this.status));
   }

   public void handle(ClientStatusPacketListener clientStatusPacketListener) {
      clientStatusPacketListener.handleStatusResponse(this);
   }

   public ServerStatus getStatus() {
      return this.status;
   }
}
