package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ClientboundSetEntityMotionPacket implements Packet {
   private int id;
   private int xa;
   private int ya;
   private int za;

   public ClientboundSetEntityMotionPacket() {
   }

   public ClientboundSetEntityMotionPacket(Entity entity) {
      this(entity.getId(), entity.getDeltaMovement());
   }

   public ClientboundSetEntityMotionPacket(int id, Vec3 vec3) {
      this.id = id;
      double var3 = 3.9D;
      double var5 = Mth.clamp(vec3.x, -3.9D, 3.9D);
      double var7 = Mth.clamp(vec3.y, -3.9D, 3.9D);
      double var9 = Mth.clamp(vec3.z, -3.9D, 3.9D);
      this.xa = (int)(var5 * 8000.0D);
      this.ya = (int)(var7 * 8000.0D);
      this.za = (int)(var9 * 8000.0D);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.id = friendlyByteBuf.readVarInt();
      this.xa = friendlyByteBuf.readShort();
      this.ya = friendlyByteBuf.readShort();
      this.za = friendlyByteBuf.readShort();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.id);
      friendlyByteBuf.writeShort(this.xa);
      friendlyByteBuf.writeShort(this.ya);
      friendlyByteBuf.writeShort(this.za);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleSetEntityMotion(this);
   }

   public int getId() {
      return this.id;
   }

   public int getXa() {
      return this.xa;
   }

   public int getYa() {
      return this.ya;
   }

   public int getZa() {
      return this.za;
   }
}
