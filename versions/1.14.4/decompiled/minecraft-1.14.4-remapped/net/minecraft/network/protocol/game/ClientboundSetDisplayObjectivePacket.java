package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.scores.Objective;

public class ClientboundSetDisplayObjectivePacket implements Packet {
   private int slot;
   private String objectiveName;

   public ClientboundSetDisplayObjectivePacket() {
   }

   public ClientboundSetDisplayObjectivePacket(int slot, @Nullable Objective objective) {
      this.slot = slot;
      if(objective == null) {
         this.objectiveName = "";
      } else {
         this.objectiveName = objective.getName();
      }

   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.slot = friendlyByteBuf.readByte();
      this.objectiveName = friendlyByteBuf.readUtf(16);
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeByte(this.slot);
      friendlyByteBuf.writeUtf(this.objectiveName);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleSetDisplayObjective(this);
   }

   public int getSlot() {
      return this.slot;
   }

   @Nullable
   public String getObjectiveName() {
      return Objects.equals(this.objectiveName, "")?null:this.objectiveName;
   }
}
