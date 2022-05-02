package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;

public class ServerboundUseItemOnPacket implements Packet {
   private BlockHitResult blockHit;
   private InteractionHand hand;

   public ServerboundUseItemOnPacket() {
   }

   public ServerboundUseItemOnPacket(InteractionHand hand, BlockHitResult blockHit) {
      this.hand = hand;
      this.blockHit = blockHit;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.hand = (InteractionHand)friendlyByteBuf.readEnum(InteractionHand.class);
      this.blockHit = friendlyByteBuf.readBlockHitResult();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeEnum(this.hand);
      friendlyByteBuf.writeBlockHitResult(this.blockHit);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleUseItemOn(this);
   }

   public InteractionHand getHand() {
      return this.hand;
   }

   public BlockHitResult getHitResult() {
      return this.blockHit;
   }
}
