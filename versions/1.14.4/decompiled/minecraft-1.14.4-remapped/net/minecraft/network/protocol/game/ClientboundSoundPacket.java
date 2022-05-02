package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.apache.commons.lang3.Validate;

public class ClientboundSoundPacket implements Packet {
   private SoundEvent sound;
   private SoundSource source;
   private int x;
   private int y;
   private int z;
   private float volume;
   private float pitch;

   public ClientboundSoundPacket() {
   }

   public ClientboundSoundPacket(SoundEvent sound, SoundSource source, double var3, double var5, double var7, float volume, float pitch) {
      Validate.notNull(sound, "sound", new Object[0]);
      this.sound = sound;
      this.source = source;
      this.x = (int)(var3 * 8.0D);
      this.y = (int)(var5 * 8.0D);
      this.z = (int)(var7 * 8.0D);
      this.volume = volume;
      this.pitch = pitch;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.sound = (SoundEvent)Registry.SOUND_EVENT.byId(friendlyByteBuf.readVarInt());
      this.source = (SoundSource)friendlyByteBuf.readEnum(SoundSource.class);
      this.x = friendlyByteBuf.readInt();
      this.y = friendlyByteBuf.readInt();
      this.z = friendlyByteBuf.readInt();
      this.volume = friendlyByteBuf.readFloat();
      this.pitch = friendlyByteBuf.readFloat();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(Registry.SOUND_EVENT.getId(this.sound));
      friendlyByteBuf.writeEnum(this.source);
      friendlyByteBuf.writeInt(this.x);
      friendlyByteBuf.writeInt(this.y);
      friendlyByteBuf.writeInt(this.z);
      friendlyByteBuf.writeFloat(this.volume);
      friendlyByteBuf.writeFloat(this.pitch);
   }

   public SoundEvent getSound() {
      return this.sound;
   }

   public SoundSource getSource() {
      return this.source;
   }

   public double getX() {
      return (double)((float)this.x / 8.0F);
   }

   public double getY() {
      return (double)((float)this.y / 8.0F);
   }

   public double getZ() {
      return (double)((float)this.z / 8.0F);
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleSoundEvent(this);
   }
}
