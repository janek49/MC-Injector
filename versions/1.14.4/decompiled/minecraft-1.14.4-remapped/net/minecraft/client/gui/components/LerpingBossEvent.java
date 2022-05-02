package net.minecraft.client.gui.components;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.Util;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

@ClientJarOnly
public class LerpingBossEvent extends BossEvent {
   protected float targetPercent;
   protected long setTime;

   public LerpingBossEvent(ClientboundBossEventPacket clientboundBossEventPacket) {
      super(clientboundBossEventPacket.getId(), clientboundBossEventPacket.getName(), clientboundBossEventPacket.getColor(), clientboundBossEventPacket.getOverlay());
      this.targetPercent = clientboundBossEventPacket.getPercent();
      this.percent = clientboundBossEventPacket.getPercent();
      this.setTime = Util.getMillis();
      this.setDarkenScreen(clientboundBossEventPacket.shouldDarkenScreen());
      this.setPlayBossMusic(clientboundBossEventPacket.shouldPlayMusic());
      this.setCreateWorldFog(clientboundBossEventPacket.shouldCreateWorldFog());
   }

   public void setPercent(float percent) {
      this.percent = this.getPercent();
      this.targetPercent = percent;
      this.setTime = Util.getMillis();
   }

   public float getPercent() {
      long var1 = Util.getMillis() - this.setTime;
      float var3 = Mth.clamp((float)var1 / 100.0F, 0.0F, 1.0F);
      return Mth.lerp(var3, this.percent, this.targetPercent);
   }

   public void update(ClientboundBossEventPacket clientboundBossEventPacket) {
      switch(clientboundBossEventPacket.getOperation()) {
      case UPDATE_NAME:
         this.setName(clientboundBossEventPacket.getName());
         break;
      case UPDATE_PCT:
         this.setPercent(clientboundBossEventPacket.getPercent());
         break;
      case UPDATE_STYLE:
         this.setColor(clientboundBossEventPacket.getColor());
         this.setOverlay(clientboundBossEventPacket.getOverlay());
         break;
      case UPDATE_PROPERTIES:
         this.setDarkenScreen(clientboundBossEventPacket.shouldDarkenScreen());
         this.setPlayBossMusic(clientboundBossEventPacket.shouldPlayMusic());
      }

   }
}
