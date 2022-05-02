package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ClientboundSetObjectivePacket implements Packet {
   private String objectiveName;
   private Component displayName;
   private ObjectiveCriteria.RenderType renderType;
   private int method;

   public ClientboundSetObjectivePacket() {
   }

   public ClientboundSetObjectivePacket(Objective objective, int method) {
      this.objectiveName = objective.getName();
      this.displayName = objective.getDisplayName();
      this.renderType = objective.getRenderType();
      this.method = method;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.objectiveName = friendlyByteBuf.readUtf(16);
      this.method = friendlyByteBuf.readByte();
      if(this.method == 0 || this.method == 2) {
         this.displayName = friendlyByteBuf.readComponent();
         this.renderType = (ObjectiveCriteria.RenderType)friendlyByteBuf.readEnum(ObjectiveCriteria.RenderType.class);
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeUtf(this.objectiveName);
      friendlyByteBuf.writeByte(this.method);
      if(this.method == 0 || this.method == 2) {
         friendlyByteBuf.writeComponent(this.displayName);
         friendlyByteBuf.writeEnum(this.renderType);
      }

   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleAddObjective(this);
   }

   public String getObjectiveName() {
      return this.objectiveName;
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public int getMethod() {
      return this.method;
   }

   public ObjectiveCriteria.RenderType getRenderType() {
      return this.renderType;
   }
}
