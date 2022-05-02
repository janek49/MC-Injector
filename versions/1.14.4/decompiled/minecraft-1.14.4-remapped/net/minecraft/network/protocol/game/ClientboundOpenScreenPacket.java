package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.inventory.MenuType;

public class ClientboundOpenScreenPacket implements Packet {
   private int containerId;
   private int type;
   private Component title;

   public ClientboundOpenScreenPacket() {
   }

   public ClientboundOpenScreenPacket(int containerId, MenuType menuType, Component title) {
      this.containerId = containerId;
      this.type = Registry.MENU.getId(menuType);
      this.title = title;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.containerId = friendlyByteBuf.readVarInt();
      this.type = friendlyByteBuf.readVarInt();
      this.title = friendlyByteBuf.readComponent();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.containerId);
      friendlyByteBuf.writeVarInt(this.type);
      friendlyByteBuf.writeComponent(this.title);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleOpenScreen(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   @Nullable
   public MenuType getType() {
      return (MenuType)Registry.MENU.byId(this.type);
   }

   public Component getTitle() {
      return this.title;
   }
}
