package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;

public class ServerboundClientInformationPacket implements Packet {
   private String language;
   private int viewDistance;
   private ChatVisiblity chatVisibility;
   private boolean chatColors;
   private int modelCustomisation;
   private HumanoidArm mainHand;

   public ServerboundClientInformationPacket() {
   }

   public ServerboundClientInformationPacket(String language, int viewDistance, ChatVisiblity chatVisibility, boolean chatColors, int modelCustomisation, HumanoidArm mainHand) {
      this.language = language;
      this.viewDistance = viewDistance;
      this.chatVisibility = chatVisibility;
      this.chatColors = chatColors;
      this.modelCustomisation = modelCustomisation;
      this.mainHand = mainHand;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.language = friendlyByteBuf.readUtf(16);
      this.viewDistance = friendlyByteBuf.readByte();
      this.chatVisibility = (ChatVisiblity)friendlyByteBuf.readEnum(ChatVisiblity.class);
      this.chatColors = friendlyByteBuf.readBoolean();
      this.modelCustomisation = friendlyByteBuf.readUnsignedByte();
      this.mainHand = (HumanoidArm)friendlyByteBuf.readEnum(HumanoidArm.class);
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeUtf(this.language);
      friendlyByteBuf.writeByte(this.viewDistance);
      friendlyByteBuf.writeEnum(this.chatVisibility);
      friendlyByteBuf.writeBoolean(this.chatColors);
      friendlyByteBuf.writeByte(this.modelCustomisation);
      friendlyByteBuf.writeEnum(this.mainHand);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleClientInformation(this);
   }

   public String getLanguage() {
      return this.language;
   }

   public ChatVisiblity getChatVisibility() {
      return this.chatVisibility;
   }

   public boolean getChatColors() {
      return this.chatColors;
   }

   public int getModelCustomisation() {
      return this.modelCustomisation;
   }

   public HumanoidArm getMainHand() {
      return this.mainHand;
   }
}
