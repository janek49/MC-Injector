package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundLevelParticlesPacket implements Packet {
   private float x;
   private float y;
   private float z;
   private float xDist;
   private float yDist;
   private float zDist;
   private float maxSpeed;
   private int count;
   private boolean overrideLimiter;
   private ParticleOptions particle;

   public ClientboundLevelParticlesPacket() {
   }

   public ClientboundLevelParticlesPacket(ParticleOptions particle, boolean overrideLimiter, float x, float y, float z, float xDist, float yDist, float zDist, float maxSpeed, int count) {
      this.particle = particle;
      this.overrideLimiter = overrideLimiter;
      this.x = x;
      this.y = y;
      this.z = z;
      this.xDist = xDist;
      this.yDist = yDist;
      this.zDist = zDist;
      this.maxSpeed = maxSpeed;
      this.count = count;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      ParticleType<?> var2 = (ParticleType)Registry.PARTICLE_TYPE.byId(friendlyByteBuf.readInt());
      if(var2 == null) {
         var2 = ParticleTypes.BARRIER;
      }

      this.overrideLimiter = friendlyByteBuf.readBoolean();
      this.x = friendlyByteBuf.readFloat();
      this.y = friendlyByteBuf.readFloat();
      this.z = friendlyByteBuf.readFloat();
      this.xDist = friendlyByteBuf.readFloat();
      this.yDist = friendlyByteBuf.readFloat();
      this.zDist = friendlyByteBuf.readFloat();
      this.maxSpeed = friendlyByteBuf.readFloat();
      this.count = friendlyByteBuf.readInt();
      this.particle = this.readParticle(friendlyByteBuf, var2);
   }

   private ParticleOptions readParticle(FriendlyByteBuf friendlyByteBuf, ParticleType particleType) {
      return particleType.getDeserializer().fromNetwork(particleType, friendlyByteBuf);
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeInt(Registry.PARTICLE_TYPE.getId(this.particle.getType()));
      friendlyByteBuf.writeBoolean(this.overrideLimiter);
      friendlyByteBuf.writeFloat(this.x);
      friendlyByteBuf.writeFloat(this.y);
      friendlyByteBuf.writeFloat(this.z);
      friendlyByteBuf.writeFloat(this.xDist);
      friendlyByteBuf.writeFloat(this.yDist);
      friendlyByteBuf.writeFloat(this.zDist);
      friendlyByteBuf.writeFloat(this.maxSpeed);
      friendlyByteBuf.writeInt(this.count);
      this.particle.writeToNetwork(friendlyByteBuf);
   }

   public boolean isOverrideLimiter() {
      return this.overrideLimiter;
   }

   public double getX() {
      return (double)this.x;
   }

   public double getY() {
      return (double)this.y;
   }

   public double getZ() {
      return (double)this.z;
   }

   public float getXDist() {
      return this.xDist;
   }

   public float getYDist() {
      return this.yDist;
   }

   public float getZDist() {
      return this.zDist;
   }

   public float getMaxSpeed() {
      return this.maxSpeed;
   }

   public int getCount() {
      return this.count;
   }

   public ParticleOptions getParticle() {
      return this.particle;
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleParticleEvent(this);
   }
}
