package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class ClientboundCustomSoundPacket implements Packet {
   private ResourceLocation name;
   private SoundSource source;
   private int x;
   private int y = Integer.MAX_VALUE;
   private int z;
   private float volume;
   private float pitch;

   public ClientboundCustomSoundPacket() {
   }

   public ClientboundCustomSoundPacket(ResourceLocation name, SoundSource source, Vec3 vec3, float volume, float pitch) {
      this.name = name;
      this.source = source;
      this.x = (int)(vec3.x * 8.0D);
      this.y = (int)(vec3.y * 8.0D);
      this.z = (int)(vec3.z * 8.0D);
      this.volume = volume;
      this.pitch = pitch;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.name = friendlyByteBuf.readResourceLocation();
      this.source = (SoundSource)friendlyByteBuf.readEnum(SoundSource.class);
      this.x = friendlyByteBuf.readInt();
      this.y = friendlyByteBuf.readInt();
      this.z = friendlyByteBuf.readInt();
      this.volume = friendlyByteBuf.readFloat();
      this.pitch = friendlyByteBuf.readFloat();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeResourceLocation(this.name);
      friendlyByteBuf.writeEnum(this.source);
      friendlyByteBuf.writeInt(this.x);
      friendlyByteBuf.writeInt(this.y);
      friendlyByteBuf.writeInt(this.z);
      friendlyByteBuf.writeFloat(this.volume);
      friendlyByteBuf.writeFloat(this.pitch);
   }

   public ResourceLocation getName() {
      return this.name;
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
      clientGamePacketListener.handleCustomSoundEvent(this);
   }
}
