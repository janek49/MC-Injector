package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.IOException;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;

public class ClientboundAwardStatsPacket implements Packet {
   private Object2IntMap stats;

   public ClientboundAwardStatsPacket() {
   }

   public ClientboundAwardStatsPacket(Object2IntMap stats) {
      this.stats = stats;
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleAwardStats(this);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      int var2 = friendlyByteBuf.readVarInt();
      this.stats = new Object2IntOpenHashMap(var2);

      for(int var3 = 0; var3 < var2; ++var3) {
         this.readStat((StatType)Registry.STAT_TYPE.byId(friendlyByteBuf.readVarInt()), friendlyByteBuf);
      }

   }

   private void readStat(StatType statType, FriendlyByteBuf friendlyByteBuf) {
      int var3 = friendlyByteBuf.readVarInt();
      int var4 = friendlyByteBuf.readVarInt();
      this.stats.put(statType.get(statType.getRegistry().byId(var3)), var4);
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.stats.size());
      ObjectIterator var2 = this.stats.object2IntEntrySet().iterator();

      while(var2.hasNext()) {
         Entry<Stat<?>> var3 = (Entry)var2.next();
         Stat<?> var4 = (Stat)var3.getKey();
         friendlyByteBuf.writeVarInt(Registry.STAT_TYPE.getId(var4.getType()));
         friendlyByteBuf.writeVarInt(this.getId(var4));
         friendlyByteBuf.writeVarInt(var3.getIntValue());
      }

   }

   private int getId(Stat stat) {
      return stat.getType().getRegistry().getId(stat.getValue());
   }

   public Map getStats() {
      return this.stats;
   }
}
