package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.dimension.DimensionType;

public class ClientboundRespawnPacket implements Packet {
   private DimensionType dimension;
   private GameType playerGameType;
   private LevelType levelType;

   public ClientboundRespawnPacket() {
   }

   public ClientboundRespawnPacket(DimensionType dimension, LevelType levelType, GameType playerGameType) {
      this.dimension = dimension;
      this.playerGameType = playerGameType;
      this.levelType = levelType;
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleRespawn(this);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.dimension = DimensionType.getById(friendlyByteBuf.readInt());
      this.playerGameType = GameType.byId(friendlyByteBuf.readUnsignedByte());
      this.levelType = LevelType.getLevelType(friendlyByteBuf.readUtf(16));
      if(this.levelType == null) {
         this.levelType = LevelType.NORMAL;
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeInt(this.dimension.getId());
      friendlyByteBuf.writeByte(this.playerGameType.getId());
      friendlyByteBuf.writeUtf(this.levelType.getName());
   }

   public DimensionType getDimension() {
      return this.dimension;
   }

   public GameType getPlayerGameType() {
      return this.playerGameType;
   }

   public LevelType getLevelType() {
      return this.levelType;
   }
}
