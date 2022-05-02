package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundEntityTagQuery;

@ClientJarOnly
public class DebugQueryHandler {
   private final ClientPacketListener connection;
   private int transactionId = -1;
   @Nullable
   private Consumer callback;

   public DebugQueryHandler(ClientPacketListener connection) {
      this.connection = connection;
   }

   public boolean handleResponse(int var1, @Nullable CompoundTag compoundTag) {
      if(this.transactionId == var1 && this.callback != null) {
         this.callback.accept(compoundTag);
         this.callback = null;
         return true;
      } else {
         return false;
      }
   }

   private int startTransaction(Consumer callback) {
      this.callback = callback;
      return ++this.transactionId;
   }

   public void queryEntityTag(int var1, Consumer consumer) {
      int var3 = this.startTransaction(consumer);
      this.connection.send((Packet)(new ServerboundEntityTagQuery(var3, var1)));
   }

   public void queryBlockEntityTag(BlockPos blockPos, Consumer consumer) {
      int var3 = this.startTransaction(consumer);
      this.connection.send((Packet)(new ServerboundBlockEntityTagQuery(var3, blockPos)));
   }
}
