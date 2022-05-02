package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientboundBlockBreakAckPacket implements Packet {
   private static final Logger LOGGER = LogManager.getLogger();
   private BlockPos pos;
   private BlockState state;
   ServerboundPlayerActionPacket.Action action;
   private boolean allGood;

   public ClientboundBlockBreakAckPacket() {
   }

   public ClientboundBlockBreakAckPacket(BlockPos blockPos, BlockState state, ServerboundPlayerActionPacket.Action action, boolean allGood) {
      this.pos = blockPos.immutable();
      this.state = state;
      this.action = action;
      this.allGood = allGood;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.pos = friendlyByteBuf.readBlockPos();
      this.state = (BlockState)Block.BLOCK_STATE_REGISTRY.byId(friendlyByteBuf.readVarInt());
      this.action = (ServerboundPlayerActionPacket.Action)friendlyByteBuf.readEnum(ServerboundPlayerActionPacket.Action.class);
      this.allGood = friendlyByteBuf.readBoolean();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeBlockPos(this.pos);
      friendlyByteBuf.writeVarInt(Block.getId(this.state));
      friendlyByteBuf.writeEnum(this.action);
      friendlyByteBuf.writeBoolean(this.allGood);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleBlockBreakAck(this);
   }

   public BlockState getState() {
      return this.state;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public boolean allGood() {
      return this.allGood;
   }

   public ServerboundPlayerActionPacket.Action action() {
      return this.action;
   }
}
