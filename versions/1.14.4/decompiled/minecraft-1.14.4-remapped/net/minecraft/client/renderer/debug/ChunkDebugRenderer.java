package net.minecraft.client.renderer.debug;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;

@ClientJarOnly
public class ChunkDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private double lastUpdateTime = Double.MIN_VALUE;
   private final int radius = 12;
   @Nullable
   private ChunkDebugRenderer.ChunkData data;

   public ChunkDebugRenderer(Minecraft minecraft) {
      this.minecraft = minecraft;
   }

   public void render(long l) {
      double var3 = (double)Util.getNanos();
      if(var3 - this.lastUpdateTime > 3.0E9D) {
         this.lastUpdateTime = var3;
         IntegratedServer var5 = this.minecraft.getSingleplayerServer();
         if(var5 != null) {
            this.data = new ChunkDebugRenderer.ChunkData(var5);
         } else {
            this.data = null;
         }
      }

      if(this.data != null) {
         GlStateManager.disableFog();
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.lineWidth(2.0F);
         GlStateManager.disableTexture();
         GlStateManager.depthMask(false);
         Map<ChunkPos, String> var5 = (Map)this.data.serverData.getNow((Object)null);
         double var6 = this.minecraft.gameRenderer.getMainCamera().getPosition().y * 0.85D;

         for(Entry<ChunkPos, String> var9 : this.data.clientData.entrySet()) {
            ChunkPos var10 = (ChunkPos)var9.getKey();
            String var11 = (String)var9.getValue();
            if(var5 != null) {
               var11 = var11 + (String)var5.get(var10);
            }

            String[] vars12 = var11.split("\n");
            int var13 = 0;

            for(String var17 : vars12) {
               DebugRenderer.renderFloatingText(var17, (double)((var10.x << 4) + 8), var6 + (double)var13, (double)((var10.z << 4) + 8), -1, 0.15F);
               var13 -= 2;
            }
         }

         GlStateManager.depthMask(true);
         GlStateManager.enableTexture();
         GlStateManager.disableBlend();
         GlStateManager.enableFog();
      }

   }

   @ClientJarOnly
   final class ChunkData {
      private final Map clientData;
      private final CompletableFuture serverData;

      private ChunkData(IntegratedServer integratedServer) {
         MultiPlayerLevel var3 = ChunkDebugRenderer.this.minecraft.level;
         DimensionType var4 = ChunkDebugRenderer.this.minecraft.level.dimension.getType();
         ServerLevel var5;
         if(integratedServer.getLevel(var4) != null) {
            var5 = integratedServer.getLevel(var4);
         } else {
            var5 = null;
         }

         Camera var6 = ChunkDebugRenderer.this.minecraft.gameRenderer.getMainCamera();
         int var7 = (int)var6.getPosition().x >> 4;
         int var8 = (int)var6.getPosition().z >> 4;
         Builder<ChunkPos, String> var9 = ImmutableMap.builder();
         ClientChunkCache var10 = var3.getChunkSource();

         for(int var11 = var7 - 12; var11 <= var7 + 12; ++var11) {
            for(int var12 = var8 - 12; var12 <= var8 + 12; ++var12) {
               ChunkPos var13 = new ChunkPos(var11, var12);
               String var14 = "";
               LevelChunk var15 = var10.getChunk(var11, var12, false);
               var14 = var14 + "Client: ";
               if(var15 == null) {
                  var14 = var14 + "0n/a\n";
               } else {
                  var14 = var14 + (var15.isEmpty()?" E":"");
                  var14 = var14 + "\n";
               }

               var9.put(var13, var14);
            }
         }

         this.clientData = var9.build();
         this.serverData = integratedServer.submit(() -> {
            Builder<ChunkPos, String> var4 = ImmutableMap.builder();
            ServerChunkCache var5 = var5.getChunkSource();

            for(int var6 = var7 - 12; var6 <= var7 + 12; ++var6) {
               for(int var7 = var8 - 12; var7 <= var8 + 12; ++var7) {
                  ChunkPos var8 = new ChunkPos(var6, var7);
                  var4.put(var8, "Server: " + var5.getChunkDebugData(var8));
               }
            }

            return var4.build();
         });
      }
   }
}
