package net.minecraft.world.level;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class PortalForcer {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final NetherPortalBlock PORTAL_BLOCK = (NetherPortalBlock)Blocks.NETHER_PORTAL;
   private final ServerLevel level;
   private final Random random;
   private final Map cachedPortals = Maps.newHashMapWithExpectedSize(4096);
   private final Object2LongMap negativeChecks = new Object2LongOpenHashMap();

   public PortalForcer(ServerLevel level) {
      this.level = level;
      this.random = new Random(level.getSeed());
   }

   public boolean findAndMoveToPortal(Entity entity, float var2) {
      Vec3 var3 = entity.getPortalEntranceOffset();
      Direction var4 = entity.getPortalEntranceForwards();
      BlockPattern.PortalInfo var5 = this.findPortal(new BlockPos(entity), entity.getDeltaMovement(), var4, var3.x, var3.y, entity instanceof Player);
      if(var5 == null) {
         return false;
      } else {
         Vec3 var6 = var5.pos;
         Vec3 var7 = var5.speed;
         entity.setDeltaMovement(var7);
         entity.yRot = var2 + (float)var5.angle;
         if(entity instanceof ServerPlayer) {
            ((ServerPlayer)entity).connection.teleport(var6.x, var6.y, var6.z, entity.yRot, entity.xRot);
            ((ServerPlayer)entity).connection.resetPosition();
         } else {
            entity.moveTo(var6.x, var6.y, var6.z, entity.yRot, entity.xRot);
         }

         return true;
      }
   }

   @Nullable
   public BlockPattern.PortalInfo findPortal(BlockPos blockPos, Vec3 vec3, Direction direction, double var4, double var6, boolean var8) {
      int var9 = 128;
      boolean var10 = true;
      BlockPos var11 = null;
      ColumnPos var12 = new ColumnPos(blockPos);
      if(!var8 && this.negativeChecks.containsKey(var12)) {
         return null;
      } else {
         PortalForcer.PortalPosition var13 = (PortalForcer.PortalPosition)this.cachedPortals.get(var12);
         if(var13 != null) {
            var11 = var13.pos;
            var13.lastUsed = this.level.getGameTime();
            var10 = false;
         } else {
            double var14 = Double.MAX_VALUE;

            for(int var16 = -128; var16 <= 128; ++var16) {
               BlockPos var19;
               for(int var17 = -128; var17 <= 128; ++var17) {
                  for(BlockPos var18 = blockPos.offset(var16, this.level.getHeight() - 1 - blockPos.getY(), var17); var18.getY() >= 0; var18 = var19) {
                     var19 = var18.below();
                     if(this.level.getBlockState(var18).getBlock() == PORTAL_BLOCK) {
                        for(var19 = var18.below(); this.level.getBlockState(var19).getBlock() == PORTAL_BLOCK; var19 = var19.below()) {
                           var18 = var19;
                        }

                        double var20 = var18.distSqr(blockPos);
                        if(var14 < 0.0D || var20 < var14) {
                           var14 = var20;
                           var11 = var18;
                        }
                     }
                  }
               }
            }
         }

         if(var11 == null) {
            long var14 = this.level.getGameTime() + 300L;
            this.negativeChecks.put(var12, var14);
            return null;
         } else {
            if(var10) {
               this.cachedPortals.put(var12, new PortalForcer.PortalPosition(var11, this.level.getGameTime()));
               Logger var10000 = LOGGER;
               Supplier[] var10002 = new Supplier[2];
               Dimension var10005 = this.level.getDimension();
               var10002[0] = var10005::getType;
               var10002[1] = () -> {
                  return var12;
               };
               var10000.debug("Adding nether portal ticket for {}:{}", var10002);
               this.level.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(var11), 3, var12);
            }

            BlockPattern.BlockPatternMatch var14 = PORTAL_BLOCK.getPortalShape(this.level, var11);
            return var14.getPortalOutput(direction, var11, var6, vec3, var4);
         }
      }
   }

   public boolean createPortal(Entity entity) {
      int var2 = 16;
      double var3 = -1.0D;
      int var5 = Mth.floor(entity.x);
      int var6 = Mth.floor(entity.y);
      int var7 = Mth.floor(entity.z);
      int var8 = var5;
      int var9 = var6;
      int var10 = var7;
      int var11 = 0;
      int var12 = this.random.nextInt(4);
      BlockPos.MutableBlockPos var13 = new BlockPos.MutableBlockPos();

      for(int var14 = var5 - 16; var14 <= var5 + 16; ++var14) {
         double var15 = (double)var14 + 0.5D - entity.x;

         for(int var17 = var7 - 16; var17 <= var7 + 16; ++var17) {
            double var18 = (double)var17 + 0.5D - entity.z;

            label146:
            for(int var20 = this.level.getHeight() - 1; var20 >= 0; --var20) {
               if(this.level.isEmptyBlock(var13.set(var14, var20, var17))) {
                  while(var20 > 0 && this.level.isEmptyBlock(var13.set(var14, var20 - 1, var17))) {
                     --var20;
                  }

                  for(int var21 = var12; var21 < var12 + 4; ++var21) {
                     int var22 = var21 % 2;
                     int var23 = 1 - var22;
                     if(var21 % 4 >= 2) {
                        var22 = -var22;
                        var23 = -var23;
                     }

                     for(int var24 = 0; var24 < 3; ++var24) {
                        for(int var25 = 0; var25 < 4; ++var25) {
                           for(int var26 = -1; var26 < 4; ++var26) {
                              int var27 = var14 + (var25 - 1) * var22 + var24 * var23;
                              int var28 = var20 + var26;
                              int var29 = var17 + (var25 - 1) * var23 - var24 * var22;
                              var13.set(var27, var28, var29);
                              if(var26 < 0 && !this.level.getBlockState(var13).getMaterial().isSolid() || var26 >= 0 && !this.level.isEmptyBlock(var13)) {
                                 continue label146;
                              }
                           }
                        }
                     }

                     double var24 = (double)var20 + 0.5D - entity.y;
                     double var26 = var15 * var15 + var24 * var24 + var18 * var18;
                     if(var3 < 0.0D || var26 < var3) {
                        var3 = var26;
                        var8 = var14;
                        var9 = var20;
                        var10 = var17;
                        var11 = var21 % 4;
                     }
                  }
               }
            }
         }
      }

      if(var3 < 0.0D) {
         for(int var14 = var5 - 16; var14 <= var5 + 16; ++var14) {
            double var15 = (double)var14 + 0.5D - entity.x;

            for(int var17 = var7 - 16; var17 <= var7 + 16; ++var17) {
               double var18 = (double)var17 + 0.5D - entity.z;

               label565:
               for(int var20 = this.level.getHeight() - 1; var20 >= 0; --var20) {
                  if(this.level.isEmptyBlock(var13.set(var14, var20, var17))) {
                     while(var20 > 0 && this.level.isEmptyBlock(var13.set(var14, var20 - 1, var17))) {
                        --var20;
                     }

                     for(int var21 = var12; var21 < var12 + 2; ++var21) {
                        int var22 = var21 % 2;
                        int var23 = 1 - var22;

                        for(int var24 = 0; var24 < 4; ++var24) {
                           for(int var25 = -1; var25 < 4; ++var25) {
                              int var26 = var14 + (var24 - 1) * var22;
                              int var27 = var20 + var25;
                              int var28 = var17 + (var24 - 1) * var23;
                              var13.set(var26, var27, var28);
                              if(var25 < 0 && !this.level.getBlockState(var13).getMaterial().isSolid() || var25 >= 0 && !this.level.isEmptyBlock(var13)) {
                                 continue label565;
                              }
                           }
                        }

                        double var24 = (double)var20 + 0.5D - entity.y;
                        double var26 = var15 * var15 + var24 * var24 + var18 * var18;
                        if(var3 < 0.0D || var26 < var3) {
                           var3 = var26;
                           var8 = var14;
                           var9 = var20;
                           var10 = var17;
                           var11 = var21 % 2;
                        }
                     }
                  }
               }
            }
         }
      }

      int var15 = var8;
      int var16 = var9;
      int var17 = var10;
      int var18 = var11 % 2;
      int var19 = 1 - var18;
      if(var11 % 4 >= 2) {
         var18 = -var18;
         var19 = -var19;
      }

      if(var3 < 0.0D) {
         var9 = Mth.clamp(var9, 70, this.level.getHeight() - 10);
         var16 = var9;

         for(int var20 = -1; var20 <= 1; ++var20) {
            for(int var21 = 1; var21 < 3; ++var21) {
               for(int var22 = -1; var22 < 3; ++var22) {
                  int var23 = var15 + (var21 - 1) * var18 + var20 * var19;
                  int var24 = var16 + var22;
                  int var25 = var17 + (var21 - 1) * var19 - var20 * var18;
                  boolean var26 = var22 < 0;
                  var13.set(var23, var24, var25);
                  this.level.setBlockAndUpdate(var13, var26?Blocks.OBSIDIAN.defaultBlockState():Blocks.AIR.defaultBlockState());
               }
            }
         }
      }

      for(int var20 = -1; var20 < 3; ++var20) {
         for(int var21 = -1; var21 < 4; ++var21) {
            if(var20 == -1 || var20 == 2 || var21 == -1 || var21 == 3) {
               var13.set(var15 + var20 * var18, var16 + var21, var17 + var20 * var19);
               this.level.setBlock(var13, Blocks.OBSIDIAN.defaultBlockState(), 3);
            }
         }
      }

      BlockState var20 = (BlockState)PORTAL_BLOCK.defaultBlockState().setValue(NetherPortalBlock.AXIS, var18 == 0?Direction.Axis.Z:Direction.Axis.X);

      for(int var21 = 0; var21 < 2; ++var21) {
         for(int var22 = 0; var22 < 3; ++var22) {
            var13.set(var15 + var21 * var18, var16 + var22, var17 + var21 * var19);
            this.level.setBlock(var13, var20, 18);
         }
      }

      return true;
   }

   public void tick(long l) {
      if(l % 100L == 0L) {
         this.purgeNegativeChecks(l);
         this.clearStaleCacheEntries(l);
      }

   }

   private void purgeNegativeChecks(long l) {
      LongIterator var3 = this.negativeChecks.values().iterator();

      while(var3.hasNext()) {
         long var4 = var3.nextLong();
         if(var4 <= l) {
            var3.remove();
         }
      }

   }

   private void clearStaleCacheEntries(long l) {
      long var3 = l - 300L;
      Iterator<Entry<ColumnPos, PortalForcer.PortalPosition>> var5 = this.cachedPortals.entrySet().iterator();

      while(var5.hasNext()) {
         Entry<ColumnPos, PortalForcer.PortalPosition> var6 = (Entry)var5.next();
         PortalForcer.PortalPosition var7 = (PortalForcer.PortalPosition)var6.getValue();
         if(var7.lastUsed < var3) {
            ColumnPos var8 = (ColumnPos)var6.getKey();
            Logger var10000 = LOGGER;
            Supplier[] var10002 = new Supplier[2];
            Dimension var10005 = this.level.getDimension();
            var10002[0] = var10005::getType;
            var10002[1] = () -> {
               return var8;
            };
            var10000.debug("Removing nether portal ticket for {}:{}", var10002);
            this.level.getChunkSource().removeRegionTicket(TicketType.PORTAL, new ChunkPos(var7.pos), 3, var8);
            var5.remove();
         }
      }

   }

   static class PortalPosition {
      public final BlockPos pos;
      public long lastUsed;

      public PortalPosition(BlockPos pos, long lastUsed) {
         this.pos = pos;
         this.lastUsed = lastUsed;
      }
   }
}
