package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.Level;

public class ServerboundSetCommandMinecartPacket implements Packet {
   private int entity;
   private String command;
   private boolean trackOutput;

   public ServerboundSetCommandMinecartPacket() {
   }

   public ServerboundSetCommandMinecartPacket(int entity, String command, boolean trackOutput) {
      this.entity = entity;
      this.command = command;
      this.trackOutput = trackOutput;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.entity = friendlyByteBuf.readVarInt();
      this.command = friendlyByteBuf.readUtf(32767);
      this.trackOutput = friendlyByteBuf.readBoolean();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.entity);
      friendlyByteBuf.writeUtf(this.command);
      friendlyByteBuf.writeBoolean(this.trackOutput);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleSetCommandMinecart(this);
   }

   @Nullable
   public BaseCommandBlock getCommandBlock(Level level) {
      Entity var2 = level.getEntity(this.entity);
      return var2 instanceof MinecartCommandBlock?((MinecartCommandBlock)var2).getCommandBlock():null;
   }

   public String getCommand() {
      return this.command;
   }

   public boolean isTrackOutput() {
      return this.trackOutput;
   }
}
