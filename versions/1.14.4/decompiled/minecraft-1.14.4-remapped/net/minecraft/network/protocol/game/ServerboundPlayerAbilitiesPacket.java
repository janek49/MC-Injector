package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.world.entity.player.Abilities;

public class ServerboundPlayerAbilitiesPacket implements Packet {
   private boolean invulnerable;
   private boolean isFlying;
   private boolean canFly;
   private boolean instabuild;
   private float flyingSpeed;
   private float walkingSpeed;

   public ServerboundPlayerAbilitiesPacket() {
   }

   public ServerboundPlayerAbilitiesPacket(Abilities abilities) {
      this.setInvulnerable(abilities.invulnerable);
      this.setFlying(abilities.flying);
      this.setCanFly(abilities.mayfly);
      this.setInstabuild(abilities.instabuild);
      this.setFlyingSpeed(abilities.getFlyingSpeed());
      this.setWalkingSpeed(abilities.getWalkingSpeed());
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      byte var2 = friendlyByteBuf.readByte();
      this.setInvulnerable((var2 & 1) > 0);
      this.setFlying((var2 & 2) > 0);
      this.setCanFly((var2 & 4) > 0);
      this.setInstabuild((var2 & 8) > 0);
      this.setFlyingSpeed(friendlyByteBuf.readFloat());
      this.setWalkingSpeed(friendlyByteBuf.readFloat());
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      byte var2 = 0;
      if(this.isInvulnerable()) {
         var2 = (byte)(var2 | 1);
      }

      if(this.isFlying()) {
         var2 = (byte)(var2 | 2);
      }

      if(this.canFly()) {
         var2 = (byte)(var2 | 4);
      }

      if(this.canInstabuild()) {
         var2 = (byte)(var2 | 8);
      }

      friendlyByteBuf.writeByte(var2);
      friendlyByteBuf.writeFloat(this.flyingSpeed);
      friendlyByteBuf.writeFloat(this.walkingSpeed);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handlePlayerAbilities(this);
   }

   public boolean isInvulnerable() {
      return this.invulnerable;
   }

   public void setInvulnerable(boolean invulnerable) {
      this.invulnerable = invulnerable;
   }

   public boolean isFlying() {
      return this.isFlying;
   }

   public void setFlying(boolean flying) {
      this.isFlying = flying;
   }

   public boolean canFly() {
      return this.canFly;
   }

   public void setCanFly(boolean canFly) {
      this.canFly = canFly;
   }

   public boolean canInstabuild() {
      return this.instabuild;
   }

   public void setInstabuild(boolean instabuild) {
      this.instabuild = instabuild;
   }

   public void setFlyingSpeed(float flyingSpeed) {
      this.flyingSpeed = flyingSpeed;
   }

   public void setWalkingSpeed(float walkingSpeed) {
      this.walkingSpeed = walkingSpeed;
   }
}
