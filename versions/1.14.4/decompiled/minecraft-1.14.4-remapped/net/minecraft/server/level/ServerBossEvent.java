package net.minecraft.server.level;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

public class ServerBossEvent extends BossEvent {
   private final Set players = Sets.newHashSet();
   private final Set unmodifiablePlayers;
   private boolean visible;

   public ServerBossEvent(Component component, BossEvent.BossBarColor bossEvent$BossBarColor, BossEvent.BossBarOverlay bossEvent$BossBarOverlay) {
      super(Mth.createInsecureUUID(), component, bossEvent$BossBarColor, bossEvent$BossBarOverlay);
      this.unmodifiablePlayers = Collections.unmodifiableSet(this.players);
      this.visible = true;
   }

   public void setPercent(float percent) {
      if(percent != this.percent) {
         super.setPercent(percent);
         this.broadcast(ClientboundBossEventPacket.Operation.UPDATE_PCT);
      }

   }

   public void setColor(BossEvent.BossBarColor color) {
      if(color != this.color) {
         super.setColor(color);
         this.broadcast(ClientboundBossEventPacket.Operation.UPDATE_STYLE);
      }

   }

   public void setOverlay(BossEvent.BossBarOverlay overlay) {
      if(overlay != this.overlay) {
         super.setOverlay(overlay);
         this.broadcast(ClientboundBossEventPacket.Operation.UPDATE_STYLE);
      }

   }

   public BossEvent setDarkenScreen(boolean darkenScreen) {
      if(darkenScreen != this.darkenScreen) {
         super.setDarkenScreen(darkenScreen);
         this.broadcast(ClientboundBossEventPacket.Operation.UPDATE_PROPERTIES);
      }

      return this;
   }

   public BossEvent setPlayBossMusic(boolean playBossMusic) {
      if(playBossMusic != this.playBossMusic) {
         super.setPlayBossMusic(playBossMusic);
         this.broadcast(ClientboundBossEventPacket.Operation.UPDATE_PROPERTIES);
      }

      return this;
   }

   public BossEvent setCreateWorldFog(boolean createWorldFog) {
      if(createWorldFog != this.createWorldFog) {
         super.setCreateWorldFog(createWorldFog);
         this.broadcast(ClientboundBossEventPacket.Operation.UPDATE_PROPERTIES);
      }

      return this;
   }

   public void setName(Component name) {
      if(!Objects.equal(name, this.name)) {
         super.setName(name);
         this.broadcast(ClientboundBossEventPacket.Operation.UPDATE_NAME);
      }

   }

   private void broadcast(ClientboundBossEventPacket.Operation clientboundBossEventPacket$Operation) {
      if(this.visible) {
         ClientboundBossEventPacket var2 = new ClientboundBossEventPacket(clientboundBossEventPacket$Operation, this);

         for(ServerPlayer var4 : this.players) {
            var4.connection.send(var2);
         }
      }

   }

   public void addPlayer(ServerPlayer serverPlayer) {
      if(this.players.add(serverPlayer) && this.visible) {
         serverPlayer.connection.send(new ClientboundBossEventPacket(ClientboundBossEventPacket.Operation.ADD, this));
      }

   }

   public void removePlayer(ServerPlayer serverPlayer) {
      if(this.players.remove(serverPlayer) && this.visible) {
         serverPlayer.connection.send(new ClientboundBossEventPacket(ClientboundBossEventPacket.Operation.REMOVE, this));
      }

   }

   public void removeAllPlayers() {
      if(!this.players.isEmpty()) {
         for(ServerPlayer var2 : Lists.newArrayList(this.players)) {
            this.removePlayer(var2);
         }
      }

   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean visible) {
      if(visible != this.visible) {
         this.visible = visible;

         for(ServerPlayer var3 : this.players) {
            var3.connection.send(new ClientboundBossEventPacket(visible?ClientboundBossEventPacket.Operation.ADD:ClientboundBossEventPacket.Operation.REMOVE, this));
         }
      }

   }

   public Collection getPlayers() {
      return this.unmodifiablePlayers;
   }
}
