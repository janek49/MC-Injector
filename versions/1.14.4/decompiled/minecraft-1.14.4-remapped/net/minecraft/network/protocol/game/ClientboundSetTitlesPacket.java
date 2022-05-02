package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundSetTitlesPacket implements Packet {
   private ClientboundSetTitlesPacket.Type type;
   private Component text;
   private int fadeInTime;
   private int stayTime;
   private int fadeOutTime;

   public ClientboundSetTitlesPacket() {
   }

   public ClientboundSetTitlesPacket(ClientboundSetTitlesPacket.Type clientboundSetTitlesPacket$Type, Component component) {
      this(clientboundSetTitlesPacket$Type, component, -1, -1, -1);
   }

   public ClientboundSetTitlesPacket(int var1, int var2, int var3) {
      this(ClientboundSetTitlesPacket.Type.TIMES, (Component)null, var1, var2, var3);
   }

   public ClientboundSetTitlesPacket(ClientboundSetTitlesPacket.Type type, @Nullable Component text, int fadeInTime, int stayTime, int fadeOutTime) {
      this.type = type;
      this.text = text;
      this.fadeInTime = fadeInTime;
      this.stayTime = stayTime;
      this.fadeOutTime = fadeOutTime;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.type = (ClientboundSetTitlesPacket.Type)friendlyByteBuf.readEnum(ClientboundSetTitlesPacket.Type.class);
      if(this.type == ClientboundSetTitlesPacket.Type.TITLE || this.type == ClientboundSetTitlesPacket.Type.SUBTITLE || this.type == ClientboundSetTitlesPacket.Type.ACTIONBAR) {
         this.text = friendlyByteBuf.readComponent();
      }

      if(this.type == ClientboundSetTitlesPacket.Type.TIMES) {
         this.fadeInTime = friendlyByteBuf.readInt();
         this.stayTime = friendlyByteBuf.readInt();
         this.fadeOutTime = friendlyByteBuf.readInt();
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeEnum(this.type);
      if(this.type == ClientboundSetTitlesPacket.Type.TITLE || this.type == ClientboundSetTitlesPacket.Type.SUBTITLE || this.type == ClientboundSetTitlesPacket.Type.ACTIONBAR) {
         friendlyByteBuf.writeComponent(this.text);
      }

      if(this.type == ClientboundSetTitlesPacket.Type.TIMES) {
         friendlyByteBuf.writeInt(this.fadeInTime);
         friendlyByteBuf.writeInt(this.stayTime);
         friendlyByteBuf.writeInt(this.fadeOutTime);
      }

   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleSetTitles(this);
   }

   public ClientboundSetTitlesPacket.Type getType() {
      return this.type;
   }

   public Component getText() {
      return this.text;
   }

   public int getFadeInTime() {
      return this.fadeInTime;
   }

   public int getStayTime() {
      return this.stayTime;
   }

   public int getFadeOutTime() {
      return this.fadeOutTime;
   }

   public static enum Type {
      TITLE,
      SUBTITLE,
      ACTIONBAR,
      TIMES,
      CLEAR,
      RESET;
   }
}
