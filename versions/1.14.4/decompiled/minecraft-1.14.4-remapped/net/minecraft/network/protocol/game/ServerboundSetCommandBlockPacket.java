package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.level.block.entity.CommandBlockEntity;

public class ServerboundSetCommandBlockPacket implements Packet {
   private BlockPos pos;
   private String command;
   private boolean trackOutput;
   private boolean conditional;
   private boolean automatic;
   private CommandBlockEntity.Mode mode;

   public ServerboundSetCommandBlockPacket() {
   }

   public ServerboundSetCommandBlockPacket(BlockPos pos, String command, CommandBlockEntity.Mode mode, boolean trackOutput, boolean conditional, boolean automatic) {
      this.pos = pos;
      this.command = command;
      this.trackOutput = trackOutput;
      this.conditional = conditional;
      this.automatic = automatic;
      this.mode = mode;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.pos = friendlyByteBuf.readBlockPos();
      this.command = friendlyByteBuf.readUtf(32767);
      this.mode = (CommandBlockEntity.Mode)friendlyByteBuf.readEnum(CommandBlockEntity.Mode.class);
      int var2 = friendlyByteBuf.readByte();
      this.trackOutput = (var2 & 1) != 0;
      this.conditional = (var2 & 2) != 0;
      this.automatic = (var2 & 4) != 0;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeBlockPos(this.pos);
      friendlyByteBuf.writeUtf(this.command);
      friendlyByteBuf.writeEnum(this.mode);
      int var2 = 0;
      if(this.trackOutput) {
         var2 |= 1;
      }

      if(this.conditional) {
         var2 |= 2;
      }

      if(this.automatic) {
         var2 |= 4;
      }

      friendlyByteBuf.writeByte(var2);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleSetCommandBlock(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public String getCommand() {
      return this.command;
   }

   public boolean isTrackOutput() {
      return this.trackOutput;
   }

   public boolean isConditional() {
      return this.conditional;
   }

   public boolean isAutomatic() {
      return this.automatic;
   }

   public CommandBlockEntity.Mode getMode() {
      return this.mode;
   }
}
