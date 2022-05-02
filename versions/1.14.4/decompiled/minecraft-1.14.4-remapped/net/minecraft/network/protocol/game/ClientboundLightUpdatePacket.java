package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ClientboundLightUpdatePacket implements Packet {
   private int x;
   private int z;
   private int skyYMask;
   private int blockYMask;
   private int emptySkyYMask;
   private int emptyBlockYMask;
   private List skyUpdates;
   private List blockUpdates;

   public ClientboundLightUpdatePacket() {
   }

   public ClientboundLightUpdatePacket(ChunkPos chunkPos, LevelLightEngine levelLightEngine) {
      this.x = chunkPos.x;
      this.z = chunkPos.z;
      this.skyUpdates = Lists.newArrayList();
      this.blockUpdates = Lists.newArrayList();

      for(int var3 = 0; var3 < 18; ++var3) {
         DataLayer var4 = levelLightEngine.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of(chunkPos, -1 + var3));
         DataLayer var5 = levelLightEngine.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of(chunkPos, -1 + var3));
         if(var4 != null) {
            if(var4.isEmpty()) {
               this.emptySkyYMask |= 1 << var3;
            } else {
               this.skyYMask |= 1 << var3;
               this.skyUpdates.add(var4.getData().clone());
            }
         }

         if(var5 != null) {
            if(var5.isEmpty()) {
               this.emptyBlockYMask |= 1 << var3;
            } else {
               this.blockYMask |= 1 << var3;
               this.blockUpdates.add(var5.getData().clone());
            }
         }
      }

   }

   public ClientboundLightUpdatePacket(ChunkPos chunkPos, LevelLightEngine levelLightEngine, int skyYMask, int blockYMask) {
      this.x = chunkPos.x;
      this.z = chunkPos.z;
      this.skyYMask = skyYMask;
      this.blockYMask = blockYMask;
      this.skyUpdates = Lists.newArrayList();
      this.blockUpdates = Lists.newArrayList();

      for(int var5 = 0; var5 < 18; ++var5) {
         if((this.skyYMask & 1 << var5) != 0) {
            DataLayer var6 = levelLightEngine.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of(chunkPos, -1 + var5));
            if(var6 != null && !var6.isEmpty()) {
               this.skyUpdates.add(var6.getData().clone());
            } else {
               this.skyYMask &= ~(1 << var5);
               if(var6 != null) {
                  this.emptySkyYMask |= 1 << var5;
               }
            }
         }

         if((this.blockYMask & 1 << var5) != 0) {
            DataLayer var6 = levelLightEngine.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of(chunkPos, -1 + var5));
            if(var6 != null && !var6.isEmpty()) {
               this.blockUpdates.add(var6.getData().clone());
            } else {
               this.blockYMask &= ~(1 << var5);
               if(var6 != null) {
                  this.emptyBlockYMask |= 1 << var5;
               }
            }
         }
      }

   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.x = friendlyByteBuf.readVarInt();
      this.z = friendlyByteBuf.readVarInt();
      this.skyYMask = friendlyByteBuf.readVarInt();
      this.blockYMask = friendlyByteBuf.readVarInt();
      this.emptySkyYMask = friendlyByteBuf.readVarInt();
      this.emptyBlockYMask = friendlyByteBuf.readVarInt();
      this.skyUpdates = Lists.newArrayList();

      for(int var2 = 0; var2 < 18; ++var2) {
         if((this.skyYMask & 1 << var2) != 0) {
            this.skyUpdates.add(friendlyByteBuf.readByteArray(2048));
         }
      }

      this.blockUpdates = Lists.newArrayList();

      for(int var2 = 0; var2 < 18; ++var2) {
         if((this.blockYMask & 1 << var2) != 0) {
            this.blockUpdates.add(friendlyByteBuf.readByteArray(2048));
         }
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.x);
      friendlyByteBuf.writeVarInt(this.z);
      friendlyByteBuf.writeVarInt(this.skyYMask);
      friendlyByteBuf.writeVarInt(this.blockYMask);
      friendlyByteBuf.writeVarInt(this.emptySkyYMask);
      friendlyByteBuf.writeVarInt(this.emptyBlockYMask);

      for(byte[] vars3 : this.skyUpdates) {
         friendlyByteBuf.writeByteArray(vars3);
      }

      for(byte[] vars3 : this.blockUpdates) {
         friendlyByteBuf.writeByteArray(vars3);
      }

   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleLightUpdatePacked(this);
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }

   public int getSkyYMask() {
      return this.skyYMask;
   }

   public int getEmptySkyYMask() {
      return this.emptySkyYMask;
   }

   public List getSkyUpdates() {
      return this.skyUpdates;
   }

   public int getBlockYMask() {
      return this.blockYMask;
   }

   public int getEmptyBlockYMask() {
      return this.emptyBlockYMask;
   }

   public List getBlockUpdates() {
      return this.blockUpdates;
   }
}
