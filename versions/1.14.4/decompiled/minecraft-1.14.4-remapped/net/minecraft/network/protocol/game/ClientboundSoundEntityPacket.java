package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.Validate;

public class ClientboundSoundEntityPacket implements Packet {
   private SoundEvent sound;
   private SoundSource source;
   private int id;
   private float volume;
   private float pitch;

   public ClientboundSoundEntityPacket() {
   }

   public ClientboundSoundEntityPacket(SoundEvent sound, SoundSource source, Entity entity, float volume, float pitch) {
      Validate.notNull(sound, "sound", new Object[0]);
      this.sound = sound;
      this.source = source;
      this.id = entity.getId();
      this.volume = volume;
      this.pitch = pitch;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.sound = (SoundEvent)Registry.SOUND_EVENT.byId(friendlyByteBuf.readVarInt());
      this.source = (SoundSource)friendlyByteBuf.readEnum(SoundSource.class);
      this.id = friendlyByteBuf.readVarInt();
      this.volume = friendlyByteBuf.readFloat();
      this.pitch = friendlyByteBuf.readFloat();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(Registry.SOUND_EVENT.getId(this.sound));
      friendlyByteBuf.writeEnum(this.source);
      friendlyByteBuf.writeVarInt(this.id);
      friendlyByteBuf.writeFloat(this.volume);
      friendlyByteBuf.writeFloat(this.pitch);
   }

   public SoundEvent getSound() {
      return this.sound;
   }

   public SoundSource getSource() {
      return this.source;
   }

   public int getId() {
      return this.id;
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleSoundEntityEvent(this);
   }
}
