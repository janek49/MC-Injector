package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.dimension.DimensionType;

public class ClientboundLoginPacket implements Packet {
   private int playerId;
   private boolean hardcore;
   private GameType gameType;
   private DimensionType dimension;
   private int maxPlayers;
   private LevelType levelType;
   private int chunkRadius;
   private boolean reducedDebugInfo;

   public ClientboundLoginPacket() {
   }

   public ClientboundLoginPacket(int playerId, GameType gameType, boolean hardcore, DimensionType dimension, int maxPlayers, LevelType levelType, int chunkRadius, boolean reducedDebugInfo) {
      this.playerId = playerId;
      this.dimension = dimension;
      this.gameType = gameType;
      this.maxPlayers = maxPlayers;
      this.hardcore = hardcore;
      this.levelType = levelType;
      this.chunkRadius = chunkRadius;
      this.reducedDebugInfo = reducedDebugInfo;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.playerId = friendlyByteBuf.readInt();
      int var2 = friendlyByteBuf.readUnsignedByte();
      this.hardcore = (var2 & 8) == 8;
      var2 = var2 & -9;
      this.gameType = GameType.byId(var2);
      this.dimension = DimensionType.getById(friendlyByteBuf.readInt());
      this.maxPlayers = friendlyByteBuf.readUnsignedByte();
      this.levelType = LevelType.getLevelType(friendlyByteBuf.readUtf(16));
      if(this.levelType == null) {
         this.levelType = LevelType.NORMAL;
      }

      this.chunkRadius = friendlyByteBuf.readVarInt();
      this.reducedDebugInfo = friendlyByteBuf.readBoolean();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeInt(this.playerId);
      int var2 = this.gameType.getId();
      if(this.hardcore) {
         var2 |= 8;
      }

      friendlyByteBuf.writeByte(var2);
      friendlyByteBuf.writeInt(this.dimension.getId());
      friendlyByteBuf.writeByte(this.maxPlayers);
      friendlyByteBuf.writeUtf(this.levelType.getName());
      friendlyByteBuf.writeVarInt(this.chunkRadius);
      friendlyByteBuf.writeBoolean(this.reducedDebugInfo);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleLogin(this);
   }

   public int getPlayerId() {
      return this.playerId;
   }

   public boolean isHardcore() {
      return this.hardcore;
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public DimensionType getDimension() {
      return this.dimension;
   }

   public LevelType getLevelType() {
      return this.levelType;
   }

   public int getChunkRadius() {
      return this.chunkRadius;
   }

   public boolean isReducedDebugInfo() {
      return this.reducedDebugInfo;
   }
}
