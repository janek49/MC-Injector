package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.entity.LivingEntity;

public class ClientboundPlayerCombatPacket implements Packet {
   public ClientboundPlayerCombatPacket.Event event;
   public int playerId;
   public int killerId;
   public int duration;
   public Component message;

   public ClientboundPlayerCombatPacket() {
   }

   public ClientboundPlayerCombatPacket(CombatTracker combatTracker, ClientboundPlayerCombatPacket.Event clientboundPlayerCombatPacket$Event) {
      this(combatTracker, clientboundPlayerCombatPacket$Event, new TextComponent(""));
   }

   public ClientboundPlayerCombatPacket(CombatTracker combatTracker, ClientboundPlayerCombatPacket.Event event, Component message) {
      this.event = event;
      LivingEntity var4 = combatTracker.getKiller();
      switch(event) {
      case END_COMBAT:
         this.duration = combatTracker.getCombatDuration();
         this.killerId = var4 == null?-1:var4.getId();
         break;
      case ENTITY_DIED:
         this.playerId = combatTracker.getMob().getId();
         this.killerId = var4 == null?-1:var4.getId();
         this.message = message;
      }

   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.event = (ClientboundPlayerCombatPacket.Event)friendlyByteBuf.readEnum(ClientboundPlayerCombatPacket.Event.class);
      if(this.event == ClientboundPlayerCombatPacket.Event.END_COMBAT) {
         this.duration = friendlyByteBuf.readVarInt();
         this.killerId = friendlyByteBuf.readInt();
      } else if(this.event == ClientboundPlayerCombatPacket.Event.ENTITY_DIED) {
         this.playerId = friendlyByteBuf.readVarInt();
         this.killerId = friendlyByteBuf.readInt();
         this.message = friendlyByteBuf.readComponent();
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeEnum(this.event);
      if(this.event == ClientboundPlayerCombatPacket.Event.END_COMBAT) {
         friendlyByteBuf.writeVarInt(this.duration);
         friendlyByteBuf.writeInt(this.killerId);
      } else if(this.event == ClientboundPlayerCombatPacket.Event.ENTITY_DIED) {
         friendlyByteBuf.writeVarInt(this.playerId);
         friendlyByteBuf.writeInt(this.killerId);
         friendlyByteBuf.writeComponent(this.message);
      }

   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handlePlayerCombat(this);
   }

   public boolean isSkippable() {
      return this.event == ClientboundPlayerCombatPacket.Event.ENTITY_DIED;
   }

   public static enum Event {
      ENTER_COMBAT,
      END_COMBAT,
      ENTITY_DIED;
   }
}
