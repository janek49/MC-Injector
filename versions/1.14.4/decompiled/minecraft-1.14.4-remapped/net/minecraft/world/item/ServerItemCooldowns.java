package net.minecraft.world.item;

import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;

public class ServerItemCooldowns extends ItemCooldowns {
   private final ServerPlayer player;

   public ServerItemCooldowns(ServerPlayer player) {
      this.player = player;
   }

   protected void onCooldownStarted(Item item, int var2) {
      super.onCooldownStarted(item, var2);
      this.player.connection.send(new ClientboundCooldownPacket(item, var2));
   }

   protected void onCooldownEnded(Item item) {
      super.onCooldownEnded(item);
      this.player.connection.send(new ClientboundCooldownPacket(item, 0));
   }
}
