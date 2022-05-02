package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ClientboundExplodePacket implements Packet {
   private double x;
   private double y;
   private double z;
   private float power;
   private List toBlow;
   private float knockbackX;
   private float knockbackY;
   private float knockbackZ;

   public ClientboundExplodePacket() {
   }

   public ClientboundExplodePacket(double x, double y, double z, float power, List list, Vec3 vec3) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.power = power;
      this.toBlow = Lists.newArrayList(list);
      if(vec3 != null) {
         this.knockbackX = (float)vec3.x;
         this.knockbackY = (float)vec3.y;
         this.knockbackZ = (float)vec3.z;
      }

   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.x = (double)friendlyByteBuf.readFloat();
      this.y = (double)friendlyByteBuf.readFloat();
      this.z = (double)friendlyByteBuf.readFloat();
      this.power = friendlyByteBuf.readFloat();
      int var2 = friendlyByteBuf.readInt();
      this.toBlow = Lists.newArrayListWithCapacity(var2);
      int var3 = Mth.floor(this.x);
      int var4 = Mth.floor(this.y);
      int var5 = Mth.floor(this.z);

      for(int var6 = 0; var6 < var2; ++var6) {
         int var7 = friendlyByteBuf.readByte() + var3;
         int var8 = friendlyByteBuf.readByte() + var4;
         int var9 = friendlyByteBuf.readByte() + var5;
         this.toBlow.add(new BlockPos(var7, var8, var9));
      }

      this.knockbackX = friendlyByteBuf.readFloat();
      this.knockbackY = friendlyByteBuf.readFloat();
      this.knockbackZ = friendlyByteBuf.readFloat();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeFloat((float)this.x);
      friendlyByteBuf.writeFloat((float)this.y);
      friendlyByteBuf.writeFloat((float)this.z);
      friendlyByteBuf.writeFloat(this.power);
      friendlyByteBuf.writeInt(this.toBlow.size());
      int var2 = Mth.floor(this.x);
      int var3 = Mth.floor(this.y);
      int var4 = Mth.floor(this.z);

      for(BlockPos var6 : this.toBlow) {
         int var7 = var6.getX() - var2;
         int var8 = var6.getY() - var3;
         int var9 = var6.getZ() - var4;
         friendlyByteBuf.writeByte(var7);
         friendlyByteBuf.writeByte(var8);
         friendlyByteBuf.writeByte(var9);
      }

      friendlyByteBuf.writeFloat(this.knockbackX);
      friendlyByteBuf.writeFloat(this.knockbackY);
      friendlyByteBuf.writeFloat(this.knockbackZ);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleExplosion(this);
   }

   public float getKnockbackX() {
      return this.knockbackX;
   }

   public float getKnockbackY() {
      return this.knockbackY;
   }

   public float getKnockbackZ() {
      return this.knockbackZ;
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

   public float getPower() {
      return this.power;
   }

   public List getToBlow() {
      return this.toBlow;
   }
}
