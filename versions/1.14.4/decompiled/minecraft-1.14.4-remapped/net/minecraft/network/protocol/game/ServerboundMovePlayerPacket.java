package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;

public class ServerboundMovePlayerPacket implements Packet {
   protected double x;
   protected double y;
   protected double z;
   protected float yRot;
   protected float xRot;
   protected boolean onGround;
   protected boolean hasPos;
   protected boolean hasRot;

   public ServerboundMovePlayerPacket() {
   }

   public ServerboundMovePlayerPacket(boolean onGround) {
      this.onGround = onGround;
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleMovePlayer(this);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.onGround = friendlyByteBuf.readUnsignedByte() != 0;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeByte(this.onGround?1:0);
   }

   public double getX(double d) {
      return this.hasPos?this.x:d;
   }

   public double getY(double d) {
      return this.hasPos?this.y:d;
   }

   public double getZ(double d) {
      return this.hasPos?this.z:d;
   }

   public float getYRot(float f) {
      return this.hasRot?this.yRot:f;
   }

   public float getXRot(float f) {
      return this.hasRot?this.xRot:f;
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public static class Pos extends ServerboundMovePlayerPacket {
      public Pos() {
         this.hasPos = true;
      }

      public Pos(double x, double y, double z, boolean onGround) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.onGround = onGround;
         this.hasPos = true;
      }

      public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
         this.x = friendlyByteBuf.readDouble();
         this.y = friendlyByteBuf.readDouble();
         this.z = friendlyByteBuf.readDouble();
         super.read(friendlyByteBuf);
      }

      public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
         friendlyByteBuf.writeDouble(this.x);
         friendlyByteBuf.writeDouble(this.y);
         friendlyByteBuf.writeDouble(this.z);
         super.write(friendlyByteBuf);
      }
   }

   public static class PosRot extends ServerboundMovePlayerPacket {
      public PosRot() {
         this.hasPos = true;
         this.hasRot = true;
      }

      public PosRot(double x, double y, double z, float yRot, float xRot, boolean onGround) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.yRot = yRot;
         this.xRot = xRot;
         this.onGround = onGround;
         this.hasRot = true;
         this.hasPos = true;
      }

      public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
         this.x = friendlyByteBuf.readDouble();
         this.y = friendlyByteBuf.readDouble();
         this.z = friendlyByteBuf.readDouble();
         this.yRot = friendlyByteBuf.readFloat();
         this.xRot = friendlyByteBuf.readFloat();
         super.read(friendlyByteBuf);
      }

      public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
         friendlyByteBuf.writeDouble(this.x);
         friendlyByteBuf.writeDouble(this.y);
         friendlyByteBuf.writeDouble(this.z);
         friendlyByteBuf.writeFloat(this.yRot);
         friendlyByteBuf.writeFloat(this.xRot);
         super.write(friendlyByteBuf);
      }
   }

   public static class Rot extends ServerboundMovePlayerPacket {
      public Rot() {
         this.hasRot = true;
      }

      public Rot(float yRot, float xRot, boolean onGround) {
         this.yRot = yRot;
         this.xRot = xRot;
         this.onGround = onGround;
         this.hasRot = true;
      }

      public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
         this.yRot = friendlyByteBuf.readFloat();
         this.xRot = friendlyByteBuf.readFloat();
         super.read(friendlyByteBuf);
      }

      public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
         friendlyByteBuf.writeFloat(this.yRot);
         friendlyByteBuf.writeFloat(this.xRot);
         super.write(friendlyByteBuf);
      }
   }
}
