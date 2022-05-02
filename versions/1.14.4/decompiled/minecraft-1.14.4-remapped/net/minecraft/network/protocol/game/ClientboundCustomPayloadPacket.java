package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;

public class ClientboundCustomPayloadPacket implements Packet {
   public static final ResourceLocation BRAND = new ResourceLocation("brand");
   public static final ResourceLocation DEBUG_PATHFINDING_PACKET = new ResourceLocation("debug/path");
   public static final ResourceLocation DEBUG_NEIGHBORSUPDATE_PACKET = new ResourceLocation("debug/neighbors_update");
   public static final ResourceLocation DEBUG_CAVES_PACKET = new ResourceLocation("debug/caves");
   public static final ResourceLocation DEBUG_STRUCTURES_PACKET = new ResourceLocation("debug/structures");
   public static final ResourceLocation DEBUG_WORLDGENATTEMPT_PACKET = new ResourceLocation("debug/worldgen_attempt");
   public static final ResourceLocation DEBUG_POI_TICKET_COUNT_PACKET = new ResourceLocation("debug/poi_ticket_count");
   public static final ResourceLocation DEBUG_POI_ADDED_PACKET = new ResourceLocation("debug/poi_added");
   public static final ResourceLocation DEBUG_POI_REMOVED_PACKET = new ResourceLocation("debug/poi_removed");
   public static final ResourceLocation DEBUG_VILLAGE_SECTIONS = new ResourceLocation("debug/village_sections");
   public static final ResourceLocation DEBUG_GOAL_SELECTOR = new ResourceLocation("debug/goal_selector");
   public static final ResourceLocation DEBUG_BRAIN = new ResourceLocation("debug/brain");
   public static final ResourceLocation DEBUG_RAIDS = new ResourceLocation("debug/raids");
   private ResourceLocation identifier;
   private FriendlyByteBuf data;

   public ClientboundCustomPayloadPacket() {
   }

   public ClientboundCustomPayloadPacket(ResourceLocation identifier, FriendlyByteBuf data) {
      this.identifier = identifier;
      this.data = data;
      if(data.writerIndex() > 1048576) {
         throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
      }
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.identifier = friendlyByteBuf.readResourceLocation();
      int var2 = friendlyByteBuf.readableBytes();
      if(var2 >= 0 && var2 <= 1048576) {
         this.data = new FriendlyByteBuf(friendlyByteBuf.readBytes(var2));
      } else {
         throw new IOException("Payload may not be larger than 1048576 bytes");
      }
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeResourceLocation(this.identifier);
      friendlyByteBuf.writeBytes(this.data.copy());
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleCustomPayload(this);
   }

   public ResourceLocation getIdentifier() {
      return this.identifier;
   }

   public FriendlyByteBuf getData() {
      return new FriendlyByteBuf(this.data.copy());
   }
}
