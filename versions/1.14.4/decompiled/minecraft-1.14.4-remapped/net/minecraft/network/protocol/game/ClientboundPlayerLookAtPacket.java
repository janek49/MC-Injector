package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ClientboundPlayerLookAtPacket implements Packet {
   private double x;
   private double y;
   private double z;
   private int entity;
   private EntityAnchorArgument.Anchor fromAnchor;
   private EntityAnchorArgument.Anchor toAnchor;
   private boolean atEntity;

   public ClientboundPlayerLookAtPacket() {
   }

   public ClientboundPlayerLookAtPacket(EntityAnchorArgument.Anchor fromAnchor, double x, double y, double z) {
      this.fromAnchor = fromAnchor;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public ClientboundPlayerLookAtPacket(EntityAnchorArgument.Anchor fromAnchor, Entity entity, EntityAnchorArgument.Anchor toAnchor) {
      this.fromAnchor = fromAnchor;
      this.entity = entity.getId();
      this.toAnchor = toAnchor;
      Vec3 var4 = toAnchor.apply(entity);
      this.x = var4.x;
      this.y = var4.y;
      this.z = var4.z;
      this.atEntity = true;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.fromAnchor = (EntityAnchorArgument.Anchor)friendlyByteBuf.readEnum(EntityAnchorArgument.Anchor.class);
      this.x = friendlyByteBuf.readDouble();
      this.y = friendlyByteBuf.readDouble();
      this.z = friendlyByteBuf.readDouble();
      if(friendlyByteBuf.readBoolean()) {
         this.atEntity = true;
         this.entity = friendlyByteBuf.readVarInt();
         this.toAnchor = (EntityAnchorArgument.Anchor)friendlyByteBuf.readEnum(EntityAnchorArgument.Anchor.class);
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeEnum(this.fromAnchor);
      friendlyByteBuf.writeDouble(this.x);
      friendlyByteBuf.writeDouble(this.y);
      friendlyByteBuf.writeDouble(this.z);
      friendlyByteBuf.writeBoolean(this.atEntity);
      if(this.atEntity) {
         friendlyByteBuf.writeVarInt(this.entity);
         friendlyByteBuf.writeEnum(this.toAnchor);
      }

   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleLookAt(this);
   }

   public EntityAnchorArgument.Anchor getFromAnchor() {
      return this.fromAnchor;
   }

   @Nullable
   public Vec3 getPosition(Level level) {
      if(this.atEntity) {
         Entity var2 = level.getEntity(this.entity);
         return var2 == null?new Vec3(this.x, this.y, this.z):this.toAnchor.apply(var2);
      } else {
         return new Vec3(this.x, this.y, this.z);
      }
   }
}
