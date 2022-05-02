package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundCommandSuggestionsPacket implements Packet {
   private int id;
   private Suggestions suggestions;

   public ClientboundCommandSuggestionsPacket() {
   }

   public ClientboundCommandSuggestionsPacket(int id, Suggestions suggestions) {
      this.id = id;
      this.suggestions = suggestions;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.id = friendlyByteBuf.readVarInt();
      int var2 = friendlyByteBuf.readVarInt();
      int var3 = friendlyByteBuf.readVarInt();
      StringRange var4 = StringRange.between(var2, var2 + var3);
      int var5 = friendlyByteBuf.readVarInt();
      List<Suggestion> var6 = Lists.newArrayListWithCapacity(var5);

      for(int var7 = 0; var7 < var5; ++var7) {
         String var8 = friendlyByteBuf.readUtf(32767);
         Component var9 = friendlyByteBuf.readBoolean()?friendlyByteBuf.readComponent():null;
         var6.add(new Suggestion(var4, var8, var9));
      }

      this.suggestions = new Suggestions(var4, var6);
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.id);
      friendlyByteBuf.writeVarInt(this.suggestions.getRange().getStart());
      friendlyByteBuf.writeVarInt(this.suggestions.getRange().getLength());
      friendlyByteBuf.writeVarInt(this.suggestions.getList().size());

      for(Suggestion var3 : this.suggestions.getList()) {
         friendlyByteBuf.writeUtf(var3.getText());
         friendlyByteBuf.writeBoolean(var3.getTooltip() != null);
         if(var3.getTooltip() != null) {
            friendlyByteBuf.writeComponent(ComponentUtils.fromMessage(var3.getTooltip()));
         }
      }

   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleCommandSuggestions(this);
   }

   public int getId() {
      return this.id;
   }

   public Suggestions getSuggestions() {
      return this.suggestions;
   }
}
