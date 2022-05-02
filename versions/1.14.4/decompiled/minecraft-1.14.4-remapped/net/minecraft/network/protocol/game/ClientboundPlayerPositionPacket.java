package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundPlayerPositionPacket implements Packet {
   private double x;
   private double y;
   private double z;
   private float yRot;
   private float xRot;
   private Set relativeArguments;
   private int id;

   public ClientboundPlayerPositionPacket() {
   }

   public ClientboundPlayerPositionPacket(double x, double y, double z, float yRot, float xRot, Set relativeArguments, int id) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.yRot = yRot;
      this.xRot = xRot;
      this.relativeArguments = relativeArguments;
      this.id = id;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.x = friendlyByteBuf.readDouble();
      this.y = friendlyByteBuf.readDouble();
      this.z = friendlyByteBuf.readDouble();
      this.yRot = friendlyByteBuf.readFloat();
      this.xRot = friendlyByteBuf.readFloat();
      this.relativeArguments = ClientboundPlayerPositionPacket.RelativeArgument.unpack(friendlyByteBuf.readUnsignedByte());
      this.id = friendlyByteBuf.readVarInt();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeDouble(this.x);
      friendlyByteBuf.writeDouble(this.y);
      friendlyByteBuf.writeDouble(this.z);
      friendlyByteBuf.writeFloat(this.yRot);
      friendlyByteBuf.writeFloat(this.xRot);
      friendlyByteBuf.writeByte(ClientboundPlayerPositionPacket.RelativeArgument.pack(this.relativeArguments));
      friendlyByteBuf.writeVarInt(this.id);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleMovePlayer(this);
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public float getYRot() {
      return this.yRot;
   }

   public float getXRot() {
      return this.xRot;
   }

   public int getId() {
      return this.id;
   }

   public Set getRelativeArguments() {
      return this.relativeArguments;
   }

   public static enum RelativeArgument {
      X(0),
      Y(1),
      Z(2),
      Y_ROT(3),
      X_ROT(4);

      private final int bit;

      private RelativeArgument(int bit) {
         this.bit = bit;
      }

      private int getMask() {
         return 1 << this.bit;
      }

      private boolean isSet(int i) {
         return (i & this.getMask()) == this.getMask();
      }

      public static Set unpack(int i) {
         Set<ClientboundPlayerPositionPacket.RelativeArgument> set = EnumSet.noneOf(ClientboundPlayerPositionPacket.RelativeArgument.class);

         for(ClientboundPlayerPositionPacket.RelativeArgument var5 : values()) {
            if(var5.isSet(i)) {
               set.add(var5);
            }
         }

         return set;
      }

      public static int pack(Set set) {
         int var1 = 0;

         for(ClientboundPlayerPositionPacket.RelativeArgument var3 : set) {
            var1 |= var3.getMask();
         }

         return var1;
      }
   }
}
