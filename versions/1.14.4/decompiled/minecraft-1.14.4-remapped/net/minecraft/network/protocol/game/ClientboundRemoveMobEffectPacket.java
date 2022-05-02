package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundRemoveMobEffectPacket implements Packet {
   private int entityId;
   private MobEffect effect;

   public ClientboundRemoveMobEffectPacket() {
   }

   public ClientboundRemoveMobEffectPacket(int entityId, MobEffect effect) {
      this.entityId = entityId;
      this.effect = effect;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.entityId = friendlyByteBuf.readVarInt();
      this.effect = MobEffect.byId(friendlyByteBuf.readUnsignedByte());
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.entityId);
      friendlyByteBuf.writeByte(MobEffect.getId(this.effect));
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleRemoveMobEffect(this);
   }

   @Nullable
   public Entity getEntity(Level level) {
      return level.getEntity(this.entityId);
   }

   @Nullable
   public MobEffect getEffect() {
      return this.effect;
   }
}
