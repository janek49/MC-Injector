package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.item.trading.MerchantOffers;

public class ClientboundMerchantOffersPacket implements Packet {
   private int containerId;
   private MerchantOffers offers;
   private int villagerLevel;
   private int villagerXp;
   private boolean showProgress;
   private boolean canRestock;

   public ClientboundMerchantOffersPacket() {
   }

   public ClientboundMerchantOffersPacket(int containerId, MerchantOffers offers, int villagerLevel, int villagerXp, boolean showProgress, boolean canRestock) {
      this.containerId = containerId;
      this.offers = offers;
      this.villagerLevel = villagerLevel;
      this.villagerXp = villagerXp;
      this.showProgress = showProgress;
      this.canRestock = canRestock;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.containerId = friendlyByteBuf.readVarInt();
      this.offers = MerchantOffers.createFromStream(friendlyByteBuf);
      this.villagerLevel = friendlyByteBuf.readVarInt();
      this.villagerXp = friendlyByteBuf.readVarInt();
      this.showProgress = friendlyByteBuf.readBoolean();
      this.canRestock = friendlyByteBuf.readBoolean();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.containerId);
      this.offers.writeToStream(friendlyByteBuf);
      friendlyByteBuf.writeVarInt(this.villagerLevel);
      friendlyByteBuf.writeVarInt(this.villagerXp);
      friendlyByteBuf.writeBoolean(this.showProgress);
      friendlyByteBuf.writeBoolean(this.canRestock);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleMerchantOffers(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public MerchantOffers getOffers() {
      return this.offers;
   }

   public int getVillagerLevel() {
      return this.villagerLevel;
   }

   public int getVillagerXp() {
      return this.villagerXp;
   }

   public boolean showProgress() {
      return this.showProgress;
   }

   public boolean canRestock() {
      return this.canRestock;
   }
}
