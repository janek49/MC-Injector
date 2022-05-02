package net.minecraft.server.level;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddMobPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquippedItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.ModifiableAttributeMap;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerEntity {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ServerLevel level;
   private final Entity entity;
   private final int updateInterval;
   private final boolean trackDelta;
   private final Consumer broadcast;
   private long xp;
   private long yp;
   private long zp;
   private int yRotp;
   private int xRotp;
   private int yHeadRotp;
   private Vec3 ap = Vec3.ZERO;
   private int tickCount;
   private int teleportDelay;
   private List lastPassengers = Collections.emptyList();
   private boolean wasRiding;
   private boolean wasOnGround;

   public ServerEntity(ServerLevel level, Entity entity, int updateInterval, boolean trackDelta, Consumer broadcast) {
      this.level = level;
      this.broadcast = broadcast;
      this.entity = entity;
      this.updateInterval = updateInterval;
      this.trackDelta = trackDelta;
      this.updateSentPos();
      this.yRotp = Mth.floor(entity.yRot * 256.0F / 360.0F);
      this.xRotp = Mth.floor(entity.xRot * 256.0F / 360.0F);
      this.yHeadRotp = Mth.floor(entity.getYHeadRot() * 256.0F / 360.0F);
      this.wasOnGround = entity.onGround;
   }

   public void sendChanges() {
      List<Entity> var1 = this.entity.getPassengers();
      if(!var1.equals(this.lastPassengers)) {
         this.lastPassengers = var1;
         this.broadcast.accept(new ClientboundSetPassengersPacket(this.entity));
      }

      if(this.entity instanceof ItemFrame && this.tickCount % 10 == 0) {
         ItemFrame var2 = (ItemFrame)this.entity;
         ItemStack var3 = var2.getItem();
         if(var3.getItem() instanceof MapItem) {
            MapItemSavedData var4 = MapItem.getOrCreateSavedData(var3, this.level);

            for(ServerPlayer var6 : this.level.players()) {
               var4.tickCarriedBy(var6, var3);
               Packet<?> var7 = ((MapItem)var3.getItem()).getUpdatePacket(var3, this.level, var6);
               if(var7 != null) {
                  var6.connection.send(var7);
               }
            }
         }

         this.sendDirtyEntityData();
      }

      if(this.tickCount % this.updateInterval == 0 || this.entity.hasImpulse || this.entity.getEntityData().isDirty()) {
         if(this.entity.isPassenger()) {
            int var2 = Mth.floor(this.entity.yRot * 256.0F / 360.0F);
            int var3 = Mth.floor(this.entity.xRot * 256.0F / 360.0F);
            boolean var4 = Math.abs(var2 - this.yRotp) >= 1 || Math.abs(var3 - this.xRotp) >= 1;
            if(var4) {
               this.broadcast.accept(new ClientboundMoveEntityPacket.Rot(this.entity.getId(), (byte)var2, (byte)var3, this.entity.onGround));
               this.yRotp = var2;
               this.xRotp = var3;
            }

            this.updateSentPos();
            this.sendDirtyEntityData();
            this.wasRiding = true;
         } else {
            ++this.teleportDelay;
            int var2 = Mth.floor(this.entity.yRot * 256.0F / 360.0F);
            int var3 = Mth.floor(this.entity.xRot * 256.0F / 360.0F);
            Vec3 var4 = (new Vec3(this.entity.x, this.entity.y, this.entity.z)).subtract(ClientboundMoveEntityPacket.packetToEntity(this.xp, this.yp, this.zp));
            boolean var5 = var4.lengthSqr() >= 7.62939453125E-6D;
            Packet<?> var6 = null;
            boolean var7 = var5 || this.tickCount % 60 == 0;
            boolean var8 = Math.abs(var2 - this.yRotp) >= 1 || Math.abs(var3 - this.xRotp) >= 1;
            if(this.tickCount > 0 || this.entity instanceof AbstractArrow) {
               long var9 = ClientboundMoveEntityPacket.entityToPacket(var4.x);
               long var11 = ClientboundMoveEntityPacket.entityToPacket(var4.y);
               long var13 = ClientboundMoveEntityPacket.entityToPacket(var4.z);
               boolean var15 = var9 < -32768L || var9 > 32767L || var11 < -32768L || var11 > 32767L || var13 < -32768L || var13 > 32767L;
               if(!var15 && this.teleportDelay <= 400 && !this.wasRiding && this.wasOnGround == this.entity.onGround) {
                  if((!var7 || !var8) && !(this.entity instanceof AbstractArrow)) {
                     if(var7) {
                        var6 = new ClientboundMoveEntityPacket.Pos(this.entity.getId(), (short)((int)var9), (short)((int)var11), (short)((int)var13), this.entity.onGround);
                     } else if(var8) {
                        var6 = new ClientboundMoveEntityPacket.Rot(this.entity.getId(), (byte)var2, (byte)var3, this.entity.onGround);
                     }
                  } else {
                     var6 = new ClientboundMoveEntityPacket.PosRot(this.entity.getId(), (short)((int)var9), (short)((int)var11), (short)((int)var13), (byte)var2, (byte)var3, this.entity.onGround);
                  }
               } else {
                  this.wasOnGround = this.entity.onGround;
                  this.teleportDelay = 0;
                  var6 = new ClientboundTeleportEntityPacket(this.entity);
               }
            }

            if((this.trackDelta || this.entity.hasImpulse || this.entity instanceof LivingEntity && ((LivingEntity)this.entity).isFallFlying()) && this.tickCount > 0) {
               Vec3 var9 = this.entity.getDeltaMovement();
               double var10 = var9.distanceToSqr(this.ap);
               if(var10 > 1.0E-7D || var10 > 0.0D && var9.lengthSqr() == 0.0D) {
                  this.ap = var9;
                  this.broadcast.accept(new ClientboundSetEntityMotionPacket(this.entity.getId(), this.ap));
               }
            }

            if(var6 != null) {
               this.broadcast.accept(var6);
            }

            this.sendDirtyEntityData();
            if(var7) {
               this.updateSentPos();
            }

            if(var8) {
               this.yRotp = var2;
               this.xRotp = var3;
            }

            this.wasRiding = false;
         }

         int var2 = Mth.floor(this.entity.getYHeadRot() * 256.0F / 360.0F);
         if(Math.abs(var2 - this.yHeadRotp) >= 1) {
            this.broadcast.accept(new ClientboundRotateHeadPacket(this.entity, (byte)var2));
            this.yHeadRotp = var2;
         }

         this.entity.hasImpulse = false;
      }

      ++this.tickCount;
      if(this.entity.hurtMarked) {
         this.broadcastAndSend(new ClientboundSetEntityMotionPacket(this.entity));
         this.entity.hurtMarked = false;
      }

   }

   public void removePairing(ServerPlayer serverPlayer) {
      this.entity.stopSeenByPlayer(serverPlayer);
      serverPlayer.sendRemoveEntity(this.entity);
   }

   public void addPairing(ServerPlayer serverPlayer) {
      ServerGamePacketListenerImpl var10001 = serverPlayer.connection;
      serverPlayer.connection.getClass();
      this.sendPairingData(var10001::send);
      this.entity.startSeenByPlayer(serverPlayer);
      serverPlayer.cancelRemoveEntity(this.entity);
   }

   public void sendPairingData(Consumer consumer) {
      if(this.entity.removed) {
         LOGGER.warn("Fetching packet for removed entity " + this.entity);
      }

      Packet<?> var2 = this.entity.getAddEntityPacket();
      this.yHeadRotp = Mth.floor(this.entity.getYHeadRot() * 256.0F / 360.0F);
      consumer.accept(var2);
      if(!this.entity.getEntityData().isEmpty()) {
         consumer.accept(new ClientboundSetEntityDataPacket(this.entity.getId(), this.entity.getEntityData(), true));
      }

      boolean var3 = this.trackDelta;
      if(this.entity instanceof LivingEntity) {
         ModifiableAttributeMap var4 = (ModifiableAttributeMap)((LivingEntity)this.entity).getAttributes();
         Collection<AttributeInstance> var5 = var4.getSyncableAttributes();
         if(!var5.isEmpty()) {
            consumer.accept(new ClientboundUpdateAttributesPacket(this.entity.getId(), var5));
         }

         if(((LivingEntity)this.entity).isFallFlying()) {
            var3 = true;
         }
      }

      this.ap = this.entity.getDeltaMovement();
      if(var3 && !(var2 instanceof ClientboundAddMobPacket)) {
         consumer.accept(new ClientboundSetEntityMotionPacket(this.entity.getId(), this.ap));
      }

      if(this.entity instanceof LivingEntity) {
         for(EquipmentSlot var7 : EquipmentSlot.values()) {
            ItemStack var8 = ((LivingEntity)this.entity).getItemBySlot(var7);
            if(!var8.isEmpty()) {
               consumer.accept(new ClientboundSetEquippedItemPacket(this.entity.getId(), var7, var8));
            }
         }
      }

      if(this.entity instanceof LivingEntity) {
         LivingEntity var4 = (LivingEntity)this.entity;

         for(MobEffectInstance var6 : var4.getActiveEffects()) {
            consumer.accept(new ClientboundUpdateMobEffectPacket(this.entity.getId(), var6));
         }
      }

      if(!this.entity.getPassengers().isEmpty()) {
         consumer.accept(new ClientboundSetPassengersPacket(this.entity));
      }

      if(this.entity.isPassenger()) {
         consumer.accept(new ClientboundSetPassengersPacket(this.entity.getVehicle()));
      }

   }

   private void sendDirtyEntityData() {
      SynchedEntityData var1 = this.entity.getEntityData();
      if(var1.isDirty()) {
         this.broadcastAndSend(new ClientboundSetEntityDataPacket(this.entity.getId(), var1, false));
      }

      if(this.entity instanceof LivingEntity) {
         ModifiableAttributeMap var2 = (ModifiableAttributeMap)((LivingEntity)this.entity).getAttributes();
         Set<AttributeInstance> var3 = var2.getDirtyAttributes();
         if(!var3.isEmpty()) {
            this.broadcastAndSend(new ClientboundUpdateAttributesPacket(this.entity.getId(), var3));
         }

         var3.clear();
      }

   }

   private void updateSentPos() {
      this.xp = ClientboundMoveEntityPacket.entityToPacket(this.entity.x);
      this.yp = ClientboundMoveEntityPacket.entityToPacket(this.entity.y);
      this.zp = ClientboundMoveEntityPacket.entityToPacket(this.entity.z);
   }

   public Vec3 sentPos() {
      return ClientboundMoveEntityPacket.packetToEntity(this.xp, this.yp, this.zp);
   }

   private void broadcastAndSend(Packet packet) {
      this.broadcast.accept(packet);
      if(this.entity instanceof ServerPlayer) {
         ((ServerPlayer)this.entity).connection.send(packet);
      }

   }
}
