package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ServerboundInteractPacket implements Packet {
   private int entityId;
   private ServerboundInteractPacket.Action action;
   private Vec3 location;
   private InteractionHand hand;

   public ServerboundInteractPacket() {
   }

   public ServerboundInteractPacket(Entity entity) {
      this.entityId = entity.getId();
      this.action = ServerboundInteractPacket.Action.ATTACK;
   }

   public ServerboundInteractPacket(Entity entity, InteractionHand hand) {
      this.entityId = entity.getId();
      this.action = ServerboundInteractPacket.Action.INTERACT;
      this.hand = hand;
   }

   public ServerboundInteractPacket(Entity entity, InteractionHand hand, Vec3 location) {
      this.entityId = entity.getId();
      this.action = ServerboundInteractPacket.Action.INTERACT_AT;
      this.hand = hand;
      this.location = location;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.entityId = friendlyByteBuf.readVarInt();
      this.action = (ServerboundInteractPacket.Action)friendlyByteBuf.readEnum(ServerboundInteractPacket.Action.class);
      if(this.action == ServerboundInteractPacket.Action.INTERACT_AT) {
         this.location = new Vec3((double)friendlyByteBuf.readFloat(), (double)friendlyByteBuf.readFloat(), (double)friendlyByteBuf.readFloat());
      }

      if(this.action == ServerboundInteractPacket.Action.INTERACT || this.action == ServerboundInteractPacket.Action.INTERACT_AT) {
         this.hand = (InteractionHand)friendlyByteBuf.readEnum(InteractionHand.class);
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.entityId);
      friendlyByteBuf.writeEnum(this.action);
      if(this.action == ServerboundInteractPacket.Action.INTERACT_AT) {
         friendlyByteBuf.writeFloat((float)this.location.x);
         friendlyByteBuf.writeFloat((float)this.location.y);
         friendlyByteBuf.writeFloat((float)this.location.z);
      }

      if(this.action == ServerboundInteractPacket.Action.INTERACT || this.action == ServerboundInteractPacket.Action.INTERACT_AT) {
         friendlyByteBuf.writeEnum(this.hand);
      }

   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleInteract(this);
   }

   @Nullable
   public Entity getTarget(Level level) {
      return level.getEntity(this.entityId);
   }

   public ServerboundInteractPacket.Action getAction() {
      return this.action;
   }

   public InteractionHand getHand() {
      return this.hand;
   }

   public Vec3 getLocation() {
      return this.location;
   }

   public static enum Action {
      INTERACT,
      ATTACK,
      INTERACT_AT;
   }
}
