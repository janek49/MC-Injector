package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundPlayerInputPacket implements Packet {
   private float xxa;
   private float zza;
   private boolean isJumping;
   private boolean isSneaking;

   public ServerboundPlayerInputPacket() {
   }

   public ServerboundPlayerInputPacket(float xxa, float zza, boolean isJumping, boolean isSneaking) {
      this.xxa = xxa;
      this.zza = zza;
      this.isJumping = isJumping;
      this.isSneaking = isSneaking;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.xxa = friendlyByteBuf.readFloat();
      this.zza = friendlyByteBuf.readFloat();
      byte var2 = friendlyByteBuf.readByte();
      this.isJumping = (var2 & 1) > 0;
      this.isSneaking = (var2 & 2) > 0;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeFloat(this.xxa);
      friendlyByteBuf.writeFloat(this.zza);
      byte var2 = 0;
      if(this.isJumping) {
         var2 = (byte)(var2 | 1);
      }

      if(this.isSneaking) {
         var2 = (byte)(var2 | 2);
      }

      friendlyByteBuf.writeByte(var2);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handlePlayerInput(this);
   }

   public float getXxa() {
      return this.xxa;
   }

   public float getZza() {
      return this.zza;
   }

   public boolean isJumping() {
      return this.isJumping;
   }

   public boolean isSneaking() {
      return this.isSneaking;
   }
}
