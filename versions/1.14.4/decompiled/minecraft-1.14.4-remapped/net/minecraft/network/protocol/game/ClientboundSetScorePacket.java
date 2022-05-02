package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.ServerScoreboard;

public class ClientboundSetScorePacket implements Packet {
   private String owner = "";
   @Nullable
   private String objectiveName;
   private int score;
   private ServerScoreboard.Method method;

   public ClientboundSetScorePacket() {
   }

   public ClientboundSetScorePacket(ServerScoreboard.Method method, @Nullable String objectiveName, String owner, int score) {
      if(method != ServerScoreboard.Method.REMOVE && objectiveName == null) {
         throw new IllegalArgumentException("Need an objective name");
      } else {
         this.owner = owner;
         this.objectiveName = objectiveName;
         this.score = score;
         this.method = method;
      }
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.owner = friendlyByteBuf.readUtf(40);
      this.method = (ServerScoreboard.Method)friendlyByteBuf.readEnum(ServerScoreboard.Method.class);
      String var2 = friendlyByteBuf.readUtf(16);
      this.objectiveName = Objects.equals(var2, "")?null:var2;
      if(this.method != ServerScoreboard.Method.REMOVE) {
         this.score = friendlyByteBuf.readVarInt();
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeUtf(this.owner);
      friendlyByteBuf.writeEnum(this.method);
      friendlyByteBuf.writeUtf(this.objectiveName == null?"":this.objectiveName);
      if(this.method != ServerScoreboard.Method.REMOVE) {
         friendlyByteBuf.writeVarInt(this.score);
      }

   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleSetScore(this);
   }

   public String getOwner() {
      return this.owner;
   }

   @Nullable
   public String getObjectiveName() {
      return this.objectiveName;
   }

   public int getScore() {
      return this.score;
   }

   public ServerScoreboard.Method getMethod() {
      return this.method;
   }
}
