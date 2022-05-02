package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class ClientboundUpdateAttributesPacket implements Packet {
   private int entityId;
   private final List attributes = Lists.newArrayList();

   public ClientboundUpdateAttributesPacket() {
   }

   public ClientboundUpdateAttributesPacket(int entityId, Collection collection) {
      this.entityId = entityId;

      for(AttributeInstance var4 : collection) {
         this.attributes.add(new ClientboundUpdateAttributesPacket.AttributeSnapshot(var4.getAttribute().getName(), var4.getBaseValue(), var4.getModifiers()));
      }

   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.entityId = friendlyByteBuf.readVarInt();
      int var2 = friendlyByteBuf.readInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = friendlyByteBuf.readUtf(64);
         double var5 = friendlyByteBuf.readDouble();
         List<AttributeModifier> var7 = Lists.newArrayList();
         int var8 = friendlyByteBuf.readVarInt();

         for(int var9 = 0; var9 < var8; ++var9) {
            UUID var10 = friendlyByteBuf.readUUID();
            var7.add(new AttributeModifier(var10, "Unknown synced attribute modifier", friendlyByteBuf.readDouble(), AttributeModifier.Operation.fromValue(friendlyByteBuf.readByte())));
         }

         this.attributes.add(new ClientboundUpdateAttributesPacket.AttributeSnapshot(var4, var5, var7));
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.entityId);
      friendlyByteBuf.writeInt(this.attributes.size());

      for(ClientboundUpdateAttributesPacket.AttributeSnapshot var3 : this.attributes) {
         friendlyByteBuf.writeUtf(var3.getName());
         friendlyByteBuf.writeDouble(var3.getBase());
         friendlyByteBuf.writeVarInt(var3.getModifiers().size());

         for(AttributeModifier var5 : var3.getModifiers()) {
            friendlyByteBuf.writeUUID(var5.getId());
            friendlyByteBuf.writeDouble(var5.getAmount());
            friendlyByteBuf.writeByte(var5.getOperation().toValue());
         }
      }

   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleUpdateAttributes(this);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public List getValues() {
      return this.attributes;
   }

   public class AttributeSnapshot {
      private final String name;
      private final double base;
      private final Collection modifiers;

      public AttributeSnapshot(String name, double base, Collection modifiers) {
         this.name = name;
         this.base = base;
         this.modifiers = modifiers;
      }

      public String getName() {
         return this.name;
      }

      public double getBase() {
         return this.base;
      }

      public Collection getModifiers() {
         return this.modifiers;
      }
   }
}
