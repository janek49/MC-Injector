package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;

public class ClientboundSetPlayerTeamPacket implements Packet {
   private String name = "";
   private Component displayName = new TextComponent("");
   private Component playerPrefix = new TextComponent("");
   private Component playerSuffix = new TextComponent("");
   private String nametagVisibility;
   private String collisionRule;
   private ChatFormatting color;
   private final Collection players;
   private int method;
   private int options;

   public ClientboundSetPlayerTeamPacket() {
      this.nametagVisibility = Team.Visibility.ALWAYS.name;
      this.collisionRule = Team.CollisionRule.ALWAYS.name;
      this.color = ChatFormatting.RESET;
      this.players = Lists.newArrayList();
   }

   public ClientboundSetPlayerTeamPacket(PlayerTeam playerTeam, int method) {
      this.nametagVisibility = Team.Visibility.ALWAYS.name;
      this.collisionRule = Team.CollisionRule.ALWAYS.name;
      this.color = ChatFormatting.RESET;
      this.players = Lists.newArrayList();
      this.name = playerTeam.getName();
      this.method = method;
      if(method == 0 || method == 2) {
         this.displayName = playerTeam.getDisplayName();
         this.options = playerTeam.packOptions();
         this.nametagVisibility = playerTeam.getNameTagVisibility().name;
         this.collisionRule = playerTeam.getCollisionRule().name;
         this.color = playerTeam.getColor();
         this.playerPrefix = playerTeam.getPlayerPrefix();
         this.playerSuffix = playerTeam.getPlayerSuffix();
      }

      if(method == 0) {
         this.players.addAll(playerTeam.getPlayers());
      }

   }

   public ClientboundSetPlayerTeamPacket(PlayerTeam playerTeam, Collection collection, int method) {
      this.nametagVisibility = Team.Visibility.ALWAYS.name;
      this.collisionRule = Team.CollisionRule.ALWAYS.name;
      this.color = ChatFormatting.RESET;
      this.players = Lists.newArrayList();
      if(method != 3 && method != 4) {
         throw new IllegalArgumentException("Method must be join or leave for player constructor");
      } else if(collection != null && !collection.isEmpty()) {
         this.method = method;
         this.name = playerTeam.getName();
         this.players.addAll(collection);
      } else {
         throw new IllegalArgumentException("Players cannot be null/empty");
      }
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.name = friendlyByteBuf.readUtf(16);
      this.method = friendlyByteBuf.readByte();
      if(this.method == 0 || this.method == 2) {
         this.displayName = friendlyByteBuf.readComponent();
         this.options = friendlyByteBuf.readByte();
         this.nametagVisibility = friendlyByteBuf.readUtf(40);
         this.collisionRule = friendlyByteBuf.readUtf(40);
         this.color = (ChatFormatting)friendlyByteBuf.readEnum(ChatFormatting.class);
         this.playerPrefix = friendlyByteBuf.readComponent();
         this.playerSuffix = friendlyByteBuf.readComponent();
      }

      if(this.method == 0 || this.method == 3 || this.method == 4) {
         int var2 = friendlyByteBuf.readVarInt();

         for(int var3 = 0; var3 < var2; ++var3) {
            this.players.add(friendlyByteBuf.readUtf(40));
         }
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeUtf(this.name);
      friendlyByteBuf.writeByte(this.method);
      if(this.method == 0 || this.method == 2) {
         friendlyByteBuf.writeComponent(this.displayName);
         friendlyByteBuf.writeByte(this.options);
         friendlyByteBuf.writeUtf(this.nametagVisibility);
         friendlyByteBuf.writeUtf(this.collisionRule);
         friendlyByteBuf.writeEnum(this.color);
         friendlyByteBuf.writeComponent(this.playerPrefix);
         friendlyByteBuf.writeComponent(this.playerSuffix);
      }

      if(this.method == 0 || this.method == 3 || this.method == 4) {
         friendlyByteBuf.writeVarInt(this.players.size());

         for(String var3 : this.players) {
            friendlyByteBuf.writeUtf(var3);
         }
      }

   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleSetPlayerTeamPacket(this);
   }

   public String getName() {
      return this.name;
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public Collection getPlayers() {
      return this.players;
   }

   public int getMethod() {
      return this.method;
   }

   public int getOptions() {
      return this.options;
   }

   public ChatFormatting getColor() {
      return this.color;
   }

   public String getNametagVisibility() {
      return this.nametagVisibility;
   }

   public String getCollisionRule() {
      return this.collisionRule;
   }

   public Component getPlayerPrefix() {
      return this.playerPrefix;
   }

   public Component getPlayerSuffix() {
      return this.playerSuffix;
   }
}
