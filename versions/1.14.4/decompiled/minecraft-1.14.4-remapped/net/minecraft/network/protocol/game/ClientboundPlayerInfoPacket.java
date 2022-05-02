package net.minecraft.network.protocol.game;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class ClientboundPlayerInfoPacket implements Packet {
   private ClientboundPlayerInfoPacket.Action action;
   private final List entries = Lists.newArrayList();

   public ClientboundPlayerInfoPacket() {
   }

   public ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action action, ServerPlayer... serverPlayers) {
      this.action = action;

      for(ServerPlayer var6 : serverPlayers) {
         this.entries.add(new ClientboundPlayerInfoPacket.PlayerUpdate(var6.getGameProfile(), var6.latency, var6.gameMode.getGameModeForPlayer(), var6.getTabListDisplayName()));
      }

   }

   public ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action action, Iterable iterable) {
      this.action = action;

      for(ServerPlayer var4 : iterable) {
         this.entries.add(new ClientboundPlayerInfoPacket.PlayerUpdate(var4.getGameProfile(), var4.latency, var4.gameMode.getGameModeForPlayer(), var4.getTabListDisplayName()));
      }

   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.action = (ClientboundPlayerInfoPacket.Action)friendlyByteBuf.readEnum(ClientboundPlayerInfoPacket.Action.class);
      int var2 = friendlyByteBuf.readVarInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         GameProfile var4 = null;
         int var5 = 0;
         GameType var6 = null;
         Component var7 = null;
         switch(this.action) {
         case ADD_PLAYER:
            var4 = new GameProfile(friendlyByteBuf.readUUID(), friendlyByteBuf.readUtf(16));
            int var8 = friendlyByteBuf.readVarInt();
            int var9 = 0;

            for(; var9 < var8; ++var9) {
               String var10 = friendlyByteBuf.readUtf(32767);
               String var11 = friendlyByteBuf.readUtf(32767);
               if(friendlyByteBuf.readBoolean()) {
                  var4.getProperties().put(var10, new Property(var10, var11, friendlyByteBuf.readUtf(32767)));
               } else {
                  var4.getProperties().put(var10, new Property(var10, var11));
               }
            }

            var6 = GameType.byId(friendlyByteBuf.readVarInt());
            var5 = friendlyByteBuf.readVarInt();
            if(friendlyByteBuf.readBoolean()) {
               var7 = friendlyByteBuf.readComponent();
            }
            break;
         case UPDATE_GAME_MODE:
            var4 = new GameProfile(friendlyByteBuf.readUUID(), (String)null);
            var6 = GameType.byId(friendlyByteBuf.readVarInt());
            break;
         case UPDATE_LATENCY:
            var4 = new GameProfile(friendlyByteBuf.readUUID(), (String)null);
            var5 = friendlyByteBuf.readVarInt();
            break;
         case UPDATE_DISPLAY_NAME:
            var4 = new GameProfile(friendlyByteBuf.readUUID(), (String)null);
            if(friendlyByteBuf.readBoolean()) {
               var7 = friendlyByteBuf.readComponent();
            }
            break;
         case REMOVE_PLAYER:
            var4 = new GameProfile(friendlyByteBuf.readUUID(), (String)null);
         }

         this.entries.add(new ClientboundPlayerInfoPacket.PlayerUpdate(var4, var5, var6, var7));
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeEnum(this.action);
      friendlyByteBuf.writeVarInt(this.entries.size());

      for(ClientboundPlayerInfoPacket.PlayerUpdate var3 : this.entries) {
         switch(this.action) {
         case ADD_PLAYER:
            friendlyByteBuf.writeUUID(var3.getProfile().getId());
            friendlyByteBuf.writeUtf(var3.getProfile().getName());
            friendlyByteBuf.writeVarInt(var3.getProfile().getProperties().size());

            for(Property var5 : var3.getProfile().getProperties().values()) {
               friendlyByteBuf.writeUtf(var5.getName());
               friendlyByteBuf.writeUtf(var5.getValue());
               if(var5.hasSignature()) {
                  friendlyByteBuf.writeBoolean(true);
                  friendlyByteBuf.writeUtf(var5.getSignature());
               } else {
                  friendlyByteBuf.writeBoolean(false);
               }
            }

            friendlyByteBuf.writeVarInt(var3.getGameMode().getId());
            friendlyByteBuf.writeVarInt(var3.getLatency());
            if(var3.getDisplayName() == null) {
               friendlyByteBuf.writeBoolean(false);
            } else {
               friendlyByteBuf.writeBoolean(true);
               friendlyByteBuf.writeComponent(var3.getDisplayName());
            }
            break;
         case UPDATE_GAME_MODE:
            friendlyByteBuf.writeUUID(var3.getProfile().getId());
            friendlyByteBuf.writeVarInt(var3.getGameMode().getId());
            break;
         case UPDATE_LATENCY:
            friendlyByteBuf.writeUUID(var3.getProfile().getId());
            friendlyByteBuf.writeVarInt(var3.getLatency());
            break;
         case UPDATE_DISPLAY_NAME:
            friendlyByteBuf.writeUUID(var3.getProfile().getId());
            if(var3.getDisplayName() == null) {
               friendlyByteBuf.writeBoolean(false);
            } else {
               friendlyByteBuf.writeBoolean(true);
               friendlyByteBuf.writeComponent(var3.getDisplayName());
            }
            break;
         case REMOVE_PLAYER:
            friendlyByteBuf.writeUUID(var3.getProfile().getId());
         }
      }

   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handlePlayerInfo(this);
   }

   public List getEntries() {
      return this.entries;
   }

   public ClientboundPlayerInfoPacket.Action getAction() {
      return this.action;
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("action", this.action).add("entries", this.entries).toString();
   }

   public static enum Action {
      ADD_PLAYER,
      UPDATE_GAME_MODE,
      UPDATE_LATENCY,
      UPDATE_DISPLAY_NAME,
      REMOVE_PLAYER;
   }

   public class PlayerUpdate {
      private final int latency;
      private final GameType gameMode;
      private final GameProfile profile;
      private final Component displayName;

      public PlayerUpdate(GameProfile profile, int latency, GameType gameMode, @Nullable Component displayName) {
         this.profile = profile;
         this.latency = latency;
         this.gameMode = gameMode;
         this.displayName = displayName;
      }

      public GameProfile getProfile() {
         return this.profile;
      }

      public int getLatency() {
         return this.latency;
      }

      public GameType getGameMode() {
         return this.gameMode;
      }

      @Nullable
      public Component getDisplayName() {
         return this.displayName;
      }

      public String toString() {
         return MoreObjects.toStringHelper(this).add("latency", this.latency).add("gameMode", this.gameMode).add("profile", this.profile).add("displayName", this.displayName == null?null:Component.Serializer.toJson(this.displayName)).toString();
      }
   }
}
