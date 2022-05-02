package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class ServerboundEditBookPacket implements Packet {
   private ItemStack book;
   private boolean signing;
   private InteractionHand hand;

   public ServerboundEditBookPacket() {
   }

   public ServerboundEditBookPacket(ItemStack itemStack, boolean signing, InteractionHand hand) {
      this.book = itemStack.copy();
      this.signing = signing;
      this.hand = hand;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.book = friendlyByteBuf.readItem();
      this.signing = friendlyByteBuf.readBoolean();
      this.hand = (InteractionHand)friendlyByteBuf.readEnum(InteractionHand.class);
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeItem(this.book);
      friendlyByteBuf.writeBoolean(this.signing);
      friendlyByteBuf.writeEnum(this.hand);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleEditBook(this);
   }

   public ItemStack getBook() {
      return this.book;
   }

   public boolean isSigning() {
      return this.signing;
   }

   public InteractionHand getHand() {
      return this.hand;
   }
}
