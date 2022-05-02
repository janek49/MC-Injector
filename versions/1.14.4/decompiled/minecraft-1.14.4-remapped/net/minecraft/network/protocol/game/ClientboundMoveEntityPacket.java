package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ClientboundMoveEntityPacket implements Packet {
   protected int entityId;
   protected short xa;
   protected short ya;
   protected short za;
   protected byte yRot;
   protected byte xRot;
   protected boolean onGround;
   protected boolean hasRot;

   public static long entityToPacket(double d) {
      return Mth.lfloor(d * 4096.0D);
   }

   public static Vec3 packetToEntity(long var0, long var2, long var4) {
      return (new Vec3((double)var0, (double)var2, (double)var4)).scale(2.44140625E-4D);
   }

   public ClientboundMoveEntityPacket() {
   }

   public ClientboundMoveEntityPacket(int entityId) {
      this.entityId = entityId;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.entityId = friendlyByteBuf.readVarInt();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.entityId);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleMoveEntity(this);
   }

   public String toString() {
      return "Entity_" + super.toString();
   }

   public Entity getEntity(Level level) {
      return level.getEntity(this.entityId);
   }

   public short getXa() {
      return this.xa;
   }

   public short getYa() {
      return this.ya;
   }

   public short getZa() {
      return this.za;
   }

   public byte getyRot() {
      return this.yRot;
   }

   public byte getxRot() {
      return this.xRot;
   }

   public boolean hasRotation() {
      return this.hasRot;
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public static class Pos extends ClientboundMoveEntityPacket {
      public Pos() {
      }

      public Pos(int var1, short xa, short ya, short za, boolean onGround) {
         super(var1);
         this.xa = xa;
         this.ya = ya;
         this.za = za;
         this.onGround = onGround;
      }

      public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
         super.read(friendlyByteBuf);
         this.xa = friendlyByteBuf.readShort();
         this.ya = friendlyByteBuf.readShort();
         this.za = friendlyByteBuf.readShort();
         this.onGround = friendlyByteBuf.readBoolean();
      }

      public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
         super.write(friendlyByteBuf);
         friendlyByteBuf.writeShort(this.xa);
         friendlyByteBuf.writeShort(this.ya);
         friendlyByteBuf.writeShort(this.za);
         friendlyByteBuf.writeBoolean(this.onGround);
      }
   }

   public static class PosRot extends ClientboundMoveEntityPacket {
      public PosRot() {
         this.hasRot = true;
      }

      public PosRot(int var1, short xa, short ya, short za, byte yRot, byte xRot, boolean onGround) {
         super(var1);
         this.xa = xa;
         this.ya = ya;
         this.za = za;
         this.yRot = yRot;
         this.xRot = xRot;
         this.onGround = onGround;
         this.hasRot = true;
      }

      public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
         super.read(friendlyByteBuf);
         this.xa = friendlyByteBuf.readShort();
         this.ya = friendlyByteBuf.readShort();
         this.za = friendlyByteBuf.readShort();
         this.yRot = friendlyByteBuf.readByte();
         this.xRot = friendlyByteBuf.readByte();
         this.onGround = friendlyByteBuf.readBoolean();
      }

      public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
         super.write(friendlyByteBuf);
         friendlyByteBuf.writeShort(this.xa);
         friendlyByteBuf.writeShort(this.ya);
         friendlyByteBuf.writeShort(this.za);
         friendlyByteBuf.writeByte(this.yRot);
         friendlyByteBuf.writeByte(this.xRot);
         friendlyByteBuf.writeBoolean(this.onGround);
      }
   }

   public static class Rot extends ClientboundMoveEntityPacket {
      public Rot() {
         this.hasRot = true;
      }

      public Rot(int var1, byte yRot, byte xRot, boolean onGround) {
         super(var1);
         this.yRot = yRot;
         this.xRot = xRot;
         this.hasRot = true;
         this.onGround = onGround;
      }

      public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
         super.read(friendlyByteBuf);
         this.yRot = friendlyByteBuf.readByte();
         this.xRot = friendlyByteBuf.readByte();
         this.onGround = friendlyByteBuf.readBoolean();
      }

      public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
         super.write(friendlyByteBuf);
         friendlyByteBuf.writeByte(this.yRot);
         friendlyByteBuf.writeByte(this.xRot);
         friendlyByteBuf.writeBoolean(this.onGround);
      }
   }
}
